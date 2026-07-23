package lancement.gui;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import lancement.Gestionnaires.Gestionnaire_pet;
import lancement.Gestionnaires.Gestionnaire_pet.Entrainement;

public class EcranCreaturesSacreesController {

    private GameContext ctx;

    @FXML private VBox infoBox;
    @FXML private VBox actionsBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichir();
    }

    private void rafraichir() {
        Gestionnaire_pet gcs = ctx.gestionnaireCreaturesSacrees;
        actionsBox.getChildren().clear();

        if (!gcs.isOeufDebloque()) {
            Label vide = new Label("Aucun oeuf obtenu pour l'instant.\n"
                    + "Terminez le Chapitre 2 Elite pour recevoir un Oeuf Mysterieux !");
            vide.getStyleClass().add("item-vide");
            vide.setWrapText(true);
            vide.setMaxWidth(380);
            infoBox.getChildren().setAll(vide);
            return;
        }

        Label nomLabel = new Label(gcs.getType().nom);
        nomLabel.getStyleClass().add("item-nom");
        nomLabel.setStyle("-fx-font-size: 20px;");

        Label niveauLabel = new Label("Niveau " + gcs.getNiveau() + " / " + Gestionnaire_pet.NIVEAU_MAX);
        niveauLabel.getStyleClass().add("item-detail");

        Label bonusLabel = new Label(String.format("ATK +%.0f | PV +%.0f | DEF +%.0f | VIT +%.0f",
                gcs.getType().getATK(gcs.getNiveau()), gcs.getType().getPV(gcs.getNiveau()),
                gcs.getType().getDEF(gcs.getNiveau()), gcs.getType().getVIT(gcs.getNiveau())));
        bonusLabel.getStyleClass().add("item-qte");

        Label evoLabel = new Label(gcs.getType().peutEvoluer()
                ? "Prochaine évolution : " + gcs.getType().suivant().nom
                : "Forme finale : Igneel veille sur vous !");
        evoLabel.getStyleClass().add("item-detail");

        VBox texte = new VBox(6, nomLabel, niveauLabel,
                GuiVisuels.creerBarreProgression(240, 14, gcs.getNiveau(), Gestionnaire_pet.NIVEAU_MAX),
                GuiVisuels.creerBarreXP(240, 14, gcs.getExperience(), gcs.getExperienceMax()),
                bonusLabel, evoLabel);
        texte.setAlignment(Pos.CENTER);

        VBox carte = new VBox(texte);
        carte.setAlignment(Pos.CENTER);
        carte.getStyleClass().add("carte-item-joueur");
        carte.setPrefWidth(320);
        infoBox.getChildren().setAll(carte);

        if (gcs.entrainementEnCours()) {
            LocalDateTime debut = gcs.getDebutEntrainement();
            Entrainement  actif = gcs.getEntrainementActif();
            long heuresEcoulees   = Duration.between(debut, LocalDateTime.now()).toHours();
            long minutesEcoulees  = Duration.between(debut, LocalDateTime.now()).toMinutes() % 60;
            long heuresRestantes  = actif.dureeHeures - heuresEcoulees;
            long minutesRestantes = 60 - minutesEcoulees;
            if (minutesRestantes == 60) { heuresRestantes++; minutesRestantes = 0; }
            String tempsRestant = String.format("Temps restant : ~%dh%02dm", heuresRestantes, minutesRestantes);
            ajouterCarteInerte("Entraînement (" + actif.libelle + ") en cours...", tempsRestant);

        } else if (gcs.entrainementTermine()) {
            ajouterCarteAction("Réclamer l'XP", "Entraînement terminé !", e -> {
                String msg = gcs.reclamerEntrainement();
                ctx.formation.appliquerBonusLiens();
                ctx.sauvegarde.sauvegarder(ctx);
                if (msg != null) info("Creatures Sacrees", msg);
                rafraichir();
            });

        } else if (gcs.estAuNiveauMax() && gcs.getType().peutEvoluer()) {
            ajouterCarteAction("Évoluer → " + gcs.getType().suivant().nom, "Niveau max atteint", e -> {
                String msg = gcs.evoluer();
                ctx.formation.appliquerBonusLiens();
                ctx.sauvegarde.sauvegarder(ctx);
                info("Creatures Sacrees", msg);
                rafraichir();
            });

        } else if (gcs.estAuNiveauMax()) {
            ajouterCarteInerte("Igneel est à son niveau maximum !", "");

        } else {
            for (Entrainement entr : Entrainement.values()) {
                ajouterCarteAction("Entraînement " + entr.libelle,
                        "+" + entr.xpRecompense + " XP  (" + entr.dureeHeures + "h)",
                        e -> lancerEntrainement(entr));
            }
        }
    }

    private void lancerEntrainement(Entrainement e) {
        Gestionnaire_pet gcs = ctx.gestionnaireCreaturesSacrees;
        boolean ok = gcs.lancerEntrainement(e);
        if (ok) {
            ctx.sauvegarde.sauvegarder(ctx);
            info("Creatures Sacrees", "Entrainement " + e.libelle + " lance ! Revenez dans " + e.dureeHeures + "h.");
        } else {
            info("Creatures Sacrees", "Impossible de lancer l'entrainement.");
        }
        rafraichir();
    }

    private void ajouterCarteAction(String titre, String description, EventHandler<javafx.scene.input.MouseEvent> action) {
        actionsBox.getChildren().add(GuiVisuels.creerCarteChoix(titre, description, action));
    }

    private void ajouterCarteInerte(String titre, String description) {
        Node carte = GuiVisuels.creerCarteChoix(titre, description, e -> {});
        carte.setCursor(Cursor.DEFAULT);
        carte.setOnMouseClicked(null);
        actionsBox.getChildren().add(carte);
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
