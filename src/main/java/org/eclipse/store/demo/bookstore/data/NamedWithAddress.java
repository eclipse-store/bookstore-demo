
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

import one.microstream.gigamap.Indexer;
import one.microstream.gigamap.IndexerString;

/**
 * Feature type for all named entities with an {@link Address}.
 *
 */
public abstract class NamedWithAddress extends Named
{
	public static final IndexerString<NamedWithAddress> address1Index = new IndexerString.Abstract<>()
	{
		@Override
		public String getString(final NamedWithAddress entity)
		{
			return entity.address().address();
		}
	};

	public static final IndexerString<NamedWithAddress> address2Index = new IndexerString.Abstract<>()
	{
		@Override
		public String getString(final NamedWithAddress entity)
		{
			return entity.address().address2();
		}
	};

	public static final IndexerString<NamedWithAddress> zipcodeIndex = new IndexerString.Abstract<>()
	{
		@Override
		public String getString(final NamedWithAddress entity)
		{
			return entity.address().zipCode();
		}
	};
	
	public static final Indexer.Abstract<NamedWithAddress, City> cityIndex = new Indexer.Abstract<>()
	{
		@Override
		public Class<City> keyType()
		{
			return City.class;
		}
		
		@Override
		public City indexEntity(final NamedWithAddress entity)
		{
			return entity.address().city();
		}
	};
	
	public static final Indexer.Abstract<NamedWithAddress, State> stateIndex = new Indexer.Abstract<>()
	{
		@Override
		public Class<State> keyType()
		{
			return State.class;
		}
		
		@Override
		public State indexEntity(final NamedWithAddress entity)
		{
			return entity.address().city().state();
		}
	};
	
	public static final Indexer.Abstract<NamedWithAddress, Country> countryIndex = new Indexer.Abstract<>()
	{
		@Override
		public Class<Country> keyType()
		{
			return Country.class;
		}
		
		@Override
		public Country indexEntity(final NamedWithAddress entity)
		{
			return entity.address().city().state().country();
		}
	};
	
	
	
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
