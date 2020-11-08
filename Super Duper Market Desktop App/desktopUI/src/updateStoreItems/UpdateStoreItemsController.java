package updateStoreItems;

import SDMEngine.MainSystem;
import SDMEngine.Store;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import mainSDMpage.SDMController;
import updateStoreItems.addNewItem.AddNewItemController;
import updateStoreItems.addNewSale.AddNewSaleController;
import updateStoreItems.changeItemPrice.ChangeItemPriceController;
import updateStoreItems.deleteItem.DeleteItemController;

import java.io.IOException;
import java.net.URL;

public class UpdateStoreItemsController {

    @FXML private ComboBox<String> storesComboBox;
    @FXML private Button addNewItemChooserBtn;
    @FXML private Button changeItemPriceChooserBtn;
    @FXML private Button deleteItemChooserBtn;
    @FXML private Button addNewSaleChooserBtn;
    @FXML private GridPane gridPane;
    @FXML private ScrollPane scrollPane;

    private AddNewSaleController addNewSaleController = new AddNewSaleController();
    private DeleteItemController deleteItemController = new DeleteItemController();
    private AddNewItemController addNewItemController = new AddNewItemController();
    private ChangeItemPriceController changeItemPriceController = new ChangeItemPriceController();
    private StringProperty storeBeenChooseStr = new SimpleStringProperty();
    private BorderPane mainBorderPane;
    private MainSystem mainSystem;
    private SDMController sdmController;
    private int storeID;

    @FXML
    private void initialize() {
        storeBeenChooseStr.bind(storesComboBox.valueProperty());
        addNewItemChooserBtn.disableProperty().bind(storesComboBox.valueProperty().isNull());
        changeItemPriceChooserBtn.disableProperty().bind(storesComboBox.valueProperty().isNull());
        deleteItemChooserBtn.disableProperty().bind(storesComboBox.valueProperty().isNull());
        addNewSaleChooserBtn.disableProperty().bind(storesComboBox.valueProperty().isNull());
        storesComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            storeID = Integer.parseInt( storesComboBox.valueProperty().getValue().
                    substring(1).split(" ")[0] );
        });
    }

    @FXML
    void onClickAddNewItemChooserBtn(ActionEvent event) {
        Thread thread = new Thread(this::runAddNewItem);
        thread.setName("Add New Item thread");
        thread.start();
    }

    @FXML
    void onClickChangeItemPriceChooserBtn(ActionEvent event) {
        Thread thread = new Thread(this::runChangeItemPrice);
        thread.setName("Chang item price thread");
        thread.start();
    }

    @FXML
    void onClickDeleteItemChooserBtn(ActionEvent event) {
        Thread thread = new Thread(this::runDeleteItem);
        thread.setName("Delete item thread");
        thread.start();
    }

    @FXML
    void onClickAddNewSaleChooserBtn(ActionEvent event) {
        Thread thread = new Thread(this::runAddNewSale);
        thread.setName("Add New Sale thread");
        thread.start();
    }

    @FXML
    void clickedCancel(ActionEvent event) { sdmController.displayMainScreen(); }

    private void runDeleteItem() {
        createDeleteItemController();
        deleteItemController.init(mainBorderPane,mainSystem,sdmController,storeID,scrollPane);
    }

    private void runAddNewSale() {
        createAddNewSaleController();
        addNewSaleController.init(mainBorderPane,mainSystem,sdmController,storeID,scrollPane);
    }

    private void createAddNewSaleController() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = AddNewSaleController.class.getResource("resources/addNewSalePage.fxml");
            loader.setLocation(FXML);
            Node root = loader.load();
            addNewSaleController = loader.getController();

            addNewSaleController.getMainScrollPane().getStylesheets().add(
                    UpdateStoreItemsController.class.getResource(
                            "resources/updateStoreItem" + sdmController.getSystemStyle().toString()).toExternalForm());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDeleteItemController() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = DeleteItemController.class.getResource("resources/deleteItemPage.fxml");
            loader.setLocation(FXML);
            Node root = loader.load();
            deleteItemController = loader.getController();

            deleteItemController.getMainScrollPane().getStylesheets().add(
                    UpdateStoreItemsController.class.getResource(
                            "resources/updateStoreItem" + sdmController.getSystemStyle().toString()).toExternalForm());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runChangeItemPrice() {
        createChangeItemPriceController();
        changeItemPriceController.init(mainBorderPane,mainSystem,sdmController,storeID,scrollPane);
    }

    private void createChangeItemPriceController() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = ChangeItemPriceController.class.getResource("resources/changeItemPricePage.fxml");
            loader.setLocation(FXML);
            Node root = loader.load();
            changeItemPriceController = loader.getController();

            changeItemPriceController.getMainScrollPane().getStylesheets().add(
                    UpdateStoreItemsController.class.getResource(
                            "resources/updateStoreItem" + sdmController.getSystemStyle().toString()).toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init(BorderPane mainBorderPane, MainSystem mainSystem, SDMController sdmController) {
        this.mainBorderPane = mainBorderPane;
        this.mainSystem = mainSystem;
        this.sdmController = sdmController;

        initStoreListComboBox();

        sdmController.displayOnScreen(scrollPane);

    }

    private void initStoreListComboBox() {
        for (Store store:mainSystem.getStoresList().values()) {
            storesComboBox.getItems().add("#" + store.getSerialNumber() + " " + store.getName());
        }
    }

    private void runAddNewItem() {
        createAddNewItemController();
        addNewItemController.init(mainBorderPane, mainSystem, sdmController, storeID, scrollPane);
    }

    public ScrollPane getScrollPane() { return scrollPane; }

    private void createAddNewItemController() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = AddNewItemController.class.getResource("resources/addNewItemPage.fxml");
            loader.setLocation(FXML);
            Node root = loader.load();
            addNewItemController = loader.getController();

            addNewItemController.getMainScrollPane().getStylesheets().add(
                    UpdateStoreItemsController.class.getResource(
                            "resources/updateStoreItem" + sdmController.getSystemStyle().toString()).toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}