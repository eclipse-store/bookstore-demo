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

import java.util.Collection;

import org.eclipse.store.demo.bookstore.data.Named;

import com.vaadin.flow.component.combobox.ComboBox;

/**
 * {@link ComboBox} for {@link Named} entities.
 *
 * @param <T> the entity type
 */
public class ComboBoxNamed<T extends Named> extends ComboBox<T>
{
	public ComboBoxNamed()
	{
		super();
		this.init();
	}

	public ComboBoxNamed(final Collection<T> items)
	{
		super();
		this.setItems(items);
		this.init();
	}

	public ComboBoxNamed(final String label)
	{
		super(label);
		this.init();
	}

	public ComboBoxNamed(final String label, final Collection<T> items)
	{
		super(label, items);
		this.init();
	}

	private void init()
	{
		this.setItemLabelGenerator(Named::name);
	}

	public ComboBoxNamed<T> withItems(final Collection<T> items)
	{
		this.setItems(items);
		return this;
	}

}
