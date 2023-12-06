
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
 * Publisher entity which holds a name and an {@link Address}.
 * <p>
 * This type is immutable and therefor inherently thread safe.
 *
 */
public class Publisher extends NamedWithAddress
{
	/**
	 * Constructor to create a new {@link Publisher} instance.
	 *
	 * @param name not empty
	 * @param address not <code>null</code>
	 */
	public Publisher(
		final String  name   ,
		final Address address
	)
	{
		super(name, address);
	}

}
