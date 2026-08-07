package lancement.Gestionnaires;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import lancement.GameContext;

/**
 * Suivi du temps de jeu reellement passe dans l'application aujourd'hui, pour les paliers de
 * la Recompense quotidienne (30 min / 1h / 2h / 4h, voir GestionnaireRecompenses). Un seul
 * Timeline tourne par lancement de l'application (independant de l'ecran affiche a l'instant,
 * un Timeline JavaFX n'est pas lie a une Scene) : il incremente le compteur toutes les minutes
 * et sauvegarde la progression.
 */
public final class SuiviTempsJeu {

    private static boolean demarre = false;

    private SuiviTempsJeu() {}

    /** A appeler une fois des que le GameContext est pret (idempotent : les appels suivants ne
     *  font rien, pour supporter un controller dont initData() est rappele plusieurs fois). */
    public static void demarrer(GameContext ctx) {
        if (demarre) return;
        demarre = true;

        Timeline timeline = new Timeline(new KeyFrame(Duration.minutes(1), e -> {
            ctx.gestionnaireRecompenses.ajouterMinutesJeu(1);
            ctx.sauvegarde.sauvegarder(ctx);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
