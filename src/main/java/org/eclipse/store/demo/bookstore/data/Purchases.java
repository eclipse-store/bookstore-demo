
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

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toList;
import static org.eclipse.store.demo.bookstore.util.CollectionUtils.maxKey;
import static org.eclipse.store.demo.bookstore.util.CollectionUtils.summingMonetaryAmount;
import static org.javamoney.moneta.function.MonetaryFunctions.summarizingMonetary;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.money.MonetaryAmount;

import org.eclipse.serializer.persistence.types.PersistenceStoring;
import org.eclipse.serializer.reference.Lazy;
import org.eclipse.store.demo.bookstore.BookStoreDemo;
import org.eclipse.store.demo.bookstore.util.concurrent.ReadWriteLocked;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;

import com.google.common.collect.Range;

import one.microstream.gigamap.Condition;
import one.microstream.gigamap.GigaMap;
import one.microstream.gigamap.GigaQuery;

/**
 * All purchases made by all customers in all stores.
 * <p>
 * This type is used to read and write the {@link Purchase}s and statistics thereof.
 * <p>
 * All operations on this type are thread safe.
 *
 * @see Data#purchases()
 * @see ReadWriteLocked
 */
public class Purchases extends ReadWriteLocked
{
	private final GigaMap<Purchase> map = GigaMap.<Purchase>Builder()
		.withBitmapIndex(Purchase.yearIndex)
		.withBitmapIndex(Purchase.foreignIndex)
		.withBitmapIndex(Purchase.shopIndex)
		.withBitmapIndex(Purchase.shopCountryIndex)
		.withBitmapIndex(Purchase.employeeIndex)
		.withBitmapIndex(Purchase.customerIndex)
		.build()
	;
	

	public Purchases()
	{
		super();
	}
	
	/**
	 * This method is used exclusively by the {@link RandomDataGenerator}
	 * and it's not published by the {@link Purchases} interface.
	 */
	Set<Customer> init(
		final List<Purchase>     purchases,
		final PersistenceStoring persister
	)
	{
		return this.write(() ->
		{
			this.map.addAll(purchases);
			persister.store(this.map);
			this.map.release();
						
			return purchases.stream().map(p -> p.customer())
				.distinct()
				.collect(Collectors.toSet());
		});
	}
	
	/**
	 * Adds a new purchase and stores it with the {@link BookStoreDemo}'s {@link EmbeddedStorageManager}.
	 * <p>
	 * This is a synonym for:<pre>this.add(purchase, BookStoreDemo.getInstance().storageManager())</pre>
	 *
	 * @param purchase the new purchase
	 */
	public void add(final Purchase purchase)
	{
		this.write(() ->
		{
			this.map.add(purchase);
			this.map.store();
		});
	}

	/**
	 * Adds a new purchase and stores it with the given persister.
	 *
	 * @param purchase the new purchase
	 * @param persister the persister to store it with
	 * @see #add(Purchase)
	 */
	public void add(
		final Purchase           purchase ,
		final PersistenceStoring persister
	)
	{
		this.write(() ->
		{
			this.map.add(purchase);
			persister.store(this.map);
		});
	}

	/**
	 * Gets the range of all years in which purchases were made.
	 *
	 * @return all years with revenue
	 */
	public Range<Integer> years()
	{
		return this.read(() -> {
			final IntSummaryStatistics summary = new IntSummaryStatistics();
			Purchase.yearIndex.resolveKeys(this.map).forEach(summary::accept);
			return Range.closed(summary.getMin(), summary.getMax());
		});
	}

	/**
	 * Clears all {@link Lazy} references regarding all purchases.
	 * This frees the used memory but you do not lose the persisted data. It is loaded again on demand.
	 *
	 * @see #clear(int)
	 */
	public void clear()
	{
		this.map.release();
	}

	/**
	 * Computes the best selling books for a specific year.
	 *
	 * @param year the year to filter by
	 * @return list of best selling books
	 */
	public List<BookSales> bestSellerList(final int year)
	{
		return this.read(() ->
			this.bestSellerList(
				this.map.query(Purchase.yearIndex.is(year)).stream()
			)
		);
	}

	/**
	 * Computes the best selling books for a specific year and country.
	 *
	 * @param year the year to filter by
	 * @param country the country to filter by
	 * @return list of best selling books
	 */
	public List<BookSales> bestSellerList(
		final int     year   ,
		final Country country
	)
	{
		return this.read(() ->
			this.bestSellerList(
				this.map.query(Purchase.yearIndex.is(year).and(Purchase.shopCountryIndex.is(country))).stream()
			)
		);
	}
	
