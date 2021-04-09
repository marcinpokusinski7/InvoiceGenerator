package com.vaadin.invoice.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.invoice.entity.Product;
import com.vaadin.invoice.service.ProductService;
import com.vaadin.invoice.ui.MainLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Route(value = "create", layout = MainLayout.class)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class InvoiceGenerator extends VerticalLayout {
    private ProductService productService;
    Map<Integer, Double> values = new TreeMap<Integer, Double>();
    Map<Integer, Double> totalPriceMap = new TreeMap<Integer, Double>();
    List<Integer> productList = new ArrayList<Integer>();

    TextField searchItems = new TextField();
    TextField preparedBy = new TextField("Full name");
    TextField clientName = new TextField("Client Full name");
    TextField IdentificationNumber = new TextField("Client Full name");
    DatePicker dateOfPreparation = new DatePicker("Transaction Day");
    DatePicker paymentDate = new DatePicker("Payment Day");
    RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
    Grid<Product> chooseProduct = new Grid<>(Product.class);
    Grid<Product> productsAdded = new Grid<>(Product.class);
    Button addItem = new Button("Add");
    Paragraph priceToPayField = new Paragraph();
    Button generateInvoice = new Button("Generate Invoice");
    Button removeItemsFromGrid = new Button("Remove Items");
    Div component1 = new Div();
    Div component2 = new Div();

    public InvoiceGenerator(ProductService productService) {
        this.productService = productService;
        addClassName("list-view");
        setSizeFull();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        configureDiv();
        component1.add(searchItems, chooseProduct, addItem, new VerticalLayout(preparedBy, clientName, dateOfPreparation, radioGroup, paymentDate));
        component2.add(removeItemsFromGrid, productsAdded, generateInvoice, priceToPayField);
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
        radioGroup.setItems("Pay now", "Payment within 14 days");
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroup.setValue("Option one");
    }

    private void configureGridWithAvailableItems() {
        chooseProduct.setWidth("99%");
        chooseProduct.setMinHeight("400px");
        chooseProduct.getColumns().forEach(col -> col.setAutoWidth(true));
        chooseProduct.setColumns("productCategory", "productName", "price");
        chooseProduct.addComponentColumn(item -> getItemQuantity(item)).setHeader("In Stock");
        chooseProduct.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        chooseProduct.setSelectionMode(Grid.SelectionMode.MULTI);
        chooseProduct.getStyle().set("border", "1px solid #d3d3d3");
    }


    private Component getItemQuantity(Product item) {
        // set how many items you want buy
        values.put(item.getId(), 1d);
        NumberField quantityField = new NumberField();
        quantityField.setHasControls(true);
        quantityField.setMin(1);
        quantityField.setMax(item.getInStock());
        quantityField.setValue(1d);
        quantityField.addValueChangeListener(e -> {
                values.put(item.getId(), e.getValue());
        });
        return quantityField;
    }

    private Component addedToCard(Grid<Product> productsAdded, Product item) {
        return new Paragraph(values.get(item.getId()).intValue() + "/" + item.getInStock());
    }

    private void configureGrid() {
        productsAdded.setSelectionMode(Grid.SelectionMode.MULTI);
        productsAdded.setHeightByRows(true);
        productsAdded.setWidth("99%");
        productsAdded.setMinHeight("400px");
        productsAdded.getColumns().forEach(col -> col.setAutoWidth(true));
        productsAdded.setColumns("productName", "price");
        productsAdded.addComponentColumn(item -> addedToCard(productsAdded, item)).setHeader("Quantity");
        productsAdded.getStyle().set("border", "1px solid #d3d3d3");
        productsAdded.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        productsAdded.addComponentColumn(item -> addTotalPrice(productsAdded, item)).setHeader("Total Price").setKey("Total Price");
    }


    public Component addTotalPrice(Grid<Product> productsAdded, Product item) {
        Paragraph price = new Paragraph();
        price.getStyle().set("font-size", "15px");
        totalPriceMap.put(item.getId(), item.getPrice() * values.get(item.getId()));
        price.setText("$" + totalPriceMap.get(item.getId()));
        Double sum = totalPriceMap.values()
                .stream()
                .mapToDouble(Double::valueOf)
                .sum();
        Paragraph totalPrice = new Paragraph();
        totalPrice.setText("Total Price: $" +sum);
        totalPrice.getStyle().set("font-size", "15px");
        productsAdded.getColumnByKey("Total Price").setFooter(totalPrice);
        return price;
    }


    private void configureAddButton() {
        addItem.addClickListener(click -> {
            chooseProduct.getSelectedItems().forEach(e -> {
                int findId = e.getId();
                productList.add(findId);
            });
            productsAdded.setItems(productService.getProductRepository().findAllById(productList));

        });

    }


    private void configureGenerateButton() {
        generateInvoice.getStyle().set("margin-left", "5px");
        removeItemsFromGrid.getStyle().set("margin-right", "auto");
        removeItemsFromGrid.addThemeVariants(ButtonVariant.LUMO_ERROR);
    }

    private void configureButtons() {
        searchItems.setPlaceholder("Search products by category, name...");
        searchItems.setClearButtonVisible(true);
        searchItems.setValueChangeMode(ValueChangeMode.LAZY);
        searchItems.addValueChangeListener(e -> updateList());
        searchItems.getStyle().set("width", "300px");
    }


    private void configureDiv() {
        component2.setWidth("100%");
        component1.setWidth("100%");
        component2.setHeightFull();
        component1.setHeightFull();
    }

    private void updateList() {
        chooseProduct.setItems(productService.findAll(searchItems.getValue()));
    }

}