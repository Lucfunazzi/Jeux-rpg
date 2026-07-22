package lancement.gui;

import Joueur.ArbreCompetences;
import Joueur.NoeudArbre;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
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
            String etat = n.isDebloque() ? "[OK] " : "[  ] ";
            String cout = n.isDebloque() ? "" : "  (" + n.getCoutPoints() + " pts)";

            Button bouton = new Button(etat + "Noeud " + i + " - " + n.getDescription() + cout);
            bouton.getStyleClass().add("menu-bouton");
            bouton.setWrapText(true);
            bouton.setPrefWidth(380);

            boolean precedentOk = (i == 1) || MenuAbilite.getNoeud(arbre, numArbre, i - 1).isDebloque();
            if (n.isDebloque() || !precedentOk) {
                bouton.setDisable(true);
            } else {
                int index = i;
                bouton.setOnAction(e -> debloquer(index, n));
            }
            noeudsBox.getChildren().add(bouton);
        }
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
