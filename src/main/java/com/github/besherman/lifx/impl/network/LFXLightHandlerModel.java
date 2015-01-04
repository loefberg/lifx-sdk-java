/*
 * The MIT License
 *
 * Copyright 2015 Richard.
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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class simply keeps track of light handlers.
 * 
 * This looks like it is over the top and top complicated, but: 
 * <ul>
 *    <li>We need to pass a list of handlers to the connection.</li>
 *    <li>A handler can be added before, during and after a connection is created.
 * </ul>
 */
public class LFXLightHandlerModel {
    private final List<LFXLightHandler> handlers = new CopyOnWriteArrayList<>();
    private final List<LFXLightHandlerModelListener> listeners = new CopyOnWriteArrayList<>();
    private final Object lock = new Object();
    
    public void addLightHandler(LFXLightHandler handler) {
        synchronized(lock) {
            if(!handlers.contains(handler)) {
                handlers.add(handler);       
                for(LFXLightHandlerModelListener l: listeners) {
                    l.handlerAdded(handler);
                }
            }
        }
    }
    
    public void removeLightHandler(LFXLightHandler handler) {
        synchronized(lock) {
            if(handlers.contains(handler)) {
                handlers.remove(handler);       
                for(LFXLightHandlerModelListener l: listeners) {
                    l.handlerRemoved(handler);
                }
            }
        }
    }    

    public void forEach(LFXLightHandlerModelConsumer action) {
        // This has to be synchronized because this is going to call close()
        // on the handler, and that is not allowed to happen AFTER the
        // handler has been removed.
        synchronized(lock) {
            for(LFXLightHandler handler: handlers) {
                action.accept(handler);
            }
        }
    }
    
    public void addLightHandlerModelListener(LFXLightHandlerModelListener l) {
        listeners.add(l);
    }
    
    public void removeLightHandlerModelListener(LFXLightHandlerModelListener l) {
        listeners.remove(l);
    }    
}
