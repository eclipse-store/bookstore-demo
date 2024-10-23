package org.eclipse.store.demo.bookstore.ui.views;

import java.util.stream.Stream;

import org.eclipse.store.demo.bookstore.BookStoreDemo;
import org.eclipse.store.demo.bookstore.data.Book;
import org.eclipse.store.demo.bookstore.data.Books;
import org.eclipse.store.demo.bookstore.data.Named;

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
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.router.Route;

import one.microstream.gigamap.Condition;

/**
 * View to display and modify {@link Books}.
 *
 */
@Route(value = "books", layout = RootLayout.class)
public class ViewBooks extends ViewEntity<Book>
{
	public ViewBooks()
	{
		super();
	}

	@Override
	protected void createUI()
	{
		this.addGridColumnWithTextFilter   ("title"    , Book::title    , Named.nameIndex::containsIgnoreCase);
		this.addGridColumnWithDynamicFilter("author"   , Book::author   , Book.authorIndex::is);
		this.addGridColumnWithDynamicFilter("genre"    , Book::genre    , Book.genreIndex::is);
		this.addGridColumnWithDynamicFilter("publisher", Book::publisher, Book.publisherIndex::is);
		this.addGridColumnWithDynamicFilter("language" , Book::language , Book.languageIndex::is);
		this.addGridColumnWithTextFilter   ("isbn13"   , Book::isbn13   , Book.isbn13Index::containsIgnoreCase);

		final Button createBookButton = new Button(
			this.getTranslation("createBook"),
			VaadinIcon.PLUS_CIRCLE.create(),
			event -> this.openCreateBookDialog()
		);

		this.add(createBookButton);
	}

	private void openCreateBookDialog()
	{
		DialogBookCreate.open(book ->
		{
			BookStoreDemo.getInstance().data().books().add(book);
			this.listEntities();
		});
	}

	@Override
	public <R> R compute(
		final Condition<Book>                       condition,
		final int                                   offset,
		final int                                   limit,
		final SerializableFunction<Stream<Book>, R> function
	)
	{
		return BookStoreDemo.getInstance().data().books().compute(condition, offset, limit, function);
	}
}
