package map.map;

import SDMEngine.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import mainSDMpage.SDMController;
import map.customerIcon.CustomerIconController;
import map.storeIcon.StoreIconController;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

public class MapController {

    private Pane areaForDisplaying;
    private SDMController sdmController;
    private MainSystem mainSystem;
    private int mapSize;
    private final int emptyPaneSize = 10;

    @FXML private ScrollPane mapScrollPane;
    @FXML private GridPane mapGrid;

    public static MapController create() {
        MapController mapController = null;

        try {
            FXMLLoader loader = new FXMLLoader();
            URL FXML = MapController.class.getResource("resources/map.fxml");
            loader.setLocation(FXML);

            Node root = loader.load();
            mapController = loader.getController();

            mapController.getMapScrollPane().getStylesheets().add(MapController.class.getResource(
                    "resources/map.css").toExternalForm());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return mapController;
    }

    public void init(Pane areaForDisplaying, SDMController sdmController, MainSystem mainSystem) {
        this.areaForDisplaying = areaForDisplaying;
        this.mainSystem = mainSystem;
        this.sdmController = sdmController;

        CoordinateSystem coordinateSystem = mainSystem.getCoordinateMap();

        AtomicInteger lastRow = new AtomicInteger(-1), lastCol = new AtomicInteger(-1);
        calculateMapSize(coordinateSystem, lastRow, lastCol);
        this.initMapIcons(coordinateSystem, lastRow.get(), lastCol.get());
    }

    private void calculateMapSize(CoordinateSystem coordinateSystem, AtomicInteger lastRow, AtomicInteger lastCol) {
        boolean toContinue = true;
        boolean emptyMap = true;
        int startOfSystem = 0;

        for (int i = 1; i <= coordinateSystem.getMAX_ROW() && toContinue; i++) {
            for (int j = 1; j <= coordinateSystem.getMAX_COL() && toContinue; j++) {
                if (coordinateSystem.isCoordinateTaken(j, i)) {
                    startOfSystem = Math.min(j, i);
                    toContinue = false;
                    emptyMap = false;
                }
            }
        }

        // find the last collum in use
        toContinue = true;
        for (int col = coordinateSystem.getMAX_COL() ; col >= 1 && toContinue; col--){
            for (int row = coordinateSystem.getMAX_ROW(); row >= 1 && toContinue; row--) {

                if (coordinateSystem.isCoordinateTaken(col, row)) {
                    toContinue = false;
                    lastCol.set(col);
                }
            }
        }

        // find the last row in use
        toContinue = true;
        for (int row = coordinateSystem.getMAX_ROW(); row >= 1 && toContinue; row--) {
            for (int col = coordinateSystem.getMAX_COL() ; col >= 1 && toContinue; col--) {

                if (coordinateSystem.isCoordinateTaken(col, row)) {
                    toContinue = false;
                    lastRow.set(row);
                }
            }
        }
    }

    private void initMapIcons(CoordinateSystem coordinateSystem, int lastRow, int lastCol) {
        int gapFromStart = -1;

        for (int originalMapRow = 1; originalMapRow <= Math.min(lastRow + 1, coordinateSystem.getMAX_ROW()); originalMapRow++) {

            for (int originalMapCol = 1; originalMapCol <= Math.min(lastCol + 1, coordinateSystem.getMAX_COL()) ; originalMapCol++) {

                if (coordinateSystem.isCoordinateTaken(originalMapCol, originalMapRow)) {
                    if (gapFromStart == -1) {
                        gapFromStart = Math.min(originalMapCol, originalMapRow);
                    }
                    this.setIconInMap(coordinateSystem.getCoordinate(originalMapCol, originalMapRow), originalMapRow,  originalMapCol);
                }
                else if(gapFromStart != -1){
                    Pane pane = new Pane();
                    pane.setMaxSize(emptyPaneSize, emptyPaneSize);
                    pane.setMinSize(emptyPaneSize, emptyPaneSize);
                    this.mapGrid.add(pane, originalMapCol, originalMapRow);
                }
            }
        }
    }

    private void setIconInMap(Coordinate coordinate, int row, int col) {

        if (coordinate.getElement().getClass().equals(Store.class)) {
            StoreIconController storeIconController = StoreIconController.create();
            storeIconController.init((Store) coordinate.getElement());

            this.mapGrid.add(storeIconController.getIconVBox(), col, row);
        }

        else {
            CustomerIconController customerIconController = CustomerIconController.create();
            customerIconController.init((Customer) coordinate.getElement());

            this.mapGrid.add(customerIconController.getIconVBox(), col, row);
        }
    }

    public ScrollPane getMapScrollPane() { return mapScrollPane; }

    public GridPane getMapGrid() { return mapGrid; }
}
