package order.selectSales;

import SDMEngine.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import mainSDMpage.SDMController;
import order.orderSummery.OrderSummeryController;
import order.orderTypeChooser.OrderTypeChooserController;
import order.saleOfferCheckBox.SaleOfferCheckBox;
import order.singleSale.SingleSaleController;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class SelectSalesController {

    Order order;
    Pane areaForDisplaying;
    MainSystem mainSystem;
    SDMController sdmController;
    List<SingleSaleController> singleSaleControllerList;

    @FXML private ScrollPane scrollpane;
    @FXML private VBox salesVbox;
    @FXML private Button addSales;

    public SelectSalesController() { }

    public static SelectSalesController create(SDMController sdmController) {
        SelectSalesController selectSalesController = null;

        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = SelectSalesController.class.getResource("resources/selectSales.fxml");
            loader.setLocation(FXML);

            Node root = loader.load();
            selectSalesController = loader.getController();

            selectSalesController.scrollpane.getStylesheets().add(SelectSalesController.class.getResource(
                    "resources/selectSales" + sdmController.getSystemStyle().toString()).toExternalForm());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return selectSalesController;
    }

    public void init(Order order, Pane areaForDisplaying, MainSystem mainSystem, SDMController sdmController) {
        this.order = order;
        this.areaForDisplaying = areaForDisplaying;
        this.mainSystem = mainSystem;
        this.sdmController = sdmController;
        this.singleSaleControllerList = new LinkedList<>();

        for (Sale sale : order.getSaleDeserved()) {
            SingleSaleController singleSaleController = SingleSaleController.create(this.sdmController);
            singleSaleController.init(sale, this.order, sale.getStore(), this.sdmController);

            this.singleSaleControllerList.add(singleSaleController);
            salesVbox.getChildren().add(singleSaleController.getMainHBox());
        }
    }

    public VBox getFlowPane() { return salesVbox; }

    public Button getAddSales() { return addSales; }

    public ScrollPane getScrollpane() { return scrollpane; }

    @FXML
    void clickedAddSales(ActionEvent event) {
        Thread thread = new Thread(this::prepareOrderSummeryPage);
        thread.setName("prepare order summery page");
        thread.start();
    }

    private void prepareOrderSummeryPage() {
        this.addSaleItemsToOrder();
        OrderSummeryController orderSummeryController = OrderSummeryController.create(this.sdmController);
        orderSummeryController.init(this.order, this.sdmController, this.mainSystem);
        Platform.runLater(() -> sdmController.displayOnScreen(orderSummeryController.getMainBorderPain()));
    }

    private void addSaleItemsToOrder() {
        Sale.SaleOffer saleOffer;

        for (SingleSaleController singleSaleController : this.singleSaleControllerList) {

            if(singleSaleController.getSale().getSaleType().equals(Sale.SaleType.irrelevant) ||
                    singleSaleController.getSale().getSaleType().equals(Sale.SaleType.oneOf)) {

                for (SaleOfferCheckBox saleOfferCheckBox : singleSaleController.getSaleOfferCheckBoxList()) {

                    if (saleOfferCheckBox.getCheckBox().isSelected()) {
                        saleOffer = saleOfferCheckBox.getSaleOffer();

                        Store store = saleOffer.getStore();
                        StoreItem storeItem = new StoreItem(saleOffer.getStore().getItemsList().get(saleOffer.getItemId()).getItem(), saleOffer.getPricePerUnit());
                        double amountFromItem = saleOffer.getAmountNeeded();

                        this.order.addOrderItemToOrderList(store, storeItem, amountFromItem, true);
                    }
                }
            }

            else {
                for (SaleOfferCheckBox saleOfferCheckBox : singleSaleController.getSaleOfferCheckBoxList()) {
                    if (saleOfferCheckBox.getCheckBox().isSelected()) {
                        this.addAllOffersInSale(singleSaleController);
                        break;
                    }
                }
            }
        }
    }

    private void addAllOffersInSale(SingleSaleController singleSaleController) {
        Sale.SaleOffer saleOffer;

        for (SaleOfferCheckBox saleOfferCheckBox : singleSaleController.getSaleOfferCheckBoxList()) {

                saleOffer = saleOfferCheckBox.getSaleOffer();
                Store store = saleOffer.getStore();
                StoreItem storeItem = new StoreItem(saleOffer.getStore().getItemsList().get(saleOffer.getItemId()).getItem(), saleOffer.getPricePerUnit());
                double amountFromItem = saleOffer.getAmountNeeded();
                this.order.addOrderItemToOrderList(store, storeItem, amountFromItem, true);
        }

    }
}