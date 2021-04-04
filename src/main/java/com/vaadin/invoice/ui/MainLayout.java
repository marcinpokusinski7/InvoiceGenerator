package com.vaadin.invoice.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.invoice.ui.views.InvoiceGenerator;
import com.vaadin.invoice.ui.views.InvoiceView;
import com.vaadin.invoice.ui.views.ProductView;

@CssImport("./styles/shared-styles.css")
@JsModule("@vaadin/vaadin-lumo-styles/presets/compact.js")
@Theme(Lumo.class)
@PWA(name = "Invoice Generator",
        shortName = "IG",
        description = "Vaadin invoice generator",
        enableInstallPrompt = false)
public class MainLayout extends AppLayout {


    public MainLayout() {
        createHeader();


    }

    private void createHeader() {

        H1 logo = new H1("Invoices Generator");
        logo.addClassName("logo");
        HorizontalLayout header = new HorizontalLayout(logo);
        header.addClassName("header");
        header.setWidth("10%");
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        RouterLink invoiceView = new RouterLink("Invoices", InvoiceView.class);
        RouterLink productView = new RouterLink("Products", ProductView.class);
        RouterLink invoiceCreation = new RouterLink("Create Invoice", InvoiceGenerator.class);
        Tabs tabs = new Tabs(new Tab(invoiceView), new Tab(productView), new Tab(invoiceCreation));

        addToNavbar(header, tabs);


    }


}
