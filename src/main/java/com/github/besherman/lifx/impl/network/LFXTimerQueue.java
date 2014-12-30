/*
 * The MIT License
 *
 * Copyright 2014 Richard Löfberg.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.besherman.lifx.impl.network;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Scheduler for tasks to be performed at a later time. 
 */
public class LFXTimerQueue {
    private static final AtomicInteger instanceCounter = new AtomicInteger(0);
    private final Timer timer = new Timer("LIFX Timer Queue " + instanceCounter.incrementAndGet(), false);
    
    public LFXTimerQueue() {        
        
    }
    
    public void close() {
        timer.cancel();
    }

    /**
     * Execute the runnable once at a later time.
     * 
     * @param runnable the task to execute
     * @param time how much later
     * @param unit the unit of time
     * @return a unique key used to cancel the action
     */
    public Object doLater(Runnable runnable, long time, TimeUnit unit) {        
        TimerTaskImpl task = new TimerTaskImpl(runnable);
        timer.schedule(task, unit.toMillis(time));
        return task;
    }
    
    
    /**
     * Execute the runnable periodically when duration time has passed.
     * 
     * @param runnable the task to execute
     * @param duration how much later
     * @param unit the unit of time
     * @return a unique key used to cancel the action
     */
    public Object doRepeatedly(Runnable runnable, long duration, TimeUnit unit) {
        long ms = unit.toMillis(duration);
        TimerTaskImpl task = new TimerTaskImpl(runnable);
        timer.scheduleAtFixedRate(task, ms, ms);
        return task;
    }    
    
    /**
     * Cancels a running task.
     * @param key the key given by doLater() or doRepeatedly()
     */
    public void cancel(Object key) {
        if(key == null) {
            throw new IllegalArgumentException("key can not be null");
        }
        ((TimerTaskImpl)key).cancel();
    }
    
    
    private static class TimerTaskImpl extends TimerTask {
        private final Runnable runnable;

        public TimerTaskImpl(Runnable runnable) {
            this.runnable = runnable;
        }        
        
        @Override
        public void run() {
            runnable.run();
        }
    }
}
