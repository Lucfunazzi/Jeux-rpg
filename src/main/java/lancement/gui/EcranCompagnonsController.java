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
import javafx.scene.layout.HBox;
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
        GuiVisuels.afficherExplicationPremiereVisite(ctx, "Compagnons", "Compagnons");
        rafraichir();
    }

    private void rafraichir() {
        GestionnaireCompagnons gc = ctx.gestionnaireCompagnons;

        Label nomLabel = new Label(gc.getType().nom);
        nomLabel.getStyleClass().add("item-nom");
        nomLabel.setStyle("-fx-font-size: 20px;");

        Label niveauLabel = new Label("Niveau " + gc.getNiveau() + " / " + GestionnaireCompagnons.NIVEAU_MAX);
        niveauLabel.getStyleClass().add("item-detail");

        Label bonusLabel = new Label(String.format("Bonus équipe : ATK +%.0f | PV +%.0f | DEF +%.0f | VIT +%.0f",
                gc.getBonusATK(), gc.getBonusPV(), gc.getBonusDEF(), gc.getBonusVIT()));
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
                GuiVisuels.creerFicheStat("●", "Or disponible", String.format("%.0f", ctx.joueur.getOr())));

        Node carteAction;
        if (!gc.estAuNiveauMax()) {
            carteAction = carteAction("⚒",
                    "Améliorer → Niv." + (gc.getNiveau() + 1),
                    gc.getCoutProchainNiveau() + " or",
                    e -> onAction());
        } else if (gc.peutEvoluer()) {
            carteAction = carteAction("☄",
                    "Évoluer → " + gc.getType().suivant().nom,
                    gc.getCoutEvolution() + " or",
                    e -> onAction());
        } else {
            carteAction = carteAction("✔", "Compagnon au maximum actuel", "", e -> {});
            carteAction.setOpacity(0.5);
            carteAction.setCursor(Cursor.DEFAULT);
            carteAction.setOnMouseClicked(null);
        }
        actionBox.getChildren().setAll(carteAction);
    }

    /** Variante de GuiVisuels.creerCarteChoix() avec une icone. */
    private Node carteAction(String icone, String titre, String description, javafx.event.EventHandler<javafx.scene.input.MouseEvent> action) {
        Label iconeLabel = new Label(icone);
        iconeLabel.getStyleClass().add("fiche-stat-icone");

        Label titreLabel = new Label(titre);
        titreLabel.getStyleClass().add("item-nom");

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("item-detail");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(280);

        VBox texte = new VBox(4, titreLabel, descLabel);
        HBox carte = new HBox(10, iconeLabel, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(360);
        carte.setCursor(Cursor.HAND);
        carte.setOnMouseClicked(action);
        return carte;
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
