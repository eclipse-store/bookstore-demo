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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.serializer.concurrency.LockScope;
import org.eclipse.serializer.persistence.types.PersistenceStoring;
import org.eclipse.store.demo.bookstore.BookStoreDemo;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;

/**
 * All registered customers of this company.
 * <p>
 * This type is used to read and write the {@link Customer}s.
 * <p>
 * All operations on this type are thread safe.
 *
 * @see Data#customers()
 * @see LockScope
 */
public class Customers extends LockScope
{
	/**
	 * Map with {@link Customer#customerId()} as key
	 */
	private final Map<Integer, Customer> customers = new HashMap<>();

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
		this.add(customer, BookStoreDemo.getInstance().storageManager());
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
		this.write(() -> {
			this.customers.put(customer.customerId(), customer);
			persister.store(this.customers);
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
		this.addAll(customers, BookStoreDemo.getInstance().storageManager());
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
		this.write(() -> {
			this.customers.putAll(
				customers.stream().collect(
					Collectors.toMap(Customer::customerId, Function.identity())
				)
			);
			persister.store(this.customers);
		});
	}

	/**
	 * Gets the total amount of all customers.
	 *
	 * @return the amount of customers
	 */
	public synchronized int customerCount()
	{
		return this.read(
			this.customers::size
		);
	}

	/**
	 * Gets all customers as a {@link List}.
	 * Modifications to the returned list are not reflected to the backed data.
	 *
	 * @return all customers
	 */
	public List<Customer> all()
	{
		return this.read(() ->
			new ArrayList<>(this.customers.values())
		);
	}

	/**
	 * Executes a function with a {@link Stream} of {@link Customer}s and returns the computed value.
	 *
	 * @param <T> the return type
	 * @param streamFunction computing function
	 * @return the computed result
	 */
	public <T> T compute(final Function<Stream<Customer>, T> streamFunction)
	{
		return this.read(() ->
			streamFunction.apply(
				this.customers.values().parallelStream()
			)
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
			this.customers.get(customerId)
		);
	}

}
