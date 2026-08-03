package lancement.gui;

import Equipement.Equipement;
import Personnage.PersonnageBase;
import java.io.IOException;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;

public class EcranPersonnagesController {

    private GameContext ctx;

    @FXML private VBox persosBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        GuiVisuels.afficherExplicationPremiereVisite(ctx, "Personnages", "Personnages");
        rafraichir();
    }

    private void rafraichir() {
        persosBox.getChildren().clear();

        ArrayList<PersonnageBase> tous = new ArrayList<>();
        tous.add(ctx.joueur);
        tous.addAll(ctx.personnagesRecruites);

        FlowPane grille = new FlowPane(12, 12);
        grille.setAlignment(Pos.CENTER);
        for (PersonnageBase p : tous) {
            grille.getChildren().add(cartePersonnage(p));
        }
        persosBox.getChildren().add(grille);
    }

    private Node cartePersonnage(PersonnageBase p) {
        boolean dansFormation = ctx.formation.getEquipe().contains(p);
        Equipement.Rarete raretesSet = p.getRareteSetDominante();
        int piecesSet = raretesSet != null ? p.compterPieces(raretesSet) : 0;

        Label badge = GuiVisuels.creerBadgeRarete(p.getRarete());

        Label nomLabel = new Label(p.getNom());
        nomLabel.getStyleClass().add("item-nom");

        String nomClasse = GuiVisuels.nomClasseAffiche(p);
        Label detailLabel = new Label("Niv. " + p.getNiveau()
                + (!nomClasse.isEmpty() ? "  ·  " + nomClasse : "")
                + "  ·  " + iconeRole(p.getRole()) + " " + p.getRole());
        detailLabel.getStyleClass().add("item-detail");

        VBox texte = new VBox(4, nomLabel, detailLabel, GuiVisuels.creerBarrePV(150, 14, p.getVie(), p.getVieMax()));

        if (raretesSet != null) {
            Label setLabel = new Label("Set " + raretesSet + " : " + piecesSet + "/6");
            setLabel.getStyleClass().add("item-qte");
            texte.getChildren().add(setLabel);
        }
        if (dansFormation) {
            Label formationLabel = new Label("✔ En formation");
            formationLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #f2c14e;");
            texte.getChildren().add(formationLabel);
        }

        HBox carte = new HBox(10, badge, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add(dansFormation ? "carte-item-joueur" : "carte-item");
        carte.setPrefWidth(260);
        carte.setCursor(Cursor.HAND);
        carte.setOnMouseClicked(e -> ouvrirFiche(carte, p));
        return carte;
    }

    private String iconeRole(String role) {
        return switch (role) {
            case "Tank"    -> "⛨";
            case "Support" -> "✚";
            default        -> "⚔"; // DPS
        };
    }

    private void ouvrirFiche(Node source, PersonnageBase p) {
        Stage stage = (Stage) source.getScene().getWindow();
        Runnable retour = () -> {
            try {
                FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranPersonnages.fxml");
                EcranPersonnagesController controller = loader.getController();
                controller.initData(ctx);
            } catch (IOException e2) {
                throw new RuntimeException(e2);
            }
        };
        try {
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranFichePersonnage.fxml");
            EcranFichePersonnageController controller = loader.getController();
            controller.initData(ctx, p, retour);
        } catch (IOException e2) {
            throw new RuntimeException(e2);
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
