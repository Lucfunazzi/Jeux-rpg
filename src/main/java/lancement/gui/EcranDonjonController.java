package lancement.gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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
        GuiVisuels.afficherExplicationPremiereVisite(ctx, "Donjon de ressources", "Donjon de ressources");
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

        String icone = switch (type) {
            case OR       -> "🪙";
            case AFFINAGE -> "⚒";
            case XP       -> "📖";
        };
        String couleur = switch (type) {
            case OR       -> "#f2c14e";
            case AFFINAGE -> "#4ea8f2";
            case XP       -> "#56c98a";
        };

        Label titre = new Label("Donjon " + MenuDonjon.nomType(type));
        titre.getStyleClass().add("item-nom");

        HBox difficultes = new HBox(14);
        difficultes.setAlignment(Pos.CENTER_LEFT);
        for (Difficulte diff : Difficulte.values()) {
            if (!MenuDonjon.estDebloque(diff, ctx)) continue;
            Label nomDiff = new Label(MenuDonjon.nomDiff(diff).trim());
            nomDiff.getStyleClass().add("item-detail");
            VBox bloc = new VBox(2, nomDiff,
                    GuiVisuels.creerBarreProgression(70, 10, gd.getRunsRestants(type, diff), 3));
            difficultes.getChildren().add(bloc);
        }

        VBox texte = new VBox(6, titre, difficultes);
        HBox carte = new HBox(14, GuiVisuels.creerIconeCadre(icone, couleur), texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(360);
        carte.setCursor(Cursor.HAND);
        carte.setOnMouseClicked(action);
        return carte;
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
