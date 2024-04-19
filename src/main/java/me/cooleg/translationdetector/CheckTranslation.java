package me.cooleg.translationdetector;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientNameItem;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCloseWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.*;

public class CheckTranslation implements PacketListener {

    private static final int WINDOW_ID = 239;
    private static final int ANVIL_ID = Reflection.getAnvilId();
    private static final WrapperPlayServerOpenWindow openWindow = new WrapperPlayServerOpenWindow(WINDOW_ID, ANVIL_ID, Component.text("Repair & Name"));
    private static final WrapperPlayServerCloseWindow closeWindow = new WrapperPlayServerCloseWindow(WINDOW_ID);

    private static final HashMap<UUID, List<String>> goodTranslationsToCheck = new HashMap<>();
    private static final HashMap<UUID, List<String>> badTranslationsToCheck = new HashMap<>();

    private static final HashSet<UUID> awaitingBadTranslation = new HashSet<>();
    private static final HashSet<UUID> awaitingGoodTranslation = new HashSet<>();

    public static void addToQueue(Player player, List<String> translations, boolean badTranslations) {
        if (translations.isEmpty()) return;

        if (badTranslations) badTranslationsToCheck.put(player.getUniqueId(), translations);
        else goodTranslationsToCheck.put(player.getUniqueId(), translations);
    }

    public static void runQueue(Player player) {
        List<String> good = goodTranslationsToCheck.get(player.getUniqueId());
        List<String> bad = badTranslationsToCheck.get(player.getUniqueId());

        if (good != null) {
            if (good.isEmpty()) {
                goodTranslationsToCheck.remove(player.getUniqueId());
            } else {
                checkPlayer(player, good.remove(0), false);
                return;
            }
        }

        if (bad != null) {
            if (bad.isEmpty()) {
                badTranslationsToCheck.remove(player.getUniqueId());
            } else {
                checkPlayer(player, bad.remove(0), true);
                return;
            }
        }
    }

    public static void checkPlayer(Player player, String translation, boolean badTranslation) {
        checkPlayer(PacketEvents.getAPI().getPlayerManager().getUser(player), translation, badTranslation);
    }

    public static void checkPlayer(User user, String translation, boolean badTranslation) {
        user.sendPacket(openWindow);

        NBTCompound nbt = new NBTCompound();
        nbt.setTag("display", new NBTCompound());
        nbt.getCompoundTagOrNull("display").setTag("Name", new NBTString("{\"translate\":\"" + translation + "\"}"));
        ItemStack stack = ItemStack.builder().type(ItemTypes.IRON_SWORD).amount(1).nbt(nbt).build();

        WrapperPlayServerWindowItems setSlot = new WrapperPlayServerWindowItems(WINDOW_ID, 1, List.of(stack), stack);
        user.sendPacket(setSlot);

        user.sendPacket(closeWindow);

        if (badTranslation) awaitingBadTranslation.add(user.getUUID());
        else awaitingGoodTranslation.add(user.getUUID());
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.NAME_ITEM) {
            if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
                WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);
                System.out.println(packet.getSlot());
            }
            return;
        }

        User user = event.getUser();
        UUID uuid = user.getUUID();
        WrapperPlayClientNameItem nameItem = new WrapperPlayClientNameItem(event);
        String name = nameItem.getItemName();
        System.out.println(name);

        if (awaitingGoodTranslation.contains(uuid)) {
            if (Config.goodTranslations.contains(name)) {
                user.closeConnection();

                System.out.println("Good translation kick");
                removePlayer(user);
                return;
            }

            awaitingGoodTranslation.remove(uuid);
        } else if (awaitingBadTranslation.contains(uuid)) {
            if (!Config.badTranslations.contains(name)) {
                user.closeConnection();

                System.out.println("Bad translation kick");
                removePlayer(user);
                return;
            }

            awaitingBadTranslation.remove(uuid);
        }

        if (goodTranslationsToCheck.containsKey(uuid)) {
            List<String> translations = goodTranslationsToCheck.get(uuid);
            if (translations == null || translations.isEmpty()) {
                goodTranslationsToCheck.remove(uuid);
            } else {
                checkPlayer(user, translations.remove(0), false);
                return;
            }
        }

        if (badTranslationsToCheck.containsKey(uuid)) {
            List<String> translations = badTranslationsToCheck.get(uuid);
            if (translations == null || translations.isEmpty()) {
                badTranslationsToCheck.remove(uuid);
            } else {
                checkPlayer(user, translations.remove(0), true);
            }
        }
    }

    public static void removePlayer(UUID uuid) {
        goodTranslationsToCheck.remove(uuid);
        badTranslationsToCheck.remove(uuid);

        awaitingGoodTranslation.remove(uuid);
        awaitingBadTranslation.remove(uuid);
    }

    public static void removePlayer(Player player) {
        removePlayer(player.getUniqueId());
    }

    public static void removePlayer(User user) {
        removePlayer(user.getUUID());
    }
}
