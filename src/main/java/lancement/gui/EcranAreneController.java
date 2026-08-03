package lancement.gui;

import Personnage.PersonnageBase;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Gestionnaires.AreneData;
import lancement.Gestionnaires.GestionnaireArene;

public class EcranAreneController {

    private GameContext ctx;
    private GestionnaireArene gestionnaireArene;
    private AreneData joueurArene;

    @FXML private VBox statsBox;
    @FXML private VBox coffreBox;
    @FXML private VBox choixBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        GuiVisuels.afficherExplicationPremiereVisite(ctx, "Arene", "Arène");
        this.gestionnaireArene = new GestionnaireArene(ctx.sauvegarde::creerPersonnageParNom);

        choixBox.getChildren().setAll(
                carteNav("♛", "Voir le classement",
                        "Ton rang parmi tous les joueurs de l'arene.", this::onClassement),
                carteNav("⚔", "Choisir un adversaire",
                        "Affronte un adversaire proche de ton rang pour gagner des places.", this::onAdversaires),
                carteNav("❒", "Boutique",
                        "Echange tes points boutique contre des recompenses.", this::onBoutique)
        );

        gestionnaireArene.chargerDepuisFirebase();
        initialiserJoueur();
        gestionnaireArene.uploaderRangJoueur(joueurArene);
        rafraichir();
    }

    private void initialiserJoueur() {
        List<String> equipeNoms = ctx.formation.getEquipe().stream()
                .filter(p -> p != null)
                .map(PersonnageBase::getNom)
                .collect(Collectors.toList());

        String userId = ctx.joueur.getNom().trim().toLowerCase().replace(" ", "_");
        joueurArene = gestionnaireArene.getOuCreerJoueur(userId, ctx.joueur.getNom(), equipeNoms, ctx.joueur.getNom());
    }

    private void rafraichir() {
        FlowPane stats = new FlowPane(10, 10);
        stats.setAlignment(Pos.CENTER);
        stats.getChildren().addAll(
                GuiVisuels.creerFicheStat("◆", "Rang", "#" + joueurArene.getRang()),
                GuiVisuels.creerFicheStat("★", "Points classement", String.valueOf(joueurArene.getPointsArene())),
                GuiVisuels.creerFicheStat("✉", "Points boutique", String.valueOf(joueurArene.getPointsBoutique()))
        );
        statsBox.getChildren().setAll(stats);

        boolean disponible = isCoffreDisponible();
        Node carteCoffre = carteNav("❒",
                disponible ? "Coffre journalier — DISPONIBLE !" : "Coffre journalier",
                disponible ? "Recompenses selon ton rang actuel. Clique pour recuperer !" : "Revient chaque jour a 20h.",
                e -> onCoffre());
        if (disponible) carteCoffre.getStyleClass().add("carte-item-joueur");

        boolean coffreRangDisponible = ctx.rangJoueur.estCoffreRangDisponible();
        Node carteCoffreRang = carteNav("❒",
                "Coffre d'Arène — Rang " + ctx.rangJoueur.getRangNom()
                        + (coffreRangDisponible ? " — DISPONIBLE !" : " — deja reclame"),
                "5x Sceau de Rang " + ctx.rangJoueur.getRangNom() + ". Une fois par palier de rang atteint.",
                e -> onCoffreRang());
        if (coffreRangDisponible) carteCoffreRang.getStyleClass().add("carte-item-joueur");

        coffreBox.getChildren().setAll(carteCoffre, carteCoffreRang);
    }

    /** Variante de GuiVisuels.creerCarteChoix() avec une icone, pour les cartes de navigation
     *  et de coffre de cet ecran. */
    private Node carteNav(String icone, String titre, String description, javafx.event.EventHandler<MouseEvent> action) {
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

    private void onCoffreRang() {
        String resultat = ctx.rangJoueur.reclamerCoffreRang(ctx.inventaire);
        ctx.sauvegarde.sauvegarder(ctx);
        info("Coffre d'Arène", resultat);
        rafraichir();
    }

    private void onClassement(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        naviguer(stage, "/fxml/EcranAreneClassement.fxml",
                c -> ((EcranAreneClassementController) c).initData(ctx, gestionnaireArene, joueurArene, retourVers(stage)));
    }

    private void onAdversaires(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        naviguer(stage, "/fxml/EcranAreneAdversaires.fxml",
                c -> ((EcranAreneAdversairesController) c).initData(ctx, gestionnaireArene, joueurArene, retourVers(stage)));
    }

    private void onBoutique(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        naviguer(stage, "/fxml/EcranAreneBoutique.fxml",
                c -> ((EcranAreneBoutiqueController) c).initData(ctx, gestionnaireArene, joueurArene, retourVers(stage)));
    }

    private void onCoffre() {
        if (!isCoffreDisponible()) {
            info("Coffre journalier", "Le coffre sera disponible a 20h !");
            return;
        }

        int rang = joueurArene.getRang();
        int pointsBoutique;
        int or;
        int coupons = 0;
        String tranche;

        if (rang == 1) {
            pointsBoutique = 15_000; or = 110_000; coupons = 50;
            tranche = "Rang #1 - Legendaire";
        } else if (rang <= 4) {
            pointsBoutique = 11_000; or = 100_000;
            tranche = "Rang #2-4 - Elite";
        } else if (rang <= 9) {
            pointsBoutique = 10_000; or = 90_000;
            tranche = "Rang #5-9 - Maitre";
        } else if (rang <= 24) {
            pointsBoutique = 7_000; or = 60_000;
            tranche = "Rang #10-24 - Expert";
        } else if (rang <= 49) {
            pointsBoutique = 5_000; or = 40_000;
            tranche = "Rang #25-49 - Avance";
        } else if (rang <= 74) {
            pointsBoutique = 2_500; or = 20_000;
            tranche = "Rang #50-74 - Intermediaire";
        } else {
            pointsBoutique = 1_500; or = 10_000;
            tranche = "Rang #75-100 - Debutant";
        }

        joueurArene.ajouterPointsBoutique(pointsBoutique);
        ctx.joueur.ajouterOr(or);
        if (coupons > 0) ctx.joueur.setCoupons(ctx.joueur.getCoupons() + coupons);

        ctx.dernierCoffreArene = getCleJour();
        gestionnaireArene.uploaderRangJoueur(joueurArene);
        ctx.sauvegarde.sauvegarder(ctx);

        String message = "Coffre journalier - " + tranche + "\n"
                + "+ " + String.format("%,d", pointsBoutique) + " points boutique\n"
                + "+ " + String.format("%,d", or) + " or"
                + (coupons > 0 ? "\n+ " + coupons + " coupons !" : "");
        info("Coffre journalier", message);
        rafraichir();
    }

    private boolean isCoffreDisponible() {
        if (LocalDateTime.now().getHour() < 20) return false;
        return !getCleJour().equals(getDerniereCleCoffre());
    }

    private String getCleJour() {
        LocalDate aujourd = LocalDate.now();
        if (LocalDateTime.now().getHour() < 20) aujourd = aujourd.minusDays(1);
        return aujourd.toString();
    }

    private String getDerniereCleCoffre() {
        return ctx.dernierCoffreArene != null ? ctx.dernierCoffreArene : "";
    }

    private Runnable retourVers(Stage stage) {
        return () -> {
            try {
                FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranArene.fxml");
                EcranAreneController controller = loader.getController();
                controller.initData(ctx);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private void naviguer(Stage stage, String fxml, java.util.function.Consumer<Object> initialiser) {
        try {
            FXMLLoader loader = Navigation.changerEcran(stage, fxml);
            initialiser.accept(loader.getController());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
