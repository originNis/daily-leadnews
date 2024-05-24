package com.heima.wemedia.service;

import java.util.Date;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/5/24
 */
public interface WmNewsTaskService {

    /**
     * 添加文章到延时任务队列中
     * @param id
     * @param publishTime
     */
    public void addNewsAsTask(Integer id, Date publishTime);

    /**
     * 根据任务队列，审核文章并发布
     */
    public void scanNewsByTask();
}
