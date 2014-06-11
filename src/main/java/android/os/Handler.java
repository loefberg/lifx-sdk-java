/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package android.os;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Richard
 */
public class Handler {
    private final Looper looper;
    
    public Handler(Looper looper) {
        this.looper = looper;
    }
    
    public void handleMessage(Message message) {
        // default implementation does nothing
    }
    
    public Message obtainMessage() {
        return new Message();
    }

    public void sendMessage(final Message msg) {
        looper.submit(new Runnable() {
            @Override
            public void run() {
                handleMessage(msg);
            }
        });
    }

    public void post(Runnable task) {        
        looper.submit(task);
    }

    public void postDelayed(final Runnable task, long delay) {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {                
                looper.submit(task); 
            }
        }, delay);
    }
}
