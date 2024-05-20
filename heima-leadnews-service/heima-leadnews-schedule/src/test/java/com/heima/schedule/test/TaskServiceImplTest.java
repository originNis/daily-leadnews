package com.heima.schedule.test;

import com.heima.model.schedule.dtos.Task;
import com.heima.schedule.ScheduleApplication;
import com.heima.schedule.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/5/19
 */
@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class TaskServiceImplTest {

    @Autowired
    TaskService taskService;

    @Test
    public void addTask() {
        Task task = new Task();
        task.setTaskType(80);
        task.setPriority(50);
        task.setParameters("testing".getBytes());
        task.setExecuteTime(System.currentTimeMillis());

        taskService.addTask(task);
    }

    @Test
    public void cancelTask() {
        taskService.cancelTask(1792530670938435586L);
    }

    @Test
    public void pollTask() {
        Task task = taskService.getTaskToExecute(80, 50);
        System.out.println(task);
    }
}