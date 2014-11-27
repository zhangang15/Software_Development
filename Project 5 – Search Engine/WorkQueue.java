import java.util.LinkedList;

/**
 * 
 * @author Sophie Engle
 */

public class WorkQueue {
	/** Thread pool. **/
	private final PoolWorker[] workers;

	/** Work queue. Using a {@link LinkedList} as a queue. **/
	private final LinkedList<Runnable> queue;

	/** Shutdown flag. Example of using a <code>volatile</code> variable. **/
	private volatile boolean shutdown;

	/**
	 * Class-specific logger. Allows debug messages for this class to be
	 * disabled by adding <code>WorkQueue.log.setLevel(Level.OFF)</code> in your
	 * code.
	 */

	/**
	 * Creates a thread pool and work queue and starts the worker threads.
	 * 
	 * @param threads
	 *            number of worker threads (must be at least 1)
	 */
	public WorkQueue(int threads) {
		if (threads < 1) {
			throw new IllegalArgumentException("Only " + threads
					+ " worker threads specified. Must have at least"
					+ " one worker thread.");
		}

		this.queue = new LinkedList<Runnable>();
		this.workers = new PoolWorker[threads];

		shutdown = false;

		for (int i = 0; i < threads; i++) {
			workers[i] = new PoolWorker();
			workers[i].start();
		}

	}

	/**
	 * Adds work to the work queue to be executed by the next available worker
	 * thread. Note that this method is not synchronized! Do you understand why?
	 * 
	 * @param r
	 *            work to execute
	 */
	public void execute(Runnable r) {
		synchronized (queue) {
			queue.addLast(r);
			queue.notify();
		}
	}

	/**
	 * Shuts down the work queue after allowing any currently executing threads
	 * to finish first. Might leave unfinished work in queue.
	 */
	public void shutdown() {

		shutdown = true;

		synchronized (queue) {
			queue.notifyAll();
		}
	}

	/**
	 * Worker thread class. Each thread looks for work in the work queue and
	 * executes the work if found. Otherwise, the thread will wait until
	 * notified that there is now work in the queue.
	 */
	private class PoolWorker extends Thread {
		@Override
		public void run() {
			Runnable r = null;

			while (true) {
				synchronized (queue) {
					while (queue.isEmpty() && !shutdown) {
						try {

							queue.wait();
						} catch (InterruptedException ex) {

						}
					}

					if (shutdown) {

						break;
					} else {

						r = queue.removeFirst();
					}
				}

				try {

					r.run();
				} catch (RuntimeException ex) {

				}
			}

		}
	}
}
