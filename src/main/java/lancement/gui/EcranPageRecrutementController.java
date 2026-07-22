package lancement.gui;

import Personnage.PersonnageBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lancement.GameContext;
import lancement.Menus.MenuRecrutement;

public class EcranPageRecrutementController {

    private GameContext ctx;
    private int numero;
    private Runnable onRetour;

    @FXML private Label titreLabel;
    @FXML private Label parcheminsLabel;
    @FXML private VBox persosBox;

    public void initData(GameContext ctx, int numero, Runnable onRetour) {
        this.ctx = ctx;
        this.numero = numero;
        this.onRetour = onRetour;
        titreLabel.setText("PAGE " + numero + " - RANG " + MenuRecrutement.getRangPage(numero));
        rafraichir();
    }

    private void rafraichir() {
        int requis = MenuRecrutement.getParcheminsRequisPage(numero);
        String rang = MenuRecrutement.getRangPage(numero);
        int actuels = parcheminsActuels(rang);
        parcheminsLabel.setText("Parchemins " + rang + " : " + actuels + "/" + requis);

        persosBox.getChildren().clear();
        for (String[] info : MenuRecrutement.getPage(numero)) {
            String nom = info[0], role = info[1];
            boolean dejaRecrute = ctx.personnagesRecruites.stream()
                    .anyMatch(p -> p.getNom().equalsIgnoreCase(nom));

            Button bouton = new Button(nom + "  [" + role + "]  - " + requis + " parchemins " + rang
                    + (dejaRecrute ? "  [DEJA RECRUTE]" : ""));
            bouton.getStyleClass().add("menu-bouton");
            bouton.setWrapText(true);
            bouton.setPrefWidth(380);
            if (dejaRecrute) {
                bouton.setDisable(true);
            } else {
                bouton.setOnAction(e -> recruter(nom));
            }
            persosBox.getChildren().add(bouton);
        }
    }

    private int parcheminsActuels(String rang) {
        return switch (rang) {
            case "C" -> ctx.menuRecrutement.getParcheminC();
            case "B" -> ctx.menuRecrutement.getParcheminB();
            default  -> ctx.menuRecrutement.getParcheminA();
        };
    }

    private void recruter(String nom) {
        int requis = MenuRecrutement.getParcheminsRequisPage(numero);
        String rang = MenuRecrutement.getRangPage(numero);
        int actuels = parcheminsActuels(rang);

        if (actuels < requis) {
            info("Recrutement", "Pas assez de parchemins " + rang + " ! (" + actuels + "/" + requis + ")");
            return;
        }

        PersonnageBase recrute = ctx.menuRecrutement.creerPersonnage(nom);
        if (recrute == null) {
            info("Recrutement", "[ERREUR] Personnage introuvable : " + nom + ". Aucun parchemin deduit.");
            return;
        }

        switch (rang) {
            case "C" -> ctx.menuRecrutement.ajouterParcheminC(-requis);
            case "B" -> ctx.menuRecrutement.ajouterParcheminB(-requis);
            default  -> ctx.menuRecrutement.ajouterParcheminA(-requis);
        }
        ctx.personnagesRecruites.add(recrute);
        ctx.sauvegarde.sauvegarder(ctx);
        info("Recrutement", recrute.getNom() + " a rejoint vos allies !");
        rafraichir();
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
