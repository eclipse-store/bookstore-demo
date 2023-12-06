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

import org.eclipse.serializer.chars.XChars;
import org.rapidpm.dependencies.core.logger.HasLogger;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.Route;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Generic view for internal server errors.
 *
 */
@Route(value = "error", layout = RootLayout.class)
public class ViewError extends VerticalLayout implements HasErrorParameter<Exception>, HasLogger
{
	public ViewError()
	{
		super();

		this.setSizeFull();
	}

	@Override
	public int setErrorParameter(
		final BeforeEnterEvent event,
		final ErrorParameter<Exception> parameter
	)
	{
		String message = parameter.getCustomMessage();
		if(XChars.isEmpty(message))
		{
			message = parameter.getCaughtException().getMessage();
		}
		if(message == null)
		{
			message = "";
		}

		this.logger().severe(message, parameter.getCaughtException());
		
		this.add(new Paragraph(message));

		return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	}

}
