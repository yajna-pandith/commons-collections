/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/decorators/Attic/ObservedCollection.java,v 1.6 2003/09/03 22:29:51 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.collections.decorators;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections.event.ModificationHandler;
import org.apache.commons.collections.event.ModificationHandlerFactory;
import org.apache.commons.collections.event.StandardModificationHandler;

/**
 * Decorates a <code>Collection</code> implementation to observe modifications.
 * <p>
 * Each modifying method call made on this <code>Collection</code> is forwarded to a
 * {@link ModificationHandler}.
 * The handler manages the event, notifying listeners and optionally vetoing changes.
 * The default handler is {@link StandardModificationHandler}.
 * See this class for details of configuration available.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.6 $ $Date: 2003/09/03 22:29:51 $
 * 
 * @author Stephen Colebourne
 */
public class ObservedCollection extends AbstractCollectionDecorator {
    
    /** The handler to delegate event handling to */
    protected final ModificationHandler handler;

    // Factories
    //-----------------------------------------------------------------------
    /**
     * Factory method to create an observable collection.
     * <p>
     * A {@link StandardModificationHandler} will be created.
     * This can be accessed by {@link #getHandler()} to add listeners.
     *
     * @param coll  the collection to decorate, must not be null
     * @return the observed collection
     * @throws IllegalArgumentException if the collection is null
     */
    public static ObservedCollection decorate(final Collection coll) {
        return new ObservedCollection(coll, null);
    }

    /**
     * Factory method to create an observable collection using a listener or a handler.
     * <p>
     * A lot of functionality is available through this method.
     * If you don't need the extra functionality, simply implement the
     * {@link org.apache.commons.collections.event.StandardModificationListener}
     * interface and pass it in as the second parameter.
     * <p>
     * Internally, an <code>ObservedCollection</code> relies on a {@link ModificationHandler}.
     * The handler receives all the events and processes them, typically by
     * calling listeners. Different handler implementations can be plugged in
     * to provide a flexible event system.
     * <p>
     * The handler implementation is determined by the listener parameter.
     * If the parameter is a <code>ModificationHandler</code> it is used directly.
     * Otherwise, the factory mechanism of {@link ModificationHandlerFactory} is used
     * to create the handler for the listener parameter.
     * <p>
     * The listener is defined as an Object for maximum flexibility.
     * It does not have to be a listener in the classic JavaBean sense.
     * It is entirely up to the factory and handler as to how the parameter
     * is interpretted. An IllegalArgumentException is thrown if no suitable
     * handler can be found for this listener.
     * <p>
     * A <code>null</code> listener will throw an IllegalArgumentException
     * unless a special handler factory has been registered.
     * <p>
     *
     * @param coll  the collection to decorate, must not be null
     * @param listener  collection listener, may be null
     * @return the observed collection
     * @throws IllegalArgumentException if the collection is null
     * @throws IllegalArgumentException if there is no valid handler for the listener
     */
    public static ObservedCollection decorate(
            final Collection coll,
            final Object listener) {
        
        if (coll == null) {
            throw new IllegalArgumentException("Collection must not be null");
        }
        if (listener instanceof ModificationHandler) {
            return new ObservedCollection(coll, (ModificationHandler) listener);
        } else {
            ModificationHandler handler = ModificationHandlerFactory.createHandler(coll, listener);
            return new ObservedCollection(coll, handler);
        }
    }

    // Constructors
    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies) and takes a handler.
     * <p>
     * If a <code>null</code> handler is specified, an 
     * <code>ObservedHandler</code> is created. 
     * 
     * @param coll  the collection to decorate, must not be null
     * @param handler  the observing handler, may be null
     * @throws IllegalArgumentException if the collection is null
     */
    protected ObservedCollection(
            final Collection coll,
            final ModificationHandler handler) {
        super(coll);
        this.handler = (handler == null ? new StandardModificationHandler() : handler);
        ModificationHandlerFactory.initHandler(this.handler, this);
    }

    // Handler access
    //-----------------------------------------------------------------------
    /**
     * Gets the handler that is observing this collection.
     * 
     * @return the observing handler, never null
     */
    public ModificationHandler getHandler() {
        return handler;
    }
    
    // Collection
    //-----------------------------------------------------------------------
    public boolean add(Object object) {
        boolean result = false;
        if (handler.preAdd(object)) {
            result = collection.add(object);
            handler.postAdd(object, result);
        }
        return result;
    }

    public boolean addAll(Collection coll) {
        boolean result = false;
        if (handler.preAddAll(coll)) {
            result = collection.addAll(coll);
            handler.postAddAll(coll, result);
        }
        return result;
    }

    public void clear() {
        if (handler.preClear()) {
            collection.clear();
            handler.postClear();
        }
    }

    public Iterator iterator() {
        return new ObservedIterator(collection.iterator());
    }

    public boolean remove(Object object) {
        boolean result = false;
        if (handler.preRemove(object)) {
            result = collection.remove(object);
            handler.postRemove(object, result);
        }
        return result;
    }

    public boolean removeAll(Collection coll) {
        boolean result = false;
        if (handler.preRemoveAll(coll)) {
            result = collection.removeAll(coll);
            handler.postRemoveAll(coll, result);
        }
        return result;
    }

    public boolean retainAll(Collection coll) {
        boolean result = false;
        if (handler.preRetainAll(coll)) {
            result = collection.retainAll(coll);
            handler.postRetainAll(coll, result);
        }
        return result;
    }

    // Iterator
    //-----------------------------------------------------------------------
    /**
     * Inner class Iterator for the ObservedCollection.
     */
    protected class ObservedIterator extends AbstractIteratorDecorator {
        
        protected Object last;
        
        protected ObservedIterator(Iterator iterator) {
            super(iterator);
        }
        
        public Object next() {
            last = super.next();
            return last;
        }

        public void remove() {
            if (handler.preRemove(last)) {
                iterator.remove();
                handler.postRemove(last, true);
            }
        }
    }

}
