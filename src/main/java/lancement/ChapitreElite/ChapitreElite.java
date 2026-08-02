package lancement.ChapitreElite;

import lancement.Chapitres.Chapitre;

/** Forme commune a tous les chapitres elite (1-13), pour permettre un cablage generique. */
public interface ChapitreElite extends Chapitre {
    boolean estDebloque();
}
