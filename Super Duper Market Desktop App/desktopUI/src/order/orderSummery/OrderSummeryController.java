package order.orderSummery;

import SDMEngine.MainSystem;
import SDMEngine.Order;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import mainSDMpage.SDMController;
import order.smartOrder.SmartOrderController;

import java.io.IOException;
import java.net.URL;

public class OrderSummeryController {

    Order order;
    SDMController sdmController;
    private MainSystem mainSystem;

    @FXML private BorderPane mainBorderPain;
    @FXML private Text orderSummerTxt;
    @FXML private Button cancelBtn;
    @FXML private Button confirmedBtn;

    public static OrderSummeryController create(SDMController sdmController) {
        OrderSummeryController orderSummeryController = null;

        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = OrderSummeryController.class.getResource("resources/orderSummery.fxml");
            loader.setLocation(FXML);

            Node root = loader.load();
            orderSummeryController = loader.getController();

            orderSummeryController.mainBorderPain.getStylesheets().add(OrderSummeryController.class.getResource(
                    "resources/orderSummery" + sdmController.getSystemStyle().toString()).toExternalForm());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return orderSummeryController;
    }

    public void init(Order order, SDMController sdmController, MainSystem mainSystem) {
        this.order = order;
        this.orderSummerTxt.setText(order.showOrderSummery());
        this.sdmController = sdmController;
        this.mainSystem = mainSystem;
    }

    @FXML
    void clickedCancelBtn(ActionEvent event) { sdmController.displayMainScreen(); }

    @FXML
    void clickedConfirmBtn(ActionEvent event) {
        Thread thread = new Thread(() -> this.mainSystem.updateInformationAfterCompleteOrder(this.order));
        thread.setName("update system after order thread");
        thread.start();
        Platform.runLater(() -> sdmController.displayMessageDialog("Order Completed Successfully!"));
        Platform.runLater(() -> sdmController.displayMainScreen());
    }

    public BorderPane getMainBorderPain() { return mainBorderPain; }
}
