/**
 * This class MultiReaderLock is for protecting shared objects by read and write
 * lock.
 * 
 * @author ANG ZHANG
 * 
 */
public class MultiReadersLock {

	/**
	 * The number of threads which need to read data from shared objects.
	 */
	private int readers;
	/**
	 * The number of threads which need to write data from shared objects.
	 */
	private int writers;


	/**
	 * Constructor of class
	 */
	public MultiReadersLock() {
		this.readers = 0;
		this.writers = 0;

	}

	/**
	 * To get the read lock to avoid other threads write data, increasing the
	 * number of thread who is reading data.
	 */
	public synchronized void acquireReadLock() {
		while (writers > 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
		readers++;
	}

	/**
	 * Releasing the lock, decrease the number of thread who is reading data.
	 */
	public synchronized void releaseReadLock() {
		readers--;
		notifyAll();
	}

	/**
	 * Obtaining the lock to avoid other threads read or write the shared
	 * objects, increasing the number of thread who is writing the data.
	 */
	public synchronized void acquireWriteLock() {

		while (readers > 0 || writers > 0) {
			try {

				wait();
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
		writers++;

	}

	/**
	 * releasing the lock, decrease the number of thread who is writing shared
	 * objects.
	 */
	public synchronized void releaseWriteLock() {
		writers--;
		notifyAll();
	}

}
