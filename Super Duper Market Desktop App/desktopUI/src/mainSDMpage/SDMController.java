package mainSDMpage;

import SDMEngine.*;
import customerDetails.CustomersDetailsController;
import itemsDetails.ItemsDetailsController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import map.map.MapController;
import order.orderTypeChooser.OrderTypeChooserController;
import order.smartOrder.SmartOrderController;
import orderHistoryDetails.OrderHistoryController;
import progressTask.ProgressTaskController;
import singleHoverButton.SingleHoverButtonController;
import storesDetails.StoresDetailsController;
import systemUpdates.addItem.AddItemController;
import systemUpdates.addStore.AddStoreController;
import updateStoreItems.UpdateStoreItemsController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Optional;

public class SDMController {

    public enum  SystemStyle {
        Light, Dark, LightBlue;

        public String toString() {
            if (this.equals(SystemStyle.Light)) {
                return "Light.css";
            }
            else if (this.equals(SystemStyle.Dark)) {
                return "Dark.css";
            }
            else {
                return "LightBlue.css";
            }
        }
    }

    @FXML private ScrollPane mainScrollPane;
    @FXML private BorderPane mainBorderPane;
    @FXML private VBox menuVBox;
    @FXML private Button loadXMLBtn;
    @FXML private Button showStoreBtn;
    @FXML private Button showItemsBtn;
    @FXML private Button makeAnOrderBtn;
    @FXML private Button showOrderHistoryBtn;
    @FXML private Button showCustomersBtn;
    @FXML private Button updateStoreItemsBtn;
    @FXML private Button addNewStoreBtn;
    @FXML private Button addNewItemBtn;
    @FXML private Text title;
    @FXML private Button lightStyleBtn;
    @FXML private Button lightBlueStyleBtn;
    @FXML private Button darkStyleBtn;

    private Stage stage;
    private Stage secondStage = new Stage();
    private MainSystem mainSystem;
    private SdmGui sdmGui;
    private Path pathToSystemXMLFile;
    private List<SingleHoverButtonController> hoverButtonStoresList = new LinkedList<>();
    private List<SingleHoverButtonController> hoverButtonItemsList = new LinkedList<>();
    private List<SingleHoverButtonController> hoverButtonOrderHistoryList = new LinkedList<>();
    private List<SingleHoverButtonController> hoverButtonCustomersList = new LinkedList<>();
    private Boolean alreadyClickOnButton = false;
    private StoresDetailsController storesDetailsController = new StoresDetailsController();
    private CustomersDetailsController customersDetailsController = new CustomersDetailsController();
    private ItemsDetailsController itemsDetailsController = new ItemsDetailsController();
    private ProgressTaskController progressTaskController = new ProgressTaskController();
    private MapController mainMapController;
    private UpdateStoreItemsController updateStoreItemsController = new UpdateStoreItemsController();
    private OrderHistoryController orderHistoryController = new OrderHistoryController();
    private SystemStyle systemStyle = SystemStyle.Light;

    @FXML
    private void initialize(){ secondStage.initStyle(StageStyle.UNDECORATED); }

    @FXML
    void clickedLoadXMLBtn(ActionEvent event) {
        File selectedFile = sdmGui.chooseFileWithFileChooser(this.stage);

        if (selectedFile != null) {
            this.pathToSystemXMLFile = selectedFile.toPath();
            Thread thread = new Thread(this::runLoadXml);
            thread.setName("load XML thread");
            thread.start();
        }
    }

    @FXML
    void clickedMakeAnOrderBtn(ActionEvent event) {
        Thread thread = new Thread(this::runMakeAnOrder);
        thread.setName("make Order thread");
        thread.start();
    }

    @FXML
    void clickedUpdateStoreItemsBtn(ActionEvent event) {
        Thread thread = new Thread(this::runUpdateStoreItems);
        thread.setName("Update store items thread");
        thread.start();
    }

    @FXML
    void clickedAddNewStore(ActionEvent event) {
        Thread thread = new Thread(this::runAddStore);
        thread.setName("add store thread");
        thread.start();
    }

    @FXML
    void clickedAddNewItem(ActionEvent event) {
        Thread thread = new Thread(this::runAddItem);
        thread.setName("add item thread");
        thread.start();
    }

