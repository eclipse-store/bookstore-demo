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
import static org.eclipse.store.demo.bookstore.ui.data.DataBindingUtils.setterDummy;
import static org.eclipse.store.demo.bookstore.ui.data.DataBindingUtils.validator;

import java.util.function.Consumer;

import javax.money.MonetaryAmount;

import org.eclipse.store.demo.bookstore.BookStoreDemo;
import org.eclipse.store.demo.bookstore.data.Author;
import org.eclipse.store.demo.bookstore.data.Book;
import org.eclipse.store.demo.bookstore.data.Books;
import org.eclipse.store.demo.bookstore.data.Genre;
import org.eclipse.store.demo.bookstore.data.Language;
import org.eclipse.store.demo.bookstore.data.Publisher;
import org.eclipse.store.demo.bookstore.ui.data.DoubleToMonetaryAmountConverter;

import com.github.javafaker.Faker;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

/**
 * {@link Dialog} to create a new {@link Book}.
 *
 * @see #open(Consumer)
 *
 */
public class DialogBookCreate extends Dialog
{
	/**
	 * Opens a new dialog and triggers the {@code successHandler}
	 * when the user approved and validation was OK.
	 *
	 * @param successHandler
	 */
	public static void open(
		final Consumer<Book> successHandler
	)
	{
		new DialogBookCreate(
			notNull(successHandler)
		).open();
	}


	private DialogBookCreate(
		final Consumer<Book> successHandler
	)
	{
		super();

		final TextField isbn13Field = new TextField(this.getTranslation("isbn13"));
		isbn13Field.setPattern(Book.isbn13Pattern());
		isbn13Field.setValue(generateIsbn13());

		final TextField titleField = new TextField(this.getTranslation("title"));

		final ComboBox<Author> authorCombo = new ComboBoxNamed<>(
			this.getTranslation("author"),
			BookStoreDemo.getInstance().data().books().authors()
		);

		final ComboBox<Genre> genreCombo = new ComboBoxNamed<>(
			this.getTranslation("genre"),
			BookStoreDemo.getInstance().data().books().genres()
		);

		final ComboBox<Publisher> publisherCombo = new ComboBoxNamed<>(
			this.getTranslation("publisher"),
			BookStoreDemo.getInstance().data().books().publishers()
		);

		final ComboBox<Language> languageCombo = new ComboBoxNamed<>(
			this.getTranslation("language"),
			BookStoreDemo.getInstance().data().books().languages()
		);

		final NumberField purchasePriceField = new NumberField(this.getTranslation("purchasePrice"));

		final Binder<Book> binder = new Binder<>();
		binder.forField(isbn13Field)
			.asRequired()
			.withValidator(validator(this::validateIsbn13))
			.bind(Book::isbn13, setterDummy());
		binder.forField(titleField)
			.asRequired()
			.withValidator(validator(Book::validateTitle))
			.bind(Book::title, setterDummy());
		binder.forField(authorCombo)
			.asRequired()
			.withValidator(validator(Book::validateAuthor))
			.bind(Book::author, setterDummy());
		binder.forField(genreCombo)
			.asRequired()
			.withValidator(validator(Book::validateGenre))
			.bind(Book::genre, setterDummy());
		binder.forField(publisherCombo)
			.asRequired()
			.withValidator(validator(Book::validatePublisher))
			.bind(Book::publisher, setterDummy());
		binder.forField(languageCombo)
			.asRequired()
			.withValidator(validator(Book::validateLanguage))
			.bind(Book::language, setterDummy());
		binder.forField(purchasePriceField)
			.asRequired()
			.withConverter(new DoubleToMonetaryAmountConverter())
			.withValidator(validator(Book::validatePrice))
			.bind(Book::purchasePrice, setterDummy());

		final FormLayout form = new FormLayout(
			isbn13Field,
			titleField,
			authorCombo,
			genreCombo,
			publisherCombo,
			languageCombo,
			purchasePriceField
		);

		final Button okButton = new Button(this.getTranslation("ok"), event ->
		{
			if(binder.validate().isOk())
			{
				final MonetaryAmount purchasePrice = BookStoreDemo.money(purchasePriceField.getValue());
				final Book book = new Book(
					isbn13Field.getValue(),
					titleField.getValue(),
					authorCombo.getValue(),
					genreCombo.getValue(),
					publisherCombo.getValue(),
					languageCombo.getValue(),
					purchasePrice,
					BookStoreDemo.retailPrice(purchasePrice)
				);
				this.close();
				successHandler.accept(book);
			}
		});
		okButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		final Button cancelButton = new Button(this.getTranslation("cancel"), event -> this.close());
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		this.add(new VerticalLayout(
			new H3(this.getTranslation("createBook")),
			form,
			new HorizontalLayout(okButton, cancelButton)
		));
	}

	private static String generateIsbn13()
	{
		final Faker faker = Faker.instance();
		final Books books = BookStoreDemo.getInstance().data().books();
		String      isbn;
		while(books.ofIsbn13(isbn = faker.code().isbn13(true)) != null)
		{
			// empty loop
		}
		return isbn;
	}

	private String validateIsbn13(final String isbn13)
	{
		Book.validateIsbn13(isbn13);
		if(BookStoreDemo.getInstance().data().books().ofIsbn13(isbn13) != null)
		{
			throw new IllegalArgumentException(this.getTranslation("isbnAlreadyAssigned"));
		}
		return isbn13;
	}
}
