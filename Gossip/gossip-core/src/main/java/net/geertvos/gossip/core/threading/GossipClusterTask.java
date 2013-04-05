package net.geertvos.gossip.core.threading;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;


public abstract class GossipClusterTask<T> implements Callable, Runnable {

	private CountDownLatch latch = new CountDownLatch(1);
	private T result = null;

	@Override
	public T call() {
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}

	protected void setResult(T result) {
		this.result = result;
		latch.countDown();
	}
	
}
