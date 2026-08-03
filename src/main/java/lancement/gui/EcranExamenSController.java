package lancement.gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
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
        GuiVisuels.afficherExplicationPremiereVisite(ctx, "Examen de Rang S", "Examen de Rang S");
        ctx.gestionnaireExamenS.mettreAJour();
        rafraichir();
    }

    private void rafraichir() {
        GestionnaireExamenS g = ctx.gestionnaireExamenS;
        infoLabel.setText("1 tentative par stage et par jour (reset a minuit).\n"
                + "Premiere reussite d'un stage : boite garantie. Ensuite : 30% de chance.");

        int progression = 0;
        while (progression < GestionnaireExamenS.NB_STAGES && g.estDejaReussi(progression + 1)) progression++;
        int prochainStage = Math.min(progression + 1, GestionnaireExamenS.NB_STAGES);

        stagesBox.getChildren().setAll(
                GuiVisuels.creerFicheStat("Progression", progression + " / " + GestionnaireExamenS.NB_STAGES),
                cartePaliers(progression),
                carteSpotlight(g, prochainStage));
    }

    /** Ligne de paliers de recompense tous les 10 etages (dore si deja atteint). */
    private Node cartePaliers(int progression) {
        HBox ligne = new HBox(10);
        ligne.setAlignment(Pos.CENTER);
        for (int m = 10; m <= GestionnaireExamenS.NB_STAGES; m += 10) {
            boolean atteint = progression >= m;

            Label icone = new Label("🎁");
            icone.setStyle("-fx-font-size: 18px;");
            Label label = new Label("Étage " + m);
            label.getStyleClass().add(atteint ? "item-qte" : "item-detail");

            VBox carte = new VBox(4, icone, label);
            carte.setAlignment(Pos.CENTER);
            carte.getStyleClass().add(atteint ? "carte-milestone-atteinte" : "carte-milestone");
            ligne.getChildren().add(carte);

            if (m + 10 <= GestionnaireExamenS.NB_STAGES) {
                Label fleche = new Label("→");
                fleche.getStyleClass().add("ordre-fleche");
                ligne.getChildren().add(fleche);
            }
        }
        return ligne;
    }

    /** Carte mise en avant pour la prochaine etape a jouer, avec un gros bouton d'action. */
    private Node carteSpotlight(GestionnaireExamenS g, int numero) {
        boolean faitAujourdhui = g.estFaitAujourdhui(numero);
        boolean premiereFois = !faitAujourdhui && !g.estDejaReussi(numero);

        Label titre = new Label("PROCHAIN DÉFI");
        titre.getStyleClass().add("groupe-titre");

        Label nom = new Label("Étage " + numero);
        nom.getStyleClass().add("titre");
        nom.setStyle("-fx-font-size: 26px;");

        String etatTxt = faitAujourdhui ? "Déjà tenté aujourd'hui — revenez demain"
                : premiereFois           ? "🎁 Première tentative : récompense garantie !"
                                          : "30% de chance d'obtenir une boîte";
        Label etat = new Label(etatTxt);
        etat.getStyleClass().add(premiereFois ? "item-qte" : "item-detail");

        Button defier = new Button(faitAujourdhui ? "Fait aujourd'hui" : "⚔ Défier");
        defier.getStyleClass().add("bouton-defier");
        defier.setDisable(faitAujourdhui);
        defier.setOnAction(e -> lancerStage(numero, (Stage) defier.getScene().getWindow()));

        VBox contenu = new VBox(8, titre, nom, etat, defier);
        contenu.setAlignment(Pos.CENTER);
        contenu.getStyleClass().add("carte-examen-spotlight");
        contenu.setPrefWidth(320);
        return contenu;
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
            GestionnaireMusique.jouerMusiqueExamenS(numero);
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
