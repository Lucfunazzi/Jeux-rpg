package lancement.gui;

import Equipement.PotionEnergie;
import java.io.IOException;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Personnage.PersonnageBase;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireQuetes;
import lancement.Quetes.Quete;
import lancement.Quetes.QueteJournaliere;
import lancement.Quetes.QueteProgression;

public class EcranQuetesController {

    private GameContext ctx;
    private boolean ongletChapitres = true;

    @FXML private VBox quetesBox;
    @FXML private Button ongletJournalieresButton;
    @FXML private Button ongletChapitresButton;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        GuiVisuels.afficherExplicationPremiereVisite(ctx, "Quetes", "Quêtes");
        ctx.gestionnaireQuetes.verifierRenouvellement();
        rafraichir();
    }

    @FXML
    private void onOngletJournalieres(ActionEvent event) {
        if (ctx.joueur.getNiveau() < GestionnaireQuetes.NIVEAU_DEBLOCAGE_JOURNALIERES) {
            info("Quêtes journalières", "Se débloquent au niveau "
                    + GestionnaireQuetes.NIVEAU_DEBLOCAGE_JOURNALIERES + ".");
            return;
        }
        ongletChapitres = false;
        rafraichir();
    }

    @FXML
    private void onOngletChapitres(ActionEvent event) {
        ongletChapitres = true;
        rafraichir();
    }

    private void rafraichir() {
        quetesBox.getChildren().clear();

        boolean journalieresDebloquees = ctx.joueur.getNiveau() >= GestionnaireQuetes.NIVEAU_DEBLOCAGE_JOURNALIERES;
        ongletJournalieresButton.setText(journalieresDebloquees
                ? "Quêtes journalières"
                : "Quêtes journalières (Niveau " + GestionnaireQuetes.NIVEAU_DEBLOCAGE_JOURNALIERES + ")");
        ongletJournalieresButton.setOpacity(journalieresDebloquees ? 1.0 : 0.5);

        ongletJournalieresButton.getStyleClass().removeAll("carte-item-joueur");
        ongletChapitresButton.getStyleClass().removeAll("carte-item-joueur");
        (ongletChapitres ? ongletChapitresButton : ongletJournalieresButton).getStyleClass().add("carte-item-joueur");

        if (ongletChapitres) rafraichirChapitres(); else rafraichirJournalieres();
    }

    private void rafraichirJournalieres() {
        int pts = ctx.gestionnaireQuetes.getPointsJournaliers();
        quetesBox.getChildren().add(titreSection("Barre de points journalière : " + pts + " / 100"));
        for (int i = 0; i < GestionnaireQuetes.PALIERS_BARRE_JOURNALIERE.length; i++) {
            quetesBox.getChildren().add(carteBarre(i));
        }

        quetesBox.getChildren().add(titreSection("Quêtes journalières"));
        for (QueteJournaliere qj : ctx.gestionnaireQuetes.getQuetesJournalieres()) {
            quetesBox.getChildren().add(carteQuete(qj));
        }
    }

    private void rafraichirChapitres() {
        quetesBox.getChildren().add(titreSection("Quêtes de chapitre"));
        quetesBox.getChildren().add(texteVide("Acceptez une quête pour débloquer le stage associé."));

        List<QueteProgression> visibles = ctx.gestionnaireQuetes.getQuetesVisibles(ctx);
        if (visibles.isEmpty()) {
            quetesBox.getChildren().add(texteVide("Aucune quête de chapitre disponible pour l'instant."));
        } else {
            for (QueteProgression q : visibles) quetesBox.getChildren().add(carteQuete(q));
        }
    }

    /** Carte pour un palier de la barre de points journalière. Clic -> Réclamer si disponible. */
    private Node carteBarre(int index) {
        int palier = GestionnaireQuetes.PALIERS_BARRE_JOURNALIERE[index];
        boolean reclamee   = ctx.gestionnaireQuetes.isBarreReclamee(index);
        boolean disponible = ctx.gestionnaireQuetes.estBarreDisponible(index);

        Label iconeLabel = new Label(reclamee ? "✔" : disponible ? "✪" : "◔");
        iconeLabel.getStyleClass().add("fiche-stat-icone");

        String titre = palier + " points" + (reclamee ? " — Réclamée" : disponible ? " — Disponible !" : "");
        Label titreLabel = new Label(titre);
        titreLabel.getStyleClass().add("item-nom");

        Label descLabel = new Label(ctx.gestionnaireQuetes.afficherRecompenseBarre(index));
        descLabel.getStyleClass().add("item-detail");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(300);

        VBox texte = new VBox(4, titreLabel, descLabel);
        HBox carte = new HBox(10, iconeLabel, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add(disponible ? "carte-item-joueur" : "carte-item");
        carte.setPrefWidth(360);
        if (disponible) {
            carte.setCursor(Cursor.HAND);
            carte.setOnMouseClicked(e -> onReclamerBarre(index));
        }
        return carte;
    }

    private void onReclamerBarre(int index) {
        String resultat = ctx.gestionnaireQuetes.reclamerBarre(index, ctx.inventaire);
        ctx.sauvegarde.sauvegarder(ctx);
        info("Barre journalière", resultat);
        rafraichir();
    }

    private Label titreSection(String texte) {
        Label l = new Label(texte);
        l.getStyleClass().add("section-titre");
        return l;
    }

    private Label texteVide(String texte) {
        Label l = new Label(texte);
        l.getStyleClass().add("item-vide");
        return l;
    }

    /** Carte pour une quete (journaliere ou de progression). Clic -> Reclamer si prete, sinon affiche la progression. */
    private Node carteQuete(Quete q) {
        Label iconeLabel = new Label(iconeStatut(q));
        iconeLabel.getStyleClass().add("fiche-stat-icone");
        iconeLabel.setStyle("-fx-text-fill: " + couleurStatut(q) + ";");

        Label titre = new Label(q.getTitre());
        titre.getStyleClass().add("item-nom");

        Label description = new Label(q.getDescription());
        description.getStyleClass().add("item-detail");
        description.setWrapText(true);
        description.setMaxWidth(420);

        Node progression;
        if (q instanceof QueteJournaliere qj) {
            progression = GuiVisuels.creerBarreProgression(200, 14, qj.getProgressionValeur(), qj.getObjectifCible());
        } else {
            Label p = new Label(q.getProgression());
            p.getStyleClass().add("item-detail");
            progression = p;
        }

        Label recompense = new Label("Récompense : " + q.afficherRecompenses());
        recompense.getStyleClass().add("item-qte");

        Label statut = new Label(libelleStatut(q));
        statut.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + couleurStatut(q) + ";");

        VBox texte = new VBox(4, titre, description, progression, recompense);
        HBox ligne = new HBox(14, iconeLabel, texte, statut);
        ligne.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(texte, Priority.ALWAYS);

        ligne.getStyleClass().add("carte-item");
        ligne.setCursor(Cursor.HAND);
        ligne.setOnMouseClicked(e -> actionQuete(q));
        return ligne;
    }

    private String libelleStatut(Quete q) {
        if (q.isReclamee())  return "Réclamée";
        if (q.isCompletee()) return "Prête !";
        if (q instanceof QueteProgression qp && !qp.isAcceptee()) return "À accepter";
        return "En cours";
    }

    /** Icone selon le meme statut que libelleStatut()/couleurStatut(). */
    private String iconeStatut(Quete q) {
        if (q.isReclamee())  return "✔";
        if (q.isCompletee()) return "✪";
        if (q instanceof QueteProgression qp && !qp.isAcceptee()) return "⚑";
        return "◔";
    }

    private String couleurStatut(Quete q) {
        if (q.isReclamee())  return "#7a7a95";
        if (q.isCompletee()) return "#56c98a";
        if (q instanceof QueteProgression qp && !qp.isAcceptee()) return "#e07a5f";
        return "#f2c14e";
    }

    private void actionQuete(Quete q) {
        if (q.isReclamee()) { info(q.getTitre(), "Récompense déjà réclamée."); return; }
        if (q instanceof QueteProgression qp && !qp.isAcceptee()) { accepter(qp); return; }
        if (!q.isCompletee()) {
            info(q.getTitre(), q.getDescription() + "\nProgression : " + q.getProgression());
            return;
        }
        reclamer(q);
    }

    private void accepter(QueteProgression q) {
        String resultat = ctx.gestionnaireQuetes.accepterQueteProgression(ctx, q.getId());
        ctx.sauvegarde.sauvegarder(ctx);
        info(q.getTitre(), resultat);
        rafraichir();
    }

    private void reclamer(Quete q) {
        q.setReclamee(true);

        StringBuilder message = new StringBuilder("Récompenses reçues pour : " + q.getTitre() + "\n");
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
        if (q instanceof QueteJournaliere) {
            ctx.inventaire.ajouterMateriau(PotionEnergie.MOYENNE.nom, 1);
            message.append("+ 1x ").append(PotionEnergie.MOYENNE.nom).append("\n");
        }
        for (Quete.RecompenseItem item : q.getRecompensesItems()) {
            ctx.inventaire.ajouterMateriau(item.nom(), item.quantite());
            message.append("+ ").append(item.quantite()).append("x ").append(item.nom()).append("\n");
        }

        ctx.sauvegarde.sauvegarder(ctx);
        info("Quêtes", message.toString().trim());
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

    @FXML
    private void onRetourHistoire(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranHistoire.fxml");
            EcranHistoireController controller = loader.getController();
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
