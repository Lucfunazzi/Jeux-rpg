package lancement.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
            if (!ligne.deverrouille()) {
                lignesBox.getChildren().add(carteVerrouillee(ligne));
                continue;
            }
            lignesBox.getChildren().add(carteChapitre(ligne, ge));
        }
    }

    /** Carte grisée mais explicite pour un chapitre pas encore débloqué (au lieu d'un simple label facile à manquer). */
    private Node carteVerrouillee(LigneChapitre ligne) {
        int tiret = ligne.label().indexOf(" - ");
        String nomGenerique = tiret >= 0 ? ligne.label().substring(0, tiret) : ligne.label();

        Label nom = new Label(nomGenerique);
        nom.getStyleClass().add("item-nom");
        Label detail = new Label("🔒 Verrouillé — " + ligne.messageVerrouille());
        detail.getStyleClass().add("item-detail");
        detail.setWrapText(true);
        detail.setMaxWidth(420);

        VBox texte = new VBox(4, nom, detail);
        VBox carte = new VBox(texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setOpacity(0.6);
        carte.setPrefWidth(460);
        return carte;
    }

    private Node carteChapitre(LigneChapitre ligne, GestionnaireEtoiles ge) {
        int etoiles = ge.compterEtoiles(ligne.numeroChapitre(), ligne.elite());

        Label nom = new Label(ligne.label());
        nom.getStyleClass().add("item-nom");
        Label detail = new Label(etoiles + " / 30 étoiles");
        detail.getStyleClass().add("item-qte");

        VBox texteBox = new VBox(4, nom, detail);

        HBox boutons = new HBox(10);
        boutons.setAlignment(Pos.CENTER_LEFT);

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

        VBox contenu = new VBox(10, texteBox, boutons);
        VBox carte = new VBox(contenu);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item-joueur");
        carte.setPrefWidth(460);
        return carte;
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

        List<Integer> options = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            if (ge.coffreDisponible(ligne.numeroChapitre(), ligne.elite(), i)) options.add(i);
        }

        if (options.isEmpty()) { info("Coffres", "Aucun coffre disponible pour l'instant."); return; }

        Integer choix = GuiVisuels.choisirParmiCartes("Coffres - " + ligne.label(), options,
                i -> carteCoffre(ge, i, labelsRecomp[i - 1]));
        if (choix == null) return;

        int numeroCoffre = choix;
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

    private Node carteCoffre(GestionnaireEtoiles ge, int numero, String recompense) {
        Label nom = new Label("Coffre " + numero);
        nom.getStyleClass().add("item-nom");
        Label detail = new Label(ge.getSeuilCoffre(numero) + " étoiles → " + recompense);
        detail.getStyleClass().add("item-detail");
        detail.setWrapText(true);
        detail.setMaxWidth(260);

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(300);
        return carte;
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
