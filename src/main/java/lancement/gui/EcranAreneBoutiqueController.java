package lancement.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.Optional;
import lancement.GameContext;
import lancement.Gestionnaires.AreneData;
import lancement.Gestionnaires.GestionnaireArene;
import lancement.Gestionnaires.GestionnaireEtoilesPerso;
import lancement.Menus.MenuBoutiqueArene;

public class EcranAreneBoutiqueController {

    private GameContext ctx;
    private MenuBoutiqueArene menuBoutiqueArene;
    private AreneData joueurArene;
    private Runnable onRetour;

    @FXML private VBox pointsBox;
    @FXML private VBox catalogueBox;

    public void initData(GameContext ctx, GestionnaireArene gestionnaireArene, AreneData joueurArene, Runnable onRetour) {
        this.ctx = ctx;
        this.joueurArene = joueurArene;
        this.onRetour = onRetour;
        this.menuBoutiqueArene = new MenuBoutiqueArene(ctx, Navigation.scannerSilencieux(), joueurArene, gestionnaireArene);
        rafraichir();
    }

    private void rafraichir() {
        pointsBox.getChildren().setAll(
                GuiVisuels.creerFicheStat("Points boutique", joueurArene.getPointsBoutique() + " pts"));

        FlowPane grille = new FlowPane(10, 10);
        grille.setAlignment(Pos.CENTER);
        for (Object[] entree : MenuBoutiqueArene.getCatalogue()) {
            String nom = (String) entree[0];
            String rarete = (String) entree[1];
            grille.getChildren().add(carteBoutique(nom, rarete));
        }
        catalogueBox.getChildren().setAll(grille);
    }

    /** Fragments encore necessaires pour la prochaine etape (recrutement si pas encore recrute,
     *  sinon prochaine etoile) ; -1 si deja a 5 etoiles (rien de plus a acheter pour ce perso). */
    private int prochainPalierFragments(String nom, String rarete) {
        var perso = ctx.personnagesRecruites.stream().filter(p -> p.getNom().equals(nom)).findFirst();
        if (perso.isEmpty()) return GestionnaireEtoilesPerso.coutFragmentsRecrutement(rarete);
        int etoiles = perso.get().getNbreEtoiles();
        if (etoiles >= 5) return -1;
        return GestionnaireEtoilesPerso.coutFragmentsEtoile(rarete, etoiles);
    }

    private Node carteBoutique(String nom, String rarete) {
        boolean recrute = GestionnaireEtoilesPerso.dejaRecruteParNom(ctx.personnagesRecruites, nom);
        int prixUnitaire = MenuBoutiqueArene.prixFragment(rarete);
        int possedes = GestionnaireEtoilesPerso.getFragments(ctx.inventaire, nom);
        int palier = prochainPalierFragments(nom, rarete);

        Label badge = GuiVisuels.creerBadgeRarete(rarete);
        Label nomLabel = new Label(nom + (recrute ? " (recruté)" : ""));
        nomLabel.getStyleClass().add("item-nom");

        String sousTexte = palier > 0
                ? possedes + " / " + palier + " fragments  ·  " + prixUnitaire + " pts/fragment"
                : possedes + " fragments  ·  5★ MAX";
        Label prixLabel = new Label(sousTexte);
        prixLabel.getStyleClass().add(palier > 0 ? "item-qte" : "item-vide");

        VBox texte = new VBox(4, nomLabel, prixLabel);
        HBox carte = new HBox(10, badge, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(280);
        if (palier <= 0) {
            carte.setOpacity(0.55);
        } else {
            carte.setCursor(Cursor.HAND);
            carte.setOnMouseClicked(e -> acheter(nom, rarete, prixUnitaire));
        }
        return carte;
    }

    private void acheter(String nom, String rarete, int prixUnitaire) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Acheter des fragments de " + nom);
        dialog.setHeaderText(null);
        dialog.setContentText("Prix : " + prixUnitaire + " pts/fragment (vous avez "
                + joueurArene.getPointsBoutique() + " pts).\nCombien de fragments acheter ?");
        styliser(dialog);
        Optional<String> reponse = dialog.showAndWait();
        if (reponse.isEmpty()) return;

        int quantite;
        try {
            quantite = Integer.parseInt(reponse.get().trim());
        } catch (NumberFormatException e) {
            info("Boutique", "Quantite invalide.");
            return;
        }
        if (quantite <= 0) {
            info("Boutique", "Quantite invalide.");
            return;
        }

        info("Boutique", menuBoutiqueArene.acheterFragments(nom, rarete, quantite));
        rafraichir();
    }

    @FXML
    private void onRetour(ActionEvent event) {
        onRetour.run();
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
