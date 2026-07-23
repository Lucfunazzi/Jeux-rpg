package lancement.gui;

import Joueur.ArbreCompetences;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Menus.MenuAbilite;

public class EcranAbilitesController {

    private GameContext ctx;

    @FXML private Label infoLabel;
    @FXML private Button boutonArbre1;
    @FXML private Button boutonArbre2;
    @FXML private Button boutonArbre3;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichir();
    }

    private void rafraichir() {
        ArbreCompetences arbre = ctx.joueur.getArbreCompetences();
        String[] noms = ctx.joueur.getNomsAttaques();
        String classe = ctx.joueur.getChoixClasses();

        infoLabel.setText("Points disponibles : " + arbre.getPointsDisponibles() + "\n"
                + "Speciale active : " + noms[1] + "\n"
                + "Ultime active   : " + noms[2]);

        boutonArbre1.setText("Arbre 1 - Nouvelle Speciale"
                + (arbre.isNoeud10Debloque() ? "  [DEBLOQUE : " + MenuAbilite.getNomCompetence(classe, 1) + "]" : ""));

        if (!arbre.isArbre2Debloque()) {
            boutonArbre2.setText("Arbre 2 - Nouvelle Speciale  [VERROUILLE - terminez l'Arbre 1]");
            boutonArbre2.setDisable(true);
        } else {
            boutonArbre2.setText("Arbre 2 - Nouvelle Speciale"
                    + (arbre.isNoeud10Arbre2Debloque() ? "  [DEBLOQUE : " + MenuAbilite.getNomCompetence(classe, 2) + "]" : ""));
            boutonArbre2.setDisable(false);
        }

        if (!arbre.isArbre3Debloque()) {
            boutonArbre3.setText("Arbre 3 - Nouvelle Speciale  [VERROUILLE - terminez l'Arbre 2]");
            boutonArbre3.setDisable(true);
        } else {
            boutonArbre3.setText("Arbre 3 - Nouvelle Speciale"
                    + (arbre.isNoeud10Arbre3Debloque() ? "  [DEBLOQUE : " + MenuAbilite.getNomCompetence(classe, 3) + "] - Rang B !" : ""));
            boutonArbre3.setDisable(false);
        }
    }

    @FXML private void onArbre1(ActionEvent event) { ouvrirArbre(event, 1); }
    @FXML private void onArbre2(ActionEvent event) { ouvrirArbre(event, 2); }
    @FXML private void onArbre3(ActionEvent event) { ouvrirArbre(event, 3); }

    private void ouvrirArbre(ActionEvent event, int numero) {
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
