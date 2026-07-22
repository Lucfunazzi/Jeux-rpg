package lancement.gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireCompagnons;
import lancement.Gestionnaires.GestionnaireCompagnons.ResultatCompagnon;

public class EcranCompagnonsController {

    private GameContext ctx;

    @FXML private Label infoLabel;
    @FXML private Label orLabel;
    @FXML private Button actionButton;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichir();
    }

    private void rafraichir() {
        GestionnaireCompagnons gc = ctx.gestionnaireCompagnons;

        infoLabel.setText("Compagnon actif : " + gc.getType().nom + "\n"
                + "Niveau : " + gc.getNiveau() + " / " + GestionnaireCompagnons.NIVEAU_MAX + "\n"
                + String.format("Bonus equipe : +%.1f%% ATK/PV/DEF/VIT", gc.getBonusPourcentage()));
        orLabel.setText("Or disponible : " + String.format("%.0f", ctx.joueur.getOr()));

        if (!gc.estAuNiveauMax()) {
            actionButton.setText("Ameliorer -> Niv." + (gc.getNiveau() + 1) + "  (" + gc.getCoutProchainNiveau() + " or)");
            actionButton.setDisable(false);
        } else if (gc.peutEvoluer()) {
            actionButton.setText("Evoluer -> " + gc.getType().suivant().nom + "  (" + gc.getCoutEvolution() + " or)");
            actionButton.setDisable(false);
        } else {
            actionButton.setText("Compagnon au maximum actuel");
            actionButton.setDisable(true);
        }
    }

    @FXML
    private void onAction(ActionEvent event) {
        GestionnaireCompagnons gc = ctx.gestionnaireCompagnons;
        ResultatCompagnon res = gc.peutEvoluer()
                ? gc.evoluer(ctx.joueur.getOr())
                : gc.ameliorer(ctx.joueur.getOr());

        if (res.succes()) {
            ctx.joueur.setOr(ctx.joueur.getOr() - res.orDepense());
            ctx.formation.appliquerBonusLiens();
            ctx.sauvegarde.sauvegarder(ctx);
        }
        info("Compagnons", res.message());
        rafraichir();
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

    private void info(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        styliser(alert);
        alert.showAndWait();
    }

    private void styliser(Dialog<?> dialog) {
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("root-menu");
    }
}
