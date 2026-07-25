package lancement.gui;

import Joueur.ChasseurDeDragon;
import Joueur.Chevalier;
import Joueur.Competences;
import Joueur.Elementaliste;
import Joueur.Invocateur;
import Joueur.Personnage_principale;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;

public class EcranChoixClasseController {

    private final Competences competChevalier          = new Chevalier();
    private final Competences competChasseur           = new ChasseurDeDragon();
    private final Competences competMage                = new Elementaliste();
    private final Competences competConstellationniste  = new Invocateur();

    private GameContext ctx;
    private String pseudo;
    private String genreChoisi = "Homme";

    @FXML private Label sousTitre;
    @FXML private HBox genreBox;
    @FXML private FlowPane classesBox;

    @FXML
    private void initialize() {
        rafraichirGenre();
        rafraichirClasses();
    }

    public void initData(GameContext ctx, String pseudo) {
        this.ctx = ctx;
        this.pseudo = pseudo;
        sousTitre.setText("Bienvenue, " + pseudo + " ! Choisissez votre genre et votre classe :");
    }

    // ── Genre ─────────────────────────────────────────────────────────────
    private void rafraichirGenre() {
        genreBox.getChildren().setAll(carteGenre("Homme"), carteGenre("Femme"));
    }

    private Node carteGenre(String genre) {
        Label nom = new Label(genre);
        nom.getStyleClass().add("item-nom");

        VBox carte = new VBox(nom);
        carte.setAlignment(Pos.CENTER);
        carte.getStyleClass().add(genre.equals(genreChoisi) ? "carte-item-joueur" : "carte-item");
        carte.setPrefWidth(110);
        carte.setCursor(Cursor.HAND);
        carte.setOnMouseClicked(e -> {
            genreChoisi = genre;
            rafraichirGenre();
            rafraichirClasses();
        });
        return carte;
    }

    // ── Classes ───────────────────────────────────────────────────────────
    private void rafraichirClasses() {
        classesBox.getChildren().setAll(
                carteClasse("Chevalier", competChevalier),
                carteClasse("Chasseur de Dragon", competChasseur),
                carteClasse("Mage", competMage),
                carteClasse("Constellationniste", competConstellationniste)
        );
    }

    private Node carteClasse(String classeInterne, Competences competences) {
        String[] noms = competences.getNomsCompetences();
        String nomAffiche = Personnage_principale.nomClasseAffiche(classeInterne, genreChoisi);

        Label nom = new Label(nomAffiche);
        nom.getStyleClass().add("item-nom");
        nom.setStyle("-fx-font-size: 16px;");

        Label special = new Label("Spéciale : " + noms[0]);
        special.getStyleClass().add("item-detail");
        special.setWrapText(true);
        special.setMaxWidth(220);

        Label ultime = new Label("Ultime : " + noms[1]);
        ultime.getStyleClass().add("item-detail");
        ultime.setWrapText(true);
        ultime.setMaxWidth(220);

        Label voirFiche = new Label("Voir la fiche");
        voirFiche.setStyle("-fx-font-size: 11px; -fx-text-fill: #4ea8f2; -fx-underline: true;");

        VBox texte = new VBox(4, nom, special, ultime, voirFiche);
        texte.setAlignment(Pos.CENTER);

        VBox carte = new VBox(texte);
        carte.setAlignment(Pos.CENTER);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(240);
        carte.setCursor(Cursor.HAND);
        carte.setOnMouseClicked(e -> ouvrirFiche(classeInterne, nomAffiche, competences));
        return carte;
    }

    private void ouvrirFiche(String classeInterne, String nomAffiche, Competences competences) {
        try {
            Stage stage = (Stage) classesBox.getScene().getWindow();
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranFicheClasse.fxml");
            EcranFicheClasseController controller = loader.getController();
            controller.initData(ctx, pseudo, genreChoisi, classeInterne, nomAffiche, competences);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @FXML
    private void onRetour(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Navigation.changerEcran(stage, "/fxml/EcranAccueil.fxml");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
