package lancement.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * Cinematique jouee a la fin d'un stage reussi (avant de revenir a l'ecran des stages). Se
 * termine automatiquement a la fin de la video, ou immediatement via le bouton "Passer".
 * Generalisation d'EcranIntroVideoController : chemin de video et suite variables au lieu
 * d'etre cables en dur vers le menu principal.
 */
public class EcranFinStageVideoController {

    @FXML private MediaView mediaView;
    @FXML private Button boutonPasser;

    private MediaPlayer lecteur;
    private Runnable onTermine;
    private boolean navigationLancee = false;

    /**
     * @param cheminVideo chemin classpath de la video (ex: "/video/chapitre1/stage3_chap1.mp4")
     * @param onTermine   suite a executer a la fin de la video ou apres "Passer"
     */
    public void initData(String cheminVideo, Runnable onTermine) {
        this.onTermine = onTermine;
        // Coupe toute musique encore en cours : sinon elle se superpose a la bande-son de la video.
        GestionnaireMusique.arreter();
        try {
            var url = EcranFinStageVideoController.class.getResource(cheminVideo);
            if (url == null) {
                terminer();
                return;
            }
            lecteur = new MediaPlayer(new Media(url.toExternalForm()));
            mediaView.setMediaPlayer(lecteur);
            lecteur.setOnEndOfMedia(this::terminer);
            lecteur.setOnError(this::terminer);
            lecteur.play();
        } catch (Exception e) {
            terminer();
        }
    }

    @FXML
    private void onPasser(ActionEvent event) {
        terminer();
    }

    private void terminer() {
        if (navigationLancee) return;
        navigationLancee = true;
        if (lecteur != null) lecteur.stop();
        onTermine.run();
    }
}
