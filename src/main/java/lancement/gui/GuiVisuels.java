package lancement.gui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Petits composants visuels partages entre les ecrans (badge de rarete, barres de PV/XP)
 * pour garder le meme langage graphique que l'ecran de combat.
 */
public final class GuiVisuels {

    private GuiVisuels() {}

    public static Color couleurRarete(String rarete) {
        return switch (rarete == null ? "" : rarete.toUpperCase()) {
            case "UR" -> Color.web("#e056c9");
            case "SS" -> Color.web("#e08a3c");
            case "S"  -> Color.web("#f2c14e");
            case "A"  -> Color.web("#b565d8");
            case "B"  -> Color.web("#4ea8f2");
            case "C"  -> Color.web("#7ed9a3");
            default   -> Color.web("#9a9ac0");
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
