package order.orderTypeChooser;

import SDMEngine.Customer;
import SDMEngine.MainSystem;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import mainSDMpage.SDMController;
import order.orderSummery.OrderSummeryController;
import order.simpleOrder.SimpleOrderController;
import order.smartOrder.SmartOrderController;

import java.io.IOException;
import java.net.URL;

public class OrderTypeChooserController {

    Pane areaForDisplaying;
    MainSystem mainSystem;
    SDMController sdmController;
    Customer customer;

    @FXML private VBox chooseOrderTypeVBox;
    @FXML private ChoiceBox<String> customerChoiceBox;
    @FXML private HBox chooseOrderTypeHBox;
    @FXML private Button SimpleOrderBtn;
    @FXML private Button SmartOrderBtn;

    public OrderTypeChooserController() { }

    public static OrderTypeChooserController create(SDMController sdmController) {
        OrderTypeChooserController orderTypeChooserController = null;

        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = OrderTypeChooserController.class.getResource("resources/orderTypeChooser.fxml");
            loader.setLocation(FXML);

            Node root = loader.load();
            orderTypeChooserController = loader.getController();

            orderTypeChooserController.chooseOrderTypeVBox.getStylesheets().add(OrderTypeChooserController.class.getResource(
                    "resources/orderTypeChooser" + sdmController.getSystemStyle().toString()).toExternalForm());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return orderTypeChooserController;
    }

    public void init(Pane areaForDisplaying, MainSystem mainSystem, SDMController sdmController) {
        this.areaForDisplaying = areaForDisplaying;
        this.mainSystem = mainSystem;
        this.sdmController = sdmController;

        initCustomerList();

        this.customerChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            Thread thread = new Thread(this::runSetOrderCustomer);
            thread.start();
        });
    }

    private void initCustomerList() {
        this.customerChoiceBox.getItems().remove(0, this.customerChoiceBox.getItems().size());

        for (Customer customer : this.mainSystem.getCustomersList().values()) {
            this.customerChoiceBox.getItems().add("#" + customer.getSerialNumber() + " Name: " + customer.getName());
        }
    }

    private void runSetOrderCustomer() {

        int customerNumber = this.getSerialNumberChosen(this.customerChoiceBox);
        this.customer = this.mainSystem.getCustomersList().get(customerNumber);
        Platform.runLater(() -> this.SimpleOrderBtn.setDisable(false));
        Platform.runLater(() -> this.SmartOrderBtn.setDisable(false));
    }

    public VBox getChooseOrderTypeVBox() { return chooseOrderTypeVBox; }

    public Button getSimpleOrderBtn() { return SimpleOrderBtn; }

    @FXML
    void clickedSimpleOrderBtn(ActionEvent event) {
        Thread thread = new Thread(this::runMakeSimpleOrder);
        thread.setName("simple order thread");
        thread.start();
    }

    @FXML
    void clickedSmartOrderBtn(ActionEvent event) {
        Thread thread = new Thread(this::rumMakeSmartOrder);
        thread.setName("smart order thread");
        thread.start();
    }

    private void runMakeSimpleOrder() {
        SimpleOrderController simpleOrderController = SimpleOrderController.create(this.sdmController);
        simpleOrderController.init(this.areaForDisplaying, this.mainSystem, this.sdmController, this.customer);
        Platform.runLater(() -> sdmController.displayOnScreen(simpleOrderController.getSimpleOrderScrollPane()));
    }

    private void rumMakeSmartOrder() {
        SmartOrderController smartOrderController = SmartOrderController.create(this.sdmController);
        smartOrderController.init(this.areaForDisplaying, this.mainSystem, this.sdmController, this.customer);
        Platform.runLater(() -> sdmController.displayOnScreen(smartOrderController.getSmartOrderScrollPane()));
    }

    private int getSerialNumberChosen(ChoiceBox<String> chooser) {
        return Integer.parseInt( chooser.valueProperty().getValue().
                toString().substring(1).split(" ")[0] );
    }
}
