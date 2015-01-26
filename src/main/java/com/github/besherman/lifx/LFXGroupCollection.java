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
 * A collection of groups.
 */
public interface LFXGroupCollection extends Iterable<LFXGroup> {
    /**
     * Returns a group with the given label or null if no such group exists.
     */
    LFXGroup get(String label);

    /**
     * Creates a new group with given label.
     * @param label the label can not be empty of longer than 32 characters.
     * @return the new group or null if the group could not be created
     */
    LFXGroup add(String label);
    
    /**
     * Removes the given group.
     */
    void remove(LFXGroup group);

    /**
     * Returns the number of groups.
     */
    int size();

    /**
     * Returns true if the group is empty. 
     */
    boolean isEmpty();

    /**
     * Returns an iterator over the groups in the collection.
     */
    @Override
    Iterator<LFXGroup> iterator();

    /**
     * Checks to see if a given group is included in the collection.
     * @param group the group to search for
     * @return true if the provided group is included in the colection
     */
    boolean contains(LFXGroup group);

    /**
     * Checks to see if a given light is included in any of the groups in the collection.
     * @param light the light to search for
     * @return true if the provided light is included in any of the groups in the collection.
     */
    boolean contains(LFXLight light);

    /**
     * Adds a group listener that will be notified when groups are added or 
     * removed.
     */
    void addGroupCollectionListener(LFXGroupCollectionListener l);

    void removeGroupCollectionListener(LFXGroupCollectionListener l);        
}
