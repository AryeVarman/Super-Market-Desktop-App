package updateStoreItems.changeItemPrice;

import SDMEngine.MainSystem;
import SDMEngine.Store;
import SDMEngine.StoreItem;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import mainSDMpage.SDMController;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class ChangeItemPriceController {

    @FXML private VBox mainVBox;
    @FXML private ComboBox<String> itemsComboBox;
    @FXML private TextField priceText;
    @FXML private Button submitBtn;
    @FXML private Button cancelBtn;
    @FXML private ScrollPane mainScrollPane;

    private ScrollPane prevWindow;
    private BorderPane mainBorderPane;
    private MainSystem mainSystem;
    private SDMController sdmController;
    private Store selectedStore;
    private Double itemPrice;
    private int itemID;

    @FXML
    private void initialize(){
        submitBtn.disableProperty().bind(itemsComboBox.valueProperty().isNull().or(priceText.textProperty().isEmpty()));

        setNumbersFilterOnTextField();

        priceText.textProperty().addListener(L->{
            if(priceText.getText().equals("")){
                itemPrice = 0.0;
            }
            else {
                itemPrice = Double.parseDouble(priceText.getText());
            }
        });

        itemsComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(itemsComboBox.valueProperty().isNotNull().get()) {
                itemID = Integer.parseInt(itemsComboBox.valueProperty().getValue().
                        substring(1).split(" ")[0]);
            }
        });

        priceText.setText("0.0");

    }

    private void setNumbersFilterOnTextField() {
        Pattern validEditingState = Pattern.compile("(([1-9][0-9]*)|0)?(\\.[0-9]*)?");

        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c ;
            } else {
                return null ;
            }
        };

        StringConverter<Double> converter = new StringConverter<Double>() {

            @Override
            public Double fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                    return 0.0 ;
                } else {
                    return Double.valueOf(s);
                }
            }


            @Override
            public String toString(Double d) {
                return d.toString();
            }
        };

        TextFormatter<Double> textFormatter = new TextFormatter<>(converter, 0.0, filter);
        priceText.setTextFormatter(textFormatter);

    }

    @FXML
    void onClickCancelBtn(ActionEvent event) {
        sdmController.getSdmGui().showOnScreen(prevWindow,mainBorderPane);
    }

    @FXML
    void onClickSubmitBtn(ActionEvent event) {
        Thread thread = new Thread(this::runSubmitChangeItemPrice);
        thread.setName("Submit Add New Item thread");
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

    private void runSubmitChangeItemPrice(){
        try {
            mainSystem.changeItemPriceInStore(selectedStore.getSerialNumber(),itemID,itemPrice);
            Platform.runLater(()->sdmController.getSdmGui().showMessageDialog("The item price changed successfully!"));
            Platform.runLater(()->refreshPage());
        } catch (Exception e) {
            Platform.runLater(()->sdmController.getSdmGui().showMessageDialog(e.getMessage()));
            System.out.println(e.getMessage());
        }
    }

    private void refreshPage() {
        itemsComboBox.getItems().remove(0,itemsComboBox.getItems().size());
        initItemsListComboBox();
        priceText.setText("0.0");
    }

    public ScrollPane getMainScrollPane() { return mainScrollPane; }
}
