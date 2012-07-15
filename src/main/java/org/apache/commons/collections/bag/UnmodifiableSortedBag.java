/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections.bag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.SortedBag;
import org.apache.commons.collections.Unmodifiable;
import org.apache.commons.collections.iterators.UnmodifiableIterator;
import org.apache.commons.collections.set.UnmodifiableSet;

/**
 * Decorates another {@link SortedBag} to ensure it can't be altered.
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 * <p>
 * Attempts to modify it will result in an UnsupportedOperationException. 
 *
 * @since 3.0
 * @version $Id$
 */
public final class UnmodifiableSortedBag<E>
        extends AbstractSortedBagDecorator<E> implements Unmodifiable, Serializable {

    /** Serialization version */
    private static final long serialVersionUID = -3190437252665717841L;

    /**
     * Factory method to create an unmodifiable bag.
     * <p>
     * If the bag passed in is already unmodifiable, it is returned.
     * 
     * @param <E> the type of the elements in the bag
     * @param bag  the bag to decorate, must not be null
     * @return an unmodifiable SortedBag
     * @throws IllegalArgumentException if bag is null
     */
    public static <E> SortedBag<E> unmodifiableSortedBag(SortedBag<E> bag) {
        if (bag instanceof Unmodifiable) {
            return bag;
        }
        return new UnmodifiableSortedBag<E>(bag);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * 
     * @param bag  the bag to decorate, must not be null
     * @throws IllegalArgumentException if bag is null
     */
    private UnmodifiableSortedBag(SortedBag<E> bag) {
        super(bag);
    }

    //-----------------------------------------------------------------------
    /**
     * Write the collection out using a custom routine.
     * 
     * @param out  the output stream
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(collection);
    }

    /**
     * Read the collection in using a custom routine.
     * 
     * @param in  the input stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        collection = (Collection<E>) in.readObject();
    }

    //-----------------------------------------------------------------------
    @Override
    public Iterator<E> iterator() {
        return UnmodifiableIterator.unmodifiableIterator(decorated().iterator());
    }

    @Override
    public boolean add(E object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean add(E object, int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object object, int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<E> uniqueSet() {
        Set<E> set = decorated().uniqueSet();
        return UnmodifiableSet.unmodifiableSet(set);
    }

}
