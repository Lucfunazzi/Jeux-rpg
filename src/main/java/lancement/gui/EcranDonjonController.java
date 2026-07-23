package lancement.gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireDonjon;
import lancement.Gestionnaires.GestionnaireDonjon.Difficulte;
import lancement.Gestionnaires.GestionnaireDonjon.TypeDonjon;
import lancement.Menus.MenuDonjon;

public class EcranDonjonController {

    private GameContext ctx;

    @FXML private VBox choixBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        ctx.gestionnaireDonjon.mettreAJour();
        rafraichir();
    }

    private void rafraichir() {
        choixBox.getChildren().setAll(
                carteDonjon(TypeDonjon.OR, this::onOr),
                carteDonjon(TypeDonjon.AFFINAGE, this::onAffinage),
                carteDonjon(TypeDonjon.XP, this::onXp)
        );
    }

    private Node carteDonjon(TypeDonjon type, EventHandler<MouseEvent> action) {
        GestionnaireDonjon gd = ctx.gestionnaireDonjon;
        StringBuilder sb = new StringBuilder();
        for (Difficulte diff : Difficulte.values()) {
            if (MenuDonjon.estDebloque(diff, ctx)) {
                if (sb.length() > 0) sb.append("   ");
                sb.append(MenuDonjon.nomDiff(diff)).append(" ").append(gd.getRunsRestants(type, diff)).append("/3");
            }
        }
        return GuiVisuels.creerCarteChoix(MenuDonjon.nomType(type), sb.toString(), action);
    }

    private void onOr(MouseEvent event)       { ouvrirDifficulte(event, TypeDonjon.OR); }
    private void onAffinage(MouseEvent event) { ouvrirDifficulte(event, TypeDonjon.AFFINAGE); }
    private void onXp(MouseEvent event)       { ouvrirDifficulte(event, TypeDonjon.XP); }

    private void ouvrirDifficulte(MouseEvent event, TypeDonjon type) {
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
