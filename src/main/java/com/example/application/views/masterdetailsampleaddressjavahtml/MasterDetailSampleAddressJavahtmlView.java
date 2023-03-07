package com.example.application.views.masterdetailsampleaddressjavahtml;

import com.example.application.data.entity.SampleAddress;
import com.example.application.data.service.SampleAddressService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Master-Detail SampleAddress (Javahtml)")
@Route(value = "master-detail-view-sampleaddress/:sampleAddressID?/:action?(edit)", layout = MainLayout.class)
@Tag("master-detail-sample-address-javahtml-view")
@JsModule("./views/masterdetailsampleaddressjavahtml/master-detail-sample-address-javahtml-view.ts")
public class MasterDetailSampleAddressJavahtmlView extends LitTemplate implements HasStyle, BeforeEnterObserver {

    private final String SAMPLEADDRESS_ID = "sampleAddressID";
    private final String SAMPLEADDRESS_EDIT_ROUTE_TEMPLATE = "master-detail-view-sampleaddress/%s/edit";

    // This is the Java companion file of a design
    // You can find the design file inside /frontend/views/
    // The design can be easily edited by using Vaadin Designer
    // (vaadin.com/designer)

    @Id
    private Grid<SampleAddress> grid;

    @Id
    private TextField street;
    @Id
    private TextField postalCode;
    @Id
    private TextField city;
    @Id
    private TextField state;
    @Id
    private TextField country;

    @Id
    private Button cancel;
    @Id
    private Button save;

    private BeanValidationBinder<SampleAddress> binder;

    private SampleAddress sampleAddress;

    private final SampleAddressService sampleAddressService;

    public MasterDetailSampleAddressJavahtmlView(SampleAddressService sampleAddressService) {
        this.sampleAddressService = sampleAddressService;
        addClassNames("master-detail-sample-address-javahtml-view");
        grid.addColumn(SampleAddress::getStreet).setHeader("Street").setAutoWidth(true);
        grid.addColumn(SampleAddress::getPostalCode).setHeader("Postal Code").setAutoWidth(true);
        grid.addColumn(SampleAddress::getCity).setHeader("City").setAutoWidth(true);
        grid.addColumn(SampleAddress::getState).setHeader("State").setAutoWidth(true);
        grid.addColumn(SampleAddress::getCountry).setHeader("Country").setAutoWidth(true);
        grid.setItems(query -> sampleAddressService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SAMPLEADDRESS_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(MasterDetailSampleAddressJavahtmlView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(SampleAddress.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.sampleAddress == null) {
                    this.sampleAddress = new SampleAddress();
                }
                binder.writeBean(this.sampleAddress);
                sampleAddressService.update(this.sampleAddress);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(MasterDetailSampleAddressJavahtmlView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> sampleAddressId = event.getRouteParameters().get(SAMPLEADDRESS_ID).map(Long::parseLong);
        if (sampleAddressId.isPresent()) {
            Optional<SampleAddress> sampleAddressFromBackend = sampleAddressService.get(sampleAddressId.get());
            if (sampleAddressFromBackend.isPresent()) {
                populateForm(sampleAddressFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested sampleAddress was not found, ID = %s", sampleAddressId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(MasterDetailSampleAddressJavahtmlView.class);
            }
        }
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(SampleAddress value) {
        this.sampleAddress = value;
        binder.readBean(this.sampleAddress);

    }
}
