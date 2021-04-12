package com.vaadin.invoice.ui.views;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfDiv;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.*;
import java.util.stream.Stream;

@Route(value = "create", layout = MainLayout.class)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class InvoiceGenerator extends VerticalLayout {

    private ProductService productService;
    private Integer randomNumberFV;
    Document document = new Document();
    H1 invoiceFvNumber = new H1();
    Random random = new Random();
    Map<Integer, Double> values = new TreeMap<Integer, Double>();
    Map<Integer, Double> totalPriceMap = new TreeMap<Integer, Double>();
    List<Integer> productList = new ArrayList<Integer>();
    List<Double> productPrice = new ArrayList<>();
    Set<Double> productPrices = new HashSet<>();
    List<String> productName = new ArrayList<>();
    Set<String> productNames = new HashSet<>();
    TextField searchItems = new TextField();
    TextField preparedBy = new TextField("Prepared By");
    TextField companyLocation = new TextField("Company postal address");
    TextField identificationNumberPreparedBy = new TextField("Identification Number");
    TextField clientName = new TextField("Client Full name");
    TextField companyLocationOClient = new TextField("Company postal address");
    TextField identificationNumber = new TextField("Identification Number");
    DatePicker dateOfPreparation = new DatePicker("Transaction Day");
    DatePicker paymentDate = new DatePicker("Payment Day");
    RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
    RadioButtonGroup<String> radioGroupCompany = new RadioButtonGroup<>();
    Grid<Product> chooseProduct = new Grid<>(Product.class);
    Grid<Product> productsAdded = new Grid<>(Product.class);
    Button addItem = new Button("Add Items to Invoice");
    Button hint = new Button("Help");
    Button generateInvoice = new Button("Generate Invoice", VaadinIcon.CLIPBOARD_TEXT.create());
    Div component1 = new Div();
    Div component2 = new Div();

    HorizontalLayout layout = new HorizontalLayout();
    HorizontalLayout layout2 = new HorizontalLayout();
    HorizontalLayout layout3 = new HorizontalLayout();
    VerticalLayout layoutFormOne = new VerticalLayout();
    VerticalLayout layoutFormTwo = new VerticalLayout();
    VerticalLayout layoutFormThree = new VerticalLayout();
    VerticalLayout layoutFormFour = new VerticalLayout();
    HorizontalLayout layoutUnderGrid = new HorizontalLayout();

    public InvoiceGenerator(ProductService productService) {
        this.productService = productService;
        addClassName("list-view");
        setSizeFull();
        configureViews();
        layoutUnderGrid.add(new VerticalLayout(generateInvoice));
        layoutFormTwo.add(preparedBy, companyLocation, identificationNumberPreparedBy);
        layoutFormOne.add(clientName, companyLocationOClient, identificationNumber);
        layoutFormThree.add(radioGroup, dateOfPreparation, paymentDate);
        layoutFormFour.add(radioGroupCompany);
        layout2.add(hint);
        component1.add(searchItems, chooseProduct, addItem, new HorizontalLayout(layoutFormTwo, layoutFormOne, layoutFormFour, layoutFormThree));
        component2.add(layout2, productsAdded, layoutUnderGrid);
        layout3.add(invoiceFvNumber);
        layout.add(component1, component2);
        configureButtons();
        configureGenerateButton();
        radioButtons();
        configureGridWithAvailableItems();
        configureGrid();
        configureAddButton();
        generateRandomFv();
        configureHelpButton();
        configureGenerateInvoiceButton();
        configureTextFields();
        add(layout3, layout, new Footer(new Text("@2021")));
        updateList();
    }


    private void radioButtons() {
        radioGroup.setLabel("Payment option");
        radioGroup.setItems("Pay now", "Payment within 14 days", "Choose other date");
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroup.setValue("Pay now");
        if (radioGroup.getValue().equals("Pay now")) {
            dateOfPreparation.setValue(LocalDate.now());
            paymentDate.setValue(LocalDate.now());
            dateOfPreparation.setEnabled(false);
            paymentDate.setEnabled(false);
            radioGroup.setRequired(true);
        }
        radioGroup.setRequired(true);
        radioGroupCompany.setLabel("Choose Invoice Format");
        radioGroupCompany.setItems("Company invoice", "Personal invoice");
        radioGroupCompany.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroupCompany.setValue("Option one");
        radioGroupCompany.setRequired(true);
        radioGroupCompany.setValue("Company invoice");
        if (radioGroup.getValue().equals("Company invoice")) {
            companyLocationOClient.setEnabled(true);
            identificationNumber.setEnabled(true);
            companyLocationOClient.setLabel("Company postal address");
            radioGroupCompany.setRequired(true);
        }
        radioGroupCompany.addValueChangeListener(event -> {
            if (event.getValue().equals("Personal invoice")) {
                companyLocationOClient.setLabel("Customer address");
                identificationNumber.setEnabled(false);
            } else if (event.getValue().equals("Company invoice")) {
                companyLocationOClient.setEnabled(true);
                identificationNumber.setEnabled(true);
                companyLocationOClient.setLabel("Company postal address");
            }
        });

        radioGroup.addValueChangeListener(event -> {
            if (event.getValue().equals("Pay now")) {
                dateOfPreparation.setValue(LocalDate.now());
                paymentDate.setValue(LocalDate.now());
                dateOfPreparation.setEnabled(false);
                paymentDate.setEnabled(false);
            } else if (event.getValue().equals("Payment within 14 days")) {
                dateOfPreparation.setValue(LocalDate.now());
                dateOfPreparation.setEnabled(false);
                paymentDate.setEnabled(false);
                paymentDate.setValue(LocalDate.now().plusDays(14));
            } else if (event.getValue().equals("Choose other data")) {
                dateOfPreparation.setEnabled(true);
                paymentDate.setEnabled(true);
                dateOfPreparation.setMin(LocalDate.now());
            }
        });

    }

    private void configureTextFields() {
        identificationNumber.setMaxLength(10);
        identificationNumber.setMinLength(10);
        identificationNumberPreparedBy.setMinLength(10);
        identificationNumberPreparedBy.setMaxLength(10);
        Icon icon = VaadinIcon.SEARCH.create();
        searchItems.setPrefixComponent(icon);
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
        totalPrice.setText("Total Price: $" + sum);
        totalPrice.getStyle().set("font-size", "15px");
        productsAdded.getColumnByKey("Total Price").setFooter(totalPrice);
        return price;
    }


    private void configureAddButton() {
        addItem.getStyle().set("margin-left", "5px");
        addItem.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addItem.addClickListener(click -> {
            chooseProduct.getSelectedItems().forEach(e -> {
                int findId = e.getId();
                productList.add(findId);
                productName.add(e.getProductName());
                productPrice.add(e.getPrice());
            });
            productsAdded.setItems(productService.getProductRepository().findAllById(productList));

        });

    }


    private void configureGenerateButton() {
        generateInvoice.addClassName("generate");
        generateInvoice.setIconAfterText(true);
        generateInvoice.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        hint.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    }


    private void configureButtons() {
        searchItems.setPlaceholder("Search products by category, name...");
        searchItems.setClearButtonVisible(true);
        searchItems.setValueChangeMode(ValueChangeMode.LAZY);
        searchItems.addValueChangeListener(e -> updateList());
        searchItems.getStyle().set("width", "300px");
    }


    private void configureViews() {
        component2.setWidth("100%");
        component1.setWidth("100%");
        component2.addClassName("component2");
        component2.setHeightFull();
        component1.setHeightFull();
        layout.setSizeFull();
        layout2.setJustifyContentMode(JustifyContentMode.END);
        invoiceFvNumber.setWidth(null);
        layout3.setWidth("100%");
        layout3.setJustifyContentMode(JustifyContentMode.CENTER);
        layoutUnderGrid.setJustifyContentMode(JustifyContentMode.END);
    }

    private void updateList() {
        chooseProduct.setItems(productService.findAll(searchItems.getValue()));
    }

    private void generateRandomFv() {
        randomNumberFV = random.nextInt(Integer.MAX_VALUE);
        invoiceFvNumber.getStyle().set("font-size", "18px");
        invoiceFvNumber.setText("FV/" + String.valueOf(randomNumberFV) + "/");
    }

    private void configureHelpButton() {
        hint.addClickListener(e -> {
            Dialog dialog = new Dialog();

            H1 h1 = new H1();
            h1.add("Help");
            Text text = new Text("Fill up required fields to generate PDF invoice. \n" +
                    "Old generated invoices can be found in Invoices tab. \n" +
                    "To add more products go to Products tab, and fill up required form. \n" +
                    "Good luck :)");
            dialog.setWidth("500px");
            dialog.add(h1, text);
            dialog.open();
        });
    }

    private void configureGenerateInvoiceButton() {
        generateInvoice.addClickListener(e -> {
            try {
                productPrices.addAll(productPrice);
                productNames.addAll(productName);
                productPrice.clear();
                productName.clear();
                productName.addAll(productNames);
                productPrice.addAll(productPrices);
                pdfCon();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (DocumentException documentException) {
                documentException.printStackTrace();
            } catch (URISyntaxException uriSyntaxException) {
                uriSyntaxException.printStackTrace();
            }


        });
    }


    private void pdfCon() throws IOException, DocumentException, URISyntaxException {
        PdfWriter.getInstance(document, new FileOutputStream("Test.pdf"));

        document.open();
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Double sum = totalPriceMap.values()
                .stream()
                .mapToDouble(Double::valueOf)
                .sum();

        Chunk chunk = new Chunk("FV/" + String.valueOf(randomNumberFV) + "/", font);
        Chunk chunk2 = new Chunk("      Total Price: $" + String.valueOf(sum), font);
        Chunk chunkName = new Chunk("Prepared By: " + preparedBy.getValue());
        Chunk chunkDate = new Chunk("Transaction Day: " + dateOfPreparation.getValue() + "/ Payment Day: " + paymentDate.getValue());
        Chunk chunkAddress = new Chunk("Company Adress: " + companyLocation.getValue());
        Chunk chunkNIP = new Chunk("Company ID: " + identificationNumberPreparedBy.getValue());
        Chunk chunkNameClient = new Chunk("Client: " + clientName.getValue());
        Chunk chunkAddressCompany = new Chunk("Company address: " + companyLocationOClient.getValue());
        Chunk chunkNIPClient = new Chunk("Client ID: " + identificationNumber.getValue());

        PdfDiv div = new PdfDiv();
        PdfDiv div1 = new PdfDiv();
        PdfDiv div2 = new PdfDiv();
        PdfDiv div3 = new PdfDiv();
        div.addElement(chunk);
        div.addElement(chunkDate);
        div.setTextAlignment(1);
        div.setPercentageHeight(0.1f);
        PdfPTable table = new PdfPTable(3);
        addTableHeader(table);
        addRows(table);
        div2.addElement(table);
        div3.addElement(chunk2);
        div3.setTextAlignment(2);
        div3.getRight();
        div1.setPercentageHeight(0.25f);
        document.add(div);
        div1.addElement(chunkName);
        div1.addElement(chunkAddress);
        div1.addElement(chunkNIP);
        div1.addElement(chunkNameClient);
        div1.addElement(chunkAddressCompany);
        div1.addElement(chunkNIPClient);

        document.add(div1);
        document.add(div2);
        document.add(div3);
        document.close();
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Product Name", "Price", "Quantity")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private void addRows(PdfPTable table) {

        for (int i = 0; i < productName.size(); i++) {
            table.addCell(String.valueOf(productName.get(i)));
            table.addCell(String.valueOf(productPrice.get(i)));
            table.addCell(String.valueOf(values.getOrDefault(i, 1d))); // need to be repaired
        }
    }
}