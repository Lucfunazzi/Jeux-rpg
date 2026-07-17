package Personnage.json;

/**
 * Représente un effet à appliquer, désérialisé depuis JSON.
 *
 * Types supportés (correspond aux classes dans Effets/) :
 *   Buffs offensifs  : BuffAttaque, BuffDefense, BuffVitesse, BuffBlocage,
 *                      BuffTauxCritique, BuffDegatCritique, BuffTauxEsquive, BuffPrecision
 *   Debuffs          : ReductionAttaque, ReductionDefense, ReductionVitesse
 *   DoT              : Brulure, Saignement, Poison
 *   CC               : Etourdissement, Sommeil, Gel, Silence, Confusion,
 *                      Petrification, Paralysie, Ralentissement, Aveuglement, Trempe
 *   Spéciaux         : Marquage, Fragilite, Malediction, Invincibilite,
 *                      Resurrection, Absorption, ContreAttaque, Bouclier, Regeneration
 *
 * Champs utilisés selon le type :
 *   - tours   : durée en tours (quasi tous)
 *   - valeur  : pourcentage principal (dégâts DoT, buff%, debuff%, etc.)
 *   - valeur2 : second paramètre si besoin (ex: Paralysie.chanceLiberation,
 *               ContreAttaque.multiplicateurDegats, Aveuglement.pourcentage)
 *   - chance  : probabilité 0.0-1.0 que l'effet soit appliqué (optionnel, défaut 1.0)
 *
 * Exemples JSON :
 *   { "type": "Brulure",       "tours": 2, "valeur": 0.08 }
 *   { "type": "Sommeil",       "tours": 1, "chance": 0.15 }
 *   { "type": "Paralysie",     "tours": 2, "valeur2": 0.33 }
 *   { "type": "Marquage",      "tours": 2, "valeur": 0.15 }
 *   { "type": "Resurrection",  "valeur": 0.30 }
 *   { "type": "Bouclier",      "valeur": 0.30 }   ← valeur = % PV max
 *   { "type": "BuffAttaque",   "tours": 2, "valeur": 0.20 }
 *   { "type": "ContreAttaque", "tours": 2, "valeur2": 0.50 }
 */
public class EffetData {
    public String type;
    public int    tours  = 0;
    public double valeur = 0.0;
    public double valeur2 = 0.0;
    public double chance = 1.0;   // 1.0 = garanti
}