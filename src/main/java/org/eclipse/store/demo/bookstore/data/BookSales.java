
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

import static org.eclipse.store.demo.bookstore.util.ValidationUtils.requireZeroOrPositive;

import java.util.Objects;

/**
 * View of a book's sale numbers, with {@link Comparable} capabilities.
 * <p>
 * This type is immutable and therefor inherently thread safe.
 *
 */
public class BookSales implements Comparable<BookSales>
{
	private final Book book  ;
	private final int  amount;

	/**
	 * Constructor to create a new {@link BookSales} instance.
	 *
	 * @param book not <code>null</code>
	 * @param amount zero or positive
	 */
	public BookSales(
		final Book book  ,
		final int  amount
	)
	{
		super();
		
		this.book   = Objects.requireNonNull(book, () -> "Book cannot be null");
		this.amount = requireZeroOrPositive(amount, () -> "Amount cannot be negative");
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
	 * Get the amount
	 *
	 * @return the amount
	 */
	public int amount()
	{
		return this.amount;
	}

	@Override
	public int compareTo(final BookSales other)
	{
		return Integer.compare(other.amount(), this.amount());
	}

	@Override
	public String toString()
	{
		return "BookSales"
			+ " [book="   + this.book
			+ ", amount=" + this.amount
			+ "]";
	}

}
