package lancement.gui;

import Equipement.Equipement;
import Equipement.Inventaire;
import Equipement.Pierre;
import Personnage.PersonnageBase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

public class EcranPierresController {

    private GameContext ctx;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
    }

    @FXML
    private void onVoirStock(ActionEvent event) {
        List<Inventaire.StackPierre> stock = ctx.inventaire.getPierres();
        if (stock.isEmpty()) { info("Pierres", "Aucune pierre en stock."); return; }

        GuiVisuels.choisirParmiCartes("Stock de pierres", stock, this::cartePierreDispo);
    }

    @FXML
    private void onSynthetiser(ActionEvent event) {
        List<Inventaire.StackPierre> eligibles = new ArrayList<>();
        for (Inventaire.StackPierre s : ctx.inventaire.getPierres()) {
            if (s.getQuantite() >= 2 && s.getNiveau() < Pierre.NIVEAU_MAX) eligibles.add(s);
        }
        if (eligibles.isEmpty()) {
            info("Synthese", "Aucune pierre synthetisable (il faut 2 pierres identiques, niveau < " + Pierre.NIVEAU_MAX + ").");
            return;
        }

        Inventaire.StackPierre choisi = GuiVisuels.choisirParmiCartes(
                "Synthétiser des pierres", eligibles, this::cartePierreSynthese);
        if (choisi == null) return;

        info("Synthese", ctx.inventaire.synthetiserPierre(choisi.getType(), choisi.getNiveau()));
    }

    private Node cartePierreSynthese(Inventaire.StackPierre s) {
        Label nom = new Label(s.toString());
        nom.getStyleClass().add("item-nom");
        Label detail = new Label("→ " + new Pierre(s.getType(), s.getNiveau() + 1));
        detail.getStyleClass().add("item-detail");

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(260);
        return carte;
    }

    @FXML
    private void onGererEquipe(ActionEvent event) {
        PersonnageBase cible = choisirPersonnage();
        if (cible == null) return;

        List<Equipement> portes = cible.getEquipementsPortes();
        if (portes.isEmpty()) { info("Pierres", cible.getNom() + " ne porte aucun equipement."); return; }

        Equipement choisi = choisirEquipement(portes);
        if (choisi == null) return;
        gererPierresEquipement(choisi);
    }

    @FXML
    private void onGererInventaire(ActionEvent event) {
        List<Equipement> equips = ctx.inventaire.getEquipements();
        if (equips.isEmpty()) { info("Pierres", "Aucun equipement dans l'inventaire."); return; }

        Equipement choisi = choisirEquipement(equips);
        if (choisi == null) return;
        gererPierresEquipement(choisi);
    }

    /** Boucle de gestion des 5 emplacements de pierres d'une piece, via une suite de boites de dialogue. */
    private static final int EMPLACEMENT_TERMINER = -1;

    private void gererPierresEquipement(Equipement equip) {
        boolean continuer = true;
        while (continuer) {
            List<Integer> emplacements = new ArrayList<>();
            for (int i = 0; i < Equipement.NB_EMPLACEMENTS_PIERRES; i++) emplacements.add(i);
            emplacements.add(EMPLACEMENT_TERMINER);

            Integer choix = GuiVisuels.choisirParmiCartes("Pierres - " + equip.getNomAffiche(), emplacements,
                    i -> carteEmplacementPierre(equip, i));
            if (choix == null || choix == EMPLACEMENT_TERMINER) { continuer = false; continue; }

            gererEmplacement(equip, choix);
        }
    }

    private Node carteEmplacementPierre(Equipement equip, int index) {
        if (index == EMPLACEMENT_TERMINER) {
            Label nom = new Label("Terminer");
            nom.getStyleClass().add("item-nom");
            HBox carte = new HBox(nom);
            carte.setAlignment(Pos.CENTER_LEFT);
            carte.getStyleClass().add("carte-item");
            carte.setPrefWidth(240);
            return carte;
        }

        Pierre p = equip.getPierre(index);
        Label nom = new Label("Emplacement " + (index + 1));
        nom.getStyleClass().add("item-nom");
        Label detail = new Label(p != null ? p.toString() : "[vide]");
        detail.getStyleClass().add("item-detail");

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(240);
        return carte;
    }

    private void gererEmplacement(Equipement equip, int index) {
        Pierre actuelle = equip.getPierre(index);
        if (actuelle != null) {
            boolean confirme = confirmer("Cet emplacement contient : " + actuelle + "\nRetirer cette pierre ?");
            if (!confirme) return;
            equip.retirerPierre(index);
            ctx.inventaire.ajouterPierre(actuelle.getType(), actuelle.getNiveau(), 1);
            info("Pierres", "Pierre retiree et remise en stock.");
            return;
        }

        List<Inventaire.StackPierre> dispo = ctx.inventaire.getPierres();
        if (dispo.isEmpty()) { info("Pierres", "Aucune pierre en stock."); return; }

        Inventaire.StackPierre choisi = GuiVisuels.choisirParmiCartes("Insérer une pierre", dispo, this::cartePierreDispo);
        if (choisi == null) return;

        Pierre nouvelle = new Pierre(choisi.getType(), choisi.getNiveau());
        String message = equip.insererPierre(index, nouvelle);
        if (message.equals("OK")) {
            ctx.inventaire.retirerPierre(choisi.getType(), choisi.getNiveau(), 1);
            info("Pierres", "Pierre inseree : " + nouvelle);
        } else {
            info("Pierres", message);
        }
    }

    private Node cartePierreDispo(Inventaire.StackPierre s) {
        Label nom = new Label(s.toString());
        nom.getStyleClass().add("item-nom");

        HBox carte = new HBox(nom);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(240);
        return carte;
    }

    private PersonnageBase choisirPersonnage() {
        List<PersonnageBase> tous = new ArrayList<>();
        tous.add(ctx.joueur);
        tous.addAll(ctx.personnagesRecruites);
        return GuiVisuels.choisirParmiCartes("Choisir un personnage", tous, this::cartePersonnageChoix);
    }

    private Node cartePersonnageChoix(PersonnageBase p) {
        Label badge = GuiVisuels.creerBadgeRarete(p.getRarete());
        Label nom = new Label(p.getNom());
        nom.getStyleClass().add("item-nom");
        Label detail = new Label("Niv. " + p.getNiveau() + "  ·  " + p.getRole());
        detail.getStyleClass().add("item-detail");

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(10, badge, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(240);
        return carte;
    }

    private Equipement choisirEquipement(List<Equipement> liste) {
        return GuiVisuels.choisirParmiCartes("Choisir un équipement", liste, this::carteEquipementChoix);
    }

    private Node carteEquipementChoix(Equipement e) {
        int rempli = 0;
        for (Pierre p : e.getPierres()) if (p != null) rempli++;

        Label badge = GuiVisuels.creerBadgeRarete(e.getRarete().name());
        Label nom = new Label(e.getNomAffiche());
        nom.getStyleClass().add("item-nom");
        Label detail = new Label("Pierres : " + rempli + "/" + Equipement.NB_EMPLACEMENTS_PIERRES);
        detail.getStyleClass().add("item-detail");

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(10, badge, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(260);
        return carte;
    }

    private boolean confirmer(String question) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, question, ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        styliser(confirm);
        Optional<ButtonType> resultat = confirm.showAndWait();
        return resultat.isPresent() && resultat.get() == ButtonType.YES;
    }

    @FXML
    private void onRetour(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranAmeliorations.fxml");
            EcranAmeliorationsController controller = loader.getController();
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
