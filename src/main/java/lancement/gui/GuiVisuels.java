package lancement.gui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lancement.Menus.MenuTirage_recrutement.LigneResultat;
import lancement.Menus.MenuTirage_recrutement.ResultatTirage;

/**
 * Petits composants visuels partages entre les ecrans (badge de rarete, barres de PV/XP)
 * pour garder le meme langage graphique que l'ecran de combat.
 */
public final class GuiVisuels {

    private GuiVisuels() {}

    public static Color couleurRarete(String rarete) {
        return switch (rarete == null ? "" : rarete.toUpperCase()) {
            case "UR"  -> Color.web("#e056c9");
            case "SSS" -> Color.web("#e0393c");
            case "SS"  -> Color.web("#e08a3c");
            case "S"   -> Color.web("#f2c14e");
            case "A"   -> Color.web("#b565d8");
            case "B"   -> Color.web("#4ea8f2");
            case "C"   -> Color.web("#7ed9a3");
            default    -> Color.web("#9a9ac0");
        };
    }

    public static String hex(Color c) {
        return String.format("#%02x%02x%02x",
                (int) (c.getRed() * 255), (int) (c.getGreen() * 255), (int) (c.getBlue() * 255));
    }

    /** Badge colore compact affichant la rarete ("S", "A", "SS", ...). */
    public static Label creerBadgeRarete(String rarete) {
        Label badge = new Label(rarete == null ? "?" : rarete.toUpperCase());
        badge.setStyle(
                "-fx-font-size: 12px; -fx-font-weight: bold;"
              + "-fx-text-fill: #12121c;"
              + "-fx-background-color: " + hex(couleurRarete(rarete)) + ";"
              + "-fx-background-radius: 4;"
              + "-fx-padding: 1 6 1 6;");
        return badge;
    }

    /** Barre horizontale generique, remplie selon ratio (0-1) avec la couleur donnee. */
    public static StackPane creerBarre(double largeur, double hauteur, double ratio, Color couleur) {
        double r = Math.max(0, Math.min(1, ratio));
        Rectangle fond = new Rectangle(largeur, hauteur, Color.web("#12121c"));
        Rectangle remplissage = new Rectangle(largeur * r, hauteur, couleur);
        StackPane pane = new StackPane(fond, remplissage);
        StackPane.setAlignment(fond, Pos.CENTER_LEFT);
        StackPane.setAlignment(remplissage, Pos.CENTER_LEFT);
        pane.setPrefSize(largeur, hauteur);
        pane.setMaxWidth(largeur);
        return pane;
    }

    private static Color couleurPV(double ratio) {
        if (ratio > 0.5) return Color.web("#56c98a");
        if (ratio > 0.25) return Color.web("#f2c14e");
        return Color.web("#e05656");
    }

    /** Barre de PV (verte/jaune/rouge selon le ratio) avec le texte "X / Y" superpose. */
    public static StackPane creerBarrePV(double largeur, double hauteur, double vie, double vieMax) {
        double ratio = vieMax > 0 ? vie / vieMax : 0;
        StackPane barre = creerBarre(largeur, hauteur, ratio, couleurPV(ratio));
        Label texte = new Label((int) Math.ceil(vie) + " / " + (int) Math.ceil(vieMax));
        texte.setStyle("-fx-font-size: 11px; -fx-text-fill: white;");
        barre.getChildren().add(texte);
        return barre;
    }

    /** Barre d'XP (bleue) avec le texte "X / Y" superpose. */
    public static StackPane creerBarreXP(double largeur, double hauteur, int xp, int xpMax) {
        double ratio = xpMax > 0 ? (double) xp / xpMax : 0;
        StackPane barre = creerBarre(largeur, hauteur, ratio, Color.web("#4ea8f2"));
        Label texte = new Label(xp + " / " + xpMax);
        texte.setStyle("-fx-font-size: 11px; -fx-text-fill: white;");
        barre.getChildren().add(texte);
        return barre;
    }

    /** Barre de progression generique (orange) avec le texte "X / Y" superpose (ex : fragments). */
    public static StackPane creerBarreProgression(double largeur, double hauteur, int valeur, int max) {
        double ratio = max > 0 ? (double) valeur / max : 0;
        StackPane barre = creerBarre(largeur, hauteur, ratio, Color.web("#f2c14e"));
        Label texte = new Label(valeur + " / " + max);
        texte.setStyle("-fx-font-size: 11px; -fx-text-fill: white;");
        barre.getChildren().add(texte);
        return barre;
    }

    /** Petite fiche compacte "valeur en grand + libelle" (ex : ressources, statistiques de hub). */
    public static Node creerFicheStat(String label, String valeur) {
        Label v = new Label(valeur);
        v.getStyleClass().add("item-nom");
        v.setWrapText(true);
        v.setMaxWidth(160);
        v.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label l = new Label(label);
        l.getStyleClass().add("item-detail");
        l.setWrapText(true);
        l.setMaxWidth(160);
        l.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        VBox box = new VBox(2, v, l);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("carte-item");
        box.setMinWidth(130);
        return box;
    }

