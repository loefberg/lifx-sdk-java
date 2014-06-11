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
    
    public ExecutorService getExecutor() {
        return executor;
    }
    
    public void quit() {
        executor.shutdown();
    }
}
