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

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.vaadin.flow.i18n.I18NProvider;

/**
 * I18N provider for Vaadin
 *
 */
@Component
@SuppressWarnings("serial")
public class VaadinApplicationI18NProvider implements I18NProvider
{
	private final String                      bundleBaseName  = "META-INF/resources/frontend/i18n/i18n";
	private final List<Locale>                providedLocales = Arrays.asList(
		Locale.ENGLISH,
		Locale.GERMAN,
		new Locale("es")
	);
	private final Map<Locale, ResourceBundle> bundles         = new HashMap<>();

	public VaadinApplicationI18NProvider()
	{
		super();
	}

	@Override
	public List<Locale> getProvidedLocales()
	{
		return this.providedLocales;
	}

	@Override
	public String getTranslation(
		final String key,
		final Locale locale,
		final Object... params
	)
	{
		try
		{
			final ResourceBundle bundle = this.bundles.computeIfAbsent(
				locale,
				this::createBundle
			);
			final String         value  = bundle.getString(key);
			return params.length > 0
				? MessageFormat.format(value, params)
				: value
			;
		}
		catch(final MissingResourceException e)
		{
			return "!{" + locale.getLanguage() + ": " + key + "}";
		}
	}

	private ResourceBundle createBundle(
		final Locale language
	)
	{
		return ResourceBundle.getBundle(
			this.bundleBaseName,
			language,
			this.getClass().getClassLoader()
		);
	}

}
