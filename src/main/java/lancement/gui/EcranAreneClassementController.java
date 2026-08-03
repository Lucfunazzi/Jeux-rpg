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
        String couleurMedaille = switch (a.getRang()) {
            case 1  -> "#f2c14e"; // or
            case 2  -> "#c0c0c0"; // argent
            case 3  -> "#cd7f32"; // bronze
            default -> null;
        };
        if (couleurMedaille != null) {
            rang.setStyle("-fx-text-fill: " + couleurMedaille + "; -fx-font-size: 16px;");
        }

        Label pseudo = new Label(a.getPseudo());
        pseudo.getStyleClass().add("item-nom");

        HBox infosJoueur = new HBox(8, pseudo);
        infosJoueur.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(infosJoueur, Priority.ALWAYS);
        if (!a.isEstFauxJoueur()) {
            Label tag = new Label("★ Joueur");
            tag.getStyleClass().add("item-qte");
            infosJoueur.getChildren().add(tag);
        }

        Label points = new Label(String.format("%,d pts", a.getPointsArene()));
        points.getStyleClass().add("item-qte");

        HBox ligne = new HBox(14, rang, infosJoueur, points);
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
