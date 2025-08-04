
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

import static org.eclipse.store.demo.bookstore.util.ValidationUtils.requireNonBlank;

import org.eclipse.store.gigamap.types.IndexerString;

/**
 * Feature type for all named entities, with {@link Comparable} capabilities.
 *
 */
public abstract class Named implements Comparable<Named>
{
	public static final IndexerString<Named> nameIndex = new IndexerString.Abstract<>()
	{
		@Override
		public String getString(final Named entity)
		{
			return entity.name();
		}
	};
	
	
	private final String name;

	protected Named(final String name)
	{
		super();
		
		this.name = requireNonBlank(name, () -> "Name cannot be empty");
	}

	/**
	 * Get the name of this entity.
	 *
	 * @return the name
	 */
	public String name()
	{
		return this.name;
	}

	@Override
	public int compareTo(final Named other)
	{
		return this.name().compareTo(other.name());
	}

	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + " [" + this.name + "]";
	}

}
