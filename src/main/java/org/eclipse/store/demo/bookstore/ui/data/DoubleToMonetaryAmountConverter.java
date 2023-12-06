package org.eclipse.store.demo.bookstore.ui.data;

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

import javax.money.MonetaryAmount;

import org.eclipse.store.demo.bookstore.BookStoreDemo;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

/**
 * A {@link Converter} that converts from {@link Double} to {@link MonetaryAmount} and back.
 *
 */
public class DoubleToMonetaryAmountConverter implements Converter<Double, MonetaryAmount>
{
	public DoubleToMonetaryAmountConverter()
	{
		super();
	}

	@Override
	public Result<MonetaryAmount> convertToModel(
		final Double       value  ,
		final ValueContext context
	)
	{
		return Result.ok(value != null
			? BookStoreDemo.money(value)
			: null
		);
	}

	@Override
	public Double convertToPresentation(
		final MonetaryAmount value  ,
		final ValueContext   context
	)
	{
		return value != null
			? value.getNumber().doubleValue()
			: null
		;
	}

}
