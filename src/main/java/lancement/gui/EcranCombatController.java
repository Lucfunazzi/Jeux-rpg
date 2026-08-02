package lancement.gui;

import Combat.Combat.CombatEvent;
import Combat.Combat.PersonnageSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
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
    private static final Duration DUREE_AUTO          = Duration.millis(900);
    private static final Duration DUREE_AUTO_SPECIALE = Duration.millis(1400);
    private static final Duration DUREE_AUTO_ULTIME   = Duration.millis(2200);
    private static final Duration DUREE_FLASH = Duration.millis(500);

    @FXML private Label etapeLabel;
    @FXML private Label banniereLabel;
    @FXML private Label ultimeLabel;
    @FXML private HBox ordreBox;
    @FXML private GridPane equipeJoueurBox;
    @FXML private GridPane equipeAdverseBox;
    @FXML private TextArea logArea;
    @FXML private Button quitterButton;
    @FXML private Button suivantButton;
    @FXML private Button autoButton;
    @FXML private Button passerButton;
    @FXML private Button continuerButton;

    private List<CombatEvent> evenements;
    private List<PersonnageSnapshot> etatPrecedent;
    private int index = 0;
    private CarteCombat[] cartes;
    private PauseTransition autoPause;
    private Consumer<Boolean> onTermine;
    private boolean victoire;

    private static final class CarteCombat {
        final String nom;
        final StackPane conteneur;
        final VBox racine;
        final Rectangle barrePV;
        final Rectangle barreRage;
        final Label pvTexte;
        final Label rageTexte;
        final FlowPane effetsBox;
        final Label koTampon;

        CarteCombat(String nom, StackPane conteneur, VBox racine, Rectangle barrePV, Rectangle barreRage,
                    Label pvTexte, Label rageTexte, FlowPane effetsBox, Label koTampon) {
            this.nom = nom;
            this.conteneur = conteneur;
            this.racine = racine;
            this.barrePV = barrePV;
            this.barreRage = barreRage;
            this.pvTexte = pvTexte;
            this.rageTexte = rageTexte;
            this.effetsBox = effetsBox;
            this.koTampon = koTampon;
        }
    }

    /**
     * Sons joues quand un personnage precis declenche une speciale/ultime precise (cle :
     * "NomPersonnage|NomAttaque", voir PersonnageBase.getNomsAttaques()). Charges paresseusement
     * (AudioClip) pour ne pas echouer si le fichier manque ou si aucun son n'est encore fourni.
     */
    private static final Map<String, AudioClip> SONS_ACTIONS = new HashMap<>();
    static {
        chargerSonAction("Bora", "Fouet de la Protubérance", "/audio/bora_fouet_protuberance.wav");
        chargerSonAction("Bora", "Typhon de la Protubérance", "/audio/bora_typhon_protuberance.wav");
        chargerSonAction("Lucy", "Invocation Taurus", "/audio/lucy_invocation_taurus.wav");
        chargerSonAction("Lucy", "Invocation Aquarius", "/audio/lucy_invocation_aquarius.wav");
        chargerSonAction("Angel", "Caelum", "/audio/angel_caelum.wav");
        chargerSonAction("Angel", "Aries", "/audio/angel_aries.wav");
        chargerSonAction("Gray", "Geyser de glace", "/audio/gray_geyser_glace.wav");
        chargerSonAction("Gray", "Marteau de glace", "/audio/gray_marteau_glace.wav");
        chargerSonAction("Erza", "Armure adamantine", "/audio/erza_armure_adamantine.wav");
        chargerSonAction("Erza", "Ronde des épées", "/audio/erza_ronde_epees.wav");
        chargerSonAction("Natsu", "Poings d'acier du dragon de feu", "/audio/natsu_poings_acier_dragon_feu.wav");
        chargerSonAction("Natsu", "Hurlement du dragon de feu", "/audio/natsu_hurlement_dragon_feu.wav");
        chargerSonAction("Leon", "Ice-Make : Aigle de Glace", "/audio/leon_aigle_glace.wav");
        chargerSonAction("Leon", "Ice-Make : Gorille de Glace", "/audio/leon_gorille_glace.wav");
    }

    private static void chargerSonAction(String nomPerso, String nomAction, String cheminRessource) {
        try {
            SONS_ACTIONS.put(nomPerso + "|" + nomAction,
                    new AudioClip(EcranCombatController.class.getResource(cheminRessource).toExternalForm()));
        } catch (Exception e) {
            System.err.println("[Son] Impossible de charger " + cheminRessource + " : " + e.getMessage());
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
        INFOS_EFFETS.put("Bouclier",          new EffetInfo("Absorbe les prochains degats subis.", true, "#5bc8e8"));
        INFOS_EFFETS.put("Regeneration",      new EffetInfo("Restaure des PV chaque tour.", true, "#56c98a"));
        INFOS_EFFETS.put("Invincibilite",     new EffetInfo("Bloque tous les degats subis.", true, "#f2e9c9"));
        INFOS_EFFETS.put("Resurrection",      new EffetInfo("Revient a la vie une fois KO.", true, "#e07ec0"));
        INFOS_EFFETS.put("ContreAttaque",     new EffetInfo("Riposte automatiquement en cas d'attaque.", true, "#e08b3d"));
        INFOS_EFFETS.put("Absorption",        new EffetInfo("Vole des PV en infligeant des degats.", true, "#9b6fe0"));
        INFOS_EFFETS.put("Immunite",          new EffetInfo("Bloque les prochains effets negatifs.", true, "#cfd8e3"));
        INFOS_EFFETS.put("ImmuniteControle",  new EffetInfo("Bloque Etourdissement, Paralysie, Sommeil et Petrification.", true, "#cfd8e3"));

        // Debuffs — fleche vers le bas
        INFOS_EFFETS.put("Poison",           new EffetInfo("Perd des PV chaque tour (empire en stacks).", false, "#5a8f3d"));
        INFOS_EFFETS.put("Brulure",          new EffetInfo("Perd un % des PV actuels chaque tour.", false, "#e0562a"));
        INFOS_EFFETS.put("Saignement",       new EffetInfo("Perd un % des PV max chaque tour.", false, "#a83232"));
        INFOS_EFFETS.put("Gel",              new EffetInfo("Ne peut plus agir tant que l'effet dure.", false, "#7fd6e8"));
        INFOS_EFFETS.put("Petrification",    new EffetInfo("Ne peut pas agir tant que l'effet dure.", false, "#8a8a8a"));
        INFOS_EFFETS.put("Etourdissement",   new EffetInfo("Passe son prochain tour.", false, "#e0c22a"));
        INFOS_EFFETS.put("Silence",          new EffetInfo("Ne peut plus utiliser de competence speciale ni ultime.", false, "#6a5a8a"));
        INFOS_EFFETS.put("Sommeil",          new EffetInfo("Dort et ne peut pas agir.", false, "#4a4a8a"));
        INFOS_EFFETS.put("Paralysie",        new EffetInfo("Chance de ne pas pouvoir agir a chaque tour.", false, "#e8e02a"));
        INFOS_EFFETS.put("Confusion",        new EffetInfo("Peut attaquer un allie par erreur.", false, "#c04ac0"));
        INFOS_EFFETS.put("Aveuglement",      new EffetInfo("Reduit la precision.", false, "#3a3a3a"));
        INFOS_EFFETS.put("Ralentissement",   new EffetInfo("Reduit le gain de rage.", false, "#7ea8d8"));
        INFOS_EFFETS.put("ReductionAttaque", new EffetInfo("Reduit l'attaque.", false, "#a85a3a"));
        INFOS_EFFETS.put("ReductionDefense", new EffetInfo("Reduit la defense.", false, "#5a6a9a"));
        INFOS_EFFETS.put("ReductionVitesse", new EffetInfo("Reduit la vitesse.", false, "#a8a05a"));
        INFOS_EFFETS.put("Fragilite",        new EffetInfo("Augmente les degats recus (pourcentage/duree variables).", false, "#d88a9a"));
        INFOS_EFFETS.put("Malediction",      new EffetInfo("Reduit les soins recus.", false, "#5a2a6a"));
        INFOS_EFFETS.put("Marquage",         new EffetInfo("Augmente les degats recus de 30% pendant 2 tours (fixe).", false, "#d8622a"));
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
        initCombat(etatInitial, evenements, victoire, onTermine, 0);
    }

    /** Variante avec le numero du chapitre (1 a 13), pour lancer la musique de fond associee.
     *  Passer 0 (ou utiliser l'autre surcharge) pour un combat sans musique dediee (arene, donjon, examen S). */
    public void initCombat(List<PersonnageSnapshot> etatInitial, List<CombatEvent> evenements,
                            boolean victoire, Consumer<Boolean> onTermine, int numeroChapitre) {
        this.evenements    = evenements;
        this.etatPrecedent = etatInitial;
        this.victoire      = victoire;
        this.onTermine     = onTermine;
        this.index         = 0;

        if (numeroChapitre > 0) {
            GestionnaireMusique.jouerMusiqueChapitre(numeroChapitre);
        }

        construireCartes(etatInitial);
        etapeLabel.setText("Debut du combat");
        mettreAJourOrdreTours();

        if (evenements.isEmpty()) {
            terminerAffichage();
        }
    }

    /** Affiche, sous forme de puces, les prochains personnages a agir dans le tour en cours
     *  (deduit de l'ordre des evenements deja enregistres, sans logique supplementaire cote modele). */
    private void mettreAJourOrdreTours() {
        ordreBox.getChildren().clear();
        List<String> prochains = new ArrayList<>();
        for (int i = index; i < evenements.size() && prochains.size() < 6; i++) {
            String titre = evenements.get(i).titre;
            if (titre == null || titre.equals("Effets") || titre.equals("Fin du combat")) continue;
            if (titre.startsWith("Tour ")) {
                if (!prochains.isEmpty()) break;
                continue;
            }
            prochains.add(titre);
        }
        for (int i = 0; i < prochains.size(); i++) {
            Label chip = new Label(prochains.get(i));
            chip.getStyleClass().add(i == 0 ? "ordre-chip-actif" : "ordre-chip");
            ordreBox.getChildren().add(chip);
            if (i < prochains.size() - 1) {
                Label fleche = new Label("→");
                fleche.getStyleClass().add("ordre-fleche");
                ordreBox.getChildren().add(fleche);
            }
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
                Node carte = cartes[membres.get(0)].conteneur;
                grille.add(carte, col, 1);
                GridPane.setRowSpan(carte, lignesMax);
                GridPane.setValignment(carte, VPos.CENTER);
            } else {
                for (int ligne = 0; ligne < membres.size(); ligne++) {
                    Node carte = cartes[membres.get(ligne)].conteneur;
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

        Label koTampon = new Label("K.O.");
        koTampon.getStyleClass().add("ko-tampon");
        koTampon.setVisible(false);
        koTampon.setManaged(false);

        // StackPane englobant : sert de support aux nombres de degats/soins flottants et au
        // tampon K.O., affiches par-dessus la carte sans perturber sa mise en page.
        StackPane conteneur = new StackPane(racine, koTampon);
        conteneur.setPrefWidth(LARGEUR_CARTE);
        StackPane.setAlignment(racine, Pos.CENTER);
        StackPane.setAlignment(koTampon, Pos.CENTER);

        CarteCombat carte = new CarteCombat(snap.nom, conteneur, racine, barrePV, barreRage, pvTexte, rageTexte, effetsBox, koTampon);
        appliquerEtat(carte, snap, 0);
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

    /**
     * @param deltaVie variation de PV depuis l'etat precedent (negatif = degats, positif =
     *                 soin, 0 = pas de variation a mettre en evidence, ex. etat initial).
     */
    private void appliquerEtat(CarteCombat carte, PersonnageSnapshot snap, double deltaVie) {
        appliquerEtat(carte, snap, deltaVie, false);
    }

    private void appliquerEtat(CarteCombat carte, PersonnageSnapshot snap, double deltaVie, boolean critique) {
        double ratioPV = snap.vieMax > 0 ? Math.max(0, Math.min(1, snap.vie / snap.vieMax)) : 0;
        animerBarre(carte.barrePV, LARGEUR_BARRE * ratioPV);
        carte.barrePV.setFill(couleurPV(ratioPV));
        carte.pvTexte.setText("PV : " + (int) Math.ceil(snap.vie) + " / " + (int) Math.ceil(snap.vieMax));

        double ratioRage = Math.max(0, Math.min(1, snap.rage / 100.0));
        animerBarre(carte.barreRage, LARGEUR_BARRE * ratioRage);
        carte.barreRage.setFill(Color.web("#4ea8f2"));
        carte.rageTexte.setText("Rage : " + (int) snap.rage + " / 100");

        carte.effetsBox.getChildren().clear();
        for (String nomEffet : snap.effets) {
            carte.effetsBox.getChildren().add(creerIconeEffet(nomEffet));
        }

        carte.koTampon.setVisible(!snap.vivant);
        carte.koTampon.setManaged(!snap.vivant);

        carte.racine.getStyleClass().removeAll("carte-combat-hit", "carte-combat-heal", "carte-combat-ko");
        if (!snap.vivant) {
            carte.racine.getStyleClass().add("carte-combat-ko");
        } else if (deltaVie < 0) {
            carte.racine.getStyleClass().add("carte-combat-hit");
            retirerApres(carte.racine, "carte-combat-hit");
            afficherNombreFlottant(carte.conteneur, "-" + (int) Math.round(-deltaVie), "#ff6b6b", critique);
        } else if (deltaVie > 0) {
            carte.racine.getStyleClass().add("carte-combat-heal");
            retirerApres(carte.racine, "carte-combat-heal");
            afficherNombreFlottant(carte.conteneur, "+" + (int) Math.round(deltaVie), "#6bffa0", false);
        }
    }

    /** Anime la largeur d'une barre (PV/rage) vers sa nouvelle valeur au lieu d'un saut instantane. */
    private void animerBarre(Rectangle barre, double largeurCible) {
        Timeline anim = new Timeline(new KeyFrame(Duration.millis(350),
                new KeyValue(barre.widthProperty(), largeurCible, Interpolator.EASE_OUT)));
        anim.play();
    }

    /** Fait apparaitre un nombre (degats/soin) au-dessus de la carte, qui monte et s'estompe. */
    private void afficherNombreFlottant(StackPane conteneur, String texte, String couleurHex, boolean critique) {
        VBox pile = new VBox(0);
        pile.setAlignment(Pos.CENTER);
        pile.setMouseTransparent(true);

        if (critique) {
            Label critiqueLabel = new Label("CRITIQUE !");
            critiqueLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #f2c14e;"
                    + " -fx-effect: dropshadow(gaussian, black, 3, 0.6, 0, 0);");
            pile.getChildren().add(critiqueLabel);
        }

        Label nombre = new Label(texte);
        nombre.setStyle("-fx-font-size: " + (critique ? "20px" : "16px") + "; -fx-font-weight: bold; -fx-text-fill: "
                + couleurHex + "; -fx-effect: dropshadow(gaussian, black, 3, 0.6, 0, 0);");
        pile.getChildren().add(nombre);

        StackPane.setAlignment(pile, Pos.TOP_CENTER);
        pile.setTranslateY(-10);
        conteneur.getChildren().add(pile);

        TranslateTransition monte = new TranslateTransition(Duration.millis(900), pile);
        monte.setByY(-30);
        FadeTransition disparait = new FadeTransition(Duration.millis(900), pile);
        disparait.setFromValue(1);
        disparait.setToValue(0);

        ParallelTransition anim = new ParallelTransition(monte, disparait);
        anim.setOnFinished(e -> conteneur.getChildren().remove(pile));
        anim.play();
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
        surlignerActeur(evt.titre);
        mettreAJourOrdreTours();

        boolean critique = evt.lignes.contains("Coup critique !");
        for (int i = 0; i < evt.etat.size(); i++) {
            PersonnageSnapshot avant = etatPrecedent.get(i);
            PersonnageSnapshot apres = evt.etat.get(i);
            appliquerEtat(cartes[i], apres, apres.vie - avant.vie, critique);
        }

        if (evt.actionNom != null) {
            afficherBanniereAction(evt.titre, evt.actionNom, evt.estUltime);
            jouerSonAction(evt.titre, evt.actionNom, evt.estUltime);
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

    /** Met en evidence la/les carte(s) du personnage qui agit ce pas-ci (aucune correspondance
     *  pour les evenements meta comme "Tour N" ou "Effets" : rien n'est surligne). */
    private void surlignerActeur(String nomActeur) {
        for (CarteCombat carte : cartes) {
            carte.racine.getStyleClass().remove("carte-combat-active");
        }
        if (nomActeur == null) return;
        for (CarteCombat carte : cartes) {
            if (nomActeur.equals(carte.nom)) {
                carte.racine.getStyleClass().add("carte-combat-active");
            }
        }
    }

    /** Banniere temporaire annoncant le declenchement d'une speciale ou d'un ultime (plus
     *  marquee/plus longue pour un ultime, plus discrete pour une speciale). */
    /** Joue le son associe a cette speciale/ultime, si un a ete fourni pour ce personnage, en
     *  baissant brievement la musique de fond pour que les deux ne se superposent pas trop fort. */
    private void jouerSonAction(String nomPerso, String nomAction, boolean estUltime) {
        AudioClip clip = SONS_ACTIONS.get(nomPerso + "|" + nomAction);
        if (clip == null) return;
        clip.play();
        GestionnaireMusique.attenuerPendant(Duration.millis(estUltime ? 1600 : 900));
    }

    private void afficherBanniereAction(String nomPerso, String nomAction, boolean estUltime) {
        ultimeLabel.setText(nomPerso + " lance " + nomAction + " !");
        ultimeLabel.getStyleClass().removeAll("banniere-ultime", "banniere-speciale");
        ultimeLabel.getStyleClass().add(estUltime ? "banniere-ultime" : "banniere-speciale");
        ultimeLabel.setOpacity(0);
        ultimeLabel.setVisible(true);
        ultimeLabel.setManaged(true);

        FadeTransition apparait = new FadeTransition(Duration.millis(200), ultimeLabel);
        apparait.setFromValue(0);
        apparait.setToValue(1);
        PauseTransition maintien = new PauseTransition(Duration.millis(estUltime ? 1400 : 700));
        FadeTransition disparait = new FadeTransition(Duration.millis(400), ultimeLabel);
        disparait.setFromValue(1);
        disparait.setToValue(0);

        SequentialTransition sequence = new SequentialTransition(apparait, maintien, disparait);
        sequence.setOnFinished(e -> {
            ultimeLabel.setVisible(false);
            ultimeLabel.setManaged(false);
        });
        sequence.play();
    }

    private void mettreAJourBoutons() {
        boolean fini = index >= evenements.size();
        suivantButton.setDisable(fini);
        autoButton.setDisable(fini);
        passerButton.setDisable(fini);
    }

    private void terminerAffichage() {
        arreterAuto();
        ordreBox.getChildren().clear();
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
        if (autoPause != null) {
            autoPause.stop();
            autoPause = null;
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
        if (autoPause != null) {
            arreterAuto();
            return;
        }
        autoButton.setText("Pause ⏸");
        jouerProchaineEtapeAuto();
    }

    /**
     * Enchaine les etapes en lecture auto avec une pause adaptee a chaque etape (plus longue
     * quand une speciale/un ultime vient de se declencher, pour laisser le temps de voir la
     * banniere), plutot qu'un intervalle fixe qui les rendrait illisibles.
     */
    private void jouerProchaineEtapeAuto() {
        if (index >= evenements.size()) {
            arreterAuto();
            return;
        }
        CombatEvent prochain = evenements.get(index);
        Duration pause = prochain.actionNom == null ? DUREE_AUTO
                : prochain.estUltime ? DUREE_AUTO_ULTIME : DUREE_AUTO_SPECIALE;
        avancerEtape();
        if (index >= evenements.size()) {
            arreterAuto();
            return;
        }
        autoPause = new PauseTransition(pause);
        autoPause.setOnFinished(e -> jouerProchaineEtapeAuto());
        autoPause.play();
    }

    @FXML
    private void onPasser() {
        arreterAuto();
        while (index < evenements.size()) {
            CombatEvent evt = evenements.get(index);
            index++;
            for (int i = 0; i < evt.etat.size(); i++) {
                appliquerEtat(cartes[i], evt.etat.get(i), 0);
            }
            if (!evt.lignes.isEmpty()) {
                logArea.appendText(String.join("\n", evt.lignes) + "\n\n");
            }
            etatPrecedent = evt.etat;
        }
        surlignerActeur(null);
        ordreBox.getChildren().clear();
        ultimeLabel.setVisible(false);
        ultimeLabel.setManaged(false);
        etapeLabel.setText("Combat termine");
        terminerAffichage();
    }

    @FXML
    private void onContinuer() {
        GestionnaireMusique.arreter();
        if (onTermine != null) onTermine.accept(victoire);
    }

    /** Sortie rapide en un clic, quel que soit l'avancement (avant meme le premier tour) : passe
     * directement a la fin du combat deja resolu et revient a l'ecran precedent. */
    @FXML
    private void onQuitter() {
        if (index < evenements.size()) {
            onPasser();
        }
        onContinuer();
    }
}
