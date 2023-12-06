package org.eclipse.store.demo.bookstore.ui.views;

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

import org.eclipse.store.demo.bookstore.data.NamedWithAddress;

import com.vaadin.flow.component.grid.Grid;

/**
 * Abstract view to display {@link NamedWithAddress} entities in a {@link Grid}.
 *
 * @param <E> the entity type
 */
@SuppressWarnings("serial")
public abstract class ViewNamedWithAddress<E extends NamedWithAddress> extends ViewNamed<E>
{
	protected ViewNamedWithAddress()
	{
		super();
	}

	protected void addGridColumnsForAddress()
	{
		this.addGridColumnWithTextFilter   ("address1", e -> e.address().address()               );
		this.addGridColumnWithTextFilter   ("address2", e -> e.address().address2()              );
		this.addGridColumnWithTextFilter   ("zipcode",  e -> e.address().zipCode()               );
		this.addGridColumnWithDynamicFilter("city",     e -> e.address().city()                  );
		this.addGridColumnWithDynamicFilter("state",    e -> e.address().city().state()          );
		this.addGridColumnWithDynamicFilter("country",  e -> e.address().city().state().country());
	}
}
