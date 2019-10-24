package com.common.util.uuid;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultIdGenerator implements IdGenerator, Runnable{
      
    private String time;  
      
    private AtomicInteger value;
      
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
      
    private IdGeneratorConfig config;  
      
    private Thread thread;  
      
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
      
    public DefaultIdGenerator(){  
        config = new DefaultIdGeneratorConfig();  
        time = LocalDateTime.now().format(FORMATTER);
        value = new AtomicInteger(config.getInitial());  
          
        thread = new Thread(this);  
        thread.setDaemon(true);  
        thread.start();  
    }  
      
    public DefaultIdGenerator(IdGeneratorConfig config){  
        this.config = config;  
        time = LocalDateTime.now().format(FORMATTER);  
        value = new AtomicInteger(config.getInitial());  
          
        thread = new Thread(this);  
        thread.setDaemon(true);  
        thread.start();  
    }  
      
    @Override  
    public String next() {  
        lock.readLock().lock();
        System.out.println(">>>>>>>>>> " + value.get());
        StringBuffer sb = new StringBuffer(config.getPrefix())
                .append(config.getSplitString())
                .append(time)
                .append(config.getSplitString())
                .append(value.getAndIncrement());
        lock.readLock().unlock();  
        return sb.toString();  
    }  
      
    @Override  
    public void run() {  
        while (true){  
            try {  
                Thread.sleep(1000 * config.getRollingInterval());  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
            String now = LocalDateTime.now().format(FORMATTER);  
            if (!now.equals(time)){  
                lock.writeLock().lock();  
                time = now;  
                value.set(config.getInitial());  
                lock.writeLock().unlock();  
            }  
        }  
    }  
      
}  