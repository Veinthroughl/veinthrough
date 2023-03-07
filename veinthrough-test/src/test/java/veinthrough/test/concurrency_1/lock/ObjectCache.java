package veinthrough.test.concurrency_1.lock;

import lombok.Getter;

import java.util.concurrent.Semaphore;

/**
 * 文档：深入浅出Java Concurrency/《锁机制 part 7 信号量 (Semaphore)》,
 * 使用{@link Semaphore}实现的对象池
 */
@SuppressWarnings("unused")
public class ObjectCache<T> {
    public interface ObjectFactory<T> {
        T makeObject();
    }

    class Node {
        T obj;
        Node next;
    }

    @Getter
    private final int capacity;
    private final ObjectFactory<T> factory;
    private final Semaphore semaphore;
    private Node head;
    private Node tail;

    public ObjectCache(int capacity, ObjectFactory<T> factory) {
        this.capacity = capacity;
        this.factory = factory;
        this.semaphore = new Semaphore(this.capacity);
        this.head = null;
        this.tail = null;
    }

    public T getObject() throws InterruptedException {
        semaphore.acquire();
        return getNextObject();
    }

    /**
     * 并不能保证信号量足够的时候获取对象和返还对象是线程安全的, 所以仍然要使用synchronized
     */
    private synchronized T getNextObject() {
        if (head == null) {
            return factory.makeObject();
        } else {
            Node ret = head;
            head = head.next;
            if (head == null) tail = null;
            ret.next = null; // help GC
            return ret.obj;
        }
    }

    /**
     * 并不能保证信号量足够的时候获取对象和返还对象是线程安全的, 所以仍然要使用synchronized
     */
    private synchronized void returnObjectToPool(T t) {
        Node node = new Node();
        node.obj = t;
        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
    }

    public void returnObject(T t) {
        returnObjectToPool(t);
        semaphore.release();
    }
}

