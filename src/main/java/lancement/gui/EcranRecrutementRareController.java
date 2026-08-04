package lancement.gui;

import Personnage.PersonnageBase;
import java.io.IOException;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Gestionnaires.SauvegardeJoueur;
import lancement.Menus.MenuRecrutementRare;

public class EcranRecrutementRareController {

    private final MenuRecrutementRare menuRecrutementRare = new MenuRecrutementRare();

    private GameContext ctx;

    @FXML private VBox boutonsBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        GuiVisuels.afficherExplicationPremiereVisite(ctx, "Recrutement Rare", "Recrutement Rare");
        rafraichir();
    }

    private void rafraichir() {
        boutonsBox.getChildren().clear();

        int possedeA  = ctx.inventaire.getQuantiteMateriau(MenuRecrutementRare.PARCHEMIN_A);
        int possedeS  = ctx.inventaire.getQuantiteMateriau(MenuRecrutementRare.PARCHEMIN_S);
        int possedeSS = ctx.inventaire.getQuantiteMateriau(MenuRecrutementRare.PARCHEMIN_SS);

        Label solde = new Label("Parchemins : " + possedeA + "x A  |  " + possedeS + "x S  |  " + possedeSS + "x SS");
        solde.getStyleClass().add("item-detail");
        boutonsBox.getChildren().add(solde);

        int niveauJoueur = ctx.joueur.getNiveau();

        boolean natsuA = MenuRecrutementRare.dejaRecruteParNom("Natsu", ctx.personnagesRecruites);
        boolean natsuS = MenuRecrutementRare.dejaRecruteParNom("Natsu Etherion", ctx.personnagesRecruites);
        boolean natsuGrayVerrouille = niveauJoueur < MenuRecrutementRare.NIVEAU_REQUIS_NATSU_GRAY;
        boolean evoNatsuVerrouille  = niveauJoueur < MenuRecrutementRare.NIVEAU_REQUIS_EVOLUTION_NATSU;

        boutonsBox.getChildren().add(titreSection("Natsu"));
        boutonsBox.getChildren().add(carteRecrutementRare("Natsu", "Natsu", "A", possedeA,
                MenuRecrutementRare.COUT_RECRUTEMENT_NATSU, natsuA || natsuS,
                natsuGrayVerrouille, MenuRecrutementRare.NIVEAU_REQUIS_NATSU_GRAY, this::confirmerEtRecruterNatsu));
        if (natsuA) {
            boutonsBox.getChildren().add(carteRecrutementRare("Natsu Etherion (évolution)", "Natsu Etherion", "S", possedeS,
                    MenuRecrutementRare.COUT_EVOLUTION_NATSU, false,
                    evoNatsuVerrouille, MenuRecrutementRare.NIVEAU_REQUIS_EVOLUTION_NATSU, this::confirmerEtEvoluerNatsu));
        }

        boolean grayRecru = MenuRecrutementRare.dejaRecruteParNom("Gray", ctx.personnagesRecruites);

        boutonsBox.getChildren().add(titreSection("Gray"));
        boutonsBox.getChildren().add(carteRecrutementRare("Gray", "Gray", "A", possedeA,
                MenuRecrutementRare.COUT_RECRUTEMENT_GRAY, grayRecru,
                natsuGrayVerrouille, MenuRecrutementRare.NIVEAU_REQUIS_NATSU_GRAY, this::confirmerEtRecruterGray));

        boolean lucyRecru = MenuRecrutementRare.dejaRecruteParNom("Lucy", ctx.personnagesRecruites);

        boutonsBox.getChildren().add(titreSection("Lucy"));
        boutonsBox.getChildren().add(carteRecrutementRare("Lucy", "Lucy", "A", possedeA,
                MenuRecrutementRare.COUT_RECRUTEMENT_LUCY, lucyRecru,
                natsuGrayVerrouille, MenuRecrutementRare.NIVEAU_REQUIS_NATSU_GRAY, this::confirmerEtRecruterLucy));

        boolean jubiaRecru = MenuRecrutementRare.dejaRecruteParNom("Jubia", ctx.personnagesRecruites);

        boutonsBox.getChildren().add(titreSection("Jubia"));
        boutonsBox.getChildren().add(carteRecrutementRare("Jubia", "Jubia", "A", possedeA,
                MenuRecrutementRare.COUT_RECRUTEMENT_JUBIA, jubiaRecru,
                natsuGrayVerrouille, MenuRecrutementRare.NIVEAU_REQUIS_NATSU_GRAY, this::confirmerEtRecruterJubia));

        boolean wendyRecru = MenuRecrutementRare.dejaRecruteParNom("Wendy", ctx.personnagesRecruites);

        boutonsBox.getChildren().add(titreSection("Wendy"));
        boutonsBox.getChildren().add(carteRecrutementRare("Wendy", "Wendy", "A", possedeA,
                MenuRecrutementRare.COUT_RECRUTEMENT_WENDY, wendyRecru,
                natsuGrayVerrouille, MenuRecrutementRare.NIVEAU_REQUIS_NATSU_GRAY, this::confirmerEtRecruterWendy));

        boolean miraS  = MenuRecrutementRare.dejaRecruteParNom("Mirajane", ctx.personnagesRecruites);
        boolean miraSS = MenuRecrutementRare.dejaRecruteParNom("Mirajane Halphas", ctx.personnagesRecruites);
        boolean miraJellalVerrouille   = niveauJoueur < MenuRecrutementRare.NIVEAU_REQUIS_MIRAJANE_JELLAL;
        boolean evoMiraJellalVerrouille = niveauJoueur < MenuRecrutementRare.NIVEAU_REQUIS_EVOLUTION_MIRAJANE_JELLAL;

        boutonsBox.getChildren().add(titreSection("Mirajane"));
        boutonsBox.getChildren().add(carteRecrutementRare("Mirajane", "Mirajane", "S", possedeS,
                MenuRecrutementRare.COUT_MIRAJANE_S, miraS || miraSS,
                miraJellalVerrouille, MenuRecrutementRare.NIVEAU_REQUIS_MIRAJANE_JELLAL, this::confirmerEtRecruterMirajane));
        if (miraS) {
            boutonsBox.getChildren().add(carteRecrutementRare("Mirajane Halphas (évolution)", "Mirajane Halphas", "SS", possedeSS,
                    MenuRecrutementRare.COUT_MIRAJANE_SS, false,
                    evoMiraJellalVerrouille, MenuRecrutementRare.NIVEAU_REQUIS_EVOLUTION_MIRAJANE_JELLAL, this::confirmerEtEvoluerMirajane));
        }

        boolean jellalRecru  = MenuRecrutementRare.dejaRecruteParNom("Jellal", ctx.personnagesRecruites);
        boolean jellalEvolue = MenuRecrutementRare.dejaRecruteParNom("Jellal Intermagie", ctx.personnagesRecruites);

        boutonsBox.getChildren().add(titreSection("Jellal"));
        boutonsBox.getChildren().add(carteRecrutementRare("Jellal", "Jellal", "S", possedeS,
                MenuRecrutementRare.COUT_JELLAL, jellalRecru || jellalEvolue,
                miraJellalVerrouille, MenuRecrutementRare.NIVEAU_REQUIS_MIRAJANE_JELLAL, this::confirmerEtRecruterJellal));
        if (jellalRecru) {
            boutonsBox.getChildren().add(carteRecrutementRare("Jellal Intermagie (évolution)", "Jellal Intermagie", "SS", possedeSS,
                    MenuRecrutementRare.COUT_JELLAL_SS, false,
                    evoMiraJellalVerrouille, MenuRecrutementRare.NIVEAU_REQUIS_EVOLUTION_MIRAJANE_JELLAL, this::confirmerEtEvoluerJellal));
        }

        boolean erzaRecru = MenuRecrutementRare.dejaRecruteParNom("Erza", ctx.personnagesRecruites);
        boolean erzaVerrouille = niveauJoueur < MenuRecrutementRare.NIVEAU_REQUIS_ERZA;

        boutonsBox.getChildren().add(titreSection("Erza"));
        boutonsBox.getChildren().add(carteRecrutementRare("Erza", "Erza", "S", possedeS,
                MenuRecrutementRare.COUT_ERZA, erzaRecru,
                erzaVerrouille, MenuRecrutementRare.NIVEAU_REQUIS_ERZA, this::confirmerEtRecruterErza));

        boolean luxusRecru = MenuRecrutementRare.dejaRecruteParNom("Luxus", ctx.personnagesRecruites);
        boolean luxusVerrouille = niveauJoueur < MenuRecrutementRare.NIVEAU_REQUIS_LUXUS;

        boutonsBox.getChildren().add(titreSection("Luxus"));
        boutonsBox.getChildren().add(carteRecrutementRare("Luxus", "Luxus", "SS", possedeSS,
                MenuRecrutementRare.COUT_LUXUS, luxusRecru,
                luxusVerrouille, MenuRecrutementRare.NIVEAU_REQUIS_LUXUS, this::confirmerEtRecruterLuxus));

        boolean brainRecru = MenuRecrutementRare.dejaRecruteParNom("Brain", ctx.personnagesRecruites);
        boolean brainVerrouille = niveauJoueur < MenuRecrutementRare.NIVEAU_REQUIS_BRAIN;

        boutonsBox.getChildren().add(titreSection("Brain"));
        boutonsBox.getChildren().add(carteRecrutementRare("Brain", "Brain", "S", possedeS,
                MenuRecrutementRare.COUT_BRAIN, brainRecru,
                brainVerrouille, MenuRecrutementRare.NIVEAU_REQUIS_BRAIN, this::confirmerEtRecruterBrain));

        boolean hadesRecru = MenuRecrutementRare.dejaRecruteParNom("Hades", ctx.personnagesRecruites);
        boolean hadesVerrouille = niveauJoueur < MenuRecrutementRare.NIVEAU_REQUIS_HADES;

        boutonsBox.getChildren().add(titreSection("Hades"));
        boutonsBox.getChildren().add(carteRecrutementRare("Hades", "Hades", "SSS", possedeSS,
                MenuRecrutementRare.COUT_HADES, hadesRecru,
                hadesVerrouille, MenuRecrutementRare.NIVEAU_REQUIS_HADES, this::confirmerEtRecruterHades));
    }

    private Label titreSection(String texte) {
        Label l = new Label(texte);
        l.getStyleClass().add("section-titre");
        return l;
    }

    private Node carteRecrutementRare(String nom, String nomTechnique, String rang, int possede, int cout, boolean dejaFait,
                                       boolean verrouille, int niveauRequis, Runnable action) {
        Label badge = GuiVisuels.creerBadgeRarete(rang);
        Label nomLabel = new Label(nom);
        nomLabel.getStyleClass().add("item-nom");

        VBox texte = new VBox(4, nomLabel, GuiVisuels.creerBarreProgression(180, 14, possede, cout));
        HBox carte = new HBox(10, badge, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.setPrefWidth(300);

        if (dejaFait) {
            Label tag = new Label("Déjà recruté");
            tag.getStyleClass().add("item-vide");
            carte.getChildren().add(tag);
            carte.getStyleClass().add("carte-item");
            carte.setOpacity(0.5);
        } else if (verrouille) {
            Label tag = new Label("Niveau " + niveauRequis + " requis");
            tag.getStyleClass().add("item-vide");
            carte.getChildren().add(tag);
            carte.getStyleClass().add("carte-item");
            carte.setOpacity(0.5);
        } else {
            carte.getStyleClass().add(possede >= cout ? "carte-item-joueur" : "carte-item");
            carte.setCursor(Cursor.HAND);
            carte.setOnMouseClicked(e -> ouvrirFiche(nomTechnique, action));
        }
        return carte;
    }

    /** Ouvre la fiche du personnage (stats de base + competences) avant confirmation, sur le
     *  meme modele que EcranPageRecrutementController.ouvrirFiche. */
    private void ouvrirFiche(String nomTechnique, Runnable actionRecrutement) {
        PersonnageBase p = SauvegardeJoueur.creerPersonnageParNom(nomTechnique);
        if (p == null) {
            info("Recrutement Rare", "[ERREUR] Personnage introuvable : " + nomTechnique + ".");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(p.getNom());
        dialog.setHeaderText(null);
        styliser(dialog);

        ButtonType recruterType = new ButtonType("Recruter", ButtonBar.ButtonData.OK_DONE);
        ButtonType retourType   = new ButtonType("Retour", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(recruterType, retourType);

        String[] noms = p.getNomsAttaques();

        Label badge = GuiVisuels.creerBadgeRarete(p.getRarete());
        Label nomLabel = new Label(p.getNom() + "  -  " + p.getRole());
        nomLabel.getStyleClass().add("texte");
        nomLabel.setStyle("-fx-font-weight: bold;");
        HBox entete = new HBox(8, badge, nomLabel);
        entete.setAlignment(Pos.CENTER_LEFT);

        StringBuilder sb = new StringBuilder();
        sb.append("ATK : ").append(String.format("%.0f", p.getAttaque())).append("\n");
        sb.append("DEF : ").append(String.format("%.0f", p.getDefense())).append("\n");
        sb.append("VIT : ").append(String.format("%.0f", p.getVitesse())).append("\n\n");
        sb.append(noms[0]).append(" (base)\n   ").append(GuiVisuels.capturerDescription(p::descriptionAttaqueBase)).append("\n\n");
        sb.append(noms[1]).append(" (speciale)\n   ").append(GuiVisuels.capturerDescription(p::descriptionAttaqueSpeciale)).append("\n\n");
        sb.append(noms[2]).append(" (ultime)\n   ").append(GuiVisuels.capturerDescription(p::descriptionAttaqueUltime));

        Label label = new Label(sb.toString());
        label.getStyleClass().add("texte");
        label.setWrapText(true);
        label.setPrefWidth(380);

        VBox contenu = new VBox(8, entete, GuiVisuels.creerBarrePV(380, 12, p.getVie(), p.getVieMax()), label);

        ScrollPane scroll = new ScrollPane(contenu);
        scroll.setFitToWidth(true);
        scroll.setPrefSize(400, 340);
        dialog.getDialogPane().setContent(scroll);

        Optional<ButtonType> resultat = dialog.showAndWait();
        if (resultat.isPresent() && resultat.get() == recruterType) {
            actionRecrutement.run();
        }
    }

    private void confirmerEtRecruterNatsu() {
        if (!confirmer("Recruter Natsu [A] pour " + MenuRecrutementRare.COUT_RECRUTEMENT_NATSU + " " + MenuRecrutementRare.PARCHEMIN_A + " ?")) return;
        info("Recrutement Rare", menuRecrutementRare.recruterNatsu(ctx));
        rafraichir();
    }

    private void confirmerEtEvoluerNatsu() {
        if (!confirmer("Evoluer Natsu [A] vers Natsu Etherion [S] pour " + MenuRecrutementRare.COUT_EVOLUTION_NATSU + " " + MenuRecrutementRare.PARCHEMIN_S
                + " ?\nATTENTION : Natsu [A] sera remplace definitivement.")) return;
        info("Recrutement Rare", menuRecrutementRare.evoluerNatsu(ctx));
        rafraichir();
    }

    private void confirmerEtRecruterGray() {
        if (!confirmer("Recruter Gray [A] pour " + MenuRecrutementRare.COUT_RECRUTEMENT_GRAY + " " + MenuRecrutementRare.PARCHEMIN_A + " ?")) return;
        info("Recrutement Rare", menuRecrutementRare.recruterGray(ctx));
        rafraichir();
    }

    private void confirmerEtRecruterLucy() {
        if (!confirmer("Recruter Lucy [A] pour " + MenuRecrutementRare.COUT_RECRUTEMENT_LUCY + " " + MenuRecrutementRare.PARCHEMIN_A + " ?")) return;
        info("Recrutement Rare", menuRecrutementRare.recruterLucy(ctx));
        rafraichir();
    }

    private void confirmerEtRecruterJubia() {
        if (!confirmer("Recruter Jubia [A] pour " + MenuRecrutementRare.COUT_RECRUTEMENT_JUBIA + " " + MenuRecrutementRare.PARCHEMIN_A + " ?")) return;
        info("Recrutement Rare", menuRecrutementRare.recruterJubia(ctx));
        rafraichir();
    }

    private void confirmerEtRecruterWendy() {
        if (!confirmer("Recruter Wendy [A] pour " + MenuRecrutementRare.COUT_RECRUTEMENT_WENDY + " " + MenuRecrutementRare.PARCHEMIN_A + " ?")) return;
        info("Recrutement Rare", menuRecrutementRare.recruterWendy(ctx));
        rafraichir();
    }

    private void confirmerEtRecruterMirajane() {
        if (!confirmer("Recruter Mirajane [S] pour " + MenuRecrutementRare.COUT_MIRAJANE_S + " " + MenuRecrutementRare.PARCHEMIN_S + " ?")) return;
        info("Recrutement Rare", menuRecrutementRare.recruterMirajane(ctx));
        rafraichir();
    }

    private void confirmerEtEvoluerMirajane() {
        if (!confirmer("Evoluer Mirajane [S] vers Mirajane Halphas [SS] pour " + MenuRecrutementRare.COUT_MIRAJANE_SS + " " + MenuRecrutementRare.PARCHEMIN_SS
                + " ?\nATTENTION : Mirajane [S] sera remplacee definitivement.")) return;
        info("Recrutement Rare", menuRecrutementRare.evoluerMirajane(ctx));
        rafraichir();
    }

    private void confirmerEtRecruterJellal() {
        if (!confirmer("Recruter Jellal [S] pour " + MenuRecrutementRare.COUT_JELLAL + " " + MenuRecrutementRare.PARCHEMIN_S + " ?")) return;
        info("Recrutement Rare", menuRecrutementRare.recruterJellal(ctx));
        rafraichir();
    }

    private void confirmerEtEvoluerJellal() {
        if (!confirmer("Evoluer Jellal [S] vers Jellal Intermagie [SS] pour " + MenuRecrutementRare.COUT_JELLAL_SS + " " + MenuRecrutementRare.PARCHEMIN_SS
                + " ?\nATTENTION : Jellal [S] sera remplace definitivement.")) return;
        info("Recrutement Rare", menuRecrutementRare.evoluerJellal(ctx));
        rafraichir();
    }

    private void confirmerEtRecruterErza() {
        if (!confirmer("Recruter Erza [S] pour " + MenuRecrutementRare.COUT_ERZA + " " + MenuRecrutementRare.PARCHEMIN_S + " ?")) return;
        info("Recrutement Rare", menuRecrutementRare.recruterErza(ctx));
        rafraichir();
    }

    private void confirmerEtRecruterLuxus() {
        if (!confirmer("Recruter Luxus [SS] pour " + MenuRecrutementRare.COUT_LUXUS + " " + MenuRecrutementRare.PARCHEMIN_SS + " ?")) return;
        info("Recrutement Rare", menuRecrutementRare.recruterLuxus(ctx));
        rafraichir();
    }

    private void confirmerEtRecruterBrain() {
        if (!confirmer("Recruter Brain [S] pour " + MenuRecrutementRare.COUT_BRAIN + " " + MenuRecrutementRare.PARCHEMIN_S + " ?")) return;
        info("Recrutement Rare", menuRecrutementRare.recruterBrain(ctx));
        rafraichir();
    }

    private void confirmerEtRecruterHades() {
        if (!confirmer("Recruter Hades [SSS] pour " + MenuRecrutementRare.COUT_HADES + " " + MenuRecrutementRare.PARCHEMIN_SS + " ?")) return;
        info("Recrutement Rare", menuRecrutementRare.recruterHades(ctx));
        rafraichir();
    }

    private boolean confirmer(String question) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, question, ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Recrutement Rare");
        confirm.setHeaderText(null);
        styliser(confirm);
        Optional<ButtonType> resultat = confirm.showAndWait();
        return resultat.isPresent() && resultat.get() == ButtonType.YES;
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
