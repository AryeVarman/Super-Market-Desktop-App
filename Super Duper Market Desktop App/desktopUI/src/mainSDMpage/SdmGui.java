package mainSDMpage;

import customerDetails.CustomersDetailsController;
import itemsDetails.ItemsDetailsController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import orderHistoryDetails.OrderHistoryController;
import singleHoverButton.SingleHoverButtonController;
import SDMEngine.*;
import storesDetails.StoresDetailsController;
import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.List;

public class SdmGui extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();

        URL mainFXML = getClass().getResource("resources/mainSDMPageLayout.fxml");
        loader.setLocation(mainFXML);
        Parent root = loader.load();
        root.getStylesheets().add(SdmGui.class.getResource(
                "resources/mainSDMPageLight.css").toExternalForm());

        SDMController sdmController = loader.getController();
        MainSystem businessLogic = new MainSystem(sdmController);

        sdmController.setSdmGui(this);
        sdmController.setPrimaryStage(primaryStage);
        sdmController.setMainSystem(businessLogic);

        primaryStage.setTitle("Super Duper Market");
        primaryStage.getIcons().add(new Image(SDMController.class.getResourceAsStream("resources/storePicture.jpg")));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public File chooseFileWithFileChooser(Stage stage) {
        System.out.println(Thread.currentThread().getName() + " choose file");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select XML file");
        return fileChooser.showOpenDialog(stage);
    }

    public void showMessageDialog(String message) {
        System.out.println( "show msg dialog " + Thread.currentThread().getName());

        JOptionPane optionPane = new JOptionPane(message);
        JDialog dialog = optionPane.createDialog("Super Duper Market");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }

    public void showText(String text, BorderPane mainBorderPane) {
        System.out.println(Thread.currentThread().getName() + " show text");

        mainBorderPane.centerProperty().setValue(new ScrollPane(new Text(text)));
    }

    public void placeButtons(List<SingleHoverButtonController> hoverButtonList, BorderPane borderPane) {
        System.out.println(Thread.currentThread().getName() + " place Buttons");

        VBox centerVbox = new VBox();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        centerVbox.setAlignment(Pos.TOP_CENTER);

        for (SingleHoverButtonController button: hoverButtonList) {
            centerVbox.getChildren().add(button.getVbox());
            button.getScrollPane().setVisible(false);
        }
        scrollPane.setContent(centerVbox);
        borderPane.centerProperty().setValue(scrollPane);
    }

    public void placeButtonsForCustomers(List<SingleHoverButtonController> hoverButtonList, CustomersDetailsController customersDetailsController, BorderPane borderPane){

        for (SingleHoverButtonController button: hoverButtonList) {
            customersDetailsController.getVbox().getChildren().add(button.getVbox());
            button.getScrollPane().setVisible(false);
        }

        borderPane.centerProperty().setValue(customersDetailsController.getBorderPane());
    }

    public void placeButtonsForStores(List<SingleHoverButtonController> hoverButtonList, StoresDetailsController storesDetailsController, BorderPane borderPane){


        for (SingleHoverButtonController button: hoverButtonList) {
            storesDetailsController.getVbox().getChildren().add(button.getVbox());
            button.getScrollPane().setVisible(false);
        }

        borderPane.centerProperty().setValue(storesDetailsController.getBorderPane());
    }

    public void showOnScreen(Region regionToShow, BorderPane placeToShow) {
        System.out.println(Thread.currentThread().getName() + " show on screen");

        placeToShow.centerProperty().setValue(regionToShow);
    }

    public void placeButtonsForOrderHistory(List<SingleHoverButtonController> hoverButtonOrderHistoryList, OrderHistoryController orderHistoryController, BorderPane mainBorderPane) {
        for (SingleHoverButtonController button: hoverButtonOrderHistoryList) {
            orderHistoryController.getVbox().getChildren().add(button.getVbox());
            button.getScrollPane().setVisible(false);
        }

        mainBorderPane.centerProperty().setValue(orderHistoryController.getBorderPane());
    }

    public void showItemsDetailsPage(ItemsDetailsController itemsDetailsController, BorderPane mainBorderPane) {
        mainBorderPane.centerProperty().setValue(itemsDetailsController.getScrollPane());
    }
}