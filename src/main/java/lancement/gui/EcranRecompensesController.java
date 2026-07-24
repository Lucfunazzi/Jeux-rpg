package lancement.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireRecompenses;

/**
 * Ecran Récompenses — regroupe les différents systèmes de récompenses du jeu :
 * récompenses de niveau, pointage du mois, connexion 7 jours, et récompense
 * quotidienne (paliers de temps). Le contenu de la récompense quotidienne
 * n'est défini que pour le palier 30 minutes ; les autres restent à venir.
 */
public class EcranRecompensesController {

    /** Un palier affichable dans une liste de choix (niveau / mois / jour connexion). */
    private record Palier(String libelle, String etat, String recompense, boolean disponible) {
        @Override public String toString() { return libelle; }
    }

    private GameContext ctx;

    @FXML private VBox recompensesBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        ctx.gestionnaireRecompenses.mettreAJourPointageMois();
        ctx.gestionnaireRecompenses.mettreAJourConnexion();
        rafraichir();
    }

    private void rafraichir() {
        GestionnaireRecompenses gr = ctx.gestionnaireRecompenses;
        List<Node> cartes = new ArrayList<>();
        cartes.add(carteRecompense("Récompense de niveau", this::menuNiveau));
        cartes.add(carteRecompense("Pointage du mois (" + gr.getJoursCumulesMois() + " jour(s))", this::menuMois));
        if (!gr.estTerminee()) {
            cartes.add(carteRecompense("Récompense des 7 jours (jour " + gr.getJourConnexion() + "/7)", this::menuConnexion));
        }
        cartes.add(carteRecompense("Récompense quotidienne", this::menuQuotidienne));
        recompensesBox.getChildren().setAll(cartes);
    }

    private Node carteRecompense(String nom, Runnable action) {
        Label nomLabel = new Label(nom);
        nomLabel.getStyleClass().add("item-nom");

        HBox carte = new HBox(nomLabel);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(340);
        carte.setCursor(Cursor.HAND);
        carte.setOnMouseClicked(e -> action.run());
        return carte;
    }

    private Node cartePalier(Palier p) {
        Label nomLabel = new Label(p.libelle() + "  " + p.etat());
        nomLabel.getStyleClass().add("item-nom");
        Label detail = new Label(p.recompense());
        detail.getStyleClass().add("item-detail");

        VBox texte = new VBox(2, nomLabel, detail);
        HBox carte = new HBox(texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add(p.disponible() ? "carte-item-joueur" : "carte-item");
        carte.setPrefWidth(340);
        if (!p.disponible()) carte.setOpacity(0.6);
        return carte;
    }

    // ── Récompenses de niveau ────────────────────────────────────────────
    private void menuNiveau() {
        GestionnaireRecompenses gr = ctx.gestionnaireRecompenses;
        int niveauJoueur = ctx.joueur.getNiveau();

        List<Palier> paliers = new ArrayList<>();
        for (int i = 0; i < GestionnaireRecompenses.PALIERS_NIVEAU.length; i++) {
            String etat = gr.isNiveauReclame(i) ? "[Réclamé]"
                    : gr.estNiveauDisponible(i, niveauJoueur) ? "[Disponible]" : "[Verrouillé]";
            paliers.add(new Palier("Niveau " + GestionnaireRecompenses.PALIERS_NIVEAU[i], etat,
                    gr.afficherRecompenseNiveau(i), gr.estNiveauDisponible(i, niveauJoueur)));
        }

        Palier choisi = GuiVisuels.choisirParmiCartes("Récompense de niveau", paliers, this::cartePalier);
        if (choisi == null || !choisi.disponible()) return;

        int index = paliers.indexOf(choisi);
        info("Récompense de niveau", gr.reclamerNiveau(index, ctx.joueur, ctx.inventaire));
        ctx.sauvegarde.sauvegarder(ctx);
        rafraichir();
    }

    // ── Pointage du mois ──────────────────────────────────────────────────
    private void menuMois() {
        GestionnaireRecompenses gr = ctx.gestionnaireRecompenses;
        int[] seuils = gr.getPaliersMois();

        List<Palier> paliers = new ArrayList<>();
        for (int i = 0; i < seuils.length; i++) {
            String etat = gr.isMoisReclame(i) ? "[Réclamé]"
                    : gr.estMoisDisponible(i) ? "[Disponible]" : "[Verrouillé]";
            paliers.add(new Palier(seuils[i] + " jours", etat, gr.afficherRecompenseMois(i), gr.estMoisDisponible(i)));
        }

        Palier boutique = new Palier("Boutique du mois", "[" + gr.getPointsMois() + " points]",
                "Dépenser vos points contre des fragments de personnage", true);
        paliers.add(boutique);

        Palier choisi = GuiVisuels.choisirParmiCartes(
                "Pointage du mois (" + gr.getJoursCumulesMois() + " jour(s) cumulés)", paliers, this::cartePalier);
        if (choisi == null) return;

        if (choisi == boutique) { menuBoutiqueMois(); return; }
        if (!choisi.disponible()) return;

        int index = paliers.indexOf(choisi);
        info("Pointage du mois", gr.reclamerMois(index, ctx.joueur, ctx.inventaire));
        ctx.sauvegarde.sauvegarder(ctx);
        rafraichir();
    }

    // ── Boutique du mois (fragments de personnages contre points) ──────────
    private void menuBoutiqueMois() {
        GestionnaireRecompenses gr = ctx.gestionnaireRecompenses;
        List<String[]> catalogue = new ArrayList<>();
        for (String[] infoBrute : lancement.Menus.MenuEtoilesPerso.getCatalogue()) {
            if (!infoBrute[0].equals("Lucas")) catalogue.add(infoBrute); // Lucas [SS] non vendable en boutique
        }

        List<Palier> items = new ArrayList<>();
        for (String[] infoPerso : catalogue) {
            int prix = GestionnaireRecompenses.prixFragmentBoutiqueMois(infoPerso[1]);
            items.add(new Palier(infoPerso[0] + " [" + infoPerso[1] + "]", "",
                    prix + " points / fragment", true));
        }

        Palier choisi = GuiVisuels.choisirParmiCartes(
                "Boutique du mois — " + gr.getPointsMois() + " points", items, this::cartePalier);
        if (choisi == null) return;

        int index = items.indexOf(choisi);
        String[] infoPerso = catalogue.get(index);
        info("Boutique du mois", gr.acheterFragmentBoutiqueMois(infoPerso[0], infoPerso[1], ctx.inventaire));
        ctx.sauvegarde.sauvegarder(ctx);
        rafraichir();
    }

    // ── Récompense de connexion 7 jours ─────────────────────────────────────
    private void menuConnexion() {
        GestionnaireRecompenses gr = ctx.gestionnaireRecompenses;
        if (gr.estTerminee()) return;

        List<Palier> paliers = new ArrayList<>();
        for (int j = 1; j <= 7; j++) {
            String etat = gr.isJourReclame(j) ? "[Réclamé]"
                    : gr.estJourDisponible(j) ? "[Disponible]" : "[Verrouillé]";
            paliers.add(new Palier("Jour " + j, etat, gr.afficherRecompenseJour(j), gr.estJourDisponible(j)));
        }

        Palier choisi = GuiVisuels.choisirParmiCartes(
                "Récompense des 7 jours (jour " + gr.getJourConnexion() + "/7)", paliers, this::cartePalier);
        if (choisi == null || !choisi.disponible()) return;

        int jour = paliers.indexOf(choisi) + 1;
        if (jour == 7) {
            List<Palier> choixPersos = new ArrayList<>();
            for (String nom : GestionnaireRecompenses.CHOIX_COFFRE_RANG_S) {
                choixPersos.add(new Palier(nom, "[S]", "Rejoint votre équipe", true));
            }
            Palier persoChoisi = GuiVisuels.choisirParmiCartes("Coffre de personnage [S]", choixPersos, this::cartePalier);
            if (persoChoisi == null) return;
            info("Récompense des 7 jours", gr.reclamerJour7(persoChoisi.libelle(), ctx.personnagesRecruites));
        } else {
            info("Récompense des 7 jours", gr.reclamerJour(jour, ctx.joueur, ctx.inventaire));
        }
        ctx.sauvegarde.sauvegarder(ctx);
        rafraichir();
    }

    // ── Récompense quotidienne (paliers de temps) ───────────────────────────
    private void menuQuotidienne() {
        GestionnaireRecompenses gr = ctx.gestionnaireRecompenses;
        List<Palier> paliers = List.of(
                new Palier("30 minutes", gr.peutReclamer30min() ? "[Disponible]" : "[" + gr.getTempsRestant30min() + "]",
                        "2 Petite(s) Potion(s) d'Énergie, 10 Carte(s) d'Or Lv.1", gr.peutReclamer30min()),
                new Palier("1 heure", "[Bientôt disponible]", "À définir", false),
                new Palier("2 heures", "[Bientôt disponible]", "À définir", false),
                new Palier("4 heures", "[Bientôt disponible]", "À définir", false)
        );

        Palier choisi = GuiVisuels.choisirParmiCartes("Récompense quotidienne", paliers, this::cartePalier);
        if (choisi == null) return;

        if (choisi.libelle().equals("30 minutes")) {
            if (!choisi.disponible()) return;
            info("Récompense quotidienne", gr.reclamer30min(ctx.inventaire));
            ctx.sauvegarde.sauvegarder(ctx);
            rafraichir();
        } else {
            info(choisi.libelle(), "Bientôt disponible.");
        }
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
