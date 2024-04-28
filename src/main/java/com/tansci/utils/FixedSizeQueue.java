package com.tansci.utils;

import java.util.LinkedList;

public class FixedSizeQueue<T> {
    private LinkedList<T> queue;
    private int maxSize;

    public FixedSizeQueue(int maxSize) {
        this.queue = new LinkedList<>();
        this.maxSize = maxSize;
    }

    public void enqueue(T element) {
        queue.addLast(element);
        if (queue.size() > maxSize) {
            queue.removeFirst();
        }
    }

    public T dequeue() {
        return queue.removeFirst();
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
    public LinkedList<T> getQueue(){
        return queue;
    }
}
