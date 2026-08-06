package lancement.gui;

import Combat.Combat;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Gestionnaires.AreneData;
import lancement.Gestionnaires.GestionnaireArene;
import lancement.Menus.MenuArene;

public class EcranAreneAdversairesController {

    private GameContext ctx;
    private GestionnaireArene gestionnaireArene;
    private AreneData joueurArene;
    private Runnable onRetour;

    @FXML private VBox adversairesBox;
    @FXML private Label labelCombativiteJoueur;

    public void initData(GameContext ctx, GestionnaireArene gestionnaireArene, AreneData joueurArene, Runnable onRetour) {
        this.ctx = ctx;
        this.gestionnaireArene = gestionnaireArene;
        this.joueurArene = joueurArene;
        this.onRetour = onRetour;
        rafraichir();
    }

    private void rafraichir() {
        adversairesBox.getChildren().clear();
        List<AreneData> adversaires = gestionnaireArene.getAdversairesVisibles(joueurArene.getRang());

        double combativiteJoueur = ctx.formation.getEquipe().stream()
                .filter(p -> p != null)
                .mapToDouble(PersonnageBase::getCombativite)
                .sum();
        labelCombativiteJoueur.setText("Ta combativité : " + GuiVisuels.formaterMontant(combativiteJoueur));

        for (AreneData a : adversaires) {
            adversairesBox.getChildren().add(carteAdversaire(a));
        }
    }

    /** Combativite totale d'un adversaire (4 coequipiers + PP), pour comparaison rapide avec
     *  la combativite du joueur avant de s'engager dans le combat. */
    private double combativiteAdversaire(AreneData a) {
        List<PersonnageBase> equipe = a.construireEquipe(ctx.sauvegarde::creerPersonnageParNom);
        double total = equipe.stream().mapToDouble(PersonnageBase::getCombativite).sum();
        PersonnageBase principal = MenuArene.creerPersonnagePrincipalIA(
                a.getPersonnagePrincipalNom(), Math.max(1, a.getNiveauMoyenEquipe()), a.getRang());
        if (principal != null) total += principal.getCombativite();
        return total;
    }

    private Node carteAdversaire(AreneData a) {
        List<String> nomsEquipe = new ArrayList<>(a.getEquipeDefensiveNoms());
        String nomPrincipal = a.getPersonnagePrincipalNom();
        if (nomPrincipal != null && nomPrincipal.startsWith("PP_")) {
            nomsEquipe.add("Combattant (" + nomPrincipal.replace("PP_", "") + ")");
        } else if (nomPrincipal != null) {
            nomsEquipe.add(nomPrincipal);
        }

        Label rang = new Label("#" + a.getRang());
        rang.getStyleClass().add("item-nom");
        rang.setMinWidth(50);
        String couleurMedaille = switch (a.getRang()) {
            case 1  -> "#f2c14e"; // or
            case 2  -> "#c0c0c0"; // argent
            case 3  -> "#cd7f32"; // bronze
            default -> null;
        };
        if (couleurMedaille != null) {
            rang.setStyle("-fx-text-fill: " + couleurMedaille + "; -fx-font-size: 16px;");
        }

        Label pseudo = new Label(a.getPseudo());
        pseudo.getStyleClass().add("item-nom");

        Label niveau = new Label("Niv. moy. " + a.getNiveauMoyenEquipe());
        niveau.getStyleClass().add("item-qte");

        Label combativite = new Label("Combativité recommandée : "
                + GuiVisuels.formaterMontant(combativiteAdversaire(a)));
        combativite.getStyleClass().add("item-detail");

        HBox entete = new HBox(10, rang, pseudo);
        entete.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(pseudo, Priority.ALWAYS);

        FlowPane equipe = new FlowPane(6, 4);
        equipe.setPrefWrapLength(340);
        for (String nom : nomsEquipe) {
            Label chip = new Label(nom);
            chip.getStyleClass().add("ordre-chip");
            equipe.getChildren().add(chip);
        }

        VBox texte = new VBox(6, entete, equipe, combativite);
        HBox carte = new HBox(14, texte, niveau);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(420);
        HBox.setHgrow(texte, Priority.ALWAYS);
        carte.setCursor(Cursor.HAND);
        carte.setOnMouseClicked(e -> lancerCombat(a, (Stage) ((Node) e.getSource()).getScene().getWindow()));
        return carte;
    }

    private void lancerCombat(AreneData adversaire, Stage stage) {
        List<PersonnageBase> equipeJoueur = new ArrayList<>();
        for (PersonnageBase p : ctx.formation.getEquipe()) if (p != null) equipeJoueur.add(p);

        if (equipeJoueur.isEmpty()) {
            info("Arene", "Ton equipe est vide ! Configure ta formation d'abord.");
            return;
        }

        List<PersonnageBase> equipeAdverse = adversaire.construireEquipe(ctx.sauvegarde::creerPersonnageParNom);
        int niveauEquipeAdv = Math.max(1, adversaire.getNiveauMoyenEquipe());
        PersonnageBase principalAdverse = MenuArene.creerPersonnagePrincipalIA(adversaire.getPersonnagePrincipalNom(), niveauEquipeAdv, adversaire.getRang());
        if (principalAdverse != null) {
            equipeAdverse.add(principalAdverse);
        } else if (!equipeAdverse.isEmpty()) {
            principalAdverse = equipeAdverse.get(0);
        }

        if (equipeAdverse.isEmpty()) {
            info("Arene", "Erreur : impossible de construire l'equipe adverse.");
            return;
        }

        for (PersonnageBase p : equipeJoueur)  p.reinitialiserPourCombat();
        for (PersonnageBase p : equipeAdverse) p.reinitialiserPourCombat();

        List<Combat.PersonnageSnapshot> etatInitial = Combat.snapshotEquipes(equipeJoueur, equipeAdverse);
        Combat combat = new Combat(equipeJoueur, equipeAdverse, false);
        List<Combat.CombatEvent> evenements = combat.lancerCombatEnregistre();
        boolean victoire = combat.equipeKO(equipeAdverse);

        try {
            GestionnaireMusique.jouerMusiqueAreneAuHasard();
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranCombat.fxml");
            EcranCombatController controller = loader.getController();
            controller.initCombat(etatInitial, evenements, victoire, v -> {
                if (v) gestionnaireArene.appliquerVictoire(joueurArene, adversaire);
                else   gestionnaireArene.appliquerDefaite(joueurArene);
                gestionnaireArene.uploaderRangJoueur(joueurArene);
                ctx.sauvegarde.sauvegarder(ctx);
                retourEcranAdversaires(stage);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void retourEcranAdversaires(Stage stage) {
        try {
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranAreneAdversaires.fxml");
            EcranAreneAdversairesController controller = loader.getController();
            controller.initData(ctx, gestionnaireArene, joueurArene, onRetour);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void info(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, javafx.scene.control.ButtonType.OK);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("root-menu");
        alert.showAndWait();
    }

    @FXML
    private void onRetour() {
        onRetour.run();
    }
}
