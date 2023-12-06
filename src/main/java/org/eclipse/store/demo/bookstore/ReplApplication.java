package org.eclipse.store.demo.bookstore;

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

import org.eclipse.store.demo.bookstore.data.RandomDataAmount;

/**
 * Entry point for the demo application variant with a simple console.
 *
 */
public class ReplApplication
{
	public static void main(final String[] args)
	{
		final BookStoreDemo bookStoreDemo = new BookStoreDemo(
			RandomDataAmount.Medium()
		);

		new Repl(bookStoreDemo).run();
	}
}
