package me.cooleg.translationdetector;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class TranslationDetector extends JavaPlugin {

    public static final Logger LOGGER = LogManager.getLogger("TranslationDetector");

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Config.loadConfig(this);

        Bukkit.getPluginManager().registerEvents(new JoinLeaveListeners(this), this);
        PacketEvents.getAPI().getEventManager().registerListener(new CheckTranslation(), PacketListenerPriority.LOW);

        getCommand("testtranslations").setExecutor(new TestTranslationCommand());
    }

}
