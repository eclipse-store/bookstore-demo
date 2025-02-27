
package org.eclipse.store.demo.bookstore.data;

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

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.QueryBuilder;
import org.eclipse.serializer.concurrency.LockScope;
import org.eclipse.serializer.persistence.types.PersistenceStoring;
import org.eclipse.store.demo.bookstore.BookStoreDemo;
import org.eclipse.store.demo.bookstore.data.Index.DocumentPopulator;
import org.eclipse.store.demo.bookstore.data.Index.EntityMatcher;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;

/**
 * Range of all books sold by this company.
 * <p>
 * This type is used to read and write the {@link Book}s, their {@link Author}s, {@link Genre}s,
 * {@link Publisher}s and {@link Language}s.
 * <p>
 * All operations on this type are thread safe.
 *
 * @see Data#books()
 * @see LockScope
 */
public class Books extends LockScope
{
	/*
	 * Multiple maps holding references to the books, for a faster lookup.
	 */
	private final Map<String, Book>          isbn13ToBook     = new HashMap<>();
	private final Map<Author, List<Book>>    authorToBooks    = new HashMap<>();
	private final Map<Genre, List<Book>>     genreToBooks     = new HashMap<>();
	private final Map<Publisher, List<Book>> publisherToBooks = new HashMap<>();
	private final Map<Language, List<Book>>  languageToBooks  = new HashMap<>();
	/**
	 * Index used by {@link #searchByTitle(String)}.
	 */
	/*
	 * Transient means it is not persisted by EclipseStore, but created on demand.
	 */
	private transient volatile Index<Book>   index;
	
	public Books()
	{
		super();
	}
	
	
	/**
	 * Adds a new book and stores it with the {@link BookStoreDemo}'s {@link EmbeddedStorageManager}.
	 * <p>
	 * This is a synonym for:<pre>this.add(book, BookStoreDemo.getInstance().storageManager())</pre>
	 *
	 * @param book the new book
	 */
	public void add(final Book book)
	{
		this.add(book, BookStoreDemo.getInstance().storageManager());
	}

	/**
	 * Adds a new book and stores it with the given persister.
	 *
	 * @param book the new book
	 * @param persister the persister to store it with
	 * @see #add(Book)
	 */
	public void add(
		final Book               book     ,
		final PersistenceStoring persister
	)
	{
		this.write(() ->
		{
			this.ensureIndex().add(book);
			this.addToCollections(book);
			this.storeCollections(persister);
		});
	}

	/**
	 * Adds a range of new books and stores it with the {@link BookStoreDemo}'s {@link EmbeddedStorageManager}.
	 * <p>
	 * This is a synonym for:<pre>this.addAll(books, BookStoreDemo.getInstance().storageManager())</pre>
	 *
	 * @param books the new books
	 */
	public void addAll(final Collection<? extends Book> books)
	{
		this.addAll(books, BookStoreDemo.getInstance().storageManager());
	}

	/**
	 * Adds a range of new books and stores it with the given persister.
	 *
	 * @param books the new books
	 * @param persister the persister to store them with
	 * @see #addAll(Collection)
	 */
	public void addAll(
		final Collection<? extends Book>          books    ,
		final PersistenceStoring                  persister
	)
	{
		this.write(() ->
		{
			this.ensureIndex().addAll(books);
			books.forEach(this::addToCollections);
			this.storeCollections(persister);
		});
	}

	/**
	 * Stores all collections of this implementation with the given persister.
	 *
	 * @param persister the EclipseStore persister used to store the objects
	 */
	private void storeCollections(final PersistenceStoring persister)
	{
		persister.storeAll(
			this.isbn13ToBook    ,
			this.authorToBooks   ,
			this.genreToBooks    ,
			this.publisherToBooks,
			this.languageToBooks
		);
	}
	
	/**
	 * Adds a book to all collections used by this implementation.
	 *
	 * @param book the book to add
	 */
	private void addToCollections(final Book book)
	{
		this.isbn13ToBook.put(book.isbn13(), book);
		this.addToMap(this.authorToBooks   , book.author()   , book);
		this.addToMap(this.genreToBooks    , book.genre()    , book);
		this.addToMap(this.publisherToBooks, book.publisher(), book);
		this.addToMap(this.languageToBooks , book.language() , book);
	}

	/**
	 * Adds a book to a map with a list as values.
	 * If no list is present for the given key, it will be created.
	 *
	 * @param <K> the key type
	 * @param map the collection
	 * @param key the key
	 * @param book the book to add
	 */
	private <K> void addToMap(
		final Map<K, List<Book>> map ,
		final K                  key ,
		final Book               book
	)
	{
		map.computeIfAbsent(
			key,
			k -> new ArrayList<>(1024)
		)
		.add(book);
	}

	/**
	 * Gets all books as a sorted {@link List}.
	 * Modifications to the returned list are not reflected to the backed data.
	 *
	 * @return all books
	 */
	public List<Book> all()
	{
		return this.read(() ->
			this.isbn13ToBook.values().stream()
				.sorted()
				.collect(toList())
		);
	}

