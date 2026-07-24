package lancement.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireCompagnons;
import lancement.Gestionnaires.GestionnaireExamenS;
import lancement.Gestionnaires.Gestionnaire_pet;

public class EcranMenuPrincipalController {

    private GameContext ctx;

    @FXML private FlowPane statsBox;
    @FXML private VBox xpBarBox;
    @FXML private VBox boutonsBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;

        statsBox.getChildren().setAll(
                GuiVisuels.creerFicheStat(ctx.joueur.getNom(), "Niv. " + ctx.joueur.getNiveau()),
                GuiVisuels.creerFicheStat("Rang", ctx.rangJoueur.getRangNom()),
                GuiVisuels.creerFicheStat("Or", String.format("%.0f", ctx.joueur.getOr())),
                GuiVisuels.creerFicheStat("Coupons", String.valueOf(ctx.joueur.getCoupons())),
                GuiVisuels.creerFicheStat("Combativité", String.valueOf(ctx.formation.getCombativite())),
                GuiVisuels.creerFicheStat("Énergie", ctx.gestionnaireEnergie.afficherEnergie())
        );

        xpBarBox.getChildren().setAll(
                GuiVisuels.creerBarreXP(260, 8, ctx.joueur.getExperience(), ctx.joueur.getExperienceMax()));

        boolean chapitre2Fini      = ctx.chapitre2.getStagesReussis()[10];
        boolean chapitre1EliteFini = ctx.chapitre1Elite.getStagesReussis()[10];
        int     niveau             = ctx.joueur.getNiveau();

        List<BoutonDef> progression = new ArrayList<>();
        progression.add(new BoutonDef("Histoire", this::onHistoire));
        progression.add(new BoutonDef("Quetes", this::onQuetes));
        progression.add(new BoutonDef("Recompenses", this::onRecompenses));
        if (niveau >= 3)                                       progression.add(new BoutonDef("Abilites", this::onAbilites));
        if (chapitre1EliteFini)                                progression.add(new BoutonDef("Rang & Titres", this::onRangTitres));

        List<BoutonDef> personnages = new ArrayList<>();
        personnages.add(new BoutonDef("Formation", this::onFormation));
        personnages.add(new BoutonDef("Personnages", this::onPersonnages));
        personnages.add(new BoutonDef("Inventaire", this::onInventaire));
        if (niveau >= 10)                                       personnages.add(new BoutonDef("Ameliorations", this::onAmeliorations));
        if (niveau >= GestionnaireCompagnons.NIVEAU_DEBLOCAGE)  personnages.add(new BoutonDef("Compagnons", this::onCompagnons));
        if (niveau >= Gestionnaire_pet.NIVEAU_DEBLOCAGE)        personnages.add(new BoutonDef("Creatures Sacrees", this::onCreaturesSacrees));

        List<BoutonDef> recrutementGacha = new ArrayList<>();
        if (niveau >= 6)                                       recrutementGacha.add(new BoutonDef("Recrutement", this::onRecrutement));
        if (chapitre2Fini)                                     recrutementGacha.add(new BoutonDef("Recrutement Rare", this::onRecrutementRare));
        recrutementGacha.add(new BoutonDef("Tirages", this::onTirages));
        if (niveau >= 6)                                       recrutementGacha.add(new BoutonDef("Etoiles & Fragments", this::onEtoiles));

        List<BoutonDef> modes = new ArrayList<>();
        if (niveau >= 10)                                       modes.add(new BoutonDef("Donjon de ressources", this::onDonjon));
        if (niveau >= 20)                                       modes.add(new BoutonDef("Arene", this::onArene));
        if (niveau >= GestionnaireExamenS.NIVEAU_REQUIS)        modes.add(new BoutonDef("Examen de Rang S", this::onExamenS));

        boutonsBox.getChildren().clear();
        ajouterSection("Progression", progression);
        ajouterSection("Personnages & Équipement", personnages);
        ajouterSection("Recrutement & Gacha", recrutementGacha);
        ajouterSection("Modes & Défis", modes);
    }

    private record BoutonDef(String libelle, EventHandler<ActionEvent> action) {}

    private void ajouterSection(String titre, List<BoutonDef> boutons) {
        if (boutons.isEmpty()) return;

        Label titreLabel = new Label(titre);
        titreLabel.getStyleClass().add("section-titre");
        boutonsBox.getChildren().add(titreLabel);

        FlowPane grille = new FlowPane(10, 10);
        grille.setAlignment(Pos.CENTER);
        grille.setPrefWrapLength(460);
        for (BoutonDef b : boutons) {
            Button bouton = new Button(b.libelle());
            bouton.getStyleClass().add("menu-bouton");
            bouton.setOnAction(b.action());
            grille.getChildren().add(bouton);
        }
        boutonsBox.getChildren().add(grille);
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

    private void onRecompenses(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranRecompenses.fxml", c -> ((EcranRecompensesController) c).initData(ctx));
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

    private void onExamenS(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranExamenS.fxml", c -> ((EcranExamenSController) c).initData(ctx));
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
        ButtonType sauvegarderEtQuitter = new ButtonType("Sauvegarder et quitter");
        ButtonType quitterSansSauver    = new ButtonType("Quitter sans sauvegarder");
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Voulez-vous sauvegarder votre partie avant de quitter ?",
                sauvegarderEtQuitter, quitterSansSauver, ButtonType.CANCEL);
        confirm.setTitle("Quitter");
        confirm.setHeaderText(null);
        styliser(confirm);

        ButtonType choix = confirm.showAndWait().orElse(ButtonType.CANCEL);
        if (choix == ButtonType.CANCEL) return;
        if (choix == sauvegarderEtQuitter) {
            ctx.sauvegarde.sauvegarder(ctx);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void styliser(Dialog<?> dialog) {
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("root-menu");
    }
}
