// For week 7
// raup@itu.dk * 10/10/2021
package exercises07;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

class ReadWriteCASLock implements SimpleRWTryLockInterface {

    public static void main(String[] args) {
        runSequentialTests();
        runParallelTests();
    }

    private final AtomicReference<Holders> holders = new AtomicReference<Holders>();

    public boolean readerTryLock() {
        final Thread current = Thread.currentThread();
        Holders tmpHolders;
        do {
            tmpHolders = holders.get();
            if (tmpHolders == null) {
                return holders.compareAndSet(null, new ReaderList(current, (ReaderList) tmpHolders));
            } else if (tmpHolders.getClass() == ReaderList.class) {
                return holders.compareAndSet(tmpHolders, new ReaderList(current, (ReaderList) tmpHolders));
            }
        } while (tmpHolders == null || tmpHolders.getClass() == ReaderList.class);
        return false;
    }

    // Challenging 7.2.7: You may add new methods
	
    
    public void readerUnlock() { 
        final Thread currentThread = Thread.currentThread();
        ReaderList tmpHolders = (ReaderList) holders.get();
        if (tmpHolders == null) {
            throw new RuntimeException("The lock is not held.");
        }
        if (tmpHolders.contains(currentThread)) {
            holders.compareAndSet(tmpHolders, tmpHolders.remove(currentThread));
            return;
        } else {
            throw new RuntimeException("The lock is not held by this reader.");
        }
    }

    
   public boolean writerTryLock() {
        final Thread currentThread = Thread.currentThread();
        final Writer writer = new Writer(currentThread);
        if (holders.get() == null) {
            Holders tmpHolders = holders.get();
            while (!holders.compareAndSet(tmpHolders, writer)) {
                tmpHolders = holders.get();
            }
            return true;
        }
        return false;
    }

    public void writerUnlock() {
        final Thread currentThread = Thread.currentThread();
        if (holders.get() == null) {
            throw new RuntimeException("The lock is not held.");
        }
        Writer tmpHolders = (Writer) holders.get();
        if (tmpHolders.thread == currentThread) {
            holders.compareAndSet(tmpHolders, null);
            return;
        } else {
            throw new RuntimeException("The lock is not held by this writer.");
        }
    }

    private static abstract class Holders { }

    private static class ReaderList extends Holders {
        private final Thread thread;
        private final ReaderList next;

        public ReaderList(Thread thread, ReaderList next) {
            this.thread = thread;
            this.next = next;
        }

        public boolean contains(Thread t) {
            for (ReaderList current = this; current != null; current = current.next) {
                if (thread == t) return true;
            }
            return false; 
        }

        public ReaderList remove(Thread t) {
            if (t == thread) return next;
            if (next == null) return this;
            return new ReaderList(thread, remove(t, next));
        }

        private static ReaderList remove(Thread t, ReaderList current) {
            if (current == null) return null;
            if (t == current.thread) return current.next;
            return new ReaderList(current.thread, remove(t, current.next));
        }

    }

    private static class Writer extends Holders {
        public final Thread thread;

        public Writer(Thread thread) {
            this.thread = thread;
        }
    }

public static void runSequentialTests() {
    ReadWriteCASLock lock = new ReadWriteCASLock();
    for (int i = 0; i < 5; i++) {
        Boolean b1 = lock.readerTryLock();
        System.out.println("Reader lock (should be true): " + b1);
        Boolean b2 = lock.writerTryLock();
        System.out.println("Writer lock (should be false): " + b2);
    }

    for (int i = 0; i < 5; i++) {
        lock.readerUnlock();
    }

    for (int i = 0; i < 5; i++) {
        Boolean b1 = lock.writerTryLock();
        System.out.println("Writer lock (should be true): " + b1);
        lock.writerUnlock();
    }
}

public static void runParallelTests() {
    ReadWriteCASLock lock = new ReadWriteCASLock();
    ExecutorService exec = Executors.newFixedThreadPool(10);
    exec.execute( () -> { 
        try { 
            Boolean b1 =  lock.writerTryLock();
            Thread.sleep(1000);
            lock.writerUnlock();
        }
        catch (Exception e) {}
    }); 
    Runnable runnable = () -> { 
        try { 
            Boolean b1 = lock.writerTryLock();
            lock.writerUnlock();
            Boolean b2 = lock.readerTryLock();
            lock.readerUnlock();
            System.out.println(b1 + " + " + b2);
        }
        catch (Exception e) {}
    }; 

    for (int i = 0; i < 10; i++)  {
        exec.execute(runnable);
    }
    exec.shutdown();
}


}
