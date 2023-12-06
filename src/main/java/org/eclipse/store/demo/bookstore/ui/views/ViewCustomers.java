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

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.router.Route;

import org.eclipse.store.demo.bookstore.BookStoreDemo;
import org.eclipse.store.demo.bookstore.data.Customer;
import org.eclipse.store.demo.bookstore.data.Customers;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.stream.Stream;

/**
 * View to display {@link Customers}.
 *
 */
@Route(value = "customers", layout = RootLayout.class)
@SuppressWarnings("serial")
public class ViewCustomers extends ViewNamedWithAddress<Customer>
{
	public ViewCustomers()
	{
		super();
	}

	@Override
	protected void createUI()
	{
		this.addGridColumn("id", Customer::customerId);
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

		this.add(new HorizontalLayout(showPurchasesButton));
	}

	@Override
	public <R> R compute(SerializableFunction<Stream<Customer>, R> function) {
		return BookStoreDemo.getInstance().data().customers().compute(function);
	}

	private void showPurchases(final Customer customer)
	{
		getUI().get().navigate(ViewPurchases.class).get().filterBy(customer);
	}

}
