package lancement.gui;

import java.io.IOException;
import java.util.Arrays;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireChasseTresor;

public class EcranChasseTresorController {

    private static final int NB_EMPLACEMENTS = 6;

    private GameContext ctx;
    private final boolean[] fouilles = new boolean[NB_EMPLACEMENTS];

    @FXML private FlowPane statsBox;
    @FXML private Label statutLabel;
    @FXML private FlowPane grilleBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        GuiVisuels.afficherExplicationPremiereVisite(ctx, "Chasse au tresor", "Chasse au tresor");
        ctx.gestionnaireChasseTresor.mettreAJour();
        Arrays.fill(fouilles, false);
        rafraichir();
    }

    private void rafraichir() {
        statsBox.getChildren().setAll(
                GuiVisuels.creerFicheStat("Parchemin A", String.valueOf(ctx.inventaire.getQuantiteMateriau(GestionnaireChasseTresor.PARCHEMIN_A))),
                GuiVisuels.creerFicheStat("Parchemin S", String.valueOf(ctx.inventaire.getQuantiteMateriau(GestionnaireChasseTresor.PARCHEMIN_S))),
                GuiVisuels.creerFicheStat("Parchemin SS", String.valueOf(ctx.inventaire.getQuantiteMateriau(GestionnaireChasseTresor.PARCHEMIN_SS)))
        );

        int restantes = ctx.gestionnaireChasseTresor.getFouillesRestantes();
        statutLabel.setText("Fouilles restantes aujourd'hui : " + restantes + "/" + GestionnaireChasseTresor.MAX_FOUILLES_PAR_JOUR);

        grilleBox.getChildren().clear();
        for (int i = 0; i < NB_EMPLACEMENTS; i++) {
            grilleBox.getChildren().add(carteEmplacement(i, restantes > 0));
        }
    }

    private Node carteEmplacement(int index, boolean disponible) {
        boolean dejaFouille = fouilles[index];

        Label icone = new Label(dejaFouille ? "✔" : "?");
        icone.setStyle("-fx-font-size: 26px;");

        Label texte = new Label(dejaFouille ? "Fouille" : "Emplacement");
        texte.getStyleClass().add("item-detail");

        VBox carte = new VBox(6, icone, texte);
        carte.setAlignment(Pos.CENTER);
        carte.setPrefSize(100, 100);
        carte.getStyleClass().add(dejaFouille ? "carte-item" : "carte-item-joueur");
        carte.setOpacity(dejaFouille || !disponible ? 0.5 : 1.0);

        if (!dejaFouille && disponible) {
            carte.setCursor(Cursor.HAND);
            carte.setOnMouseClicked(e -> onFouiller(index));
        }
        return carte;
    }

    private void onFouiller(int index) {
        if (!ctx.gestionnaireChasseTresor.peutFouiller()) {
            info("Chasse au tresor", "Plus de fouilles disponibles aujourd'hui ! Revenez demain.");
            rafraichir();
            return;
        }
        fouilles[index] = true;
        GestionnaireChasseTresor.ResultatFouille r = ctx.gestionnaireChasseTresor.fouiller(ctx.inventaire, ctx.joueur);
        ctx.sauvegarde.sauvegarder(ctx);
        info("Chasse au tresor", formaterResultat(r));
        rafraichir();
    }

    private String formaterResultat(GestionnaireChasseTresor.ResultatFouille r) {
        if (r.materiau() == null) return "Petit tresor... vous trouvez " + r.or() + " or !";
        return "Vous deterrez " + r.quantite() + "x " + r.materiau() + " !";
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
