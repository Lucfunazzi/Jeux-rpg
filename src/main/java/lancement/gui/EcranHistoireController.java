package lancement.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.stage.Stage;
import lancement.GameContext;

public class EcranHistoireController {

    private GameContext ctx;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
    }

    @FXML
    private void onChapitres(ActionEvent event) {
        boolean c1Fini = ctx.chapitre1.getStagesReussis()[10];
        boolean c2Fini = ctx.chapitre2.getStagesReussis()[10];

        List<LigneChapitre> lignes = new ArrayList<>();
        lignes.add(new LigneChapitre("Chapitre 1 - L'Eveil", true, null,
                1, false, ctx.chapitre1::getStagesReussis, ctx.chapitre1::getStagesDebloques, ctx.chapitre1::getTitreStage, ctx.chapitre1::lancerStage));
        lignes.add(new LigneChapitre("Chapitre 2 - L'Ile de Galuna", c1Fini, "Terminez le Chapitre 1 pour debloquer.",
                2, false, ctx.chapitre2::getStagesReussis, ctx.chapitre2::getStagesDebloques, ctx.chapitre2::getTitreStage, ctx.chapitre2::lancerStage));
        lignes.add(new LigneChapitre("Chapitre 3 - Phantom Lord", c2Fini, "Terminez le Chapitre 2 pour debloquer.",
                3, false, ctx.chapitre3::getStagesReussis, ctx.chapitre3::getStagesDebloques, ctx.chapitre3::getTitreStage, ctx.chapitre3::lancerStage));

        naviguerVersListe(event, "CHAPITRES", lignes);
    }

    @FXML
    private void onChapitresElite(ActionEvent event) {
        boolean c1Fini = ctx.chapitre1.getStagesReussis()[10];

        List<LigneChapitre> lignes = new ArrayList<>();
        lignes.add(new LigneChapitre("Chapitre 1 Elite", c1Fini, "Terminez le Chapitre 1 pour debloquer.",
                1, true, ctx.chapitre1Elite::getStagesReussis, ctx.chapitre1Elite::getStagesDebloques, ctx.chapitre1Elite::getTitreStage, ctx.chapitre1Elite::lancerStage));
        lignes.add(new LigneChapitre("Chapitre 2 Elite", ctx.chapitre2Elite.estDebloque(), "Terminez C1, C2 et C1 Elite pour debloquer.",
                2, true, ctx.chapitre2Elite::getStagesReussis, ctx.chapitre2Elite::getStagesDebloques, ctx.chapitre2Elite::getTitreStage, ctx.chapitre2Elite::lancerStage));
        lignes.add(new LigneChapitre("Chapitre 3 Elite", ctx.chapitre3Elite.estDebloque(), "Terminez C3 et C2 Elite pour debloquer.",
                3, true, ctx.chapitre3Elite::getStagesReussis, ctx.chapitre3Elite::getStagesDebloques, ctx.chapitre3Elite::getTitreStage, ctx.chapitre3Elite::lancerStage));

        naviguerVersListe(event, "CHAPITRES ELITE", lignes);
    }

    private void naviguerVersListe(ActionEvent event, String titre, List<LigneChapitre> lignes) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Runnable retour = () -> {
            try {
                FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranHistoire.fxml");
                EcranHistoireController controller = loader.getController();
                controller.initData(ctx);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        try {
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranListeChapitres.fxml");
            EcranListeChapitresController controller = loader.getController();
            controller.initData(ctx, titre, lignes, retour);
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
}