	/**
	 * Gets all authors as a sorted {@link List}.
	 * Modifications to the returned list are not reflected to the backed data.
	 *
	 * @return all authors
	 */
	public List<Author> authors()
	{
		return this.read(() ->
			this.authorToBooks.keySet().stream()
				.sorted()
				.collect(toList())
		);
	}

	/**
	 * Gets all genres as a sorted {@link List}.
	 * Modifications to the returned list are not reflected to the backed data.
	 *
	 * @return all genres
	 */
	public List<Genre> genres()
	{
		return this.read(() ->
			this.genreToBooks.keySet().stream()
				.sorted()
				.collect(toList())
		);
	}

	/**
	 * Gets all publishers as a sorted {@link List}.
	 * Modifications to the returned list are not reflected to the backed data.
	 *
	 * @return all publishers
	 */
	public List<Publisher> publishers()
	{
		return this.read(() ->
			this.publisherToBooks.keySet().stream()
				.sorted()
				.collect(toList())
		);
	}

	/**
	 * Gets all languages as a sorted {@link List}.
	 * Modifications to the returned list are not reflected to the backed data.
	 *
	 * @return all languages
	 */
	public List<Language> languages()
	{
		return this.read(() ->
			this.languageToBooks.keySet().stream()
				.sorted()
				.collect(toList())
		);
	}

	/**
	 * Gets the total amount of all books.
	 *
	 * @return the amount of books
	 */
	public int bookCount()
	{
		return this.read(
			this.isbn13ToBook::size
		);
	}

	/**
	 * Executes a function with a {@link Stream} of {@link Book}s and returns the computed value.
	 * <p>
	 * This example counts all authors which have written a book with a title starting with 'The':
	 * <pre>
	 * long bookCount = compute(books ->
	 *    books.filter(book -> book.title().startsWith("The"))
	 *        .map(Book::author)
	 *        .distinct()
	 *        .count()
	 * );
	 * </pre>
	 *
	 * @param <T> the return type
	 * @param streamFunction computing function
	 * @return the computed result
	 */
	public <T> T compute(final Function<Stream<Book>, T> streamFunction)
	{
		return this.read(() ->
			streamFunction.apply(this.isbn13ToBook.values().stream())
		);
	}

	/**
	 * Executes a function with a pre-filtered {@link Stream} of {@link Book}s and returns the computed value.
	 *
	 * @param <T> the return type
	 * @param author author to filter by
	 * @param streamFunction computing function
	 * @return the computed result
	 * @see #compute(Function)
	 */
	public <T> T computeByAuthor(
		final Author                    author        ,
		final Function<Stream<Book>, T> streamFunction
	)
	{
		return this.read(() ->
		{
			final List<Book> list = this.authorToBooks.get(author);
			return streamFunction.apply(
				list != null
					? list.stream()
					: Stream.empty()
			);
		});
	}

	/**
	 * Executes a function with a pre-filtered {@link Stream} of {@link Book}s and returns the computed value.
	 *
	 * @param <T> the return type
	 * @param genre genre to filter by
	 * @param streamFunction computing function
	 * @return the computed result
	 * @see #compute(Function)
	 */
	public  <T> T computeByGenre(
		final Genre                     genre         ,
		final Function<Stream<Book>, T> streamFunction
	)
	{
		return this.read(() ->
		{
			final List<Book> list = this.genreToBooks.get(genre);
			return streamFunction.apply(
				list != null
					? list.stream()
					: Stream.empty()
			);
		});
	}

	/**
	 * Executes a function with a pre-filtered {@link Stream} of {@link Book}s and returns the computed value.
	 *
	 * @param <T> the return type
	 * @param publisher publisher to filter by
	 * @param streamFunction computing function
	 * @return the computed result
	 * @see #compute(Function)
	 */
	public <T> T computeByPublisher(
		final Publisher                 publisher     ,
		final Function<Stream<Book>, T> streamFunction
	)
	{
		return this.read(() ->
		{
			final List<Book> list = this.publisherToBooks.get(publisher);
			return streamFunction.apply(
				list != null
					? list.stream()
					: Stream.empty()
			);
		});
	}

	/**
	 * Executes a function with a pre-filtered {@link Stream} of {@link Book}s and returns the computed value.
	 *
	 * @param <T> the return type
	 * @param language language to filter by
	 * @param streamFunction computing function
	 * @return the computed result
	 * @see #compute(Function)
	 */
	public <T> T computeByLanguage(
		final Language                  language      ,
		final Function<Stream<Book>, T> streamFunction
	)
	{
		return this.read(() ->
		{
			final List<Book> list = this.languageToBooks.get(language);
			return streamFunction.apply(
				list != null
					? list.stream()
					: Stream.empty()
			);
		});
	}

