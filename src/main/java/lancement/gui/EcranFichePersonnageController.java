package lancement.gui;

import Equipement.Equipement;
import Equipement.EquipementFactory;
import Equipement.Inventaire;
import Equipement.ParcheminXP;
import Joueur.Personnage_principale;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import lancement.Formation;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireLiens;
import lancement.Menus.MenuPersonnage;

public class EcranFichePersonnageController {

    private final MenuPersonnage menuPersonnage = new MenuPersonnage();

    private GameContext ctx;
    private PersonnageBase perso;
    private Runnable onRetour;

    @FXML private Label titreLabel;
    @FXML private Label statsLabel;
    @FXML private Label liensLabel;
    @FXML private Label setLabel;
    @FXML private VBox slotsBox;
    @FXML private Button parcheminButton;

    public void initData(GameContext ctx, PersonnageBase perso, Runnable onRetour) {
        this.ctx = ctx;
        this.perso = perso;
        this.onRetour = onRetour;
        rafraichir();
    }

    private void rafraichir() {
        titreLabel.setText(perso.getNom()
                + "  Niv." + perso.getNiveau()
                + "  [" + perso.getRarete() + "]"
                + "  " + (perso.getType() != null ? perso.getType() : "")
                + "  " + perso.getRole());

        int piecesC = compterPiecesRangC(perso);

        double arbreATK = 0, arbreDEF = 0, arbrePV = 0, arbreVIT = 0;
        if (perso instanceof Personnage_principale pp) {
            arbreATK = pp.getArbreCompetences().getBonusATK();
            arbreDEF = pp.getArbreCompetences().getBonusDEF();
            arbrePV  = pp.getArbreCompetences().getBonusPV();
            arbreVIT = pp.getArbreCompetences().getBonusVIT();
        }
        double bonusPVSet   = piecesC >= 3 ? 200 : 0;
        double totalPctATK = arbreATK + perso.getBonusLienATK() + (piecesC >= 6 ? 0.05 : 0);
        double totalPctDEF = arbreDEF + perso.getBonusLienDEF();
        double totalPctPV  = arbrePV  + perso.getBonusLienPV();
        double totalPctVIT = arbreVIT + perso.getBonusLienVIT() + (piecesC >= 4 ? 0.02 : 0);

        StringBuilder stats = new StringBuilder("[ Stats ]\n");
        stats.append(String.format("PV  : %.0f / %.0f  (equip +%.0f%s  +%.0f%%)%n",
                perso.getVie(), perso.getVieMax(), perso.getBonusEquipementPV(),
                bonusPVSet > 0 ? " +set" + (int) bonusPVSet : "", totalPctPV * 100));
        stats.append(String.format("ATK : %.0f  (equip +%.0f  +%.0f%%)%n",
                perso.getAttaque(), perso.getBonusEquipementATK(), totalPctATK * 100));
        stats.append(String.format("DEF : %.0f  (equip +%.0f  +%.0f%%)%n",
                perso.getDefense(), perso.getBonusEquipementDEF(), totalPctDEF * 100));
        stats.append(String.format("VIT : %.0f  (equip +%.0f  +%.0f%%)%n",
                perso.getVitesse(), perso.getBonusEquipementVIT(), totalPctVIT * 100));
        stats.append(String.format("Crit : %.0f%%  Degat crit : x%.2f%n",
                perso.getTauxCritique() * 100, perso.getTauxDegatCritique()));
        stats.append(String.format("Esquive : %.0f%%  Blocage : %.0f%%%n",
                perso.getTauxEsquives() * 100, perso.getTauxBlocage() * 100));
        stats.append("XP : ").append(perso.getExperience()).append(" / ").append(perso.getExperienceMax());
        statsLabel.setText(stats.toString());

        List<GestionnaireLiens.Lien> liensActifs = ctx.formation.getLiensActifs();
        if (liensActifs.isEmpty()) {
            liensLabel.setText("[ Liens ] Aucun lien actif dans la formation.");
        } else {
            StringBuilder sb = new StringBuilder("[ Liens actifs ]\n");
            for (GestionnaireLiens.Lien l : liensActifs) {
                boolean membre = false;
                for (String m : l.membres) if (m.equals(perso.getNom())) { membre = true; break; }
                sb.append(l.nom).append(membre ? " *" : "").append(" - ").append(l.description).append("\n");
            }
            liensLabel.setText(sb.toString().trim());
        }

        StringBuilder set = new StringBuilder("[ Bonus de Set - Rang C (" + piecesC + "/6) ]\n");
        if (piecesC < 3) {
            set.append("Aucun bonus actif. Prochain : 3 pieces - +200 PV");
        } else {
            set.append("[OK] 3 pieces : +200 PV\n");
            if (piecesC < 4) set.append("Prochain : 4 pieces - +2% VIT");
            else {
                set.append("[OK] 4 pieces : +2% VIT\n");
                if (piecesC < 6) set.append("Prochain : 6 pieces - +5% ATK (manque ").append(6 - piecesC).append(")");
                else set.append("[OK] 6 pieces : +5% ATK");
            }
        }
        setLabel.setText(set.toString());

        slotsBox.getChildren().clear();
        for (Equipement.Slot slot : Equipement.Slot.values()) {
            Equipement equipe = perso.getEquipement(slot);
            String equipeStr = equipe != null
                    ? equipe.getNomRarete() + " " + equipe.getNomAffiche() + " (" + equipe.getDescriptionBonus() + ")"
                    : "[vide]";
            Button bouton = new Button(nomSlot(slot) + " : " + equipeStr);
            bouton.getStyleClass().add("menu-bouton");
            bouton.setWrapText(true);
            bouton.setPrefWidth(420);
            bouton.setOnAction(e -> gererSlot(slot));
            slotsBox.getChildren().add(bouton);
        }

        boolean estPrincipal = perso.estPersonnagePrincipal();
        parcheminButton.setVisible(!estPrincipal);
        parcheminButton.setManaged(!estPrincipal);
        if (!estPrincipal) {
            parcheminButton.setText("Utiliser un Parchemin XP  (Niv." + perso.getNiveau() + " / max " + ctx.joueur.getNiveau() + ")");
        }
    }

