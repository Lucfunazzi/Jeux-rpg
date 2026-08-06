package lancement.gui;

import java.io.IOException;
import java.util.Scanner;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Navigation {

    private static final String ECRAN_COMBAT = "/fxml/EcranCombat.fxml";
    private static final String ECRAN_INTRO_VIDEO = "/fxml/EcranIntroVideo.fxml";
    private static final String ECRAN_ACCUEIL = "/fxml/EcranAccueil.fxml";
    private static final String ECRAN_CHOIX_CLASSE = "/fxml/EcranChoixClasse.fxml";
    private static final String ECRAN_FICHE_CLASSE = "/fxml/EcranFicheClasse.fxml";

    /** Ecrans avec leur propre bande-son (pas de musique de menu generique automatique). */
    private static final java.util.Set<String> ECRANS_MUSIQUE_DEDIEE = java.util.Set.of(
            ECRAN_COMBAT, ECRAN_INTRO_VIDEO, ECRAN_ACCUEIL, ECRAN_CHOIX_CLASSE, ECRAN_FICHE_CLASSE);

    private Navigation() {}

    public static FXMLLoader changerEcran(Stage stage, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(Navigation.class.getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = stage.getScene();
        scene.setRoot(root);

        // Musique d'ambiance des menus : automatique sur tout ecran qui n'a pas sa propre
        // bande-son dediee (combat, cinematique d'intro, accueil, creation de personnage).
        if (!ECRANS_MUSIQUE_DEDIEE.contains(fxmlPath)) {
            GestionnaireMusique.jouerMusiqueMenuAuHasard();
        }
        return loader;
    }

    /**
     * Scanner "silencieux" pour les methodes console qui attendent une touche Entree
     * (ex: Combat.lancerCombatArene) mais qu'on appelle depuis l'interface graphique,
     * ou il n'y a pas d'entree utilisateur reelle a fournir.
     */
    public static Scanner scannerSilencieux() {
        return new Scanner("\n".repeat(50));
    }
}
