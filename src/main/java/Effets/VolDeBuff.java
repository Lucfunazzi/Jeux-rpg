package Effets;

import Personnage.PersonnageBase;
import Combat.Combat;
import java.util.List;

/**
 * Utilitaire de vol de buff : retire le premier effet positif actif sur la cible
 * et l'applique tel quel au voleur (meme duree restante).
 *
 * Usage dans une attaque :
 *   VolDeBuff.voler(soi, ennemi, log);
 */
public class VolDeBuff {

    /**
     * Liste des effets positifs consideres comme volables.
     * A completer si de nouveaux buffs sont ajoutes au projet.
     */
    private static final List<Class<? extends Effet>> EFFETS_VOLABLES = List.<Class<? extends Effet>>of(
        BuffAttaque.class,
        BuffDefense.class,
        BuffVitesse.class,
        BuffTauxCritique.class,
        BuffDegatCritique.class,
        BuffPrecision.class,
        BuffTauxEsquive.class,
        BuffBlocage.class,
        Bouclier.class,
        Regeneration.class,
        Absorption.class,
        Invincibilite.class
    );

    private VolDeBuff() {}

    /**
     * Vole le premier effet positif trouve sur {@code cible} et l'applique a {@code voleur}.
     */
    public static void voler(PersonnageBase voleur, PersonnageBase cible, List<String> log) {
        Effet aVoler = null;
        for (Effet e : cible.getEffetsActifs()) {
            if (estVolable(e)) { aVoler = e; break; }
        }

        if (aVoler == null) {
            log.add(cible.getNom() + " n'a aucun effet a voler.");
            return;
        }

        cible.getEffetsActifs().remove(aVoler);
        log.add("🌀 " + voleur.getNom() + " vole [" + aVoler.getNom() + "] a " + cible.getNom() + " !");
        Combat.appliquerEffet(voleur, aVoler, log);
    }

    private static boolean estVolable(Effet e) {
        for (Class<? extends Effet> classe : EFFETS_VOLABLES) {
            if (classe.isInstance(e)) return true;
        }
        return false;
    }
}
