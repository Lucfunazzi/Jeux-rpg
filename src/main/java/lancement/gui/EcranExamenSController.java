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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireExamenS;
import lancement.Menus.MenuExamenS;

public class EcranExamenSController {

    private GameContext ctx;

    @FXML private Label infoLabel;
    @FXML private VBox stagesBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        ctx.gestionnaireExamenS.mettreAJour();
        rafraichir();
    }

    private void rafraichir() {
        GestionnaireExamenS g = ctx.gestionnaireExamenS;
        infoLabel.setText("1 tentative par stage et par jour (reset a minuit).\n"
                + "Premiere reussite d'un stage : boite garantie. Ensuite : 30% de chance.");

        FlowPane grille = new FlowPane(10, 10);
        grille.setAlignment(Pos.CENTER);
        for (int i = 1; i <= GestionnaireExamenS.NB_STAGES; i++) {
            grille.getChildren().add(carteStage(g, i));
        }
        stagesBox.getChildren().setAll(grille);
    }

    private Node carteStage(GestionnaireExamenS g, int numero) {
        boolean verrouille = !g.estDebloque(numero);
        boolean faitAujourdhui = g.estFaitAujourdhui(numero);
        boolean premiereFois = !verrouille && !faitAujourdhui && !g.estDejaReussi(numero);

        String etat;
        if (verrouille)          etat = "Verrouillé";
        else if (faitAujourdhui) etat = "Fait aujourd'hui";
        else if (premiereFois)   etat = "100% — première fois !";
        else                     etat = "30% de chance";

        Label nom = new Label("Stage " + numero);
        nom.getStyleClass().add("item-nom");
        Label statut = new Label(etat);
        statut.getStyleClass().add(premiereFois ? "item-qte" : "item-detail");

        VBox texte = new VBox(2, nom, statut);
        HBox carte = new HBox(texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add(premiereFois ? "carte-item-joueur" : "carte-item");
        carte.setPrefWidth(220);

        if (g.peutTenter(numero)) {
            carte.setCursor(Cursor.HAND);
            carte.setOnMouseClicked(e -> lancerStage(numero, (Stage) ((Node) e.getSource()).getScene().getWindow()));
        } else {
            carte.setOpacity(0.5);
        }
        return carte;
    }

    private void lancerStage(int numero, Stage stage) {
        if (ctx.formation.getEquipe().isEmpty()) {
            info("Examen de Rang S", "Votre formation est vide ! Ajoutez des personnages d'abord.");
            return;
        }

        MenuExamenS.ResultatExamenS resultat = ctx.menuExamenS.lancerStage(numero, ctx, Navigation.scannerSilencieux());
        if (!resultat.lance()) {
            rafraichir();
            return;
        }

        try {
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranCombat.fxml");
            EcranCombatController controller = loader.getController();
            controller.initCombat(resultat.etatInitial(), resultat.evenements(), resultat.victoire(),
                    v -> retourExamen(stage, resultat.victoire(), resultat.boiteGagnee()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void retourExamen(Stage stage, boolean victoire, boolean boiteGagnee) {
        if (victoire) {
            info("Examen de Rang S", boiteGagnee
                    ? "Victoire ! Vous obtenez 1x " + MenuExamenS.MATERIAU_BOITE_PIERRE_LV1 + " !"
                    : "Victoire ! Pas de boite cette fois...");
        }
        try {
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranExamenS.fxml");
            EcranExamenSController controller = loader.getController();
            controller.initData(ctx);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onRatisser(ActionEvent event) {
        String message = ctx.menuExamenS.ratisser(ctx);
        info("Ratissage", message);
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
