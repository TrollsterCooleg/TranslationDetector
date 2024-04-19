package me.cooleg.translationdetector;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Config {

    public static List<String> badTranslations;
    public static List<String> goodTranslations;
    public static boolean resourcePackSupport;

    public static void loadConfig(TranslationDetector detector) {
        FileConfiguration config = detector.getConfig();

        badTranslations = config.getStringList("bad-translations");
        goodTranslations = config.getStringList("good-translations");
        resourcePackSupport = config.getBoolean("legacy-resource-packs");
    }

}
