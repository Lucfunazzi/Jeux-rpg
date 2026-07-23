package lancement.gui;

import Joueur.ArbreCompetences;
import Joueur.Personnage_principale;
import Personnage.PersonnageBase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.Formation;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireLiens;
import lancement.Menus.MenuAbilite;

public class EcranFormationController {

    private GameContext ctx;

    @FXML private HBox ligneTankBox;
    @FXML private HBox ligneDpsBox;
    @FXML private HBox ligneSupportBox;
    @FXML private VBox competenceBox;
    @FXML private FlowPane liensBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichir();
    }

    private void rafraichir() {
        Formation f = ctx.formation;

        ligneTankBox.getChildren().setAll(
                f.getTank() != null ? creerCarte(f.getTank()) : creerCarteVide("Tank"));

        List<Node> dps = new ArrayList<>();
        dps.add(creerCarte(ctx.joueur));
        for (PersonnageBase a : f.getAttaquants()) dps.add(creerCarte(a));
        ligneDpsBox.getChildren().setAll(dps);

        if (f.getSupports().isEmpty()) {
            ligneSupportBox.getChildren().setAll(creerCarteVide("Support"));
        } else {
            List<Node> supports = new ArrayList<>();
            for (PersonnageBase s : f.getSupports()) supports.add(creerCarte(s));
            ligneSupportBox.getChildren().setAll(supports);
        }

        competenceBox.getChildren().setAll(
                GuiVisuels.creerFicheStat("Compétence spéciale active", nomCompetenceActive()));

        List<GestionnaireLiens.Lien> liens = f.getLiensActifs();
        if (liens.isEmpty()) {
            liensBox.getChildren().clear();
        } else {
            List<Node> chips = new ArrayList<>();
            for (GestionnaireLiens.Lien l : liens) chips.add(carteLien(l));
            liensBox.getChildren().setAll(chips);
        }
    }

    private Node carteLien(GestionnaireLiens.Lien l) {
        Label nom = new Label(l.nom);
        nom.getStyleClass().add("item-nom");
        Label detail = new Label(l.description);
        detail.getStyleClass().add("item-detail");
        detail.setWrapText(true);
        detail.setMaxWidth(200);

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item-joueur");
        carte.setPrefWidth(220);
        return carte;
    }

    @FXML
    private void onAjouter(ActionEvent event) {
        if (ctx.formation.estPleine()) { info("Formation", "La formation est pleine (5/5) !"); return; }

        List<PersonnageBase> disponibles = new ArrayList<>();
        disponibles.add(ctx.joueur);
        disponibles.addAll(ctx.personnagesRecruites);

        List<PersonnageBase> equipe = ctx.formation.getEquipe();
        List<PersonnageBase> ajoutables = new ArrayList<>();
        for (PersonnageBase p : disponibles) if (!equipe.contains(p)) ajoutables.add(p);

        if (ajoutables.isEmpty()) { info("Formation", "Aucun personnage disponible a ajouter."); return; }

        PersonnageBase choisi = choisirPersonnage("Ajouter un personnage", ajoutables);
        if (choisi == null) return;

        info("Formation", ctx.formation.ajouterPersonnage(choisi));
        rafraichir();
    }

    @FXML
    private void onRetirer(ActionEvent event) {
        List<PersonnageBase> equipe = ctx.formation.getEquipe();
        List<PersonnageBase> retirables = new ArrayList<>(equipe);
        retirables.remove(0); // index 0 = joueur, toujours en premier, non retirable

        if (retirables.isEmpty()) { info("Formation", "Aucun personnage a retirer (vous etes seul dans l'equipe)."); return; }

        PersonnageBase choisi = choisirPersonnage("Retirer un personnage", retirables);
        if (choisi == null) return;

        info("Formation", ctx.formation.retirerPersonnage(choisi));
        rafraichir();
    }

    @FXML
    private void onChangerCompetence(ActionEvent event) {
        Personnage_principale joueur = ctx.joueur;
        ArbreCompetences arbre = joueur.getArbreCompetences();
        String classe = joueur.getChoixClasses();
        int actuelle = joueur.getCompetenceSpecialeActive();

        String nomOriginal = joueur.getCompetencesChoisie() != null
                ? joueur.getCompetencesChoisie().getNomsCompetences()[joueur.getChoixComp() - 1]
                : "Competence originale";
        boolean arbre1Dispo = arbre.isNoeud10Debloque();
        String nomArbre1    = MenuAbilite.getNomCompetence(classe, 1);
        boolean arbre2Dispo = arbre.isNoeud10Arbre2Debloque();
        String nomArbre2    = MenuAbilite.getNomCompetence(classe, 2);

        List<Integer> options = List.of(0, 1, 2);
        Integer choix = GuiVisuels.choisirParmiCartes("Compétence spéciale", options,
                i -> carteCompetenceChoix(i, nomOriginal, nomArbre1, nomArbre2, arbre1Dispo, arbre2Dispo, actuelle));
        if (choix == null) return;

        int index = choix;
        switch (index) {
            case 0 -> {
                joueur.setCompetenceSpecialeActive(0);
                info("Competence", "Competence active : " + nomOriginal);
            }
            case 1 -> {
                if (!arbre1Dispo) info("Competence", "Competence verrouillee - completez l'arbre 1 d'abord.");
                else { joueur.setCompetenceSpecialeActive(1); info("Competence", "Competence active : " + nomArbre1); }
            }
            case 2 -> {
                if (!arbre2Dispo) info("Competence", "Competence verrouillee - completez l'arbre 2 d'abord.");
                else { joueur.setCompetenceSpecialeActive(2); info("Competence", "Competence active : " + nomArbre2); }
            }
        }
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

    // ── Utilitaires ───────────────────────────────────────────────────────

    /** Petite carte visuelle (rareté + PV) pour un slot de formation occupé. */
    private Node creerCarte(PersonnageBase p) {
        Label badge = GuiVisuels.creerBadgeRarete(p.getRarete());
        Label nom = new Label(p.getNom());
        nom.getStyleClass().add("texte");
        nom.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        HBox entete = new HBox(6, badge, nom);
        entete.setAlignment(Pos.CENTER);

        Label role = new Label(p.getRole());
        role.setStyle("-fx-font-size: 11px; -fx-text-fill: #9a9ac0;");

        VBox carte = new VBox(4, entete, role, GuiVisuels.creerBarrePV(110, 10, p.getVie(), p.getVieMax()));
        carte.getStyleClass().add("carte-combat");
        carte.setAlignment(Pos.CENTER);
        carte.setPrefWidth(130);
        return carte;
    }

    /** Carte grisée pour un slot de formation vide (ex: Tank non assigné). */
    private Node creerCarteVide(String role) {
        Label label = new Label(role + "\n[vide]");
        label.getStyleClass().add("texte");
        label.setStyle("-fx-opacity: 0.5; -fx-text-alignment: center;");
        label.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        VBox carte = new VBox(label);
        carte.getStyleClass().add("carte-combat");
        carte.setAlignment(Pos.CENTER);
        carte.setPrefWidth(130);
        carte.setPrefHeight(74);
        return carte;
    }

    private String nomCompetenceActive() {
        Personnage_principale joueur = ctx.joueur;
        return switch (joueur.getCompetenceSpecialeActive()) {
            case 1  -> MenuAbilite.getNomCompetence(joueur.getChoixClasses(), 1);
            case 2  -> MenuAbilite.getNomCompetence(joueur.getChoixClasses(), 2);
            default -> joueur.getCompetencesChoisie() != null
                    ? joueur.getCompetencesChoisie().getNomsCompetences()[joueur.getChoixComp() - 1]
                    : "Aucune";
        };
    }

    private Node carteCompetenceChoix(int index, String nomOriginal, String nomArbre1, String nomArbre2,
            boolean arbre1Dispo, boolean arbre2Dispo, int actuelle) {
        String nom;
        String statut;
        switch (index) {
            case 0 -> { nom = nomOriginal + " (originale)"; statut = actuelle == 0 ? "ACTIVE" : ""; }
            case 1 -> { nom = nomArbre1 + " (Arbre 1)"; statut = !arbre1Dispo ? "VERROUILLÉ" : actuelle == 1 ? "ACTIVE" : ""; }
            default -> { nom = nomArbre2 + " (Arbre 2)"; statut = !arbre2Dispo ? "VERROUILLÉ" : actuelle == 2 ? "ACTIVE" : ""; }
        }

        Label nomLabel = new Label(nom);
        nomLabel.getStyleClass().add("item-nom");

        HBox carte = new HBox(10, nomLabel);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("ACTIVE".equals(statut) ? "carte-item-joueur" : "carte-item");
        carte.setPrefWidth(280);

        if (!statut.isEmpty()) {
            Label statutLabel = new Label(statut);
            statutLabel.getStyleClass().add("item-qte");
            carte.getChildren().add(statutLabel);
        }
        return carte;
    }

    private PersonnageBase choisirPersonnage(String titre, List<PersonnageBase> options) {
        return GuiVisuels.choisirParmiCartes(titre, options, this::cartePersonnageChoix);
    }

    private Node cartePersonnageChoix(PersonnageBase p) {
        Label badge = GuiVisuels.creerBadgeRarete(p.getRarete());
        Label nom = new Label(p.getNom());
        nom.getStyleClass().add("item-nom");
        Label detail = new Label("Niv. " + p.getNiveau() + "  ·  " + p.getRole());
        detail.getStyleClass().add("item-detail");

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(10, badge, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(260);
        return carte;
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
