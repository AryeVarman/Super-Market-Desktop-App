package systemUpdates.addStore;

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

public class AddStoreController {

    MainSystem mainSystem;
    SDMController sdmController;
    Pane areaForDisplaying;
    Map<Integer, StoreItem> itemList;
    String storeName;
    int x;
    int y;

    @FXML private ScrollPane scrollPane;
    @FXML private BorderPane borderPane;
    @FXML private GridPane gridPane;
    @FXML private TextField serialNumberTextField;
    @FXML private TextField storeNameTextField;
    @FXML private ChoiceBox<Integer> locationXChooser;
    @FXML private ChoiceBox<Integer> locationYChooser;
    @FXML private TextField PPKTextField;
    @FXML private ChoiceBox<String> itemChooser;
    @FXML private TextField itemPriceTextField;
    @FXML private Button addStoreItem;
    @FXML private Button addItemBtn;
    @FXML private Text itemSummeryTxt;
    @FXML private Text serialNumbetErorTxt;
    @FXML private Text locationErrorTxt;
    @FXML private Text PPKErrorTxt;
    @FXML private Text priceErrorTxt;
    @FXML private Text nameErrorTxt;

    public AddStoreController() { }

    public static AddStoreController create(SDMController sdmController) {
        AddStoreController addStoreController = null;

        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = AddStoreController.class.getResource("resources/addStore.fxml");
            loader.setLocation(FXML);

            Node root = loader.load();
            addStoreController = loader.getController();

            addStoreController.scrollPane.getStylesheets().add(
                    AddStoreController.class.getResource(
                            "resources/addStore" + sdmController.getSystemStyle().toString()).toExternalForm());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return addStoreController;
    }

