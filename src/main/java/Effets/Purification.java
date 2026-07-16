package Effets;

import Personnage.PersonnageBase;
import Effets.Brulure;
import Effets.Saignement;
import Effets.Poison;
import Effets.Aveuglement;
import Effets.Petrification;
import Effets.Etourdissement;
import Effets.Sommeil;
import Effets.Malediction;
import Effets.Ralentissement;
import Effets.ReductionDefense;
import Effets.ReductionAttaque;
import Effets.ReductionVitesse;
import Effets.Fragilite;
import Effets.Marquage;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilitaire de purification : retire un nombre défini d'effets négatifs
 * sur un allié ciblé ou sur toute l'équipe alliée.
 *
 * Usage dans une attaque (cible unique) :
 *   Purification.purifier(cible, 2, log);
 *
 * Usage dans une attaque (AoE) :
 *   Purification.purifierEquipe(equipeAlliee, Integer.MAX_VALUE, log);
 *
 * Passer Integer.MAX_VALUE comme nbEffets pour retirer TOUS les effets négatifs.
 */
public class Purification {

    /**
     * Liste de toutes les classes d'effets considérées comme négatives.
     * À compléter si de nouveaux effets négatifs sont ajoutés au projet.
     */
    @SuppressWarnings("unchecked")
    private static final List<Class<? extends Effet>> EFFETS_NEGATIFS = List.of(
        Brulure.class,
        Saignement.class,
        Poison.class,
        Aveuglement.class,
        Petrification.class,
        Etourdissement.class,
        Sommeil.class,
        Malediction.class,
        Ralentissement.class,
        ReductionDefense.class,
        ReductionAttaque.class,
        ReductionVitesse.class,
        Fragilite.class,
        Marquage.class
    );

    // Constructeur privé : classe purement statique
    private Purification() {}

    /**
     * Retire jusqu'à {@code nbEffets} effets négatifs sur un seul personnage.
     *
     * @param cible     Le personnage à purifier.
     * @param nbEffets  Nombre d'effets à retirer. Passer {@code Integer.MAX_VALUE} pour tous les retirer.
     * @param log       Journal de combat.
     */
    public static void purifier(PersonnageBase cible, int nbEffets, List<String> log) {
        List<Effet> aRetirer = trouverEffetsNegatifs(cible, nbEffets);

        if (aRetirer.isEmpty()) {
            log.add("✨ " + cible.getNom() + " n'a aucun effet négatif à purifier.");
            return;
        }

        List<String> nomsRetires = new ArrayList<>();
        for (Effet e : aRetirer) {
            cible.getEffetsActifs().remove(e);
            nomsRetires.add(e.getNom());
        }

        log.add("✨ Purification : " + cible.getNom() + " est libéré(e) de "
                + String.join(", ", nomsRetires) + " !");
    }

    /**
     * Retire jusqu'à {@code nbEffets} effets négatifs sur chaque membre vivant de l'équipe.
     *
     * @param equipe    Liste des alliés (vivants ou non, le filtre est interne).
     * @param nbEffets  Nombre d'effets à retirer par personnage. Passer {@code Integer.MAX_VALUE} pour tous.
     * @param log       Journal de combat.
     */
    public static void purifierEquipe(List<PersonnageBase> equipe, int nbEffets, List<String> log) {
        boolean auMoinsUn = false;

        for (PersonnageBase allie : equipe) {
            if (!allie.estVivant()) continue;

            List<Effet> aRetirer = trouverEffetsNegatifs(allie, nbEffets);
            if (aRetirer.isEmpty()) continue;

            auMoinsUn = true;
            List<String> nomsRetires = new ArrayList<>();
            for (Effet e : aRetirer) {
                allie.getEffetsActifs().remove(e);
                nomsRetires.add(e.getNom());
            }

            log.add("✨ Purification : " + allie.getNom() + " est libéré(e) de "
                    + String.join(", ", nomsRetires) + " !");
        }

        if (!auMoinsUn) {
            log.add("✨ Purification de l'équipe : aucun effet négatif à retirer.");
        }
    }

    // -------------------------------------------------------------------------
    // Méthode interne
    // -------------------------------------------------------------------------

    /**
     * Collecte jusqu'à {@code nbEffets} effets négatifs actifs sur {@code cible}.
     */
    private static List<Effet> trouverEffetsNegatifs(PersonnageBase cible, int nbEffets) {
        List<Effet> resultat = new ArrayList<>();
        for (Effet e : cible.getEffetsActifs()) {
            if (resultat.size() >= nbEffets) break;
            if (estNegatif(e)) {
                resultat.add(e);
            }
        }
        return resultat;
    }

    /**
     * Retourne {@code true} si l'effet appartient à la liste des effets négatifs connus.
     */
    private static boolean estNegatif(Effet e) {
        for (Class<? extends Effet> classe : EFFETS_NEGATIFS) {
            if (classe.isInstance(e)) return true;
        }
        return false;
    }
}
