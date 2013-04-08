package net.geertvos.gossip.core.threading;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

	private AtomicInteger counter = new AtomicInteger();
	private String prefix = "";

	public NamedThreadFactory(String prefix) {
		this.prefix = prefix;
	}

	public Thread newThread(Runnable r) {
		return new Thread(r, prefix + "-" + counter.incrementAndGet());
	}
}