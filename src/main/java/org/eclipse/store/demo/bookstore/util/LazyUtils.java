package org.eclipse.store.demo.bookstore.util;

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

import java.util.Optional;

import org.eclipse.serializer.reference.Lazy;

/**
 * {@link Lazy} reference utilities
 *
 */
public interface LazyUtils
{
	/**
	 * Clears a {@link Lazy} reference if it is not <code>null</code> and stored.
	 *
	 * @param <T>
	 * @param lazy the reference, may be <code>null</code>
	 * @return the optional content of the lazy reference when it was cleared successfully
	 * @see Lazy#isStored()
	 * @see Lazy#clear()
	 */
	public static <T> Optional<T> clearIfStored(final Lazy<T> lazy)
	{
		return lazy != null && lazy.isStored()
			? Optional.ofNullable(lazy.clear())
			: Optional.empty()
		;
	}
}
