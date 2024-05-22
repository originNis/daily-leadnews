package com.heima.schedule.test;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/5/22
 */
public class MyThread extends Thread{
    private final RedissonClient redissonClient;

    private final int id;

    public MyThread(int id, RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
       this.id = id;
    }

    @Override
    public void run() {
        RLock lock = redissonClient.getLock("lock");

        if (lock.tryLock()) {
            try {
                System.out.println("线程" + id + "获得锁");
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                System.out.println("线程" + id + "释放锁");
            }
        } else {
            System.out.println("线程" + id + "未获得锁并退出");
        }
    }
}
