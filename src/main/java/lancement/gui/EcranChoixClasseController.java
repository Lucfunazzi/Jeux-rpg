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
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
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
    @FXML private Button btnChevalier;
    @FXML private Button btnChasseur;
    @FXML private Button btnMage;
    @FXML private Button btnConstellationniste;

    @FXML
    private void initialize() {
        libeller(btnChevalier, "Chevalier", competChevalier);
        libeller(btnChasseur, "Chasseur de Dragon", competChasseur);
        libeller(btnMage, "Mage", competMage);
        libeller(btnConstellationniste, "Constellationniste", competConstellationniste);
    }

    private void libeller(Button bouton, String nomClasse, Competences competences) {
        String[] noms = competences.getNomsCompetences();
        bouton.setText(nomClasse + "\nSpeciale : " + noms[0] + "\nUltime : " + noms[1]);
    }

    public void initData(GameContext ctx, String pseudo) {
        this.ctx = ctx;
        this.pseudo = pseudo;
        sousTitre.setText("Bienvenue, " + pseudo + " ! Choisissez votre classe :");
    }

    @FXML private void onChoixChevalier(ActionEvent e)         { creerJoueur(e, "Chevalier", competChevalier); }
    @FXML private void onChoixChasseur(ActionEvent e)          { creerJoueur(e, "Chasseur de Dragon", competChasseur); }
    @FXML private void onChoixMage(ActionEvent e)               { creerJoueur(e, "Mage", competMage); }
    @FXML private void onChoixConstellationniste(ActionEvent e) { creerJoueur(e, "Constellationniste", competConstellationniste); }

    @FXML private void onDetailsChevalier(ActionEvent e)         { afficherDetails("Chevalier", competChevalier); }
    @FXML private void onDetailsChasseur(ActionEvent e)          { afficherDetails("Chasseur de Dragon", competChasseur); }
    @FXML private void onDetailsMage(ActionEvent e)               { afficherDetails("Mage", competMage); }
    @FXML private void onDetailsConstellationniste(ActionEvent e) { afficherDetails("Constellationniste", competConstellationniste); }

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

    private void creerJoueur(ActionEvent event, String classe, Competences competences) {
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
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
