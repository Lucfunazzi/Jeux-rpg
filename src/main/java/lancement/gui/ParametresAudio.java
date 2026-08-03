package lancement.gui;

import java.util.prefs.Preferences;

/**
 * Reglages audio (volume musique/effets, mute), persistants d'une session a l'autre.
 * Ce sont des preferences d'installation (comme un volume systeme), pas des donnees de
 * partie : elles ne passent donc pas par SauvegardeData/GestionnaireSauvegarde.
 */
public final class ParametresAudio {

    private static final Preferences PREFS = Preferences.userNodeForPackage(ParametresAudio.class);
    private static final String CLE_VOLUME_MUSIQUE = "volumeMusique";
    private static final String CLE_VOLUME_EFFETS  = "volumeEffets";
    private static final String CLE_MUET           = "muet";

    private ParametresAudio() {}

    public static double getVolumeMusique() { return PREFS.getDouble(CLE_VOLUME_MUSIQUE, 1.0); }

    public static double getVolumeEffets() { return PREFS.getDouble(CLE_VOLUME_EFFETS, 1.0); }

    public static boolean isMuet() { return PREFS.getBoolean(CLE_MUET, false); }

    public static void setVolumeMusique(double volume) {
        PREFS.putDouble(CLE_VOLUME_MUSIQUE, clamp(volume));
        GestionnaireMusique.appliquerVolume();
    }

    public static void setVolumeEffets(double volume) {
        PREFS.putDouble(CLE_VOLUME_EFFETS, clamp(volume));
    }

    public static void setMuet(boolean muet) {
        PREFS.putBoolean(CLE_MUET, muet);
        GestionnaireMusique.appliquerVolume();
    }

    private static double clamp(double v) {
        return Math.max(0, Math.min(1, v));
    }
}
