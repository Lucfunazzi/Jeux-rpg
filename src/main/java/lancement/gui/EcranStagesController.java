package lancement.gui;

import Personnage.PersonnageBase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Chapitres.CourbeChapitres;

public class EcranStagesController {

    private GameContext ctx;
    private LigneChapitre ligne;
    private Runnable onRetour;

    @FXML private Label titreLabel;
    @FXML private VBox statsBox;
    @FXML private VBox stagesBox;
    @FXML private HBox navigationChapitreBox;
    @FXML private Button flecheGaucheButton;
    @FXML private Button flecheDroiteButton;
    @FXML private Button coffresButton;

    public void initData(GameContext ctx, LigneChapitre ligne, Runnable onRetour) {
        this.ctx = ctx;
        this.ligne = ligne;
        this.onRetour = onRetour;
        titreLabel.setText(ligne.label().toUpperCase());
        configurerNavigationChapitre();
        rafraichir();
    }

    /** Fleches gauche/droite pour changer de chapitre normal sans repasser par la liste — pas
     *  de sens pour les Chapitres Elite (qui restent accessibles via EcranListeChapitres). */
    private void configurerNavigationChapitre() {
        if (ligne.elite()) {
            navigationChapitreBox.setVisible(false);
            navigationChapitreBox.setManaged(false);
            return;
        }
        navigationChapitreBox.setVisible(true);
        navigationChapitreBox.setManaged(true);

        int numero = ligne.numeroChapitre();
        flecheGaucheButton.setDisable(numero <= 1);

        boolean chapitreSuivantExiste = numero < CourbeChapitres.NB_CHAPITRES_VISIBLES;
        boolean chapitreSuivantDeverrouille = chapitreSuivantExiste
                && EcranHistoireController.construireLigneChapitre(ctx, numero + 1).deverrouille();
        flecheDroiteButton.setDisable(!chapitreSuivantDeverrouille);
    }

    @FXML
    private void onChapitrePrecedent(ActionEvent event) {
        allerVersChapitre((Stage) flecheGaucheButton.getScene().getWindow(), ligne.numeroChapitre() - 1);
    }

    @FXML
    private void onChapitreSuivant(ActionEvent event) {
        allerVersChapitre((Stage) flecheDroiteButton.getScene().getWindow(), ligne.numeroChapitre() + 1);
    }

