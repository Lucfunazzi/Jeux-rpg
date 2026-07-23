package lancement.gui;

import java.io.IOException;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Menus.MenuRecrutementRare;

public class EcranRecrutementRareController {

    private final MenuRecrutementRare menuRecrutementRare = new MenuRecrutementRare();

    private GameContext ctx;

    @FXML private VBox boutonsBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichir();
    }

    private void rafraichir() {
        boutonsBox.getChildren().clear();

        int possedeIgnir = ctx.inventaire.getQuantiteMateriau(MenuRecrutementRare.MATERIAU_NATSU);
        boolean natsuA = MenuRecrutementRare.dejaRecruteParNom("Natsu", ctx.personnagesRecruites);
        boolean natsuS = MenuRecrutementRare.dejaRecruteParNom("Natsu Etherion", ctx.personnagesRecruites);

        boutonsBox.getChildren().add(titreSection("Natsu"));
        boutonsBox.getChildren().add(carteRecrutementRare("Natsu", "A", possedeIgnir,
                MenuRecrutementRare.COUT_RECRUTEMENT, natsuA || natsuS, this::confirmerEtRecruterNatsu));
        if (natsuA) {
            boutonsBox.getChildren().add(carteRecrutementRare("Natsu Etherion (évolution)", "S", possedeIgnir,
                    MenuRecrutementRare.COUT_EVOLUTION, false, this::confirmerEtEvoluerNatsu));
        }

        int possedeAile = ctx.inventaire.getQuantiteMateriau(MenuRecrutementRare.MATERIAU_MIRAJANE);
        boolean miraS  = MenuRecrutementRare.dejaRecruteParNom("Mirajane", ctx.personnagesRecruites);
        boolean miraSS = MenuRecrutementRare.dejaRecruteParNom("Mirajane Halphas", ctx.personnagesRecruites);

        boutonsBox.getChildren().add(titreSection("Mirajane"));
        boutonsBox.getChildren().add(carteRecrutementRare("Mirajane", "S", possedeAile,
                MenuRecrutementRare.COUT_MIRAJANE_S, miraS || miraSS, this::confirmerEtRecruterMirajane));
        if (miraS) {
            boutonsBox.getChildren().add(carteRecrutementRare("Mirajane Halphas (évolution)", "SS", possedeAile,
                    MenuRecrutementRare.COUT_MIRAJANE_SS, false, this::confirmerEtEvoluerMirajane));
        }
    }

    private Label titreSection(String texte) {
        Label l = new Label(texte);
        l.getStyleClass().add("section-titre");
        return l;
    }

    private Node carteRecrutementRare(String nom, String rang, int possede, int cout, boolean dejaFait, Runnable action) {
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
        } else {
            carte.getStyleClass().add(possede >= cout ? "carte-item-joueur" : "carte-item");
            carte.setCursor(Cursor.HAND);
            carte.setOnMouseClicked(e -> action.run());
        }
        return carte;
    }

    private void confirmerEtRecruterNatsu() {
        if (!confirmer("Recruter Natsu [A] pour " + MenuRecrutementRare.COUT_RECRUTEMENT + " " + MenuRecrutementRare.MATERIAU_NATSU + " ?")) return;
        info("Recrutement Rare", menuRecrutementRare.recruterNatsu(ctx));
        rafraichir();
    }

    private void confirmerEtEvoluerNatsu() {
        if (!confirmer("Evoluer Natsu [A] vers Natsu Etherion [S] pour " + MenuRecrutementRare.COUT_EVOLUTION + " " + MenuRecrutementRare.MATERIAU_NATSU
                + " ?\nATTENTION : Natsu [A] sera remplace definitivement.")) return;
        info("Recrutement Rare", menuRecrutementRare.evoluerNatsu(ctx));
        rafraichir();
    }

    private void confirmerEtRecruterMirajane() {
        if (!confirmer("Recruter Mirajane [S] pour " + MenuRecrutementRare.COUT_MIRAJANE_S + " " + MenuRecrutementRare.MATERIAU_MIRAJANE + " ?")) return;
        info("Recrutement Rare", menuRecrutementRare.recruterMirajane(ctx));
        rafraichir();
    }

    private void confirmerEtEvoluerMirajane() {
        if (!confirmer("Evoluer Mirajane [S] vers Mirajane Halphas [SS] pour " + MenuRecrutementRare.COUT_MIRAJANE_SS + " " + MenuRecrutementRare.MATERIAU_MIRAJANE
                + " ?\nATTENTION : Mirajane [S] sera remplacee definitivement.")) return;
        info("Recrutement Rare", menuRecrutementRare.evoluerMirajane(ctx));
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
