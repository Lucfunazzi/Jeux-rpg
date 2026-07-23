package lancement.gui;

import Personnage.PersonnageBase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;

public class EcranStagesController {

    private GameContext ctx;
    private LigneChapitre ligne;
    private Runnable onRetour;

    @FXML private Label titreLabel;
    @FXML private VBox statsBox;
    @FXML private VBox stagesBox;

    public void initData(GameContext ctx, LigneChapitre ligne, Runnable onRetour) {
        this.ctx = ctx;
        this.ligne = ligne;
        this.onRetour = onRetour;
        titreLabel.setText(ligne.label().toUpperCase());
        rafraichir();
    }

    private void rafraichir() {
        FlowPane stats = new FlowPane(10, 10);
        stats.setAlignment(Pos.CENTER);
        stats.getChildren().add(GuiVisuels.creerFicheStat("Or", String.format("%.0f", ctx.joueur.getOr())));
        if (ligne.elite()) {
            stats.getChildren().add(GuiVisuels.creerFicheStat("Énergie", ctx.gestionnaireEnergie.afficherEnergie()));
        }
        statsBox.getChildren().setAll(stats);

        stagesBox.getChildren().clear();
        boolean[] reussis   = ligne.stagesReussis().get();
        boolean[] debloques = ligne.stagesDebloques().get();

        for (int i = 1; i <= 10; i++) {
            stagesBox.getChildren().add(carteStage(i, reussis[i], debloques[i]));
        }
    }

    private Node carteStage(int numero, boolean reussi, boolean debloque) {
        String etoiles = ctx.gestionnaireEtoiles.getEtoiles(ligne.numeroChapitre(), numero, ligne.elite()).afficher();
        String runs = ligne.elite() ? "  ·  " + ctx.gestionnaireEnergie.getRunsEliteRestants(numero) + "/10 runs" : "";

        Label nom = new Label("Stage " + numero + " - " + ligne.titreStage().apply(numero));
        nom.getStyleClass().add("item-nom");
        nom.setWrapText(true);
        nom.setMaxWidth(320);

        Label detail = new Label((debloque ? etoiles : "Verrouillé") + runs);
        detail.getStyleClass().add("item-detail");

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.setMaxWidth(Double.MAX_VALUE);
        carte.setPrefWidth(420);
        carte.getStyleClass().add(reussi ? "carte-item-joueur" : "carte-item");

        if (debloque) {
            carte.setCursor(Cursor.HAND);
            carte.setOnMouseClicked(e -> lancerStage(numero, (Stage) ((Node) e.getSource()).getScene().getWindow()));
        } else {
            carte.setOpacity(0.4);
        }
        return carte;
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
                if (resultat.recompenses != null) {
                    annoncerRecompenses(resultat.recompenses);
                }
                for (PersonnageBase recrue : nouveauxRecrues) {
                    annoncerRecrue(recrue);
                }
                retourStages(stage);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Popup recapitulant les recompenses obtenues a la fin d'un stage reussi. */
    private void annoncerRecompenses(lancement.Stage.Recompenses r) {
        StringBuilder sb = new StringBuilder();
        sb.append("+ ").append(r.xp).append(" XP par personnage\n");
        sb.append("+ ").append(r.or).append(" or\n");
        if (r.equipement != null) {
            sb.append("+ Equipement obtenu : ").append(r.equipement).append("\n");
        }
        if (r.carteOrQuantite > 0) {
            sb.append("+ ").append(r.carteOrQuantite).append("x ").append(r.carteOrNom).append("\n");
        }
        if (r.pointsAbilite > 0) {
            sb.append("+ ").append(r.pointsAbilite).append(" point(s) d'habilete\n");
        }

        Label texte = new Label(sb.toString().trim());
        texte.setWrapText(true);
        texte.setStyle("-fx-font-size: 16px; -fx-text-fill: #f2c14e;");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Recompenses");
        alert.setHeaderText("Victoire !");
        alert.getDialogPane().setContent(texte);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("root-menu");
        alert.showAndWait();
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
