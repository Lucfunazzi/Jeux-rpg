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
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Rejoue visuellement un combat deja resolu (voir Combat.lancerCombatEnregistre) :
 * une carte par personnage avec barres de PV/rage, et une progression tour par
 * tour (pas a pas, lecture auto, ou passage direct a la fin).
 */
public class EcranCombatController {

    private static final double LARGEUR_BARRE = 92;
    private static final double LARGEUR_CARTE = 108;
    private static final Duration DUREE_AUTO          = Duration.millis(900);
    private static final Duration DUREE_AUTO_SPECIALE = Duration.millis(2200);
    private static final Duration DUREE_AUTO_ULTIME   = Duration.millis(3200);
    private static final Duration DUREE_FLASH = Duration.millis(500);

    @FXML private Label etapeLabel;
    @FXML private Label banniereLabel;
    @FXML private Label ultimeLabel;
    @FXML private HBox ordreBox;
    @FXML private GridPane equipeJoueurBox;
    @FXML private GridPane equipeAdverseBox;
    @FXML private Pane overlayCombat;
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
        final boolean coteJoueur;
        final StackPane conteneur;
        final VBox racine;
        final Rectangle barrePV;
        final Rectangle barreRage;
        final Label pvTexte;
        final Label rageTexte;
        final FlowPane effetsBox;
        final Label koTampon;
        final StackPane barreBouclierPane;
        final Rectangle barreBouclier;
        final Label bouclierTexte;

        // Sprite anime hors-carte (voir overlayCombat), null si aucun asset fourni pour ce personnage.
        final SpriteSetPersonnage sprites;
        final ImageView spriteView;
        final StackPane spriteHolder;
        Timeline animSprite;

        // Mini-infobulle (nom/niveau + barres PV/rage) affichee au survol du sprite (voir
        // creerInfobulleSprite). Null si le personnage n'a pas de sprite.
        StackPane infobulle;
        Label infobulleTexte;
        Rectangle infobullePV;
        Rectangle infobulleRage;
        Label infobullePvTexte;
        Label infobulleRageTexte;

        // Plaquette nom + barre de PV affichee EN PERMANENCE au-dessus de la tete du sprite
        // (comme dans Fairy Tail Hero's Journey), independante de l'infobulle au survol.
        VBox plaquetteNom;
        Rectangle plaquetteBarrePV;

        // Effets actifs (buffs/debuffs) : affiches en PERMANENCE juste derriere le sprite (pas au
        // survol), sur 1 ou 2 colonnes verticales selon leur nombre (voir creerInfobulleSprite).
        FlowPane effetsPersistants;

        // Ombre au sol suivant les pieds du sprite (ancrage visuel sur l'arene, voir creerOmbreSol).
        Ellipse ombreSol;

