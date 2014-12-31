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
package com.github.besherman.lifx;

import java.util.Iterator;

/**
 * A collection of lights.
 */
public interface LFXLightCollection extends Iterable<LFXLight> {
    
    /**
     * Returns the number of lights in the collection.
     */
    int size();

    /**
     * Returns true if the collection is empty.
     */
    boolean isEmpty();

    /**
     * Returns true if the collection contains the light.
     */
    boolean contains(LFXLight light);

    /**
     * Returns an interator over the lights in the collection.
     */
    @Override
    Iterator<LFXLight> iterator();
    
    /**
     * Returns the first light found with label or null if not found.
     */
    LFXLight getLightByLabel(String label);

    /**
     * Returns the light with id or null if not found.
     */
    LFXLight getLightByID(String id);

    /**
     * Adds a listener that gets notified when lights are added or removed
     * from the collection.
     */
    void addLightCollectionListener(LFXLightCollectionListener l);
    
    void removeLightCollectionListener(LFXLightCollectionListener l);    
}
