package lancement.gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Menus.MenuTirage_recrutement;

public class EcranTiragesController {

    private GameContext ctx;

    @FXML private VBox ressourcesBox;
    @FXML private VBox choixBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;

        choixBox.getChildren().setAll(
                GuiVisuels.creerCarteChoix("Tirage Ordinaire",
                        "Fragments C/B/A — 2% de chance d'obtenir directement un personnage complet C.",
                        this::onOrdinaire),
                GuiVisuels.creerCarteChoix("Tirage Elite",
                        "Fragments B/A/S/SS. Paliers de pity : un personnage complet garanti tous les 10/150/500 tirages.",
                        this::onElite)
        );

        rafraichir();
    }

    private void rafraichir() {
        MenuTirage_recrutement mt = ctx.menuTirage;

        FlowPane stats = new FlowPane(10, 10);
        stats.setAlignment(Pos.CENTER);
        stats.getChildren().addAll(
                GuiVisuels.creerFicheStat("Parchemins Ordinaires", String.valueOf(mt.getParcheminOrdinaire())),
                GuiVisuels.creerFicheStat("Parchemins Elite", String.valueOf(mt.getParcheminElite())),
                GuiVisuels.creerFicheStat("Coupons", String.valueOf(ctx.joueur.getCoupons()))
        );

        Label titrePity = new Label("Pity Elite");
        titrePity.getStyleClass().add("section-titre");

        VBox pity = new VBox(6,
                lignePity("Rang A", mt.getCompteurPityA(), mt.getPityA()),
                lignePity("Rang S", mt.getCompteurPityS(), mt.getPityS()),
                lignePity("Rang SS", mt.getCompteurPitySS(), mt.getPitySS())
        );
        pity.setAlignment(Pos.CENTER);

        ressourcesBox.getChildren().setAll(stats, titrePity, pity);
    }

    private Node lignePity(String label, int valeur, int max) {
        Label l = new Label(label);
        l.getStyleClass().add("item-detail");
        l.setMinWidth(60);

        HBox ligne = new HBox(10, l, GuiVisuels.creerBarreProgression(220, 14, valeur, max));
        ligne.setAlignment(Pos.CENTER_LEFT);
        return ligne;
    }

    private void onOrdinaire(MouseEvent event) {
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

    private void onElite(MouseEvent event) {
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