    @FXML
    void clickedShowAllStoresBtn(ActionEvent event) {
        Thread thread = new Thread(this::runShowStores);
        thread.setName("show stores thread");
        thread.start();
    }

    @FXML
    void clickedShowAllItemsBtn(ActionEvent event) {
        Thread thread = new Thread(this :: createItemsDetailsPage);
        thread.setName("show items thread");
        thread.start();
    }

    @FXML
    void clickedShowCustomersDetailsBtn(ActionEvent event) {
        Thread thread = new Thread(this::runShowCustomers);
        thread.setName("show customers thread");
        thread.start();
    }

    @FXML
    void clickedShowOrderHistoryBtn(ActionEvent event) {
        Thread thread = new Thread(this::runShowOrders);
        thread.setName("show orders thread");
        thread.start();
    }

    @FXML
    void clickedBlackStyle(ActionEvent event) {
        System.out.println(Thread.currentThread().getName() + " black style");

        this.setSystemStyle(SystemStyle.Dark);

        this.darkStyleBtn.setDisable(true);
        this.lightStyleBtn.setDisable(false);
        this.lightBlueStyleBtn.setDisable(false);

        this.displayMainScreen();
        this.changeStyle();
    }

    @FXML
    void clickedCyanStyle(ActionEvent event) {
        System.out.println(Thread.currentThread().getName()+ " cyan style");

        this.darkStyleBtn.setDisable(false);
        this.lightStyleBtn.setDisable(false);
        this.lightBlueStyleBtn.setDisable(true);

        this.setSystemStyle(SystemStyle.LightBlue);
        this.displayMainScreen();
        this.changeStyle();
    }

    @FXML
    void clickedRedStyle(ActionEvent event) {
        System.out.println(Thread.currentThread().getName() + " red style");

        this.darkStyleBtn.setDisable(false);
        this.lightStyleBtn.setDisable(true);
        this.lightBlueStyleBtn.setDisable(false);

        this.setSystemStyle(SystemStyle.Light);
        this.displayMainScreen();
        this.changeStyle();
    }

    @FXML
    void clickedExitBtn(ActionEvent event) {
        sdmGui.showMessageDialog("Thanks For Buying!");
        stage.close();
    }

    private void changeStyle() {
        this.mainScrollPane.getStylesheets().remove(0, this.mainScrollPane.getStylesheets().size());

        this.mainScrollPane.getStylesheets().add(SDMController.class.getResource(
                "resources/mainSDMPage" + this.systemStyle.toString()).toExternalForm());
    }

    public SdmGui getSdmGui() { return sdmGui; }

    public MainSystem getMainSystem() { return mainSystem; }

