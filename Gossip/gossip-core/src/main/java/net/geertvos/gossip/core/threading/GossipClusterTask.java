/**
    This file is part of the Java Gossip Cluster Framework.

    The Java Gossip Framework is free software: you can redistribute it and/or modify
    it under the terms of the Lesser GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The Java Gossip Framework is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.geertvos.gossip.core.threading;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * @author Geert Vos
 */
public abstract class GossipClusterTask<T> implements Callable<T>, Runnable {

	private final CountDownLatch latch = new CountDownLatch(1);
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
