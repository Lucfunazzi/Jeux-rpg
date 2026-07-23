package lancement.gui;

import Equipement.Equipement;
import Equipement.EquipementFactory;
import Equipement.Inventaire;
import Equipement.ParcheminXP;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
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

    @FXML private HBox badgeBox;
    @FXML private Label titreLabel;
    @FXML private VBox barresBox;
    @FXML private FlowPane statsBox;
    @FXML private Label statsDetailLabel;
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
                + "  " + (perso.getType() != null ? perso.getType() : "")
                + "  " + perso.getRole());

        barresBox.getChildren().setAll(
                GuiVisuels.creerBarrePV(380, 14, perso.getVie(), perso.getVieMax()),
                GuiVisuels.creerBarreXP(380, 10, perso.getExperience(), perso.getExperienceMax()));

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
        statsDetailLabel.setText(String.format(
                "Crit : %.0f%%  ·  Dégât crit : x%.2f  ·  Esquive : %.0f%%  ·  Blocage : %.0f%%  ·  Attaque S : %.0f  ·  Contre : %.0f",
                perso.getTauxCritique() * 100, perso.getTauxDegatCritique(),
                perso.getTauxEsquives() * 100, perso.getTauxBlocage() * 100,
                perso.getTauxAttaqueS(), perso.getTauxContre()));

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

        setBox.getChildren().setAll(carteSetBonus(piecesC));

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

    private Node carteSetBonus(int piecesC) {
        Label titre = new Label("Bonus de Set — Rang C (" + piecesC + "/6)");
        titre.getStyleClass().add("section-titre");

        String etape;
        if (piecesC < 3)      etape = "Aucun bonus actif. Prochain palier : 3 pièces (+200 PV).";
        else if (piecesC < 4) etape = "Actif : 3 pièces (+200 PV). Prochain palier : 4 pièces (+2% VIT).";
        else if (piecesC < 6) etape = "Actifs : 3 et 4 pièces (+200 PV, +2% VIT). Prochain palier : 6 pièces (+5% ATK).";
        else                  etape = "Tous les bonus actifs : +200 PV, +2% VIT, +5% ATK.";

        Label detail = new Label(etape);
        detail.getStyleClass().add("item-detail");
        detail.setWrapText(true);
        detail.setMaxWidth(380);

        VBox box = new VBox(6, titre, GuiVisuels.creerBarreProgression(240, 12, piecesC, 6), detail);
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
