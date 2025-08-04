package org.eclipse.store.demo.bookstore.ui.views;

import static org.javamoney.moneta.function.MonetaryFunctions.summarizingMonetary;

import java.time.Year;
import java.util.stream.Stream;

import org.eclipse.store.demo.bookstore.BookStoreDemo;
import org.eclipse.store.demo.bookstore.data.Customer;
import org.eclipse.store.demo.bookstore.data.Purchase;
import org.eclipse.store.demo.bookstore.data.PurchaseItem;
import org.eclipse.store.demo.bookstore.data.Purchases;
import org.eclipse.store.demo.bookstore.data.Shop;
import org.javamoney.moneta.function.MonetarySummaryStatistics;

import com.google.common.collect.Range;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.router.Route;

import org.eclipse.store.gigamap.types.Condition;

/**
 * View to display {@link Purchases}.
 *
 */
@Route(value = "purchases", layout = RootLayout.class)
public class ViewPurchases extends ViewEntity<Purchase>
{
	int      year = Year.now().getValue();
	Span    totalColumnFooter;
	private FilterComboBox<Purchase, Shop>     shopFilter;
	private FilterComboBox<Purchase, Customer> customerFilter;

	public ViewPurchases()
	{
		super();
	}

	public void filterBy(final Shop shop)
	{
		this.shopFilter.setValue(shop);
		this.listEntities();
	}

	public void filterBy(final Customer customer)
	{
		this.customerFilter.setValue(customer);
		this.listEntities();
	}

	@Override
	protected void createUI()
	{
		this.shopFilter     = this.addGridColumnWithDynamicFilter("shop"     , Purchase::shop    , Purchase.shopIndex::is);
		                      this.addGridColumnWithDynamicFilter("employee" , Purchase::employee, Purchase.employeeIndex::is);
		this.customerFilter = this.addGridColumnWithDynamicFilter("customer" , Purchase::customer, Purchase.customerIndex::is);
		this.addGridColumn                 ("timestamp", Purchase::timestamp               );
		this.addGridColumn                 ("total"    , moneyRenderer(Purchase::total)    )
			.setFooter(this.totalColumnFooter = new Span());

		final Range<Integer> years = BookStoreDemo.getInstance().data().purchases().years();

		final IntegerField yearField = new IntegerField();
		yearField.setStepButtonsVisible(true);
		yearField.setMin(years.lowerEndpoint());
		yearField.setMax(years.upperEndpoint());
		yearField.setValue(this.year);
		yearField.addValueChangeListener(event -> {
			this.year = event.getValue();
			this.listEntities();
		});

		final HorizontalLayout bar = new HorizontalLayout(
			new Span(this.getTranslation("year")),
			yearField
		);
		bar.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
		this.add(bar);

		this.grid.setItemDetailsRenderer(new ComponentRenderer<>(this::createPurchaseDetails));
		this.grid.setDetailsVisibleOnClick(true);
	}

	private Component createPurchaseDetails(final Purchase purchase)
	{
		final Grid<PurchaseItem> grid = createGrid();
		addGridColumn(grid, "isbn13"   , item -> item.book().isbn13()          );
		addGridColumn(grid, "book"     , item -> item.book().title()           );
		addGridColumn(grid, "author"   , item -> item.book().author().name()   );
		addGridColumn(grid, "publisher", item -> item.book().publisher().name());
		addGridColumn(grid, "price"    , moneyRenderer(PurchaseItem::price)            );
		addGridColumn(grid, "amount"   , PurchaseItem::amount                          );
		addGridColumn(grid, "total"    , moneyRenderer(PurchaseItem::itemTotal)        );
		grid.setItems(purchase.items().toList());
		grid.setAllRowsVisible(true);
		return grid;
	}
	
	@Override
	protected Condition<Purchase> getCondition()
	{
		final Condition<Purchase> condition = super.getCondition();
		final Condition<Purchase> yearCondition = Purchase.yearIndex.is(this.year);
		return condition != null
			? condition.and(yearCondition)
			: yearCondition
		;
	}

	@Override
	public <R> R compute(
		final Condition<Purchase>                       condition,
		final int                                       offset,
		final int                                       limit,
		final SerializableFunction<Stream<Purchase>, R> function
	)
	{
		return BookStoreDemo.getInstance().data().purchases().compute(condition, offset, limit, function);
	}

	@Override
	public void listEntities() {
		super.listEntities();
		try {
			final MonetarySummaryStatistics stats = this.compute(this.getCondition(), 0, Integer.MAX_VALUE, stream ->
				stream.map(Purchase::total)
					.collect(summarizingMonetary(BookStoreDemo.CURRENCY_UNIT))
			);
			this.totalColumnFooter.setText(
				BookStoreDemo.MONETARY_AMOUNT_FORMAT.format(stats.getSum())
			);
		} catch (final Exception e) {
			// division by zero
			this.totalColumnFooter.setText("-");
		}
	}
}
