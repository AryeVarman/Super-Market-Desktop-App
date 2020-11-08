package orderHistoryDetails;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import mainSDMpage.SDMController;

public class OrderHistoryController {

    @FXML private BorderPane borderPane;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox vbox;
    @FXML private Button cancelBtn;

    private SDMController sdmController;

    @FXML
    void onClickCancelBtn(ActionEvent event) { sdmController.displayMainScreen(); }

    public void init(SDMController sdmController){ this.sdmController = sdmController; }

    public ScrollPane getScrollPane() { return scrollPane; }

    public VBox getVbox() { return vbox; }

    public BorderPane getBorderPane() { return borderPane; }


}
