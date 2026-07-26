package lancement.gui;

import Equipement.Equipement;
import Equipement.EquipementFactory;
import Equipement.Inventaire;
import Equipement.ParcheminXP;
import Equipement.BonusSet;
import Joueur.Personnage_principale;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lancement.Formation;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireLiens;
import lancement.Menus.MenuPersonnage;

public class EcranFichePersonnageController {

    private final MenuPersonnage menuPersonnage = new MenuPersonnage();

    private GameContext ctx;
    private PersonnageBase perso;
    private Runnable onRetour;

    @FXML private HBox badgeBox;
    @FXML private Label titreLabel;
    @FXML private VBox barresBox;
    @FXML private FlowPane statsBox;
    @FXML private FlowPane statsDetailBox;
    @FXML private VBox competencesBox;
    @FXML private FlowPane liensBox;
    @FXML private VBox setBox;
    @FXML private VBox slotsBox;
    @FXML private Button parcheminButton;

    public void initData(GameContext ctx, PersonnageBase perso, Runnable onRetour) {
        this.ctx = ctx;
        this.perso = perso;
        this.onRetour = onRetour;
        rafraichir();
    }

    private void rafraichir() {
        badgeBox.getChildren().setAll(GuiVisuels.creerBadgeRarete(perso.getRarete()));
        titreLabel.setText(perso.getNom()
                + "  Niv." + perso.getNiveau()
                + "  " + GuiVisuels.nomClasseAffiche(perso)
                + "  " + perso.getRole());

        barresBox.getChildren().setAll(
                GuiVisuels.creerBarrePV(380, 14, perso.getVie(), perso.getVieMax()),
                GuiVisuels.creerBarreXP(380, 10, perso.getExperience(), perso.getExperienceMax()));

        Equipement.Rarete raretesSet = perso.getRareteSetDominante();
        int piecesSet = raretesSet != null ? perso.compterPieces(raretesSet) : 0;

        double arbreATK = 0, arbreDEF = 0, arbrePV = 0, arbreVIT = 0;
        if (perso instanceof Personnage_principale pp) {
            arbreATK = pp.getArbreCompetences().getBonusATK();
            arbreDEF = pp.getArbreCompetences().getBonusDEF();
            arbrePV  = pp.getArbreCompetences().getBonusPV();
            arbreVIT = pp.getArbreCompetences().getBonusVIT();
        }
        double bonusPVSet  = piecesSet >= BonusSet.SEUIL_PV  ? BonusSet.palier(raretesSet).bonusPV() : 0;
        double bonusVITSet = piecesSet >= BonusSet.SEUIL_VIT ? BonusSet.palier(raretesSet).bonusVitessePct() : 0;
        double bonusATKSet = piecesSet >= BonusSet.SEUIL_ATK ? BonusSet.palier(raretesSet).bonusAttaquePct() : 0;
        double totalPctATK = arbreATK + perso.getBonusLienATK() + bonusATKSet;
        double totalPctDEF = arbreDEF + perso.getBonusLienDEF();
        double totalPctPV  = arbrePV  + perso.getBonusLienPV();
        double totalPctVIT = arbreVIT + perso.getBonusLienVIT() + bonusVITSet;

        statsBox.getChildren().setAll(
                carteStat("PV", String.format("+%.0f", perso.getBonusEquipementPV()),
                        (bonusPVSet > 0 ? "équip +set" + (int) bonusPVSet : "équip") + String.format("  +%.0f%%", totalPctPV * 100)),
                carteStat("ATK", String.format("%.0f", perso.getAttaque()),
                        String.format("équip +%.0f  +%.0f%%", perso.getBonusEquipementATK(), totalPctATK * 100)),
                carteStat("DEF", String.format("%.0f", perso.getDefense()),
                        String.format("équip +%.0f  +%.0f%%", perso.getBonusEquipementDEF(), totalPctDEF * 100)),
                carteStat("VIT", String.format("%.0f", perso.getVitesse()),
                        String.format("équip +%.0f  +%.0f%%", perso.getBonusEquipementVIT(), totalPctVIT * 100))
        );
        statsDetailBox.getChildren().setAll(
                statMini("Crit", String.format("%.0f%%", perso.getTauxCritique() * 100),
                        "Chance de coup critique (dégâts renforcés)."),
                statMini("Dégât crit", String.format("x%.2f", perso.getTauxDegatCritique()),
                        "Multiplicateur de dégâts appliqué lors d'un coup critique."),
                statMini("Esquive", String.format("%.0f%%", perso.getTauxEsquives() * 100),
                        "Chance d'éviter complètement une attaque ennemie."),
                statMini("Blocage", String.format("%.0f%%", perso.getTauxBlocage() * 100),
                        "Chance de bloquer une attaque ennemie et réduire les dégâts subis."),
                statMini("Attaque S", String.format("%.0f", perso.getTauxAttaqueS()),
                        "Réduit l'efficacité du Blocage adverse. 100 = neutre."),
                statMini("Contre", String.format("%.0f", perso.getTauxContre()),
                        "Réduit la chance de subir un coup critique. 100 = neutre.")
        );

        String[] nomsAttaques = perso.getNomsAttaques();
        competencesBox.getChildren().setAll(
                carteCompetence(nomsAttaques[0], "base", GuiVisuels.capturerDescription(perso::descriptionAttaqueBase)),
                carteCompetence(nomsAttaques[1], "spéciale", GuiVisuels.capturerDescription(perso::descriptionAttaqueSpeciale)),
                carteCompetence(nomsAttaques[2], "ultime", GuiVisuels.capturerDescription(perso::descriptionAttaqueUltime))
        );

        List<GestionnaireLiens.Lien> liensActifs = ctx.formation.getLiensActifs();
        if (liensActifs.isEmpty()) {
            Label vide = new Label("Aucun lien actif dans la formation.");
            vide.getStyleClass().add("item-vide");
            liensBox.getChildren().setAll(vide);
        } else {
            List<Node> chips = new ArrayList<>();
            for (GestionnaireLiens.Lien l : liensActifs) {
                boolean membre = false;
                for (String m : l.membres) if (m.equals(perso.getNom())) { membre = true; break; }
                chips.add(carteLien(l, membre));
            }
            liensBox.getChildren().setAll(chips);
        }

        setBox.getChildren().setAll(carteSetBonus(raretesSet, piecesSet));

        slotsBox.getChildren().clear();
        for (Equipement.Slot slot : Equipement.Slot.values()) {
            slotsBox.getChildren().add(carteSlot(slot));
        }

        boolean estPrincipal = perso.estPersonnagePrincipal();
        parcheminButton.setVisible(!estPrincipal);
        parcheminButton.setManaged(!estPrincipal);
        if (!estPrincipal) {
            parcheminButton.setText("Utiliser un Parchemin XP  (Niv." + perso.getNiveau() + " / max " + ctx.joueur.getNiveau() + ")");
        }
    }

