package lancement.gui;

import java.io.IOException;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
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

        if (GuiVisuels.unCoffreChapitreDisponible(ctx, ligne)) {
            Button coffres = new Button("Coffres disponibles !");
            coffres.getStyleClass().add("menu-bouton");
            coffres.setOnAction(e -> GuiVisuels.ouvrirCoffresChapitre(ctx, ligne, this::rafraichir));
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

    @FXML
    private void onRetour(ActionEvent event) {
        onRetour.run();
    }

    @FXML
    private void onQuetes(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranQuetes.fxml");
            EcranQuetesController controller = loader.getController();
            controller.initData(ctx);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
