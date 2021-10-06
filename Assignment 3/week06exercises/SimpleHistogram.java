
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

interface Histogram {
	public void increment(int bin);

	public int getCount(int bin);

	public float getPercentage(int bin);

	public int getSpan();

	public int getTotal();
}

public class SimpleHistogram {
	public static void main(String[] args) {
		final Histogram histogram = new Histogram2(30);
        dump(countParallelN(5_000_000, 30, histogram));
    }
    
    public static Histogram countParallelN(int range, int threadCount,
			final Histogram histogram) {
		final int perThread = range / threadCount;

		Thread[] threads = new Thread[threadCount];
		for (int t = 0; t < threadCount; t++) {
			final int from = perThread * t, to = (t + 1 == threadCount) ? range
					: perThread * (t + 1);
			threads[t] = new Thread(new Runnable() {
				public void run() {
					for (int i = from; i < to; i++)
						histogram.increment(countFactors(i));
				}
			});
		}
		for (int t = 0; t < threadCount; t++)
			threads[t].start();
		try {
			for (int t = 0; t < threadCount; t++)
				threads[t].join();
		} catch (InterruptedException exn) {
		}
		return histogram;
	}

	public static void dump(Histogram histogram) {
		for (int bin = 0; bin < histogram.getSpan(); bin++) {
			System.out.printf("%4d: %9d%n", bin, histogram.getCount(bin));
		}
		System.out.printf("      %9d%n", histogram.getTotal());
	}
    
    public static int countFactors(int p) {
		if (p < 2)
			return 0;
		int factorCount = 1, k = 2;
		while (p >= k * k) {
			if (p % k == 0) {
				factorCount++;
				p /= k;
			} else
				k++;
		}
		return factorCount;
	}
}

class Histogram1 implements Histogram {
	private int[] counts;
	private int total = 0;

	public Histogram1(int span) {
		this.counts = new int[span];
	}

	public void increment(int bin) {
		counts[bin] = counts[bin] + 1;
		total++;
	}

	public int getCount(int bin) {
		return counts[bin];
	}

	public float getPercentage(int bin) {
		return getCount(bin) / getTotal() * 100;
	}

	public int getSpan() {
		return counts.length;
	}

	public int getTotal() {
		return total;
	}
}

class Histogram2 implements Histogram {
	private final int[] counts;
	private int total = 0;

	public Histogram2(int span) {
		this.counts = new int[span];
	}

	public synchronized void increment(int bin) {
		counts[bin] = counts[bin] + 1;
		total++;
	}

	public synchronized int getCount(int bin) {
		return counts[bin];
	}

	public float getPercentage(int bin) {
		return getCount(bin) / getTotal() * 100;
	}

	public int getSpan() {
		return counts.length;
	}

	public synchronized int getTotal() {
		return total;
	}
}
