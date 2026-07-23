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
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.MiniJeuPFC;
import lancement.Menus.MenuRecrutement;

public class EcranRecrutementController {

    private GameContext ctx;

    @FXML private FlowPane statsBox;
    @FXML private VBox boutonsBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichir();
    }

    private void rafraichir() {
        MenuRecrutement mr = ctx.menuRecrutement;
        statsBox.getChildren().setAll(
                GuiVisuels.creerFicheStat("Or", String.format("%.0f", ctx.joueur.getOr())),
                GuiVisuels.creerFicheStat("Parchemins C", String.valueOf(mr.getParcheminC())),
                GuiVisuels.creerFicheStat("Parchemins B", String.valueOf(mr.getParcheminB())),
                GuiVisuels.creerFicheStat("Parchemins A", String.valueOf(mr.getParcheminA()))
        );

        boutonsBox.getChildren().clear();
        int niveau = ctx.joueur.getNiveau();

        boolean unePageAjoutee = false;
        for (int page = 1; page <= 3; page++) {
            int niveauRequis = MenuRecrutement.getNiveauRequisPage(page);
            if (niveau < niveauRequis) continue;
            if (!unePageAjoutee) { boutonsBox.getChildren().add(titreSection("Pages de recrutement")); unePageAjoutee = true; }
            String rang = MenuRecrutement.getRangPage(page);
            int pageFinal = page;
            boutonsBox.getChildren().add(GuiVisuels.creerCarteChoix("Page " + page,
                    "Rang " + rang + " — recrutez des personnages contre parchemins.", e -> ouvrirPage(e, pageFinal)));
        }

        boutonsBox.getChildren().add(titreSection("Parchemins"));
        boutonsBox.getChildren().add(GuiVisuels.creerCarteChoix("Acheter des Parchemins XP",
                "Échange des parchemins de recrutement contre de l'XP pour un personnage.", e -> onAchatParchemins()));

        MiniJeuPFC mj = mr.getMiniJeu();
        boolean uneManche = false;
        for (String rang : List.of("C", "B", "A")) {
            int page = rang.equals("C") ? 1 : rang.equals("B") ? 2 : 3;
            if (niveau < MenuRecrutement.getNiveauRequisPage(page)) continue;
            if (!uneManche) { boutonsBox.getChildren().add(titreSection("Mini-jeu Pierre-Feuille-Ciseaux")); uneManche = true; }

            int cout = switch (rang) {
                case "C" -> mj.getCoutPartieC();
                case "B" -> mj.getCoutPartieB();
                default  -> mj.getCoutPartieA();
            };
            boutonsBox.getChildren().add(GuiVisuels.creerCarteChoix("Rang " + rang + " — x10 auto",
                    (cout * 10) + " or pour 10 parties jouées automatiquement.", e -> jouerAuto(rang)));
            boutonsBox.getChildren().add(GuiVisuels.creerCarteChoix("Rang " + rang + " — manuel",
                    cout + " or, 3 manches jouées à la main.", e -> jouerManuel(rang)));
        }
    }

    private Label titreSection(String texte) {
        Label l = new Label(texte);
        l.getStyleClass().add("section-titre");
        return l;
    }

    private void ouvrirPage(MouseEvent event, int numero) {
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

        List<ParcheminXP.Rarete> options = List.of(ParcheminXP.Rarete.C, ParcheminXP.Rarete.B);
        ParcheminXP.Rarete rarete = GuiVisuels.choisirParmiCartes(
                "Achat - Parchemins XP", options, r -> carteAchatParchemin(r, mr));
        if (rarete == null) return;

        boolean estC = rarete == ParcheminXP.Rarete.C;
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

    private Node carteAchatParchemin(ParcheminXP.Rarete r, MenuRecrutement mr) {
        boolean estC = r == ParcheminXP.Rarete.C;
        int xp = estC ? 500 : 1500;
        int cout = estC ? mr.getCoutParcheminXpC() : mr.getCoutParcheminXpB();

        Label badge = GuiVisuels.creerBadgeRarete(r.name());
        Label nom = new Label("Parchemin XP (+" + xp + " XP)");
        nom.getStyleClass().add("item-nom");
        Label detail = new Label("Coût : " + cout + " parchemins " + r.name());
        detail.getStyleClass().add("item-detail");

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(10, badge, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(300);
        return carte;
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
