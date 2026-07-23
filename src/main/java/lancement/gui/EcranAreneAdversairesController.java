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

        for (AreneData a : adversaires) {
            adversairesBox.getChildren().add(carteAdversaire(a));
        }
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

        Label pseudo = new Label(a.getPseudo());
        pseudo.getStyleClass().add("item-nom");

        Label niveau = new Label("Niv. moy. " + a.getNiveauMoyenEquipe());
        niveau.getStyleClass().add("item-qte");

        HBox entete = new HBox(10, rang, pseudo);
        entete.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(pseudo, Priority.ALWAYS);

        Label equipe = new Label(String.join(", ", nomsEquipe));
        equipe.getStyleClass().add("item-detail");
        equipe.setWrapText(true);
        equipe.setMaxWidth(340);

        VBox texte = new VBox(4, entete, equipe);
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
        PersonnageBase principalAdverse = MenuArene.creerPersonnagePrincipalIA(adversaire.getPersonnagePrincipalNom(), niveauEquipeAdv);
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
