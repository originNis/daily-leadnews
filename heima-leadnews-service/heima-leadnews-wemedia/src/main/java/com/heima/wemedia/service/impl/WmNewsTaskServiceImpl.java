package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.IScheduleClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.TaskTypeEnum;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.utils.common.ProtostuffUtil;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.heima.wemedia.service.WmNewsTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/5/24
 */
@Service
@Slf4j
public class WmNewsTaskServiceImpl implements WmNewsTaskService {
    @Autowired
    private IScheduleClient scheduleClient;
    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;
    /**
     * 添加文章到延时任务队列中
     *
     * @param id
     * @param publishTime
     */
    @Override
    @Async
    public void addNewsAsTask(Integer id, Date publishTime) {
        log.info("正在添加文章到任务队列");

        Task task = new Task();
        task.setExecuteTime(publishTime.getTime());
        task.setTaskType(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType());
        task.setPriority(TaskTypeEnum.NEWS_SCAN_TIME.getPriority());

        WmNews wmNews = new WmNews();
        wmNews.setId(id);
        task.setParameters(ProtostuffUtil.serialize(wmNews));

        scheduleClient.addTask(task);

        log.info("文章添加到任务队列成功");
    }

    /**
     * 根据任务队列，审核文章并发布
     */
    @Scheduled(fixedRate = 1000) // 固定频率，1秒执行一次
    @Override
    public void scanNewsByTask() {
        ResponseResult response = scheduleClient.getTask(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType(),
                TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        if (response.getCode().equals(200) && response.getData() != null) {
            Task task = JSON.parseObject(JSON.toJSONString(response.getData()), Task.class);
            WmNews news = ProtostuffUtil.deserialize(task.getParameters(), WmNews.class);
            wmNewsAutoScanService.autoScanWmNews(news.getId());
            log.info("成功消费一个文章");
        }
    }
}
