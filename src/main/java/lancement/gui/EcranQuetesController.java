package lancement.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Personnage.PersonnageBase;
import lancement.GameContext;
import lancement.Quetes.Quete;
import lancement.Quetes.QueteJournaliere;
import lancement.Quetes.QueteProgression;

public class EcranQuetesController {

    private GameContext ctx;

    @FXML private VBox quetesBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        ctx.gestionnaireQuetes.verifierRenouvellement();
        rafraichir();
    }

    private void rafraichir() {
        quetesBox.getChildren().clear();

        QueteJournaliere qj = ctx.gestionnaireQuetes.getQueteJournaliere();
        quetesBox.getChildren().add(section("[ Quete journaliere ]",
                qj.getEtat() + qj.getTitre() + "\n"
                        + qj.getDescription() + "\n"
                        + "Progression : " + qj.getProgression() + "\n"
                        + "Recompense  : " + qj.afficherRecompenses()));

        StringBuilder progression = new StringBuilder();
        for (QueteProgression q : ctx.gestionnaireQuetes.getQuetesVisibles(ctx)) {
            progression.append(q.getEtat()).append(q.getTitre()).append("\n")
                    .append("  ").append(q.getDescription()).append("\n")
                    .append("  Recompense : ").append(q.afficherRecompenses()).append("\n\n");
        }
        quetesBox.getChildren().add(section("[ Quetes de progression ]", progression.toString().trim()));
    }

    private Label section(String titre, String contenu) {
        Label label = new Label(titre + "\n" + contenu);
        label.getStyleClass().add("texte");
        label.setWrapText(true);
        return label;
    }

    @FXML
    private void onReclamer(ActionEvent event) {
        List<Quete> reclamables = new ArrayList<>();

        QueteJournaliere qj = ctx.gestionnaireQuetes.getQueteJournaliere();
        if (qj.isCompletee() && !qj.isReclamee()) reclamables.add(qj);

        for (QueteProgression q : ctx.gestionnaireQuetes.getQuetesVisibles(ctx))
            if (q.isCompletee() && !q.isReclamee()) reclamables.add(q);

        if (reclamables.isEmpty()) { info("Quetes", "Aucune recompense a reclamer."); return; }

        Map<String, Quete> map = new LinkedHashMap<>();
        List<String> libelles = new ArrayList<>();
        for (Quete q : reclamables) {
            String libelle = q.getTitre() + " - " + q.afficherRecompenses();
            libelles.add(libelle);
            map.put(libelle, q);
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(libelles.get(0), libelles);
        dialog.setTitle("Reclamer une recompense");
        dialog.setHeaderText(null);
        dialog.setContentText("Quete :");
        styliser(dialog);
        Optional<String> resultat = dialog.showAndWait();
        if (resultat.isEmpty()) return;

        Quete q = map.get(resultat.get());
        q.setReclamee(true);

        StringBuilder message = new StringBuilder("Recompenses recues pour : " + q.getTitre() + "\n");
        if (q.getRecompenseXP() > 0) {
            for (PersonnageBase p : ctx.formation.getEquipe()) p.gagnerExperience(q.getRecompenseXP());
            message.append("+ ").append(q.getRecompenseXP()).append(" XP a toute la formation\n");
        }
        if (q.getRecompenseOr() > 0) {
            ctx.joueur.ajouterOr(q.getRecompenseOr());
            message.append("+ ").append(q.getRecompenseOr()).append(" or\n");
        }
        if (q.getRecompenseParcheminC() > 0) {
            ctx.menuRecrutement.ajouterParcheminC(q.getRecompenseParcheminC());
            message.append("+ ").append(q.getRecompenseParcheminC()).append(" parchemins C\n");
        }

        ctx.sauvegarde.sauvegarder(ctx);
        info("Quetes", message.toString().trim());
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
