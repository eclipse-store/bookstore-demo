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

import static org.eclipse.serializer.util.X.notNull;

import java.util.function.Function;

import org.eclipse.store.demo.bookstore.data.Named;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.function.SerializablePredicate;

/**
 * Filter {@link ComboBox} for {@link Named} entities.
 *
 * @param <E> the entity type
 * @param <F> the field type
 */
public class FilterComboBox<E, F extends Named> extends ComboBoxNamed<F> implements FilterField<E, F>
{
	private final Function<F, SerializablePredicate<E>> filterFactory;

	public FilterComboBox(
		final Function<F, SerializablePredicate<E>> filterFactory
	)
	{
		super();

		this.filterFactory = notNull(filterFactory);

		this.setPlaceholder(this.getTranslation("filter"));
		this.setClearButtonVisible(true);
	}

	@Override
	public SerializablePredicate<E> filter(final SerializablePredicate<E> filter)
	{
		final F value = this.getValue();
		return value != null
			? filter.and(this.filterFactory.apply(value))
			: filter
		;
	}

	@Override
	public void updateOptions() {
		this.getDataProvider().refreshAll();
	}
}
