package updateStoreItems.deleteItem;

import SDMEngine.MainSystem;
import SDMEngine.Store;
import SDMEngine.StoreItem;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import mainSDMpage.SDMController;

public class DeleteItemController {

    @FXML private VBox mainVBox;
    @FXML private ComboBox<String> itemsComboBox;
    @FXML private Button deleteBtn;
    @FXML private Button cancelBtn;
    @FXML private ScrollPane mainScrollPane;

    private ScrollPane prevWindow;
    private BorderPane mainBorderPane;
    private MainSystem mainSystem;
    private SDMController sdmController;
    private Store selectedStore;
    private int itemID;

    @FXML
    private void initialize(){
        deleteBtn.disableProperty().bind(itemsComboBox.valueProperty().isNull());

        itemsComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(itemsComboBox.valueProperty().isNotNull().get()) {
                itemID = Integer.parseInt(itemsComboBox.valueProperty().getValue().
                        substring(1).split(" ")[0]);
            }
        });
    }

    @FXML
    void onClickCancelBtn(ActionEvent event) {
        sdmController.getSdmGui().showOnScreen(prevWindow,mainBorderPane);
    }

    @FXML
    void onClickDeleteBtn(ActionEvent event) {
        Thread thread = new Thread(this::runDeleteItem);
        thread.setName("Delete Item thread");
        thread.start();
    }

    public void init(BorderPane mainBorderPane, MainSystem mainSystem, SDMController sdmController,int storeId,ScrollPane prevWindow) {
        this.mainBorderPane = mainBorderPane;
        this.mainSystem = mainSystem;
        this.sdmController = sdmController;
        this.selectedStore = mainSystem.getStoresList().get(storeId);
        this.prevWindow = prevWindow;

        initItemsListComboBox();

        sdmController.displayOnScreen(mainScrollPane);
    }

    private void initItemsListComboBox() {
        for (StoreItem item : selectedStore.getItemsList().values()) {
            itemsComboBox.getItems().add("#" + item.getSerialNumber() + " " + item.getName());
        }
    }

    private void runDeleteItem(){
        try {
            String deletedSalesMessage = mainSystem.removeItemFromStore(selectedStore.getSerialNumber(),itemID);
            Platform.runLater(()->sdmController.getSdmGui().showMessageDialog("The item deleted successfully!\n" + deletedSalesMessage));
            Platform.runLater(()->refreshPage());
        } catch (Exception e) {
            Platform.runLater(()->sdmController.getSdmGui().showMessageDialog(e.getMessage()));
            System.out.println(e.getMessage());
        }
    }

    private void refreshPage() {
        itemsComboBox.getItems().remove(0,itemsComboBox.getItems().size());
        initItemsListComboBox();
    }

    public ScrollPane getMainScrollPane() { return mainScrollPane; }
}
