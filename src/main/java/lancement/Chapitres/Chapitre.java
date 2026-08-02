package lancement.Chapitres;

import java.util.Scanner;
import lancement.GameContext;
import lancement.Stage;

/** Forme commune a tous les chapitres normaux (1-13), pour permettre un cablage generique. */
public interface Chapitre {
    void afficher(GameContext ctx, Scanner scanner);
    Stage.ResultatStage lancerStage(GameContext ctx, int numero);
    String getTitreStage(int numero);
    String getNomChapitre();
    boolean[] getStagesDebloques();
    boolean[] getStagesReussis();
    void setStagesDebloques(boolean[] d);
    void setStagesReussis(boolean[] r);
}
