package exercises07;


class LockHistogram implements Histogram {
	private final int[] counts;
	private int total = 0;

	public LockHistogram(int span) {
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

	@Override
	public int getAndClear(int bin) {
		// TODO Auto-generated method stub
		return 0;
	}
}