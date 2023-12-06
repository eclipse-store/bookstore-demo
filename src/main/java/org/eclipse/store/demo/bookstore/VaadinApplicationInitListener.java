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

import org.rapidpm.dependencies.core.logger.HasLogger;

import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.SessionInitListener;
import com.vaadin.flow.server.VaadinServiceInitListener;

/**
 * Service listener, registered via the Java service loader registry, which adds a logger as an error handler
 * and modifies the bootstrap page.
 */
public class VaadinApplicationInitListener
implements VaadinServiceInitListener, SessionInitListener, HasLogger
{
	public VaadinApplicationInitListener()
	{
		super();
	}

	@Override
	public void serviceInit(final ServiceInitEvent event)
	{
		event.getSource().addSessionInitListener(this);
	}

	@Override
	public void sessionInit(final SessionInitEvent event) throws ServiceException
	{
		event.getSession().setErrorHandler(error ->
			this.logger().severe(error.getThrowable())
		);
	}

}
