
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
import static org.eclipse.store.demo.bookstore.util.ValidationUtils.requireNonEmpty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.money.MonetaryAmount;

import org.eclipse.store.gigamap.types.Indexer;
import org.eclipse.store.gigamap.types.IndexerBoolean;
import org.eclipse.store.gigamap.types.IndexerInteger;

/**
 * Purchase entity which holds a {@link Shop}, {@link Employee},
 * {@link Customer}, timestamp and {@link PurchaseItem}s.
 * <p>
 * This type is immutable and therefor inherently thread safe.
 *
 */
public class Purchase
{
	public static final IndexerInteger<Purchase> yearIndex = new IndexerInteger.Abstract<>()
	{
		@Override
		public Integer getInteger(final Purchase entity)
		{
			return entity.timestamp().getYear();
		}
	};
	
	public static final IndexerBoolean<Purchase> foreignIndex = new IndexerBoolean.Abstract<>()
	{
		@Override
		public Boolean getBoolean(final Purchase entity)
		{
			return entity.customer().address().city() != entity.shop().address().city();
		}
	};
	
	public static final Indexer.Abstract<Purchase, Shop> shopIndex = new Indexer.Abstract<>()
	{
		@Override
		public Class<Shop> keyType()
		{
			return Shop.class;
		}
		
		@Override
		public Shop index(final Purchase entity)
		{
			return entity.shop();
		}
	};
	
	public static final Indexer.Abstract<Purchase, Country> shopCountryIndex = new Indexer.Abstract<>()
	{
		@Override
		public Class<Country> keyType()
		{
			return Country.class;
		}
		
		@Override
		public Country index(final Purchase entity)
		{
			return entity.shop().address().city().state().country();
		}
	};
	
	public static final Indexer.Abstract<Purchase, Employee> employeeIndex = new Indexer.Abstract<>()
	{
		@Override
		public Class<Employee> keyType()
		{
			return Employee.class;
		}
		
		@Override
		public Employee index(final Purchase entity)
		{
			return entity.employee();
		}
	};
	
	public static final Indexer.Abstract<Purchase, Customer> customerIndex = new Indexer.Abstract<>()
	{
		@Override
		public Class<Customer> keyType()
		{
			return Customer.class;
		}
		
		@Override
		public Customer index(final Purchase entity)
		{
			return entity.customer();
		}
	};
	
	
	private final Shop               shop     ;
	private final Employee           employee ;
	private final Customer           customer ;
	private final LocalDateTime      timestamp;
	private final List<PurchaseItem> items    ;
	private transient MonetaryAmount total    ;

	/**
	 * Constructor to create a new {@link Purchase} instance.
	 *
	 * @param shop not <code>null</code>
	 * @param employee not <code>null</code>
	 * @param customer not <code>null</code>
	 * @param timestamp not <code>null</code>
	 * @param items not empty
	 */
	public Purchase(
		final Shop               shop     ,
		final Employee           employee ,
		final Customer           customer ,
		final LocalDateTime      timestamp,
		final List<PurchaseItem> items
	)
	{
		super();
		this.shop      = notNull(shop);
		this.employee  = notNull(employee);
		this.customer  = notNull(customer);
		this.timestamp = notNull(timestamp);
		this.items     = new ArrayList<>(requireNonEmpty(items, () -> "at least one item required in purchase"));
	}

	/**
	 * Get the shop the purchase was made in
	 *
	 * @return the shop
	 */
	public Shop shop()
	{
		return this.shop;
	}

	/**
	 * Get the employee who sold
	 *
	 * @return the employee
	 */
	public Employee employee()
	{
		return this.employee;
	}

	/**
	 * Get the customer who made the purchase
	 *
	 * @return the customer
	 */
	public Customer customer()
	{
		return this.customer;
	}

	/**
	 * The timestamp the purchase was made at
	 *
	 * @return the timestamp
	 */
	public LocalDateTime timestamp()
	{
		return this.timestamp;
	}

	/**
	 * Get all {@link PurchaseItem}s of this purchase
	 *
	 * @return a {@link Stream} of {@link PurchaseItem}s
	 */
	public Stream<PurchaseItem> items()
	{
		return this.items.stream();
	}
	
	/**
	 * Get all {@link PurchaseItem}s of this purchase
	 *
	 * @return a {@link List} of {@link PurchaseItem}s
	 */
	public List<PurchaseItem> itemsList()
	{
		return new ArrayList<>(this.items);
	}

	public int itemCount()
	{
		return this.items.size();
	}

	/**
	 * Computes the total of this purchase (sum of {@link PurchaseItem#itemTotal()})
	 *
	 * @return the total amount
	 */
	public MonetaryAmount total()
	{
		if(this.total  == null)
		{
			MonetaryAmount total = null;
			for(final PurchaseItem item : this.items)
			{
				total = total == null
					? item.itemTotal()
					: total.add(item.itemTotal());
			}
			this.total = total;
		}
		return this.total;
	}

}
