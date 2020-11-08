package systemUpdates.addItem;

import SDMEngine.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import mainSDMpage.SDMController;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AddItemController {

    MainSystem mainSystem;
    SDMController sdmController;
    Pane areaForDisplaying;
    Map<Integer, Double> storeList;
    String purchaseMethod;

    @FXML private ScrollPane scrollPane;
    @FXML private BorderPane borderPane;
    @FXML private GridPane gridPane;
    @FXML private TextField serialNumberTextField;
    @FXML private TextField itemNameTextField;
    @FXML private ChoiceBox<String> storeChooser;
    @FXML private TextField itemPriceTextField;
    @FXML private Button addItemToStoreBtn;
    @FXML private Button addItemBtn;
    @FXML private Text storeThatSellTxt;
    @FXML private Text serialNumberErrorTxt;
    @FXML private Text priceErrorTxt;
    @FXML private ChoiceBox<String> purchaseMethodChooser;
    @FXML private Text purchaseMethodErrorTxt;
    @FXML private Text nameErrorTxt;

    public AddItemController() { }

    public static AddItemController create(SDMController sdmController) {
        AddItemController addItemController = null;

        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = AddItemController.class.getResource("resources/addItem.fxml");
            loader.setLocation(FXML);

            Node root = loader.load();
            addItemController = loader.getController();

            addItemController.scrollPane.getStylesheets().add(
                    AddItemController.class.getResource(
                            "resources/addItem" + sdmController.getSystemStyle().toString()).toExternalForm());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return addItemController;
    }

    public void init(MainSystem mainSystem, SDMController sdmController, Pane areaForDisplaying) {
        this.mainSystem = mainSystem;
        this.sdmController = sdmController;
        this.areaForDisplaying = areaForDisplaying;
        this.storeList = new HashMap<>();
        this.purchaseMethod = "";

        this.initStoreList();
        this.initPurchaseMethodChooser();

        storeChooser.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!storeChooser.valueProperty().getValue().equals("")) {
                this.addItemToStoreBtn.setDisable(false);
            }});

        purchaseMethodChooser.valueProperty().addListener((observable, oldValue, newValue) -> {this.purchaseMethod = newValue.toString();});
    }

    private void initStoreList() {
        System.out.println(Thread.currentThread().getName() + " init store list");

        this.storeChooser.setValue(null);
        this.addItemToStoreBtn.setDisable(true);
        this.storeChooser.getItems().remove(0, this.storeChooser.getItems().size());

        for (Store store : this.mainSystem.getStoresList().values()) {
            this.storeChooser.getItems().add("#" + store.getSerialNumber() + " Name: " + store.getName());
        }
    }

    private void initPurchaseMethodChooser() {
        System.out.println(Thread.currentThread().getName() + " init purchase method list");

        this.purchaseMethodChooser.setValue(null);
        this.purchaseMethodChooser.getItems().remove(0, this.purchaseMethodChooser.getItems().size());

        this.purchaseMethodChooser.getItems().add("Quantity");
        this.purchaseMethodChooser.getItems().add("Weight");
    }

    @FXML
    void clickedAddToStore(ActionEvent event) {
        if(this.checkAddItemToStoreValues()) {
            int storeSerialnumber = getSerialNumberChosen(this.storeChooser);
            double itemPrice = getPriceForItem(this.itemPriceTextField);

            if(!storeList.containsKey(storeSerialnumber)) {
                Store store = this.mainSystem.getStoresList().get(storeSerialnumber);

                this.storeList.put(storeSerialnumber, itemPrice);
                this.storeThatSellTxt.setText(String.format("%S\nStore: %S Price: %.2f",
                        this.storeThatSellTxt.getText(), store.getName(), itemPrice));

                this.addItemBtn.setDisable(false);
            }
            else {
                sdmController.displayMessageDialog("Item Already been chosen");
            }

            this.addItemToStoreBtn.setDisable(true);
            this.itemPriceTextField.setText("");
            this.storeChooser.getItems().remove(this.storeChooser.getValue());
            this.storeChooser.valueProperty().setValue("");
        }
    }

    @FXML
    void clickedAddItem(ActionEvent event) {
        if (this.checkAddItemToSystemValues()) {
            Thread thread = new Thread(this::runAddItemToSystem);
            thread.setName("add store thread");
            thread.start();
        }
    }

    @FXML
    void clickedCancel(ActionEvent event) { sdmController.displayMainScreen(); }

    private void runAddItemToSystem() {
        try {
            if (this.purchaseMethod.toLowerCase().equals("quantity")) {
                this.mainSystem.addItemToSystemDynamically(
                        new ItemByQuantity(
                                Integer.parseInt(this.serialNumberTextField.getText()), this.itemNameTextField.getText()), this.storeList);
            }
            else {
                this.mainSystem.addItemToSystemDynamically(
                        new ItemByWeight(
                                Integer.parseInt(this.serialNumberTextField.getText()), this.itemNameTextField.getText()), this.storeList);
            }

            sdmController.displayMessageDialog("Item Added Successfully");
            sdmController.displayMainScreen();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            this.sdmController.displayMessageDialog(ex.getMessage());
        }
    }

    private boolean checkAddItemToSystemValues() {
        boolean serialValid = true, purchaseMethodValid = true, nameValid = true;

        String serialNumStr = this.serialNumberTextField.getText();

        if (!Utils.isPositiveDouble(serialNumStr)) {
            this.serialNumberErrorTxt.setText("serial number must be positive");
            this.serialNumberErrorTxt.setVisible(true);
            serialValid = false;
        }
        if (!Utils.tryParseStringToInt(serialNumStr)) {
            this.serialNumberErrorTxt.setText("serial number must be hole number");
            this.serialNumberErrorTxt.setVisible(true);
            serialValid = false;
        }
        if (serialValid && this.mainSystem.getItemsList().containsKey(Integer.parseInt(serialNumStr))) {
            this.serialNumberErrorTxt.setText("serial number is taken by another store");
            this.serialNumberErrorTxt.setVisible(true);
            serialValid = false;
        }
        if(serialValid) { this.serialNumberErrorTxt.setVisible(false); }

        if(purchaseMethodChooser.getValue() == null) {
            this.purchaseMethodErrorTxt.setVisible(true);
            purchaseMethodValid = false;
        }
        else {
            this.purchaseMethodErrorTxt.setVisible(false);
            this.purchaseMethod = this.purchaseMethodChooser.getValue();
        }

        if (this.itemNameTextField.getText().equals("")) {
           this.nameErrorTxt.setVisible(true);
           nameValid = false;
        }
        else { this.nameErrorTxt.setVisible(false); }

        return serialValid && purchaseMethodValid && nameValid;
    }

    private boolean checkAddItemToStoreValues() {
        boolean valid = true;

        if (!Utils.tryParseStringToDouble(this.itemPriceTextField.getText())) {
            priceErrorTxt.setText("price must be a number");
            priceErrorTxt.setVisible(true);
            valid = false;
        }
        else if(valid && !Utils.isPositiveDouble(this.itemPriceTextField.getText())) {
            priceErrorTxt.setText("price must be positive");
            priceErrorTxt.setVisible(true);
            valid = false;
        }
        else { priceErrorTxt.setVisible(false); }

        return valid;
    }

    private double getPriceForItem(TextField textField) { return Double.parseDouble(textField.getText()); }

    public ScrollPane getScrollPane() { return scrollPane; }

    private int getSerialNumberChosen(ChoiceBox<String> chooser) {
        return Integer.parseInt(chooser.valueProperty().getValue().
                toString().substring(1).split(" ")[0]);
    }

}