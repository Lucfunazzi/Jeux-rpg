package lancement.gui;

import Equipement.Inventaire;
import Personnage.PersonnageBase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireEtoilesPerso;
import lancement.Menus.MenuEtoilesPerso;

public class EcranEtoilesController {

    private GameContext ctx;

    @FXML private VBox contenuBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichir();
    }

    private void rafraichir() {
        Inventaire inv = ctx.inventaire;
        contenuBox.getChildren().clear();

        // ── Recrutement par fragments ────────────────────────────────────
        contenuBox.getChildren().add(titreSection("Recrutement par fragments"));
        List<String[]> recrutables = new ArrayList<>();
        for (String[] info : MenuEtoilesPerso.getCatalogue()) {
            if (GestionnaireEtoilesPerso.dejaRecruteParNom(ctx.personnagesRecruites, info[0])) continue;
            if (GestionnaireEtoilesPerso.getFragments(inv, info[0]) == 0) continue;
            recrutables.add(info);
        }
        if (recrutables.isEmpty()) {
            contenuBox.getChildren().add(texteVide(
                    "Aucun fragment de personnage pour l'instant. Obtenez-en dans le Recrutement par Fragments !"));
        } else {
            FlowPane grille = new FlowPane(10, 10);
            for (String[] info : recrutables) grille.getChildren().add(carteFragmentPerso(info[0], info[1]));
            contenuBox.getChildren().add(grille);
        }

        // ── Montée en étoile ──────────────────────────────────────────────
        contenuBox.getChildren().add(titreSection("Montée en étoile"));
        List<PersonnageBase> tous = new ArrayList<>();
        tous.add(ctx.joueur);
        tous.addAll(ctx.personnagesRecruites);
        List<PersonnageBase> eligibles = new ArrayList<>();
        for (PersonnageBase p : tous) if (p.getNbreEtoiles() < 5) eligibles.add(p);

        if (eligibles.isEmpty()) {
            contenuBox.getChildren().add(texteVide("Tous vos personnages sont à 5 étoiles !"));
        } else {
            FlowPane grille = new FlowPane(10, 10);
            for (PersonnageBase p : eligibles) grille.getChildren().add(carteMonteeEtoile(p));
            contenuBox.getChildren().add(grille);
        }
    }

    private Label titreSection(String texte) {
        Label l = new Label(texte);
        l.getStyleClass().add("section-titre");
        return l;
    }

    private Label texteVide(String texte) {
        Label l = new Label(texte);
        l.getStyleClass().add("item-vide");
        l.setWrapText(true);
        l.setMaxWidth(500);
        return l;
    }

    /** Carte pour un personnage recrutable via fragments. Clic -> Recruter (ou affiche la progression). */
    private Node carteFragmentPerso(String nom, String rarete) {
        int qte  = GestionnaireEtoilesPerso.getFragments(ctx.inventaire, nom);
        int cout = GestionnaireEtoilesPerso.coutFragmentsRecrutement(rarete);

        Label badge = GuiVisuels.creerBadgeRarete(rarete);
        Label nomLabel = new Label(nom);
        nomLabel.getStyleClass().add("item-nom");

        VBox texte = new VBox(4, nomLabel, GuiVisuels.creerBarreProgression(150, 14, qte, cout));
        HBox ligne = new HBox(10, badge, texte);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.getStyleClass().add("carte-item");
        ligne.setPrefWidth(260);
        ligne.setCursor(Cursor.HAND);
        ligne.setOnMouseClicked(e -> recruter(nom, rarete));
        return ligne;
    }

    /** Carte pour la montée en étoile d'un personnage recruté. Clic -> Monter en étoile (ou affiche la progression). */
    private Node carteMonteeEtoile(PersonnageBase p) {
        int etoiles   = p.getNbreEtoiles();
        int cout      = GestionnaireEtoilesPerso.coutFragmentsEtoile(p.getRarete(), etoiles);
        int fragments = GestionnaireEtoilesPerso.getFragments(ctx.inventaire, p.getNom());

        Label badge = GuiVisuels.creerBadgeRarete(p.getRarete());
        Label nomLabel = new Label(p.getNom() + "  " + "★".repeat(etoiles) + "☆".repeat(5 - etoiles));
        nomLabel.getStyleClass().add("item-nom");

        VBox texte = new VBox(4, nomLabel, GuiVisuels.creerBarreProgression(150, 14, fragments, cout));
        HBox ligne = new HBox(10, badge, texte);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.getStyleClass().add("carte-item");
        ligne.setPrefWidth(260);
        ligne.setCursor(Cursor.HAND);
        ligne.setOnMouseClicked(e -> monterEtoile(p));
        return ligne;
    }

    // ── Actions ──────────────────────────────────────────────────────────

    private void recruter(String nom, String rarete) {
        int qte  = GestionnaireEtoilesPerso.getFragments(ctx.inventaire, nom);
        int cout = GestionnaireEtoilesPerso.coutFragmentsRecrutement(rarete);
        if (qte < cout) {
            info("Recrutement", nom + " [" + rarete + "] : " + qte + " / " + cout
                    + " fragments.\nContinuez à en collecter pour le recruter.");
            return;
        }

        if (!confirmer("Recruter " + nom + " [" + rarete + "] pour " + cout + " fragments ?")) return;

        GestionnaireEtoilesPerso.recruterViaFragments(ctx.inventaire, nom, rarete);
        PersonnageBase nouveau = ctx.sauvegarde.creerPersonnageParNom(nom);
        if (nouveau == null) { info("Recrutement", "Erreur : personnage introuvable."); return; }

        ctx.personnagesRecruites.add(nouveau);
        ctx.sauvegarde.sauvegarder(ctx);
        info("Recrutement", nom + " [" + rarete + "] a rejoint l'équipe !\n" + cout + " fragments consommés.");
        rafraichir();
    }

    private void monterEtoile(PersonnageBase p) {
        int avant     = p.getNbreEtoiles();
        int cout      = GestionnaireEtoilesPerso.coutFragmentsEtoile(p.getRarete(), avant);
        int fragments = GestionnaireEtoilesPerso.getFragments(ctx.inventaire, p.getNom());
        if (fragments < cout) {
            info("Étoiles", p.getNom() + " : " + fragments + " / " + cout
                    + " fragments.\nContinuez à en collecter pour monter d'étoile.");
            return;
        }

        if (!confirmer("Monter " + p.getNom() + " à " + (avant + 1) + " étoile(s) pour " + cout + " fragments ?")) return;

        boolean succes = GestionnaireEtoilesPerso.monterEtoileViaFragments(ctx.inventaire, p);
        if (!succes) { info("Étoiles", "Fragments insuffisants."); return; }

        int apres = p.getNbreEtoiles();
        ctx.sauvegarde.sauvegarder(ctx);
        info("Étoiles", p.getNom() + " passe à " + apres + " étoile(s) !\n"
                + "Bonus stats : +" + (apres * 5) + "% ATK/DEF/PV/VIT");
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

    private boolean confirmer(String question) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, question, ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        styliser(confirm);
        Optional<ButtonType> resultat = confirm.showAndWait();
        return resultat.isPresent() && resultat.get() == ButtonType.YES;
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
