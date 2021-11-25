package testingconcurrency;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

public class ConcurrentSetTest {

    // Variable with set under test
    private ConcurrentIntegerSet set;
    private int setSize;
    CyclicBarrier barrier;
    private int nrOfThreads;

    // TODO: Very likely you should add more variables here
        

    // Uncomment the appropriate line below to choose the class to
    // test
    // Remember that @BeforeEach is executed before each test
    @BeforeEach
    public void initialize() {
	    // init set
         set = new ConcurrentIntegerSetBuggy();
        // set = new ConcurrentIntegerSetSync();	
        // set = new ConcurrentIntegerSetLibrary();
    }

    // TODO: Define your tests below
    
    @RepeatedTest(10000)
    @DisplayName("Functional correctness test for add(Integer element)")
    public void addTest() {
        int N = 100;
        barrier = new CyclicBarrier(N + 1);
        for (int i = 0; i < N; i++) {
            try {
                int iR = i;
                new Thread(() -> {
                    try {
                        barrier.await();
                        set.add(iR);
                        barrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (Exception e) {
            }
        }
        try {
            barrier.await();
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        
        System.out.println(set.size());
        set.printSet();
        assert (set.size() == N);
    }

    @RepeatedTest(10000)
    @DisplayName("Functional correctness test for remove(Integer element)")
    public void removeTest() {
        int N = 100;
        for (int i = 0; i < N; i++) {
            set.add(i);
        }
        barrier = new CyclicBarrier(N + 1);
        for (int i = 0; i < N; i++) {
            try {
                int iR = i;
                new Thread(() -> {
                    try {
                        barrier.await();
                        set.remove(iR);
                        barrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (Exception e) {
            }
        }
        try {
            barrier.await();
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        
        System.out.println(set.size());
        set.printSet();
        assert (set.size() == 0);
    }
}
