package lancement.gui;

import java.io.IOException;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import lancement.GameContext;
import lancement.Gestionnaires.CompagnonsType;
import lancement.Gestionnaires.GestionnaireCompagnons;
import lancement.Gestionnaires.GestionnaireCompagnons.ResultatCompagnon;

public class EcranCompagnonsController {

    /** Frames du battement d'ailes de Happy (voles sur place), chargees une seule fois. */
    private static final Image[] HAPPY_FRAMES_VOL = {
        new Image(EcranCompagnonsController.class.getResourceAsStream("/images/compagnons/happy_vol_1.png")),
        new Image(EcranCompagnonsController.class.getResourceAsStream("/images/compagnons/happy_vol_2.png")),
        new Image(EcranCompagnonsController.class.getResourceAsStream("/images/compagnons/happy_vol_3.png")),
        new Image(EcranCompagnonsController.class.getResourceAsStream("/images/compagnons/happy_vol_4.png")),
    };

    private GameContext ctx;

    @FXML private VBox compagnonBox;
    @FXML private VBox orBox;
    @FXML private VBox actionBox;

    /** Animations du sprite en cours (battement d'ailes + oscillation haut/bas), a stopper avant d'en recreer. */
    private Timeline    animationBattement;
    private TranslateTransition animationVol;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        GuiVisuels.afficherExplicationPremiereVisite(ctx, "Compagnons", "Compagnons");
        rafraichir();
    }

    private void rafraichir() {
        arreterAnimationSprite();
        GestionnaireCompagnons gc = ctx.gestionnaireCompagnons;

        Label nomLabel = new Label(gc.getType().nom);
        nomLabel.getStyleClass().add("item-nom");
        nomLabel.setStyle("-fx-font-size: 20px;");

        Label niveauLabel = new Label("Niveau " + gc.getNiveau() + " / " + GestionnaireCompagnons.NIVEAU_MAX);
        niveauLabel.getStyleClass().add("item-detail");

        Label bonusLabel = new Label(String.format("Bonus équipe : ATK +%.0f | PV +%.0f | DEF +%.0f | VIT +%.0f",
                gc.getBonusATK(), gc.getBonusPV(), gc.getBonusDEF(), gc.getBonusVIT()));
        bonusLabel.getStyleClass().add("item-qte");

        VBox texte = new VBox(6, nomLabel, niveauLabel,
                GuiVisuels.creerBarreProgression(240, 14, gc.getNiveau(), GestionnaireCompagnons.NIVEAU_MAX),
                bonusLabel);
        texte.setAlignment(Pos.CENTER);

        VBox carte = new VBox(10, texte);
        carte.setAlignment(Pos.CENTER);
        carte.getStyleClass().add("carte-item-joueur");
        carte.setPrefWidth(300);
        if (gc.getType() == CompagnonsType.HAPPY) {
            carte.getChildren().add(0, creerSpriteHappyAnime());
        }
        compagnonBox.getChildren().setAll(carte);

        orBox.getChildren().setAll(
                GuiVisuels.creerFicheStat("●", "Or disponible", String.format("%.0f", ctx.joueur.getOr())));

        Node carteAction;
        if (!gc.estAuNiveauMax()) {
            carteAction = carteAction("⚒",
                    "Améliorer → Niv." + (gc.getNiveau() + 1),
                    gc.getCoutProchainNiveau() + " or",
                    e -> onAction());
        } else if (gc.peutEvoluer()) {
            carteAction = carteAction("☄",
                    "Évoluer → " + gc.getType().suivant().nom,
                    gc.getCoutEvolution() + " or",
                    e -> onAction());
        } else {
            carteAction = carteAction("✔", "Compagnon au maximum actuel", "", e -> {});
            carteAction.setOpacity(0.5);
            carteAction.setCursor(Cursor.DEFAULT);
            carteAction.setOnMouseClicked(null);
        }
        actionBox.getChildren().setAll(carteAction);
    }

    /** Sprite de Happy qui vole sur place : battement d'ailes (4 frames) + oscillation haut/bas. */
    private Node creerSpriteHappyAnime() {
        ImageView vue = new ImageView(HAPPY_FRAMES_VOL[0]);
        vue.setPreserveRatio(true);
        vue.setFitHeight(90);
        vue.setSmooth(false); // garde le pixel art net, sans flou de redimensionnement

        animationBattement = new Timeline(
                new KeyFrame(Duration.ZERO,        e -> vue.setImage(HAPPY_FRAMES_VOL[0])),
                new KeyFrame(Duration.millis(120),  e -> vue.setImage(HAPPY_FRAMES_VOL[1])),
                new KeyFrame(Duration.millis(240),  e -> vue.setImage(HAPPY_FRAMES_VOL[2])),
                new KeyFrame(Duration.millis(360),  e -> vue.setImage(HAPPY_FRAMES_VOL[3])),
                new KeyFrame(Duration.millis(480)));
        animationBattement.setCycleCount(Timeline.INDEFINITE);
        animationBattement.play();

        animationVol = new TranslateTransition(Duration.millis(700), vue);
        animationVol.setByY(-8);
        animationVol.setCycleCount(TranslateTransition.INDEFINITE);
        animationVol.setAutoReverse(true);
        animationVol.setInterpolator(Interpolator.EASE_BOTH);
        animationVol.play();

        return vue;
    }

    /** Stoppe les animations en cours avant de reconstruire l'ecran (sinon elles s'accumulent en arriere-plan). */
    private void arreterAnimationSprite() {
        if (animationBattement != null) animationBattement.stop();
        if (animationVol != null)       animationVol.stop();
    }

    /** Variante de GuiVisuels.creerCarteChoix() avec une icone. */
    private Node carteAction(String icone, String titre, String description, javafx.event.EventHandler<javafx.scene.input.MouseEvent> action) {
        Label iconeLabel = new Label(icone);
        iconeLabel.getStyleClass().add("fiche-stat-icone");

        Label titreLabel = new Label(titre);
        titreLabel.getStyleClass().add("item-nom");

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("item-detail");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(280);

        VBox texte = new VBox(4, titreLabel, descLabel);
        HBox carte = new HBox(10, iconeLabel, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(360);
        carte.setCursor(Cursor.HAND);
        carte.setOnMouseClicked(action);
        return carte;
    }

    private void onAction() {
        GestionnaireCompagnons gc = ctx.gestionnaireCompagnons;
        ResultatCompagnon res = gc.peutEvoluer()
                ? gc.evoluer(ctx.joueur.getOr())
                : gc.ameliorer(ctx.joueur.getOr());

        if (res.succes()) {
            ctx.joueur.setOr(ctx.joueur.getOr() - res.orDepense());
            ctx.formation.appliquerBonusLiens();
            ctx.sauvegarde.sauvegarder(ctx);
        }
        info("Compagnons", res.message());
        rafraichir();
    }

    @FXML
    private void onRetour(ActionEvent event) {
        arreterAnimationSprite();
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
