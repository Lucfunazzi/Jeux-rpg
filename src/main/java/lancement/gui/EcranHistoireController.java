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

    /** Construit la LigneChapitre d'un chapitre normal donne (1 a NB_CHAPITRES_VISIBLES),
     *  reutilisable par EcranStagesController pour la navigation par fleches gauche/droite. */
    public static LigneChapitre construireLigneChapitre(GameContext ctx, int numero) {
        Chapitre c = ctx.chapitres.get(numero - 1);
        boolean sequentiel = (numero == 1) || ctx.chapitres.get(numero - 2).getStagesReussis()[10];
        int niveauRequis = CourbeChapitres.niveauRequis(numero);
        boolean niveauSuffisant = ctx.joueur.getNiveau() >= niveauRequis;
        boolean deverrouille = sequentiel && niveauSuffisant;
        String messageVerrouille = !sequentiel
                ? "Terminez le Chapitre " + (numero - 1) + " pour debloquer."
                : !niveauSuffisant
                    ? "Necessite le niveau " + niveauRequis + "."
                    : null;
        return new LigneChapitre("Chapitre " + numero + " - " + c.getNomChapitre(), deverrouille, messageVerrouille,
                numero, false, c::getStagesReussis, c::getStagesDebloques, c::getTitreStage, c::lancerStage);
    }

    /** Chapitre normal a afficher par defaut : le premier chapitre debloque et pas encore
     *  entierement termine (stage 10 non reussi) — c'est le Chapitre 1 en debut de partie, et le
     *  chapitre "en cours" ensuite. Si tous les chapitres visibles sont finis, reste sur le dernier. */
    public static int chapitreEnCours(GameContext ctx) {
        for (int i = 1; i <= CourbeChapitres.NB_CHAPITRES_VISIBLES; i++) {
            if (!ctx.chapitres.get(i - 1).getStagesReussis()[10]) return i;
        }
        return CourbeChapitres.NB_CHAPITRES_VISIBLES;
    }

    /** Va directement sur l'ecran des stages du chapitre en cours (au lieu de la liste des 10
     *  chapitres) : avec la navigation par fleches gauche/droite de EcranStagesController, passer
     *  par la liste intermediaire n'apporte plus rien pour les chapitres normaux. */
    private void onChapitres(MouseEvent event) {
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
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranStages.fxml");
            EcranStagesController controller = loader.getController();
            controller.initData(ctx, construireLigneChapitre(ctx, chapitreEnCours(ctx)), retour);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
