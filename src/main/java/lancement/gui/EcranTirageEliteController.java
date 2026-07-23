package lancement.gui;

import java.util.List;
import java.util.function.Supplier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lancement.GameContext;
import lancement.Menus.MenuTirage_recrutement;
import lancement.Menus.MenuTirage_recrutement.LigneResultat;
import lancement.Menus.MenuTirage_recrutement.TirageInsuffisantException;

public class EcranTirageEliteController {

    private GameContext ctx;
    private Runnable onRetour;

    @FXML private VBox statsBox;
    @FXML private VBox pityBox;
    @FXML private VBox choixBox;

    public void initData(GameContext ctx, Runnable onRetour) {
        this.ctx = ctx;
        this.onRetour = onRetour;

        MenuTirage_recrutement mt = ctx.menuTirage;
        choixBox.getChildren().setAll(
                GuiVisuels.creerCarteChoix("Tirage x1", "1 parchemin elite", e -> onX1()),
                GuiVisuels.creerCarteChoix("Tirage x10", "10 parchemins elite", e -> onX10()),
                GuiVisuels.creerCarteChoix("Tirage x1 (coupons)", mt.getCoutCouponEliX1() + " coupons", e -> onCouponX1()),
                GuiVisuels.creerCarteChoix("Tirage x10 (coupons)", mt.getCoutCouponEliX10() + " coupons", e -> onCouponX10())
        );

        rafraichir();
    }

    private void rafraichir() {
        MenuTirage_recrutement mt = ctx.menuTirage;
        FlowPane stats = new FlowPane(10, 10);
        stats.setAlignment(Pos.CENTER);
        stats.getChildren().addAll(
                GuiVisuels.creerFicheStat("Parchemins Elite", String.valueOf(mt.getParcheminElite())),
                GuiVisuels.creerFicheStat("Coupons", String.valueOf(ctx.joueur.getCoupons()))
        );
        statsBox.getChildren().setAll(stats);

        pityBox.getChildren().setAll(
                lignePity("Rang A", mt.getCompteurPityA(), mt.getPityA()),
                lignePity("Rang S", mt.getCompteurPityS(), mt.getPityS()),
                lignePity("Rang SS", mt.getCompteurPitySS(), mt.getPitySS())
        );
    }

    private Node lignePity(String label, int valeur, int max) {
        Label l = new Label(label);
        l.getStyleClass().add("item-detail");
        l.setMinWidth(60);

        HBox ligne = new HBox(10, l, GuiVisuels.creerBarreProgression(220, 14, valeur, max));
        ligne.setAlignment(Pos.CENTER_LEFT);
        return ligne;
    }

    private void onX1()        { tirer(() -> ctx.menuTirage.tirageEliteX1(ctx)); }
    private void onX10()       { tirer(() -> ctx.menuTirage.tirageEliteX10(ctx)); }
    private void onCouponX1()  { tirer(() -> ctx.menuTirage.tirageEliteCouponX1(ctx)); }
    private void onCouponX10() { tirer(() -> ctx.menuTirage.tirageEliteCouponX10(ctx)); }

    private void tirer(Supplier<List<LigneResultat>> action) {
        try {
            GuiVisuels.afficherResultatsTirage("Tirage Elite", action.get());
        } catch (TirageInsuffisantException e) {
            info("Tirage Elite", e.getMessage());
        }
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