        CarteCombat(String nom, boolean coteJoueur, StackPane conteneur, VBox racine, Rectangle barrePV, Rectangle barreRage,
                    Label pvTexte, Label rageTexte, FlowPane effetsBox, Label koTampon,
                    StackPane barreBouclierPane, Rectangle barreBouclier, Label bouclierTexte,
                    SpriteSetPersonnage sprites, ImageView spriteView, StackPane spriteHolder) {
            this.nom = nom;
            this.coteJoueur = coteJoueur;
            this.conteneur = conteneur;
            this.racine = racine;
            this.barrePV = barrePV;
            this.barreRage = barreRage;
            this.pvTexte = pvTexte;
            this.rageTexte = rageTexte;
            this.effetsBox = effetsBox;
            this.koTampon = koTampon;
            this.barreBouclierPane = barreBouclierPane;
            this.barreBouclier = barreBouclier;
            this.bouclierTexte = bouclierTexte;
            this.sprites = sprites;
            this.spriteView = spriteView;
            this.spriteHolder = spriteHolder;
        }
    }

    /** Taille fixe du support invisible qui porte le sprite d'un personnage sur le champ de
     *  bataille (voir overlayCombat) : une taille stable evite que le point d'ancrage ne saute
     *  d'une frame a l'autre alors que la largeur reelle de l'image varie (idle vs attaque...).
     *  Volontairement compacte : jusqu'a 5 personnages par camp doivent tenir cote a cote sur
     *  l'arene sans se chevaucher ni deborder de l'ecran. */
    private static final double LARGEUR_SPRITE_HOLDER = 130;
    private static final double HAUTEUR_SPRITE_HOLDER = 118;
    /** Hauteur d'affichage du sprite sur le champ de bataille. */
    private static final double HAUTEUR_SPRITE_COMBAT = 92;
    private static final double LARGEUR_INFOBULLE = 100;
    /** Largeur de la plaquette nom/PV flottant au-dessus de la tete (voir creerPlaquetteNom) :
     *  volontairement etroite pour ne jamais deborder du personnage, meme un petit sprite. */
    private static final double LARGEUR_PLAQUETTE = 48;

    /**
     * Jeu d'animations sprite d'un personnage.
     * {@code windup} est la pose d'invocation, commune aux 2 competences.
     * {@code specialEntite}/{@code ultimeEntite} sont les frames de l'esprit invoque, jouees au
     * centre du champ de bataille (voir jouerInvocationCentrale) : Taurus frappe, Aquarius
     * apparait et dechaine la vague.
     * {@code specialImpact}/{@code ultimeImpact} sont un effet plus petit rejoue sur CHAQUE
     * cible reellement touchee (ex. le sol qui se brise sous Taurus) ; null si l'effet central
     * suffit a lui seul (pas d'effet dedie par cible).
     * Un tableau null signifie "pas d'asset pour cette phase" : l'appelant garde la pose courante.
     * Si {@code specialEntite}/{@code ultimeEntite} est null, la competence n'invoque rien : voir
     * jouerAnimationAttaque, l'attaquant fonce alors lui-meme sur la cible (comme une attaque de
     * base) au lieu de rester sur place a invoquer un esprit au centre du champ.
     * {@code baseADistance} : true pour une attaque de base a distance (l'attaquant reste sur
     * place, l'animation montre deja le projectile partir) ; false (defaut) pour qu'il se
     * deplace jusqu'a la cible comme une attaque au corps a corps.
     * {@code msBase}/{@code msSpecial}/{@code msUltime} : duree (ms) de chaque frame pour
     * l'attaque de base / la speciale (windup+entite) / l'ultime (windup+entite) de CE personnage
     * — reglable individuellement puisque les feuilles de sprites n'ont pas toutes le meme rythme.
     */
    private record SpriteSetPersonnage(Image[] idle, Image[] hurt, Image[] attaqueBase,
                                        Image[] specialWindup, Image[] specialEntite, Image[] specialImpact,
                                        Image[] ultimeWindup, Image[] ultimeEntite, Image[] ultimeImpact,
                                        boolean baseADistance, int msBase, int msSpecial, int msUltime) {}

    private static final Map<String, SpriteSetPersonnage> SPRITES_PERSONNAGES = new HashMap<>();
    static {
        Image[] windupLucy = chargerFramesSprite("lucy", "windup", 11);
        SPRITES_PERSONNAGES.put("Lucy", new SpriteSetPersonnage(
                chargerFramesSprite("lucy", "idle", 6),
                chargerFramesSprite("lucy", "hurt", 3),
                chargerFramesSprite("lucy", "attaque_base", 12),
                windupLucy,
                chargerFramesSprite("lucy", "taurus_entity", 3),
                chargerFramesSprite("lucy", "taurus_dust", 3),
                windupLucy,
                concat(chargerFramesSprite("lucy", "aquarius_mermaid", 4),
                       concat(chargerFramesSprite("lucy", "aquarius_tornado_a", 6),
                              chargerFramesSprite("lucy", "aquarius_tornado_b", 3))),
                // Meme principe que l'effet "terre brisee" de Taurus : un petit tourbillon d'eau
                // rejoue sur CHAQUE cible reellement touchee, pour que la vague semble vraiment
                // les atteindre plutot que de rester un simple decor au centre de l'ecran.
                chargerFramesSprite("lucy", "aquarius_tornado_b", 3),
                false, 110, 170, 190));

        SPRITES_PERSONNAGES.put("Angel", new SpriteSetPersonnage(
                chargerFramesSprite("angel", "idle", 4),
                chargerFramesSprite("angel", "hurt", 2),
                chargerFramesSprite("angel", "attaque_base", 4),
                chargerFramesSprite("angel", "special_windup", 4),
                chargerFramesSprite("angel", "special_entite", 2),
                chargerFramesSprite("angel", "special_impact", 2),
                chargerFramesSprite("angel", "ultime_windup", 4),
                chargerFramesSprite("angel", "ultime_entite", 5),
                chargerFramesSprite("angel", "ultime_impact", 1),
                false, 110, 170, 190));

        // Natsu n'invoque rien : sa speciale (karyuu no tekken) n'a pas d'entite, donc l'attaquant
        // fonce lui-meme sur la cible (voir jouerAnimationAttaque) et l'etincelle "Effect if hit"
        // du sprite sheet tombe sur chaque cible touchee, comme les impacts de Lucy. Son ultime
        // (karyuu no houkou) garde le split windup/entite : il rassemble la flamme sur place, puis
        // le souffle (boule de feu qui grossit + vague) est joue au centre, comme la vague d'Aquarius.
        SPRITES_PERSONNAGES.put("Natsu", new SpriteSetPersonnage(
                chargerFramesSprite("natsu", "idle", 4),
                chargerFramesSprite("natsu", "hurt", 4),
                chargerFramesSprite("natsu", "attaque_base", 7),
                chargerFramesSprite("natsu", "special_windup", 10),
                null,
                chargerFramesSprite("natsu", "special_impact", 1),
                chargerFramesSprite("natsu", "ultime_windup", 4),
                chargerFramesSprite("natsu", "ultime_entite", 14),
                null,
                false, 130, 210, 220));

        // Gray : l'attaque de base (Fleche de glace) est a distance, il reste donc sur place et
        // tire (baseADistance=true) au lieu de foncer sur la cible. La speciale (Marteau de glace)
        // invoque un unique bloc de glace immobile (comme Taurus pour Lucy), sans impact dedie par
        // cible. L'ultime (Canon de glace) tire un eclat qui vole au centre, avec l'eclat d'impact
        // blanc rejoue sur chaque cible touchee.
        SPRITES_PERSONNAGES.put("Gray", new SpriteSetPersonnage(
                chargerFramesSprite("gray", "idle", 4),
                chargerFramesSprite("gray", "hurt", 2),
                chargerFramesSprite("gray", "attaque_base", 4),
                chargerFramesSprite("gray", "special_windup", 3),
                chargerFramesSprite("gray", "special_entite", 1),
                null,
                chargerFramesSprite("gray", "ultime_windup", 3),
                chargerFramesSprite("gray", "ultime_entite", 1),
                chargerFramesSprite("gray", "ultime_impact", 1),
                true, 140, 220, 240));

        // Erza n'invoque rien pour sa speciale (lance de foudre) ni son ultime (armure des
        // flammes) : les deux sont des attaques a l'arme propre, donc l'attaquant fonce lui-meme
        // sur la cible (voir jouerAnimationAttaque), comme la speciale de Natsu.
        SPRITES_PERSONNAGES.put("Erza", new SpriteSetPersonnage(
                chargerFramesSprite("erza", "idle", 4),
                chargerFramesSprite("erza", "hurt", 2),
                chargerFramesSprite("erza", "attaque_base", 10),
                chargerFramesSprite("erza", "special_windup", 8),
                null,
                null,
                chargerFramesSprite("erza", "ultime_windup", 8),
                null,
                null,
                false, 120, 200, 220));

        // Leon (Lyon) n'invoque rien non plus : sa speciale (Ice-Make Aigle) et son ultime
        // (Ice-Make Gorille) sont toutes deux lancees a bout portant, donc l'attaquant fonce
        // lui-meme sur la cible (voir jouerAnimationAttaque), meme principe que Natsu/Erza.
        SPRITES_PERSONNAGES.put("Leon", new SpriteSetPersonnage(
                chargerFramesSprite("leon", "idle", 4),
                chargerFramesSprite("leon", "hurt", 2),
                chargerFramesSprite("leon", "attaque_base", 6),
                chargerFramesSprite("leon", "special_windup", 8),
                null,
                null,
                chargerFramesSprite("leon", "ultime_windup", 10),
                null,
                null,
                false, 120, 200, 220));
    }

    private static Image[] chargerFramesSprite(String dossier, String prefixe, int nombre) {
        Image[] frames = new Image[nombre];
        for (int i = 1; i <= nombre; i++) {
            String chemin = "/images/personnages/" + dossier + "/" + prefixe + "_" + i + ".png";
            try (var flux = EcranCombatController.class.getResourceAsStream(chemin)) {
                if (flux == null) {
                    System.err.println("[Sprite] introuvable : " + chemin);
                    return null;
                }
                frames[i - 1] = new Image(flux);
            } catch (Exception e) {
                System.err.println("[Sprite] erreur chargement " + chemin + " : " + e.getMessage());
                return null;
            }
        }
        return frames;
    }

    private static Image[] concat(Image[] a, Image[] b) {
        if (a == null || b == null) return null;
        Image[] res = new Image[a.length + b.length];
        System.arraycopy(a, 0, res, 0, a.length);
        System.arraycopy(b, 0, res, a.length, b.length);
        return res;
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
        chargerSonAction("Duc Everlue", "Invocation de Virgo", "/audio/duc_everlue_invocation_virgo.wav");
        chargerSonAction("Yuka", "Onde Explosive", "/audio/yuka_onde_explosive.wav");
        chargerSonAction("Yuka", "Annulation Totale des Magies", "/audio/yuka_annulation_totale.wav");
        chargerSonAction("Cherry", "Je Fais de Toi ma Marionnette", "/audio/cherry_je_fais_de_toi_ma_marionnette.wav");
        chargerSonAction("Cherry", "Grand Arbre Marionnette", "/audio/cherry_grand_arbre_marionnette.wav");
        chargerSonAction("Eligoal", "Armure des Vents", "/audio/eligoal_armure_des_vents.wav");
        chargerSonAction("Eligoal", "Déclencheur de Tornade", "/audio/eligoal_declencheur_tornade.wav");
        chargerSonAction("Tobi", "Super Griffe Paralysante", "/audio/tobi_super_griffe_paralysante.wav");
        chargerSonAction("Tobi", "Super Griffe Paralysante — Méga Méduse", "/audio/tobi_super_griffe_paralysante_mega_meduse.wav");
        chargerSonAction("Jubia", "Déferlante", "/audio/jubia_deferlante.wav");
        chargerSonAction("Jubia", "Prison d'eau", "/audio/jubia_prison_eau.wav");
        chargerSonAction("Jellal", "Grand Chariot", "/audio/jellal_grand_chariot.wav");
        chargerSonAction("Jellal", "Altairis", "/audio/altaris_jellal.wav");
        chargerSonAction("Ikaruga", "Flammes du Garuda", "/audio/ikaruga_flammes_garuda.wav");
        chargerSonAction("Ikaruga", "Eclats des Esprits", "/audio/ikaruga_eclats_esprits.wav");
        chargerSonAction("Miliana", "Kitten Blast", "/audio/miliana_kitten_blast.wav");
        chargerSonAction("Miliana", "Entraves Féline multiples ", "/audio/entravesmultiples_miliana.wav");
        chargerSonAction("Simon", "Nuit fugitive", "/audio/nuit_fugitif_simon.wav");
        chargerSonAction("Simon", "Sombre Explosion", "/audio/nuit_fugitif_simon.wav");
        chargerSonAction("Wolly", "Pistolet des Polygones", "/audio/wolly-attaque-special.wav");
        chargerSonAction("Owl", "Mise à feu", "/audio/owl_mise_a_feu.wav");
        chargerSonAction("Owl", "Missiles HOU HOU ", "/audio/owl_missiles_hou_hou.wav");
        chargerSonAction("Vivaldus", "Rock of Succubus", "/audio/vivaldus_rock_succubus.wav");
        chargerSonAction("Vivaldus", "Rock and roll", "/audio/vivaldus_rock_and_roll.wav");
        chargerSonAction("Jura", "Montagne", "/audio/jura_montagne.wav");
        chargerSonAction("Jura", "Grondement du Mont Fuji", "/audio/jura_grondement_mont_fuji.wav");
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
        INFOS_EFFETS.put("ImmuniteControle",  new EffetInfo("Bloque Etourdissement, Paralysie, Sommeil, Petrification et Gel.", true, "#cfd8e3"));

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
        // Differe le placement des sprites au prochain pulse : les cartes n'ont pas encore de
        // bounds valides tant qu'un premier passage de layout n'a pas eu lieu sur la scene.
        Platform.runLater(this::placerSpritesInitiaux);

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

    /** Formation en colonnes par role (voir EcranCombat.fxml) : Support en retrait, DPS au
     *  milieu, Tank au contact de la ligne de front adverse — plusieurs personnages du meme role
     *  s'empilent verticalement dans leur colonne. */
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
            List<Integer> membres = colonnes.get(col);

            // Si tous les membres de cette colonne ont un sprite (donc pas de carte affichee,
            // voir creerCarte), l'entete de groupe n'a plus rien a chapeauter : on l'omet pour
            // eviter un titre "SUPPORT" flottant au-dessus d'un espace vide. Les cartes (invisibles)
            // sont ajoutees plus bas quand meme : elles servent d'ancrage de position au sprite.
            boolean aUnMembreVisible = membres.stream().anyMatch(i -> cartes[i].spriteHolder == null);
            if (aUnMembreVisible) {
                Label entete = new Label(titres.get(col));
                entete.getStyleClass().add("groupe-titre");
                GridPane.setHalignment(entete, HPos.CENTER);
                grille.add(entete, col, 0);
            }

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
        StackPane barrePVPane = creerBarre(12);
        Rectangle barrePV = (Rectangle) barrePVPane.getChildren().get(1);

        Label bouclierTexte = new Label();
        bouclierTexte.getStyleClass().add("texte");
        bouclierTexte.setStyle("-fx-font-size: 10px; -fx-text-fill: #5bc8e8;");
        StackPane barreBouclierPane = creerBarre(6);
        Rectangle barreBouclier = (Rectangle) barreBouclierPane.getChildren().get(1);
        barreBouclier.setFill(Color.web("#5bc8e8"));
        barreBouclier.setWidth(0);

        Label rageTexte = new Label();
        rageTexte.getStyleClass().add("texte");
        rageTexte.setStyle("-fx-font-size: 11px;");
        StackPane barreRagePane = creerBarre(12);
        Rectangle barreRage = (Rectangle) barreRagePane.getChildren().get(1);

        FlowPane effetsBox = new FlowPane(4, 4);
        effetsBox.setPrefWrapLength(LARGEUR_BARRE);

        VBox racine = new VBox(4, nomLabel, raretteLabel, pvTexte, barrePVPane,
                bouclierTexte, barreBouclierPane, rageTexte, barreRagePane, effetsBox);
        racine.getStyleClass().addAll("carte-combat", classeRole(snap.role));
        racine.setAlignment(Pos.CENTER);
        racine.setPrefWidth(LARGEUR_CARTE);

        // Le sprite anime n'est PAS insere dans la carte : il vit dans overlayCombat (voir
        // placerSpritesInitiaux) pour pouvoir se deplacer librement sur tout le champ de bataille
        // (s'approcher d'une cible, etc.) sans etre contraint par la mise en page de la carte.
        SpriteSetPersonnage sprites = SPRITES_PERSONNAGES.get(snap.nom);
        ImageView spriteView = null;
        StackPane spriteHolder = null;
        if (sprites != null && sprites.idle() != null) {
            spriteView = new ImageView(sprites.idle()[0]);
            spriteView.setPreserveRatio(true);
            spriteView.setFitHeight(HAUTEUR_SPRITE_COMBAT);
            spriteView.setSmooth(false);
            // Les sprites sont dessines faisant face au camp joueur : le camp adverse doit donc
            // etre miroir (image ET animations, puisqu'elles partagent toutes ce meme ImageView).
            if (!snap.coteJoueur) spriteView.setScaleX(-1);
            spriteHolder = new StackPane(spriteView);
            spriteHolder.setAlignment(Pos.BOTTOM_CENTER);
            spriteHolder.setPrefSize(LARGEUR_SPRITE_HOLDER, HAUTEUR_SPRITE_HOLDER);
        }

        Label koTampon = new Label("K.O.");
        koTampon.getStyleClass().add("ko-tampon");
        koTampon.setVisible(false);
        koTampon.setManaged(false);

        // StackPane englobant : sert de support aux nombres de degats/soins flottants et au
        // tampon K.O., affiches par-dessus la carte sans perturber sa mise en page.
        StackPane conteneur = new StackPane(racine);
        conteneur.setPrefWidth(LARGEUR_CARTE);
        StackPane.setAlignment(racine, Pos.CENTER);

        if (spriteHolder != null) {
            // Personnage avec sprite : plus de carte texte affichee (juste le sprite sur le
            // champ de bataille) — le tampon K.O. et les nombres flottants s'affichent donc sur
            // le sprite lui-meme, pas sur la carte (cachee, gardee uniquement comme point
            // d'ancrage de position — voir positionAncrage).
            spriteHolder.getChildren().add(koTampon);
            StackPane.setAlignment(koTampon, Pos.CENTER);
            conteneur.setVisible(false);
        } else {
            conteneur.getChildren().add(koTampon);
            StackPane.setAlignment(koTampon, Pos.CENTER);
        }

        CarteCombat carte = new CarteCombat(snap.nom, snap.coteJoueur, conteneur, racine, barrePV, barreRage, pvTexte, rageTexte,
                effetsBox, koTampon, barreBouclierPane, barreBouclier, bouclierTexte, sprites, spriteView, spriteHolder);
        if (spriteHolder != null) creerInfobulleSprite(carte);
        appliquerEtat(carte, snap, 0);
        return carte;
    }

    /** Conteneur visuel qui doit recevoir les nombres flottants/tampon K.O. d'un personnage : son
     *  sprite s'il en a un (visible en jeu), sinon sa carte (cas normal). */
    private StackPane conteneurVisuel(CarteCombat carte) {
        return carte.spriteHolder != null ? carte.spriteHolder : carte.conteneur;
    }

    /** Element sur lequel appliquer les classes CSS de flash (coup recu/soin/K.O./tour actif) :
     *  le support du sprite s'il en a un, sinon la carte texte. */
    private Region elementFlash(CarteCombat carte) {
        return carte.spriteHolder != null ? carte.spriteHolder : carte.racine;
    }

    /** Petite infobulle (nom, niveau, barre PV, barre rage) affichee UNIQUEMENT au survol du
     *  sprite d'un personnage sur le champ de bataille — puisque son sprite n'est plus rive a sa
     *  carte (il se deplace pendant les attaques), survoler l'endroit ou il se trouve permet de
     *  l'identifier sans devoir chercher sa carte fixe. Cachee par defaut, mise a jour en continu
     *  par appliquerEtat pour toujours refleter l'etat courant quand elle s'affiche. Les effets
     *  actifs (buffs/debuffs) sont geres a part par creerEffetsPersistants : eux restent visibles
     *  en permanence, pas seulement au survol. */
    private void creerInfobulleSprite(CarteCombat carte) {
        Label texte = new Label();
        texte.setStyle("-fx-font-size: 10px; -fx-text-fill: white; -fx-font-weight: bold;");

        Rectangle fondPV = new Rectangle(LARGEUR_INFOBULLE, 12, Color.web("#12121c"));
        Rectangle barrePV = new Rectangle(LARGEUR_INFOBULLE, 12, Color.web("#56c98a"));
        Label pvTexte = new Label();
        pvTexte.setStyle("-fx-font-size: 9px; -fx-text-fill: white; -fx-effect: dropshadow(gaussian, black, 2, 0.8, 0, 0);");
        StackPane pvPane = new StackPane(fondPV, barrePV, pvTexte);
        StackPane.setAlignment(fondPV, Pos.CENTER_LEFT);
        StackPane.setAlignment(barrePV, Pos.CENTER_LEFT);

        Rectangle fondRage = new Rectangle(LARGEUR_INFOBULLE, 10, Color.web("#12121c"));
        Rectangle barreRage = new Rectangle(LARGEUR_INFOBULLE, 10, Color.web("#4ea8f2"));
        Label rageTexte = new Label();
        rageTexte.setStyle("-fx-font-size: 9px; -fx-text-fill: white; -fx-effect: dropshadow(gaussian, black, 2, 0.8, 0, 0);");
        StackPane ragePane = new StackPane(fondRage, barreRage, rageTexte);
        StackPane.setAlignment(fondRage, Pos.CENTER_LEFT);
        StackPane.setAlignment(barreRage, Pos.CENTER_LEFT);

        VBox contenu = new VBox(4, texte, pvPane, ragePane);
        contenu.setAlignment(Pos.CENTER);
        contenu.setPadding(new javafx.geometry.Insets(6));
        contenu.setStyle("-fx-background-color: rgba(10,10,20,0.9); -fx-background-radius: 6;"
                + " -fx-border-color: #f2c14e; -fx-border-radius: 6; -fx-border-width: 1;");

        StackPane infobulle = new StackPane(contenu);
        infobulle.setMouseTransparent(true);
        infobulle.setVisible(false);

        carte.infobulle = infobulle;
        carte.infobulleTexte = texte;
        carte.infobullePV = barrePV;
        carte.infobulleRage = barreRage;
        carte.infobullePvTexte = pvTexte;
        carte.infobulleRageTexte = rageTexte;

        carte.spriteHolder.setOnMouseEntered(e -> afficherInfobulle(carte));
        carte.spriteHolder.setOnMouseExited(e -> {
            infobulle.setVisible(false);
            infobulle.setManaged(false);
        });

        creerEffetsPersistants(carte);
        creerPlaquetteNom(carte);
        creerOmbreSol(carte);
    }

    /** Positionne l'infobulle juste a cote de l'endroit ACTUEL du sprite (pas sa position de
     *  repos : il peut etre en plein deplacement lors d'une attaque) et l'affiche. */
    private void afficherInfobulle(CarteCombat carte) {
        // Ancre sur le CENTRE du support (pas son sommet) : avec un sprite haut de 140px et un
        // support encore plus grand, ancrer depuis le haut pouvait pousser l'infobulle au-dessus
        // du haut de la fenetre (donc invisible). Affichee a cote, verticalement centree, elle
        // reste toujours dans la zone visible quelle que soit la hauteur du personnage a l'ecran.
        Bounds enScene = carte.spriteHolder.localToScene(carte.spriteHolder.getBoundsInLocal());
        Point2D centre = overlayCombat.sceneToLocal(
                enScene.getMinX() + enScene.getWidth() / 2, enScene.getMinY() + enScene.getHeight() / 2);
        carte.infobulle.setLayoutX(centre.getX() + LARGEUR_SPRITE_HOLDER / 2 - 20);
        carte.infobulle.setLayoutY(Math.max(4, centre.getY() - 45));
        carte.infobulle.setManaged(true);
        carte.infobulle.setVisible(true);
        carte.infobulle.toFront();
    }

    /** Icones des effets actifs (buffs/debuffs), affichees en PERMANENCE juste derriere le sprite
     *  (ajoutees a overlayCombat avant lui, donc dessinees sous son corps) plutot qu'au survol :
     *  contrairement aux PV/rage, savoir qu'un effet est actif ne devrait pas demander de survoler
     *  le personnage. Empilees en colonne verticale, qui se scinde en 2 colonnes cote a cote des
     *  qu'il y a trop d'effets pour tenir dans une seule (voir FlowPane.orientation VERTICAL). */
    private void creerEffetsPersistants(CarteCombat carte) {
        FlowPane effets = new FlowPane(Orientation.VERTICAL, 3, 3);
        effets.setPrefWrapLength(72); // hauteur avant de passer a une 2e colonne (~3 icones de 20px)
        effets.setAlignment(Pos.TOP_CENTER);
        effets.setMouseTransparent(true);

        carte.effetsPersistants = effets;
        if (!overlayCombat.getChildren().contains(effets)) {
            overlayCombat.getChildren().add(effets);
        }

        carte.spriteHolder.layoutXProperty().addListener((o, a, b) -> positionnerEffetsPersistants(carte));
        carte.spriteHolder.layoutYProperty().addListener((o, a, b) -> positionnerEffetsPersistants(carte));
        carte.spriteHolder.translateXProperty().addListener((o, a, b) -> positionnerEffetsPersistants(carte));
        carte.spriteHolder.translateYProperty().addListener((o, a, b) -> positionnerEffetsPersistants(carte));
    }

    /** Repositionne les icones d'effets juste derriere le sprite (legerement decalees vers
     *  l'arriere/le cote), en suivant sa position ACTUELLE (deplacement pendant une attaque
     *  compris). Appelee en continu (voir creerEffetsPersistants). */
    private void positionnerEffetsPersistants(CarteCombat carte) {
        if (carte.effetsPersistants == null) return;
        Bounds enScene = carte.spriteHolder.localToScene(carte.spriteHolder.getBoundsInLocal());
        Point2D centre = overlayCombat.sceneToLocal(
                enScene.getMinX() + enScene.getWidth() / 2, enScene.getMinY() + enScene.getHeight() / 2);
        carte.effetsPersistants.setLayoutX(centre.getX() - LARGEUR_SPRITE_HOLDER / 2 - 6);
        carte.effetsPersistants.setLayoutY(centre.getY() - 40);
    }

    /** Nom + barre de PV affiches EN PERMANENCE juste au-dessus de la tete du sprite (comme dans
     *  Fairy Tail Hero's Journey), sans avoir besoin de survoler. Mise a jour en continu par
     *  appliquerEtat. Independant de l'infobulle au survol (qui donne les chiffres precis).
     *  Volontairement etroite (LARGEUR_PLAQUETTE) : elle ne doit jamais deborder du personnage,
     *  le nom est donc tronque avec "…" plutot que de s'etaler plus large que le sprite. */
    private void creerPlaquetteNom(CarteCombat carte) {
        Label nom = new Label(carte.nom);
        nom.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-effect: dropshadow(gaussian, black, 3, 0.7, 0, 0);");
        nom.setMaxWidth(LARGEUR_PLAQUETTE);

        Rectangle fond = new Rectangle(LARGEUR_PLAQUETTE, 6, Color.web("#12121c"));
        Rectangle remplissage = new Rectangle(LARGEUR_PLAQUETTE, 6, Color.web("#56c98a"));
        StackPane barre = new StackPane(fond, remplissage);
        StackPane.setAlignment(fond, Pos.CENTER_LEFT);
        StackPane.setAlignment(remplissage, Pos.CENTER_LEFT);

        VBox plaquette = new VBox(2, nom, barre);
        plaquette.setAlignment(Pos.CENTER);
        plaquette.setMouseTransparent(true);

        carte.plaquetteNom = plaquette;
        carte.plaquetteBarrePV = remplissage;
        if (!overlayCombat.getChildren().contains(plaquette)) {
            overlayCombat.getChildren().add(plaquette);
        }

        carte.spriteHolder.layoutXProperty().addListener((o, a, b) -> positionnerPlaquetteNom(carte));
        carte.spriteHolder.layoutYProperty().addListener((o, a, b) -> positionnerPlaquetteNom(carte));
        carte.spriteHolder.translateXProperty().addListener((o, a, b) -> positionnerPlaquetteNom(carte));
        carte.spriteHolder.translateYProperty().addListener((o, a, b) -> positionnerPlaquetteNom(carte));
    }

    /** Repositionne la plaquette nom/PV juste au-dessus de la tete ACTUELLE du sprite (pas sa
     *  position de repos). Appelee en continu (voir creerPlaquetteNom). */
    private void positionnerPlaquetteNom(CarteCombat carte) {
        if (carte.plaquetteNom == null) return;
        Bounds enScene = carte.spriteHolder.localToScene(carte.spriteHolder.getBoundsInLocal());
        Point2D centreHaut = overlayCombat.sceneToLocal(
                enScene.getMinX() + enScene.getWidth() / 2,
                // Le support est plus haut que le sprite lui-meme (marge au-dessus de la tete
                // pour les poses les plus hautes) : on remonte depuis son bas plutot que depuis
                // son sommet, qui varie moins d'un personnage a l'autre.
                enScene.getMinY() + (enScene.getHeight() - HAUTEUR_SPRITE_COMBAT));
        carte.plaquetteNom.setLayoutX(centreHaut.getX() - LARGEUR_PLAQUETTE / 2);
        carte.plaquetteNom.setLayoutY(centreHaut.getY() - 22);
    }

    /** Ombre au sol elliptique suivant les pieds du sprite : ancre visuellement le personnage sur
     *  le sol de l'arene (voir style.css .arene-fond) plutot que de le laisser flotter. Ajoutee a
     *  overlayCombat AVANT le sprite (voir creerInfobulleSprite), donc toujours dessinee dessous. */
    private void creerOmbreSol(CarteCombat carte) {
        Ellipse ombre = new Ellipse(LARGEUR_SPRITE_HOLDER * 0.22, LARGEUR_SPRITE_HOLDER * 0.07);
        ombre.setFill(Color.web("#000000", 0.4));
        ombre.setMouseTransparent(true);

        carte.ombreSol = ombre;
        if (!overlayCombat.getChildren().contains(ombre)) {
            overlayCombat.getChildren().add(ombre);
        }

        carte.spriteHolder.layoutXProperty().addListener((o, a, b) -> positionnerOmbreSol(carte));
        carte.spriteHolder.layoutYProperty().addListener((o, a, b) -> positionnerOmbreSol(carte));
        carte.spriteHolder.translateXProperty().addListener((o, a, b) -> positionnerOmbreSol(carte));
        carte.spriteHolder.translateYProperty().addListener((o, a, b) -> positionnerOmbreSol(carte));
    }

    /** Repositionne l'ombre au sol juste sous les pieds ACTUELS du sprite. Appelee en continu
     *  (voir creerOmbreSol). */
    private void positionnerOmbreSol(CarteCombat carte) {
        if (carte.ombreSol == null) return;
        Bounds enScene = carte.spriteHolder.localToScene(carte.spriteHolder.getBoundsInLocal());
        Point2D bas = overlayCombat.sceneToLocal(enScene.getMinX() + enScene.getWidth() / 2, enScene.getMaxY());
        carte.ombreSol.setCenterX(bas.getX());
        carte.ombreSol.setCenterY(bas.getY() - 6);
    }

    /** Ajoute les sprites (et leur infobulle) au champ de bataille et les positionne au-dessus de
     *  leur carte (voir positionAncrage), une fois le premier passage de layout de la scene
     *  effectue. */
    private void placerSpritesInitiaux() {
        if (cartes == null) return;
        for (CarteCombat carte : cartes) {
            if (carte.spriteHolder == null) continue;
            if (!overlayCombat.getChildren().contains(carte.spriteHolder)) {
                overlayCombat.getChildren().add(carte.spriteHolder);
            }
            if (carte.infobulle != null && !overlayCombat.getChildren().contains(carte.infobulle)) {
                overlayCombat.getChildren().add(carte.infobulle);
            }
            positionnerHolderSurCarte(carte);
            positionnerEffetsPersistants(carte);
            positionnerPlaquetteNom(carte);
            positionnerOmbreSol(carte);
            demarrerIdle(carte);
        }
    }

    /** Point d'ancrage (bas-centre de la carte, la "ligne de sol" de l'arene) d'un personnage, en
     *  coordonnees locales de overlayCombat. Recalcule a chaque appel : la carte elle-meme ne
     *  bouge jamais pendant un combat, donc ce point reste stable, mais le recalcul evite toute
     *  hypothese fragile. Le bas plutot que le haut : le bouclier (barre + texte) n'est affiche
     *  que si le personnage en a un, ce qui changerait la hauteur du haut de la carte en cours de
     *  combat — son bas, lui, ne bouge jamais, donc les pieds du sprite restent stables. */
    private Point2D positionAncrage(CarteCombat carte) {
        Bounds local = carte.conteneur.getBoundsInLocal();
        Bounds enScene = carte.conteneur.localToScene(local);
        return overlayCombat.sceneToLocal(enScene.getMinX() + enScene.getWidth() / 2, enScene.getMaxY());
    }

    /** Place le support du sprite (taille fixe) pour que ses pieds reposent juste sur la ligne de
     *  sol de la carte du personnage — sa position "au repos" entre deux actions. */
    private void positionnerHolderSurCarte(CarteCombat carte) {
        Point2D pt = positionAncrage(carte);
        carte.spriteHolder.setLayoutX(pt.getX() - LARGEUR_SPRITE_HOLDER / 2);
        // pt est la ligne de sol (bas de la carte) : le bas du support (ou reposent les pieds,
        // voir StackPane.setAlignment BOTTOM_CENTER dans creerCarte) doit coincider avec elle.
        carte.spriteHolder.setLayoutY(pt.getY() - HAUTEUR_SPRITE_HOLDER);
    }

    /** Boucle d'idle (pose de garde) du sprite d'un personnage. */
    private void demarrerIdle(CarteCombat carte) {
        if (carte.sprites == null || carte.sprites.idle() == null) return;
        jouerFrames(carte, carte.sprites.idle(), 220, true);
    }

    /** Joue une animation transitoire (attaque/degats) puis revient a l'idle a la fin. */
    private void jouerAnimationTemporaire(CarteCombat carte, Image[] frames, int msParFrame) {
        if (carte.spriteView == null || frames == null || frames.length == 0) return;
        jouerFrames(carte, frames, msParFrame, false);
    }

    private void jouerFrames(CarteCombat carte, Image[] frames, int msParFrame, boolean boucle) {
        if (carte.animSprite != null) carte.animSprite.stop();
        Timeline t = new Timeline();
        for (int i = 0; i < frames.length; i++) {
            final Image img = frames[i];
            t.getKeyFrames().add(new KeyFrame(Duration.millis(i * (double) msParFrame), e -> carte.spriteView.setImage(img)));
        }
        t.getKeyFrames().add(new KeyFrame(Duration.millis(frames.length * (double) msParFrame)));
        if (boucle) {
            t.setCycleCount(Timeline.INDEFINITE);
        } else {
            t.setOnFinished(e -> demarrerIdle(carte));
        }
        t.play();
        carte.animSprite = t;
    }

    /**
     * Joue l'animation d'attaque du personnage qui agit ce pas-ci (voir avancerEtape) :
     *  - attaque de base au corps a corps (baseADistance=false) : l'attaquant s'approche de la
     *    cible, frappe, puis revient a sa place ; a distance (baseADistance=true, ex. l'arc de
     *    Gray), il reste sur place et tire, le projectile etant deja dessine dans l'animation ;
     *  - speciale/ultime AVEC entite (ex. Lucy, Angel, Gray) : l'attaquant invoque sur place
     *    (windup) puis l'esprit invoque agit au centre du champ, avec un effet d'impact sur
     *    chaque cible reellement touchee ;
     *  - speciale/ultime SANS entite (ex. la speciale de Natsu) : personne n'est invoque, donc
     *    l'attaquant fonce lui-meme sur la cible comme une attaque de base, et l'effet d'impact
     *    (s'il existe) tombe sur chaque cible reellement touchee au moment du contact.
     * Ne fait rien si ce personnage n'a pas d'assets sprite.
     */
    private void jouerAnimationAttaque(String nomActeur, String actionNom, boolean estUltime, List<CarteCombat> cibles) {
        CarteCombat attaquant = trouverCarte(nomActeur);
        if (attaquant == null || attaquant.sprites == null) return;
        boolean miroir = !attaquant.coteJoueur;
        CarteCombat premiereCible = cibles.isEmpty() ? null : cibles.get(0);

        if (actionNom == null) {
            if (attaquant.sprites.baseADistance()) {
                jouerAnimationTemporaire(attaquant, attaquant.sprites.attaqueBase(), attaquant.sprites.msBase());
            } else {
                jouerAttaqueAvecApproche(attaquant, premiereCible, attaquant.sprites.attaqueBase(), attaquant.sprites.msBase());
            }
            return;
        }

        Image[] windup = estUltime ? attaquant.sprites.ultimeWindup() : attaquant.sprites.specialWindup();
        Image[] entite = estUltime ? attaquant.sprites.ultimeEntite() : attaquant.sprites.specialEntite();
        Image[] impact = estUltime ? attaquant.sprites.ultimeImpact() : attaquant.sprites.specialImpact();
        int msWindup = estUltime ? attaquant.sprites.msUltime() : attaquant.sprites.msSpecial();

        if (entite == null) {
            // Pas d'esprit invoque : l'attaquant fonce lui-meme sur la cible, comme une attaque
            // de base. L'effet d'impact (optionnel) tombe sur CHAQUE cible touchee au moment du
            // contact (~40% du trajet, quand l'attaquant arrive sur la cible — voir jouerAttaqueAvecApproche).
            jouerAttaqueAvecApproche(attaquant, premiereCible, windup, msWindup);
            if (impact != null && impact.length > 0 && !cibles.isEmpty()) {
                double dureeAvantContact = windup != null ? windup.length * (double) msWindup * 0.4 : 0;
                PauseTransition auContact = new PauseTransition(Duration.millis(dureeAvantContact));
                auContact.setOnFinished(e -> {
                    for (CarteCombat cible : cibles) jouerEffetSurCible(cible, impact, msWindup, miroir);
                });
                auContact.play();
            }
            return;
        }

        jouerAnimationTemporaire(attaquant, windup, msWindup);
        double dureeWindup = windup != null ? windup.length * (double) msWindup : 0;
        PauseTransition apresWindup = new PauseTransition(Duration.millis(dureeWindup));
        apresWindup.setOnFinished(e -> jouerInvocationCentrale(entite, msWindup, cibles, impact, msWindup, miroir));
        apresWindup.play();
    }

    /** Attaque de base : l'attaquant se deplace vers ~70% du chemin jusqu'a la cible, joue les
     *  frames d'attaque, puis revient a sa position de depart. Sans cible identifiee (ex. esquive
     *  totale sans aucun degat visible cet evenement), l'attaque est jouee sur place. */
    private void jouerAttaqueAvecApproche(CarteCombat attaquant, CarteCombat cible, Image[] frames, int msParFrame) {
        if (frames == null || frames.length == 0) return;
        jouerAnimationTemporaire(attaquant, frames, msParFrame);
        if (cible == null) return;

        double dureeTotale = frames.length * (double) msParFrame;
        Point2D origine = positionAncrage(attaquant);
        Point2D destination = positionAncrage(cible);
        double dx = (destination.getX() - origine.getX()) * 0.7;
        double dy = (destination.getY() - origine.getY()) * 0.7;

        TranslateTransition aller = new TranslateTransition(Duration.millis(dureeTotale * 0.4), attaquant.spriteHolder);
        aller.setToX(dx);
        aller.setToY(dy);
        aller.setInterpolator(Interpolator.EASE_OUT);

        PauseTransition maintien = new PauseTransition(Duration.millis(dureeTotale * 0.25));

        TranslateTransition retour = new TranslateTransition(Duration.millis(dureeTotale * 0.35), attaquant.spriteHolder);
        retour.setToX(0);
        retour.setToY(0);
        retour.setInterpolator(Interpolator.EASE_IN);

        new SequentialTransition(aller, maintien, retour).play();
    }

    /** Joue l'esprit invoque (Taurus, Aquarius...) au centre du champ de bataille, puis un effet
     *  d'impact plus petit sur chaque cible reellement touchee (voir avancerEtape). L'effet
     *  d'impact est optionnel : certaines invocations (ex. la vague d'Aquarius) se suffisent a
     *  elles-memes, les degats/barres de PV deja affiches sur chaque carte portent le reste. */
    private void jouerInvocationCentrale(Image[] framesEntite, int msEntite,
                                          List<CarteCombat> cibles, Image[] framesImpact, int msImpact, boolean miroir) {
        if (framesEntite == null || framesEntite.length == 0) return;

        ImageView vueEntite = new ImageView(framesEntite[0]);
        vueEntite.setPreserveRatio(true);
        vueEntite.setFitHeight(130);
        vueEntite.setSmooth(false);
        if (miroir) vueEntite.setScaleX(-1);

        StackPane support = new StackPane(vueEntite);
        support.setMouseTransparent(true);
        support.setAlignment(Pos.CENTER);
        support.prefWidthProperty().bind(overlayCombat.widthProperty());
        support.prefHeightProperty().bind(overlayCombat.heightProperty());
        overlayCombat.getChildren().add(support);

        Timeline t = new Timeline();
        for (int i = 0; i < framesEntite.length; i++) {
            final Image img = framesEntite[i];
            t.getKeyFrames().add(new KeyFrame(Duration.millis(i * (double) msEntite), e -> vueEntite.setImage(img)));
        }
        t.getKeyFrames().add(new KeyFrame(Duration.millis(framesEntite.length * (double) msEntite)));
        t.setOnFinished(e -> overlayCombat.getChildren().remove(support));
        t.play();

        if (framesImpact != null && framesImpact.length > 0 && cibles != null) {
            for (CarteCombat cible : cibles) {
                jouerEffetSurCible(cible, framesImpact, msImpact, miroir);
            }
        }
    }

    /** Petit effet transitoire (ex. sol qui se brise) affiche directement sur une cible touchee,
     *  independant du sprite du personnage (retire de la scene une fois joue). {@code miroir}
     *  suit le camp de l'ATTAQUANT (l'effet reproduit le sens de son animation), pas celui de la cible. */
    private void jouerEffetSurCible(CarteCombat cible, Image[] frames, int msParFrame, boolean miroir) {
        ImageView vue = new ImageView(frames[0]);
        vue.setPreserveRatio(true);
        vue.setFitHeight(50);
        if (miroir) vue.setScaleX(-1);
        vue.setSmooth(false);

        StackPane support = new StackPane(vue);
        support.setMouseTransparent(true);
        support.setPrefSize(90, 60);
        Point2D pt = positionAncrage(cible);
        support.setLayoutX(pt.getX() - 45);
        // pt est la ligne de sol (voir positionAncrage) : on remonte a peu pres a hauteur de
        // buste pour que l'effet semble vraiment frapper le personnage, pas ses pieds.
        support.setLayoutY(pt.getY() - HAUTEUR_SPRITE_COMBAT * 0.65 - 15);
        overlayCombat.getChildren().add(support);

        Timeline t = new Timeline();
        for (int i = 0; i < frames.length; i++) {
            final Image img = frames[i];
            t.getKeyFrames().add(new KeyFrame(Duration.millis(i * (double) msParFrame), e -> vue.setImage(img)));
        }
        t.getKeyFrames().add(new KeyFrame(Duration.millis(frames.length * (double) msParFrame)));
        t.setOnFinished(e -> overlayCombat.getChildren().remove(support));
        t.play();
    }

    private CarteCombat trouverCarte(String nom) {
        if (nom == null || cartes == null) return null;
        for (CarteCombat carte : cartes) {
            if (carte.nom.equals(nom)) return carte;
        }
        return null;
    }

    /** Stoppe toutes les animations de sprite en cours (a appeler avant de quitter l'ecran, sinon
     *  les Timeline.INDEFINITE des boucles idle continuent de tourner en arriere-plan). */
    private void arreterAnimationsSprites() {
        if (cartes == null) return;
        for (CarteCombat carte : cartes) {
            if (carte.animSprite != null) carte.animSprite.stop();
        }
    }

    /** Petite icone carree colorée par effet, avec fleche haut (buff) / bas (debuff) et tooltip explicatif.
     *  @param pointsBouclier PV de bouclier restants, utilise uniquement pour enrichir l'infobulle de "Bouclier". */
    private Node creerIconeEffet(String nomEffet, double pointsBouclier) {
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

        String detail = nomEffet.equals("Bouclier")
                ? "\nPV restants : " + (int) Math.ceil(pointsBouclier)
                : "";
        Tooltip infobulle = new Tooltip(nomEffet + "\n" + info.description() + detail);
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

    private StackPane creerBarre(double hauteur) {
        Rectangle fond = new Rectangle(LARGEUR_BARRE, hauteur, Color.web("#12121c"));
        fond.setArcWidth(8);
        fond.setArcHeight(8);
        Rectangle remplissage = new Rectangle(LARGEUR_BARRE, hauteur, Color.web("#56c98a"));
        remplissage.setArcWidth(8);
        remplissage.setArcHeight(8);
        StackPane pane = new StackPane(fond, remplissage);
        StackPane.setAlignment(fond, Pos.CENTER_LEFT);
        StackPane.setAlignment(remplissage, Pos.CENTER_LEFT);
        pane.setPrefSize(LARGEUR_BARRE, hauteur);
        pane.setMaxWidth(LARGEUR_BARRE);
        return pane;
    }

    /**
     * @param deltaVie variation de PV depuis l'etat precedent (negatif = degats, positif =
     *                 soin, 0 = pas de variation a mettre en evidence, ex. etat initial).
     */
    private void appliquerEtat(CarteCombat carte, PersonnageSnapshot snap, double deltaVie) {
        appliquerEtat(carte, snap, deltaVie, false, 0);
    }

    private void appliquerEtat(CarteCombat carte, PersonnageSnapshot snap, double deltaVie, boolean critique) {
        appliquerEtat(carte, snap, deltaVie, critique, 0);
    }

    /**
     * @param deltaVie      variation de PV depuis l'etat precedent (negatif = degats, positif =
     *                      soin, 0 = pas de variation a mettre en evidence, ex. etat initial).
     * @param deltaBouclier variation des PV du bouclier depuis l'etat precedent, affichee separement
     *                      car des degats totalement absorbes par le bouclier ne bougent pas les PV.
     */
    private void appliquerEtat(CarteCombat carte, PersonnageSnapshot snap, double deltaVie, boolean critique, double deltaBouclier) {
        double ratioPV = snap.vieMax > 0 ? Math.max(0, Math.min(1, snap.vie / snap.vieMax)) : 0;
        animerBarre(carte.barrePV, LARGEUR_BARRE * ratioPV);
        carte.barrePV.setFill(couleurPV(ratioPV));
        carte.pvTexte.setText("PV : " + (int) Math.ceil(snap.vie) + " / " + (int) Math.ceil(snap.vieMax));

        if (carte.plaquetteBarrePV != null) {
            animerBarre(carte.plaquetteBarrePV, 60 * ratioPV);
            carte.plaquetteBarrePV.setFill(couleurPV(ratioPV));
        }

        boolean aBouclier = snap.pointsBouclier > 0;
        carte.barreBouclierPane.setVisible(aBouclier);
        carte.barreBouclierPane.setManaged(aBouclier);
        carte.bouclierTexte.setVisible(aBouclier);
        carte.bouclierTexte.setManaged(aBouclier);
        if (aBouclier) {
            double ratioBouclier = snap.vieMax > 0 ? Math.min(1, snap.pointsBouclier / snap.vieMax) : 0;
            animerBarre(carte.barreBouclier, LARGEUR_BARRE * ratioBouclier);
            carte.bouclierTexte.setText("🛡 Bouclier : " + (int) Math.ceil(snap.pointsBouclier) + " PV");
        }

        double ratioRage = Math.max(0, Math.min(1, snap.rage / 100.0));
        animerBarre(carte.barreRage, LARGEUR_BARRE * ratioRage);
        carte.barreRage.setFill(Color.web("#4ea8f2"));
        carte.rageTexte.setText("Rage : " + (int) snap.rage + " / 100");

        if (carte.infobulle != null) {
            carte.infobulleTexte.setText(snap.nom + " · Niv. " + snap.niveau);
            carte.infobullePV.setWidth(LARGEUR_INFOBULLE * ratioPV);
            carte.infobulleRage.setWidth(LARGEUR_INFOBULLE * ratioRage);
            carte.infobullePvTexte.setText((int) Math.ceil(snap.vie) + " / " + (int) Math.ceil(snap.vieMax));
            carte.infobulleRageTexte.setText((int) snap.rage + " / 100");
        }

        carte.effetsBox.getChildren().clear();
        for (String nomEffet : snap.effets) {
            carte.effetsBox.getChildren().add(creerIconeEffet(nomEffet, snap.pointsBouclier));
        }
        if (carte.effetsPersistants != null) {
            carte.effetsPersistants.getChildren().clear();
            for (String nomEffet : snap.effets) {
                carte.effetsPersistants.getChildren().add(creerIconeEffet(nomEffet, snap.pointsBouclier));
            }
        }

        carte.koTampon.setVisible(!snap.vivant);
        carte.koTampon.setManaged(!snap.vivant);

        // Personnage avec sprite : pas de cadre de flash (coup/soin/K.O.) autour du support
        // invisible, ca dessinait un carre colore disgracieux bien plus grand que le personnage
        // lui-meme. L'animation "hurt" + le nombre flottant suffisent a montrer le coup encaisse.
        Region flash = elementFlash(carte);
        StackPane cible = conteneurVisuel(carte);
        boolean aSprite = carte.spriteHolder != null;
        flash.getStyleClass().removeAll("carte-combat-hit", "carte-combat-heal", "carte-combat-ko");
        if (!snap.vivant) {
            if (!aSprite) flash.getStyleClass().add("carte-combat-ko");
            // KO : le personnage tombe au combat et disparait (plutot que de rejouer les frames
            // de chute), une seule fois (sinon chaque evenement suivant relancerait le fondu).
            if (carte.spriteView != null && carte.spriteView.getOpacity() > 0.01) {
                if (carte.animSprite != null) carte.animSprite.stop();
                FadeTransition disparition = new FadeTransition(Duration.millis(600), carte.spriteView);
                disparition.setToValue(0);
                disparition.play();
            }
        } else if (deltaVie < 0) {
            if (!aSprite) {
                flash.getStyleClass().add("carte-combat-hit");
                retirerApres(flash, "carte-combat-hit");
            }
            afficherNombreFlottant(cible, "-" + (int) Math.round(-deltaVie), "#ff6b6b", critique);
            if (carte.sprites != null && carte.sprites.hurt() != null) {
                jouerAnimationTemporaire(carte, carte.sprites.hurt(), 150);
            }
        } else if (deltaVie > 0) {
            if (!aSprite) {
                flash.getStyleClass().add("carte-combat-heal");
                retirerApres(flash, "carte-combat-heal");
            }
            afficherNombreFlottant(cible, "+" + (int) Math.round(deltaVie), "#6bffa0", false);
        }

        // Distinct du deltaVie : des degats entierement absorbes par le bouclier ne bougent pas
        // les PV, donc sans ceci l'attaque n'affichait aucun nombre flottant (rien ne semblait se passer).
        if (deltaBouclier < -0.01) {
            afficherNombreFlottant(cible, "-" + (int) Math.round(-deltaBouclier) + " 🛡", "#5bc8e8", false, 18);
        } else if (deltaBouclier > 0.01) {
            afficherNombreFlottant(cible, "+" + (int) Math.round(deltaBouclier) + " 🛡", "#5bc8e8", false, 18);
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
        afficherNombreFlottant(conteneur, texte, couleurHex, critique, 0);
    }

    private void afficherNombreFlottant(StackPane conteneur, String texte, String couleurHex, boolean critique, double decalageX) {
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
        pile.setTranslateX(decalageX);
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

    private void retirerApres(Region noeud, String classe) {
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
        return lancement.gui.GuiVisuels.couleurRarete(rarete);
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

        // Cibles reellement touchees ce pas-ci (PV en baisse, encore vivantes avant le coup,
        // hors l'attaquant lui-meme) : deduit de l'etat plutot que d'un champ "cible" dedie, qui
        // n'existe pas sur CombatEvent — sert a la fois au deplacement de l'attaquant et aux
        // effets d'impact des invocations (voir jouerAnimationAttaque).
        List<CarteCombat> ciblesTouchees = new ArrayList<>();
        for (int i = 0; i < evt.etat.size(); i++) {
            PersonnageSnapshot avant = etatPrecedent.get(i);
            PersonnageSnapshot apres = evt.etat.get(i);
            if (avant.vivant && apres.vie < avant.vie && !apres.nom.equals(evt.titre)) {
                ciblesTouchees.add(cartes[i]);
            }
        }
        jouerAnimationAttaque(evt.titre, evt.actionNom, evt.estUltime, ciblesTouchees);
        mettreAJourOrdreTours();

        boolean critique = evt.lignes.contains("Coup critique !");
        for (int i = 0; i < evt.etat.size(); i++) {
            PersonnageSnapshot avant = etatPrecedent.get(i);
            PersonnageSnapshot apres = evt.etat.get(i);
            appliquerEtat(cartes[i], apres, apres.vie - avant.vie, critique, apres.pointsBouclier - avant.pointsBouclier);

            if (evt.lignes.contains(apres.nom + " esquive !")) {
                afficherNombreFlottant(conteneurVisuel(cartes[i]), "Esquive !", "#f2c14e", false);
            } else if (evt.lignes.stream().anyMatch(l -> l.contains(apres.nom + " bloque ! Degats reduits a "))) {
                afficherNombreFlottant(conteneurVisuel(cartes[i]), "Bloque !", "#5bc8e8", false, -30);
            } else if (evt.lignes.stream().anyMatch(l -> l.contains(apres.nom + " resiste aux effets de controle"))) {
                afficherNombreFlottant(conteneurVisuel(cartes[i]), "Immunise !", "#cfd8e3", false);
            }
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
            elementFlash(carte).getStyleClass().remove("carte-combat-active");
        }
        if (nomActeur == null) return;
        for (CarteCombat carte : cartes) {
            // Pour un personnage avec sprite, l'animation d'attaque/deplacement montre deja
            // clairement que c'est son tour — l'aura "carte-combat-active" (pensee pour une carte
            // texte statique) est superflue sur un sprite et jugee genante visuellement.
            if (nomActeur.equals(carte.nom) && carte.spriteHolder == null) {
                elementFlash(carte).getStyleClass().add("carte-combat-active");
            }
        }
    }

    /** Banniere temporaire annoncant le declenchement d'une speciale ou d'un ultime (plus
     *  marquee/plus longue pour un ultime, plus discrete pour une speciale). */
    /** Joue le son associe a cette speciale/ultime, si un a ete fourni pour ce personnage (et si
     *  les effets ne sont pas coupes dans les Options), en baissant brievement la musique de
     *  fond pour que les deux ne se superposent pas trop fort. */
    private void jouerSonAction(String nomPerso, String nomAction, boolean estUltime) {
        // Mistgun est Jellal infiltre sous un autre nom (voir Chapitre5.lancerStage, stage 9) :
        // meme voix pour ses competences.
        String cleNom = nomPerso.equals("Mistgun") ? "Jellal" : nomPerso;
        AudioClip clip = SONS_ACTIONS.get(cleNom + "|" + nomAction);
        if (clip == null) return;
        if (!ParametresAudio.isMuet()) {
            clip.play(ParametresAudio.getVolumeEffets());
        }
        GestionnaireMusique.attenuerPendant(Duration.millis(estUltime ? 2200 : 1400));
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
        PauseTransition maintien = new PauseTransition(Duration.millis(estUltime ? 2200 : 1400));
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
        arreterAnimationsSprites();
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
