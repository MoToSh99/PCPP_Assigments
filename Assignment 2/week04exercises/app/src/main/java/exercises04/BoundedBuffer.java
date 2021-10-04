package exercises04;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.*;

public class BoundedBuffer implements BoundedBufferInteface {

    private Queue<Object> list = new LinkedList<Object>();
    private Semaphore producers;
    private Semaphore consumers;
    private Semaphore lock;

    public BoundedBuffer(int size){
        producers = new Semaphore(size);   
        consumers = new Semaphore(0); 
        lock = new Semaphore(1);  
    }

    @Override
    public Object take() throws Exception {
        consumers.acquire();
        lock.acquire();
        Object o = list.poll();
        lock.release();
        producers.release();
        return o;
    }

    @Override
    public void insert(Object elem) throws Exception {
        producers.acquire();
        lock.acquire();
        list.offer(elem);
        lock.release();
        consumers.release();
    }
    
}
