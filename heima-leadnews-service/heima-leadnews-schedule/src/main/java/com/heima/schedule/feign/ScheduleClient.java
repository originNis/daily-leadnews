package com.heima.schedule.feign;

import com.heima.apis.IScheduleClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.schedule.dtos.Task;
import com.heima.schedule.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/5/24
 */
@RestController
public class ScheduleClient implements IScheduleClient {
    @Autowired
    TaskService taskService;

    @PostMapping("/api/v1/task/add")
    public ResponseResult addTask(@RequestBody Task task) {
        return ResponseResult.okResult(taskService.addTask(task));
    }

    @GetMapping("/api/v1/task/{taskId}")
    public ResponseResult cancelTask(@PathVariable("taskId") Long taskId) {
        return ResponseResult.okResult(taskService.cancelTask(taskId));
    }

    @GetMapping("/api/v1/task/{type}/{priority}")
    public ResponseResult getTask(@PathVariable("type") Integer type, @PathVariable("priority") Integer priority) {
        return ResponseResult.okResult(taskService.getTaskToExecute(type, priority));
    }
}
