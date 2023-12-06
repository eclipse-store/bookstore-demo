
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
 * Feature type for all named entities with a code.
 *
 */
public abstract class NamedWithCode extends Named
{
	private final String code;

	protected NamedWithCode(
		final String name,
		final String code
	)
	{
		super(name);
		
		this.code = code;
	}

	/**
	 * Get the code.
	 *
	 * @return the code
	 */
	public String code()
	{
		return this.code;
	}

	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + " [" + this.name() + " - " + this.code + "]";
	}

}
