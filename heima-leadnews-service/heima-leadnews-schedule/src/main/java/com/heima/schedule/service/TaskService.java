package com.heima.schedule.service;

import com.heima.model.schedule.dtos.Task;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/5/19
 */
public interface TaskService {

    /**
     * 添加延迟任务
     * @param task
     * @return
     */
    public Long addTask(Task task);

    /**
     * 取消延迟任务
     * @param taskId
     * @return
     */
    public boolean cancelTask(Long taskId);

    /**
     * 按照类型和优先级从list中拉取任务
     * @param type
     * @param priority
     * @return
     */
    public Task getTaskToExecute(int type, int priority);
}
