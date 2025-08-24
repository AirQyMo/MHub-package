package br.pucrio.inf.lac.mobilehub.core.helpers.components.circularqueue

import java.util.*

interface CircularQueue<E> : Queue<E> {
    /**
     * Removes all available elements from this queue and adds them
     * to the given collection.  This operation may be more
     * efficient than repeatedly polling this queue.  A failure
     * encountered while attempting to add elements to
     * collection `c` may result in elements being in neither,
     * either or both collections when the associated exception is
     * thrown.  Attempts to drain a queue to itself result in
     * `IllegalArgumentException`. Further, the behavior of
     * this operation is undefined if the specified collection is
     * modified while the operation is in progress.
     *
     * @param c the collection to transfer elements into
     * @return the number of elements transferred
     * @throws UnsupportedOperationException if addition of elements
     * is not supported by the specified collection
     * @throws ClassCastException if the class of an element of this queue
     * prevents it from being added to the specified collection
     * @throws NullPointerException if the specified collection is null
     * @throws IllegalArgumentException if the specified collection is this
     * queue, or some property of an element of this queue prevents
     * it from being added to the specified collection
     */
    fun drainTo(c: MutableCollection<in E>): Int

    /**
     * Removes at most the given number of available elements from
     * this queue and adds them to the given collection.  A failure
     * encountered while attempting to add elements to
     * collection `c` may result in elements being in neither,
     * either or both collections when the associated exception is
     * thrown.  Attempts to drain a queue to itself result in
     * `IllegalArgumentException`. Further, the behavior of
     * this operation is undefined if the specified collection is
     * modified while the operation is in progress.
     *
     * @param c the collection to transfer elements into
     * @param maxElements the maximum number of elements to transfer
     * @return the number of elements transferred
     * @throws UnsupportedOperationException if addition of elements
     * is not supported by the specified collection
     * @throws ClassCastException if the class of an element of this queue
     * prevents it from being added to the specified collection
     * @throws NullPointerException if the specified collection is null
     * @throws IllegalArgumentException if the specified collection is this
     * queue, or some property of an element of this queue prevents
     * it from being added to the specified collection
     */
    fun drainTo(c: MutableCollection<in E>, maxElements: Int): Int
}
