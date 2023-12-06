
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

import java.util.Locale;


/**
 * Language entity which holds a {@link Locale}.
 * <p>
 * This type is immutable and therefor inherently thread safe.
 *
 */
public class Language extends Named
{
	private final Locale locale;
	
	/**
	 * Constructor to create a new {@link Language} instance.
	 *
	 * @param locale not <code>null</code>
	 */
	public Language(final Locale locale)
	{
		super(locale.getDisplayLanguage());
		
		this.locale = locale;
	}
	
	/**
	 * Get the locale.
	 *
	 * @return the locale
	 */
	public Locale locale()
	{
		return this.locale;
	}

}
