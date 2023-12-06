package org.eclipse.store.demo.bookstore.util.concurrent;

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

/**
 * Operation with a return value, used by {@link ReadWriteLocked} and {@link ReadWriteLockedStriped}.
 *
 * @param T the return type
 */
@FunctionalInterface
public interface ValueOperation<T>
{
	/**
	 * Execute an arbitrary operation and return the result
	 *
	 * @return the result of the operation
	 */
	public T execute();
}
