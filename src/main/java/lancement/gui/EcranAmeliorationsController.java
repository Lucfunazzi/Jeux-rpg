package lancement.gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Menus.MenuAmeliorations;

public class EcranAmeliorationsController {

    private GameContext ctx;

    @FXML private Button boutonAffinage;

    public void initData(GameContext ctx) {
        this.ctx = ctx;

        boolean affinageDebloque = ctx.joueur.getNiveau() >= MenuAmeliorations.NIVEAU_DEBLOCAGE_AFFINAGE;
        boutonAffinage.setText(affinageDebloque
                ? "Affinage"
                : "Affinage  [Debloque niveau " + MenuAmeliorations.NIVEAU_DEBLOCAGE_AFFINAGE + "]");
        boutonAffinage.setDisable(!affinageDebloque);
    }

    @FXML
    private void onFortification(ActionEvent event) {
        naviguer(event, "/fxml/EcranFortification.fxml", c -> ((EcranFortificationController) c).initData(ctx));
    }

    @FXML
    private void onAffinage(ActionEvent event) {
        naviguer(event, "/fxml/EcranAffinage.fxml", c -> ((EcranAffinageController) c).initData(ctx));
    }

    private void naviguer(ActionEvent event, String fxml, java.util.function.Consumer<Object> initialiser) {
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
