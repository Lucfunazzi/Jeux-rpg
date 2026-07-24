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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lancement.GameContext;
import lancement.Gestionnaires.AreneData;
import lancement.Gestionnaires.GestionnaireArene;
import lancement.Menus.MenuBoutiqueArene;

public class EcranAreneBoutiqueController {

    private MenuBoutiqueArene menuBoutiqueArene;
    private AreneData joueurArene;
    private Runnable onRetour;

    @FXML private VBox pointsBox;
    @FXML private VBox catalogueBox;

    public void initData(GameContext ctx, GestionnaireArene gestionnaireArene, AreneData joueurArene, Runnable onRetour) {
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
            int prix = (int) entree[2];
            grille.getChildren().add(carteBoutique(nom, rarete, prix));
        }
        catalogueBox.getChildren().setAll(grille);
    }

    private Node carteBoutique(String nom, String rarete, int prix) {
        boolean possede = menuBoutiqueArene.dejaRecruté(nom);

        Label badge = GuiVisuels.creerBadgeRarete(rarete);
        Label nomLabel = new Label(nom);
        nomLabel.getStyleClass().add("item-nom");

        Label prixLabel = new Label(possede ? "Déjà recruté" : prix + " pts");
        prixLabel.getStyleClass().add(possede ? "item-vide" : "item-qte");

        VBox texte = new VBox(4, nomLabel, prixLabel);
        HBox carte = new HBox(10, badge, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(240);
        if (possede) {
            carte.setOpacity(0.55);
        } else {
            carte.setCursor(Cursor.HAND);
            carte.setOnMouseClicked(e -> acheter(nom, prix));
        }
        return carte;
    }

    private void acheter(String nom, int prix) {
        boolean confirme = confirmer("Recruter " + nom + " pour " + prix + " points boutique ?");
        if (!confirme) return;

        info("Boutique", menuBoutiqueArene.executerAchat(nom, prix));
        rafraichir();
    }

    private boolean confirmer(String question) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, question, ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        styliser(confirm);
        return confirm.showAndWait().filter(b -> b == ButtonType.YES).isPresent();
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
