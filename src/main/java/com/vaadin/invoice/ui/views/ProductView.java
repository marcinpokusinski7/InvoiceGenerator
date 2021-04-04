package com.vaadin.invoice.ui.views;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.invoice.entity.Product;
import com.vaadin.invoice.service.ProductService;
import com.vaadin.invoice.ui.MainLayout;

@Route(value = "product", layout = MainLayout.class)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class ProductView extends VerticalLayout {
    private Grid<Product> grid = new Grid<>(Product.class);
    private ProductService productService;

    public ProductView(ProductService productService) {
        this.productService = productService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();

        add(grid);
        updateGrid();
    }

    private void updateGrid() {
        grid.setItems(productService.findAll());
    }

    private void configureGrid() {
        grid.addClassName("invoice-grid");
        grid.setSizeFull();
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setColumns("productName", "price","productCategory");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
    }

}
