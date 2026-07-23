package lancement.gui;

import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lancement.GameContext;
import lancement.Gestionnaires.AreneData;
import lancement.Gestionnaires.GestionnaireArene;

public class EcranAreneClassementController {

    private Runnable onRetour;

    @FXML private VBox lignesBox;

    public void initData(GameContext ctx, GestionnaireArene gestionnaireArene, AreneData joueurArene, Runnable onRetour) {
        this.onRetour = onRetour;

        List<AreneData> adversaires = gestionnaireArene.getAdversairesVisibles(joueurArene.getRang());

        lignesBox.getChildren().clear();
        lignesBox.getChildren().add(ligneClassement(joueurArene, true));
        for (AreneData a : adversaires) {
            lignesBox.getChildren().add(ligneClassement(a, false));
        }

        Label legende = new Label("★ = vrai joueur");
        legende.getStyleClass().add("item-vide");
        lignesBox.getChildren().add(legende);
    }

    private Node ligneClassement(AreneData a, boolean estJoueur) {
        Label rang = new Label("#" + a.getRang());
        rang.getStyleClass().add("item-nom");
        rang.setMinWidth(50);

        Label pseudo = new Label(a.getPseudo() + (a.isEstFauxJoueur() ? "" : "  ★"));
        pseudo.getStyleClass().add("item-nom");
        HBox.setHgrow(pseudo, Priority.ALWAYS);

        Label points = new Label(String.format("%,d pts", a.getPointsArene()));
        points.getStyleClass().add("item-qte");

        HBox ligne = new HBox(14, rang, pseudo, points);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.getStyleClass().add("carte-item");
        if (estJoueur) ligne.getStyleClass().add("carte-item-joueur");
        return ligne;
    }

    @FXML
    private void onRetour() {
        onRetour.run();
    }
}
