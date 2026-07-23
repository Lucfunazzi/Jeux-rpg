package lancement.gui;

import java.io.IOException;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireCompagnons;
import lancement.Gestionnaires.Gestionnaire_pet;

public class EcranMenuPrincipalController {

    private GameContext ctx;

    @FXML private Label enTeteLabel;
    @FXML private Label ressourcesLabel;
    @FXML private Label energieLabel;
    @FXML private VBox xpBarBox;
    @FXML private VBox boutonsBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;

        enTeteLabel.setText("Joueur : " + ctx.joueur.getNom()
                + "  |  Niv." + ctx.joueur.getNiveau()
                + "  |  Rang : " + ctx.rangJoueur.getRangNom());

        ressourcesLabel.setText("Or : " + String.format("%.0f", ctx.joueur.getOr())
                + "  |  Coupons : " + ctx.joueur.getCoupons()
                + "  |  Combativite : " + ctx.formation.getCombativite());

        energieLabel.setText(ctx.gestionnaireEnergie.afficherEnergie());

        xpBarBox.getChildren().setAll(
                GuiVisuels.creerBarreXP(260, 8, ctx.joueur.getExperience(), ctx.joueur.getExperienceMax()));

        boolean chapitre2Fini      = ctx.chapitre2.getStagesReussis()[10];
        boolean chapitre1EliteFini = ctx.chapitre1Elite.getStagesReussis()[10];
        int     niveau             = ctx.joueur.getNiveau();

        ajouterBouton("Histoire", this::onHistoire);
        ajouterBouton("Formation", this::onFormation);
        if (niveau >= 6)                                            ajouterBouton("Recrutement", this::onRecrutement);
        ajouterBouton("Inventaire", this::onInventaire);
        ajouterBouton("Personnages", this::onPersonnages);
        ajouterBouton("Quetes", this::onQuetes);
        if (chapitre2Fini)                                          ajouterBouton("Recrutement Rare", this::onRecrutementRare);
        if (niveau >= 3)                                            ajouterBouton("Abilites", this::onAbilites);
        if (chapitre1EliteFini)                                     ajouterBouton("Rang & Titres", this::onRangTitres);
        if (niveau >= 10)                                           ajouterBouton("Ameliorations", this::onAmeliorations);
        if (niveau >= 10)                                           ajouterBouton("Donjon de ressources", this::onDonjon);
        if (niveau >= 20)                                           ajouterBouton("Arene", this::onArene);
        if (niveau >= GestionnaireCompagnons.NIVEAU_DEBLOCAGE)      ajouterBouton("Compagnons", this::onCompagnons);
        if (niveau >= Gestionnaire_pet.NIVEAU_DEBLOCAGE)            ajouterBouton("Creatures Sacrees", this::onCreaturesSacrees);
        if (niveau >= 6)                                            ajouterBouton("Etoiles & Fragments", this::onEtoiles);
        ajouterBouton("Tirages", this::onTirages);
    }

    private void ajouterBouton(String libelle) {
        ajouterBouton(libelle, e -> System.out.println(libelle + " (ecran a venir)"));
    }

    private void ajouterBouton(String libelle, EventHandler<ActionEvent> action) {
        Button bouton = new Button(libelle);
        bouton.getStyleClass().add("menu-bouton");
        bouton.setOnAction(action);
        boutonsBox.getChildren().add(bouton);
    }

    private void onHistoire(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranHistoire.fxml", c -> ((EcranHistoireController) c).initData(ctx));
    }

    private void onRecrutement(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranRecrutement.fxml", c -> ((EcranRecrutementController) c).initData(ctx));
    }

    private void onInventaire(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranInventaire.fxml", c -> ((EcranInventaireController) c).initData(ctx));
    }

    private void onPersonnages(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranPersonnages.fxml", c -> ((EcranPersonnagesController) c).initData(ctx));
    }

    private void onFormation(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranFormation.fxml", c -> ((EcranFormationController) c).initData(ctx));
    }

    private void onQuetes(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranQuetes.fxml", c -> ((EcranQuetesController) c).initData(ctx));
    }

    private void onRangTitres(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranRangTitres.fxml", c -> ((EcranRangTitresController) c).initData(ctx));
    }

    private void onAmeliorations(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranAmeliorations.fxml", c -> ((EcranAmeliorationsController) c).initData(ctx));
    }

    private void onArene(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranArene.fxml", c -> ((EcranAreneController) c).initData(ctx));
    }

    private void onDonjon(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranDonjon.fxml", c -> ((EcranDonjonController) c).initData(ctx));
    }

    private void onAbilites(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranAbilites.fxml", c -> ((EcranAbilitesController) c).initData(ctx));
    }

    private void onRecrutementRare(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranRecrutementRare.fxml", c -> ((EcranRecrutementRareController) c).initData(ctx));
    }

    private void onTirages(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranTirages.fxml", c -> ((EcranTiragesController) c).initData(ctx));
    }

    private void onCompagnons(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranCompagnons.fxml", c -> ((EcranCompagnonsController) c).initData(ctx));
    }

    private void onCreaturesSacrees(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranCreaturesSacrees.fxml", c -> ((EcranCreaturesSacreesController) c).initData(ctx));
    }

    private void onEtoiles(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranEtoiles.fxml", c -> ((EcranEtoilesController) c).initData(ctx));
    }

    private void naviguerVers(ActionEvent event, String fxml, Consumer<Object> initialiser) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = Navigation.changerEcran(stage, fxml);
            initialiser.accept(loader.getController());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onSauvegarder(ActionEvent event) {
        ctx.sauvegarde.sauvegarder(ctx);
        System.out.println("Partie sauvegardee.");
    }

    @FXML
    private void onQuitter(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
