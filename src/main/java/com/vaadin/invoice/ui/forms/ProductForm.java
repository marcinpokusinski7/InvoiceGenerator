package com.vaadin.invoice.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.shared.Registration;
import com.vaadin.invoice.entity.Product;
import com.vaadin.invoice.service.ProductService;

public class ProductForm extends FormLayout {
    TextField productName = new TextField("Product Name");
    NumberField price = new NumberField("Price for each");
    ComboBox<Product.Status> productCategory = new ComboBox<>("Choose Category");
    TextField inStock = new TextField("In Stock");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Close");

    Binder<Product> binder = new BeanValidationBinder<>(Product.class);

    public ProductForm(ProductService productService) {
        addClassName("product-form");
        binder.forField(inStock).withNullRepresentation("").withConverter(new StringToIntegerConverter("Must enter a number"))
                .bind(Product::getInStock, Product::setInStock);
        productCategory.setItems(Product.Status.values());
        binder.bindInstanceFields(this);

        add(productName, price,
                productCategory, inStock,
                createButtonLayout());

    }


    public void setProduct(Product product) {
        binder.setBean(product);
    }

    private Component createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);



        return new HorizontalLayout(save, delete, close);
    }


    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    public static abstract class ProductFormEvent extends ComponentEvent<ProductForm> {
        private Product product;

        protected ProductFormEvent(ProductForm source, Product product) {
            super(source, false);
            this.product = product;
        }

        public Product getProduct() {
            return product;
        }
    }

    public static class SaveEvent extends ProductFormEvent {
        SaveEvent(ProductForm source, Product product) {
            super(source, product);
        }
    }

    public static class DeleteEvent extends ProductFormEvent {
        DeleteEvent(ProductForm source, Product product) {
            super(source, product);
        }

    }

    public static class CloseEvent extends ProductFormEvent {
        CloseEvent(ProductForm source) {

            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
