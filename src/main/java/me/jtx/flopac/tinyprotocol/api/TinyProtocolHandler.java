package me.jtx.flopac.tinyprotocol.api;

import lombok.Getter;
import me.jtx.flopac.FlopAC;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.packets.ChannelInjector;
import me.jtx.flopac.util.BlockUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TinyProtocolHandler {
    @Getter
    private static ChannelInjector instance;

    public TinyProtocolHandler() {
        new BlockUtil();
        instance = new ChannelInjector();
        Bukkit.getPluginManager().registerEvents(instance, FlopAC.getInstance());
    }

    public static void sendPacket(Player player, Object packet) {
        instance.getChannel().sendPacket(player, packet);
    }

    public Object onPacketOutAsync(Player sender, Object packet) {
        String name = packet.getClass().getName();
        int index = name.lastIndexOf(".");
        String packetName = name.substring(index + 1)
                .replace(Packet.Client.LEGACY_LOOK, Packet.Client.LOOK)
                .replace(Packet.Client.LEGACY_POSITION, Packet.Client.POSITION)
                .replace(Packet.Client.LEGACY_POSITION_LOOK, Packet.Client.POSITION_LOOK);

        User user = FlopAC.getInstance().getUserManager().getUser(sender);
        if (user != null) {
            this.fire(user, packetName, packet);
        }

        return packet;
    }

    public Object onPacketInAsync(Player sender, Object packet) {

        String name = packet.getClass().getName();
        int index = name.lastIndexOf(".");
        String packetName = name.substring(index + 1).replace("PacketPlayInUseItem",
                "PacketPlayInBlockPlace").replace(Packet.Client.LEGACY_LOOK,
                Packet.Client.LOOK).replace(Packet.Client.LEGACY_POSITION,
                Packet.Client.POSITION).replace(Packet.Client.LEGACY_POSITION_LOOK, Packet.Client.POSITION_LOOK);

        User user = FlopAC.getInstance().getUserManager().getUser(sender);
        if (user != null) {
            this.fire(user, packetName, packet);
        }

        return packet;
    }

    void fire(User user, String type, Object packet) {
        user.getExecutorService().execute(() -> {
            PacketEvent packetEvent = new PacketEvent(user, packet, type, System.currentTimeMillis());
            user.getEventManager().processProcessors(packetEvent);
            user.getEventManager().processChecks(packetEvent);
        });
    }
}

