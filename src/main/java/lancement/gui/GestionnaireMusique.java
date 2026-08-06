package lancement.gui;

import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * Musique de fond jouee en combat. Chargee paresseusement (comme les sons d'attaque de
 * EcranCombatController) pour ne pas echouer si le fichier manque ou si aucune musique
 * n'est encore fournie : dans ce cas le combat reste simplement silencieux.
 *
 * Fichiers attendus (non tous fournis avec le code), voir audio/musique/LISEZMOI.txt :
 *  - /audio/musique/chapitreN.mp3   (N = 1 a 13, chapitres normaux ET elites)
 *  - /audio/musique/examenSN.mp3    (N = 1 a 4, une piste par tranche de 10 stages)
 *  - /audio/musique/donjonN.mp3     (N = 1 a 5, tiree au hasard)
 *  - /audio/musique/areneN.mp3      (N = 1 a 5, tiree au hasard)
 *  - /audio/musique/menuN.mp3       (N = 1 a 3, tiree au hasard, jouee dans tous les menus en jeu)
 *  - /audio/musique/accueil.mp3     (ecran d'accueil : nouvelle partie / charger la partie)
 *  - /audio/musique/creation.mp3    (creation du personnage : choix de classe + fiche de classe)
 */
public final class GestionnaireMusique {

    private static final double VOLUME_CIBLE = 0.26;
    private static final double VOLUME_ATTENUEE = 0.10;
    private static final Duration DUREE_FONDU = Duration.millis(700);
    private static final Duration DUREE_FONDU_ATTENUATION = Duration.millis(150);

    private static final int NB_PISTES_EXAMEN_S = 4;
    // Un seul fichier fourni pour l'instant (donjon1/arene1) : augmenter cette constante et
    // ajouter donjonN.wav|mp3 / areneN.wav|mp3 quand d'autres pistes seront fournies.
    private static final int NB_PISTES_DONJON = 1;
    private static final int NB_PISTES_ARENE = 1;
    private static final int NB_PISTES_MENU = 3;
    private static final String PREFIXE_MENU = "menu";
    private static final int STAGES_PAR_TRANCHE_EXAMEN_S = 10;
    private static final Random ALEATOIRE = new Random();

    private static MediaPlayer lecteurActuel;
    private static String cleActuelle;
    private static SequentialTransition attenuationEnCours;

    private GestionnaireMusique() {}

    /** Lance la musique du chapitre donne (ne relance pas si c'est deja celle en cours).
     *  Les chapitres elites reutilisent la meme piste que le chapitre normal correspondant. */
    public static void jouerMusiqueChapitre(int numeroChapitre) {
        jouer("chapitre" + numeroChapitre, "/audio/musique/chapitre" + numeroChapitre);
    }

    /** Musique de l'Examen de Rang S : une piste par tranche de 10 stages (1-10, 11-20, 21-30, 31-40). */
    public static void jouerMusiqueExamenS(int numeroStage) {
        int tranche = Math.min(NB_PISTES_EXAMEN_S, (numeroStage - 1) / STAGES_PAR_TRANCHE_EXAMEN_S + 1);
        jouer("examenS" + tranche, "/audio/musique/examenS" + tranche);
    }

    /** Musique de donjon, tiree au hasard parmi les pistes disponibles a chaque lancement. */
    public static void jouerMusiqueDonjonAuHasard() {
        int piste = 1 + ALEATOIRE.nextInt(NB_PISTES_DONJON);
        jouer("donjon" + piste, "/audio/musique/donjon" + piste);
    }

    /** Musique d'arene, tiree au hasard parmi les pistes disponibles a chaque lancement. */
    public static void jouerMusiqueAreneAuHasard() {
        int piste = 1 + ALEATOIRE.nextInt(NB_PISTES_ARENE);
        jouer("arene" + piste, "/audio/musique/arene" + piste);
    }

    /** Musique d'ambiance des menus (hors combat) : une piste tiree au hasard, qui continue de
     *  jouer sans se relancer tant qu'on navigue entre les menus. Une nouvelle piste n'est tiree
     *  que si aucun theme de menu n'est deja en cours (ex. juste apres un combat). */
    public static void jouerMusiqueMenuAuHasard() {
        if (cleActuelle != null && cleActuelle.startsWith(PREFIXE_MENU)) return;
        int piste = 1 + ALEATOIRE.nextInt(NB_PISTES_MENU);
        jouer(PREFIXE_MENU + piste, "/audio/musique/menu" + piste);
    }

    /** Musique de l'ecran d'accueil (nouvelle partie / charger la partie), distincte de la
     *  musique des menus en jeu. */
    public static void jouerMusiqueAccueil() {
        jouer("accueil", "/audio/musique/accueil");
    }

    /** Musique de la creation de personnage (choix de classe + fiche de classe), distincte de
     *  la musique des menus en jeu. */
    public static void jouerMusiqueCreationPersonnage() {
        jouer("creation", "/audio/musique/creation");
    }

    /** Extensions essayees dans l'ordre pour chaque piste (mp3 prioritaire, wav en repli
     *  pour les pistes fournies non converties, ex. l'arene). */
    private static final String[] EXTENSIONS = {".mp3", ".wav"};

    private static void jouer(String cle, String cheminRessourceSansExtension) {
        if (cle.equals(cleActuelle) && lecteurActuel != null) return;

        java.net.URL url = null;
        for (String ext : EXTENSIONS) {
            url = GestionnaireMusique.class.getResource(cheminRessourceSansExtension + ext);
            if (url != null) break;
        }
        if (url == null) return;

        arreter();
        cleActuelle = cle;
        try {
            MediaPlayer lecteur = new MediaPlayer(new Media(url.toExternalForm()));
            lecteur.setCycleCount(MediaPlayer.INDEFINITE);
            lecteur.setVolume(0);
            lecteur.play();
            lecteurActuel = lecteur;
            fondu(lecteur, 0, volumeCibleEffectif(), null);
        } catch (Exception e) {
            System.err.println("[Musique] Impossible de charger " + url + " : " + e.getMessage());
        }
    }

    /** Volume plein (hors attenuation) tenant compte du reglage utilisateur et du mute. */
    private static double volumeCibleEffectif() {
        return ParametresAudio.isMuet() ? 0 : VOLUME_CIBLE * ParametresAudio.getVolumeMusique();
    }

    /** Volume attenue (pendant une speciale/un ultime) tenant compte du reglage utilisateur et du mute. */
    private static double volumeAttenueEffectif() {
        return ParametresAudio.isMuet() ? 0 : VOLUME_ATTENUEE * ParametresAudio.getVolumeMusique();
    }

    /** Applique immediatement le volume courant (suite a un changement de reglage depuis les
     *  Options), sans relancer la piste. Appele par ParametresAudio. */
    public static void appliquerVolume() {
        if (lecteurActuel != null) lecteurActuel.setVolume(volumeCibleEffectif());
    }

    /** Coupe la musique en cours avec un fondu de sortie. */
    public static void arreter() {
        if (attenuationEnCours != null) {
            attenuationEnCours.stop();
            attenuationEnCours = null;
        }
        MediaPlayer lecteur = lecteurActuel;
        lecteurActuel = null;
        cleActuelle = null;
        if (lecteur == null) return;
        fondu(lecteur, lecteur.getVolume(), 0, lecteur::stop);
    }

    /** Baisse temporairement la musique pendant qu'un son d'attaque speciale/ultime joue (pour
     *  eviter que les deux se superposent trop fort), puis la remonte au volume normal. */
    public static void attenuerPendant(Duration duree) {
        MediaPlayer lecteur = lecteurActuel;
        if (lecteur == null) return;
        if (attenuationEnCours != null) attenuationEnCours.stop();

        Timeline descente = new Timeline(new KeyFrame(DUREE_FONDU_ATTENUATION,
                new KeyValue(lecteur.volumeProperty(), volumeAttenueEffectif())));
        Timeline remontee = new Timeline(new KeyFrame(DUREE_FONDU,
                new KeyValue(lecteur.volumeProperty(), volumeCibleEffectif())));

        attenuationEnCours = new SequentialTransition(descente, new PauseTransition(duree), remontee);
        attenuationEnCours.play();
    }

    private static void fondu(MediaPlayer lecteur, double debut, double fin, Runnable surFin) {
        Timeline fondu = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(lecteur.volumeProperty(), debut)),
                new KeyFrame(DUREE_FONDU, new KeyValue(lecteur.volumeProperty(), fin)));
        if (surFin != null) fondu.setOnFinished(e -> surFin.run());
        fondu.play();
    }
}
