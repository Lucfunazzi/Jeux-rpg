package lancement.gui;

import Personnage.PersonnageBase;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lancement.GameContext;
import lancement.Menus.MenuRecrutement;

public class EcranPageRecrutementController {

    private GameContext ctx;
    private int numero;
    private Runnable onRetour;

    @FXML private Label titreLabel;
    @FXML private Label parcheminsLabel;
    @FXML private VBox persosBox;

    public void initData(GameContext ctx, int numero, Runnable onRetour) {
        this.ctx = ctx;
        this.numero = numero;
        this.onRetour = onRetour;
        titreLabel.setText("PAGE " + numero + " - RANG " + MenuRecrutement.getRangPage(numero));
        rafraichir();
    }

    private void rafraichir() {
        int requis = MenuRecrutement.getParcheminsRequisPage(numero);
        String rang = MenuRecrutement.getRangPage(numero);
        int actuels = parcheminsActuels(rang);
        parcheminsLabel.setText("Parchemins " + rang + " : " + actuels + "/" + requis);

        persosBox.getChildren().clear();
        for (String[] info : MenuRecrutement.getPage(numero)) {
            String nom = info[0], role = info[1];
            boolean dejaRecrute = ctx.personnagesRecruites.stream()
                    .anyMatch(p -> p.getNom().equalsIgnoreCase(nom));

            PersonnageBase apercu = ctx.menuRecrutement.creerPersonnage(nom);
            Label badge = GuiVisuels.creerBadgeRarete(apercu != null ? apercu.getRarete() : rang);

            Button bouton = new Button(nom + "  [" + role + "]  - " + requis + " parchemins " + rang
                    + (dejaRecrute ? "  [DEJA RECRUTE]" : ""));
            bouton.getStyleClass().add("menu-bouton");
            bouton.setWrapText(true);
            bouton.setPrefWidth(320);
            if (dejaRecrute) {
                bouton.setDisable(true);
            } else {
                bouton.setOnAction(e -> ouvrirFiche(nom));
            }

            HBox ligne = new HBox(8, badge, bouton);
            ligne.setAlignment(Pos.CENTER_LEFT);
            persosBox.getChildren().add(ligne);
        }
    }

    /** Ouvre la fiche du personnage (stats de base + compétences) avant confirmation du recrutement. */
    private void ouvrirFiche(String nom) {
        PersonnageBase p = ctx.menuRecrutement.creerPersonnage(nom);
        if (p == null) {
            info("Recrutement", "[ERREUR] Personnage introuvable : " + nom + ".");
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
            recruter(nom);
        }
    }

    private int parcheminsActuels(String rang) {
        return switch (rang) {
            case "C" -> ctx.menuRecrutement.getParcheminC();
            case "B" -> ctx.menuRecrutement.getParcheminB();
            default  -> ctx.menuRecrutement.getParcheminA();
        };
    }

    private void recruter(String nom) {
        int requis = MenuRecrutement.getParcheminsRequisPage(numero);
        String rang = MenuRecrutement.getRangPage(numero);
        int actuels = parcheminsActuels(rang);

        if (actuels < requis) {
            info("Recrutement", "Pas assez de parchemins " + rang + " ! (" + actuels + "/" + requis + ")");
            return;
        }

        PersonnageBase recrute = ctx.menuRecrutement.creerPersonnage(nom);
        if (recrute == null) {
            info("Recrutement", "[ERREUR] Personnage introuvable : " + nom + ". Aucun parchemin deduit.");
            return;
        }

        switch (rang) {
            case "C" -> ctx.menuRecrutement.ajouterParcheminC(-requis);
            case "B" -> ctx.menuRecrutement.ajouterParcheminB(-requis);
            default  -> ctx.menuRecrutement.ajouterParcheminA(-requis);
        }
        ctx.personnagesRecruites.add(recrute);
        ctx.sauvegarde.sauvegarder(ctx);
        info("Recrutement", recrute.getNom() + " a rejoint vos allies !");
        rafraichir();
    }

    @FXML
    private void onRetour(ActionEvent event) {
        onRetour.run();
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
