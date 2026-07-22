package lancement.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import lancement.GameContext;
import lancement.Gestionnaires.AreneData;
import lancement.Gestionnaires.GestionnaireArene;

public class EcranAreneClassementController {

    private Runnable onRetour;

    @FXML private TextArea classementArea;

    public void initData(GameContext ctx, GestionnaireArene gestionnaireArene, AreneData joueurArene, Runnable onRetour) {
        this.onRetour = onRetour;

        java.util.List<AreneData> adversaires = gestionnaireArene.getAdversairesVisibles(joueurArene.getRang());

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("#%-5d %-22s %10d pts%n", joueurArene.getRang(), "-> " + joueurArene.getPseudo(), joueurArene.getPointsArene()));
        sb.append("--------------------------------------------------\n");
        for (AreneData a : adversaires) {
            sb.append(String.format("#%-5d %-22s %10d pts%s%n",
                    a.getRang(), a.getPseudo(), a.getPointsArene(), a.isEstFauxJoueur() ? "" : "  *"));
        }
        sb.append("\n* = vrai joueur");
        classementArea.setText(sb.toString());
    }

    @FXML
    private void onRetour() {
        onRetour.run();
    }
}
