
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

import static java.util.Objects.requireNonNull;

/**
 * City entity which holds a name and a {@link State}.
 * <p>
 * This type is immutable and therefor inherently thread safe.
 *
 */
public class City extends Named
{
	private final State state;
	
	/**
	 * Constructor to create a new {@link City} instance.
	 *
	 * @param name not empty
	 * @param state not <code>null</code>
	 */
	public City(
		final String name ,
		final State  state
	)
	{
		super(name);
		
		this.state = requireNonNull(state, () -> "State cannot be null");
	}
	
	/**
	 * Get the state.
	 *
	 * @return the state
	 */
	public State state()
	{
		return this.state;
	}

}
