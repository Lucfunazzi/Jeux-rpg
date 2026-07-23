package lancement.gui;

import Joueur.ArbreCompetences;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Menus.MenuAbilite;

public class EcranAbilitesController {

    private GameContext ctx;

    @FXML private VBox statsBox;
    @FXML private VBox choixBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichir();
    }

    private void rafraichir() {
        ArbreCompetences arbre = ctx.joueur.getArbreCompetences();
        String[] noms = ctx.joueur.getNomsAttaques();
        String classe = ctx.joueur.getChoixClasses();

        FlowPane stats = new FlowPane(10, 10);
        stats.setAlignment(Pos.CENTER);
        stats.getChildren().addAll(
                GuiVisuels.creerFicheStat("Points disponibles", String.valueOf(arbre.getPointsDisponibles())),
                GuiVisuels.creerFicheStat("Spéciale active", noms[1]),
                GuiVisuels.creerFicheStat("Ultime active", noms[2])
        );
        statsBox.getChildren().setAll(stats);

        choixBox.getChildren().setAll(
                carteArbre(1, "Arbre 1 - Nouvelle Spéciale", true, arbre.isNoeud10Debloque(), classe),
                carteArbre(2, "Arbre 2 - Nouvelle Spéciale", arbre.isArbre2Debloque(), arbre.isNoeud10Arbre2Debloque(), classe),
                carteArbre(3, "Arbre 3 - Nouvelle Spéciale", arbre.isArbre3Debloque(), arbre.isNoeud10Arbre3Debloque(), classe)
        );
    }

    private Node carteArbre(int numero, String titre, boolean accesDebloque, boolean completee, String classe) {
        String description;
        if (!accesDebloque) {
            description = "Verrouillé — terminez l'arbre précédent.";
        } else if (completee) {
            description = "Débloqué : " + MenuAbilite.getNomCompetence(classe, numero)
                    + (numero == 3 ? " — Rang B !" : "");
        } else {
            description = "Nouvelle spéciale à débloquer.";
        }

        Node carte = GuiVisuels.creerCarteChoix(titre, description, e -> ouvrirArbre(e, numero));
        if (completee) carte.getStyleClass().add("carte-item-joueur");
        if (!accesDebloque) {
            carte.setOpacity(0.4);
            carte.setCursor(Cursor.DEFAULT);
            carte.setOnMouseClicked(null);
        }
        return carte;
    }

    private void ouvrirArbre(MouseEvent event, int numero) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Runnable retour = () -> {
            try {
                FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranAbilites.fxml");
                EcranAbilitesController controller = loader.getController();
                controller.initData(ctx);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        try {
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranArbre.fxml");
            EcranArbreController controller = loader.getController();
            controller.initData(ctx, numero, retour);
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
