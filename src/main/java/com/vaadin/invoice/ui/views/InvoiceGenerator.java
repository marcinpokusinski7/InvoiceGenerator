package com.vaadin.invoice.ui.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.invoice.entity.Product;
import com.vaadin.invoice.service.ProductService;
import com.vaadin.invoice.ui.MainLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Route(value = "create", layout = MainLayout.class)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class InvoiceGenerator extends VerticalLayout {
    private ProductService productService;
    List<Product> productList = new ArrayList<Product>();

    TextField searchItems = new TextField();
    TextField preparedBy = new TextField("Full name");
    TextField clientName = new TextField("Client Full name");
    DatePicker dateOfPreparation = new DatePicker();
    DatePicker PaymentDate = new DatePicker();
    RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
    Grid<Product> chooseProduct = new Grid<>(Product.class);
    Grid<Product> productsAdded = new Grid<>(Product.class);
    Button add = new Button("Add");
    Text totalPrice = new Text("$10.60");
    Text priceToPay = new Text("Price" + totalPrice);
    Button generateInvoice = new Button("Generate Invoice");
    Div component1 = new Div();
    Div component2 = new Div();

    public InvoiceGenerator(ProductService productService) {
        this.productService = productService;
        addClassName("list-view");
        setSizeFull();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        configureDiv();
        component1.add(searchItems, chooseProduct, add, totalPrice);
        component2.add(productsAdded, generateInvoice);
        layout.add(component1, component2);

        configureButtons();
        configureGenerateButton();
        radioButtons();
        configureGridWithAvailableItems();
        configureGrid();
        configureAddButton();
        add(layout);
        updateList();

    }

    private void radioButtons() {
        radioGroup.setLabel("Payment option");
        radioGroup.setItems("Pay after 14 days", "Payment withing 14 days");
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroup.setValue("Option one");
    }

    private void configureGridWithAvailableItems() {
        chooseProduct.setWidth("99%");
        chooseProduct.setMinHeight("400px");
        chooseProduct.getColumns().forEach(col -> col.setAutoWidth(true));
        chooseProduct.setColumns("productCategory", "productName", "price");
        chooseProduct.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        chooseProduct.setSelectionMode(Grid.SelectionMode.MULTI);
    }

    private void configureGrid() {
        productsAdded.setHeightByRows(true);
        productsAdded.setWidth("99%");
        productsAdded.setMinHeight("400px");
        productsAdded.getColumns().forEach(col -> col.setAutoWidth(true));
        productsAdded.setColumns("productName", "price");
        productsAdded.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
    }

    private void configureAddButton() {
        add.addClickListener(click -> {
            addToGrid();
            productsAdded.getDataProvider().refreshAll();
        });
    }


    private void configureGenerateButton() {
        generateInvoice.getStyle().set("margin-left", "5px");
    }

    private void configureButtons() {
        searchItems.setPlaceholder("Search products by category, name...");
        searchItems.setClearButtonVisible(true);
        searchItems.setValueChangeMode(ValueChangeMode.LAZY);
        searchItems.addValueChangeListener(e -> updateList());
        searchItems.getStyle().set("width", "300px");
    }

    private void addToGrid() {
        chooseProduct.asMultiSelect().addValueChangeListener(e -> {
            Stream<Integer> findById = e.getValue().stream().map(Product::getId);
            List<Integer> list = findById.collect(Collectors.toList());
            productList.add(productService.getProductRepository().findAllById(list));
// add multiple items to table
//                list.add(productService.findAll().get(id));
//               productsAdded.setItems(productService.findAll().get(id);
        });
        productsAdded.setItems(productList);

    }


    private void configureDiv() {
        component2.setWidth("100%");
        component1.setWidth("100%");
        component2.setHeightFull();
        component1.setHeightFull();
        component2.getStyle().set("border-left", "1px solid #d3d3d3");

    }

    private void updateList() {
        chooseProduct.setItems(productService.findAll(searchItems.getValue()));
    }

}
