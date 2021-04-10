package com.vaadin.invoice.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.invoice.entity.Product;
import com.vaadin.invoice.service.ProductService;
import com.vaadin.invoice.ui.MainLayout;
import com.vaadin.invoice.ui.forms.ProductForm;

@Route(value = "product", layout = MainLayout.class)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class ProductView extends VerticalLayout {
    private final ProductForm form;
    private Grid<Product> grid = new Grid<>(Product.class);
    private ProductService productService;
    Button addProduct = new Button("Add Product", click -> addProduct());
    TextField searchItems = new TextField();

    public ProductView(ProductService productService) {
        this.productService = productService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureButtons();
        form = new ProductForm(productService);
        form.addListener(ProductForm.SaveEvent.class, this::saveProduct);
        form.addListener(ProductForm.DeleteEvent.class, this::deleteProduct);
        form.addListener(ProductForm.CloseEvent.class, e -> closeEditor());

        Div content = new Div(grid, form);
        content.addClassName("content");
        content.setSizeFull();
        closeEditor();
        add(new HorizontalLayout(searchItems, addProduct), content);
        updateGrid();
    }

    private void deleteProduct(ProductForm.DeleteEvent evt) {
        productService.delete(evt.getProduct());
        updateList();
        closeEditor();
    }

    private void saveProduct(ProductForm.SaveEvent evt) {
        productService.save(evt.getProduct());
        updateList();
        closeEditor();
    }

    private void closeEditor() {
//        form.setProduct(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateGrid() {
        grid.setItems(productService.findAll());
    }

    private void configureGrid() {
        grid.addClassName("invoice-grid");
        grid.setSizeFull();
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setColumns("id", "productName", "price", "productCategory", "inStock");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);

        grid.asSingleSelect().addValueChangeListener(evt -> editProduct(evt.getValue()));
    }

    private void editProduct(Product product) {
        if (product == null) {
            closeEditor();
        } else {
            form.setProduct(product);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void configureButtons() {
        searchItems.setPlaceholder("Search products by category, name...");
        searchItems.setClearButtonVisible(true);
        searchItems.setValueChangeMode(ValueChangeMode.LAZY);
        searchItems.addValueChangeListener(e -> updateList());
        searchItems.getStyle().set("width", "300px");

        addProduct.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

    }

    private void addProduct() {
        grid.asSingleSelect().clear();
        editProduct(new Product());
    }

    private void updateList() {
        grid.setItems(productService.findAll(searchItems.getValue()));
    }

}
