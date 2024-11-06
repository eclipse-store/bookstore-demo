
package org.eclipse.store.demo.bookstore.data;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.serializer.persistence.types.PersistenceStoring;
import org.eclipse.store.demo.bookstore.BookStoreDemo;
import org.eclipse.store.demo.bookstore.util.concurrent.ReadWriteLocked;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;

import one.microstream.gigamap.Condition;
import one.microstream.gigamap.GigaMap;
import one.microstream.gigamap.GigaQuery;

/**
 * Range of all books sold by this company.
 * <p>
 * This type is used to read and write the {@link Book}s, their {@link Author}s, {@link Genre}s,
 * {@link Publisher}s and {@link Language}s.
 * <p>
 * All operations on this type are thread safe.
 *
 * @see Data#books()
 * @see ReadWriteLocked
 */
public class Books extends ReadWriteLocked
{
	private final GigaMap<Book> map = GigaMap.<Book>Builder()
		.withBitmapIdentityIndex(Book.isbn13Index)
		.withBitmapIndex(Named.nameIndex)
		.withBitmapIndex(Book.authorIndex)
		.withBitmapIndex(Book.genreIndex)
		.withBitmapIndex(Book.publisherIndex)
		.withBitmapIndex(Book.languageIndex)
		.build()
	;
	
	
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
		this.write(() ->
		{
			this.map.add(book);
			this.map.store();
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
		this.write(() ->
		{
			this.map.addAll(books);
			this.map.store();
		});
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
			this.map.addAll(books);
			persister.store(this.map);
		});
	}

	/**
	 * Gets all authors as a {@link List}.
	 * Modifications to the returned list are not reflected to the backed data.
	 *
	 * @return all authors
	 */
	public List<Author> authors()
	{
		return this.read(() ->
			Book.authorIndex.resolveKeys(this.map)
		);
	}

	/**
	 * Gets all genres as a {@link List}.
	 * Modifications to the returned list are not reflected to the backed data.
	 *
	 * @return all genres
	 */
	public List<Genre> genres()
	{
		return this.read(() ->
			Book.genreIndex.resolveKeys(this.map)
		);
	}

	/**
	 * Gets all publishers as a {@link List}.
	 * Modifications to the returned list are not reflected to the backed data.
	 *
	 * @return all publishers
	 */
	public List<Publisher> publishers()
	{
		return this.read(() ->
			Book.publisherIndex.resolveKeys(this.map)
		);
	}

	/**
	 * Gets all languages as a {@link List}.
	 * Modifications to the returned list are not reflected to the backed data.
	 *
	 * @return all languages
	 */
	public List<Language> languages()
	{
		return this.read(() ->
			Book.languageIndex.resolveKeys(this.map)
		);
	}

	/**
	 * Gets the total amount of all books.
	 *
	 * @return the amount of books
	 */
	public long bookCount()
	{
		return this.read(() ->
			this.map.size()
		);
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
			this.map.query(Book.isbn13Index.is(isbn13)).findFirst().orElse(null)
		);
	}

	/**
	 * Searches all books by title with a given query.
	 *
	 * @param queryText the search query
	 * @return a list of books matching the query, or an empty list
	 */
	public List<Book> searchByTitle(final String queryText)
	{
		return this.read(() ->
			this.map.query(Named.nameIndex.containsIgnoreCase(queryText)).toList()
		);
	}

	/**
	 * Gets all books written by a specific author.
	 *
	 * @param author the author to search for
	 * @return a list of books
	 */
	public List<Book> allByAuthor(final Author author)
	{
		return this.read(() ->
			this.map.query(Book.authorIndex.is(author)).toList()
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
		return this.read(() ->
			this.map.query(Book.genreIndex.is(genre)).toList()
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
		return this.read(() ->
			this.map.query(Book.publisherIndex.is(publisher)).toList()
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
		return this.read(() ->
			this.map.query(Book.languageIndex.is(language)).toList()
		);
	}
	
	public <R> R compute(
		final Condition<Book>           condition,
		final int                       offset,
		final int                       limit,
		final Function<Stream<Book>, R> function
	)
	{
		return this.read(() ->
		{
			GigaQuery<Book> query = this.map.query();
			if(condition != null)
			{
				query = query.and(condition);
			}
			return function.apply(query.toList(offset, limit).stream());
		});
	}

}
