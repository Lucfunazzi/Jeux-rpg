package lancement.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireEtoiles;

public class EcranListeChapitresController {

    private GameContext ctx;
    private List<LigneChapitre> lignes;
    private Runnable onRetour;

    @FXML private Label titreLabel;
    @FXML private VBox lignesBox;

    public void initData(GameContext ctx, String titre, List<LigneChapitre> lignes, Runnable onRetour) {
        this.ctx = ctx;
        this.lignes = lignes;
        this.onRetour = onRetour;
        titreLabel.setText(titre);
        rafraichir();
    }

    private void rafraichir() {
        lignesBox.getChildren().clear();
        GestionnaireEtoiles ge = ctx.gestionnaireEtoiles;

        for (LigneChapitre ligne : lignes) {
            VBox carte = new VBox(8);

            if (!ligne.deverrouille()) {
                // Le nom/sous-titre du chapitre reste cache tant qu'il n'est pas debloque.
                int tiret = ligne.label().indexOf(" - ");
                String nomGenerique = tiret >= 0 ? ligne.label().substring(0, tiret) : ligne.label();

                Label verrouille = new Label(nomGenerique + "  [VERROUILLE]\n" + ligne.messageVerrouille());
                verrouille.getStyleClass().add("texte");
                verrouille.setWrapText(true);
                carte.getChildren().add(verrouille);
                lignesBox.getChildren().add(carte);
                continue;
            }

            int etoiles = ge.compterEtoiles(ligne.numeroChapitre(), ligne.elite());
            Label infoLabel = new Label(ligne.label() + "  [" + etoiles + "/30 etoiles]");
            infoLabel.getStyleClass().add("texte");
            infoLabel.setWrapText(true);

            HBox boutons = new HBox(10);
            boutons.setStyle("-fx-alignment: center;");

            Button entrer = new Button("Entrer");
            entrer.getStyleClass().add("menu-bouton");
            entrer.setOnAction(e -> ouvrirStages(e, ligne));
            boutons.getChildren().add(entrer);

            boolean unCoffreDispo = ge.coffreDisponible(ligne.numeroChapitre(), ligne.elite(), 1)
                    || ge.coffreDisponible(ligne.numeroChapitre(), ligne.elite(), 2)
                    || ge.coffreDisponible(ligne.numeroChapitre(), ligne.elite(), 3);
            if (unCoffreDispo) {
                Button coffres = new Button("Coffres disponibles !");
                coffres.getStyleClass().add("menu-bouton");
                coffres.setOnAction(e -> ouvrirCoffres(ligne));
                boutons.getChildren().add(coffres);
            }

            carte.getChildren().addAll(infoLabel, boutons);
            lignesBox.getChildren().add(carte);
        }
    }

    private void ouvrirStages(ActionEvent event, LigneChapitre ligne) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Runnable retourIci = () -> {
            try {
                FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranListeChapitres.fxml");
                EcranListeChapitresController controller = loader.getController();
                controller.initData(ctx, titreLabel.getText(), lignes, onRetour);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        try {
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranStages.fxml");
            EcranStagesController controller = loader.getController();
            controller.initData(ctx, ligne, retourIci);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void ouvrirCoffres(LigneChapitre ligne) {
        GestionnaireEtoiles ge = ctx.gestionnaireEtoiles;
        String[] labelsRecomp = {
            "2x Parchemin Tirage Ordinaire",
            "5x Parchemin Tirage Ordinaire",
            "1x Parchemin Tirage Elite"
        };

        Map<String, Integer> map = new LinkedHashMap<>();
        List<String> options = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            if (!ge.coffreDisponible(ligne.numeroChapitre(), ligne.elite(), i)) continue;
            String libelle = "Coffre " + i + " (" + ge.getSeuilCoffre(i) + " etoiles) -> " + labelsRecomp[i - 1];
            options.add(libelle);
            map.put(libelle, i);
        }

        if (options.isEmpty()) { info("Coffres", "Aucun coffre disponible pour l'instant."); return; }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle("Coffres - " + ligne.label());
        dialog.setHeaderText(null);
        dialog.setContentText("Coffre a reclamer :");
        styliser(dialog);
        Optional<String> resultat = dialog.showAndWait();
        if (resultat.isEmpty()) return;

        int numeroCoffre = map.get(resultat.get());
        GestionnaireEtoiles.RecompenseCoffre recomp = ge.reclamerCoffre(ligne.numeroChapitre(), ligne.elite(), numeroCoffre);
        if (recomp == null) { info("Coffres", "Ce coffre n'est pas disponible."); return; }

        String message = switch (recomp.type()) {
            case PARCHEMIN_ORDINAIRE -> {
                ctx.menuTirage.setParcheminOrdinaire(ctx.menuTirage.getParcheminOrdinaire() + recomp.quantite());
                yield "+ " + recomp.quantite() + " Parchemin(s) de Tirage Ordinaire !\nTotal : " + ctx.menuTirage.getParcheminOrdinaire();
            }
            case PARCHEMIN_ELITE -> {
                ctx.menuTirage.setParcheminElite(ctx.menuTirage.getParcheminElite() + recomp.quantite());
                yield "+ " + recomp.quantite() + " Parchemin(s) de Tirage Elite !\nTotal : " + ctx.menuTirage.getParcheminElite();
            }
        };

        ctx.sauvegarde.sauvegarder(ctx);
        info("Coffres", message);
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
