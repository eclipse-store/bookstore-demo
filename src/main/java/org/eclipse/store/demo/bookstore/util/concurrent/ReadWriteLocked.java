package org.eclipse.store.demo.bookstore.util.concurrent;

import java.util.concurrent.locks.Lock;

/*-
 * #%L
 * EclipseStore BookStore Demo
 * %%
 * Copyright (C) 2023 MicroStream Software
 * %%
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * #L%
 */

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * Facility to execute operations with read and write locks.
 * <p>
 * Non-reentrant read operations are not allowed until all write operations have been finished.
 * Additionally, a write operation can acquire the read lock, but not vice-versa.
 */
public class ReadWriteLocked
{
	/*
	 * Transient means it is not persisted by EclipseStore, but created on demand.
	 */
	private transient volatile ReentrantReadWriteLock mutex;

	public ReadWriteLocked()
	{
		super();
	}
	
	/**
	 * Executes an operation protected by a read lock.
	 *
	 * @param <T> the operation's return type
	 * @param operation the operation to execute
	 * @return the operation's result
	 */
	public final <T> T read(final Supplier<T> operation)
	{
		return this.execute(this.mutex().readLock(), operation);
	}

	/**
	 * Executes an operation protected by a read lock.
	 *
	 * @param operation the operation to execute
	 */
	public final void read(final Runnable operation)
	{
		this.execute(this.mutex().readLock(), operation);
	}

	/**
	 * Executes an operation protected by a write lock.
	 *
	 * @param <T> the operation's return type
	 * @param operation the operation to execute
	 * @return the operation's result
	 */
	public final <T> T write(final Supplier<T> operation)
	{
		return this.execute(this.mutex().writeLock(), operation);
	}

	/**
	 * Executes an operation protected by a write lock.
	 *
	 * @param operation the operation to execute
	 */
	public final void write(final Runnable operation)
	{
		this.execute(this.mutex().writeLock(), operation);
	}
	
	
	private ReentrantReadWriteLock mutex()
	{
		/*
		 * Double-checked locking to reduce the overhead of acquiring a lock
		 * by testing the locking criterion.
		 * The field (this.mutex) has to be volatile.
		 */
		ReentrantReadWriteLock mutex = this.mutex;
		if(mutex == null)
		{
			synchronized(this)
			{
				if((mutex = this.mutex) == null)
				{
					mutex = this.mutex = new ReentrantReadWriteLock();
				}
			}
		}
		return mutex;
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
		}
	}

}
