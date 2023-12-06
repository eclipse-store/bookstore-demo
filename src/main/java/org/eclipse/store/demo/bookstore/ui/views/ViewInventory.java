package org.eclipse.store.demo.bookstore.ui.views;

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

import java.util.stream.Stream;

import org.eclipse.store.demo.bookstore.BookStoreDemo;
import org.eclipse.store.demo.bookstore.data.Book;
import org.eclipse.store.demo.bookstore.data.InventoryItem;
import org.eclipse.store.demo.bookstore.data.Shop;

import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.router.Route;

/**
 * View to display {@link InventoryItem}s.
 *
 */
@Route(value = "inventory", layout = RootLayout.class)
public class ViewInventory extends ViewEntity<InventoryItem>
{
	private FilterComboBox<InventoryItem, Shop> shopFilter;
	private FilterComboBox<InventoryItem, Book> bookFilter;

	public ViewInventory()
	{
		super();
	}

	public void filterBy(final Book book)
	{
		this.bookFilter.setValue(book);
	}

	public void filterBy(final Shop shop)
	{
		this.shopFilter.setValue(shop);
	}

	@Override
	protected void createUI()
	{
		this.shopFilter = this.addGridColumnWithDynamicFilter("shop"  , InventoryItem::shop  );
		this.bookFilter = this.addGridColumnWithDynamicFilter("book"  , InventoryItem::book  );
		this.addGridColumn                              ("amount", InventoryItem::amount);
	}

	@Override
	public <R> R compute(final SerializableFunction<Stream<InventoryItem>, R> function) {
		return BookStoreDemo.getInstance().data().shops().computeInventory(function);
	}

}
