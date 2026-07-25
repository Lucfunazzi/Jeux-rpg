package lancement.gui;

import Joueur.Competences;
import Joueur.Personnage_principale;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

/**
 * Fiche detaillee d'une classe, consultee avant de valider le choix definitif
 * a la creation du personnage : description de la classe + competences de base.
 */
public class EcranFicheClasseController {

    private GameContext ctx;
    private String pseudo;
    private String genre;
    private String classeInterne;
    private Competences competences;

    @FXML private Label titreClasse;
    @FXML private Label descriptionLabel;
    @FXML private FlowPane statsBox;
    @FXML private VBox competencesBox;

    public void initData(GameContext ctx, String pseudo, String genre,
                          String classeInterne, String nomAffiche, Competences competences) {
        this.ctx           = ctx;
        this.pseudo         = pseudo;
        this.genre          = genre;
        this.classeInterne  = classeInterne;
        this.competences    = competences;

        titreClasse.setText(nomAffiche);
        descriptionLabel.setText(description(classeInterne, genre));

        Personnage_principale.StatsClasse stats = Personnage_principale.statsPourClasse(classeInterne);
        statsBox.getChildren().setAll(
                GuiVisuels.creerFicheStat("PV", String.valueOf((int) stats.vie())),
                GuiVisuels.creerFicheStat("ATK", String.valueOf((int) stats.attaque())),
                GuiVisuels.creerFicheStat("DEF", String.valueOf((int) stats.defense())),
                GuiVisuels.creerFicheStat("VIT", String.valueOf((int) stats.vitesse()))
        );

        String[] noms = competences.getNomsCompetences();
        Node carteSpeciale = carteCompetence(noms[0] + " (spéciale)",
                GuiVisuels.capturerDescription(competences::descriptionAttaqueSpeciale));
        Node carteUltime = carteCompetence(noms[1] + " (ultime)",
                GuiVisuels.capturerDescription(competences::descriptionUltime));
        competencesBox.getChildren().setAll(carteSpeciale, carteUltime);
    }

    private Node carteCompetence(String nom, String description) {
        Label nomLabel = new Label(nom);
        nomLabel.getStyleClass().add("item-nom");

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("item-detail");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(460);

        VBox carte = new VBox(4, nomLabel, descLabel);
        carte.getStyleClass().add("carte-item");
        carte.setMaxWidth(500);
        return carte;
    }

    private static String description(String classeInterne, String genre) {
        boolean femme = "Femme".equals(genre);
        return switch (classeInterne) {
            case "Chevalier" -> femme
                    ? "La Chevalière est une combattante robuste, experte en armes changeantes et en défense. Elle excelle pour encaisser les coups et protéger son équipe."
                    : "Le Chevalier est un combattant robuste, expert en armes changeantes et en défense. Il excelle pour encaisser les coups et protéger son équipe.";
            case "Chasseur de Dragon" -> femme
                    ? "La Chasseuse de Dragon manie la magie draconique de l'eau avec une force brute redoutable. Elle frappe fort et vite au corps à corps."
                    : "Le Chasseur de Dragon manie la magie draconique de l'eau avec une force brute redoutable. Il frappe fort et vite au corps à corps.";
            case "Mage" -> femme
                    ? "La Mage manipule la glace et les cristaux pour infliger de lourds dégâts à distance, tout en perturbant ses ennemis avec des effets de contrôle."
                    : "Le Mage manipule la glace et les cristaux pour infliger de lourds dégâts à distance, tout en perturbant ses ennemis avec des effets de contrôle.";
            case "Constellationniste", "Invocateur" -> femme
                    ? "L'Invocatrice est spécialisée dans les invocations stellaires et la magie des étoiles. Elle fait appel à des esprits célestes pour combattre à ses côtés."
                    : "L'Invocateur est spécialisé dans les invocations stellaires et la magie des étoiles. Il fait appel à des esprits célestes pour combattre à ses côtés.";
            default -> "";
        };
    }

    @FXML
    private void onChoisir(ActionEvent event) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Confirmer la classe " + titreClasse.getText() + " ?\nCe choix est definitif, vous ne pourrez plus en changer.",
                ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        styliser(confirmation);
        Optional<ButtonType> reponse = confirmation.showAndWait();
        if (reponse.isEmpty() || reponse.get() != ButtonType.YES) return;

        Personnage_principale joueur = new Personnage_principale(pseudo, 1);
        joueur.setGameContext(ctx);
        joueur.setGenre(genre);
        joueur.setChoixClasses(classeInterne);
        joueur.appliquerStatsClasse(classeInterne);
        joueur.setCompetencesChoisie(competences);
        ctx.joueur               = joueur;
        ctx.formation            = new Formation(ctx.joueur, ctx.gestionnaireCompagnons);
        ctx.formation.setGestionnaireTitres(ctx.gestionnaireTitres);
        ctx.personnagesRecruites = new ArrayList<>();

        try {
            Stage stage = (Stage) titreClasse.getScene().getWindow();
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
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranChoixClasse.fxml");
            EcranChoixClasseController controller = loader.getController();
            controller.initData(ctx, pseudo);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void styliser(Dialog<?> dialog) {
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("root-menu");
    }
}
