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


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.money.MonetaryAmount;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.store.demo.bookstore.BookStoreDemo;
import org.eclipse.store.demo.bookstore.data.Named;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.BeforeEvent;
import org.eclipse.store.gigamap.types.Condition;


/**
 * Abstract view to display entities in a {@link Grid}.
 *
 * @param <E> the entity type
 */
public abstract class ViewEntity<E> extends VerticalLayout
{
	final Grid<E>                 grid;
	final List<FilterField<E, ?>> filterFields;
	HeaderRow                     filterRow;
//	BookStoreDataProvider<E>      dataProvider;

	protected ViewEntity()
	{
		super();

		this.grid = createGrid();

		this.filterFields = new ArrayList<>();

		this.setSizeFull();

		this.addAttachListener(event ->
		{
			this.createUI();
			this.add(this.grid);

			this.listEntities();
		});
	}

	protected static <T> Grid<T> createGrid()
	{
		final Grid<T> grid = new Grid<>();
		grid.setMultiSort(true);
		grid.setColumnReorderingAllowed(true);
		grid.addThemeVariants(
			GridVariant.LUMO_NO_BORDER,
			GridVariant.LUMO_NO_ROW_BORDERS,
			GridVariant.LUMO_ROW_STRIPES
		);
		return grid;
	}


	public void listEntities() {
		this.grid.setItems(q -> {
			final var comparators = q.getSortOrders().stream().map(so ->
					this.grid.getColumnByKey(so.getSorted()).getComparator(so.getDirection())
			).collect(Collectors.toList());
			return this.compute(this.getCondition(), q.getOffset(), q.getLimit(), stream -> {
				for(final var c : comparators) {
					stream = stream.sorted(c);
				}
				return stream;
			});
		});
		this.filterFields.forEach(FilterField::updateOptions);
	}
	
	protected abstract void createUI();


	public abstract <R> R compute(Condition<E> condition, int offset, int limit, SerializableFunction<Stream<E>, R> function);

	protected Condition<E> getCondition()
	{
		Condition<E> condition = null;
		for(final FilterField<E, ?> filterField : this.filterFields)
		{
			final Condition<E> fieldCondition = filterField.condition();
			if(fieldCondition != null)
			{
				if(condition == null)
				{
					condition = fieldCondition;
				}
				else
				{
					condition = condition.and(fieldCondition);
				}
			}
		}
		return condition;
	}

	protected void gridDataUpdated()
	{
		// no-op by default
	}

	protected Grid.Column<E> addGridColumn(
		final String colKey,
		final ValueProvider<E, ?> valueProvider
	)
	{
		return addGridColumn(this.grid, colKey, valueProvider);
	}

	protected static <T> Grid.Column<T> addGridColumn(
		final Grid<T> grid,
		final String colKey,
		final ValueProvider<T, ?> valueProvider
	)
	{
		return grid.addColumn(valueProvider)
			.setHeader(grid.getTranslation(colKey))
			.setKey(colKey)
			.setResizable(true)
			.setSortable(true);
	}

	protected Grid.Column<E> addGridColumn(
		final String title,
		final Renderer<E> renderer
	)
	{
		return addGridColumn(this.grid, title, renderer);
	}

	protected static <T> Grid.Column<T> addGridColumn(
		final Grid<T> grid,
		final String title,
		final Renderer<T> renderer
	)
	{
		return grid.addColumn(renderer)
			.setHeader(title)
			.setResizable(true)
			.setSortable(true);
	}

	@SuppressWarnings("unchecked")
	protected Grid.Column<E> addGridColumn(
		final String colKey,
		final ValueProvider<E, ?> valueProvider,
		final Component filterComponent
	)
	{
		if(filterComponent instanceof FilterField)
		{
			this.filterFields.add((FilterField<E, ?>)filterComponent);
		}


		final Column<E> column = this.addGridColumn(colKey, valueProvider);

		if(filterComponent instanceof HasSize)
		{
			((HasSize)filterComponent).setSizeFull();
		}
		if(this.filterRow == null)
		{
			this.filterRow = this.grid.appendHeaderRow();
		}
		this.filterRow.getCell(column).setComponent(filterComponent);

		return column;
	}

	protected Grid.Column<E> addGridColumnWithTextFilter(
		final String colKey,
		final ValueProvider<E, String> valueProvider,
		final SerializableFunction<String, Condition<E>> conditionFactory
	)
	{
		final FilterTextField<E> text = new FilterTextField<>(
			conditionFactory
		);
		text.addValueChangeListener(event -> this.listEntities());
		return this.addGridColumn(
				colKey,
			valueProvider,
			text
		);
	}

	protected <F extends Named> FilterComboBox<E, F> addGridColumnWithDynamicFilter(
		final String title,
		final ValueProvider<E, F> valueProvider,
		final SerializableFunction<F, Condition<E>> conditionFactory
	)
	{
		final FilterComboBox<E, F> combo = new FilterComboBox<>(
			conditionFactory
		);

		combo.setItems(query -> {
			return this.compute(this.getCondition(), query.getOffset(), query.getLimit(), s -> s
				.map(valueProvider))
				.distinct()
				.filter(f -> StringUtils.containsIgnoreCase(f.name(), query.getFilter().get()));
		});

		combo.addValueChangeListener(event -> this.listEntities());

		this.addGridColumn(
			title,
			entity -> valueProvider.apply(entity).name(),
			combo
		);
		return combo;
	}

	protected E getSelectedEntity()
	{
		return this.grid.getSelectionModel()
			.getFirstSelectedItem()
			.orElse(null);
	}

	protected static <T> Renderer<T> moneyRenderer(
		final ValueProvider<T, ? extends MonetaryAmount> valueProvider
	)
	{
		return new TextRenderer<>(
			entity -> BookStoreDemo.MONETARY_AMOUNT_FORMAT.format(
				valueProvider.apply(entity)
			)
		);
	}

	protected static String getQueryParameter(
		final BeforeEvent event,
		final String name
	)
	{
		final List<String> list = event.getLocation().getQueryParameters().getParameters().get(name);
		return list != null && list.size() == 1
			? list.get(0)
			: null;
	}

}