    public void init(MainSystem mainSystem, SDMController sdmController, Pane areaForDisplaying) {
        this.mainSystem = mainSystem;
        this.sdmController = sdmController;
        this.areaForDisplaying = areaForDisplaying;
        this.itemList = new HashMap<>();
        this.storeName = "";

        this.initItemList();
        this.initLocationChooser();

        itemChooser.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!itemChooser.valueProperty().getValue().equals("")) {
                this.addItemBtn.setDisable(false);
            }});

        locationXChooser.valueProperty().addListener((observable, oldValue, newValue) -> {this.x = newValue.intValue();});
        locationYChooser.valueProperty().addListener((observable, oldValue, newValue) -> {this.y = newValue.intValue();});

    }

    private void initItemList() {
        System.out.println(Thread.currentThread().getName() + " init item list");

        this.itemChooser.setValue(null);
        this.addItemBtn.setDisable(true);
        this.itemChooser.getItems().remove(0, this.itemChooser.getItems().size());

        for (Item item : this.mainSystem.getItemsList().values()) {
            this.itemChooser.getItems().add("#" + item.getSerialNumber() + " Name: " + item.getName());
        }
    }

    private void initLocationChooser() {
        System.out.println(Thread.currentThread().getName() + " init location list");

        locationXChooser.setValue(null);
        locationXChooser.getItems().remove(0, this.locationXChooser.getItems().size());
        locationYChooser.setValue(null);
        locationYChooser.getItems().remove(0, this.locationXChooser.getItems().size());

        for (int i = 1; i <= mainSystem.getCoordinateMap().getMAX_COL(); i++) {
            locationXChooser.getItems().add(i);
        }
        for (int i = 1; i <= mainSystem.getCoordinateMap().getMAX_ROW(); i++) {
            locationYChooser.getItems().add(i);
        }
    }

    @FXML
    void clickedAddItem(ActionEvent event) {
        if(this.checkAddItemValues()) {
            int itemSerialnumber = getSerialNumberChosen(this.itemChooser);
            double itemPrice = getPriceForItem(this.itemPriceTextField);

            if(!itemList.containsKey(itemSerialnumber)) {
                Item item = this.mainSystem.getItemsList().get(itemSerialnumber);

                itemList.put(itemSerialnumber, new StoreItem(item, itemPrice));
                this.itemSummeryTxt.setText(String.format("%S\nItem: %S Price: %.2f",
                        this.itemSummeryTxt.getText(), item.getName(), itemPrice));

                this.addStoreItem.setDisable(false);
            }
            else {
                sdmController.displayMessageDialog("Item Already been chosen");
            }

            this.addItemBtn.setDisable(true);
            this.itemPriceTextField.setText("");
            this.itemChooser.getItems().remove(this.itemChooser.getValue());
            this.itemChooser.valueProperty().setValue("");
        }
    }

    @FXML
    void clickedAddStore(ActionEvent event) {
        if (this.checkAddStoreValues()) {
            Thread thread = new Thread(this::runAddStore);
            thread.setName("add store thread");
            thread.start();
        }
    }

    @FXML
    void clickedCancel(ActionEvent event) { sdmController.displayMainScreen(); }

    private boolean checkAddStoreValues() {
        boolean serialValid = true;
        boolean locationValid = true;
        boolean ppkValid = true;
        boolean nameValid = true;


        String serialNumStr = this.serialNumberTextField.getText();
        if (!Utils.isPositiveDouble(serialNumStr)) {
            serialNumbetErorTxt.setText("serial number must be positive");
            serialNumbetErorTxt.setVisible(true);
            serialValid = false;
        }
        if (!Utils.tryParseStringToInt(serialNumStr)) {
            serialNumbetErorTxt.setText("serial number most be hole number");
            serialNumbetErorTxt.setVisible(true);
            serialValid = false;
        }
        if (serialValid && this.mainSystem.getStoresList().containsKey(Integer.parseInt(serialNumStr))) {
            serialNumbetErorTxt.setText("serial number is taken by another store");
            serialNumbetErorTxt.setVisible(true);
            serialValid = false;
        }
        if(serialValid) { serialNumbetErorTxt.setVisible(false); }

        int row = locationYChooser.getValue(), col = locationXChooser.getValue();

        if(this.mainSystem.getCoordinateMap().isCoordinateTaken(col, row)) {
            locationErrorTxt.setText("this location is taken");
            locationErrorTxt.setVisible(true);
            locationValid = false;
        }
        else { locationErrorTxt.setVisible(false); }


        if (!Utils.tryParseStringToDouble(this.PPKTextField.getText())) {
            PPKErrorTxt.setText("PPK must be a number");
            PPKErrorTxt.setVisible(true);
            ppkValid = false;
        }
        if(ppkValid && !Utils.isNotNegativeDouble(this.PPKTextField.getText())) {
            PPKErrorTxt.setText("PPK can't be negative");
            PPKErrorTxt.setVisible(true);
            ppkValid = false;
        }
        if (ppkValid) { PPKErrorTxt.setVisible(false); }

        if (this.storeNameTextField.getText() == null || this.storeNameTextField.getText().equals("")) {
            this.nameErrorTxt.setVisible(true);
            nameValid = false;
        }
        else { this.nameErrorTxt.setVisible(false); }


        return serialValid && locationValid && ppkValid;
    }

    private void runAddStore() {
        try {
            this.mainSystem.addStoreToSystemDynamically(new Store(
                    Integer.parseInt(serialNumberTextField.getText()), storeNameTextField.getText(), this.itemList,
                    new Coordinate(this.x, this.y), Double.parseDouble(this.PPKTextField.getText())));

            sdmController.displayMessageDialog("Store Added Successfully");
            sdmController.createAndDisplayMap();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            sdmController.displayMessageDialog(ex.getMessage());
        }
    }

    public ScrollPane getScrollPane() { return scrollPane; }

    private boolean checkAddItemValues() {
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

    private int getSerialNumberChosen(ChoiceBox<String> chooser) {
        return Integer.parseInt(chooser.valueProperty().getValue().
                toString().substring(1).split(" ")[0]);
    }
}