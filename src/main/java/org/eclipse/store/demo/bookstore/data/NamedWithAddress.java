
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

import java.util.Objects;

/**
 * Feature type for all named entities with an {@link Address}.
 *
 */
public abstract class NamedWithAddress extends Named
{
	private final Address address;

	protected NamedWithAddress(
		final String name,
		final Address address
	)
	{
		super(name);
		
		this.address = Objects.requireNonNull(address, () -> "Address cannot be null");
	}

	/**
	 * Get the address of this entity.
	 *
	 * @return the address
	 */
	public Address address()
	{
		return this.address;
	}

	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + " [" + this.name() + " - " + this.address + "]";
	}
	
}