    /** Carte cliquable "titre + description" pour un menu de navigation (ex : hub Arene/Tirages). */
    public static Node creerCarteChoix(String titre, String description, javafx.event.EventHandler<javafx.scene.input.MouseEvent> action) {
        Label titreLabel = new Label(titre);
        titreLabel.getStyleClass().add("item-nom");

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("item-detail");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(300);

        VBox texte = new VBox(4, titreLabel, descLabel);
        HBox carte = new HBox(texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(360);
        carte.setCursor(javafx.scene.Cursor.HAND);
        carte.setOnMouseClicked(action);
        return carte;
    }

    /**
     * Fenetre modale de selection parmi une liste d'options affichees en cartes
     * (remplace les ChoiceDialog a menu deroulant). Retourne l'option choisie,
     * ou null si la fenetre est fermee/annulee.
     */
    public static <T> T choisirParmiCartes(String titre, List<T> options, Function<T, Node> carteBuilder) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(titre);

        AtomicReference<T> resultat = new AtomicReference<>();

        FlowPane grille = new FlowPane(10, 10);
        grille.setAlignment(Pos.CENTER);
        for (T option : options) {
            Node carte = carteBuilder.apply(option);
            carte.setCursor(Cursor.HAND);
            carte.setOnMouseClicked(e -> {
                resultat.set(option);
                dialogStage.close();
            });
            grille.getChildren().add(carte);
        }

        ScrollPane scroll = new ScrollPane(grille);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        scroll.setStyle("-fx-background-color: transparent;");
        scroll.setPrefViewportWidth(560);
        scroll.setPrefViewportHeight(Math.min(420, 100.0 * ((options.size() + 1) / 2)));

        Label titreLabel = new Label(titre);
        titreLabel.getStyleClass().add("titre");
        titreLabel.setStyle("-fx-font-size: 22px;");

        Button annuler = new Button("Annuler");
        annuler.getStyleClass().add("menu-bouton");
        annuler.setOnAction(e -> dialogStage.close());

        VBox root = new VBox(14, titreLabel, scroll, annuler);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root-menu");
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(GuiVisuels.class.getResource("/fxml/style.css").toExternalForm());
        dialogStage.setScene(scene);
        dialogStage.showAndWait();

        return resultat.get();
    }

    /** Affiche les resultats d'un tirage gacha sous forme de cartes (au lieu d'un texte brut). */
    public static void afficherResultatsTirage(String titre, List<LigneResultat> lignes) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(titre);
        dialog.getDialogPane().getStylesheets().add(GuiVisuels.class.getResource("/fxml/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("root-menu");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        FlowPane grille = new FlowPane(10, 10);
        grille.setPrefWrapLength(520);
        for (LigneResultat l : lignes) {
            grille.getChildren().add(carteResultatTirage(l));
        }

        ScrollPane scroll = new ScrollPane(grille);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportWidth(540);
        scroll.setPrefViewportHeight(Math.min(360, 90.0 * ((lignes.size() + 1) / 2)));
        scroll.getStyleClass().add("scroll-pane");
        scroll.setStyle("-fx-background-color: transparent;");

        dialog.getDialogPane().setContent(scroll);
        dialog.showAndWait();
    }

    private static Node carteResultatTirage(LigneResultat l) {
        ResultatTirage r = l.resultat;

        Label badge = creerBadgeRarete(r.rarete);
        Label nomLabel = new Label(r.nom);
        nomLabel.getStyleClass().add("item-nom");

        boolean nouveauPerso = !r.estFragments && !l.doublon;
        String detail;
        if (r.estFragments) {
            detail = "+" + r.quantiteFragments + " fragment" + (r.quantiteFragments > 1 ? "s" : "");
        } else if (l.doublon) {
            detail = "Doublon → fragments";
        } else {
            detail = "Nouveau personnage !";
        }
        Label detailLabel = new Label(detail);
        detailLabel.getStyleClass().add(nouveauPerso ? "item-qte" : "item-detail");

        VBox texte = new VBox(4, nomLabel, detailLabel);
        HBox carte = new HBox(10, badge, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add(nouveauPerso ? "carte-item-joueur" : "carte-item");
        carte.setPrefWidth(240);
        return carte;
    }

    /**
     * Capture le texte imprime sur System.out par une methode de description
     * (ex: PersonnageBase.descriptionAttaqueSpeciale, Competences.descriptionUltime)
     * pour l'afficher dans un Label plutot que sur la console.
     */
    public static String capturerDescription(Runnable description) {
        PrintStream ancien = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            description.run();
        } finally {
            System.setOut(ancien);
        }
        return buffer.toString().trim();
    }
}
