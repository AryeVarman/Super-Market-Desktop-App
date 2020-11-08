package updateStoreItems.addNewSale;

import SDMEngine.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import mainSDMpage.SDMController;

import javax.naming.Binding;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class AddNewSaleController {

    @FXML private ScrollPane mainScrollPane;
    @FXML private BorderPane borderPane;
    @FXML private TextField saleNameTextField;
    @FXML private ComboBox<Sale.SaleType> saleTypeComboBox;
    @FXML private ComboBox<String> saleTriggerItemComboBox;
    @FXML private TextField saleTriggerAmountTextField;
    @FXML private ComboBox<String> saleOfferItemComboBox;
    @FXML private TextField saleOfferAmountTextField;
    @FXML private TextField saleOfferPriceTextField;
    @FXML private Text summerySaleNameText;
    @FXML private Text summeryTriggerItemText;
    @FXML private Text summeryTriggerAmountText;
    @FXML private Text summerySaleTypeText;
    @FXML private Text summerySaleOffersText;
    @FXML private Button addSaleBtn;
    @FXML private Button saleNameAndTypeConfirmBtn;
    @FXML private Button saleTriggerConfirmBtn;
    @FXML private Button addOfferBtn;
    @FXML private Button cancelBtn;
    @FXML private Label errorSaleNameLabel;
    @FXML private Label errorSaleTriggerAmountLabel;
    @FXML private Label errorSaleOfferAmountLabel;


    private ScrollPane prevWindow;
    private BorderPane mainBorderPane;
    private MainSystem mainSystem;
    private SDMController sdmController;
    private Store selectedStore;

    private BooleanProperty saleNameAndStyleDisableBond = new SimpleBooleanProperty();
    private BooleanProperty saleTriggerItemAndAmountDisableBond = new SimpleBooleanProperty();
    private BooleanProperty saleOfferItemAmountPriceDisableBond = new SimpleBooleanProperty();
    private BooleanProperty firstAddOffer = new SimpleBooleanProperty();
    private IntegerProperty countSaleOffers = new SimpleIntegerProperty(1);
    private BooleanProperty canDoNewFirstSale = new SimpleBooleanProperty();

    private int triggerItemID;
    private int offerItemID;
    private double saleTriggerAmountDouble;
    private double saleOfferAmountDouble;
    private double saleOfferPriceDouble;
    private Sale sale;
    private Collection<SaleOfferForGui> saleOffersList = new HashSet<>();

    @FXML
    private void initialize(){
        saleNameAndTypeConfirmBtn.disableProperty().bind(
                saleNameTextField.textProperty().isEmpty().or(saleTypeComboBox.valueProperty().isNull()));

        saleTriggerConfirmBtn.disableProperty().bind(
                saleTriggerAmountTextField.textProperty().isEmpty().or(saleTriggerItemComboBox.valueProperty().isNull()));

        addSaleBtn.disableProperty().bind(
                saleNameTextField.disableProperty().not().or(saleTriggerItemComboBox.disableProperty().not().or(firstAddOffer.not())));

        addOfferBtn.disableProperty().bind(
                saleNameTextField.disableProperty().not().or(saleTriggerItemComboBox.disableProperty().not()).or(saleOfferItemComboBox.valueProperty().isNull()));

        summerySaleNameText.textProperty().bind(saleNameTextField.textProperty());
        summerySaleTypeText.textProperty().bind(saleTypeComboBox.valueProperty().asString());
        summeryTriggerItemText.textProperty().bind(saleTriggerItemComboBox.valueProperty());
        summeryTriggerAmountText.textProperty().bind(saleTriggerAmountTextField.textProperty());

        saleNameTextField.disableProperty().bind(saleNameAndStyleDisableBond);
        saleTypeComboBox.disableProperty().bind(saleNameAndStyleDisableBond);
        saleTriggerItemComboBox.disableProperty().bind(saleTriggerItemAndAmountDisableBond);
        saleTriggerAmountTextField.disableProperty().bind(saleTriggerItemAndAmountDisableBond);


        summerySaleNameText.visibleProperty().bind(saleNameAndStyleDisableBond);
        summerySaleTypeText.visibleProperty().bind(saleNameAndStyleDisableBond);
        summeryTriggerItemText.visibleProperty().bind(saleTriggerItemAndAmountDisableBond);
        summeryTriggerAmountText.visibleProperty().bind(saleTriggerItemAndAmountDisableBond);

        saleTriggerItemComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(saleTriggerItemComboBox.valueProperty().isNotNull().get()) {
                triggerItemID = Integer.parseInt(saleTriggerItemComboBox.valueProperty().getValue().
                        substring(1).split(" ")[0]);
            }
        });

        saleOfferItemComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(saleOfferItemComboBox.valueProperty().isNotNull().get()) {
                offerItemID = Integer.parseInt(saleOfferItemComboBox.valueProperty().getValue().
                        substring(1).split(" ")[0]);
            }
        });

        summerySaleOffersText.textProperty().addListener(e->{
            countSaleOffers.set(countSaleOffers.get() + 1);
        });

        saleTriggerAmountTextField.setText("0.0");
        saleOfferPriceTextField.setText("0.0");
        saleOfferPriceTextField.setText("0.0");
        firstAddOffer.setValue(false);

    }

    @FXML
    void onClickSaleNameAndTypeConfirmBtn(ActionEvent event) {
        Thread thread = new Thread(this::runSaleNameAndTypeConfirm);
        thread.setName("Sale Name And Type Confirm thread");
        thread.start();

    }

    private void runSaleNameAndTypeConfirm() {
        try {
            mainSystem.checkIfSaleNameIsValid(selectedStore,saleNameTextField.getText());
            Platform.runLater(()->errorSaleNameLabel.setVisible(false));
            Platform.runLater(()->saleNameAndStyleDisableBond.setValue(true));
            Platform.runLater(()->saleNameAndTypeConfirmBtn.setText("change"));

            saleNameAndTypeConfirmBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Platform.runLater(()->saleNameAndTypeConfirmBtn.setText("confirm"));
                    Platform.runLater(()->saleNameAndStyleDisableBond.setValue(false));
                    saleNameAndTypeConfirmBtn.setOnAction(e->onClickSaleNameAndTypeConfirmBtn(event));
                }
            });

        } catch (Exception e) {
            Platform.runLater(()->errorSaleNameLabel.setVisible(true));
            Platform.runLater(()->errorSaleNameLabel.setText(e.getMessage()));
        }
    }

    @FXML
    void onClickSaleTriggerConfirm(ActionEvent event) {
        Thread thread = new Thread(this::runSaleTriggerConfirm);
        thread.setName("Sale Trigger Confirm thread");
        thread.start();
    }

    private void runSaleTriggerConfirm() {
        try {
            mainSystem.checkIfSaleAmountIsValid(triggerItemID,saleTriggerAmountDouble);
            Platform.runLater(()->errorSaleTriggerAmountLabel.setVisible(false));
            Platform.runLater(()->saleTriggerItemAndAmountDisableBond.setValue(true));
            Platform.runLater(()->saleTriggerConfirmBtn.setText("change"));
            saleTriggerConfirmBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Platform.runLater(()->saleTriggerConfirmBtn.setText("confirm"));
                    Platform.runLater(()->saleTriggerItemAndAmountDisableBond.setValue(false));
                    saleTriggerConfirmBtn.setOnAction(e->onClickSaleTriggerConfirm(event));
                }
            });

        } catch (Exception e) {
            Platform.runLater(()->errorSaleTriggerAmountLabel.setVisible(true));
            Platform.runLater(()->errorSaleTriggerAmountLabel.setText(e.getMessage()));
        }
    }

    @FXML
    void onClickAddNewOfferBtn(ActionEvent event) {
        Thread thread = new Thread(this::runAddNewOfferBtn);
        thread.setName("Add New Offer thread");
        thread.start();
    }

    private void runAddNewOfferBtn() {
        try {
            mainSystem.checkIfSaleAmountIsValid(offerItemID, saleOfferAmountDouble);
            Platform.runLater(()->errorSaleOfferAmountLabel.setVisible(false));
            for (SaleOfferForGui saleOfferForGui:saleOffersList) {
                if(saleOfferForGui.getItemId() == offerItemID && saleOfferForGui.getAmountNeeded() == saleOfferAmountDouble
                        && saleOfferForGui.getPricePerUnit() == saleOfferPriceDouble) {
                    throw new Exception("this exact offer is already in the offers list");
                }
            }
            saleOffersList.add(new SaleOfferForGui(offerItemID, saleOfferAmountDouble, saleOfferPriceDouble));
            Platform.runLater(()->firstAddOffer.setValue(true));
            Platform.runLater(()->summerySaleOffersText.textProperty().bind(Bindings.concat(summerySaleOffersText.getText(),countSaleOffers.get() + ". " +
                    saleOfferAmountDouble + " from item" + " #" + offerItemID + " " + mainSystem.getItemsList().get(offerItemID).getName() + " " +
                    "in the cost of " + saleOfferPriceDouble + " per unit\n")));
        }catch (Exception e){
            Platform.runLater(()->errorSaleOfferAmountLabel.setVisible(true));
            Platform.runLater(()->errorSaleOfferAmountLabel.setText(e.getMessage()));
        }
    }

    @FXML
    void onClickAddSaleBtn(ActionEvent event) {
        Thread thread = new Thread(this::runAddSale);
        thread.setName("Add Sale thread");
        thread.start();
    }

    private void runAddSale() {
        sale = new Sale(selectedStore,saleNameTextField.getText(),triggerItemID,saleTriggerAmountDouble,saleTypeComboBox.getValue());
        for (SaleOfferForGui saleOfferForGui:saleOffersList) {
            sale.addOfferToSale(saleOfferForGui.getItemId(),saleOfferForGui.getAmountNeeded(),saleOfferForGui.getPricePerUnit());
        }
        selectedStore.getSaleList().put(saleNameTextField.getText(),sale);
        Platform.runLater(()->sdmController.getSdmGui().showMessageDialog("Sale Added successfully!"));
        Platform.runLater(()->sdmController.getSdmGui().showOnScreen(prevWindow,mainBorderPane));
    }

    @FXML
    void onClickCancelBtn(ActionEvent event) {
        Platform.runLater(()->sdmController.getSdmGui().showOnScreen(prevWindow,mainBorderPane));
    }

    public void init(BorderPane mainBorderPane, MainSystem mainSystem, SDMController sdmController, int storeID, ScrollPane prevWindow) {
        this.mainBorderPane = mainBorderPane;
        this.mainSystem = mainSystem;
        this.sdmController = sdmController;
        this.selectedStore = mainSystem.getStoresList().get(storeID);
        this.prevWindow = prevWindow;

        initSaleStyleComboBox();
        initSaleTriggerItemComboBox();
        initSaleOfferItemComboBox();
        setNumbersFilterOnTextField();

        sdmController.displayOnScreen(borderPane);
    }

    private void initSaleOfferItemComboBox() {
        for (StoreItem item : selectedStore.getItemsList().values()) {
            saleOfferItemComboBox.getItems().add("#" + item.getSerialNumber() + " " + item.getName());
        }
    }

    private void initSaleTriggerItemComboBox() {
        for (StoreItem item : selectedStore.getItemsList().values()) {
            saleTriggerItemComboBox.getItems().add("#" + item.getSerialNumber() + " " + item.getName());
        }
    }

    private void initSaleStyleComboBox() {
        saleTypeComboBox.getItems().add(Sale.SaleType.allOrNothing);
        saleTypeComboBox.getItems().add(Sale.SaleType.irrelevant);
        saleTypeComboBox.getItems().add(Sale.SaleType.oneOf);
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

        TextFormatter<Double> textFormatter1 = new TextFormatter<>(converter, 0.0, filter);
        TextFormatter<Double> textFormatter2 = new TextFormatter<>(converter, 0.0, filter);
        TextFormatter<Double> textFormatter3 = new TextFormatter<>(converter, 0.0, filter);
        saleTriggerAmountTextField.setTextFormatter(textFormatter1);
        saleOfferAmountTextField.setTextFormatter(textFormatter2);
        saleOfferPriceTextField.setTextFormatter(textFormatter3);

        saleTriggerAmountTextField.textProperty().addListener(L->{
            if(saleTriggerAmountTextField.getText().isEmpty()){
                saleTriggerAmountDouble = 0.0;
            }
            else {
                try {
                    saleTriggerAmountDouble = Double.parseDouble(saleTriggerAmountTextField.getText());
                }catch(Exception e){

                }
            }
        });

        saleOfferAmountTextField.textProperty().addListener(L->{
            if(saleOfferAmountTextField.getText().isEmpty()){
                saleOfferAmountDouble = 0.0;
            }
            else {
                try {
                    saleOfferAmountDouble = Double.parseDouble(saleOfferAmountTextField.getText());
                }catch (Exception e){

                }
            }
        });

        saleOfferPriceTextField.textProperty().addListener(L->{
            if(saleOfferPriceTextField.getText().isEmpty()){
                saleOfferPriceDouble = 0.0;
            }
            else {
                try {
                    saleOfferPriceDouble = Double.parseDouble(saleOfferPriceTextField.getText());
                }catch (Exception e){

                }
            }
        });

    }

    public ScrollPane getMainScrollPane() { return mainScrollPane; }
}