    public void setPrimaryStage(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void setMainSystem(MainSystem mainSystem) {
        this.mainSystem = mainSystem;
    }

    public void displayMainScreen() {
        if(mainMapController != null) {
            Platform.runLater(() -> sdmGui.showOnScreen(mainMapController.getMapScrollPane(), this.mainBorderPane));
        }
    }

    public void displayOnScreen(Region paneToShow) {
        Platform.runLater(() -> sdmGui.showOnScreen(paneToShow, this.mainBorderPane));
    }

    public void displayMessageDialog(String str) {
        Platform.runLater(() ->sdmGui.showMessageDialog(str));
    }

    public void setSdmGui(SdmGui sdmGui) {
        if (this.sdmGui == null && sdmGui != null) {
            this.sdmGui = sdmGui;
        }
    }

    private void runLoadXml() {
        System.out.println( "running load XML " + Thread.currentThread().getName());

        MapController tempMap = this.mainMapController;
        this.mainMapController = null;

        try {
            Platform.runLater(() -> createProgressTaskController());
            Platform.runLater(() -> progressTaskController.getFileNameText().setText(this.pathToSystemXMLFile.toString()));
            mainSystem.loadDataFromFile(this.pathToSystemXMLFile);
            this.loadXMLBtn.setDefaultButton(false);

            Platform.runLater(() -> mainBorderPane.setCenter(new ScrollPane(progressTaskController.getScrollPane())));
            Platform.runLater(() -> mainSystem.startCalculateFileTask(this::onFinishLoadXMLSuccessfully));
        }
        catch (FileNotFoundException ex) {
            Platform.runLater(() -> sdmGui.showMessageDialog(ex.getMessage()));
            this.mainMapController = tempMap;
        }
        catch (Exception ex) {
            Platform.runLater(() -> mainBorderPane.setCenter(new ScrollPane(progressTaskController.getScrollPane())));
            Platform.runLater(() -> mainSystem.startCalculateFileTask(() -> onFinishLoadXMLNotSuccessfully(ex.getMessage())));
            this.mainMapController = tempMap;
        }
    }

    public void onFinishLoadXMLSuccessfully() {
        sdmGui.showMessageDialog("XML Loaded successfully!");
        this.createAndDisplayMap();
    }

    public void createAndDisplayMap() {
        System.out.println(Thread.currentThread().getName() + " create and display map");

        this.mainMapController = MapController.create();
        this.mainMapController.init(this.mainBorderPane, this, this.mainSystem);
        Platform.runLater(() -> sdmGui.showOnScreen(this.mainMapController.getMapScrollPane() ,this.mainBorderPane ));
    }

    public void onFinishLoadXMLNotSuccessfully(String message)  {
        sdmGui.showMessageDialog(message);
        if (this.mainMapController != null) {
            this.displayMainScreen();
        }
    }

    private void runShowStores() {
        System.out.println(Thread.currentThread().getName());

        if(!this.mainSystem.isSystemSet()) {
            Platform.runLater(() -> sdmGui.showMessageDialog("Please load XML file first"));
        }

        else if(!this.mainSystem.getStoresList().isEmpty()) {
            hoverButtonStoresList = new LinkedList<>();
            createStoresDetailsController();
            for (Store store : this.mainSystem.getStoresList().values()) {
                SingleHoverButtonController button = SingleHoverButtonController.create(this);
                button.getHoverButton().setText("#" + store.getSerialNumber() + " " + store.getName());
                button.getText().setText(store.toString());

                hoverButtonStoresList.add(button);
                button.init();
                storesDetailsController.init(this);
            }
            Platform.runLater(() -> sdmGui.placeButtonsForStores(this.hoverButtonStoresList, this.storesDetailsController ,this.mainBorderPane));

        }
        else {
            Platform.runLater(() -> sdmGui.showMessageDialog("Store list is empty"));
        }
    }

    private void createStoresDetailsController() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = StoresDetailsController.class.getResource("resources/storesDetailsPage.fxml");
            loader.setLocation(FXML);
            Node root = loader.load();
            storesDetailsController = loader.getController();

            storesDetailsController.getBorderPane().getStylesheets().add(
                    OrderHistoryController.class.getResource(
                            "resources/orderHistory" + this.getSystemStyle().toString()).toExternalForm());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runAddStore() {
        if(this.mainSystem.isSystemSet()) {
            AddStoreController addStoreController = AddStoreController.create(this);
            addStoreController.init(this.mainSystem, this, this.mainBorderPane);
            Platform.runLater(() -> sdmGui.showOnScreen(addStoreController.getScrollPane(), this.mainBorderPane));
        }
        else {
            Platform.runLater(() -> sdmGui.showMessageDialog("Please load XML file first"));
        }
    }

    private void runAddItem() {
        if(this.mainSystem.isSystemSet()) {
            AddItemController addItemController = AddItemController.create(this);
            addItemController.init(this.mainSystem, this, this.mainBorderPane);
            Platform.runLater(() -> sdmGui.showOnScreen(addItemController.getScrollPane(), this.mainBorderPane));
        }
        else {
            Platform.runLater(() -> sdmGui.showMessageDialog("Please load XML file first"));
        }
    }

    private void runMakeAnOrder() {
        System.out.println(Thread.currentThread().getName());

        if(this.mainSystem.isSystemSet()) {
            OrderTypeChooserController orderTypeChooserController = OrderTypeChooserController.create(this);
            orderTypeChooserController.init(this.mainBorderPane, this.mainSystem, this);
            Platform.runLater(() -> sdmGui.showOnScreen(orderTypeChooserController.getChooseOrderTypeVBox(), this.mainBorderPane));
        }
        else {
            Platform.runLater(() -> sdmGui.showMessageDialog("Please load XML file first"));
        }
    }

    private void runShowOrders() {
        System.out.println(Thread.currentThread().getName());

        if(!this.mainSystem.isSystemSet()) {
            Platform.runLater(() -> sdmGui.showMessageDialog("Please load XML file first"));
        }
        else if(this.mainSystem.getOrdersHistory().size() > 0) {
            if(!this.mainSystem.getItemsList().isEmpty()) {
                hoverButtonOrderHistoryList = new LinkedList<>();
                createOrderHistoryController();
                for (Order order : this.mainSystem.getOrdersHistory()) {
                    SingleHoverButtonController button = SingleHoverButtonController.create(this);
                    button.getHoverButton().setText("#" + order.getSerialNumber());
                    button.getText().setText(order.toString());

                    hoverButtonOrderHistoryList.add(button);
                    button.init();
                    orderHistoryController.init(this);
                }
            }
            Platform.runLater(() -> sdmGui.placeButtonsForOrderHistory(this.hoverButtonOrderHistoryList, this.orderHistoryController ,this.mainBorderPane));
        }
        else {
            Platform.runLater(() -> sdmGui.showMessageDialog("Order list is empty"));
        }
    }

    private void createOrderHistoryController() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = OrderHistoryController.class.getResource("resources/orderHistoryPage.fxml");
            loader.setLocation(FXML);
            Node root = loader.load();
            orderHistoryController = loader.getController();

            orderHistoryController.getBorderPane().getStylesheets().add(
                    OrderHistoryController.class.getResource(
                            "resources/orderHistory" + this.getSystemStyle().toString()).toExternalForm());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runShowCustomers() {
        System.out.println(Thread.currentThread().getName());

        if(!this.mainSystem.isSystemSet()) {
            Platform.runLater(() -> sdmGui.showMessageDialog("Please load XML file first"));
        }
        else if(!this.mainSystem.getCustomersList().isEmpty()) {
            creatCustomerController();
            hoverButtonCustomersList = new LinkedList<>();
            for (Customer customer : this.mainSystem.getCustomersList().values()) {
                SingleHoverButtonController button = SingleHoverButtonController.create(this);
                button.getHoverButton().setText("#" + customer.getSerialNumber() + " " + customer.getName());
                button.getText().setText(customer.toStringForFXML());
                hoverButtonCustomersList.add(button);
                button.init();
                customersDetailsController.init(this);
            }
            Platform.runLater(() -> sdmGui.placeButtonsForCustomers(this.hoverButtonCustomersList, this.customersDetailsController,this.mainBorderPane));
        }
        else {
            Platform.runLater(() -> sdmGui.showMessageDialog("customer list is empty"));
        }
    }

    private void creatCustomerController() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = CustomersDetailsController.class.getResource("resources/showCustomersPage.fxml");
            loader.setLocation(FXML);
            Node root = loader.load();
            customersDetailsController = loader.getController();

            customersDetailsController.getScrollPane().getStylesheets().add(
                    CustomersDetailsController.class.getResource(
                            "resources/showCustomersPage" + this.getSystemStyle().toString()).toExternalForm());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createItemsDetailsPage() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = ItemsDetailsController.class.getResource("resources/itemsDetails.fxml");
            loader.setLocation(FXML);
            Node root = loader.load();
            itemsDetailsController = loader.getController();

            itemsDetailsController.getScrollPane().getStylesheets().add(
                    ItemsDetailsController.class.getResource(
                            "resources/itemDetails" + this.getSystemStyle().toString()).toExternalForm());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if(!this.mainSystem.isSystemSet()) {
            Platform.runLater(() -> sdmGui.showMessageDialog("Please load XML file first"));
        }
        else {
            itemsDetailsController.init(this);
            itemsDetailsController.generateTheItemsButtons(mainSystem);
            Platform.runLater(() -> sdmGui.showItemsDetailsPage(this.itemsDetailsController, this.mainBorderPane));
        }
    }

