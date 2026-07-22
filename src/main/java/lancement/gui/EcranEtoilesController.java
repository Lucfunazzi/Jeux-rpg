package lancement.gui;

import Equipement.Inventaire;
import Personnage.PersonnageBase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireEtoilesPerso;
import lancement.Menus.MenuEtoilesPerso;

public class EcranEtoilesController {

    private GameContext ctx;

    @FXML private TextArea fragmentsArea;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichir();
    }

    private void rafraichir() {
        Inventaire inv = ctx.inventaire;
        StringBuilder sb = new StringBuilder();
        sb.append("Format : nom [rang] barre qte/cout_recrutement\n\n");

        boolean aucun = true;
        for (String[] info : MenuEtoilesPerso.getCatalogue()) {
            String nom = info[0], rarete = info[1];
            int qte = GestionnaireEtoilesPerso.getFragments(inv, nom);
            if (qte == 0) continue;
            aucun = false;
            int cout = GestionnaireEtoilesPerso.coutFragmentsRecrutement(rarete);
            String barre = GestionnaireEtoilesPerso.barreFragments(qte, cout);
            boolean recrute = GestionnaireEtoilesPerso.dejaRecruteParNom(ctx.personnagesRecruites, nom);
            sb.append(String.format("  %-20s [%2s] %s %3d/%-3d%s%n",
                    nom, rarete, barre, qte, cout, recrute ? " [RECRUTE]" : ""));
        }
        if (aucun) {
            sb.append("Vous n'avez aucun fragment de personnage pour l'instant.\n");
            sb.append("Obtenez-en dans le mode Recrutement par Fragments !");
        }
        fragmentsArea.setText(sb.toString());
    }

    @FXML
    private void onRecruter(ActionEvent event) {
        Inventaire inv = ctx.inventaire;
        List<String[]> prets = new ArrayList<>();

        for (String[] info : MenuEtoilesPerso.getCatalogue()) {
            String nom = info[0], rarete = info[1];
            if (GestionnaireEtoilesPerso.dejaRecruteParNom(ctx.personnagesRecruites, nom)) continue;
            int qte  = GestionnaireEtoilesPerso.getFragments(inv, nom);
            int cout = GestionnaireEtoilesPerso.coutFragmentsRecrutement(rarete);
            if (qte >= cout) prets.add(info);
        }

        if (prets.isEmpty()) { info("Recrutement", "Aucun personnage recrutable pour l'instant (fragments insuffisants)."); return; }

        Map<String, String[]> map = new LinkedHashMap<>();
        List<String> libelles = new ArrayList<>();
        for (String[] p : prets) {
            String libelle = p[0] + " [" + p[1] + "]";
            libelles.add(libelle);
            map.put(libelle, p);
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(libelles.get(0), libelles);
        dialog.setTitle("Recrutement par fragments");
        dialog.setHeaderText(null);
        dialog.setContentText("Personnage :");
        styliser(dialog);
        Optional<String> resultat = dialog.showAndWait();
        if (resultat.isEmpty()) return;

        String[] cible = map.get(resultat.get());
        GestionnaireEtoilesPerso.recruterViaFragments(inv, cible[0], cible[1]);
        PersonnageBase nouveau = ctx.sauvegarde.creerPersonnageParNom(cible[0]);
        if (nouveau != null) {
            ctx.personnagesRecruites.add(nouveau);
            ctx.sauvegarde.sauvegarder(ctx);
            info("Recrutement", cible[0] + " [" + cible[1] + "] a rejoint l'equipe !\n"
                    + GestionnaireEtoilesPerso.coutFragmentsRecrutement(cible[1]) + " fragments consommes.");
        } else {
            info("Recrutement", "Erreur : personnage introuvable.");
        }
        rafraichir();
    }

    @FXML
    private void onMonterEtoile(ActionEvent event) {
        List<PersonnageBase> tous = new ArrayList<>();
        tous.add(ctx.joueur);
        tous.addAll(ctx.personnagesRecruites);

        List<PersonnageBase> eligibles = new ArrayList<>();
        for (PersonnageBase p : tous) if (p.getNbreEtoiles() < 5) eligibles.add(p);

        if (eligibles.isEmpty()) { info("Etoiles", "Tous vos personnages sont a 5 etoiles !"); return; }

        Map<String, PersonnageBase> map = new LinkedHashMap<>();
        List<String> libelles = new ArrayList<>();
        for (PersonnageBase p : eligibles) {
            int etoiles   = p.getNbreEtoiles();
            int cout      = GestionnaireEtoilesPerso.coutFragmentsEtoile(p.getRarete(), etoiles);
            int fragments = GestionnaireEtoilesPerso.getFragments(ctx.inventaire, p.getNom());
            String etoilesStr = "*".repeat(etoiles) + ".".repeat(5 - etoiles);
            String libelle = p.getNom() + "  " + etoilesStr + "  Frag: " + fragments + "/" + cout
                    + (fragments >= cout ? " [PRET]" : "");
            libelles.add(libelle);
            map.put(libelle, p);
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(libelles.get(0), libelles);
        dialog.setTitle("Montee en etoile");
        dialog.setHeaderText(null);
        dialog.setContentText("Personnage :");
        styliser(dialog);
        Optional<String> resultat = dialog.showAndWait();
        if (resultat.isEmpty()) return;

        PersonnageBase cible = map.get(resultat.get());
        int avant = cible.getNbreEtoiles();
        boolean succes = GestionnaireEtoilesPerso.monterEtoileViaFragments(ctx.inventaire, cible);

        if (!succes) {
            int cout = GestionnaireEtoilesPerso.coutFragmentsEtoile(cible.getRarete(), avant);
            info("Etoiles", "Fragments insuffisants. Il vous faut " + cout + " fragments de " + cible.getNom() + ".");
            return;
        }

        int apres = cible.getNbreEtoiles();
        ctx.sauvegarde.sauvegarder(ctx);
        info("Etoiles", cible.getNom() + " passe a " + apres + " etoile(s) !\n"
                + "Bonus stats : +" + (apres * 5) + "% ATK/DEF/PV/VIT");
        rafraichir();
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

    private void info(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        styliser(alert);
        alert.showAndWait();
    }

    private void styliser(Dialog<?> dialog) {
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("root-menu");
    }
}
