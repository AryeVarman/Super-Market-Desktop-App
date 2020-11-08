package order.simpleOrder;

import SDMEngine.*;
import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import mainSDMpage.SDMController;
import order.orderSummery.OrderSummeryController;
import order.selectSales.SelectSalesController;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collection;

import SDMEngine.Utils;
import order.smartOrder.SmartOrderController;

public class SimpleOrderController {

    Pane areaForDisplaying;
    MainSystem mainSystem;
    SDMController sdmController;
    Order order;
    Store store;
    Customer customer;
    LocalDate localDate;

    @FXML private ScrollPane simpleOrderScrollPane;
    @FXML private BorderPane simpleOrderBorderPane;
    @FXML private Text simpleOrderTitle;
    @FXML private GridPane simpleOrderGridPane;
    @FXML private ChoiceBox<String> simpleOrderChooseStoreChooser;
    @FXML private Label simpleOrderDeliveryCostLabel;
    @FXML private DatePicker simpleOrderDatePicker;
    @FXML private TextField simpleOrderQuantityOrWeightTxtArea;
    @FXML private ChoiceBox<String> simpleOrderChooseItemChooser;
    @FXML private Button simpleOrderAddItemBtn;
    @FXML private Button simpleOrderCancelOrderBtn;
    @FXML private Button simpleOrderDoneBtn;
    @FXML private Text simpleOrderOrderSumerryTxt;
    @FXML private Label simpleOrderIllegalQuantityLabel;
    @FXML private Label simpleOrderDateErrorLable;
    @FXML private ImageView cartIcon;
    @FXML private ImageView bagIcon;
    @FXML private CheckBox disableAnimation;

    public SimpleOrderController() { }

    public static SimpleOrderController create(SDMController sdmController) {
        System.out.println(Thread.currentThread().getName() + " creating new simple order controller");

        SimpleOrderController simpleOrderController = null;

        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = SimpleOrderController.class.getResource("resources/simpleOrder.fxml");
            loader.setLocation(FXML);

            Node root = loader.load();
            simpleOrderController = loader.getController();

            simpleOrderController.simpleOrderScrollPane.getStylesheets().add(SimpleOrderController.class.getResource(
                    "resources/simpleOrder" + sdmController.getSystemStyle().toString()).toExternalForm());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return simpleOrderController;
    }

