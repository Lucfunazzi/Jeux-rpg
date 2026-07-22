package lancement.gui;

import Equipement.Equipement;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Menus.MenuAmeliorations;

public class EcranAffinageController {

    private final MenuAmeliorations menuAmeliorations = new MenuAmeliorations();

    private GameContext ctx;

    @FXML private Label infoLabel;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichir();
    }

    private void rafraichir() {
        int pierres = ctx.inventaire.getQuantiteMateriau(MenuAmeliorations.MATERIAU_AFFINAGE);
        infoLabel.setText("Pierres d'affinage disponibles : " + pierres);
    }

    @FXML
    private void onAffinerEquipe(ActionEvent event) {
        PersonnageBase cible = choisirPersonnage();
        if (cible == null) return;

        List<Equipement> portes = cible.getEquipementsPortes();
        if (portes.isEmpty()) { info("Affinage", cible.getNom() + " ne porte aucun equipement."); return; }

        Equipement choisi = choisirEquipement(portes);
        if (choisi == null) return;
        affiner(choisi);
    }

    @FXML
    private void onAffinerInventaire(ActionEvent event) {
        List<Equipement> equips = ctx.inventaire.getEquipements();
        if (equips.isEmpty()) { info("Affinage", "Aucun equipement dans l'inventaire."); return; }

        Equipement choisi = choisirEquipement(equips);
        if (choisi == null) return;
        affiner(choisi);
    }

    private void affiner(Equipement equip) {
        int niveauActuel = equip.getNiveauAffinage();
        if (niveauActuel >= MenuAmeliorations.NIVEAU_MAX_AFFINAGE) {
            info("Affinage", equip.getNom() + " a atteint le niveau d'affinage maximum !");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(String.valueOf(niveauActuel + 1));
        dialog.setTitle("Affinage");
        dialog.setHeaderText(null);
        dialog.setContentText(equip.getNomAffiche() + "\nNiveau actuel : Aff." + niveauActuel
                + "  (max : " + MenuAmeliorations.NIVEAU_MAX_AFFINAGE + ")\nNiveau cible :");
        styliser(dialog);
        Optional<String> reponse = dialog.showAndWait();
        if (reponse.isEmpty()) return;

        int niveauCible;
        try {
            niveauCible = Integer.parseInt(reponse.get().trim());
        } catch (NumberFormatException e) {
            info("Affinage", "Entree invalide.");
            return;
        }

        int coutTotal = menuAmeliorations.calculerCoutTotalAffinage(niveauActuel, niveauCible);
        boolean confirme = confirmer("Affiner " + equip.getNomAffiche() + " de Aff." + niveauActuel
                + " a Aff." + niveauCible + " pour " + coutTotal + " pierres d'affinage ?");
        if (!confirme) return;

        info("Affinage", menuAmeliorations.appliquerAffinage(equip, ctx.inventaire, niveauCible));
        rafraichir();
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
            String libelle = e.toString() + "  Aff." + e.getNiveauAffinage();
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
