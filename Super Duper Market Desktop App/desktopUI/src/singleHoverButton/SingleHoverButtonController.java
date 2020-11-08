package singleHoverButton;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import mainSDMpage.SDMController;

import java.io.IOException;
import java.net.URL;

public class SingleHoverButtonController  {


    @FXML private VBox vbox;
    @FXML private Button hoverButton;
    @FXML private ScrollPane scrollPane;
    @FXML private Text text;
    private SimpleDoubleProperty scrollPaneHeight;

    public static SingleHoverButtonController create(SDMController sdmController) {
        SingleHoverButtonController hoverButtonController = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = SingleHoverButtonController.class.getResource("resources/singleHoverButton.fxml");
            loader.setLocation(FXML);

            Node root = loader.load();
            hoverButtonController = loader.getController();

            hoverButtonController.getVbox().getStylesheets().add(
                    SingleHoverButtonController.class.getResource(
                            "resources/singleHoverButton" + sdmController.getSystemStyle().toString()).toExternalForm());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return hoverButtonController;
    }

    public void init() {
        vbox.prefHeightProperty().bind(scrollPaneHeight);
        scrollPane.prefHeightProperty().bind(scrollPaneHeight);
    }

    public VBox getVbox() { return vbox; }

    public Text getText() { return text; }

    public Button getHoverButton() { return hoverButton; }

    public ScrollPane getScrollPane() { return scrollPane; }

    @FXML
    void onButtonArea(MouseEvent event) {
        scrollPane.setVisible(true);
        scrollPaneHeight.setValue(500);
    }

    @FXML
    void onButtonOutArea(MouseEvent event) {
        scrollPane.setVisible(false);
        scrollPaneHeight.setValue(50);

    }

    @FXML
    void onScrollPaneArea(MouseEvent event) {
        scrollPane.setVisible(true);
        scrollPaneHeight.setValue(500);
    }

    @FXML
    void onScrollPaneOutArea(MouseEvent event) {
        scrollPane.setVisible(false);
        scrollPaneHeight.setValue(50);
    }

    public SingleHoverButtonController(){ scrollPaneHeight = new SimpleDoubleProperty(50); }
}




