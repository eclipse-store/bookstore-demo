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

import org.eclipse.store.gigamap.types.Condition;
import org.eclipse.store.gigamap.types.GigaMap;
import org.eclipse.store.gigamap.types.GigaQuery;

/**
 * All registered customers of this company.
 * <p>
 * This type is used to read and write the {@link Customer}s.
 * <p>
 * All operations on this type are thread safe.
 *
 * @see Data#customers()
 * @see ReadWriteLocked
 */
public class Customers extends ReadWriteLocked
{
	private final GigaMap<Customer> map = GigaMap.<Customer>Builder()
		.withBitmapIdentityIndex(Customer.idIndex)
		.withBitmapIndex(NamedWithAddress.address1Index)
		.withBitmapIndex(NamedWithAddress.address2Index)
		.withBitmapIndex(NamedWithAddress.zipcodeIndex)
		.withBitmapIndex(NamedWithAddress.cityIndex)
		.withBitmapIndex(NamedWithAddress.stateIndex)
		.withBitmapIndex(NamedWithAddress.countryIndex)
		.withBitmapIndex(Named.nameIndex)
		.build()
	;
	

	public Customers()
	{
		super();
	}
	
	/**
	 * Adds a new customer and stores it with the {@link BookStoreDemo}'s {@link EmbeddedStorageManager}.
	 * <p>
	 * This is a synonym for:<pre>this.add(customer, BookStoreDemo.getInstance().storageManager())</pre>
	 *
	 * @param customer the new customer
	 */
	public void add(final Customer customer)
	{
		this.write(() ->
		{
			this.map.add(customer);
			this.map.store();
		});
	}

	/**
	 * Adds a new customer and stores it with the given persister.
	 *
	 * @param customer the new customer
	 * @param persister the persister to store it with
	 * @see #add(Customer)
	 */
	public void add(
		final Customer           customer ,
		final PersistenceStoring persister
	)
	{
		this.write(() ->
		{
			this.map.add(customer);
			persister.store(this.map);
		});
	}

	/**
	 * Adds a range of new customers and stores it with the {@link BookStoreDemo}'s {@link EmbeddedStorageManager}.
	 * <p>
	 * This is a synonym for:<pre>this.addAll(customers, BookStoreDemo.getInstance().storageManager())</pre>
	 *
	 * @param customers the new customers
	 */
	public void addAll(final Collection<? extends Customer> customers)
	{
		this.write(() ->
		{
			this.map.addAll(customers);
			this.map.store();
		});
	}

	/**
	 * Adds a range of new customers and stores it with the given persister.
	 *
	 * @param customers the new customers
	 * @param persister the persister to store them with
	 * @see #addAll(Collection)
	 */
	public void addAll(
		final Collection<? extends Customer> customers,
		final PersistenceStoring             persister
	)
	{
		this.write(() ->
		{
			this.map.addAll(customers);
			persister.store(this.map);
		});
	}

	/**
	 * Gets the total amount of all customers.
	 *
	 * @return the amount of customers
	 */
	public synchronized long customerCount()
	{
		return this.read(() ->
			this.map.size()
		);
	}

	/**
	 * Gets the customer with a specific ID or <code>null</code> if none was found.
	 *
	 * @param customerId ID to search by
	 * @return the matching customer or <code>null</code>
	 */
	public Customer ofId(final int customerId)
	{
		return this.read(() ->
			this.map.query(Customer.idIndex.is(customerId)).findFirst().orElse(null)
		);
	}
	
	public <R> R compute(
		final Condition<Customer>           condition,
		final int                           offset,
		final int                           limit,
		final Function<Stream<Customer>, R> function
	)
	{
		return this.read(() ->
		{
			GigaQuery<Customer> query = this.map.query();
			if(condition != null)
			{
				query = query.and(condition);
			}
			return function.apply(query.toList(offset, limit).stream());
		});
	}

}
