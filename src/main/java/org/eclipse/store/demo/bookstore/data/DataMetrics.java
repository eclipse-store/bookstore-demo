
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

public class DataMetrics
{
	private final long bookCount   ;
	private final long countryCount;
	private final long shopCount   ;

	public DataMetrics(
		final long bookCount   ,
		final long countryCount,
		final long shopCount
	)
	{
		super();
		this.bookCount     = bookCount   ;
		this.countryCount  = countryCount;
		this.shopCount     = shopCount   ;
	}

	public long bookCount()
	{
		return this.bookCount;
	}

	public long countryCount()
	{
		return this.countryCount;
	}

	public long shopCount()
	{
		return this.shopCount;
	}

	@Override
	public String toString()
	{
		return this.bookCount   + " books, "
			+ this.shopCount    + " shops in "
			+ this.countryCount + " countries";
	}

}
