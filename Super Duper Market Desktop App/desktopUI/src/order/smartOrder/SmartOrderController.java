package order.smartOrder;

import SDMEngine.*;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.VLineTo;
import javafx.scene.text.Text;
import javafx.util.Duration;
import mainSDMpage.SDMController;
import map.map.MapController;
import order.orderSummery.OrderSummeryController;
import order.selectSales.SelectSalesController;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

public class SmartOrderController {

    Pane areaForDisplaying;
    MainSystem mainSystem;
    SDMController sdmController;
    Order order;
    Customer customer;
    LocalDate localDate;

    @FXML private ScrollPane smartOrderScrollPane;
    @FXML private BorderPane smartOrderBorderPane;
    @FXML private Text smartOrderTitle;
    @FXML private GridPane smartOrderGridPane;
    @FXML private DatePicker smartOrderDatePicker;
    @FXML private TextField smartOrderQuantityOrWeightTxtArea;
    @FXML private ChoiceBox<String> smartOrderChooseItemChooser;
    @FXML private Button smartOrderAddItemBtn;
    @FXML private Button smartOrderCancelOrderBtn;
    @FXML private Button smartOrderDoneBtn;
    @FXML private Text smartOrderOrderSumerryTxt;
    @FXML private Label smartOrderIllegalQuantityLabel;
    @FXML private Label smartOrderDateErrorLable;
    @FXML private Text storeListTxt;
    @FXML private ImageView cartIcon;
    @FXML private ImageView bagIcon;
    @FXML private CheckBox disableAnimation;

    public SmartOrderController() {
    }