    private void allerVersChapitre(Stage stage, int numero) {
        try {
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranStages.fxml");
            EcranStagesController controller = loader.getController();
            controller.initData(ctx, EcranHistoireController.construireLigneChapitre(ctx, numero), onRetour);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onCoffres(ActionEvent event) {
        GuiVisuels.ouvrirCoffresChapitre(ctx, ligne, this::rafraichir);
    }

    private void rafraichir() {
        FlowPane stats = new FlowPane(10, 10);
        stats.setAlignment(Pos.CENTER);
        stats.getChildren().add(GuiVisuels.creerFicheStat("●", "Or", String.format("%.0f", ctx.joueur.getOr())));
        if (ligne.elite()) {
            stats.getChildren().add(GuiVisuels.creerFicheStat("✳", "Énergie", ctx.gestionnaireEnergie.afficherEnergie()));
        }
        statsBox.getChildren().setAll(stats);

        stagesBox.getChildren().clear();
        boolean[] reussis   = ligne.stagesReussis().get();
        boolean[] debloques = ligne.stagesDebloques().get();

        for (int i = 1; i <= 10; i++) {
            stagesBox.getChildren().add(carteStage(i, reussis[i], debloques[i]));
        }

        boolean coffreDispo = GuiVisuels.unCoffreChapitreDisponible(ctx, ligne);
        coffresButton.setVisible(coffreDispo);
        coffresButton.setManaged(coffreDispo);
    }

    private Node carteStage(int numero, boolean reussi, boolean debloque) {
        boolean queteAcceptee = ctx.gestionnaireQuetes.estStageJouable(ligne.numeroChapitre(), numero, ligne.elite());
        boolean jouable = debloque && queteAcceptee;

        String etoiles = ctx.gestionnaireEtoiles.getEtoiles(ligne.numeroChapitre(), numero, ligne.elite()).afficher();
        String runs = ligne.elite() ? "  ·  " + ctx.gestionnaireEnergie.getRunsEliteRestants(numero) + "/10 runs" : "";

        Label nom = new Label("Stage " + numero + " - " + ligne.titreStage().apply(numero));
        nom.getStyleClass().add("item-nom");
        nom.setWrapText(true);
        nom.setMaxWidth(320);

        String etatTexte = !debloque ? "Verrouillé" : !queteAcceptee ? "Quête à accepter (menu Quêtes)" : etoiles;
        Label detail = new Label(etatTexte + runs);
        detail.getStyleClass().add("item-detail");

        Label icone = new Label(iconeStage(reussi, debloque, queteAcceptee));
        icone.getStyleClass().add("fiche-stat-icone");

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(10, icone, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.setMaxWidth(Double.MAX_VALUE);
        carte.setPrefWidth(420);
        carte.getStyleClass().add(reussi ? "carte-item-joueur" : "carte-item");

        if (jouable) {
            carte.setCursor(Cursor.HAND);
            carte.setOnMouseClicked(e -> lancerStage(numero, (Stage) ((Node) e.getSource()).getScene().getWindow()));
        } else {
            carte.setOpacity(0.4);
        }
        return carte;
    }

    private String iconeStage(boolean reussi, boolean debloque, boolean queteAcceptee) {
        if (reussi)         return "✔";
        if (!debloque)      return "⚿";
        if (!queteAcceptee) return "⚑";
        return "▶";
    }

    private void lancerStage(int numero, Stage stage) {
        ctx.gestionnaireEnergie.mettreAJourRecharge();

        if (ligne.elite() && !ctx.gestionnaireEnergie.peutFaireRunElite(numero)) {
            info("Stage", "Limite de runs atteinte pour ce stage aujourd'hui (10/10).");
            return;
        }
        int coutEnergie = ligne.elite() ? 5 : 1;
        if (ctx.gestionnaireEnergie.getEnergie() < coutEnergie) {
            info("Stage", "Pas assez d'énergie ! (il faut " + coutEnergie
                    + ", vous avez " + ctx.gestionnaireEnergie.getEnergie() + ")");
            return;
        }

        List<PersonnageBase> avant = new ArrayList<>(ctx.personnagesRecruites);
        List<lancement.Titre> titresAvant = new ArrayList<>(ctx.gestionnaireTitres.getTitresObtenus());
        lancement.Stage.ResultatStage resultat = ligne.lancerStage().apply(ctx, numero);
        if (resultat == null) {
            info("Stage", "Impossible de lancer ce stage pour le moment.");
            return;
        }
        List<PersonnageBase> nouveauxRecrues = new ArrayList<>(ctx.personnagesRecruites);
        nouveauxRecrues.removeAll(avant);
        List<lancement.Titre> nouveauxTitres = new ArrayList<>(ctx.gestionnaireTitres.getTitresObtenus());
        nouveauxTitres.removeAll(titresAvant);

        try {
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranCombat.fxml");
            EcranCombatController controller = loader.getController();
            controller.initCombat(resultat.etatInitial, resultat.evenements, resultat.victoire, v -> {
                Runnable suite = () -> {
                    for (lancement.Titre titre : nouveauxTitres) {
                        annoncerTitre(titre);
                    }
                    if (resultat.recompenses != null) {
                        annoncerRecompenses(resultat.recompenses);
                    }
                    for (PersonnageBase recrue : nouveauxRecrues) {
                        annoncerRecrue(recrue);
                    }
                    retourStages(stage);
                };
                String cheminVideo = v ? cheminVideoFinStage(ligne.numeroChapitre(), numero) : null;
                if (cheminVideo != null) {
                    jouerVideoFinStage(stage, cheminVideo, suite);
                } else {
                    suite.run();
                }
            }, ligne.numeroChapitre());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Chemin classpath de la cinematique de fin de stage, ou null si aucune video n'est
     *  associee a ce stage (le jeu enchaine alors directement sur les recompenses). */
    private String cheminVideoFinStage(int numeroChapitre, int numero) {
        if (numeroChapitre == 1) {
            return switch (numero) {
                case 1  -> "/video/chapitre1/introp_stage1_chap1.mp4";
                case 2  -> "/video/chapitre1/chapitre1_stage2.mp4";
                case 3  -> "/video/chapitre1/stage3_chap1.mp4";
                default -> null;
            };
        }
        return null;
    }

    /** Joue la cinematique de fin de stage (avec bouton "Passer"), puis enchaine sur {@code suite}. */
    private void jouerVideoFinStage(Stage stage, String cheminVideo, Runnable suite) {
        try {
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranFinStageVideo.fxml");
            EcranFinStageVideoController controller = loader.getController();
            controller.initData(cheminVideo, suite);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Popup recapitulant les recompenses obtenues a la fin d'un stage reussi. */
    private void annoncerRecompenses(lancement.Stage.Recompenses r) {
        StringBuilder sb = new StringBuilder();
        if (r.xp > 0) {
            sb.append("+ ").append(r.xp).append(" XP par personnage\n");
        }
        sb.append("+ ").append(r.or).append(" or\n");
        if (r.equipement != null) {
            sb.append("+ Equipement obtenu : ").append(r.equipement).append("\n");
        }
        if (r.carteOrQuantite > 0) {
            sb.append("+ ").append(r.carteOrQuantite).append("x ").append(r.carteOrNom).append("\n");
        }
        if (r.pointsAbilite > 0) {
            sb.append("+ ").append(r.pointsAbilite).append(" point(s) d'abilités\n");
        }

        Label texte = new Label(sb.toString().trim());
        texte.setWrapText(true);
        texte.setStyle("-fx-font-size: 16px; -fx-text-fill: #f2c14e;");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Recompenses");
        alert.setHeaderText("Victoire !");
        alert.getDialogPane().setContent(texte);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("root-menu");
        alert.showAndWait();
    }

    /** Grande annonce quand un titre est debloque pour la premiere fois (typiquement en
     *  terminant un chapitre Elite) — traitement visuel plus imposant que les autres popups. */
    private void annoncerTitre(lancement.Titre titre) {
        Label couronne = new Label("🏆");
        couronne.setStyle("-fx-font-size: 48px;");

        Label nom = new Label(titre.getNom());
        nom.setWrapText(true);
        nom.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #f2c14e; -fx-text-alignment: center;");

        Label description = new Label(titre.getDescription());
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 15px; -fx-text-fill: #cfd8e3; -fx-text-alignment: center;");

        Label bonus = new Label("+" + (int) (titre.getBonusStatsPourcentage() * 100) + "% à toutes les stats de l'équipe une fois équipé");
        bonus.setWrapText(true);
        bonus.setStyle("-fx-font-size: 13px; -fx-text-fill: #7ed9a3; -fx-text-alignment: center;");

        VBox contenu = new VBox(10, couronne, nom, description, bonus);
        contenu.setAlignment(Pos.CENTER);
        contenu.setMaxWidth(360);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Titre obtenu !");
        alert.setHeaderText("★ NOUVEAU TITRE ★");
        alert.getDialogPane().setContent(contenu);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("root-menu");
        alert.showAndWait();
    }

    /** Message bien visible quand un personnage rejoint l'equipe automatiquement (recompense de quete). */
    private void annoncerRecrue(PersonnageBase recrue) {
        Label texte = new Label("✨ " + recrue.getNom() + " a rejoint votre equipe ! ✨\n["
                + recrue.getRarete() + "] " + recrue.getRole());
        texte.setWrapText(true);
        texte.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #f2c14e; -fx-text-alignment: center;");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Nouveau personnage !");
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(texte);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("root-menu");
        alert.showAndWait();
    }

    private void retourStages(Stage stage) {
        try {
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranStages.fxml");
            EcranStagesController controller = loader.getController();
            controller.initData(ctx, ligne, onRetour);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onRetour() {
        onRetour.run();
    }

    @FXML
    private void onQuetes(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranQuetes.fxml");
            EcranQuetesController controller = loader.getController();
            controller.initData(ctx);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void info(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("root-menu");
        alert.showAndWait();
    }
}
