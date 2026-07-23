package lancement.gui;

import Equipement.ParcheminXP;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.MiniJeuPFC;
import lancement.Menus.MenuRecrutement;

public class EcranRecrutementController {

    private GameContext ctx;

    @FXML private Label resumeLabel;
    @FXML private VBox boutonsBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichir();
    }

    private void rafraichir() {
        MenuRecrutement mr = ctx.menuRecrutement;
        resumeLabel.setText("Or : " + String.format("%.0f", ctx.joueur.getOr())
                + "  |  Parchemins C : " + mr.getParcheminC()
                + "  |  Parchemins B : " + mr.getParcheminB()
                + "  |  Parchemins A : " + mr.getParcheminA());

        boutonsBox.getChildren().clear();
        int niveau = ctx.joueur.getNiveau();

        for (int page = 1; page <= 3; page++) {
            int niveauRequis = MenuRecrutement.getNiveauRequisPage(page);
            if (niveau < niveauRequis) continue;
            String rang = MenuRecrutement.getRangPage(page);
            Button bouton = ajouterBouton("Page " + page + " - Rang " + rang);
            int pageFinal = page;
            bouton.setOnAction(e -> ouvrirPage(e, pageFinal));
        }

        ajouterBouton("Acheter des Parchemins XP").setOnAction(e -> onAchatParchemins());

        MiniJeuPFC mj = mr.getMiniJeu();
        if (niveau >= MenuRecrutement.getNiveauRequisPage(1)) {
            ajouterBouton("Mini-jeu PFC - Rang C  (" + mj.getCoutPartieC() + " or x10 auto)").setOnAction(e -> jouerAuto("C"));
            ajouterBouton("Mini-jeu PFC - Rang C  (" + mj.getCoutPartieC() + " or, manuel)").setOnAction(e -> jouerManuel("C"));
        }
        if (niveau >= MenuRecrutement.getNiveauRequisPage(2)) {
            ajouterBouton("Mini-jeu PFC - Rang B  (" + mj.getCoutPartieB() + " or x10 auto)").setOnAction(e -> jouerAuto("B"));
            ajouterBouton("Mini-jeu PFC - Rang B  (" + mj.getCoutPartieB() + " or, manuel)").setOnAction(e -> jouerManuel("B"));
        }
        if (niveau >= MenuRecrutement.getNiveauRequisPage(3)) {
            ajouterBouton("Mini-jeu PFC - Rang A  (" + mj.getCoutPartieA() + " or x10 auto)").setOnAction(e -> jouerAuto("A"));
            ajouterBouton("Mini-jeu PFC - Rang A  (" + mj.getCoutPartieA() + " or, manuel)").setOnAction(e -> jouerManuel("A"));
        }
    }

    private Button ajouterBouton(String libelle) {
        Button bouton = new Button(libelle);
        bouton.getStyleClass().add("menu-bouton");
        bouton.setWrapText(true);
        bouton.setPrefWidth(340);
        boutonsBox.getChildren().add(bouton);
        return bouton;
    }

    private void ouvrirPage(ActionEvent event, int numero) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Runnable retour = () -> {
            try {
                FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranRecrutement.fxml");
                EcranRecrutementController controller = loader.getController();
                controller.initData(ctx);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        try {
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranPageRecrutement.fxml");
            EcranPageRecrutementController controller = loader.getController();
            controller.initData(ctx, numero, retour);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void jouerAuto(String rang) {
        MenuRecrutement mr = ctx.menuRecrutement;
        MiniJeuPFC mj = mr.getMiniJeu();
        int cout = switch (rang) {
            case "C" -> mj.getCoutPartieC();
            case "B" -> mj.getCoutPartieB();
            default  -> mj.getCoutPartieA();
        };
        int coutTotal = cout * 10;

        if (ctx.joueur.getOr() < coutTotal) {
            info("Mini-jeu", "Pas assez d'or ! Il faut " + coutTotal + " or pour 10 parties.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Jouer 10 parties automatiques pour " + coutTotal + " or ?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Mini-jeu PFC");
        confirm.setHeaderText(null);
        styliser(confirm);
        Optional<ButtonType> resultat = confirm.showAndWait();
        if (resultat.isEmpty() || resultat.get() != ButtonType.YES) return;

        int totalGagnes = 0;
        for (int i = 0; i < 10; i++) {
            totalGagnes += switch (rang) {
                case "C" -> mj.jouerAutoC(ctx.joueur);
                case "B" -> mj.jouerAutoB(ctx.joueur);
                default  -> mj.jouerAutoA(ctx.joueur);
            };
        }
        switch (rang) {
            case "C" -> mr.ajouterParcheminC(totalGagnes);
            case "B" -> mr.ajouterParcheminB(totalGagnes);
            default  -> mr.ajouterParcheminA(totalGagnes);
        }
        ctx.sauvegarde.sauvegarder(ctx);
        info("Mini-jeu PFC", "10 parties terminees !\nTotal parchemins " + rang + " gagnes : " + totalGagnes);
        rafraichir();
    }

    private void jouerManuel(String rang) {
        MenuRecrutement mr = ctx.menuRecrutement;
        MiniJeuPFC mj = mr.getMiniJeu();
        int cout = mj.getCoutPartie(rang);

        if (ctx.joueur.getOr() < cout) {
            info("Mini-jeu", "Pas assez d'or ! Il faut " + cout + " or pour jouer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Jouer une partie manuelle (3 manches) pour " + cout + " or ?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Mini-jeu PFC");
        confirm.setHeaderText(null);
        styliser(confirm);
        Optional<ButtonType> resultat = confirm.showAndWait();
        if (resultat.isEmpty() || resultat.get() != ButtonType.YES) return;

        ctx.joueur.ajouterOr(-cout);
        ouvrirDialoguePFC(rang, mj);
    }

    private void ouvrirDialoguePFC(String rang, MiniJeuPFC mj) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Mini-jeu PFC - Rang " + rang);
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        styliser(dialog);

        Label titreLabel = new Label();
        titreLabel.getStyleClass().add("texte");

        TextArea log = new TextArea();
        log.setEditable(false);
        log.setWrapText(true);
        log.setPrefSize(420, 220);
        log.getStyleClass().add("zone-mono");

        Button bPierre  = new Button("Pierre");
        Button bFeuille = new Button("Feuille");
        Button bCiseaux = new Button("Ciseaux");
        for (Button b : List.of(bPierre, bFeuille, bCiseaux)) {
            b.getStyleClass().add("menu-bouton");
        }

        HBox choixBox = new HBox(10, bPierre, bFeuille, bCiseaux);
        choixBox.setAlignment(Pos.CENTER);

        VBox contenu = new VBox(12, titreLabel, log, choixBox);
        contenu.setAlignment(Pos.CENTER);
        contenu.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(contenu);

        int[] manche = {1};
        int[] parcheminsTotaux = {0};
        boolean[] termine = {false};

        Runnable majTitre = () -> titreLabel.setText(
                "Manche " + manche[0] + "/3  —  Parchemins " + rang + " gagnes : " + parcheminsTotaux[0]);
        majTitre.run();

        java.util.function.IntConsumer jouerChoix = choix -> {
            if (termine[0]) return;
            MiniJeuPFC.ResultatManche r = mj.jouerUneManche(choix);
            log.appendText("Vous : " + r.nomJoueur() + "  |  Adversaire : " + r.nomIA() + "\n");

            if (r.resultat() == 0) {
                log.appendText("Egalite ! On rejoue la manche.\n\n");
                return;
            }

            if (r.resultat() == 1) {
                int recompense = mj.getRecompenseManche(rang, manche[0]);
                parcheminsTotaux[0] += recompense;
                log.appendText("Victoire ! +" + recompense + " parchemins " + rang + ".\n\n");
                if (manche[0] == 3) {
                    log.appendText("Partie parfaite ! Total : " + parcheminsTotaux[0] + " parchemins " + rang + " gagnes !\n");
                    termine[0] = true;
                    bPierre.setDisable(true); bFeuille.setDisable(true); bCiseaux.setDisable(true);
                } else {
                    manche[0]++;
                }
            } else {
                int remboursement = mj.getRemboursement(rang, manche[0]);
                ctx.joueur.ajouterOr(remboursement);
                log.appendText("Defaite a la manche " + manche[0] + ". Remboursement : " + remboursement + " or.\n");
                log.appendText("Parchemins conserves : " + parcheminsTotaux[0] + "\n");
                termine[0] = true;
                bPierre.setDisable(true); bFeuille.setDisable(true); bCiseaux.setDisable(true);
            }
            majTitre.run();
        };

        bPierre.setOnAction(e -> jouerChoix.accept(1));
        bFeuille.setOnAction(e -> jouerChoix.accept(2));
        bCiseaux.setOnAction(e -> jouerChoix.accept(3));

        dialog.showAndWait();

        if (parcheminsTotaux[0] > 0) {
            MenuRecrutement mr = ctx.menuRecrutement;
            switch (rang) {
                case "C" -> mr.ajouterParcheminC(parcheminsTotaux[0]);
                case "B" -> mr.ajouterParcheminB(parcheminsTotaux[0]);
                default  -> mr.ajouterParcheminA(parcheminsTotaux[0]);
            }
        }
        ctx.sauvegarde.sauvegarder(ctx);
        rafraichir();
    }

    private void onAchatParchemins() {
        MenuRecrutement mr = ctx.menuRecrutement;

        List<String> options = List.of(
                "Parchemin XP [C] (+500 XP) - cout " + mr.getCoutParcheminXpC() + " parchemins C",
                "Parchemin XP [B] (+1500 XP) - cout " + mr.getCoutParcheminXpB() + " parchemins B"
        );
        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle("Achat - Parchemins XP");
        dialog.setHeaderText(null);
        dialog.setContentText("Parchemins C : " + mr.getParcheminC() + "  |  Parchemins B : " + mr.getParcheminB() + "\nChoix :");
        styliser(dialog);
        Optional<String> choix = dialog.showAndWait();
        if (choix.isEmpty()) return;

        boolean estC = choix.get().startsWith("Parchemin XP [C]");
        ParcheminXP.Rarete rarete = estC ? ParcheminXP.Rarete.C : ParcheminXP.Rarete.B;
        int cout = estC ? mr.getCoutParcheminXpC() : mr.getCoutParcheminXpB();
        int stockDispo = estC ? mr.getParcheminC() : mr.getParcheminB();

        TextInputDialog qteDialog = new TextInputDialog("1");
        qteDialog.setTitle("Quantite");
        qteDialog.setHeaderText(null);
        qteDialog.setContentText("Combien de parchemins XP [" + rarete.name() + "] voulez-vous acheter ?");
        styliser(qteDialog);
        Optional<String> qteStr = qteDialog.showAndWait();
        if (qteStr.isEmpty()) return;

        int qte;
        try {
            qte = Integer.parseInt(qteStr.get().trim());
        } catch (NumberFormatException e) {
            info("Achat", "Entree invalide.");
            return;
        }
        if (qte <= 0) return;

        int coutTotal = qte * cout;
        if (stockDispo < coutTotal) {
            info("Achat", "Pas assez de parchemins " + rarete.name() + " ! (il faut " + coutTotal + ", vous avez " + stockDispo + ")");
            return;
        }

        if (estC) mr.ajouterParcheminC(-coutTotal); else mr.ajouterParcheminB(-coutTotal);
        ctx.inventaire.ajouterParcheminXP(rarete, qte);
        ctx.sauvegarde.sauvegarder(ctx);
        info("Achat", qte + " Parchemin(s) XP [" + rarete.name() + "] ajoutes a l'inventaire !");
        rafraichir();
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