	/**
	 * Gets the book with a specific ISBN or <code>null</code> if none was found.
	 *
	 * @param isbn13 the ISBN to search by
	 * @return the matching book or <code>null</code>
	 */
	public Book ofIsbn13(final String isbn13)
	{
		return this.read(() ->
			this.isbn13ToBook.get(isbn13)
		);
	}

	/**
	 * Executes a function with a {@link Stream} of {@link Genre}s and returns the computed value.
	 *
	 * @param <T> the return type
	 * @param streamFunction computing function
	 * @return the computed result
	 */
	public <T> T computeGenres(final Function<Stream<Genre>, T> streamFunction)
	{
		return this.read(() ->
			streamFunction.apply(this.genreToBooks.keySet().stream())
		);
	}

	/**
	 * Executes a function with a {@link Stream} of {@link Author}s and returns the computed value.
	 *
	 * @param <T> the return type
	 * @param streamFunction computing function
	 * @return the computed result
	 */
	public <T> T computeAuthors(final Function<Stream<Author>, T> streamFunction)
	{
		return this.read(() ->
			streamFunction.apply(this.authorToBooks.keySet().stream())
		);
	}

	/**
	 * Executes a function with a {@link Stream} of {@link Publisher}s and returns the computed value.
	 *
	 * @param <T> the return type
	 * @param streamFunction computing function
	 * @return the computed result
	 */
	public <T> T computePublishers(final Function<Stream<Publisher>, T> streamFunction)
	{
		return this.read(() ->
			streamFunction.apply(this.publisherToBooks.keySet().stream())
		);
	}

	/**
	 * Executes a function with a {@link Stream} of {@link Language}s and returns the computed value.
	 *
	 * @param <T> the return type
	 * @param streamFunction computing function
	 * @return the computed result
	 */
	public <T> T computeLanguages(final Function<Stream<Language>, T> streamFunction)
	{
		return this.read(() ->
			streamFunction.apply(this.languageToBooks.keySet().stream())
		);
	}

	/**
	 * Searches all books by title with a given query.
	 * The query can contain following wildcard characters:<br>
	 * * placeholder for multiple characters<br>
	 * ? placeholder for a single character
	 *
	 * @param queryText the search query
	 * @return a list of books matching the query, or an empty list
	 */
	public List<Book> searchByTitle(final String queryText)
	{
		final Index<Book>  index        = this.ensureIndex();
		final QueryBuilder queryBuilder = index.createQueryBuilder();
		final Query        query        = queryBuilder.createPhraseQuery("title", queryText);
		return index.search(query, Integer.MAX_VALUE);
	}

	/**
	 * Gets all books written by a specific author.
	 *
	 * @param author the author to search for
	 * @return a list of books
	 */
	public List<Book> allByAuthor(final Author author)
	{
		return this.computeByAuthor(
			author,
			books -> books.collect(toList())
		);
	}

	/**
	 * Gets all books with a specific genre.
	 *
	 * @param genre the genre to search for
	 * @return a list of books
	 */
	public List<Book> allByGenre(final Genre genre)
	{
		return this.computeByGenre(
			genre,
			books -> books.collect(toList())
		);
	}

	/**
	 * Gets all books of a specific publisher.
	 *
	 * @param publisher the publisher to search for
	 * @return a list of books
	 */
	public List<Book> allByPublisher(final Publisher publisher)
	{
		return this.computeByPublisher(
			publisher,
			books -> books.collect(toList())
		);
	}

	/**
	 * Gets all books which are published in a specific language.
	 *
	 * @param language the language to search for
	 * @return a list of books
	 */
	public List<Book> allByLanguage(final Language language)
	{
		return this.computeByLanguage(
			language,
			books -> books.collect(toList())
		);
	}

	/**
	 * Lazy initializes the full text search index.
	 */
	private Index<Book> ensureIndex()
	{
		/*
		 * Double-checked locking to reduce the overhead of acquiring a lock
		 * by testing the locking criterion.
		 * The field (this.index) has to be volatile.
		 */
		Index<Book> index = this.index;
		if(index == null)
		{
			synchronized(this)
			{
				if((index = this.index) == null)
				{
					index = this.index = this.createIndex();
				}
			}
		}
		return index;
	}

	/**
	 * Creates a Lucene index used for full text search.
	 */
	private Index<Book> createIndex()
	{
		final DocumentPopulator<Book> documentPopulator = (document, book) -> {
			document.add(new StringField("isbn13", book.isbn13(), Store.YES));
			document.add(new TextField("title", book.title(), Store.YES));
			document.add(new TextField("author", book.author().name(), Store.YES));
			document.add(new TextField("genre", book.genre().name(), Store.YES));
			document.add(new TextField("publisher", book.publisher().name(), Store.YES));
		};

		final EntityMatcher<Book> entityMatcher = document ->
			this.isbn13ToBook.get(document.get("isbn13"))
		;

		final Index<Book> index = new Index<>(
			Book.class,
			documentPopulator,
			entityMatcher
		);

		if(index.size() == 0 && this.bookCount() > 0)
		{
			index.addAll(this.isbn13ToBook.values());
		}

		return index;
	}

}