    public static SmartOrderController create(SDMController sdmController) {
        System.out.println(Thread.currentThread().getName() + " creating new smart order controller");

        SmartOrderController smartOrderController = null;

        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = SmartOrderController.class.getResource("resources/smartOrder.fxml");
            loader.setLocation(FXML);
            Node root = loader.load();
            smartOrderController = loader.getController();

            smartOrderController.smartOrderScrollPane.getStylesheets().add(SmartOrderController.class.getResource(
                    "resources/smartOrder"  + sdmController.getSystemStyle().toString()).toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return smartOrderController;
    }

    public void init(Pane areaForDisplaying, MainSystem mainSystem, SDMController sdmController, Customer customer) {
        System.out.println(Thread.currentThread().getName() + " init smart order");

        this.mainSystem = mainSystem;
        this.sdmController = sdmController;
        this.areaForDisplaying = areaForDisplaying;
        this.customer = customer;
        this.order = new Order(this.mainSystem.getOrderSerialNumberForNewOrder(), customer);

        this.initItemList();

        this.smartOrderChooseItemChooser.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.smartOrderAddItemBtn.setDisable(false);
            }
        });
    }

    private void initItemList() {
        System.out.println(Thread.currentThread().getName() + " init item list");

        this.smartOrderChooseItemChooser.setValue(null);
        this.smartOrderAddItemBtn.setDisable(true);
        this.smartOrderChooseItemChooser.getItems().remove(0, this.smartOrderChooseItemChooser.getItems().size());

        for (Item item : this.mainSystem.getItemsList().values()) {
            this.smartOrderChooseItemChooser.getItems().add("#" + item.getSerialNumber() + " Name: " + item.getName());
        }
    }

    public Pane getAreaForDisplaying() { return areaForDisplaying; }

    public ScrollPane getSmartOrderScrollPane() { return smartOrderScrollPane; }

    @FXML
    void clickedAddItemToOrder(ActionEvent event) {
        boolean valid = true;
        valid = checkValues();

        if (valid) {
            if (this.order.getAmountOfItems() == 0) {
                this.smartOrderDatePicker.setDisable(true);
                this.smartOrderDoneBtn.setDisable(false);
            }
            this.smartOrderAddItemBtn.setDisable(true);
            Thread addItemThread = new Thread(this::runAddItem);
            addItemThread.setName("add item thread");
            addItemThread.start();
        }
    }

    @FXML
    void clickedCancelOrder(ActionEvent event) { this.sdmController.displayMainScreen(); }

    @FXML
    void clickedDone(ActionEvent event) {
        Thread thread = new Thread(() -> {

            this.order.setSaleDeservedList();

            if (this.order.getSaleDeserved().size() == 0) {
                OrderSummeryController orderSummeryController = OrderSummeryController.create(this.sdmController);
                orderSummeryController.init(this.order, this.sdmController, this.mainSystem);
                Platform.runLater(() -> sdmController.displayOnScreen(orderSummeryController.getMainBorderPain()));
            }
            else {
                SelectSalesController selectSalesController = SelectSalesController.create(this.sdmController);
                selectSalesController.init(this.order, this.areaForDisplaying, this.mainSystem, this.sdmController);
                Platform.runLater(() -> sdmController.displayOnScreen(selectSalesController.getScrollpane()));
            }
        });

        thread.setName("sales thread");
        thread.start();
    }

    @FXML
    void clickedDatePicker(ActionEvent event) {
        this.localDate = smartOrderDatePicker.getValue();
        this.order.setDate(this.localDate);
    }

    private void runAddItem() {
       int itemId = getSerialNumberChosen(smartOrderChooseItemChooser);
       double amountFromItem = this.getAmountFromItem();
       try {
           Platform.runLater(() -> sdmController.createProgressTaskControllerForSmartOrder());
           Platform.runLater(() -> mainSystem.startCalculateOrderTask(() -> onFinish()));
           this.mainSystem.addItemToOrderFromCheapestStore(itemId, amountFromItem, this.order);

       }
       catch (Exception ex) {
           Platform.runLater(() -> this.sdmController.getSdmGui().showMessageDialog(ex.getMessage()));
       }
    }

    private void onFinish(){

        Platform.runLater(()->sdmController.getSecondStage().close());
        this.runAnimation();
        Platform.runLater(this::showStoreDeliveryDetails);

        Platform.runLater(() -> {
            this.smartOrderOrderSumerryTxt.setText(this.order.showOrderSummery());
        });
        Platform.runLater(() -> {
            this.smartOrderChooseItemChooser.setValue(null);
        });
        Platform.runLater(() -> {
            this.smartOrderQuantityOrWeightTxtArea.setText("");
        });
    }

    private void runAnimation() {
        if (!this.disableAnimation.isSelected()) {
            Platform.runLater(() -> {
                this.bagIcon.setVisible(true);
                Path path = new Path();
                path.getElements().addAll(new MoveTo(this.bagIcon.getX(), this.bagIcon.getY()),
                        new VLineTo(this.cartIcon.getLayoutY() - this.bagIcon.getLayoutY()));
                this.smartOrderGridPane.getChildren().add(path);

                PathTransition pathTransition = new PathTransition(Duration.millis(1000), path, this.bagIcon);
                pathTransition.setAutoReverse(true);
                pathTransition.setOnFinished((observable) -> {
                    this.bagIcon.setVisible(false);
                });
                pathTransition.play();
            });
        }
    }

    private void showStoreDeliveryDetails() {
        this.storeListTxt.setText("");

        for (Store store : this.order.getOrderStoresList()) {
            double distance = store.getDistanceFromStore(this.order.getCustomer().getCoordinate());
            double ppk = store.getPPK();
            double deliveryCost = ppk * distance;

            String stringToAdd = store.getName() + String.format(" Distance: %.2f | PPK: %.2f$ | Delivery Price: %.2f$\n",
                    distance, ppk, deliveryCost);

            this.storeListTxt.setText(this.storeListTxt.getText().concat(stringToAdd));
        }
    }

    private boolean checkValues() {
        boolean valid = true;

        if (this.localDate == null || this.localDate.isBefore(LocalDate.now())) {
            this.smartOrderDateErrorLable.setVisible(true);
            valid = false;
        } else {
            this.smartOrderDateErrorLable.setVisible(false);
        }

        String amountTxt = this.smartOrderQuantityOrWeightTxtArea.getText();

        if (amountTxt.equals("") ||
                !Utils.tryParseStringToDouble(amountTxt) ||
                Double.parseDouble(amountTxt)  <= 0 ||
                (this.getItemType().equals("Quantity") && !Utils.tryParseStringToInt(amountTxt))) {

            this.smartOrderIllegalQuantityLabel.setVisible(true);
            valid = false;
        }
        else { this.smartOrderIllegalQuantityLabel.setVisible(false); }

        return valid;
    }

    private int getSerialNumberChosen(ChoiceBox<String> chooser) {
        return Integer.parseInt(chooser.valueProperty().getValue().
                toString().substring(1).split(" ")[0]);
    }


    private String getItemType() {
        int itemNumber = Integer.parseInt(this.smartOrderChooseItemChooser.valueProperty().getValue().
                toString().substring(1).split(" ")[0]);
        return this.mainSystem.getItemsList().get(itemNumber).getPurchaseMethod();
    }

    private double getAmountFromItem() { return Double.parseDouble(this.smartOrderQuantityOrWeightTxtArea.getText()); }
}
