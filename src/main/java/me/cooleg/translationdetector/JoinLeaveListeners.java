package me.cooleg.translationdetector;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

public class JoinLeaveListeners implements Listener {

    private final TranslationDetector plugin;

    public JoinLeaveListeners(TranslationDetector detector) {
        plugin = detector;
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        CheckTranslation.addToQueue(event.getPlayer(), new ArrayList<>(Config.goodTranslations), false);
        CheckTranslation.addToQueue(event.getPlayer(), new ArrayList<>(Config.badTranslations), true);
        CheckTranslation.runQueue(event.getPlayer());
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {
        CheckTranslation.removePlayer(event.getPlayer());
    }

}
