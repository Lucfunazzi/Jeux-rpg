package lancement.gui;

import Equipement.ForgeFactory;
import Equipement.ForgeRecette;
import lancement.GameContext;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Première version de la Forge : création des équipements à partir de recettes. */
public class EcranForgeController {
    private GameContext ctx;
    private ForgeRecette recette;

    @FXML private VBox recetteBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        this.recette = ForgeFactory.batonC();
        afficherRecette();
    }

    private void afficherRecette() {
        recetteBox.getChildren().clear();
        recetteBox.setAlignment(Pos.TOP_CENTER);
        recetteBox.setPadding(new Insets(20));
        recetteBox.setSpacing(10);

        Label titre = new Label("🛠  " + recette.getNom());
        titre.getStyleClass().add("titre");

        Label resultat = new Label("Résultat : " + recette.getResultat().getNomAffiche());
        resultat.getStyleClass().add("item-nom");

        VBox couts = new VBox(6);
        couts.setAlignment(Pos.CENTER);
        Label coutTitre = new Label("Matériaux requis");
        coutTitre.getStyleClass().add("section-titre");
        couts.getChildren().add(coutTitre);
        for (var cout : recette.getCoutsMateriaux().entrySet()) {
            int possede = ctx.inventaire.getQuantiteMateriau(cout.getKey());
            Label ligne = new Label("◆ " + cout.getKey() + " : " + possede + " / " + cout.getValue());
            ligne.getStyleClass().add(possede >= cout.getValue() ? "item-detail" : "item-vide");
            couts.getChildren().add(ligne);
        }

        Button fabriquer = new Button("Fabriquer");
        fabriquer.getStyleClass().add("menu-bouton");
        fabriquer.setDisable(!recette.estFabricable(ctx.inventaire));
        fabriquer.setOnAction(e -> fabriquer());

        Button retour = new Button("Retour");
        retour.getStyleClass().add("menu-bouton");
        retour.setOnAction(this::onRetour);

        recetteBox.getChildren().addAll(titre, resultat, couts, fabriquer, retour);
    }

    private void fabriquer() {
        if (!recette.fabriquer(ctx.inventaire)) {
            afficher("Forge", "Matériaux insuffisants.");
            return;
        }
        ctx.sauvegarde.sauvegarder(ctx);
        afficher("Forge", "Bâton de bois C créé !");
        afficherRecette();
    }

    @FXML
    private void onRetour(javafx.event.ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            var loader = Navigation.changerEcran(stage, "/fxml/EcranMenuPrincipal.fxml");
            ((EcranMenuPrincipalController) loader.getController()).initData(ctx);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void afficher(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("root-menu");
        alert.showAndWait();
    }
}
