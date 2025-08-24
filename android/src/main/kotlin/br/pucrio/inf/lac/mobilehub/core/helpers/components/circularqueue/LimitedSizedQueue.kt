package br.pucrio.inf.lac.mobilehub.core.helpers.components.circularqueue

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class LimitedSizedQueue<T>(maxSize: Int = Int.MAX_VALUE): CircularQueue<T> {
    init {
        require(maxSize > 0) { "The size has to be greater than 0" }
    }

    private val queue: BlockingQueue<T> = LinkedBlockingQueue(maxSize)

    override val size: Int
        get() = queue.size

    override fun contains(element: T): Boolean = queue.contains(element)

    override fun addAll(elements: Collection<T>): Boolean {
        require(!(elements === this))

        var modified = false
        for (element in elements) {
            if (add(element)) {
                modified = true
            }
        }

        return modified
    }

    override fun clear() = queue.clear()

    override fun element(): T = queue.element()

    override fun isEmpty(): Boolean = queue.isEmpty()

    override fun remove(): T = queue.remove()

    override fun containsAll(elements: Collection<T>): Boolean = queue.containsAll(elements)

    override fun iterator(): MutableIterator<T> = queue.iterator()

    override fun remove(element: T): Boolean = queue.remove(element)

    override fun removeAll(elements: Collection<T>): Boolean = queue.removeAll(elements)

    override fun add(element: T): Boolean = offer(element)

    override fun offer(element: T): Boolean {
        while (!queue.offer(element)) {
            queue.poll()
        }

        return true
    }

    override fun retainAll(elements: Collection<T>): Boolean = queue.retainAll(elements)

    override fun peek(): T? = queue.peek()

    override fun poll(): T? = queue.poll()

    override fun drainTo(c: MutableCollection<in T>): Int = queue.drainTo(c)

    override fun drainTo(c: MutableCollection<in T>, maxElements: Int): Int = queue.drainTo(c, maxElements)
}
