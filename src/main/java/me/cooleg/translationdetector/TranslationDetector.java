package me.cooleg.translationdetector;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class TranslationDetector extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Config.loadConfig(this);

        Bukkit.getPluginManager().registerEvents(new JoinLeaveListeners(this), this);
        PacketEvents.getAPI().getEventManager().registerListener(new CheckTranslation(), PacketListenerPriority.LOW);
    }

}
