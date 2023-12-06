
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

import org.eclipse.store.demo.bookstore.BookStoreDemo;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;

/**
 * Root object for all data used by this application.
 * <p>
 * This is the entry point for the persisted object graph.
 * <p>
 * The data is divided into four sections:
 * <ul>
 * <li>{@link Books}</li>
 * <li>{@link Shops}</li>
 * <li>{@link Customers}</li>
 * <li>{@link Purchases}</li>
 * </ul>
 *
 * @see <a href="https://docs.eclipsestore.io/manual/storage/root-instances.html">EclipseStore Reference Manual</a>
 */
public class Data
{
	private final Books     books     = new Books    ();
	private final Shops     shops     = new Shops    ();
	private final Customers customers = new Customers();
	private final Purchases purchases = new Purchases();

	public Data()
	{
		super();
	}
	
	/**
	 * Get the {@link Books} instance of this data node.
	 * @return the {@link Books}
	 */
	public Books books()
	{
		return this.books;
	}

	/**
	 * Get the {@link Shops} instance of this data node.
	 * @return the {@link Shops}
	 */
	public Shops shops()
	{
		return this.shops;
	}

	/**
	 * Get the {@link Customers} instance of this data node.
	 * @return the {@link Customers}
	 */
	public Customers customers()
	{
		return this.customers;
	}

	/**
	 * Get the {@link Purchases} instance of this data node.
	 * @return the {@link Purchases}
	 */
	public Purchases purchases()
	{
		return this.purchases;
	}

	/**
	 * This method is used exclusively by the {@link BookStoreDemo}.
	 */
	public DataMetrics populate(
		final RandomDataAmount       initialDataSize,
		final EmbeddedStorageManager storageManager
	)
	{
		return new RandomDataGenerator(
			this.books,
			this.shops,
			this.customers,
			this.purchases,
			initialDataSize,
			storageManager
		)
		.generate();
	}

}
