package order.singleSale;

import SDMEngine.Order;
import SDMEngine.Sale;
import SDMEngine.Store;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import mainSDMpage.SDMController;
import order.orderTypeChooser.OrderTypeChooserController;
import order.saleOfferCheckBox.SaleOfferCheckBox;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class SingleSaleController {
    SDMController sdmController;
    Sale sale;
    Order order;
    Store store;
    List<SaleOfferCheckBox> saleOfferCheckBoxList;

    @FXML private HBox mainHBox;
    @FXML private Label saleTrigger;
    @FXML private Label saleTypeLabel;
    @FXML private VBox offerVBox;

    public static SingleSaleController create(SDMController sdmController) {
        SingleSaleController singleSaleController = null;

        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = SingleSaleController.class.getResource("resources/singleSale.fxml");
            loader.setLocation(FXML);

            Node root = loader.load();
            singleSaleController = loader.getController();

            singleSaleController.mainHBox.getStylesheets().add(SingleSaleController.class.getResource(
                    "resources/singleSale" + sdmController.getSystemStyle().toString()).toExternalForm());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return singleSaleController;
    }

    public void init (Sale sale, Order order, Store store, SDMController sdmController) {
            this.sdmController = sdmController;
            this.sale = sale;
            this.order = order;
            this.store = store;
            this.saleOfferCheckBoxList = new LinkedList<>();

            String saleTypeString = (sale.getSaleType().equals(Sale.SaleType.irrelevant)) ? "" : sale.getSaleType().toString();

            saleTrigger.setText(sale.getSaleTrigger().details());
            saleTypeLabel.setText(", you can get: " + saleTypeString);

            for (Sale.SaleOffer saleOffer : sale.getSaleOfferList()) {
                CheckBox checkBox = new CheckBox();
                checkBox.setText(saleOffer.toString());
                checkBox.setSelected(false);
                checkBox.setAlignment(Pos.TOP_LEFT);

                offerVBox.getChildren().add(checkBox);

                if (sale.getSaleType().equals(Sale.SaleType.allOrNothing)) {
                    /*checkBox.selectedProperty().addListener(this::allOrNothing);*/
                }
                else {
                    checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                        boolean toDisable = checkBox.isSelected();
                        for (Node node : offerVBox.getChildren()) {
                            CheckBox checkBoxFromList = (CheckBox) node;
                            if (checkBox != checkBoxFromList) {
                                checkBoxFromList.setDisable(toDisable);
                            }
                        }
                    });
                }

                this.saleOfferCheckBoxList.add(new SaleOfferCheckBox(saleOffer, checkBox, this.sdmController));
            }
        }

/*    private void allOrNothing(Observable observable) {
            for (Node node : offerVBox.getChildren()) {
                CheckBox checkBox = (CheckBox) node;
                checkBox.setSelected(!checkBox.selectedProperty().getValue());
            }


    }*/

    public HBox getMainHBox() { return mainHBox; }

    public Sale getSale() { return sale; }

    public List<SaleOfferCheckBox> getSaleOfferCheckBoxList() { return saleOfferCheckBoxList; }
}




