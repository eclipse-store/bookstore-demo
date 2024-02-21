package org.eclipse.store.demo.bookstore.util.concurrent;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;


/**
 * Facility to execute operations with striped read and write locks.
 * <p>
 * Conceptually, lock striping is the technique of dividing a lock into many stripes,
 * increasing the granularity of a single lock and allowing independent operations to lock
 * different stripes and proceed concurrently, instead of creating contention for a single lock.
 * <p>
 * Non-reentrant read operations are not allowed until all write operations of the affected stripe
 * have been finished.
 * Additionally, a write operation can acquire the read lock, but not vice-versa.
 */
public class ReadWriteLockedStriped
{
	private record LockHolder(int key, ReentrantReadWriteLock lock)
	{
	}
	
	/*
	 * Transient means it is not persisted by EclipseStore, but created on demand.
	 */
	private transient volatile int                                                   stripeCount;
	private transient volatile ReferenceQueue<LockHolder>                            queue;
	private transient volatile ConcurrentHashMap<Integer, WeakReference<LockHolder>> locks;

	public ReadWriteLockedStriped()
	{
		super();
	}
	
	/**
	 * Executes an operation protected by a read lock for a given key.
	 *
	 * @param <T> the operation's return type
	 * @param key an arbitrary, non-null key
	 * @param operation the operation to execute
	 * @return the operation's result
	 */
	public final <T> T read(final Object key, final Supplier<T> operation)
	{
		this.ensureInitialized();
		return this.execute(this.getLock(key).readLock(), operation);
	}

	/**
	 * Executes an operation protected by a read lock for a given key.
	 *
	 * @param key an arbitrary, non-null key
	 * @param operation the operation to execute
	 */
	public final void read(final Object key, final Runnable operation)
	{
		this.ensureInitialized();
		this.execute(this.getLock(key).readLock(), operation);
	}

	/**
	 * Executes an operation protected by a write lock for a given key.
	 *
	 * @param <T> the operation's return type
	 * @param key an arbitrary, non-null key
	 * @param operation the operation to execute
	 * @return the operation's result
	 */
	public final <T> T write(final Object key, final Supplier<T> operation)
	{
		this.ensureInitialized();
		return this.execute(this.getLock(key).writeLock(), operation);
	}

	/**
	 * Executes an operation protected by a write lock for a given key.
	 *
	 * @param key an arbitrary, non-null key
	 * @param operation the operation to execute
	 */
	public final void write(final Object key, final Runnable operation)
	{
		this.ensureInitialized();
		this.execute(this.getLock(key).writeLock(), operation);
	}
	
	private void ensureInitialized()
	{
		/*
		 * Double-checked locking to reduce the overhead of acquiring a lock
		 * by testing the locking criterion.
		 * The field (this.delegate) has to be volatile.
		 */
		ReferenceQueue<LockHolder> queue = this.queue;
		if(queue == null)
		{
			synchronized(this)
			{
				if((queue = this.queue) == null)
				{
					this.stripeCount = 4;
					this.queue       = new ReferenceQueue<>();
					this.locks       = new ConcurrentHashMap<>();
				}
			}
		}
	}
	
	private ReentrantReadWriteLock getLock(final Object keyObject)
	{
		Objects.requireNonNull(keyObject, "keyObject can't be null");
		final int key = keyObject.hashCode() % this.stripeCount;
		return this.locks.compute(
			key,
			(i, currentReference) ->
			{
				if(currentReference == null)
				{
					return new WeakReference<>(new LockHolder(key, new ReentrantReadWriteLock()), this.queue);
				}
				final var lockHolder = currentReference.get();
				if(lockHolder == null)
				{
					return new WeakReference<>(new LockHolder(key, new ReentrantReadWriteLock()), this.queue);
				}
				return currentReference;
			}
		).get().lock();
	}
	
	private <T> T execute(final Lock theLock, final Supplier<T> operation)
	{
		theLock.lock();
		try
		{
			return operation.get();
		}
		finally
		{
			theLock.unlock();
			this.purgeStaleLocks();
		}
	}
	
	private void execute(final Lock theLock, final Runnable operation)
	{
		theLock.lock();
		try
		{
			operation.run();
		}
		finally
		{
			theLock.unlock();
			this.purgeStaleLocks();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void purgeStaleLocks()
	{
		Reference<LockHolder> reference;
		while((reference = (Reference<LockHolder>)this.queue.poll()) != null)
		{
			synchronized(this.queue)
			{
				this.locks.remove(reference.get().key());
			}
		}
	}

}
