package itemsDetails;

import SDMEngine.Item;
import SDMEngine.MainSystem;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import mainSDMpage.SDMController;


import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ItemsDetailsController {

    @FXML private ScrollPane scrollPane;
    @FXML private BorderPane borderPane;
    @FXML private HBox hBox;
    @FXML private Label labelItems;
    @FXML private GridPane gridPane;
    @FXML private AreaChart<Number, Number> chart;
    @FXML private VBox vBox;
    @FXML private TextFlow textFlow;
    @FXML private Label headLine;
    @FXML private Text textDetails;
    private SDMController sdmController;


    @FXML
    void onClickCancelBtn(ActionEvent event) { sdmController.displayMainScreen(); }

    public void init(SDMController sdmController) { this.sdmController = sdmController; }

    Map<SingleItemDetailsButton,ItemDetailsPage> itemsDetailsPages = new HashMap<>();

    public Map<SingleItemDetailsButton, ItemDetailsPage> getItemsDetailsPages() { return itemsDetailsPages; }

    public void generateTheItemsButtons(MainSystem mainSystem) {
        for (Item item: mainSystem.getItemsList().values()) {
            SingleItemDetailsButton singleItemDetailsButton =  new SingleItemDetailsButton();
            singleItemDetailsButton.setText("#" + item.getSerialNumber() + " " + item.getName());
            Text textItemDetails = new Text(mainSystem.oneItemDetails(item));

            ItemDetailsPage itemDetailsPage = new ItemDetailsPage(textItemDetails, chart);
            itemsDetailsPages.put(singleItemDetailsButton,itemDetailsPage);
            vBox.getChildren().add(singleItemDetailsButton);
            singleItemDetailsButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Object button = event.getSource();
                    textDetails.setText(itemsDetailsPages.get((Button)button).getTextDetails().getText());
                }
            });
        }

        this.scrollPane.getStylesheets().add(ItemsDetailsController.class.getResource(
                "resources/itemDetails" + this.sdmController.getSystemStyle().toString()).toExternalForm());
    }

    public BorderPane getBorderPane() { return borderPane; }

    public ScrollPane getScrollPane() { return scrollPane; }
}
