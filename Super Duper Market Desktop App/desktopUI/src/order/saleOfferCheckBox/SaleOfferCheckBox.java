package order.saleOfferCheckBox;

import SDMEngine.Sale;
import javafx.scene.control.CheckBox;
import mainSDMpage.SDMController;
import order.orderTypeChooser.OrderTypeChooserController;

import java.util.Objects;

public class SaleOfferCheckBox {

    Sale.SaleOffer saleOffer;
    CheckBox checkBox;

    public SaleOfferCheckBox(Sale.SaleOffer saleOffer, CheckBox checkBox, SDMController sdmController) {
        this.saleOffer = saleOffer;
        this.checkBox = checkBox;

        this.checkBox.getStyleClass().add("txt");
        this.checkBox.getStylesheets().add(SaleOfferCheckBox.class.getResource(
                "resources/saleOfferCheckBox" + sdmController.getSystemStyle().toString()).toExternalForm());
    }

    public Sale.SaleOffer getSaleOffer() { return saleOffer; }

    public void setSaleOffer(Sale.SaleOffer saleOffer) {
        this.saleOffer = saleOffer;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaleOfferCheckBox that = (SaleOfferCheckBox) o;
        return saleOffer.equals(that.saleOffer) &&
                checkBox.equals(that.checkBox);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saleOffer, checkBox);
    }
}
