package lancement.gui;

import Combat.Combat.CombatEvent;
import Combat.Combat.PersonnageSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Rejoue visuellement un combat deja resolu (voir Combat.lancerCombatEnregistre) :
 * une carte par personnage avec barres de PV/rage, et une progression tour par
 * tour (pas a pas, lecture auto, ou passage direct a la fin).
 */
public class EcranCombatController {

    private static final double LARGEUR_BARRE = 130;
    private static final double LARGEUR_CARTE = 150;
    private static final Duration DUREE_AUTO  = Duration.millis(900);
    private static final Duration DUREE_FLASH = Duration.millis(500);

    @FXML private Label etapeLabel;
    @FXML private Label banniereLabel;
    @FXML private GridPane equipeJoueurBox;
    @FXML private GridPane equipeAdverseBox;
    @FXML private TextArea logArea;
    @FXML private Button suivantButton;
    @FXML private Button autoButton;
    @FXML private Button passerButton;
    @FXML private Button continuerButton;

    private List<CombatEvent> evenements;
    private List<PersonnageSnapshot> etatPrecedent;
    private int index = 0;
    private CarteCombat[] cartes;
    private Timeline autoPlay;
    private Consumer<Boolean> onTermine;
    private boolean victoire;

    private static final class CarteCombat {
        final VBox racine;
        final Rectangle barrePV;
        final Rectangle barreRage;
        final Label pvTexte;
        final Label rageTexte;
        final FlowPane effetsBox;

        CarteCombat(VBox racine, Rectangle barrePV, Rectangle barreRage, Label pvTexte, Label rageTexte, FlowPane effetsBox) {
            this.racine = racine;
            this.barrePV = barrePV;
            this.barreRage = barreRage;
            this.pvTexte = pvTexte;
            this.rageTexte = rageTexte;
            this.effetsBox = effetsBox;
        }
    }

    /** Description + sens (buff/debuff) + couleur d'un effet, pour l'icone affichee sur les cartes. */
    private record EffetInfo(String description, boolean estBuff, String couleur) {}

