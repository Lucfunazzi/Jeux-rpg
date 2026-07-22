package lancement.gui;

import java.io.IOException;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
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

        ajouterLabel("[ Natsu ]");
        ajouterLabel("Natsu [A]  -  " + MenuRecrutementRare.MATERIAU_NATSU + " : "
                + possedeIgnir + "/" + MenuRecrutementRare.COUT_RECRUTEMENT
                + (natsuA || natsuS ? "  [DEJA RECRUTE]" : ""));
        if (!natsuA && !natsuS) {
            ajouterBouton("Recruter Natsu [A]", e -> confirmerEtRecruterNatsu());
        }
        if (natsuA) {
            ajouterLabel("Natsu Etherion [S] (evolution)  -  " + MenuRecrutementRare.MATERIAU_NATSU + " : "
                    + possedeIgnir + "/" + MenuRecrutementRare.COUT_EVOLUTION);
            ajouterBouton("Evoluer vers Natsu Etherion [S]", e -> confirmerEtEvoluerNatsu());
        }

        int possedeAile = ctx.inventaire.getQuantiteMateriau(MenuRecrutementRare.MATERIAU_MIRAJANE);
        boolean miraS  = MenuRecrutementRare.dejaRecruteParNom("Mirajane", ctx.personnagesRecruites);
        boolean miraSS = MenuRecrutementRare.dejaRecruteParNom("Mirajane Halphas", ctx.personnagesRecruites);

        ajouterLabel("[ Mirajane ]");
        ajouterLabel("Mirajane [S]  -  " + MenuRecrutementRare.MATERIAU_MIRAJANE + " : "
                + possedeAile + "/" + MenuRecrutementRare.COUT_MIRAJANE_S
                + (miraS || miraSS ? "  [DEJA RECRUTE]" : ""));
        if (!miraS && !miraSS) {
            ajouterBouton("Recruter Mirajane [S]", e -> confirmerEtRecruterMirajane());
        }
        if (miraS) {
            ajouterLabel("Mirajane Halphas [SS] (evolution)  -  " + MenuRecrutementRare.MATERIAU_MIRAJANE + " : "
                    + possedeAile + "/" + MenuRecrutementRare.COUT_MIRAJANE_SS);
            ajouterBouton("Evoluer vers Mirajane Halphas [SS]", e -> confirmerEtEvoluerMirajane());
        }
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

    private void ajouterLabel(String texte) {
        Label label = new Label(texte);
        label.getStyleClass().add("texte");
        label.setWrapText(true);
        boutonsBox.getChildren().add(label);
    }

    private Button ajouterBouton(String libelle, EventHandler<ActionEvent> action) {
        Button bouton = new Button(libelle);
        bouton.getStyleClass().add("menu-bouton");
        bouton.setWrapText(true);
        bouton.setPrefWidth(320);
        bouton.setOnAction(action);
        boutonsBox.getChildren().add(bouton);
        return bouton;
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
