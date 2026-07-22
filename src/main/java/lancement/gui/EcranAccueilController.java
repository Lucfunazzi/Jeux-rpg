package lancement.gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.SauvegardeData;

public class EcranAccueilController {

    private final GameContext ctx = GameContext.creerContexteBase();
    private String pseudo;

    @FXML private VBox pseudoBox;
    @FXML private VBox choixBox;
    @FXML private TextField pseudoField;
    @FXML private Label statutLabel;
    @FXML private Button chargerButton;
    @FXML private Button nouvellePartieButton;

    @FXML
    private void onValiderPseudo(ActionEvent event) {
        pseudo = pseudoField.getText().trim();
        if (pseudo.isEmpty()) pseudo = "Aventurier";

        boolean sauvegardeExiste = ctx.sauvegarde.sauvegardeExiste(pseudo);
        if (sauvegardeExiste) {
            statutLabel.setText("Une sauvegarde existe pour " + pseudo + " !");
            chargerButton.setVisible(true);
            chargerButton.setManaged(true);
            nouvellePartieButton.setText("Ecraser et recommencer");
        } else {
            statutLabel.setText("Bienvenue pour votre premiere aventure, " + pseudo + " !");
            chargerButton.setVisible(false);
            chargerButton.setManaged(false);
            nouvellePartieButton.setText("Commencer l'aventure");
        }

        pseudoBox.setVisible(false);
        pseudoBox.setManaged(false);
        choixBox.setVisible(true);
        choixBox.setManaged(true);
    }

    @FXML
    private void onChargerPartie(ActionEvent event) {
        SauvegardeData data = ctx.sauvegarde.charger(pseudo);
        if (data == null) {
            statutLabel.setText("Erreur de chargement pour " + pseudo + ".");
            return;
        }
        ctx.restaurerDepuis(data);

        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranMenuPrincipal.fxml");
            EcranMenuPrincipalController controller = loader.getController();
            controller.initData(ctx);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onNouvellePartie(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranChoixClasse.fxml");
            EcranChoixClasseController controller = loader.getController();
            controller.initData(ctx, pseudo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onQuitter(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
