package lancement.gui;

import Equipement.CarteOr;
import Equipement.Equipement;
import Equipement.EquipementFactory;
import Equipement.FragmentEquipement;
import Equipement.GestionnaireFragments;
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
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import lancement.GameContext;

public class EcranInventaireController {

    private final GestionnaireFragments gestionnaireFragments = new GestionnaireFragments();

    private GameContext ctx;

    @FXML private TextArea inventaireArea;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichir();
    }

    private void rafraichir() {
        Inventaire inv = ctx.inventaire;
        StringBuilder sb = new StringBuilder();

        sb.append("[ Equipements (").append(inv.getStacks().size()).append(" types) ]\n");
        if (inv.getStacks().isEmpty()) {
            sb.append("  Aucun equipement.\n");
        } else {
            for (Inventaire.StackEquipement s : inv.getStacks()) {
                sb.append("  ").append(s.getEquipement());
                if (s.getQuantite() > 1) sb.append(" x").append(s.getQuantite());
                sb.append("\n");
            }
        }

        sb.append("\n[ Materiaux (").append(inv.getMateriaux().size()).append(" types) ]\n");
        if (inv.getMateriaux().isEmpty()) {
            sb.append("  Aucun materiau.\n");
        } else {
            for (var m : inv.getMateriaux()) sb.append("  ").append(m).append("\n");
        }

        sb.append("\n[ Consommables ]\n");
        boolean aucunConso = inv.getParchemins().isEmpty() && inv.getCartesOr().isEmpty();
        if (aucunConso) {
            sb.append("  Aucun consommable.\n");
        } else {
            for (var s : inv.getParchemins()) sb.append("  ").append(s).append("\n");
            for (var s : inv.getCartesOr()) sb.append("  ").append(s).append("\n");
        }

        inventaireArea.setText(sb.toString().trim());
    }

    @FXML
    private void onEquiperPersonnage(ActionEvent event) {
        PersonnageBase choisi = choisirPersonnage("Equiper un personnage");
        if (choisi == null) return;

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Runnable retour = () -> {
            try {
                FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranInventaire.fxml");
                EcranInventaireController controller = loader.getController();
                controller.initData(ctx);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        try {
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranFichePersonnage.fxml");
            EcranFichePersonnageController controller = loader.getController();
            controller.initData(ctx, choisi, retour);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onEquiperSet(ActionEvent event) {
        Inventaire inv = ctx.inventaire;
        if (inv.estVide()) { info("Equiper un set", "L'inventaire est vide !"); return; }

        PersonnageBase cible = choisirPersonnage("Equiper un set complet");
        if (cible == null) return;

        List<Equipement.Rarete> raretesDisponibles = new ArrayList<>();
        for (Equipement e : inv.getEquipements()) {
            if (!raretesDisponibles.contains(e.getRarete())) raretesDisponibles.add(e.getRarete());
        }
        if (raretesDisponibles.isEmpty()) { info("Equiper un set", "Aucun equipement disponible dans l'inventaire."); return; }

        List<String> options = new ArrayList<>();
        for (Equipement.Rarete r : raretesDisponibles) options.add("Rarete " + r.name());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle("Equiper un set complet");
        dialog.setHeaderText(null);
        dialog.setContentText("Rarete a equiper sur " + cible.getNom() + " :");
        styliser(dialog);
        Optional<String> resultat = dialog.showAndWait();
        if (resultat.isEmpty()) return;

        Equipement.Rarete rareteChoisie = Equipement.Rarete.valueOf(resultat.get().replace("Rarete ", ""));

        List<Equipement> candidats = new ArrayList<>();
        for (Equipement e : inv.getEquipements()) {
            if (e.getRarete() == rareteChoisie && EquipementFactory.estCompatibleArme(cible.getType(), e)) {
                candidats.add(e);
            }
        }

        StringBuilder message = new StringBuilder();
        int equipesCount = 0;
        for (Equipement.Slot slot : Equipement.Slot.values()) {
            if (cible.getEquipement(slot) != null) continue;
            Equipement aEquiper = null;
            for (Equipement e : candidats) {
                if (e.getSlot() == slot) { aEquiper = e; break; }
            }
            if (aEquiper == null) continue;

            inv.retirerEquipement(aEquiper);
            cible.equiper(aEquiper);
            candidats.remove(aEquiper);
            message.append(slot.name()).append(" : ").append(aEquiper.getNom()).append(" equipe !\n");
            equipesCount++;
        }

        if (equipesCount == 0) {
            info("Equiper un set", "Aucun slot libre compatible avec la rarete " + rareteChoisie.name() + ".");
        } else {
            ctx.sauvegarde.sauvegarder(ctx);
            info("Equiper un set", message.append(equipesCount).append(" piece(s) equipee(s) de rarete ").append(rareteChoisie.name()).append(".").toString());
        }
        rafraichir();
    }

    @FXML
    private void onCartesOr(ActionEvent event) {
        List<Inventaire.StackCarteOr> stacks = new ArrayList<>();
        for (Inventaire.StackCarteOr s : ctx.inventaire.getCartesOr()) {
            if (!s.estVide()) stacks.add(s);
        }
        if (stacks.isEmpty()) { info("Cartes d'Or", "Vous n'avez aucune Carte d'Or dans votre inventaire."); return; }

        Map<String, Inventaire.StackCarteOr> map = new LinkedHashMap<>();
        List<String> options = new ArrayList<>();
        for (Inventaire.StackCarteOr s : stacks) {
            options.add(s.toString());
            map.put(s.toString(), s);
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle("Cartes d'Or");
        dialog.setHeaderText(null);
        dialog.setContentText("Or actuel : " + String.format("%,.0f", ctx.joueur.getOr()) + "\nCarte a utiliser :");
        styliser(dialog);
        Optional<String> resultat = dialog.showAndWait();
        if (resultat.isEmpty()) return;

        Inventaire.StackCarteOr stackChoisi = map.get(resultat.get());
        CarteOr carte = stackChoisi.getCarte();
        int dispo = stackChoisi.getQuantite();

        TextInputDialog qteDialog = new TextInputDialog(String.valueOf(dispo));
        qteDialog.setTitle("Quantite");
        qteDialog.setHeaderText(null);
        qteDialog.setContentText("Vous avez " + dispo + " x " + carte.nom + " (" + carte.valeurOr + " or chacune).\nCombien en utiliser ?");
        styliser(qteDialog);
        Optional<String> qteStr = qteDialog.showAndWait();
        if (qteStr.isEmpty()) return;

        int quantite;
        try {
            quantite = Integer.parseInt(qteStr.get().trim());
        } catch (NumberFormatException e) {
            info("Cartes d'Or", "Entree invalide.");
            return;
        }
        if (quantite < 1 || quantite > dispo) {
            info("Cartes d'Or", "Quantite invalide. Vous en avez " + dispo + ".");
            return;
        }

        long orGagne = (long) carte.valeurOr * quantite;
        ctx.inventaire.retirerCartesOr(carte, quantite);
        long restant = orGagne;
        while (restant > 0) {
            int tranche = (int) Math.min(restant, Integer.MAX_VALUE);
            ctx.joueur.ajouterOr(tranche);
            restant -= tranche;
        }
        ctx.gestionnaireQuetes.notifierOrGagne((int) Math.min(orGagne, Integer.MAX_VALUE));
        ctx.sauvegarde.sauvegarder(ctx);

        info("Cartes d'Or", "Vous avez utilise " + quantite + " x " + carte.nom + " !\n"
                + "+ " + String.format("%,d", orGagne) + " or gagne !\n"
                + "Or total : " + String.format("%,.0f", ctx.joueur.getOr()) + " or");
        rafraichir();
    }

    @FXML
    private void onSynthese(ActionEvent event) {
        List<FragmentEquipement> catalogue = gestionnaireFragments.getCatalogue();
        List<FragmentEquipement> prets = new ArrayList<>();
        for (FragmentEquipement f : catalogue) {
            int qte = ctx.inventaire.getQuantiteMateriau(f.getNomFragment());
            if (qte >= FragmentEquipement.QUANTITE_REQUISE) prets.add(f);
        }

        if (prets.isEmpty()) {
            info("Synthese", "Aucun equipement n'est encore synthetisable.\nCollectez " + FragmentEquipement.QUANTITE_REQUISE
                    + " fragments identiques (Chapitre 3 Elite) pour synthetiser un equipement rang A.");
            return;
        }

        Map<String, FragmentEquipement> map = new LinkedHashMap<>();
        List<String> options = new ArrayList<>();
        for (FragmentEquipement f : prets) {
            String libelle = gestionnaireFragments.afficherProgression(f, ctx.inventaire);
            options.add(libelle);
            map.put(libelle, f);
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle("Synthese - Equipements [A]");
        dialog.setHeaderText(null);
        dialog.setContentText("Equipement a synthetiser :");
        styliser(dialog);
        Optional<String> resultat = dialog.showAndWait();
        if (resultat.isEmpty()) return;

        FragmentEquipement fragment = map.get(resultat.get());
        Equipement nouvel = gestionnaireFragments.synthetiser(fragment, ctx.inventaire);
        if (nouvel == null) { info("Synthese", "Fragments insuffisants."); return; }

        ctx.inventaire.ajouterEquipement(nouvel);
        ctx.sauvegarde.sauvegarder(ctx);
        info("Synthese", "Synthese reussie !\n" + nouvel + " ajoute a l'inventaire !\n"
                + FragmentEquipement.QUANTITE_REQUISE + " fragments consommes.");
        rafraichir();
    }

    private PersonnageBase choisirPersonnage(String titre) {
        List<PersonnageBase> tous = new ArrayList<>();
        tous.add(ctx.joueur);
        tous.addAll(ctx.personnagesRecruites);

        Map<String, PersonnageBase> map = new LinkedHashMap<>();
        List<String> options = new ArrayList<>();
        for (PersonnageBase p : tous) {
            String libelle = p.getNom() + " [" + p.getRole() + "] Niv." + p.getNiveau() + " - Type : " + p.getType();
            options.add(libelle);
            map.put(libelle, p);
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle(titre);
        dialog.setHeaderText(null);
        dialog.setContentText("Personnage :");
        styliser(dialog);
        Optional<String> resultat = dialog.showAndWait();
        return resultat.map(map::get).orElse(null);
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
