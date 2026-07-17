package Personnage.json;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Exécute une AttaqueData en combat.
 * C'est le moteur central du système data-driven :
 * il lit les données JSON et les traduit en appels Combat.
 */
public class MoteurAttaque {

    private static final Random RNG = new Random();

    // ─────────────────────────────────────────────────────────────────────────
    // POINTS D'ENTRÉE PUBLICS
    // ─────────────────────────────────────────────────────────────────────────

    public static void executerBase(AttaqueData data, PersonnageBase soi,
                                    PersonnageBase cible,
                                    List<PersonnageBase> allies,
                                    List<PersonnageBase> ennemis,
                                    List<String> log) {
        log.add(soi.getNom() + " utilise " + data.nom + " !");
        executerAttaque(data, soi, cible, allies, ennemis, log, false);
    }

    public static void executerSpeciale(AttaqueData data, PersonnageBase soi,
                                        PersonnageBase cible,
                                        List<PersonnageBase> allies,
                                        List<PersonnageBase> ennemis,
                                        List<String> log) {
        log.add(soi.getNom() + " utilise " + data.nom + " !");
        executerAttaque(data, soi, cible, allies, ennemis, log, false);
    }

    public static void executerUltime(AttaqueData data, PersonnageBase soi,
                                      List<PersonnageBase> allies,
                                      List<PersonnageBase> ennemis,
                                      List<String> log) {
        log.add(soi.getNom() + " utilise " + data.nom + " !");
        executerAttaque(data, soi, null, allies, ennemis, log, true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MOTEUR CENTRAL
    // ─────────────────────────────────────────────────────────────────────────

    private static void executerAttaque(AttaqueData data, PersonnageBase soi,
                                        PersonnageBase ciblePassee,
                                        List<PersonnageBase> allies,
                                        List<PersonnageBase> ennemis,
                                        List<String> log,
                                        boolean estUltime) {
        // Multiplicateur de rage (ultime uniquement)
        double multRage = 1.0;
        if (estUltime && data.bonusRage && soi.getRage() > 100) {
            multRage += (soi.getRage() - 100) / 100.0;
        }

        // Résoudre la/les cibles ennemies
        List<PersonnageBase> ciblesEnnemies = resoudreCibles(data, soi, ciblePassee, allies, ennemis);

        // Appliquer dégâts + effets sur chaque cible ennemie
        for (PersonnageBase cible : ciblesEnnemies) {
            if (!cible.estVivant()) continue;

            // Dégâts
            if (data.multiplicateur > 0) {
                double degats = soi.getAttaque() * data.multiplicateur * multRage;
                Combat.appliquerDegatsAvecLog(soi, cible, degats, log);
            }

            // Effets sur la cible
            if (data.effets != null) {
                for (EffetData ed : data.effets) {
                    appliquerEffetSurCible(ed, soi, cible, log);
                }
            }
        }

        // Effets sur soi / alliés
        if (data.effetsSoi != null) {
            for (EffetData ed : data.effetsSoi) {
                appliquerEffetSurSoi(ed, soi, allies, log);
            }
        }

        // Soin
        if (data.soinPourcentageAtk > 0) {
            double montantSoin = soi.getAttaque() * data.soinPourcentageAtk;
            if ("AOE_ALLIES".equals(data.ciblageSOin)) {
                // Soin sur toute l'équipe
                for (PersonnageBase allie : allies) {
                    if (allie.estVivant()) {
                        allie.recevoirSoin(montantSoin, log);
                        if (data.purifierApresSOin > 0)
                            Purification.purifier(allie, data.purifierApresSOin, log);
                    }
                }
            } else {
                PersonnageBase cibleSoin = resoudreCibleSoin(data, soi, allies);
                if (cibleSoin != null) {
                    cibleSoin.recevoirSoin(montantSoin, log);
                    if (data.purifierApresSOin > 0)
                        Purification.purifier(cibleSoin, data.purifierApresSOin, log);
                }
            }
        }

        // Purification équipe
        if (data.purifierEquipe) {
            Purification.purifierEquipe(allies, 1, log);
        }

        // Coût en PV sur soi
        if (data.coutPvSoi > 0) {
            double cout = soi.getVieMax() * data.coutPvSoi;
            soi.retirerVie(cout);
            log.add(soi.getNom() + " paie le prix et perd "
                    + String.format("%.1f", cout) + " PV !");
        }

        // Gain de rage
        if (data.gainRageSoi > 0) {
            soi.ajouterRage(data.gainRageSoi);
            log.add(soi.getNom() + " recupere " + (int) data.gainRageSoi + " points de rage !");
        }

        // Synergies
        if (data.synergies != null) {
            for (SynergieData syn : data.synergies) {
                executerSynergie(syn, soi, allies, log);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RÉSOLUTION DES CIBLES
    // ─────────────────────────────────────────────────────────────────────────

    private static List<PersonnageBase> resoudreCibles(AttaqueData data, PersonnageBase soi,
                                                        PersonnageBase ciblePassee,
                                                        List<PersonnageBase> allies,
                                                        List<PersonnageBase> ennemis) {
        List<PersonnageBase> result = new ArrayList<>();

        switch (data.ciblage) {
            case "CIBLE_UNIQUE" -> {
                if (ciblePassee != null && ciblePassee.estVivant())
                    result.add(ciblePassee);
            }
            case "AOE_ENNEMIS" -> {
                for (PersonnageBase e : ennemis)
                    if (e.estVivant()) result.add(e);
            }
            case "AOE_ALLIES" -> {
                // Dégâts sur alliés (confusion ou auto-flagellation)
                for (PersonnageBase a : allies)
                    if (a.estVivant()) result.add(a);
            }
            case "PLUS_HAUTE_ATK" -> {
                PersonnageBase c = ennemis.stream()
                        .filter(PersonnageBase::estVivant)
                        .max(java.util.Comparator.comparingDouble(PersonnageBase::getAttaque))
                        .orElse(null);
                if (c != null) result.add(c);
            }
            case "PLUS_HAUTE_DEF" -> {
                PersonnageBase c = ennemis.stream()
                        .filter(PersonnageBase::estVivant)
                        .max(java.util.Comparator.comparingDouble(PersonnageBase::getDefense))
                        .orElse(null);
                if (c != null) result.add(c);
            }
            case "PLUS_HAUTS_PV" -> {
                PersonnageBase c = ennemis.stream()
                        .filter(PersonnageBase::estVivant)
                        .max(java.util.Comparator.comparingDouble(PersonnageBase::getVie))
                        .orElse(null);
                if (c != null) result.add(c);
            }
            case "PLUS_BAS_PV_ENNEMI" -> {
                PersonnageBase c = ennemis.stream()
                        .filter(PersonnageBase::estVivant)
                        .min(java.util.Comparator.comparingDouble(PersonnageBase::getVie))
                        .orElse(null);
                if (c != null) result.add(c);
            }
            case "ALEATOIRE_ENNEMI" -> {
                List<PersonnageBase> vivants = ennemis.stream()
                        .filter(PersonnageBase::estVivant).toList();
                if (!vivants.isEmpty())
                    result.add(vivants.get(RNG.nextInt(vivants.size())));
            }
            case "ROLE_ENNEMI" -> {
                if (data.rolesCibles != null) {
                    for (PersonnageBase e : ennemis) {
                        if (e.estVivant() && data.rolesCibles.contains(e.getRole()))
                            result.add(e);
                    }
                }
                // Repli : si aucun ennemi du bon rôle, on cible tous les ennemis
                if (result.isEmpty()) {
                    for (PersonnageBase e : ennemis)
                        if (e.estVivant()) result.add(e);
                }
            }
            case "SOI" -> {
                // Pas de cible ennemie (buff pur)
            }
        }

        return result;
    }

    private static PersonnageBase resoudreCibleSoin(AttaqueData data, PersonnageBase soi,
                                                     List<PersonnageBase> allies) {
        return switch (data.ciblageSOin) {
            case "SOI"         -> soi;
            case "AOE_ALLIES"  -> null; // géré séparément dans le soin AoE
            default            -> allies.stream()   // PLUS_BAS_PV_ALLIE
                    .filter(PersonnageBase::estVivant)
                    .min(java.util.Comparator.comparingDouble(PersonnageBase::getVie))
                    .orElse(null);
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CRÉATION D'EFFETS
    // ─────────────────────────────────────────────────────────────────────────

    private static void appliquerEffetSurCible(EffetData ed, PersonnageBase soi,
                                                PersonnageBase cible, List<String> log) {
        if (RNG.nextDouble() >= ed.chance) return; // raté
        Effet effet = creerEffet(ed, soi);
        if (effet != null) Combat.appliquerEffet(soi, cible, effet, log);
    }

    private static void appliquerEffetSurSoi(EffetData ed, PersonnageBase soi,
                                              List<PersonnageBase> allies, List<String> log) {
        if (RNG.nextDouble() >= ed.chance) return;
        Effet effet = creerEffet(ed, soi);
        if (effet != null) Combat.appliquerEffet(soi, effet, log);
    }

    /**
     * Instancie l'objet Effet Java correspondant à un EffetData JSON.
     */
    public static Effet creerEffet(EffetData ed, PersonnageBase source) {
        return switch (ed.type) {
            // DoT
            case "Brulure"           -> new Brulure(ed.tours, ed.valeur);
            case "Saignement"        -> new Saignement(ed.tours, ed.valeur);
            case "Poison"            -> new Poison(ed.tours, ed.valeur);

            // Soins / regen
            case "Regeneration"      -> new Regeneration(ed.valeur, ed.tours);

            // Bouclier / absorption
            case "Bouclier"          -> new Bouclier(source.getVieMax() * ed.valeur);
            case "Absorption"        -> new Absorption(ed.tours, ed.valeur);

            // Buffs offensifs
            case "BuffAttaque"       -> new BuffAttaque(ed.valeur, ed.tours);
            case "BuffDefense"       -> new BuffDefense(ed.valeur, ed.tours);
            case "BuffVitesse"       -> new BuffVitesse(ed.valeur, ed.tours);
            case "BuffBlocage"       -> new BuffBlocage(ed.valeur, ed.tours);
            case "BuffTauxCritique"  -> new BuffTauxCritique(ed.valeur, ed.tours);
            case "BuffDegatCritique" -> new BuffDegatCritique(ed.valeur, ed.tours);
            case "BuffTauxEsquive"   -> new BuffTauxEsquive(ed.valeur, ed.tours);

            // Debuffs
            case "ReductionAttaque"  -> new ReductionAttaque(ed.valeur, ed.tours);
            case "ReductionDefense"  -> new ReductionDefense(ed.valeur, ed.tours);
            case "ReductionVitesse"  -> new ReductionVitesse(ed.valeur, ed.tours);

            // CC
            case "Etourdissement"    -> new Etourdissement(ed.tours);
            case "Sommeil"           -> new Sommeil(ed.tours);
            case "Gel"               -> new Gel(ed.tours);
            case "Silence"           -> new Silence(ed.tours);
            case "Confusion"         -> new Confusion(ed.tours);
            case "Petrification"     -> new Petrification(ed.tours);
            case "Paralysie"         -> new Paralysie(ed.tours, ed.valeur2);
            case "Ralentissement"    -> new Ralentissement(ed.tours, ed.valeur);
            case "Aveuglement"       -> new Aveuglement(ed.valeur, ed.tours);
            case "Trempe"            -> new Trempe(ed.tours);

            // Spéciaux
            case "Marquage"          -> new Marquage(ed.tours, ed.valeur);
            case "Fragilite"         -> new Fragilite(ed.tours, ed.valeur);
            case "Malediction"       -> new Malediction(ed.tours, ed.valeur);
            case "Invincibilite"     -> new Invincibilite(ed.tours);
            case "Resurrection"      -> new Resurrection(ed.valeur);
            case "ContreAttaque"     -> new ContreAttaque(ed.tours, ed.valeur2);
            case "Provocation"       -> new Provocation(ed.tours, source);

            default -> {
                System.err.println("[MoteurAttaque] Effet inconnu : " + ed.type);
                yield null;
            }
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SYNERGIES
    // ─────────────────────────────────────────────────────────────────────────

    private static void executerSynergie(SynergieData syn, PersonnageBase soi,
                                          List<PersonnageBase> allies, List<String> log) {
        switch (syn.action) {
            case "RAGE_EQUIPE" -> {
                for (PersonnageBase a : allies) {
                    if (a.estVivant()) a.ajouterRage(syn.valeur);
                }
                log.add("Toute l'equipe gagne " + (int) syn.valeur + " points de rage !");
            }
            default -> {
                // Synergie conditionnelle : l'allié nommé doit être vivant
                PersonnageBase cibleSyn = trouverAllie(syn.allie, allies);
                if (cibleSyn == null || !cibleSyn.estVivant()) return;

                switch (syn.action) {
                    case "RAGE" -> {
                        cibleSyn.ajouterRage(syn.valeur);
                        log.add(cibleSyn.getNom() + " gagne " + (int) syn.valeur
                                + " points de rage (synergie) !");
                    }
                    case "RAGE_SOI" -> {
                        soi.ajouterRage(syn.valeur);
                        log.add(soi.getNom() + " gagne " + (int) syn.valeur
                                + " points de rage (synergie) !");
                    }
                    case "BUFF_ATK" -> {
                        Combat.appliquerEffet(soi, cibleSyn,
                                new BuffAttaque(syn.valeur, syn.tours), log);
                    }
                    case "BUFF_DEF" -> {
                        Combat.appliquerEffet(soi, cibleSyn,
                                new BuffDefense(syn.valeur, syn.tours), log);
                    }
                    case "BUFF_ATK_SOI" -> {
                        Combat.appliquerEffet(soi, new BuffAttaque(syn.valeur, syn.tours), log);
                    }
                    case "SOIN_SOI" -> {
                        double soinMontant = soi.getAttaque() * syn.valeur;
                        soi.recevoirSoin(soinMontant, log);
                    }
                    case "EFFET" -> {
                        if (syn.effet != null) {
                            Effet e = creerEffet(syn.effet, soi);
                            if (e != null) Combat.appliquerEffet(soi, cibleSyn, e, log);
                        }
                    }
                    default -> System.err.println("[MoteurAttaque] Action synergie inconnue : " + syn.action);
                }
            }
        }
    }

    private static PersonnageBase trouverAllie(String nom, List<PersonnageBase> allies) {
        for (PersonnageBase a : allies)
            if (a.getNom().equals(nom)) return a;
        return null;
    }
}