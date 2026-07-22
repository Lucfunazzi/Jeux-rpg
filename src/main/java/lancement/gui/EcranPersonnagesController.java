package lancement.gui;

import Equipement.Equipement;
import Personnage.PersonnageBase;
import java.io.IOException;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;

public class EcranPersonnagesController {

    private GameContext ctx;

    @FXML private VBox persosBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichir();
    }

    private void rafraichir() {
        persosBox.getChildren().clear();

        ArrayList<PersonnageBase> tous = new ArrayList<>();
        tous.add(ctx.joueur);
        tous.addAll(ctx.personnagesRecruites);

        for (PersonnageBase p : tous) {
            int piecesC = compterPiecesRangC(p);
            String set = piecesC > 0 ? "  Set C : " + piecesC + "/6" : "";
            boolean dansFormation = ctx.formation.getEquipe().contains(p);
            String tag = dansFormation ? "  [F]" : "";

            Button bouton = new Button(p.getNom()
                    + "  Niv." + p.getNiveau()
                    + "  [" + p.getRarete() + "]"
                    + "  " + (p.getType() != null ? p.getType() : "")
                    + "  " + p.getRole()
                    + tag + set);
            bouton.getStyleClass().add("menu-bouton");
            bouton.setWrapText(true);
            bouton.setPrefWidth(420);
            bouton.setOnAction(e -> ouvrirFiche(e, p));
            persosBox.getChildren().add(bouton);
        }
    }

    private int compterPiecesRangC(PersonnageBase p) {
        int count = 0;
        for (Equipement.Slot slot : Equipement.Slot.values()) {
            Equipement e = p.getEquipement(slot);
            if (e != null && e.getRarete() == Equipement.Rarete.C) count++;
        }
        return count;
    }

    private void ouvrirFiche(ActionEvent event, PersonnageBase p) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
