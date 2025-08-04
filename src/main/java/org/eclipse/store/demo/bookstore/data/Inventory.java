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

import java.util.List;

import org.eclipse.store.demo.bookstore.util.concurrent.ReadWriteLocked;

import org.eclipse.store.gigamap.types.GigaMap;
import org.eclipse.store.gigamap.types.Indexer;

/**
 * Inventory entity which holds {@link Book}s and amounts of them.
 * <p>
 * All operations on this type are thread safe.
 *
 * @see ReadWriteLocked
 */
public class Inventory extends ReadWriteLocked
{
	public final static Indexer.Abstract<InventoryItem, Book> bookIndex = new Indexer.Abstract<>()
	{
		@Override
		public Class<Book> keyType()
		{
			return Book.class;
		}
		
		@Override
		public Book index(final InventoryItem entity)
		{
			return entity.book();
		}
	};
	
	
	private final GigaMap<InventoryItem> map = GigaMap.<InventoryItem>Builder()
		.withBitmapIndex(bookIndex)
		.build()
	;
	

	public Inventory()
	{
		super();
	}

	/**
	 * Package-private constructor used by {@link RandomDataGenerator}.
	 */
	Inventory(final List<InventoryItem> inventory)
	{
		super();
		
		this.map.addAll(inventory);
	}
	
	/**
	 * Get the amount of a specific book in this inventory.
	 *
	 * @param book the book
	 * @return the amount of the given book in this inventory or 0
	 */
	public int amount(final Book book)
	{
		return this.read(() ->
			(int)this.map.query(bookIndex.is(book)).findFirst().map(InventoryItem::amount).orElse(0)
		);
	}

	/**
	 * Get the total amount of slots (different books) in this inventory.
	 *
	 * @return the amount of slots
	 */
	public int slotCount()
	{
		return this.read(() ->
			(int)this.map.size()
		);
	}

	/**
	 * Gets all books as a {@link List}.
	 * Modifications to the returned list are not reflected to the backed data.
	 *
	 * @return all books
	 */
	public List<Book> books()
	{
		return this.read(() ->
			bookIndex.resolveKeys(this.map)
		);
	}

}
