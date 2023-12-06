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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VaadinApplicationConfiguration
{
	public VaadinApplicationConfiguration()
	{
		super();
	}

	/**
	 * Manages the creation and disposal of the {@link BookStoreDemo} singleton.
	 */
	@Bean(destroyMethod = "shutdown")
	public BookStoreDemo getBookStoreDemo()
	{
		final BookStoreDemo demo = new BookStoreDemo(RandomDataAmount.Medium());
		demo.storageManager(); // eager init
		return demo;
	}
}
