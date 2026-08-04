package lancement.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.Chapitres.Chapitre;
import lancement.Chapitres.CourbeChapitres;
import lancement.ChapitreElite.ChapitreElite;
import lancement.GameContext;

public class EcranHistoireController {

    private GameContext ctx;

    @FXML private VBox choixBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        GuiVisuels.afficherExplicationPremiereVisite(ctx, "Histoire", "Histoire");

        choixBox.getChildren().setAll(
                GuiVisuels.creerCarteChoix("Chapitres",
                        "L'histoire principale, du réveil à Phantom Lord.", this::onChapitres),
                GuiVisuels.creerCarteChoix("Chapitres Elite",
                        "Versions renforcées débloquées après chaque chapitre.", this::onChapitresElite)
        );
    }

    private void onChapitres(MouseEvent event) {
        List<LigneChapitre> lignes = new ArrayList<>();
        for (int i = 1; i <= CourbeChapitres.NB_CHAPITRES_VISIBLES; i++) {
            Chapitre c = ctx.chapitres.get(i - 1);
            boolean sequentiel = (i == 1) || ctx.chapitres.get(i - 2).getStagesReussis()[10];
            int niveauRequis = CourbeChapitres.niveauRequis(i);
            boolean niveauSuffisant = ctx.joueur.getNiveau() >= niveauRequis;
            boolean deverrouille = sequentiel && niveauSuffisant;
            String messageVerrouille = !sequentiel
                    ? "Terminez le Chapitre " + (i - 1) + " pour debloquer."
                    : !niveauSuffisant
                        ? "Necessite le niveau " + niveauRequis + "."
                        : null;
            lignes.add(new LigneChapitre("Chapitre " + i + " - " + c.getNomChapitre(), deverrouille, messageVerrouille,
                    i, false, c::getStagesReussis, c::getStagesDebloques, c::getTitreStage, c::lancerStage));
        }

        naviguerVersListe(event, "CHAPITRES", lignes);
    }

    private void onChapitresElite(MouseEvent event) {
        List<LigneChapitre> lignes = new ArrayList<>();
        for (int i = 1; i <= CourbeChapitres.NB_CHAPITRES_ELITE_VISIBLES; i++) {
            ChapitreElite c = ctx.chapitresElite.get(i - 1);
            String messageVerrouille = (i == 1)
                    ? "Terminez le Chapitre 1 pour debloquer."
                    : "Terminez C" + i + " et C" + (i - 1) + " Elite pour debloquer.";
            lignes.add(new LigneChapitre("Chapitre " + i + " Elite", c.estDebloque(), messageVerrouille,
                    i, true, c::getStagesReussis, c::getStagesDebloques, c::getTitreStage, c::lancerStage));
        }

        naviguerVersListe(event, "CHAPITRES ELITE", lignes);
    }

    private void naviguerVersListe(MouseEvent event, String titre, List<LigneChapitre> lignes) {
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
