package com.vaadin.invoice.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Route(value = "create", layout = MainLayout.class)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class InvoiceGenerator extends VerticalLayout {
    private ProductService productService;
    List<Integer> productList = new ArrayList<Integer>();

    NumberField quantityField = new NumberField();
    TextField searchItems = new TextField();
    TextField preparedBy = new TextField("Full name");
    TextField clientName = new TextField("Client Full name");
    DatePicker dateOfPreparation = new DatePicker();
    DatePicker PaymentDate = new DatePicker();
    RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
    Grid<Product> chooseProduct = new Grid<>(Product.class);
    Grid<Product> productsAdded = new Grid<>(Product.class);
    Button add = new Button("Add");
//    Text totalPrice = new Text("$10.60");
//    Text priceToPay = new Text("Price" + totalPrice);
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
        component1.add(searchItems, chooseProduct, add);
        component2.add(removeItemsFromGrid, productsAdded, generateInvoice);
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
        productsAdded.addComponentColumn(item -> createButton(productsAdded, item)).setHeader("Quantity");
        productsAdded.addComponentColumn(item -> countPrice(productsAdded, item)).setHeader("Total Price");
    }

    private Component countPrice(Grid<Product> productsAdded, Product item) {
        return new Text(multiplePrice(item.getPrice()));
    }


    private Component createButton(Grid<Product> productsAdded, Product item) {
        quantityField.setHasControls(true);
        quantityField.setMin(1);
        quantityField.setValue(1d);
        multiplePrice(item.getPrice());
        return quantityField;
    }

    private String multiplePrice(Double item) {
        final String[] totalPrice = new String[1];
        quantityField.addValueChangeListener(e ->{
            BigDecimal tempPrice = null;
            if(e.getValue() == null){
                tempPrice = BigDecimal.ZERO;
            }else {
                totalPrice[0] = (tempPrice.multiply(BigDecimal.valueOf(item))).toString();
            }
        });
        return totalPrice[0];
    }


    private void configureAddButton() {
        add.addClickListener(click -> {
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
        component2.getStyle().set("border-left", "1px solid #d3d3d3");

    }

    private void updateList() {
        chooseProduct.setItems(productService.findAll(searchItems.getValue()));
    }

}
