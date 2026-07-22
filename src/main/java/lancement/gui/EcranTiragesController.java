package lancement.gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Menus.MenuTirage_recrutement;

public class EcranTiragesController {

    private GameContext ctx;

    @FXML private Label resumeLabel;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichir();
    }

    private void rafraichir() {
        MenuTirage_recrutement mt = ctx.menuTirage;
        resumeLabel.setText(
                "Parchemins Ordinaires : " + mt.getParcheminOrdinaire() + "\n"
              + "Parchemins Elite : " + mt.getParcheminElite()
              + "  (pity A : " + mt.getCompteurPityA() + "/" + mt.getPityA()
              + " | S : " + mt.getCompteurPityS() + "/" + mt.getPityS()
              + " | SS : " + mt.getCompteurPitySS() + "/" + mt.getPitySS() + ")\n"
              + "Coupons : " + ctx.joueur.getCoupons());
    }

    @FXML
    private void onOrdinaire(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Runnable retour = retourVersTirages(stage);
        try {
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranTirageOrdinaire.fxml");
            EcranTirageOrdinaireController controller = loader.getController();
            controller.initData(ctx, retour);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onElite(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Runnable retour = retourVersTirages(stage);
        try {
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranTirageElite.fxml");
            EcranTirageEliteController controller = loader.getController();
            controller.initData(ctx, retour);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Runnable retourVersTirages(Stage stage) {
        return () -> {
            try {
                FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranTirages.fxml");
                EcranTiragesController controller = loader.getController();
                controller.initData(ctx);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
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