	private List<BookSales> bestSellerList(final Stream<Purchase> purchases)
	{
		return purchases
			.flatMap(Purchase::items)
			.collect(
				groupingBy(
					PurchaseItem::book,
					summingInt(PurchaseItem::amount)
				)
			)
			.entrySet()
			.stream()
			.map(e -> new BookSales(e.getKey(), e.getValue()))
			.sorted()
			.collect(toList());
	}

	/**
	 * Counts all purchases which were made by customers in foreign countries.
	 *
	 * @param year the year to filter by
	 * @return the amount of computed purchases
	 */
	public long countPurchasesOfForeigners(final int year)
	{
		return this.read(() ->
			this.map.query(Purchase.yearIndex.is(year).and(Purchase.foreignIndex.isTrue())).count()
		);
	}

	/**
	 * Computes all purchases which were made by customers in foreign cities.
	 *
	 * @param year the year to filter by
	 * @return a list of purchases
	 */
	public List<Purchase> purchasesOfForeigners(final int year)
	{
		return this.read(() ->
			this.map.query(Purchase.yearIndex.is(year).and(Purchase.foreignIndex.isTrue())).toList()
		);
	}
	
	/**
	 * Counts all purchases which were made by customers in foreign cities.
	 *
	 * @param year the year to filter by
	 * @param country the country to filter by
	 * @return the amount of computed purchases
	 */
	public long countPurchasesOfForeigners(
		final int     year   ,
		final Country country
	)
	{
		return this.read(() ->
			this.map.query(Purchase.yearIndex.is(year)
				.and(Purchase.shopCountryIndex.is(country))
				.and(Purchase.foreignIndex.isTrue())).count()
		);
	}

	/**
	 * Computes all purchases which were made by customers in foreign cities.
	 *
	 * @param year the year to filter by
	 * @param country the country to filter by
	 * @return a list of purchases
	 */
	public List<Purchase> purchasesOfForeigners(
		final int     year   ,
		final Country country
	)
	{
		return this.read(() ->
			this.map.query(Purchase.yearIndex.is(year)
				.and(Purchase.shopCountryIndex.is(country))
				.and(Purchase.foreignIndex.isTrue())).toList()
		);
	}

	/**
	 * Computes the complete revenue of a specific shop in a whole year.
	 *
	 * @param shop the shop to filter by
	 * @param year the year to filter by
	 * @return complete revenue
	 */
	public MonetaryAmount revenueOfShopInYear(
		final Shop shop,
		final int  year
	)
	{
		return this.read(() ->
			this.map.query(Purchase.yearIndex.is(year).and(Purchase.shopIndex.is(shop)))
				.stream()
				.map(Purchase::total)
				.collect(summarizingMonetary(BookStoreDemo.CURRENCY_UNIT))
				.getSum()
		);
	}

	/**
	 * Computes the worldwide best performing employee in a specific year.
	 *
	 * @param year the year to filter by
	 * @return the employee which made the most revenue
	 */
	public Employee employeeOfTheYear(final int year)
	{
		return this.read(() ->
			bestPerformingEmployee(
				this.map.query(Purchase.yearIndex.is(year)).stream()
			)
		);
	}

	/**
	 * Computes the best performing employee in a specific year.
	 *
	 * @param year the year to filter by
	 * @param country the country to filter by
	 * @return the employee which made the most revenue
	 */
	public Employee employeeOfTheYear(
		final int     year   ,
		final Country country
	)
	{
		return this.read(() ->
			bestPerformingEmployee(
				this.map.query(Purchase.yearIndex.is(year).and(Purchase.shopCountryIndex.is(country))).stream()
			)
		);
	}

	private static Employee bestPerformingEmployee(final Stream<Purchase> purchases)
	{
		return maxKey(
			purchases.collect(
				groupingBy(
					Purchase::employee,
					summingMonetaryAmount(
						BookStoreDemo.CURRENCY_UNIT,
						Purchase::total
					)
				)
			)
		);
	}
	
	public <R> R compute(
		final Condition<Purchase>           condition,
		final int                           offset,
		final int                           limit,
		final Function<Stream<Purchase>, R> function
	)
	{
		return this.read(() ->
		{
			GigaQuery<Purchase> query = this.map.query();
			if(condition != null)
			{
				query = query.and(condition);
			}
			return function.apply(query.toList(offset, limit).stream());
		});
	}

}
