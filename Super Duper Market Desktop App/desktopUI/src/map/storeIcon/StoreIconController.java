package map.storeIcon;

import SDMEngine.Store;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import map.customerIcon.CustomerIconController;

import java.io.IOException;
import java.net.URL;

public class StoreIconController {

    Store store;
    private int originalHeight;
    private int originalWidth;

    @FXML private VBox iconVBox;
    @FXML private Label label;
    @FXML private ImageView picture;

    public static StoreIconController create() {
        StoreIconController storeIconController = null;

        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = StoreIconController.class.getResource("resources/storeIcon.fxml");
            loader.setLocation(FXML);

            Node root = loader.load();
            storeIconController = loader.getController();
            storeIconController.getIconVBox().getStylesheets().add(
                    StoreIconController.class.getResource("resources/storeIconLight.css").toExternalForm());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return storeIconController;
    }

    public void init(Store store) {
        this.store = store;
        this.label.setText(store.getName());
        this.originalHeight = 60;
        this.originalWidth = 50;
    }

    public VBox getIconVBox() { return iconVBox; }

    @FXML
    void mouseEntered(MouseEvent event) {
        this.iconVBox.setPrefHeight(this.originalHeight * 2);
        this.iconVBox.setPrefWidth(this.originalWidth * 2);
        this.label.setText(String.format("Store #%d\n %S\n location: %S",
        store.getSerialNumber(), store.getName(), store.getCoordinate().toString()));

    }

    @FXML
    void mouseExited(MouseEvent event) {
        this.label.setText(store.getName());
        this.iconVBox.setPrefWidth(this.originalWidth);
        this.iconVBox.setPrefHeight(this.originalHeight);
    }
}