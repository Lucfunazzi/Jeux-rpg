package lancement.gui;

import Joueur.ArbreCompetences;
import Joueur.Personnage_principale;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireTitres;
import lancement.RangJoueur;
import lancement.Titre;

public class EcranRangTitresController {

    private GameContext ctx;

    @FXML private VBox rangBox;
    @FXML private VBox arbresBox;
    @FXML private VBox titreBox;
    @FXML private VBox choixBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;

        choixBox.getChildren().setAll(
                GuiVisuels.creerCarteChoix("Tenter une montée de rang",
                        "Vérifie si vous remplissez les conditions pour le rang supérieur.", e -> onMonteeRang()),
                GuiVisuels.creerCarteChoix("Gérer mes titres",
                        "Équiper un titre obtenu pour un bonus de stats à toute l'équipe.", e -> onGererTitres())
        );

        rafraichir();
    }

    private void rafraichir() {
        RangJoueur rangJoueur = ctx.rangJoueur;

        Label nomRang = new Label(rangJoueur.afficherRang());
        nomRang.getStyleClass().add("item-nom");
        nomRang.setStyle("-fx-font-size: 18px;");
        VBox carteRang = new VBox(nomRang);
        carteRang.setAlignment(Pos.CENTER);
        carteRang.getStyleClass().add("carte-item-joueur");
        carteRang.setPrefWidth(320);
        rangBox.getChildren().setAll(carteRang);

        arbresBox.getChildren().clear();
        if (rangJoueur.getRang() == RangJoueur.Rang.C) {
            ArbreCompetences arbre = ctx.joueur.getArbreCompetences();
            arbresBox.getChildren().add(ligneArbre("Arbre 1", progressionArbre(arbre, 1), true));
            arbresBox.getChildren().add(ligneArbre("Arbre 2", progressionArbre(arbre, 2), true));
            arbresBox.getChildren().add(ligneArbre("Arbre 3", progressionArbre(arbre, 3), arbre.isArbre3Debloque()));
        }

        Titre actif = ctx.gestionnaireTitres.getTitreActif();
        Label titreLabel = new Label(actif != null ? actif.toString() : "Aucun titre équipé");
        titreLabel.getStyleClass().add(actif != null ? "item-qte" : "item-vide");
        titreLabel.setWrapText(true);
        titreLabel.setMaxWidth(380);
        VBox carteTitre = new VBox(titreLabel);
        carteTitre.setAlignment(Pos.CENTER);
        titreBox.getChildren().setAll(carteTitre);
    }

    private Node ligneArbre(String nom, int progres, boolean debloque) {
        Label l = new Label(nom);
        l.getStyleClass().add("item-detail");
        l.setMinWidth(70);

        HBox ligne = new HBox(10, l, GuiVisuels.creerBarreProgression(200, 14, debloque ? progres : 0, 10));
        ligne.setAlignment(Pos.CENTER_LEFT);
        if (!debloque) ligne.setOpacity(0.4);
        return ligne;
    }

    private int progressionArbre(ArbreCompetences arbre, int numArbre) {
        int count = 0;
        for (int i = 1; i <= 10; i++) {
            boolean debloque = switch (numArbre) {
                case 1 -> arbre.getNoeud(i).isDebloque();
                case 2 -> arbre.getNoeudArbre2(i).isDebloque();
                default -> arbre.getNoeudArbre3(i).isDebloque();
            };
            if (debloque) count++;
        }
        return count;
    }

    private void onMonteeRang() {
        Personnage_principale joueur = ctx.joueur;
        String resultat = ctx.rangJoueur.tenterMonteeRang(joueur.getNiveau(), joueur.getArbreCompetences());

        if (resultat.equals("OK")) {
            info("Rang", "Felicitations ! Vous etes maintenant rang " + ctx.rangJoueur.getRangNom() + " !\n"
                    + "Multiplicateur de stats : x" + String.format("%.2f", ctx.rangJoueur.getMultiplicateur()));
        } else {
            info("Rang", resultat);
        }
        rafraichir();
    }

    private void onGererTitres() {
        GestionnaireTitres gestionnaireTitres = ctx.gestionnaireTitres;
        List<Titre> titres = gestionnaireTitres.getTitresObtenus();

        if (titres.isEmpty()) { info("Titres", "Aucun titre obtenu pour l'instant."); return; }

        List<ChoixTitre> options = new ArrayList<>();
        options.add(new ChoixTitre(null));
        for (Titre t : titres) options.add(new ChoixTitre(t));

        ChoixTitre choix = GuiVisuels.choisirParmiCartes("Mes titres", options, this::carteChoixTitre);
        if (choix == null) return;

        if (choix.titre() == null) {
            gestionnaireTitres.desequiperTitre();
        } else {
            gestionnaireTitres.equiperTitre(choix.titre().getNom());
        }
        rafraichir();
    }

    /** Enveloppe une option du picker de titre ; titre() == null represente "(Desequiper)". */
    private record ChoixTitre(Titre titre) {}

    private Node carteChoixTitre(ChoixTitre choix) {
        if (choix.titre() == null) {
            Label nom = new Label("(Aucun — déséquiper le titre actif)");
            nom.getStyleClass().add("item-nom");
            HBox carte = new HBox(nom);
            carte.setAlignment(Pos.CENTER_LEFT);
            carte.getStyleClass().add("carte-item");
            carte.setPrefWidth(320);
            return carte;
        }

        Titre t = choix.titre();
        Label nom = new Label(t.getNom());
        nom.getStyleClass().add("item-nom");
        Label detail = new Label(t.getDescription() + "  (+" + (int) (t.getBonusStatsPourcentage() * 100) + "% stats équipe)");
        detail.getStyleClass().add("item-detail");
        detail.setWrapText(true);
        detail.setMaxWidth(280);

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add(t.isEquipe() ? "carte-item-joueur" : "carte-item");
        carte.setPrefWidth(320);
        return carte;
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
