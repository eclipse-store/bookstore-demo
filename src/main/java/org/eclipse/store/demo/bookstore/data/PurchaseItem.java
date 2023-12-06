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

import static org.eclipse.serializer.util.X.notNull;
import static org.eclipse.store.demo.bookstore.util.ValidationUtils.requirePositive;

import javax.money.MonetaryAmount;

/**
 * Purchase item entity, which holds a {@link Book}, an amount and a price.
 *
 */
public class PurchaseItem
{
	private final Book           book  ;
	private final int            amount;
	private final MonetaryAmount price ;

	/**
	 * Constructor to create a new {@link PurchaseItem} instance.
	 *
	 * @param book not <code>null</code>
	 * @param amount positive amount
	 */
	public PurchaseItem(
		final Book book  ,
		final int  amount
	)
	{
		super();
		this.book   = notNull(book);
		this.amount = requirePositive(amount, () -> "Amount must be greater than zero");
		this.price  = book.retailPrice();
	}
	/**
	 * Get the book
	 *
	 * @return the book
	 */
	public Book book()
	{
		return this.book;
	}

	/**
	 * Get the amount of books
	 *
	 * @return the amount
	 */
	public int amount()
	{
		return this.amount;
	}

	/**
	 * Get the price the book was sold for
	 *
	 * @return the price at the time the book was sold
	 */
	public MonetaryAmount price()
	{
		return this.price;
	}

	/**
	 * Computes the total amount of the purchase item (price * amound)
	 *
	 * @return the total amount of this item
	 */
	public MonetaryAmount itemTotal()
	{
		return this.price.multiply(this.amount);
	}

}
