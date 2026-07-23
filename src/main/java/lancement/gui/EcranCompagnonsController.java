package lancement.gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireCompagnons;
import lancement.Gestionnaires.GestionnaireCompagnons.ResultatCompagnon;

public class EcranCompagnonsController {

    private GameContext ctx;

    @FXML private VBox compagnonBox;
    @FXML private VBox orBox;
    @FXML private VBox actionBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichir();
    }

    private void rafraichir() {
        GestionnaireCompagnons gc = ctx.gestionnaireCompagnons;

        Label nomLabel = new Label(gc.getType().nom);
        nomLabel.getStyleClass().add("item-nom");
        nomLabel.setStyle("-fx-font-size: 20px;");

        Label niveauLabel = new Label("Niveau " + gc.getNiveau() + " / " + GestionnaireCompagnons.NIVEAU_MAX);
        niveauLabel.getStyleClass().add("item-detail");

        Label bonusLabel = new Label(String.format("Bonus équipe : +%.1f%% ATK/PV/DEF/VIT", gc.getBonusPourcentage()));
        bonusLabel.getStyleClass().add("item-qte");

        VBox texte = new VBox(6, nomLabel, niveauLabel,
                GuiVisuels.creerBarreProgression(240, 14, gc.getNiveau(), GestionnaireCompagnons.NIVEAU_MAX),
                bonusLabel);
        texte.setAlignment(Pos.CENTER);

        VBox carte = new VBox(texte);
        carte.setAlignment(Pos.CENTER);
        carte.getStyleClass().add("carte-item-joueur");
        carte.setPrefWidth(300);
        compagnonBox.getChildren().setAll(carte);

        orBox.getChildren().setAll(
                GuiVisuels.creerFicheStat("Or disponible", String.format("%.0f", ctx.joueur.getOr())));

        Node carteAction;
        if (!gc.estAuNiveauMax()) {
            carteAction = GuiVisuels.creerCarteChoix(
                    "Améliorer → Niv." + (gc.getNiveau() + 1),
                    gc.getCoutProchainNiveau() + " or",
                    e -> onAction());
        } else if (gc.peutEvoluer()) {
            carteAction = GuiVisuels.creerCarteChoix(
                    "Évoluer → " + gc.getType().suivant().nom,
                    gc.getCoutEvolution() + " or",
                    e -> onAction());
        } else {
            carteAction = GuiVisuels.creerCarteChoix("Compagnon au maximum actuel", "", e -> {});
            carteAction.setOpacity(0.5);
            carteAction.setCursor(Cursor.DEFAULT);
            carteAction.setOnMouseClicked(null);
        }
        actionBox.getChildren().setAll(carteAction);
    }

    private void onAction() {
        GestionnaireCompagnons gc = ctx.gestionnaireCompagnons;
        ResultatCompagnon res = gc.peutEvoluer()
                ? gc.evoluer(ctx.joueur.getOr())
                : gc.ameliorer(ctx.joueur.getOr());

        if (res.succes()) {
            ctx.joueur.setOr(ctx.joueur.getOr() - res.orDepense());
            ctx.formation.appliquerBonusLiens();
            ctx.sauvegarde.sauvegarder(ctx);
        }
        info("Compagnons", res.message());
        rafraichir();
    }

    @FXML
    private void onRetour(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranMenuPrincipal.fxml");
            EcranMenuPrincipalController controller = loader.getController();
            controller.initData(ctx);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