    private static final Map<String, EffetInfo> INFOS_EFFETS = new HashMap<>();
    static {
        // Buffs — fleche vers le haut
        INFOS_EFFETS.put("BuffAttaque",       new EffetInfo("Augmente l'attaque.", true, "#e0704a"));
        INFOS_EFFETS.put("BuffDefense",       new EffetInfo("Augmente la defense.", true, "#4a7ee0"));
        INFOS_EFFETS.put("BuffVitesse",       new EffetInfo("Augmente la vitesse.", true, "#e0d24a"));
        INFOS_EFFETS.put("BuffTauxCritique",  new EffetInfo("Augmente le taux de coup critique.", true, "#b565d8"));
        INFOS_EFFETS.put("BuffDegatCritique", new EffetInfo("Augmente les degats critiques.", true, "#8e44ad"));
        INFOS_EFFETS.put("Buff Precision",    new EffetInfo("Augmente la precision.", true, "#4ac9c0"));
        INFOS_EFFETS.put("BuffTauxEsquive",   new EffetInfo("Augmente le taux d'esquive.", true, "#7ed9a3"));
        INFOS_EFFETS.put("BuffBlocage",       new EffetInfo("Augmente le taux de blocage.", true, "#6c8ebf"));
        INFOS_EFFETS.put("BuffTitre",         new EffetInfo("Bonus de toutes les statistiques (titre actif).", true, "#f2c14e"));
        INFOS_EFFETS.put("Bouclier",          new EffetInfo("Absorbe les prochains degats subis.", true, "#5bc8e8"));
        INFOS_EFFETS.put("Regeneration",      new EffetInfo("Restaure des PV chaque tour.", true, "#56c98a"));
        INFOS_EFFETS.put("Invincibilite",     new EffetInfo("Bloque tous les degats subis.", true, "#f2e9c9"));
        INFOS_EFFETS.put("Resurrection",      new EffetInfo("Revient a la vie une fois KO.", true, "#e07ec0"));
        INFOS_EFFETS.put("ContreAttaque",     new EffetInfo("Riposte automatiquement en cas d'attaque.", true, "#e08b3d"));
        INFOS_EFFETS.put("Absorption",        new EffetInfo("Vole des PV en infligeant des degats.", true, "#9b6fe0"));
        INFOS_EFFETS.put("Immunite",          new EffetInfo("Bloque les prochains effets negatifs.", true, "#cfd8e3"));

        // Debuffs — fleche vers le bas
        INFOS_EFFETS.put("Poison",           new EffetInfo("Perd des PV chaque tour (empire en stacks).", false, "#5a8f3d"));
        INFOS_EFFETS.put("Brulure",          new EffetInfo("Perd un % des PV actuels chaque tour.", false, "#e0562a"));
        INFOS_EFFETS.put("Saignement",       new EffetInfo("Perd un % des PV max chaque tour.", false, "#a83232"));
        INFOS_EFFETS.put("Gel",              new EffetInfo("Ne peut plus agir tant que l'effet dure.", false, "#7fd6e8"));
        INFOS_EFFETS.put("Petrification",    new EffetInfo("Ne peut pas agir tant que l'effet dure.", false, "#8a8a8a"));
        INFOS_EFFETS.put("Etourdissement",   new EffetInfo("Passe son prochain tour.", false, "#e0c22a"));
        INFOS_EFFETS.put("Silence",          new EffetInfo("Ne peut plus utiliser de competence speciale.", false, "#6a5a8a"));
        INFOS_EFFETS.put("Sommeil",          new EffetInfo("Dort et ne peut pas agir.", false, "#4a4a8a"));
        INFOS_EFFETS.put("Paralysie",        new EffetInfo("Chance de ne pas pouvoir agir a chaque tour.", false, "#e8e02a"));
        INFOS_EFFETS.put("Confusion",        new EffetInfo("Peut attaquer un allie par erreur.", false, "#c04ac0"));
        INFOS_EFFETS.put("Aveuglement",      new EffetInfo("Reduit la precision.", false, "#3a3a3a"));
        INFOS_EFFETS.put("Ralentissement",   new EffetInfo("Reduit le gain de rage.", false, "#7ea8d8"));
        INFOS_EFFETS.put("ReductionAttaque", new EffetInfo("Reduit l'attaque.", false, "#a85a3a"));
        INFOS_EFFETS.put("ReductionDefense", new EffetInfo("Reduit la defense.", false, "#5a6a9a"));
        INFOS_EFFETS.put("ReductionVitesse", new EffetInfo("Reduit la vitesse.", false, "#a8a05a"));
        INFOS_EFFETS.put("Fragilite",        new EffetInfo("Augmente les degats recus.", false, "#d88a9a"));
        INFOS_EFFETS.put("Malediction",      new EffetInfo("Reduit les soins recus.", false, "#5a2a6a"));
        INFOS_EFFETS.put("Marquage",         new EffetInfo("Augmente les degats recus.", false, "#d8622a"));
        INFOS_EFFETS.put("Trempe",           new EffetInfo("Plus vulnerable au gel et a la paralysie.", false, "#3a7ea8"));
        INFOS_EFFETS.put("Provocation",      new EffetInfo("Force a attaquer une cible precise.", false, "#c02a2a"));
    }

    private EffetInfo infoEffet(String nom) {
        EffetInfo info = INFOS_EFFETS.get(nom);
        return info != null ? info : new EffetInfo(nom, true, "#9a9ac0");
    }

    /**
     * @param etatInitial etat des 2 equipes juste avant le combat (voir Combat.snapshotEquipes)
     * @param evenements  etapes du combat (voir Combat.lancerCombatEnregistre)
     * @param victoire    true si l'equipe joueur a gagne
     * @param onTermine   appele avec `victoire` quand le joueur clique sur "Continuer"
     */
    public void initCombat(List<PersonnageSnapshot> etatInitial, List<CombatEvent> evenements,
                            boolean victoire, Consumer<Boolean> onTermine) {
        this.evenements    = evenements;
        this.etatPrecedent = etatInitial;
        this.victoire      = victoire;
        this.onTermine     = onTermine;
        this.index         = 0;

        construireCartes(etatInitial);
        etapeLabel.setText("Debut du combat");

        if (evenements.isEmpty()) {
            terminerAffichage();
        }
    }

