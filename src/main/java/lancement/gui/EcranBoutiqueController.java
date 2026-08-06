package lancement.gui;

import Equipement.CarteOr;
import Equipement.CristalTranscendance;
import Equipement.Inventaire;
import Equipement.PotionEnergie;
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
import lancement.Gestionnaires.GestionnaireChasseTresor;
import lancement.Menus.MenuExamenS;

public class EcranBoutiqueController {

    /** Un article de boutique : identifiant stable (limite journaliere), libelle, prix et effet d'achat. */
    private record Article(String id, String nom, int prixCoupons, Runnable octroi) {}

    private GameContext ctx;

    @FXML private VBox soldeBox;
    @FXML private VBox promoBox;
    @FXML private VBox consommablesBox;
    @FXML private VBox raresBox;
    @FXML private VBox mageEventBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichir();
    }

    private void rafraichir() {
        soldeBox.getChildren().setAll(
                GuiVisuels.creerFicheStat("✉", "Coupons", GuiVisuels.formaterMontant(ctx.joueur.getCoupons())));

        promoBox.getChildren().setAll(texteVide("Rien pour l'instant. Revenez plus tard !"));
        mageEventBox.getChildren().setAll(texteVide("Aucun Mage Event disponible pour l'instant."));

        FlowPane grilleConso = new FlowPane(10, 10);
        grilleConso.setAlignment(Pos.CENTER);
        for (Article a : construireConsommables()) grilleConso.getChildren().add(carteQuotidienne(a));
        consommablesBox.getChildren().setAll(grilleConso);

        FlowPane grilleRares = new FlowPane(10, 10);
        grilleRares.setAlignment(Pos.CENTER);
        for (Article a : construireItemsRares()) grilleRares.getChildren().add(carteIllimitee(a));
        raresBox.getChildren().setAll(grilleRares);
    }

    // ── Catalogue ────────────────────────────────────────────────────────

    private List<Article> construireConsommables() {
        Inventaire inv = ctx.inventaire;
        List<Article> liste = new ArrayList<>();
        liste.add(new Article("carte_or_1", "Carte d'Or Lv.1 x100", 20,  () -> inv.ajouterCartesOr(CarteOr.NIVEAU_1, 100)));
        liste.add(new Article("carte_or_2", "Carte d'Or Lv.2 x100", 50,  () -> inv.ajouterCartesOr(CarteOr.NIVEAU_2, 100)));
        liste.add(new Article("carte_or_3", "Carte d'Or Lv.3 x100", 100, () -> inv.ajouterCartesOr(CarteOr.NIVEAU_3, 100)));
        liste.add(new Article("carte_or_4", "Carte d'Or Lv.4 x100", 200, () -> inv.ajouterCartesOr(CarteOr.NIVEAU_4, 100)));
        liste.add(new Article("carte_or_5", "Carte d'Or Lv.5 x100", 500, () -> inv.ajouterCartesOr(CarteOr.NIVEAU_5, 100)));
        liste.add(new Article("potion_petite",  "Petite Potion d'Energie x2", 15,  () -> inv.ajouterMateriau(PotionEnergie.PETITE.nom, 2)));
        liste.add(new Article("potion_moyenne", "Potion d'Energie x1",        50,  () -> inv.ajouterMateriau(PotionEnergie.MOYENNE.nom, 1)));
        liste.add(new Article("potion_grande",  "Grande Potion d'Energie x1", 100, () -> inv.ajouterMateriau(PotionEnergie.GRANDE.nom, 1)));
        return liste;
    }

    private List<Article> construireItemsRares() {
        Inventaire inv = ctx.inventaire;
        List<Article> liste = new ArrayList<>();
        liste.add(new Article("parchemin_chasse_a",  "Parchemin de Chasse A x1",  20,  () -> inv.ajouterMateriau(GestionnaireChasseTresor.PARCHEMIN_A, 1)));
        liste.add(new Article("parchemin_chasse_s",  "Parchemin de Chasse S x1",  60,  () -> inv.ajouterMateriau(GestionnaireChasseTresor.PARCHEMIN_S, 1)));
        liste.add(new Article("parchemin_chasse_ss", "Parchemin de Chasse SS x1", 120, () -> inv.ajouterMateriau(GestionnaireChasseTresor.PARCHEMIN_SS, 1)));
        liste.add(new Article("cristal_transcendance", "Cristal de Transcendance x1", 200, () -> inv.ajouterMateriau(CristalTranscendance.NOM, 1)));
        liste.add(new Article("boite_pierre_1", "Boite de pierre Lv.1", 10,  () -> inv.ajouterMateriau(MenuExamenS.nomBoite(1), 1)));
        liste.add(new Article("boite_pierre_2", "Boite de pierre Lv.2", 25,  () -> inv.ajouterMateriau(MenuExamenS.nomBoite(2), 1)));
        liste.add(new Article("boite_pierre_3", "Boite de pierre Lv.3", 50,  () -> inv.ajouterMateriau(MenuExamenS.nomBoite(3), 1)));
        liste.add(new Article("boite_pierre_4", "Boite de pierre Lv.4", 100, () -> inv.ajouterMateriau(MenuExamenS.nomBoite(4), 1)));
        return liste;
    }

    // ── Cartes ───────────────────────────────────────────────────────────

    private Node carteQuotidienne(Article a) {
        boolean dejaAchete = ctx.gestionnaireBoutique.dejaAchete(a.id());

        Label nomLabel = new Label(a.nom());
        nomLabel.getStyleClass().add("item-nom");
        Label prixLabel = new Label(dejaAchete ? "Achete aujourd'hui" : a.prixCoupons() + " coupons");
        prixLabel.getStyleClass().add(dejaAchete ? "item-vide" : "item-qte");

        VBox texte = new VBox(4, nomLabel, prixLabel);
        HBox carte = new HBox(10, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(240);
        if (dejaAchete) {
            carte.setOpacity(0.55);
        } else {
            carte.setCursor(Cursor.HAND);
            carte.setOnMouseClicked(e -> acheterQuotidien(a));
        }
        return carte;
    }

    private Node carteIllimitee(Article a) {
        Label nomLabel = new Label(a.nom());
        nomLabel.getStyleClass().add("item-nom");
        Label prixLabel = new Label(a.prixCoupons() + " coupons");
        prixLabel.getStyleClass().add("item-qte");

        VBox texte = new VBox(4, nomLabel, prixLabel);
        HBox carte = new HBox(10, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(240);
        carte.setCursor(Cursor.HAND);
        carte.setOnMouseClicked(e -> acheterIllimite(a));
        return carte;
    }

    private Label texteVide(String texte) {
        Label l = new Label(texte);
        l.getStyleClass().add("item-vide");
        l.setWrapText(true);
        l.setMaxWidth(500);
        return l;
    }

    // ── Achats ───────────────────────────────────────────────────────────

    private void acheterQuotidien(Article a) {
        if (ctx.gestionnaireBoutique.dejaAchete(a.id())) {
            info("Boutique", "Vous avez deja achete cet article aujourd'hui. Revenez demain !");
            return;
        }
        if (!verifierEtDebiter(a)) return;

        a.octroi().run();
        ctx.gestionnaireBoutique.marquerAchete(a.id());
        ctx.sauvegarde.sauvegarder(ctx);
        info("Boutique", a.nom() + " achete !");
        rafraichir();
    }

    private void acheterIllimite(Article a) {
        if (!verifierEtDebiter(a)) return;

        a.octroi().run();
        ctx.sauvegarde.sauvegarder(ctx);
        info("Boutique", a.nom() + " achete !");
        rafraichir();
    }

    /** Verifie les coupons, demande confirmation, puis debite si le joueur confirme. */
    private boolean verifierEtDebiter(Article a) {
        int coupons = ctx.joueur.getCoupons();
        if (coupons < a.prixCoupons()) {
            info("Boutique", "Coupons insuffisants (besoin : " + a.prixCoupons() + ", vous avez : " + coupons + ").");
            return false;
        }
        if (!confirmer("Acheter " + a.nom() + " pour " + a.prixCoupons() + " coupons ?")) return false;

        ctx.joueur.setCoupons(coupons - a.prixCoupons());
        return true;
    }

    // ── Navigation / dialogues ─────────────────────────────────────────────

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
