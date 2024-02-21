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

import org.eclipse.store.demo.bookstore.BookStoreDemo;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * Landing page of the web interface.
 *
 */
@Route(value = "", layout = RootLayout.class)
public class ViewMain extends VerticalLayout
{
	public ViewMain(
		final BookStoreDemo bookStoreDemo
	)
	{
		super();

		final Image image = new Image("frontend/images/bookstoredemo.svg", this.getTranslation("app.title"));
		image.setWidth("80%");
		image.setMaxWidth("800px");
		image.setHeight(null);

		this.add(image);
		this.setSizeFull();
		this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		this.setJustifyContentMode(JustifyContentMode.CENTER);
	}

}