    public void init(Pane areaForDisplaying, MainSystem mainSystem, SDMController sdmController, Customer customer) {
        System.out.println(Thread.currentThread().getName() + " init simple order");

        this.mainSystem = mainSystem;
        this.sdmController = sdmController;
        this.areaForDisplaying = areaForDisplaying;
        this.customer = customer;
        this.order = new Order(this.mainSystem.getOrderSerialNumberForNewOrder(), customer);

        this.initStoreList();

        this.simpleOrderChooseStoreChooser.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.initItemList();
            this.store = this.mainSystem.getStoresList().get(getSerialNumberChosen(this.simpleOrderChooseStoreChooser));
            this.calculateDeliveryCost();
        });
        this.simpleOrderChooseItemChooser.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) { this.simpleOrderAddItemBtn.setDisable(false); }
        });
    }

    private void initStoreList() {
        this.simpleOrderChooseStoreChooser.getItems().remove(0, this.simpleOrderChooseStoreChooser.getItems().size());

        for (Store store : this.mainSystem.getStoresList().values()) {
            this.simpleOrderChooseStoreChooser.getItems().add("#" + store.getSerialNumber() + " Name: " + store.getName());
        }
    }

    private void initItemList() {
        System.out.println(Thread.currentThread().getName() + " init item list");

        int storeNumber = getSerialNumberChosen(this.simpleOrderChooseStoreChooser);
        this.simpleOrderChooseItemChooser.setValue(null);
        this.simpleOrderAddItemBtn.setDisable(true);
        this.simpleOrderChooseItemChooser.getItems().remove(0, this.simpleOrderChooseItemChooser.getItems().size());

        for (StoreItem storeItem : this.mainSystem.getStoresList().get(storeNumber).getItemsList().values() ) {
            this.simpleOrderChooseItemChooser.getItems().add("#" + storeItem.getSerialNumber() + " Name: " + storeItem.getName() +
                    " Price: " + storeItem.getPrice() + "$");
        }
    }

    public Pane getAreaForDisplaying() { return areaForDisplaying; }

    public ScrollPane getSimpleOrderScrollPane() { return simpleOrderScrollPane; }

    @FXML
    void clickedAddItemToOrder(ActionEvent event) {
        boolean valid = true;
        valid = checkValues();

        if(valid) {
            if(this.order.getAmountOfItems() == 0) {
                this.simpleOrderChooseStoreChooser.setDisable(true);
                this.simpleOrderDatePicker.setDisable(true);
                this.simpleOrderDoneBtn.setDisable(false);
            }
            this.simpleOrderAddItemBtn.setDisable(true);
            Thread addItemThread = new Thread(this::runAddItem);
            addItemThread.setName("add item thread");
            addItemThread.start();
        }
    }

    @FXML
    void clickedCancelOrder(ActionEvent event) { this.sdmController.displayMainScreen(); }

    @FXML
    void clickedDone(ActionEvent event) {
        Thread thread = new Thread(() ->  {
        this.order.setSaleDeservedList();

        if(this.order.getSaleDeserved().size() == 0) {
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
        this.localDate = simpleOrderDatePicker.getValue();
        this.order.setDate(this.localDate);
    }

    private void runAddItem() {
        this.order.addOrderItemToOrderList(this.store, this.store.getItemsList().get(getSerialNumberChosen(simpleOrderChooseItemChooser)),
                this.getAmountFromItem(), false);
        Platform.runLater(() -> { this.simpleOrderOrderSumerryTxt.setText( this.order.showOrderSummery()); } );
        Platform.runLater(() -> { this.simpleOrderChooseItemChooser.setValue(null); } );
        Platform.runLater(() -> { this.simpleOrderQuantityOrWeightTxtArea.setText(""); } );
        this.runAnimation();
    }

    private void runAnimation() {
        if (!this.disableAnimation.isSelected()) {
            Platform.runLater(() -> {
                this.bagIcon.setVisible(true);
                Path path = new Path();
                path.getElements().addAll(new MoveTo(this.bagIcon.getX(), this.bagIcon.getY()),
                        new VLineTo(this.cartIcon.getLayoutY() - this.bagIcon.getLayoutY()));
                this.simpleOrderGridPane.getChildren().add(path);

                PathTransition pathTransition = new PathTransition(Duration.millis(1000), path, this.bagIcon);
                pathTransition.setAutoReverse(true);
                pathTransition.setOnFinished((observable) -> {
                    this.bagIcon.setVisible(false);
                });
                pathTransition.play();
            });
        }
    }

    private void calculateDeliveryCost() {
        Double cost = this.order.getDistance(store.getCoordinate()) * store.getPPK();
        this.simpleOrderDeliveryCostLabel.setText(String.format("%.2f$", cost));
        this.simpleOrderDeliveryCostLabel.setVisible(true);
    }

    private boolean checkValues(){
        boolean valid = true;


        if (this.localDate == null || this.localDate.isBefore(LocalDate.now())) {
            this.simpleOrderDateErrorLable.setVisible(true);
            valid = false;
        }
        else { this.simpleOrderDateErrorLable.setVisible(false); }

        String amountTxt = this.simpleOrderQuantityOrWeightTxtArea.getText();

        if (amountTxt.equals("") ||
           !Utils.tryParseStringToDouble(amountTxt) ||
           Double.parseDouble(amountTxt)  <= 0 ||
           (this.getItemType().equals("Quantity") && !Utils.tryParseStringToInt(amountTxt))) {

            this.simpleOrderIllegalQuantityLabel.setVisible(true);
            valid = false;
        }
        else { this.simpleOrderIllegalQuantityLabel.setVisible(false); }

        return valid;
    }

    private int getSerialNumberChosen(ChoiceBox<String> chooser) {
        return Integer.parseInt( chooser.valueProperty().getValue().
                toString().substring(1).split(" ")[0] );
    }

    private String getItemType() {
        int itemNumber = Integer.parseInt(this.simpleOrderChooseItemChooser.valueProperty().getValue().
                toString().substring(1).split(" ")[0]);
        return this.mainSystem.getItemsList().get(itemNumber).getPurchaseMethod();
    }

    private double getAmountFromItem() {
        return Double.parseDouble(this.simpleOrderQuantityOrWeightTxtArea.getText());
    }

}
