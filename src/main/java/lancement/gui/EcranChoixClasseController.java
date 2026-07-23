package lancement.gui;

import Joueur.ChasseurDeDragon;
import Joueur.Chevalier;
import Joueur.Competences;
import Joueur.Elementaliste;
import Joueur.Invocateur;
import Joueur.Personnage_principale;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.Formation;
import lancement.GameContext;

public class EcranChoixClasseController {

    private final Competences competChevalier          = new Chevalier();
    private final Competences competChasseur           = new ChasseurDeDragon();
    private final Competences competMage                = new Elementaliste();
    private final Competences competConstellationniste  = new Invocateur();

    private GameContext ctx;
    private String pseudo;

    @FXML private Label sousTitre;
    @FXML private FlowPane classesBox;

    @FXML
    private void initialize() {
        classesBox.getChildren().setAll(
                carteClasse("Chevalier", competChevalier),
                carteClasse("Chasseur de Dragon", competChasseur),
                carteClasse("Mage", competMage),
                carteClasse("Constellationniste", competConstellationniste)
        );
    }

    private Node carteClasse(String nomClasse, Competences competences) {
        String[] noms = competences.getNomsCompetences();

        Label nom = new Label(nomClasse);
        nom.getStyleClass().add("item-nom");
        nom.setStyle("-fx-font-size: 16px;");

        Label special = new Label("Spéciale : " + noms[0]);
        special.getStyleClass().add("item-detail");
        special.setWrapText(true);
        special.setMaxWidth(220);

        Label ultime = new Label("Ultime : " + noms[1]);
        ultime.getStyleClass().add("item-detail");
        ultime.setWrapText(true);
        ultime.setMaxWidth(220);

        Label voirDetails = new Label("Voir les compétences");
        voirDetails.setStyle("-fx-font-size: 11px; -fx-text-fill: #4ea8f2; -fx-underline: true;");
        voirDetails.setCursor(Cursor.HAND);
        voirDetails.setOnMouseClicked(e -> { afficherDetails(nomClasse, competences); e.consume(); });

        VBox texte = new VBox(4, nom, special, ultime, voirDetails);
        texte.setAlignment(Pos.CENTER);

        VBox carte = new VBox(texte);
        carte.setAlignment(Pos.CENTER);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(240);
        carte.setCursor(Cursor.HAND);
        carte.setOnMouseClicked(e -> creerJoueur(nomClasse, competences));
        return carte;
    }

    public void initData(GameContext ctx, String pseudo) {
        this.ctx = ctx;
        this.pseudo = pseudo;
        sousTitre.setText("Bienvenue, " + pseudo + " ! Choisissez votre classe :");
    }

    /** Apercu des competences d'une classe, consultable avant de valider le choix. */
    private void afficherDetails(String nomClasse, Competences competences) {
        String[] noms = competences.getNomsCompetences();
        StringBuilder sb = new StringBuilder();
        sb.append("Attaque de base (100% ATK)\n\n");
        sb.append(noms[0]).append(" (speciale)\n   ")
          .append(GuiVisuels.capturerDescription(competences::descriptionAttaqueSpeciale)).append("\n\n");
        sb.append(noms[1]).append(" (ultime)\n   ")
          .append(GuiVisuels.capturerDescription(competences::descriptionUltime));

        Alert dialog = new Alert(Alert.AlertType.INFORMATION, sb.toString(), ButtonType.OK);
        dialog.setTitle(nomClasse);
        dialog.setHeaderText(null);
        styliser(dialog);
        dialog.showAndWait();
    }

    private void creerJoueur(String classe, Competences competences) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Confirmer la classe " + classe + " ?\nCe choix est definitif, vous ne pourrez plus en changer.",
                ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        styliser(confirmation);
        Optional<ButtonType> reponse = confirmation.showAndWait();
        if (reponse.isEmpty() || reponse.get() != ButtonType.YES) return;

        Personnage_principale joueur = new Personnage_principale(pseudo, 1);
        joueur.setGameContext(ctx);
        joueur.setChoixClasses(classe);
        joueur.setCompetencesChoisie(competences);
        ctx.joueur               = joueur;
        ctx.formation            = new Formation(ctx.joueur, ctx.gestionnaireCompagnons);
        ctx.personnagesRecruites = new ArrayList<>();

        try {
            Stage stage = (Stage) classesBox.getScene().getWindow();
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranMenuPrincipal.fxml");
            EcranMenuPrincipalController controller = loader.getController();
            controller.initData(ctx);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @FXML
    private void onRetour(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Navigation.changerEcran(stage, "/fxml/EcranAccueil.fxml");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void styliser(Dialog<?> dialog) {
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("root-menu");
    }
}
