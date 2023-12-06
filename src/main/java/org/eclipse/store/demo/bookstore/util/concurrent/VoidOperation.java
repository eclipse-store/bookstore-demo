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
 * Operation with no return value, used by {@link ReadWriteLocked} and {@link ReadWriteLockedStriped}.
 *
 */
@FunctionalInterface
public interface VoidOperation
{
	/**
	 * Execute an arbitrary operation
	 */
	public void execute();
}
