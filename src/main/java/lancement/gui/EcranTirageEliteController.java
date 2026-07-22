package lancement.gui;

import java.util.function.Supplier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import lancement.GameContext;
import lancement.Menus.MenuTirage_recrutement;

public class EcranTirageEliteController {

    private GameContext ctx;
    private Runnable onRetour;

    @FXML private Label resumeLabel;
    @FXML private Button boutonX1;
    @FXML private Button boutonX10;
    @FXML private Button boutonCouponX1;
    @FXML private Button boutonCouponX10;

    public void initData(GameContext ctx, Runnable onRetour) {
        this.ctx = ctx;
        this.onRetour = onRetour;

        MenuTirage_recrutement mt = ctx.menuTirage;
        boutonX1.setText("Tirage x1  (1 parchemin elite)");
        boutonX10.setText("Tirage x10 (10 parchemins elite)");
        boutonCouponX1.setText("Tirage x1  (" + mt.getCoutCouponEliX1() + " coupons)");
        boutonCouponX10.setText("Tirage x10 (" + mt.getCoutCouponEliX10() + " coupons)");

        rafraichir();
    }

    private void rafraichir() {
        MenuTirage_recrutement mt = ctx.menuTirage;
        resumeLabel.setText("Parchemins Elite : " + mt.getParcheminElite()
                + "  |  Coupons : " + ctx.joueur.getCoupons() + "\n"
                + "Pity A : " + mt.getCompteurPityA() + "/" + mt.getPityA()
                + "  |  S : " + mt.getCompteurPityS() + "/" + mt.getPityS()
                + "  |  SS : " + mt.getCompteurPitySS() + "/" + mt.getPitySS());
    }

    @FXML private void onX1(ActionEvent event)        { tirer(() -> ctx.menuTirage.tirageEliteX1(ctx)); }
    @FXML private void onX10(ActionEvent event)       { tirer(() -> ctx.menuTirage.tirageEliteX10(ctx)); }
    @FXML private void onCouponX1(ActionEvent event)  { tirer(() -> ctx.menuTirage.tirageEliteCouponX1(ctx)); }
    @FXML private void onCouponX10(ActionEvent event) { tirer(() -> ctx.menuTirage.tirageEliteCouponX10(ctx)); }

    private void tirer(Supplier<String> action) {
        info("Tirage Elite", action.get());
        rafraichir();
    }

    @FXML
    private void onRetour(ActionEvent event) {
        onRetour.run();
    }

    private void info(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        styliser(alert);
        alert.showAndWait();
    }

    private void styliser(Dialog<?> dialog) {
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("root-menu");
    }
}
