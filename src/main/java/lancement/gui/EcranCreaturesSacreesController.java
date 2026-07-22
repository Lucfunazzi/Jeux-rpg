package lancement.gui;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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

    @FXML private Label infoLabel;
    @FXML private VBox actionsBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichir();
    }

    private void rafraichir() {
        Gestionnaire_pet gcs = ctx.gestionnaireCreaturesSacrees;
        actionsBox.getChildren().clear();

        if (!gcs.isOeufDebloque()) {
            infoLabel.setText("Aucun oeuf obtenu pour l'instant.\n"
                    + "Terminez le Chapitre 2 Elite pour recevoir un Oeuf Mysterieux !");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Creature : ").append(gcs.getType().nom).append("\n");
        sb.append("Niveau   : ").append(gcs.getNiveau()).append(" / ").append(Gestionnaire_pet.NIVEAU_MAX).append("\n");
        sb.append("XP       : ").append(gcs.getExperience()).append(" / ").append(gcs.getExperienceMax()).append("\n");
        sb.append(String.format("Bonus equipe : ATK +%.0f | PV +%.0f | DEF +%.0f | VIT +%.0f",
                gcs.getType().getATK(gcs.getNiveau()), gcs.getType().getPV(gcs.getNiveau()),
                gcs.getType().getDEF(gcs.getNiveau()), gcs.getType().getVIT(gcs.getNiveau())));
        sb.append("\n");
        if (gcs.getType().peutEvoluer()) sb.append("Prochaine evolution : ").append(gcs.getType().suivant().nom);
        else sb.append("Forme finale : Igneel veille sur vous !");

        if (gcs.entrainementEnCours()) {
            LocalDateTime debut = gcs.getDebutEntrainement();
            Entrainement  actif = gcs.getEntrainementActif();
            long heuresEcoulees   = Duration.between(debut, LocalDateTime.now()).toHours();
            long minutesEcoulees  = Duration.between(debut, LocalDateTime.now()).toMinutes() % 60;
            long heuresRestantes  = actif.dureeHeures - heuresEcoulees;
            long minutesRestantes = 60 - minutesEcoulees;
            if (minutesRestantes == 60) { heuresRestantes++; minutesRestantes = 0; }
            sb.append("\n\nEntrainement (").append(actif.libelle).append(") en cours...");
            sb.append(String.format("%nTemps restant : ~%dh%02dm", heuresRestantes, minutesRestantes));

        } else if (gcs.entrainementTermine()) {
            sb.append("\n\nEntrainement termine ! Reclamez votre XP.");
            ajouterBouton("Reclamer l'XP", e -> {
                String msg = gcs.reclamerEntrainement();
                ctx.formation.appliquerBonusLiens();
                ctx.sauvegarde.sauvegarder(ctx);
                if (msg != null) info("Creatures Sacrees", msg);
                rafraichir();
            });

        } else if (gcs.estAuNiveauMax() && gcs.getType().peutEvoluer()) {
            sb.append("\n\nNiveau max atteint !");
            ajouterBouton("Evoluer -> " + gcs.getType().suivant().nom, e -> {
                String msg = gcs.evoluer();
                ctx.formation.appliquerBonusLiens();
                ctx.sauvegarde.sauvegarder(ctx);
                info("Creatures Sacrees", msg);
                rafraichir();
            });

        } else if (gcs.estAuNiveauMax()) {
            sb.append("\n\nIgneel est a son niveau maximum !");

        } else {
            for (Entrainement entr : Entrainement.values()) {
                ajouterBouton("Entrainement " + entr.libelle + " -> +" + entr.xpRecompense + " XP",
                        e -> lancerEntrainement(entr));
            }
        }

        infoLabel.setText(sb.toString());
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

    private void ajouterBouton(String libelle, EventHandler<ActionEvent> action) {
        Button bouton = new Button(libelle);
        bouton.getStyleClass().add("menu-bouton");
        bouton.setWrapText(true);
        bouton.setPrefWidth(280);
        bouton.setOnAction(action);
        actionsBox.getChildren().add(bouton);
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
