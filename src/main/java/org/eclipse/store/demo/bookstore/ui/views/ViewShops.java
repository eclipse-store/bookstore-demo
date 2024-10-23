package org.eclipse.store.demo.bookstore.ui.views;

import java.util.stream.Stream;

import org.eclipse.store.demo.bookstore.BookStoreDemo;
import org.eclipse.store.demo.bookstore.data.Shop;
import org.eclipse.store.demo.bookstore.data.Shops;
import org.vaadin.lineawesome.LineAwesomeIcon;

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

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.router.Route;

import one.microstream.gigamap.Condition;

/**
 * View to display {@link Shops}.
 *
 */
@Route(value = "shops", layout = RootLayout.class)
public class ViewShops extends ViewNamedWithAddress<Shop>
{
	public ViewShops()
	{
		super();
	}

	@Override
	protected void createUI()
	{
		this.addGridColumnForName();
		this.addGridColumnsForAddress();

		final Button showPurchasesButton = new Button(
			this.getTranslation("showPurchases"),
			LineAwesomeIcon.SHOPPING_CART_SOLID.create(),
			event -> this.showPurchases(this.getSelectedEntity())
		);

		showPurchasesButton.setEnabled(false);
		this.grid.addSelectionListener(event -> {
			final boolean b = event.getFirstSelectedItem().isPresent();
			showPurchasesButton.setEnabled(b);
		});

		this.add(showPurchasesButton);
	}

	private void showPurchases(final Shop shop)
	{
		this.getUI().get().navigate(ViewPurchases.class).get().filterBy(shop);
	}

	@Override
	public <R> R compute(
		final Condition<Shop>                       condition,
		final int                                   offset,
		final int                                   limit,
		final SerializableFunction<Stream<Shop>, R> function
	)
	{
		return BookStoreDemo.getInstance().data().shops().compute(condition, offset, limit, function);
	}

}
