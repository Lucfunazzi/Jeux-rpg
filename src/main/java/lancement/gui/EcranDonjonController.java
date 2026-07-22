package lancement.gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireDonjon;
import lancement.Gestionnaires.GestionnaireDonjon.Difficulte;
import lancement.Gestionnaires.GestionnaireDonjon.TypeDonjon;
import lancement.Menus.MenuDonjon;

public class EcranDonjonController {

    private GameContext ctx;

    @FXML private Label resumeLabel;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        ctx.gestionnaireDonjon.mettreAJour();
        rafraichir();
    }

    private void rafraichir() {
        GestionnaireDonjon gd = ctx.gestionnaireDonjon;
        StringBuilder sb = new StringBuilder();
        for (TypeDonjon type : TypeDonjon.values()) {
            sb.append(MenuDonjon.nomType(type)).append(" : ");
            for (Difficulte diff : Difficulte.values()) {
                if (MenuDonjon.estDebloque(diff, ctx)) {
                    sb.append(MenuDonjon.nomDiff(diff)).append(" ").append(gd.getRunsRestants(type, diff)).append("/3  ");
                }
            }
            sb.append("\n");
        }
        resumeLabel.setText(sb.toString().trim());
    }

    @FXML private void onOr(ActionEvent event)       { ouvrirDifficulte(event, TypeDonjon.OR); }
    @FXML private void onAffinage(ActionEvent event) { ouvrirDifficulte(event, TypeDonjon.AFFINAGE); }
    @FXML private void onXp(ActionEvent event)       { ouvrirDifficulte(event, TypeDonjon.XP); }

    private void ouvrirDifficulte(ActionEvent event, TypeDonjon type) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Runnable retour = () -> {
            try {
                FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranDonjon.fxml");
                EcranDonjonController controller = loader.getController();
                controller.initData(ctx);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        try {
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranDifficulteDonjon.fxml");
            EcranDifficulteDonjonController controller = loader.getController();
            controller.initData(ctx, type, retour);
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
