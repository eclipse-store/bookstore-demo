
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
	private final int bookCount   ;
	private final int countryCount;
	private final int shopCount   ;

	public DataMetrics(
		final int bookCount   ,
		final int countryCount,
		final int shopCount
	)
	{
		super();
		this.bookCount     = bookCount   ;
		this.countryCount  = countryCount;
		this.shopCount     = shopCount   ;
	}

	public int bookCount()
	{
		return this.bookCount;
	}

	public int countryCount()
	{
		return this.countryCount;
	}

	public int shopCount()
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
