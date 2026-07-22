package lancement.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lancement.GameContext;
import lancement.Gestionnaires.AreneData;
import lancement.Menus.MenuBoutiqueArene;

public class EcranAreneBoutiqueController {

    private MenuBoutiqueArene menuBoutiqueArene;
    private AreneData joueurArene;
    private Runnable onRetour;

    @FXML private Label pointsLabel;
    @FXML private VBox catalogueBox;

    public void initData(GameContext ctx, AreneData joueurArene, Runnable onRetour) {
        this.joueurArene = joueurArene;
        this.onRetour = onRetour;
        this.menuBoutiqueArene = new MenuBoutiqueArene(ctx, Navigation.scannerSilencieux(), joueurArene);
        rafraichir();
    }

    private void rafraichir() {
        pointsLabel.setText("Points boutique disponibles : " + joueurArene.getPointsBoutique() + " pts");

        catalogueBox.getChildren().clear();
        for (Object[] entree : MenuBoutiqueArene.getCatalogue()) {
            String nom = (String) entree[0];
            String rarete = (String) entree[1];
            int prix = (int) entree[2];
            boolean possede = menuBoutiqueArene.dejaRecruté(nom);

            Button bouton = new Button(nom + "  [" + rarete + "]  "
                    + (possede ? "- Deja recrute" : prix + " pts"));
            bouton.getStyleClass().add("menu-bouton");
            bouton.setWrapText(true);
            bouton.setPrefWidth(320);
            if (possede) {
                bouton.setDisable(true);
            } else {
                bouton.setOnAction(e -> acheter(nom, prix));
            }
            catalogueBox.getChildren().add(bouton);
        }
    }

    private void acheter(String nom, int prix) {
        boolean confirme = confirmer("Recruter " + nom + " pour " + prix + " points boutique ?");
        if (!confirme) return;

        info("Boutique", menuBoutiqueArene.executerAchat(nom, prix));
        rafraichir();
    }

    private boolean confirmer(String question) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, question, ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        styliser(confirm);
        return confirm.showAndWait().filter(b -> b == ButtonType.YES).isPresent();
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
