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

import org.eclipse.store.demo.bookstore.data.Named;

import com.vaadin.flow.component.grid.Grid;

/**
 * Abstract view to display {@link Named} entities in a {@link Grid}.
 *
 * @param <E> the entity type
 */
@SuppressWarnings("serial")
public abstract class ViewNamed<E extends Named> extends ViewEntity<E>
{
	protected ViewNamed()
	{
		super();
	}

	protected void addGridColumnForName()
	{
		this.addGridColumnWithTextFilter("name", Named::name);
	}
}
