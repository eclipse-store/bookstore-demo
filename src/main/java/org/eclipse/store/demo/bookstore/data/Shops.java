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

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.serializer.persistence.types.PersistenceStoring;
import org.eclipse.store.demo.bookstore.BookStoreDemo;
import org.eclipse.store.demo.bookstore.util.concurrent.ReadWriteLocked;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;

import one.microstream.gigamap.Condition;
import one.microstream.gigamap.GigaMap;
import one.microstream.gigamap.GigaQuery;

/**
 * All retail shops operated by this company.
 * <p>
 * This type is used to read and write the {@link Shop}s, their {@link Employee}s and {@link Inventory}s.
 * <p>
 * All operations on this type are thread safe.
 *
 * @see Data#shops()
 * @see ReadWriteLocked
 */
public class Shops extends ReadWriteLocked
{
	private final GigaMap<Shop> map = GigaMap.<Shop>Builder()
		.withBitmapIndex(Named.nameIndex)
		.withBitmapIndex(NamedWithAddress.address1Index)
		.withBitmapIndex(NamedWithAddress.address2Index)
		.withBitmapIndex(NamedWithAddress.zipcodeIndex)
		.withBitmapIndex(NamedWithAddress.cityIndex)
		.withBitmapIndex(NamedWithAddress.stateIndex)
		.withBitmapIndex(NamedWithAddress.countryIndex)
		.build();

	public Shops()
	{
		super();
	}
	
	/**
	 * Adds a new shop and stores it with the {@link BookStoreDemo}'s {@link EmbeddedStorageManager}.
	 * <p>
	 * This is a synonym for:<pre>this.add(shop, BookStoreDemo.getInstance().storageManager())</pre>
	 *
	 * @param shop the new shop
	 */
	public void add(final Shop shop)
	{
		this.write(() ->
		{
			this.map.add(shop);
			this.map.store();
		});
	}

	/**
	 * Adds a new shop and stores it with the given persister.
	 *
	 * @param shop the new shop
	 * @param persister the persister to store it with
	 * @see #add(Shop)
	 */
	public void add(
		final Shop               shop     ,
		final PersistenceStoring persister
	)
	{
		this.write(() ->
		{
			this.map.add(shop);
			persister.store(this.map);
		});
	}

	/**
	 * Adds a range of new shops and stores it with the {@link BookStoreDemo}'s {@link EmbeddedStorageManager}.
	 * <p>
	 * This is a synonym for:<pre>this.addAll(shops, BookStoreDemo.getInstance().storageManager())</pre>
	 *
	 * @param shops the new shops
	 */
	public void addAll(final Collection<? extends Shop> shops)
	{
		this.write(() ->
		{
			this.map.addAll(shops);
			this.map.store();
		});
	}

	/**
	 * Adds a range of new shops and stores it with the given persister.
	 *
	 * @param shops the new shops
	 * @param persister the persister to store them with
	 * @see #addAll(Collection)
	 */
	public void addAll(
		final Collection<? extends Shop> shops    ,
		final PersistenceStoring         persister
	)
	{
		this.write(() ->
		{
			this.map.addAll(shops);
			persister.store(this.map);
		});
	}

	/**
	 * Gets the total amount of all shops.
	 *
	 * @return the amount of shops
	 */
	public int shopCount()
	{
		return this.read(() ->
			(int)this.map.size()
		);
	}

	/**
	 * Clears all {@link Lazy} references used by all shops.
	 * This frees the used memory but you do not lose the persisted data. It is loaded again on demand.
	 *
	 * @see Shop#clear()
	 */
	public void clear()
	{
		this.write(() ->
			this.map.forEach(Shop::clear)
		);
	}

	/**
	 * Gets the shop with a specific name or <code>null</code> if none was found.
	 *
	 * @param name the name to search by
	 * @return the matching shop or <code>null</code>
	 */
	public Shop ofName(final String name)
	{
		return this.read(() ->
			this.map.query(Named.nameIndex.is(name))
				.findFirst()
				.orElse(null)
		);
	}
	
	public Country countryByCode(final String code)
	{
		return this.read(() ->
			NamedWithAddress.countryIndex.resolveKeys(this.map)
				.stream()
				.filter(c -> c.code().equals(code))
				.findAny()
				.orElse(null)
		);
	}
	
	public <R> R compute(
		final Condition<Shop>           condition,
		final int                       offset,
		final int                       limit,
		final Function<Stream<Shop>, R> function
	)
	{
		return this.read(() ->
		{
			GigaQuery<Shop> query = this.map.query();
			if(condition != null)
			{
				query = query.and(condition);
			}
			return function.apply(query.toList(offset, limit).stream());
		});
	}

}
