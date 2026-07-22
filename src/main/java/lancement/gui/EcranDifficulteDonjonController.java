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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireDonjon.Difficulte;
import lancement.Gestionnaires.GestionnaireDonjon.TypeDonjon;
import lancement.Menus.MenuDonjon;

public class EcranDifficulteDonjonController {

    private GameContext ctx;
    private TypeDonjon type;
    private Runnable onRetour;

    @FXML private Label titreLabel;
    @FXML private VBox difficultesBox;

    public void initData(GameContext ctx, TypeDonjon type, Runnable onRetour) {
        this.ctx = ctx;
        this.type = type;
        this.onRetour = onRetour;
        titreLabel.setText("DONJON " + MenuDonjon.nomType(type).toUpperCase());
        rafraichir();
    }

    private void rafraichir() {
        difficultesBox.getChildren().clear();

        for (Difficulte diff : Difficulte.values()) {
            int niveauRequis = switch (diff) {
                case NORMAL    -> 1;
                case DIFFICILE -> MenuDonjon.NIV_DIFFICILE;
                case EXTREME   -> MenuDonjon.NIV_EXTREME;
            };
            String recompense = MenuDonjon.descriptionRecompense(type, diff);
            boolean debloque = MenuDonjon.estDebloque(diff, ctx);

            String libelle = debloque
                    ? MenuDonjon.nomDiff(diff) + "  [" + ctx.gestionnaireDonjon.getRunsRestants(type, diff) + "/3 runs]  " + recompense
                    : MenuDonjon.nomDiff(diff) + "  [Debloque niveau " + niveauRequis + "]  " + recompense;

            Button bouton = new Button(libelle);
            bouton.getStyleClass().add("menu-bouton");
            bouton.setWrapText(true);
            bouton.setPrefWidth(360);
            if (!debloque) {
                bouton.setDisable(true);
            } else {
                bouton.setOnAction(e -> lancer(diff, (Stage) ((Node) e.getSource()).getScene().getWindow()));
            }
            difficultesBox.getChildren().add(bouton);
        }
    }

    private void lancer(Difficulte diff, Stage stage) {
        if (!ctx.gestionnaireDonjon.peutFaireRun(type, diff)) {
            info("Donjon", "Plus de runs disponibles pour aujourd'hui !");
            return;
        }
        if (ctx.formation.getEquipe().isEmpty()) {
            info("Donjon", "Votre formation est vide ! Ajoutez des personnages d'abord.");
            return;
        }

        MenuDonjon.ResultatRun resultat = new MenuDonjon()
                .lancerRunAvecEvenements(type, diff, ctx, Navigation.scannerSilencieux());
        if (!resultat.lance()) {
            rafraichir();
            return;
        }

        try {
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranCombat.fxml");
            EcranCombatController controller = loader.getController();
            controller.initCombat(resultat.etatInitial(), resultat.evenements(), resultat.victoire(),
                    v -> retourDifficulte(stage));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void retourDifficulte(Stage stage) {
        try {
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranDifficulteDonjon.fxml");
            EcranDifficulteDonjonController controller = loader.getController();
            controller.initData(ctx, type, onRetour);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onRetour(ActionEvent event) {
        onRetour.run();
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
