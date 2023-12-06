package org.eclipse.store.demo.bookstore.graphql;

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

import java.util.Collections;
import java.util.List;

import org.eclipse.store.demo.bookstore.BookStoreDemo;
import org.eclipse.store.demo.bookstore.data.Book;
import org.eclipse.store.demo.bookstore.data.BookSales;
import org.eclipse.store.demo.bookstore.data.Country;
import org.eclipse.store.demo.bookstore.data.Employee;
import org.eclipse.store.demo.bookstore.data.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class BookStoreQueryResolver implements GraphQLQueryResolver
{
	@Autowired
	private BookStoreDemo bookStoreDemo;

	public BookStoreQueryResolver()
	{
		super();
	}

	private Country countryByCode(final String countryCode)
	{
		return this.bookStoreDemo.data().shops().compute(shops ->
			shops
				.map(s -> s.address().city().state().country())
				.filter(c -> c.code().equalsIgnoreCase(countryCode))
				.findAny()
				.orElse(null)
		);
	}

	public List<Book> booksByTitle(final String title)
	{
		return this.bookStoreDemo.data().books().searchByTitle(title);
	}

	public Employee employeeOfTheYear(final int year)
	{
		return this.bookStoreDemo.data().purchases().employeeOfTheYear(year);
	}

	public List<BookSales> bestSellerList(final int year)
	{
		return this.bookStoreDemo.data().purchases().bestSellerList(year);
	}

	public List<BookSales> bestSellerListByCountry(final int year, final String countryCode)
	{
		final Country country = this.countryByCode(countryCode);
		return country == null
			? Collections.emptyList()
			: this.bookStoreDemo.data().purchases().bestSellerList(year, country)
		;
	}

	public List<Purchase> purchasesOfForeigners(final int year)
	{
		return this.bookStoreDemo.data().purchases().purchasesOfForeigners(year);
	}

	public List<Purchase> purchasesOfForeignersByCountry(final int year, final String countryCode)
	{
		final Country country = this.countryByCode(countryCode);
		return country == null
			? Collections.emptyList()
			: this.bookStoreDemo.data().purchases().purchasesOfForeigners(year, country)
		;
	}

}
