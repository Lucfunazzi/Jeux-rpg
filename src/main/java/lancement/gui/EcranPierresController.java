package lancement.gui;

import Equipement.Equipement;
import Equipement.Inventaire;
import Equipement.Pierre;
import Personnage.PersonnageBase;
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

        StringBuilder sb = new StringBuilder();
        for (Inventaire.StackPierre s : stock) sb.append(s).append("\n");
        info("Stock de pierres", sb.toString().trim());
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

        Map<String, Inventaire.StackPierre> map = new LinkedHashMap<>();
        List<String> options = new ArrayList<>();
        for (Inventaire.StackPierre s : eligibles) {
            String libelle = s + "  ->  " + new Pierre(s.getType(), s.getNiveau() + 1);
            options.add(libelle);
            map.put(libelle, s);
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle("Synthetiser des pierres");
        dialog.setHeaderText(null);
        dialog.setContentText("Choisissez une synthese :");
        styliser(dialog);
        Optional<String> resultat = dialog.showAndWait();
        if (resultat.isEmpty()) return;

        Inventaire.StackPierre choisi = map.get(resultat.get());
        info("Synthese", ctx.inventaire.synthetiserPierre(choisi.getType(), choisi.getNiveau()));
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
    private void gererPierresEquipement(Equipement equip) {
        boolean continuer = true;
        while (continuer) {
            List<String> emplacements = new ArrayList<>();
            for (int i = 0; i < Equipement.NB_EMPLACEMENTS_PIERRES; i++) {
                Pierre p = equip.getPierre(i);
                emplacements.add("Emplacement " + (i + 1) + " : " + (p != null ? p.toString() : "[vide]"));
            }
            emplacements.add("Terminer");

            ChoiceDialog<String> dialog = new ChoiceDialog<>(emplacements.get(0), emplacements);
            dialog.setTitle("Pierres - " + equip.getNomAffiche());
            dialog.setHeaderText(null);
            dialog.setContentText("Choisissez un emplacement :");
            styliser(dialog);
            Optional<String> resultat = dialog.showAndWait();
            if (resultat.isEmpty() || resultat.get().equals("Terminer")) { continuer = false; continue; }

            int index = emplacements.indexOf(resultat.get());
            gererEmplacement(equip, index);
        }
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

        Map<String, Inventaire.StackPierre> map = new LinkedHashMap<>();
        List<String> options = new ArrayList<>();
        for (Inventaire.StackPierre s : dispo) {
            options.add(s.toString());
            map.put(s.toString(), s);
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle("Inserer une pierre");
        dialog.setHeaderText(null);
        dialog.setContentText("Pierre a inserer :");
        styliser(dialog);
        Optional<String> resultat = dialog.showAndWait();
        if (resultat.isEmpty()) return;

        Inventaire.StackPierre choisi = map.get(resultat.get());
        Pierre nouvelle = new Pierre(choisi.getType(), choisi.getNiveau());
        String message = equip.insererPierre(index, nouvelle);
        if (message.equals("OK")) {
            ctx.inventaire.retirerPierre(choisi.getType(), choisi.getNiveau(), 1);
            info("Pierres", "Pierre inseree : " + nouvelle);
        } else {
            info("Pierres", message);
        }
    }

    private PersonnageBase choisirPersonnage() {
        List<PersonnageBase> tous = new ArrayList<>();
        tous.add(ctx.joueur);
        tous.addAll(ctx.personnagesRecruites);

        Map<String, PersonnageBase> map = new LinkedHashMap<>();
        List<String> options = new ArrayList<>();
        for (PersonnageBase p : tous) {
            String libelle = p.getNom() + " [" + p.getRole() + "] Niv." + p.getNiveau();
            options.add(libelle);
            map.put(libelle, p);
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle("Choisir un personnage");
        dialog.setHeaderText(null);
        dialog.setContentText("Personnage :");
        styliser(dialog);
        Optional<String> resultat = dialog.showAndWait();
        return resultat.map(map::get).orElse(null);
    }

    private Equipement choisirEquipement(List<Equipement> liste) {
        Map<String, Equipement> map = new LinkedHashMap<>();
        List<String> options = new ArrayList<>();
        for (Equipement e : liste) {
            int rempli = 0;
            for (Pierre p : e.getPierres()) if (p != null) rempli++;
            String libelle = e.toString() + "  |  Pierres : " + rempli + "/" + Equipement.NB_EMPLACEMENTS_PIERRES;
            options.add(libelle);
            map.put(libelle, e);
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle("Choisir un equipement");
        dialog.setHeaderText(null);
        dialog.setContentText("Equipement :");
        styliser(dialog);
        Optional<String> resultat = dialog.showAndWait();
        return resultat.map(map::get).orElse(null);
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
