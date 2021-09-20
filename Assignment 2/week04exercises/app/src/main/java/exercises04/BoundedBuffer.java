package exercises04;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.*;

public class BoundedBuffer implements BoundedBufferInteface {

    private Queue<Object> list = new LinkedList<Object>();
    private Semaphore producers;
    private Semaphore consumers;

    public BoundedBuffer(int size){
        producers = new Semaphore(size);   
        consumers = new Semaphore(0);   
    }

    @Override
    public Object take() throws Exception {
        consumers.acquire();
        Object o = list.poll();
        producers.release();
        return o;
    }

    @Override
    public void insert(Object elem) throws Exception {
        producers.acquire();
        list.offer(elem);
        consumers.release();
    }
    
}
