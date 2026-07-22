package lancement.gui;

import Personnage.PersonnageBase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;

public class EcranStagesController {

    private GameContext ctx;
    private LigneChapitre ligne;
    private Runnable onRetour;

    @FXML private Label titreLabel;
    @FXML private Label orLabel;
    @FXML private VBox stagesBox;

    public void initData(GameContext ctx, LigneChapitre ligne, Runnable onRetour) {
        this.ctx = ctx;
        this.ligne = ligne;
        this.onRetour = onRetour;
        titreLabel.setText(ligne.label().toUpperCase());
        rafraichir();
    }

    private void rafraichir() {
        orLabel.setText(ligne.elite()
                ? "Or : " + String.format("%.0f", ctx.joueur.getOr()) + "  |  " + ctx.gestionnaireEnergie.afficherEnergie()
                : "Or : " + String.format("%.0f", ctx.joueur.getOr()));

        stagesBox.getChildren().clear();
        boolean[] reussis   = ligne.stagesReussis().get();
        boolean[] debloques = ligne.stagesDebloques().get();

        for (int i = 1; i <= 10; i++) {
            String etat  = reussis[i] ? "[OK] " : debloques[i] ? "[  ] " : "[###] ";
            String runs  = ligne.elite() ? "  (" + ctx.gestionnaireEnergie.getRunsEliteRestants(i) + "/10 runs)" : "";
            String texte = etat + "Stage " + i + " - " + ligne.titreStage().apply(i) + runs;

            Button bouton = new Button(texte);
            bouton.getStyleClass().add("menu-bouton");
            bouton.setMaxWidth(Double.MAX_VALUE);
            if (!debloques[i]) {
                bouton.setDisable(true);
            } else {
                int numero = i;
                bouton.setOnAction(e -> lancerStage(numero, (Stage) ((Node) e.getSource()).getScene().getWindow()));
            }
            stagesBox.getChildren().add(bouton);
        }
    }

    private void lancerStage(int numero, Stage stage) {
        if (ligne.elite()) ctx.gestionnaireEnergie.mettreAJourRecharge();

        List<PersonnageBase> avant = new ArrayList<>(ctx.personnagesRecruites);
        lancement.Stage.ResultatStage resultat = ligne.lancerStage().apply(ctx, numero);
        List<PersonnageBase> nouveauxRecrues = new ArrayList<>(ctx.personnagesRecruites);
        nouveauxRecrues.removeAll(avant);

        try {
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranCombat.fxml");
            EcranCombatController controller = loader.getController();
            controller.initCombat(resultat.etatInitial, resultat.evenements, resultat.victoire, v -> {
                for (PersonnageBase recrue : nouveauxRecrues) {
                    annoncerRecrue(recrue);
                }
                retourStages(stage);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Message bien visible quand un personnage rejoint l'equipe automatiquement (recompense de quete). */
    private void annoncerRecrue(PersonnageBase recrue) {
        Label texte = new Label("✨ " + recrue.getNom() + " a rejoint votre equipe ! ✨\n["
                + recrue.getRarete() + "] " + recrue.getRole());
        texte.setWrapText(true);
        texte.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #f2c14e; -fx-text-alignment: center;");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Nouveau personnage !");
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(texte);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("root-menu");
        alert.showAndWait();
    }

    private void retourStages(Stage stage) {
        try {
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranStages.fxml");
            EcranStagesController controller = loader.getController();
            controller.initData(ctx, ligne, onRetour);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onRetour() {
        onRetour.run();
    }
}
