package lancement.gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import lancement.GameContext;

/**
 * Cinematique jouee une fois, juste apres la creation du personnage principal et avant
 * l'arrivee sur le menu principal. Se termine automatiquement a la fin de la video, ou
 * immediatement via le bouton "Passer".
 */
public class EcranIntroVideoController {

    private static final String CHEMIN_VIDEO = "/video/intro.mp4";

    @FXML private MediaView mediaView;
    @FXML private Button boutonPasser;

    private GameContext ctx;
    private MediaPlayer lecteur;
    private boolean navigationLancee = false;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        // Coupe toute musique de menu/creation encore en cours : sinon elle se superpose a la
        // bande-son de la video et la rend inaudible.
        GestionnaireMusique.arreter();
        try {
            var url = EcranIntroVideoController.class.getResource(CHEMIN_VIDEO);
            if (url == null) {
                allerVersMenuPrincipal();
                return;
            }
            lecteur = new MediaPlayer(new Media(url.toExternalForm()));
            mediaView.setMediaPlayer(lecteur);
            lecteur.setOnEndOfMedia(this::allerVersMenuPrincipal);
            lecteur.setOnError(this::allerVersMenuPrincipal);
            lecteur.play();
        } catch (Exception e) {
            allerVersMenuPrincipal();
        }
    }

    @FXML
    private void onPasser(ActionEvent event) {
        allerVersMenuPrincipal();
    }

    private void allerVersMenuPrincipal() {
        if (navigationLancee) return;
        navigationLancee = true;
        if (lecteur != null) lecteur.stop();
        try {
            Stage stage = (Stage) boutonPasser.getScene().getWindow();
            var loader = Navigation.changerEcran(stage, "/fxml/EcranMenuPrincipal.fxml");
            EcranMenuPrincipalController controller = loader.getController();
            controller.initData(ctx);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
