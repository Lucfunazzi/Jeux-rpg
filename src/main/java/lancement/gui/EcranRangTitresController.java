package lancement.gui;

import Joueur.ArbreCompetences;
import Joueur.Personnage_principale;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireTitres;
import lancement.RangJoueur;
import lancement.Titre;

public class EcranRangTitresController {

    private GameContext ctx;

    @FXML private Label rangLabel;
    @FXML private Label arbresLabel;
    @FXML private Label titreActifLabel;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichir();
    }

    private void rafraichir() {
        RangJoueur rangJoueur = ctx.rangJoueur;
        rangLabel.setText(rangJoueur.afficherRang());

        if (rangJoueur.getRang() == RangJoueur.Rang.C) {
            ArbreCompetences arbre = ctx.joueur.getArbreCompetences();
            arbresLabel.setText(
                    "Arbre 1 : " + (arbre.isNoeud10Debloque() ? "[COMPLETE]" : "[" + progressionArbre(arbre, 1) + "/10 noeuds]") + "\n"
                  + "Arbre 2 : " + (arbre.isNoeud10Arbre2Debloque() ? "[COMPLETE]" : "[" + progressionArbre(arbre, 2) + "/10 noeuds]") + "\n"
                  + "Arbre 3 : " + (!arbre.isArbre3Debloque() ? "[VERROUILLE]"
                          : arbre.isNoeud10Arbre3Debloque() ? "[COMPLETE]" : "[" + progressionArbre(arbre, 3) + "/10 noeuds]"));
        } else {
            arbresLabel.setText("");
        }

        Titre actif = ctx.gestionnaireTitres.getTitreActif();
        titreActifLabel.setText("Titre equipe : " + (actif != null ? actif.toString() : "aucun"));
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

    @FXML
    private void onMonteeRang(ActionEvent event) {
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

    @FXML
    private void onGererTitres(ActionEvent event) {
        GestionnaireTitres gestionnaireTitres = ctx.gestionnaireTitres;
        List<Titre> titres = gestionnaireTitres.getTitresObtenus();

        if (titres.isEmpty()) { info("Titres", "Aucun titre obtenu pour l'instant."); return; }

        String optionDesequiper = "(Aucun - desequiper le titre actif)";
        Map<String, Titre> map = new LinkedHashMap<>();
        List<String> options = new ArrayList<>();
        options.add(optionDesequiper);
        for (Titre t : titres) {
            String libelle = t.toString();
            options.add(libelle);
            map.put(libelle, t);
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle("Mes titres");
        dialog.setHeaderText(null);
        dialog.setContentText("Titre a equiper :");
        styliser(dialog);
        Optional<String> resultat = dialog.showAndWait();
        if (resultat.isEmpty()) return;

        if (resultat.get().equals(optionDesequiper)) {
            gestionnaireTitres.desequiperTitre();
        } else {
            Titre choisi = map.get(resultat.get());
            gestionnaireTitres.equiperTitre(choisi.getNom());
        }
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