    private Node carteStat(String label, String valeur, String detail) {
        Label l = new Label(label);
        l.getStyleClass().add("item-detail");
        Label v = new Label(valeur);
        v.getStyleClass().add("item-nom");
        Label d = new Label(detail);
        d.setStyle("-fx-font-size: 10px; -fx-text-fill: #7a7a95;");

        VBox box = new VBox(2, l, v, d);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("carte-item");
        box.setPrefWidth(140);
        return box;
    }

    /** Petite stat avec info-bulle expliquant ce qu'elle fait (pour les sigles peu explicites : Contre, Attaque S...). */
    private Node statMini(String libelle, String valeur, String explication) {
        Label l = new Label(libelle + " : " + valeur);
        l.getStyleClass().add("item-detail");

        Tooltip tooltip = new Tooltip(explication);
        tooltip.setShowDelay(Duration.millis(200));
        tooltip.setWrapText(true);
        tooltip.setMaxWidth(260);
        Tooltip.install(l, tooltip);

        return l;
    }

    private Node carteCompetence(String nom, String type, String description) {
        Label nomLabel = new Label(nom + " (" + type + ")");
        nomLabel.getStyleClass().add("item-nom");
        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("item-detail");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(380);

        VBox texte = new VBox(2, nomLabel, descLabel);
        HBox carte = new HBox(texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(440);
        return carte;
    }

    private Node carteLien(GestionnaireLiens.Lien l, boolean membre) {
        Label nom = new Label(l.nom + (membre ? " ★" : ""));
        nom.getStyleClass().add("item-nom");
        Label detail = new Label(l.description);
        detail.getStyleClass().add("item-detail");
        detail.setWrapText(true);
        detail.setMaxWidth(200);

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add(membre ? "carte-item-joueur" : "carte-item");
        carte.setPrefWidth(220);
        return carte;
    }

    private Node carteSetBonus(Equipement.Rarete raretesSet, int piecesSet) {
        Label titre = new Label(raretesSet == null
                ? "Bonus de Set"
                : "Bonus de Set — Rang " + raretesSet + " (" + piecesSet + "/6)");
        titre.getStyleClass().add("section-titre");

        String etape;
        if (raretesSet == null) {
            etape = "Aucune pièce équipée.";
        } else {
            BonusSet.Palier palier = BonusSet.palier(raretesSet);
            BonusSet.BonusSpecial special = BonusSet.special(raretesSet);
            int pv = (int) palier.bonusPV();
            int vit = (int) (palier.bonusVitessePct() * 100);
            int atk = (int) (palier.bonusAttaquePct() * 100);
            if (piecesSet < BonusSet.SEUIL_PV)
                etape = "Aucun bonus actif. Prochain palier : " + BonusSet.SEUIL_PV + " pièces (+" + pv + " PV).";
            else if (piecesSet < BonusSet.SEUIL_VIT)
                etape = "Actif : " + BonusSet.SEUIL_PV + " pièces (+" + pv + " PV). Prochain palier : "
                        + BonusSet.SEUIL_VIT + " pièces (+" + vit + "% VIT).";
            else if (piecesSet < BonusSet.SET_COMPLET)
                etape = "Actifs : " + BonusSet.SEUIL_PV + " et " + BonusSet.SEUIL_VIT + " pièces (+" + pv
                        + " PV, +" + vit + "% VIT). Prochain palier : " + BonusSet.SET_COMPLET + " pièces (+" + atk + "% ATK).";
            else {
                etape = "Tous les bonus actifs : +" + pv + " PV, +" + vit + "% VIT, +" + atk + "% ATK.";
                if (special != null) {
                    etape += " Bonus spécial : +" + (int) (special.bonusTauxCritique() * 100) + "% crit, +"
                            + (int) (special.bonusDegatCritiquePct() * 100) + "% dégâts crit, +"
                            + (int) (special.bonusEsquive() * 100) + "% esquive"
                            + (special.bonusVolDeVie() > 0
                                    ? ", +" + (int) (special.bonusVolDeVie() * 100) + "% vol de vie"
                                    : "") + ".";
                }
            }
        }

        Label detail = new Label(etape);
        detail.getStyleClass().add("item-detail");
        detail.setWrapText(true);
        detail.setMaxWidth(380);

        VBox box = new VBox(6, titre, GuiVisuels.creerBarreProgression(240, 12, piecesSet, 6), detail);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private Node carteSlot(Equipement.Slot slot) {
        Equipement equipe = perso.getEquipement(slot);

        Label nom = new Label(nomSlot(slot));
        nom.getStyleClass().add("item-nom");

        Label detail = new Label(equipe != null
                ? equipe.getNomAffiche() + " — " + equipe.getDescriptionBonus()
                : "[vide]");
        detail.getStyleClass().add("item-detail");
        detail.setWrapText(true);
        detail.setMaxWidth(300);

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(10, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(420);
        carte.setCursor(Cursor.HAND);
        carte.setOnMouseClicked(e -> gererSlot(slot));

        if (equipe != null) {
            carte.getChildren().add(0, GuiVisuels.creerBadgeRarete(equipe.getRarete().name()));
        } else {
            carte.setOpacity(0.7);
        }
        return carte;
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

        List<ChoixEquipement> options = new ArrayList<>();
        if (actuel != null) options.add(new ChoixEquipement(null));
        for (Equipement e : compatibles) options.add(new ChoixEquipement(e));

        ChoixEquipement choix = GuiVisuels.choisirParmiCartes(nomSlot(slot), options, this::carteChoixEquipement);
        if (choix == null) return;

        if (choix.equipement() == null) {
            perso.desequiper(slot);
            inv.ajouterEquipement(actuel);
            info("Equipement", actuel.getNom() + " remis en inventaire.");
        } else {
            Equipement choisi = choix.equipement();
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

    /** Enveloppe une option du picker d'equipement ; equipement() == null represente "(Desequiper)". */
    private record ChoixEquipement(Equipement equipement) {}

    private Node carteParcheminXPChoix(ParcheminXP.Rarete r) {
        int stock = ctx.inventaire.getQuantiteParcheminXP(r);
        Label badge = GuiVisuels.creerBadgeRarete(r.name());
        Label nom = new Label("Parchemin XP");
        nom.getStyleClass().add("item-nom");
        Label detail = new Label("Stock : " + stock);
        detail.getStyleClass().add("item-detail");

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(10, badge, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(220);
        return carte;
    }

    private Node carteChoixEquipement(ChoixEquipement choix) {
        if (choix.equipement() == null) {
            Label nom = new Label("(Déséquiper)");
            nom.getStyleClass().add("item-nom");
            HBox carte = new HBox(nom);
            carte.setAlignment(Pos.CENTER_LEFT);
            carte.getStyleClass().add("carte-item");
            carte.setPrefWidth(260);
            return carte;
        }

        Equipement e = choix.equipement();
        Label badge = GuiVisuels.creerBadgeRarete(e.getRarete().name());
        Label nom = new Label(e.getNomAffiche());
        nom.getStyleClass().add("item-nom");
        Label detail = new Label(e.getNomSlot() + " — " + e.getDescription());
        detail.getStyleClass().add("item-detail");
        detail.setWrapText(true);
        detail.setMaxWidth(240);

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(10, badge, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(320);
        return carte;
    }

    @FXML
    private void onAutoEquiper(ActionEvent event) {
        String resultat = lancement.Gestionnaires.GestionnaireEquipement.equiperMeilleurDisponible(
                perso, ctx.inventaire, ctx.rangJoueur);
        ctx.sauvegarde.sauvegarder(ctx);
        info("Équiper le meilleur disponible", resultat);
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

        List<ParcheminXP.Rarete> options = new ArrayList<>();
        for (ParcheminXP.Rarete r : ParcheminXP.Rarete.values()) {
            if (ctx.inventaire.getQuantiteParcheminXP(r) > 0) options.add(r);
        }
        if (options.isEmpty()) { info("Parchemin XP", "Aucun parchemin XP dans l'inventaire."); return; }

        ParcheminXP.Rarete rarete = GuiVisuels.choisirParmiCartes("Parchemin XP", options, this::carteParcheminXPChoix);
        if (rarete == null) return;
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
