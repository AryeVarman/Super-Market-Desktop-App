package itemsDetails;

import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ItemDetailsPage {

    private Text textDetails;
    private AreaChart<Number, Number> chart;

    ItemDetailsPage(Text textDetails,AreaChart<Number, Number> chart){
        this.textDetails = textDetails;
        this.chart = chart;
    }

    public AreaChart<Number, Number> getChart() { return chart; }

    public Text getTextDetails() { return textDetails; }
}
