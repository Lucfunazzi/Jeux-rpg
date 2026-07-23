package lancement.gui;

import Joueur.ArbreCompetences;
import Joueur.NoeudArbre;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lancement.GameContext;
import lancement.Menus.MenuAbilite;

public class EcranArbreController {

    private GameContext ctx;
    private int numArbre;
    private Runnable onRetour;

    @FXML private Label titreLabel;
    @FXML private Label pointsLabel;
    @FXML private VBox noeudsBox;

    public void initData(GameContext ctx, int numArbre, Runnable onRetour) {
        this.ctx = ctx;
        this.numArbre = numArbre;
        this.onRetour = onRetour;

        String nomArbre = switch (numArbre) {
            case 1  -> "Voie du Combattant";
            case 2  -> "Voie du Maitre";
            default -> "Voie de l'Ascension";
        };
        titreLabel.setText("ARBRE " + numArbre + " - " + nomArbre);
        rafraichir();
    }

    private void rafraichir() {
        ArbreCompetences arbre = ctx.joueur.getArbreCompetences();
        pointsLabel.setText("Points disponibles : " + arbre.getPointsDisponibles());

        noeudsBox.getChildren().clear();
        for (int i = 1; i <= 10; i++) {
            NoeudArbre n = MenuAbilite.getNoeud(arbre, numArbre, i);
            boolean precedentOk = (i == 1) || MenuAbilite.getNoeud(arbre, numArbre, i - 1).isDebloque();
            noeudsBox.getChildren().add(carteNoeud(i, n, precedentOk));
        }
    }

    private Node carteNoeud(int index, NoeudArbre n, boolean precedentOk) {
        Label numero = new Label(String.valueOf(index));
        numero.getStyleClass().add("vs-badge");

        Label description = new Label(n.getDescription());
        description.getStyleClass().add("item-nom");
        description.setWrapText(true);
        description.setMaxWidth(280);

        Label statut = new Label(n.isDebloque() ? "Débloqué" : precedentOk ? n.getCoutPoints() + " pts" : "Verrouillé");
        statut.getStyleClass().add(n.isDebloque() ? "item-qte" : "item-detail");

        VBox texte = new VBox(2, description, statut);
        HBox carte = new HBox(12, numero, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.setPrefWidth(380);

        if (n.isDebloque()) {
            carte.getStyleClass().add("carte-item-joueur");
        } else if (precedentOk) {
            carte.getStyleClass().add("carte-item");
            carte.setCursor(Cursor.HAND);
            carte.setOnMouseClicked(e -> debloquer(index, n));
        } else {
            carte.getStyleClass().add("carte-item");
            carte.setOpacity(0.4);
        }
        return carte;
    }

    private void debloquer(int index, NoeudArbre n) {
        ArbreCompetences arbre = ctx.joueur.getArbreCompetences();
        String message;
        if (n.getTypeBonus() == NoeudArbre.TypeBonus.COMPETENCE_SPECIALE) {
            message = MenuAbilite.debloquerNoeudCompetence(ctx, arbre, numArbre, index);
        } else {
            String resultat = arbre.tenterDebloquer(numArbre, index);
            message = resultat.equals("OK")
                    ? "Noeud " + index + " debloque : " + n.getDescription() + " !\nPoints restants : " + arbre.getPointsDisponibles()
                    : resultat;
        }
        info("Arbre " + numArbre, message);
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