    private void construireCartes(List<PersonnageSnapshot> etat) {
        equipeJoueurBox.getChildren().clear();
        equipeAdverseBox.getChildren().clear();
        cartes = new CarteCombat[etat.size()];

        List<Integer> indexJoueur = new ArrayList<>();
        List<Integer> indexAdverse = new ArrayList<>();

        for (int i = 0; i < etat.size(); i++) {
            PersonnageSnapshot snap = etat.get(i);
            cartes[i] = creerCarte(snap);
            (snap.coteJoueur ? indexJoueur : indexAdverse).add(i);
        }

        // Formation en colonnes, ordonnee par distance au centre (VS) : Support en
        // retrait, DPS au milieu, Tank au contact de la ligne de front adverse.
        ajouterFormation(equipeJoueurBox, etat, indexJoueur, false);
        ajouterFormation(equipeAdverseBox, etat, indexAdverse, true);
    }

    private void ajouterFormation(GridPane grille, List<PersonnageSnapshot> etat, List<Integer> indices, boolean miroir) {
        List<Integer> tank = new ArrayList<>();
        List<Integer> dps = new ArrayList<>();
        List<Integer> support = new ArrayList<>();
        for (int i : indices) {
            switch (etat.get(i).role) {
                case "Tank" -> tank.add(i);
                case "Support" -> support.add(i);
                default -> dps.add(i);
            }
        }

        List<List<Integer>> colonnes = new ArrayList<>();
        List<String> titres = new ArrayList<>();
        if (miroir) {
            // Equipe adverse : le tank est colle au VS (a gauche de sa formation).
            ajouterColonneSiPresente(colonnes, titres, "TANK", tank);
            ajouterColonneSiPresente(colonnes, titres, "ATTAQUANTS", dps);
            ajouterColonneSiPresente(colonnes, titres, "SUPPORT", support);
        } else {
            // Equipe joueur : le tank est colle au VS (a droite de sa formation).
            ajouterColonneSiPresente(colonnes, titres, "SUPPORT", support);
            ajouterColonneSiPresente(colonnes, titres, "ATTAQUANTS", dps);
            ajouterColonneSiPresente(colonnes, titres, "TANK", tank);
        }

        int lignesMax = 1;
        for (List<Integer> colonne : colonnes) {
            lignesMax = Math.max(lignesMax, colonne.size());
        }

        for (int col = 0; col < colonnes.size(); col++) {
            Label entete = new Label(titres.get(col));
            entete.getStyleClass().add("groupe-titre");
            GridPane.setHalignment(entete, HPos.CENTER);
            grille.add(entete, col, 0);

            List<Integer> membres = colonnes.get(col);
            if (membres.size() == 1) {
                // Seul occupant de la colonne (ex. le Tank) : centre sur toute la hauteur
                // disponible plutot que coince en haut, meme si les colonnes voisines
                // comptent davantage de membres.
                Node carte = cartes[membres.get(0)].racine;
                grille.add(carte, col, 1);
                GridPane.setRowSpan(carte, lignesMax);
                GridPane.setValignment(carte, VPos.CENTER);
            } else {
                for (int ligne = 0; ligne < membres.size(); ligne++) {
                    Node carte = cartes[membres.get(ligne)].racine;
                    grille.add(carte, col, ligne + 1);
                    GridPane.setValignment(carte, VPos.TOP);
                }
            }
        }
    }

    private void ajouterColonneSiPresente(List<List<Integer>> colonnes, List<String> titres, String titre, List<Integer> indices) {
        if (indices.isEmpty()) return;
        colonnes.add(indices);
        titres.add(titre);
    }

    private CarteCombat creerCarte(PersonnageSnapshot snap) {
        Label nomLabel = new Label(snap.nom);
        nomLabel.getStyleClass().add("texte");
        nomLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        nomLabel.setWrapText(true);
        nomLabel.setPrefWidth(LARGEUR_BARRE);
        nomLabel.setAlignment(Pos.CENTER);
        nomLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label raretteLabel = new Label(snap.role + " · Rang " + snap.rarete);
        raretteLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + versHex(couleurRarete(snap.rarete)) + ";");

        Label pvTexte = new Label();
        pvTexte.getStyleClass().add("texte");
        pvTexte.setStyle("-fx-font-size: 11px;");
        StackPane barrePVPane = creerBarre();
        Rectangle barrePV = (Rectangle) barrePVPane.getChildren().get(1);

        Label rageTexte = new Label();
        rageTexte.getStyleClass().add("texte");
        rageTexte.setStyle("-fx-font-size: 11px;");
        StackPane barreRagePane = creerBarre();
        Rectangle barreRage = (Rectangle) barreRagePane.getChildren().get(1);

