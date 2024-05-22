package com.heima.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.ScheduleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.schedule.pojos.TaskinfoLogs;
import com.heima.schedule.mapper.TaskinfoLogsMapper;
import com.heima.schedule.mapper.TaskinfoMapper;
import com.heima.schedule.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/5/19
 */
@Service
@Transactional
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Autowired
    RedissonClient redissonClient;
    @Autowired
    CacheService cacheService;
    @Autowired
    TaskinfoMapper taskinfoMapper;
    @Autowired
    TaskinfoLogsMapper taskinfoLogsMapper;

    /**
     * 添加延迟任务
     *
     * @param task
     * @return
     */
    @Override
    public Long addTask(Task task) {
        // 1. 将任务持久化到数据库
        addTasksToDb(task);

        // 2. 任务添加到redis
        if (task.getTaskId() != null) {
            addTasksToRedis(task);
        }

        return task.getTaskId();
    }

    private void addTasksToRedis(Task task) {
        String key = task.getTaskType() + "_" + task.getPriority();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        long nextScheduleTime = calendar.getTimeInMillis();

        if (task.getExecuteTime() <= System.currentTimeMillis()) { // 立刻执行的任务放入list
            cacheService.lLeftPush(ScheduleConstants.TOPIC + key, JSON.toJSONString(task));
        } else if (task.getExecuteTime() <= nextScheduleTime) { // 五分钟内会执行的任务放入zset
            cacheService.zAdd(ScheduleConstants.FUTURE + key, JSON.toJSONString(task), task.getExecuteTime());
        }
    }

    private void addTasksToDb(Task task) {
        try {
            Taskinfo taskinfo = new Taskinfo();
            BeanUtils.copyProperties(task, taskinfo);
            taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
            taskinfoMapper.insert(taskinfo);

            TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
            BeanUtils.copyProperties(taskinfo, taskinfoLogs);
            taskinfoLogs.setVersion(1);
            taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
            taskinfoLogsMapper.insert(taskinfoLogs);

            // 若数据库操作成功，则回填taskId
            task.setTaskId(taskinfo.getTaskId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消延迟任务
     *
     * @param taskId
     * @return
     */
    @Override
    public boolean cancelTask(Long taskId) {
        boolean flag = false;

        Task task = upadateDb(taskId, ScheduleConstants.CANCELLED);

        if (task != null) {
            removeTaskFromCache(task);
            flag = true;
        }

        return true;
    }

    private void removeTaskFromCache(Task task) {
        String key = task.getTaskType() + "_" + task.getPriority();

        // 无论是哪个key，确保能够删除指定的taskId
        cacheService.lRemove(ScheduleConstants.TOPIC + key, 0, JSON.toJSONString(task));
        cacheService.zRemove(ScheduleConstants.FUTURE + key, JSON.toJSONString(task));
    }

    private Task upadateDb(Long taskId, Integer status) {
        // 删除任务记录
        taskinfoMapper.deleteById(taskId);

        // 更新任务日志
        TaskinfoLogs taskinfoLogs = taskinfoLogsMapper.selectById(taskId);
        taskinfoLogs.setTaskId(taskId);
        taskinfoLogs.setStatus(status);
        taskinfoLogsMapper.updateById(taskinfoLogs);

        Task task = new Task();
        BeanUtils.copyProperties(taskinfoLogs, task);
        task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());

        return task;
    }

    /**
     * 按照类型和优先级从list中拉取任务
     *
     * @param type
     * @param priority
     * @return
     */
    @Override
    public Task getTaskToExecute(int type, int priority) {
        Task task = null;
        String key = type + "_" + priority;

        String taskInJSON = cacheService.lRightPop(ScheduleConstants.TOPIC + key);
        if (StringUtils.isNotBlank(taskInJSON)) {
            task = JSON.parseObject(taskInJSON, Task.class);

            upadateDb(task.getTaskId(), ScheduleConstants.EXECUTED);
        }

        return task;
    }

    /**
     * 定时刷新任务
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void refresh() {
        RLock lock = redissonClient.getLock("FUTURE_TASK_SYNC");
        try {
            if (lock.tryLock(5, TimeUnit.SECONDS)) {
                log.info("=====未来数据定时刷新任务=====");

                Set<String> keys = cacheService.scan(ScheduleConstants.FUTURE + "*");
                for (String key : keys) {
                    String newKey = ScheduleConstants.TOPIC + key.split(ScheduleConstants.FUTURE)[1];

                    Set<String> currentTasks = cacheService.zRange(key, 0, System.currentTimeMillis());

                    if (!currentTasks.isEmpty()) {
                        cacheService.refreshWithPipeline(key, newKey, currentTasks);
                        log.info("成功刷新" + newKey);
                    }
                }
            } else {
                log.info("存在其他刷新任务正在访问锁");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("未来数据定时刷新任务获取锁失败");
        } finally {
            lock.unlock();
            log.info("========刷新任务结束==========");
        }
    }
}