    public void createProgressTaskController(){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = ProgressTaskController.class.getResource("resources/progressTaskPage.fxml");
            loader.setLocation(FXML);
            Parent parent = loader.load();
            progressTaskController = loader.getController();
            parent.getStylesheets().add(ProgressTaskController.class.getResource(
                    "resources/progressTaskPage" + this.getSystemStyle().toString()).toExternalForm());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createProgressTaskControllerForSmartOrder(){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = ProgressTaskController.class.getResource("resources/progressTaskPage.fxml");
            loader.setLocation(FXML);
            Parent parent = loader.load();
            progressTaskController = loader.getController();

            progressTaskController.getScrollPane().getStylesheets().add(ProgressTaskController.class.getResource(
                    "resources/progressTaskPage" + this.getSystemStyle().toString()).toExternalForm());

            secondStage.setScene(new Scene(parent));
            progressTaskController.getFileLabel().setText("");
            progressTaskController.getFileNameText().setText("");
            progressTaskController.getTaskMassageText().setText("Finding the cheapest store that sell this item...");
            Platform.runLater(()->secondStage.show());


        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bindFileTaskToUIComponents(Task<Boolean> aTask, Runnable onFinish) {
        progressTaskController.getTaskMassageText().textProperty().bind(aTask.messageProperty());

        progressTaskController.getProgressBar().progressProperty().bind(aTask.progressProperty());

        progressTaskController.getTaskPercentText().textProperty().bind(
                Bindings.concat(
                        Bindings.format("%.0f",
                                Bindings.multiply(aTask.progressProperty(), 100)),
                        " %"));


        for (Node node : this.menuVBox.getChildren()) {
            node.disableProperty().bind(aTask.runningProperty());
        }

/*        loadXMLBtn.disableProperty().bind(aTask.runningProperty());
        showStoreBtn.disableProperty().bind(aTask.runningProperty());
        showItemsBtn.disableProperty().bind(aTask.runningProperty());
        makeAnOrderBtn.disableProperty().bind(aTask.runningProperty());
        showOrderHistoryBtn.disableProperty().bind(aTask.runningProperty());
        showCustomersBtn.disableProperty().bind(aTask.runningProperty());
        updateStoreItemsBtn.disableProperty().bind(aTask.runningProperty());
        addNewStoreBtn.disableProperty().bind(aTask.runningProperty());
        addNewItemBtn.disableProperty().bind(aTask.runningProperty());*/

        aTask.valueProperty().addListener((observable, oldValue, newValue) -> {
            onTaskFinished(Optional.ofNullable(onFinish));
        });
    }

    public void bindSmartOrderTaskToUIComponents(Task<Boolean> aTask, Runnable onFinish) {

        progressTaskController.getProgressBar().progressProperty().bind(aTask.progressProperty());

        progressTaskController.getTaskPercentText().textProperty().bind(
                Bindings.concat(
                        Bindings.format("%.0f",
                                Bindings.multiply(aTask.progressProperty(), 100)),
                        " %"));


        aTask.valueProperty().addListener((observable, oldValue, newValue) -> {
            onTaskFinished(Optional.ofNullable(onFinish));
        });
    }

    public void onTaskFinished(Optional<Runnable> onFinish) {
        progressTaskController.getTaskMassageText().textProperty().unbind();
        progressTaskController.getTaskPercentText().textProperty().unbind();
        progressTaskController.getProgressBar().progressProperty().unbind();
        onFinish.ifPresent(Runnable::run);
    }

    public ProgressTaskController getProgressTaskController() { return progressTaskController; }

    public Stage getStage() { return stage; }

    public Stage getSecondStage() { return secondStage; }

    public SystemStyle getSystemStyle() { return systemStyle; }

    public void setSystemStyle(SystemStyle systemStyle) { this.systemStyle = systemStyle; }

    private void runUpdateStoreItems() {
        System.out.println(Thread.currentThread().getName());

        if (this.mainSystem.isSystemSet()) {
            createUpdateStoreItemsController();
            updateStoreItemsController.init(this.mainBorderPane, this.mainSystem, this);
        }
        else{
            Platform.runLater(() -> sdmGui.showMessageDialog("Please load XML file first"));
        }
    }

    private void createUpdateStoreItemsController() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = UpdateStoreItemsController.class.getResource("resources/updateStoreItemsPage.fxml");
            loader.setLocation(FXML);
            Node root = loader.load();
            updateStoreItemsController = loader.getController();

            updateStoreItemsController.getScrollPane().getStylesheets().add(UpdateStoreItemsController.class.getResource(
                    "resources/updateStoreItem" + this.getSystemStyle().toString()).toExternalForm());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}