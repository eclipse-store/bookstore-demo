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

import com.vaadin.flow.function.SerializablePredicate;

/**
 * A UI component which is used for filtering.
 *
 * @param <E> the entity type
 * @param <F> the field type
 */
public interface FilterField<E, F>
{
	public SerializablePredicate<E> filter(SerializablePredicate<E> filter);

	public void updateOptions();
}
