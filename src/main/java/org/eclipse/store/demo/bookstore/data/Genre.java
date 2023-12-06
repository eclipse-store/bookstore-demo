
package org.eclipse.store.demo.bookstore.data;

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
 * Genre entity which holds a name.
 * <p>
 * This type is immutable and therefor inherently thread safe.
 *
 */
public class Genre extends Named
{
	/**
	 * Constructor method to create a new {@link Genre} instance.
	 *
	 * @param name not empty
	 */
	public Genre(
		final String name
	)
	{
		super(name);
	}

}
