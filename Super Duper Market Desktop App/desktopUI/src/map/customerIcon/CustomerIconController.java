package map.customerIcon;

import SDMEngine.Customer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import mainSDMpage.SDMController;

import java.io.IOException;
import java.net.URL;

public class CustomerIconController {

    private Customer customer;
    private int originalHeight;
    private int originalWidth;

    @FXML private VBox iconVBox;
    @FXML private Label label;
    @FXML private ImageView picture;

    public static CustomerIconController create() {
        CustomerIconController customerIconController = null;

        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = CustomerIconController.class.getResource("resources/customerIcon.fxml");
            loader.setLocation(FXML);

            Node root = loader.load();
            customerIconController = loader.getController();
            customerIconController.getIconVBox().getStylesheets().add(
                    CustomerIconController.class.getResource(
                            "resources/customerIconLight.css").toExternalForm());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return customerIconController;
    }

    public void init(Customer customer) {
        this.customer = customer;
        this.label.setText(customer.getName());
        this.originalHeight = 60;
        this.originalWidth = 50;
    }

    public VBox getIconVBox() { return iconVBox; }

    @FXML
    void mouseEntered(MouseEvent event) {
     this.iconVBox.setPrefHeight(this.originalHeight * 2);
     this.iconVBox.setPrefWidth(this.originalWidth * 2);
     this.label.setText(String.format("Customer #%d\n %S\n location: %S",
             customer.getSerialNumber(), customer.getName(), customer.getCoordinate().toString()));

    }

    @FXML
    void mouseExited(MouseEvent event) {
        this.label.setText(customer.getName());
        this.iconVBox.setPrefWidth(this.originalWidth);
        this.iconVBox.setPrefHeight(this.originalHeight);
    }

}
