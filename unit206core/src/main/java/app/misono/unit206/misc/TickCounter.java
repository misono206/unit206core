package app.misono.unit206.misc;

public class TickCounter {
	private static final String TAG = "";

	private int tickTotal;
	private int min, max;
	private int count;

	public TickCounter() {
		min = Integer.MAX_VALUE;
	}

	public synchronized void addTick(int msec) {
		tickTotal += msec;
		count++;
		if (msec < min) {
			min = msec;
		}
		if (max < msec) {
			max = msec;
		}
	}

	public int getAverageTick() {
		return tickTotal / count;
	}

	public int getMinTick() {
		return min;
	}

	public int getMaxTick() {
		return max;
	}

}
