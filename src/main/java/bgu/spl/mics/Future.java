package bgu.spl.mics;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available
 *
 *
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	private T result;
	private AtomicBoolean isDone = new AtomicBoolean(false);
	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		isDone.set(false);
		result = null;
	}

	/**
	 * retrieves the result the Future object holds if it has been resolved.
	 * This is a blocking method! It waits for the computation in case it has
	 * not been completed.
	 * <p>
	 * @return return the result of type T if it is available, if not wait until it is available.
	 *
	 */
	//We need to wait. it is better that every Future will wait on himself, instead of making other keys.
	public synchronized T get() { //sleeps of future monitor
		while(!isDone.get()){
			try {
				wait();
			}catch (InterruptedException e) {}
		}
		return result;
	}

	/**
	 * Resolves the result of this Future object.
	 */
	//wake up other sleeping thread
	public synchronized void resolve (T result) {
		if (!isDone.get()) {
			this.result = result;
			isDone.set(true);
			notifyAll();
		}
	}

	/**
	 * @return true if this object has been resolved, false otherwise
	 */
	public boolean isDone() {
		return isDone.get();
	}

	/**
	 * retrieves the result the Future object holds if it has been resolved,
	 * This method is non-blocking, it has a limited amount of time determined
	 * by {@code timeout}
	 * <p>
	 * @param timeout 	the maximal amount of time units to wait for the result.
	 * @param unit		the {@link TimeUnit} time units to wait.
	 * @return return the result of type T if it is available, if not,
	 * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
	 *         elapsed, return null.
	 */
	//Again we need to sleep the Future.
	public synchronized T get(long timeout, TimeUnit unit) {
		if(!isDone.get()){
			try {
				wait(TimeUnit.MILLISECONDS.convert(timeout,unit));
				if(!isDone.get()) {
					return null;
				}
				else {
					return result;
				}
			}catch (InterruptedException interE) {return null;}
		}
		return result;
	}

}
