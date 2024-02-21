package org.eclipse.store.demo.bookstore.ui.views;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;

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

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Root layout for all views, containing the main menu.
 *
 */
@StyleSheet("context://frontend/styles/shared-styles.css")
public class RootLayout extends AppLayout {

    private H2 viewTitle;

    public RootLayout() {
        this.setPrimarySection(Section.DRAWER);
        this.addDrawerContent();
        this.addHeaderContent();
        //<theme-editor-local-classname>
        this.addClassName("MainLayout-app-layout-1");
    }

    private void addHeaderContent() {
        final DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        this.viewTitle = new H2();
        this.viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        this.addToNavbar(true, toggle, this.viewTitle);
    }

    private void addDrawerContent() {
        final var image = new H2(this.getTranslation("app.title"));
        image.addClassNames("app-name");
        
        final Header header = new Header(new Image("frontend/images/logo32.png", "Logo"), image);

        final Scroller scroller = new Scroller(this.createNavigation());

        this.addToDrawer(header, scroller);
    }

    private SideNav createNavigation() {
        final SideNav nav = new SideNav();
        nav.addItem(new SideNavItem(this.getTranslation("home")      , ViewMain.class          , VaadinIcon.HOME.create()));
        nav.addItem(new SideNavItem(this.getTranslation("books")     , ViewBooks.class         , VaadinIcon.BOOK.create()));
        nav.addItem(new SideNavItem(this.getTranslation("shops")     , ViewShops.class         , VaadinIcon.SHOP.create()));
        nav.addItem(new SideNavItem(this.getTranslation("inventory") , ViewInventory.class     , VaadinIcon.STORAGE.create()));
        nav.addItem(new SideNavItem(this.getTranslation("customers") , ViewCustomers.class     , LineAwesomeIcon.PERSON_BOOTH_SOLID.create()));
        nav.addItem(new SideNavItem(this.getTranslation("purchases") , ViewPurchases.class     , LineAwesomeIcon.SHOPPING_BASKET_SOLID.create()));
       return nav;
    }


    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        this.viewTitle.setText(this.getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        final PageTitle title = this.getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
