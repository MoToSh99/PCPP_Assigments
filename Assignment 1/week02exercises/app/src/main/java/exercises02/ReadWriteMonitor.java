package exercises02;

public class ReadWriteMonitor {
    private int readers = 0;
    private boolean writer = false;

    //////////////////////////
    // Read lock operations //
    //////////////////////////

    public synchronized void readLock() {
        while (writer)
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        readers++;
    }

    public synchronized void readUnlock() {
        readers--;
        if (readers == 0)
            this.notifyAll();
    }

    ///////////////////////////
    // Write lock operations //
    ///////////////////////////

    public synchronized void writeLock() {
        while (readers > 0 || writer) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writer = true;
    }

    public synchronized void writeUnlock() {
        writer = false;
        this.notifyAll();
    }
}
