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

import org.eclipse.store.demo.bookstore.BookStoreDemo;
import org.eclipse.store.demo.bookstore.data.Book;
import org.eclipse.store.demo.bookstore.data.BookSales;
import org.eclipse.store.demo.bookstore.data.Country;
import org.eclipse.store.demo.bookstore.data.Employee;
import org.eclipse.store.demo.bookstore.data.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;

@Controller
public class GraphQLEndpoint {
	@Autowired
	private BookStoreDemo bookStoreDemo;

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

	@QueryMapping
	public List<Book> booksByName(@Argument String name)
	{
		return this.bookStoreDemo.data().books().searchByTitle(name);
	}

	@QueryMapping
	public Employee employeeOfTheYear(@Argument int year)
	{
		return this.bookStoreDemo.data().purchases().employeeOfTheYear(year);
	}

	@QueryMapping
	public List<BookSales> bestSellerList(@Argument int year)
	{
		return this.bookStoreDemo.data().purchases().bestSellerList(year);
	}

	@QueryMapping
	public List<BookSales> bestSellerListByCountry(@Argument int year, @Argument String countryCode)
	{
		final Country country = this.countryByCode(countryCode);
		return country == null
			? Collections.emptyList()
			: this.bookStoreDemo.data().purchases().bestSellerList(year, country)
		;
	}

	@QueryMapping
	public List<Purchase> purchasesOfForeigners(@Argument int year)
	{
		return this.bookStoreDemo.data().purchases().purchasesOfForeigners(year);
	}

	@QueryMapping
	public List<Purchase> purchasesOfForeignersByCountry(@Argument int year, @Argument String countryCode)
	{
		final Country country = this.countryByCode(countryCode);
		return country == null
			? Collections.emptyList()
			: this.bookStoreDemo.data().purchases().purchasesOfForeigners(year, country)
		;
	}

}
