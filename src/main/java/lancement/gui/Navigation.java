package lancement.gui;

import java.io.IOException;
import java.util.Scanner;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Navigation {

    private Navigation() {}

    public static FXMLLoader changerEcran(Stage stage, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(Navigation.class.getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = stage.getScene();
        scene.setRoot(root);
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
