package storesDetails;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import mainSDMpage.SDMController;

public class StoresDetailsController {

    @FXML private ScrollPane scrollPane;
    @FXML private VBox vbox;
    @FXML private BorderPane borderPane;
    private SDMController sdmController;

    public void init(SDMController sdmController){
        this.sdmController = sdmController;
    }
    @FXML
    void onClickCancelBtn(ActionEvent event) {
        sdmController.displayMainScreen();
    }

    public VBox getVbox() { return vbox; }

    public ScrollPane getScrollPane() { return scrollPane; }

    public BorderPane getBorderPane() { return borderPane; }
}
