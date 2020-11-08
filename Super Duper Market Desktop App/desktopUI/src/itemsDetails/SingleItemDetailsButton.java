package itemsDetails;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import singleHoverButton.SingleHoverButtonController;
import java.io.IOException;
import java.net.URL;

public class SingleItemDetailsButton extends Button {
/*
    @FXML
    private Button singleItemButton;

    private ItemDetailsPage itemDetailsPage;

    @FXML
    void onClickShowItemDetails(ActionEvent event) {

    }

    public ItemDetailsPage getItemDetailsPage() {
        return itemDetailsPage;
    }

    public Button getSingleItemButton() {
        return singleItemButton;
    }

    public void setItemDetailsPage(ItemDetailsPage itemDetailsPage) {
        this.itemDetailsPage = itemDetailsPage;
    }*/

    public SingleItemDetailsButton(){
        this.setPrefSize(190,30);
        this.getStyleClass().add("btn");
        this.getStyleClass().add("continueBtn");
    }
}