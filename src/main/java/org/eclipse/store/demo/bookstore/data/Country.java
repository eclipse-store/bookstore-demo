
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

/**
 * Country entity which holds a name and an ISO 3166 2-letter country code.
 * <p>
 * This type is immutable and therefor inherently thread safe.
 *
 */
public class Country extends NamedWithCode
{
	public Country(
		final String name,
		final String code
	)
	{
		super(name, code);
	}

}
