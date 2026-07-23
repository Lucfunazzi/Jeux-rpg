package lancement.gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Menus.MenuAmeliorations;

public class EcranAmeliorationsController {

    private GameContext ctx;

    @FXML private VBox choixBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;

        boolean affinageDebloque = ctx.joueur.getNiveau() >= MenuAmeliorations.NIVEAU_DEBLOCAGE_AFFINAGE;

        Node carteFortification = GuiVisuels.creerCarteChoix("Fortification",
                "Renforce l'équipement porté avec de l'or.", this::onFortification);

        Node carteAffinage = GuiVisuels.creerCarteChoix("Affinage",
                affinageDebloque
                        ? "Améliore les statistiques d'une pièce avec des pierres d'affinage."
                        : "Débloqué au niveau " + MenuAmeliorations.NIVEAU_DEBLOCAGE_AFFINAGE + ".",
                this::onAffinage);
        if (!affinageDebloque) {
            carteAffinage.setOpacity(0.4);
            carteAffinage.setCursor(Cursor.DEFAULT);
            carteAffinage.setOnMouseClicked(null);
        }

        Node cartePierres = GuiVisuels.creerCarteChoix("Pierres",
                "Synthétise et équipe des pierres (jades) sur l'équipement.", this::onPierres);

        choixBox.getChildren().setAll(carteFortification, carteAffinage, cartePierres);
    }

    private void onFortification(MouseEvent event) {
        naviguer(event, "/fxml/EcranFortification.fxml", c -> ((EcranFortificationController) c).initData(ctx));
    }

    private void onAffinage(MouseEvent event) {
        naviguer(event, "/fxml/EcranAffinage.fxml", c -> ((EcranAffinageController) c).initData(ctx));
    }

    private void onPierres(MouseEvent event) {
        naviguer(event, "/fxml/EcranPierres.fxml", c -> ((EcranPierresController) c).initData(ctx));
    }

    private void naviguer(MouseEvent event, String fxml, java.util.function.Consumer<Object> initialiser) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = Navigation.changerEcran(stage, fxml);
            initialiser.accept(loader.getController());
        } catch (IOException e) {
            throw new RuntimeException(e);
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
}
