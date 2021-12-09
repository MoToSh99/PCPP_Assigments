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
        Holders holder;
        do {
            holder = holders.get();
            if (holder instanceof Writer) {
                return false;
            }
            boolean success = holders.compareAndSet(holder, new ReaderList(Thread.currentThread(), (ReaderList) holder));
            if (success) {
                return true;
            }
        } while (holder == null || holder instanceof ReaderList);
        return true;
    }

    // Challenging 7.2.7: You may add new methods
	
    
    public void readerUnlock() { 
        ReaderList holder;
        do {
            holder = (ReaderList) holders.get();
            boolean success = holders.compareAndSet(holder, holder.remove(Thread.currentThread()));
            if (success) {
                return;
            }
        } while (holder != null && holder instanceof ReaderList && holder.contains(Thread.currentThread()));
        throw new RuntimeException("Lock is not held by this reader thread.");
    }

    
    public boolean writerTryLock() {
        return holders.compareAndSet(null, new Writer(Thread.currentThread()));
    }

    public void writerUnlock() {
        final Writer holder = (Writer) holders.get();
        if (holder == null || holder.thread == Thread.currentThread()) {
            throw new RuntimeException("Lock is not held by thread.");
        } else {
            holders.compareAndSet(holder, null);
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
