package com.vaadin.invoice.ui.views;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.invoice.entity.Invoice;
import com.vaadin.invoice.service.InvoiceService;
import com.vaadin.invoice.ui.MainLayout;

@Route(value = "", layout = MainLayout.class)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class InvoiceView extends VerticalLayout {
    private Grid<Invoice> grid = new Grid<>(Invoice.class);
    private InvoiceService invoiceService;

    public InvoiceView(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();

        add(grid);
        updateGrid();
    }

    private void updateGrid() {
        grid.setItems(invoiceService.findAll());
    }

    private void configureGrid() {
        grid.addClassName("invoice-grid");
        grid.setSizeFull();
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setColumns("invoiceId" ,"preparedBy", "dateCreated", "paymentDate","paymentDate", "clientName", "totalPrice");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
    }

}
