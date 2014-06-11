/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package android.os;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Richard
 */
public class Looper {
    private static final Looper looper = new Looper();
    
    private final ExecutorService executor;
    
    private Looper() {
        executor = Executors.newSingleThreadExecutor();
    }
    
    public static Looper getMainLooper() {
        return looper;
    }

    
    public void quit() {
        try {
            // we have to sleep for a while to let the disconnect
            // packages get out there
            Thread.sleep(500);
        } catch(InterruptedException ex) { 
            // oh well never mind                     
        }
        executor.shutdown();
    }
    
    protected void submit(Runnable runnable) {
        executor.submit(runnable);
    }
}
