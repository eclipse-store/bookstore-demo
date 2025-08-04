
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

import static org.eclipse.store.demo.bookstore.util.ValidationUtils.requirePositive;

import org.eclipse.store.gigamap.types.IndexerInteger;

/**
 * Customer entity which holds a customer id, name and an {@link Address}.
 * <p>
 * This type is immutable and therefor inherently thread safe.
 *
 */
public class Customer extends NamedWithAddress
{
	public static final IndexerInteger<Customer> idIndex = new IndexerInteger.Abstract<>()
	{
		@Override
		public Integer getInteger(final Customer entity)
		{
			return entity.customerId();
		}
	};


	private final int customerId;
	
	/**
	 * Constructor method to create a new {@link Customer} instance.
	 *
	 * @param customerId positive customer id
	 * @param name not empty
	 * @param address not <code>null</code>
	 */
	public Customer(
		final int     customerId,
		final String  name      ,
		final Address address
	)
	{
		super(name, address);
		
		this.customerId = requirePositive(customerId, () -> "Customer id must be positive");
	}
	
	/**
	 * Get the customer id
	 *
	 * @return the customer id
	 */
	public int customerId()
	{
		return this.customerId;
	}
	
}
