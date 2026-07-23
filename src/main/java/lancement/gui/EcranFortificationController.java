package lancement.gui;

import Equipement.Equipement;
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
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Menus.MenuAmeliorations;

public class EcranFortificationController {

    private final MenuAmeliorations menuAmeliorations = new MenuAmeliorations();

    private GameContext ctx;

    @FXML private VBox infoBox;
    @FXML private VBox choixBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichir();
    }

    private void rafraichir() {
        infoBox.getChildren().setAll(
                GuiVisuels.creerFicheStat("Or disponible", String.format("%.0f", ctx.joueur.getOr())));
        choixBox.getChildren().setAll(
                GuiVisuels.creerCarteChoix("Fortifier un équipement équipé",
                        "Choisir parmi les pièces portées par vos personnages.", e -> onFortifierEquipe()),
                GuiVisuels.creerCarteChoix("Fortifier un équipement de l'inventaire",
                        "Choisir parmi les pièces non équipées.", e -> onFortifierInventaire())
        );
    }

    private void onFortifierEquipe() {
        PersonnageBase cible = choisirPersonnage();
        if (cible == null) return;

        List<Equipement> portes = cible.getEquipementsPortes();
        if (portes.isEmpty()) { info("Fortification", cible.getNom() + " ne porte aucun equipement."); return; }

        Equipement choisi = choisirEquipement(portes);
        if (choisi == null) return;
        fortifier(choisi);
    }

    private void onFortifierInventaire() {
        List<Equipement> equips = ctx.inventaire.getEquipements();
        if (equips.isEmpty()) { info("Fortification", "Aucun equipement dans l'inventaire."); return; }

        Equipement choisi = choisirEquipement(equips);
        if (choisi == null) return;
        fortifier(choisi);
    }

    private void fortifier(Equipement equip) {
        int niveauActuel = equip.getNiveauFortification();
        int fortMax = ctx.joueur.getNiveau();
        if (niveauActuel >= fortMax) { info("Fortification", "Cet equipement est deja a la fortification maximale !"); return; }

        TextInputDialog dialog = new TextInputDialog(String.valueOf(Math.min(niveauActuel + 1, fortMax)));
        dialog.setTitle("Fortification");
        dialog.setHeaderText(null);
        dialog.setContentText(equip.getNomAffiche() + "\nNiveau actuel : Fort." + niveauActuel
                + "  (max : Fort." + fortMax + ")\nNiveau cible :");
        styliser(dialog);
        Optional<String> reponse = dialog.showAndWait();
        if (reponse.isEmpty()) return;

        int niveauCible;
        try {
            niveauCible = Integer.parseInt(reponse.get().trim());
        } catch (NumberFormatException e) {
            info("Fortification", "Entree invalide.");
            return;
        }

        int coutTotal = menuAmeliorations.calculerCoutTotalFortification(niveauActuel, niveauCible);
        boolean confirme = confirmer("Fortifier " + equip.getNomAffiche() + " de Fort." + niveauActuel
                + " a Fort." + niveauCible + " pour " + coutTotal + " or ?");
        if (!confirme) return;

        info("Fortification", menuAmeliorations.appliquerFortification(ctx.joueur, equip, niveauCible));
        rafraichir();
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
        Label badge = GuiVisuels.creerBadgeRarete(e.getRarete().name());
        Label nom = new Label(e.getNomAffiche());
        nom.getStyleClass().add("item-nom");
        Label detail = new Label("Fort." + e.getNiveauFortification());
        detail.getStyleClass().add("item-detail");

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(10, badge, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(240);
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