        FlowPane effetsBox = new FlowPane(4, 4);
        effetsBox.setPrefWrapLength(LARGEUR_BARRE);

        VBox racine = new VBox(4, nomLabel, raretteLabel, pvTexte, barrePVPane, rageTexte, barreRagePane, effetsBox);
        racine.getStyleClass().addAll("carte-combat", classeRole(snap.role));
        racine.setAlignment(Pos.CENTER);
        racine.setPrefWidth(LARGEUR_CARTE);

        CarteCombat carte = new CarteCombat(racine, barrePV, barreRage, pvTexte, rageTexte, effetsBox);
        appliquerEtat(carte, snap, false, false);
        return carte;
    }

    /** Petite icone carree colorée par effet, avec fleche haut (buff) / bas (debuff) et tooltip explicatif. */
    private Node creerIconeEffet(String nomEffet) {
        EffetInfo info = infoEffet(nomEffet);

        Rectangle carre = new Rectangle(16, 16, Color.web(info.couleur()));
        carre.setArcWidth(4);
        carre.setArcHeight(4);
        carre.setStroke(Color.web("#00000066"));

        Label fleche = new Label(info.estBuff() ? "▲" : "▼");
        fleche.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: "
                + (info.estBuff() ? "#56c98a" : "#e05656") + ";");

        VBox icone = new VBox(0);
        icone.setAlignment(Pos.CENTER);
        if (info.estBuff()) {
            icone.getChildren().addAll(fleche, carre);
        } else {
            icone.getChildren().addAll(carre, fleche);
        }

        Tooltip infobulle = new Tooltip(nomEffet + "\n" + info.description());
        infobulle.setShowDelay(Duration.millis(150));
        Tooltip.install(icone, infobulle);

        return icone;
    }

    private String classeRole(String role) {
        return switch (role == null ? "" : role) {
            case "Tank" -> "carte-combat-tank";
            case "Support" -> "carte-combat-support";
            default -> "carte-combat-dps";
        };
    }

    private StackPane creerBarre() {
        Rectangle fond = new Rectangle(LARGEUR_BARRE, 12, Color.web("#12121c"));
        fond.setArcWidth(8);
        fond.setArcHeight(8);
        Rectangle remplissage = new Rectangle(LARGEUR_BARRE, 12, Color.web("#56c98a"));
        remplissage.setArcWidth(8);
        remplissage.setArcHeight(8);
        StackPane pane = new StackPane(fond, remplissage);
        StackPane.setAlignment(fond, Pos.CENTER_LEFT);
        StackPane.setAlignment(remplissage, Pos.CENTER_LEFT);
        pane.setPrefSize(LARGEUR_BARRE, 12);
        pane.setMaxWidth(LARGEUR_BARRE);
        return pane;
    }

    private void appliquerEtat(CarteCombat carte, PersonnageSnapshot snap, boolean flashDegats, boolean flashSoin) {
        double ratioPV = snap.vieMax > 0 ? Math.max(0, Math.min(1, snap.vie / snap.vieMax)) : 0;
        carte.barrePV.setWidth(LARGEUR_BARRE * ratioPV);
        carte.barrePV.setFill(couleurPV(ratioPV));
        carte.pvTexte.setText("PV : " + (int) Math.ceil(snap.vie) + " / " + (int) Math.ceil(snap.vieMax));

        double ratioRage = Math.max(0, Math.min(1, snap.rage / 100.0));
        carte.barreRage.setWidth(LARGEUR_BARRE * ratioRage);
        carte.barreRage.setFill(Color.web("#4ea8f2"));
        carte.rageTexte.setText("Rage : " + (int) snap.rage + " / 100");

        carte.effetsBox.getChildren().clear();
        for (String nomEffet : snap.effets) {
            carte.effetsBox.getChildren().add(creerIconeEffet(nomEffet));
        }

        carte.racine.getStyleClass().removeAll("carte-combat-hit", "carte-combat-heal", "carte-combat-ko");
        if (!snap.vivant) {
            carte.racine.getStyleClass().add("carte-combat-ko");
        } else if (flashDegats) {
            carte.racine.getStyleClass().add("carte-combat-hit");
            retirerApres(carte.racine, "carte-combat-hit");
        } else if (flashSoin) {
            carte.racine.getStyleClass().add("carte-combat-heal");
            retirerApres(carte.racine, "carte-combat-heal");
        }
    }

    private void retirerApres(VBox noeud, String classe) {
        PauseTransition pause = new PauseTransition(DUREE_FLASH);
        pause.setOnFinished(e -> noeud.getStyleClass().remove(classe));
        pause.play();
    }

    private Color couleurPV(double ratio) {
        if (ratio > 0.5) return Color.web("#56c98a");
        if (ratio > 0.25) return Color.web("#f2c14e");
        return Color.web("#e05656");
    }

    private Color couleurRarete(String rarete) {
        return switch (rarete == null ? "" : rarete.toUpperCase()) {
            case "S" -> Color.web("#f2c14e");
            case "A" -> Color.web("#b565d8");
            case "B" -> Color.web("#4ea8f2");
            case "C" -> Color.web("#7ed9a3");
            default  -> Color.web("#9a9ac0");
        };
    }

    private String versHex(Color c) {
        return String.format("#%02x%02x%02x",
                (int) (c.getRed() * 255), (int) (c.getGreen() * 255), (int) (c.getBlue() * 255));
    }

    private void avancerEtape() {
        if (index >= evenements.size()) return;
        CombatEvent evt = evenements.get(index);
        index++;

        etapeLabel.setText(evt.titre != null ? evt.titre : "");

        for (int i = 0; i < evt.etat.size(); i++) {
            PersonnageSnapshot avant = etatPrecedent.get(i);
            PersonnageSnapshot apres = evt.etat.get(i);
            boolean flashDegats = apres.vie < avant.vie;
            boolean flashSoin   = apres.vie > avant.vie;
            appliquerEtat(cartes[i], apres, flashDegats, flashSoin);
        }

        if (!evt.lignes.isEmpty()) {
            logArea.appendText(String.join("\n", evt.lignes) + "\n\n");
        } else if (evt.titre != null && evt.titre.startsWith("Tour ")) {
            logArea.appendText("\n— " + evt.titre + " —\n");
        }

        etatPrecedent = evt.etat;

        if (evt.finDeCombat) {
            terminerAffichage();
        }
        mettreAJourBoutons();
    }

    private void mettreAJourBoutons() {
        boolean fini = index >= evenements.size();
        suivantButton.setDisable(fini);
        autoButton.setDisable(fini);
        passerButton.setDisable(fini);
    }

    private void terminerAffichage() {
        arreterAuto();
        banniereLabel.setText(victoire ? "VICTOIRE !" : "DEFAITE...");
        banniereLabel.getStyleClass().removeAll("banniere-victoire", "banniere-defaite");
        banniereLabel.getStyleClass().addAll("banniere", victoire ? "banniere-victoire" : "banniere-defaite");
        banniereLabel.setVisible(true);
        banniereLabel.setManaged(true);
        continuerButton.setVisible(true);
        continuerButton.setManaged(true);
        suivantButton.setDisable(true);
        autoButton.setDisable(true);
        passerButton.setDisable(true);
    }

    private void arreterAuto() {
        if (autoPlay != null) {
            autoPlay.stop();
            autoPlay = null;
            autoButton.setText("Lecture auto ▶▶");
        }
    }

    @FXML
    private void onSuivant() {
        arreterAuto();
        avancerEtape();
    }

    @FXML
    private void onAuto() {
        if (autoPlay != null) {
            arreterAuto();
            return;
        }
        autoButton.setText("Pause ⏸");
        autoPlay = new Timeline(new KeyFrame(DUREE_AUTO, e -> {
            if (index >= evenements.size()) {
                arreterAuto();
                return;
            }
            avancerEtape();
        }));
        autoPlay.setCycleCount(Timeline.INDEFINITE);
        autoPlay.play();
    }

    @FXML
    private void onPasser() {
        arreterAuto();
        while (index < evenements.size()) {
            CombatEvent evt = evenements.get(index);
            index++;
            for (int i = 0; i < evt.etat.size(); i++) {
                appliquerEtat(cartes[i], evt.etat.get(i), false, false);
            }
            if (!evt.lignes.isEmpty()) {
                logArea.appendText(String.join("\n", evt.lignes) + "\n\n");
            }
            etatPrecedent = evt.etat;
        }
        etapeLabel.setText("Combat termine");
        terminerAffichage();
    }

    @FXML
    private void onContinuer() {
        if (onTermine != null) onTermine.accept(victoire);
    }
}