    private int compterPiecesRangC(PersonnageBase p) {
        int count = 0;
        for (Equipement.Slot slot : Equipement.Slot.values()) {
            Equipement e = p.getEquipement(slot);
            if (e != null && e.getRarete() == Equipement.Rarete.C) count++;
        }
        return count;
    }

    private String nomSlot(Equipement.Slot slot) {
        return switch (slot) {
            case ARME        -> "Arme";
            case COUVRE_CHEF -> "Couvre-chef";
            case TORSE       -> "Torse";
            case MAINS       -> "Mains";
            case JAMBIERES   -> "Jambieres";
            case BOTTES      -> "Bottes";
        };
    }

    private void gererSlot(Equipement.Slot slot) {
        Inventaire inv = ctx.inventaire;
        Equipement actuel = perso.getEquipement(slot);

        List<Equipement> compatibles = new ArrayList<>();
        for (Equipement e : inv.getEquipements()) {
            if (e.getSlot() == slot && EquipementFactory.estCompatibleArme(perso.getType(), e)) {
                compatibles.add(e);
            }
        }

        if (compatibles.isEmpty() && actuel == null) {
            info("Equipement", "Aucun equipement disponible pour ce slot.");
            return;
        }

        Map<String, Equipement> map = new LinkedHashMap<>();
        List<String> options = new ArrayList<>();
        String optionDesequiper = "(Desequiper)";
        if (actuel != null) { options.add(optionDesequiper); }
        for (Equipement e : compatibles) {
            String libelle = e.toString();
            options.add(libelle);
            map.put(libelle, e);
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle(nomSlot(slot));
        dialog.setHeaderText(null);
        dialog.setContentText("Equipe actuellement : " + (actuel != null ? actuel.toString() : "[vide]") + "\nChoix :");
        styliser(dialog);
        Optional<String> resultat = dialog.showAndWait();
        if (resultat.isEmpty()) return;

        if (resultat.get().equals(optionDesequiper)) {
            perso.desequiper(slot);
            inv.ajouterEquipement(actuel);
            info("Equipement", actuel.getNom() + " remis en inventaire.");
        } else {
            Equipement choisi = map.get(resultat.get());
            if (actuel != null) {
                perso.desequiper(slot);
                inv.ajouterEquipement(actuel);
            }
            perso.equiper(choisi);
            inv.retirerEquipement(choisi);
            info("Equipement", choisi.getNomAffiche() + " equipe sur " + perso.getNom() + " !");
        }
        rafraichir();
    }

    @FXML
    private void onParcheminXP(ActionEvent event) {
        if (ctx.inventaire.getParchemins().isEmpty()) {
            info("Parchemin XP", "Aucun parchemin XP dans l'inventaire.");
            return;
        }
        int niveauMax = ctx.joueur.getNiveau();
        if (perso.getNiveau() >= niveauMax) {
            info("Parchemin XP", perso.getNom() + " est deja au niveau maximum autorise (" + niveauMax + ").");
            return;
        }

        List<String> options = new ArrayList<>();
        Map<String, ParcheminXP.Rarete> map = new LinkedHashMap<>();
        for (ParcheminXP.Rarete r : ParcheminXP.Rarete.values()) {
            int stock = ctx.inventaire.getQuantiteParcheminXP(r);
            if (stock <= 0) continue;
            String libelle = "Parchemin XP [" + r.name() + "]  (stock : " + stock + ")";
            options.add(libelle);
            map.put(libelle, r);
        }
        if (options.isEmpty()) { info("Parchemin XP", "Aucun parchemin XP dans l'inventaire."); return; }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle("Parchemin XP");
        dialog.setHeaderText(null);
        dialog.setContentText("Choisissez une rarete :");
        styliser(dialog);
        Optional<String> choix = dialog.showAndWait();
        if (choix.isEmpty()) return;

        ParcheminXP.Rarete rarete = map.get(choix.get());
        int stock = ctx.inventaire.getQuantiteParcheminXP(rarete);

        TextInputDialog qteDialog = new TextInputDialog(String.valueOf(stock));
        qteDialog.setTitle("Quantite");
        qteDialog.setHeaderText(null);
        qteDialog.setContentText("Quantite a utiliser (1-" + stock + ") :");
        styliser(qteDialog);
        Optional<String> qteStr = qteDialog.showAndWait();
        if (qteStr.isEmpty()) return;

        int quantite;
        try {
            quantite = Integer.parseInt(qteStr.get().trim());
        } catch (NumberFormatException e) {
            info("Parchemin XP", "Entree invalide.");
            return;
        }
        if (quantite < 1 || quantite > stock) {
            info("Parchemin XP", "Quantite invalide (doit etre entre 1 et " + stock + ").");
            return;
        }

        String resultat = menuPersonnage.appliquerParcheminsXP(perso, ctx, rarete, quantite);
        info("Parchemin XP", resultat);
        rafraichir();
    }

    @FXML
    private void onRetour(ActionEvent event) {
        onRetour.run();
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
