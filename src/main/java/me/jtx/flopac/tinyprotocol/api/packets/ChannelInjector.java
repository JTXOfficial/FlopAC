package me.jtx.flopac.tinyprotocol.api.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.jtx.flopac.FlopAC;
import me.jtx.flopac.tinyprotocol.api.ProtocolVersion;
import me.jtx.flopac.tinyprotocol.api.packets.channelhandler.ChannelHandler1_7;
import me.jtx.flopac.tinyprotocol.api.packets.channelhandler.ChannelHandler1_8;
import me.jtx.flopac.tinyprotocol.api.packets.channelhandler.ChannelHandlerAbstract;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


@Getter
public class ChannelInjector implements Listener {
    private final ChannelHandlerAbstract channel;

    public ChannelInjector() {
        this.channel = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)
                ? new ChannelHandler1_8() : new ChannelHandler1_7();
    }

    public void addChannel(Player player) {
        FlopAC.getInstance().getUserManager().addUser(player);
        this.channel.addChannel(player);
    }

    public void removeChannel(Player player) {
        FlopAC.getInstance().getUserManager().removeUser(player);
        this.channel.removeChannel(player);
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        FlopAC.getInstance().getExecutorService().execute(() -> addChannel(event.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        FlopAC.getInstance().getExecutorService().execute(() ->
                removeChannel(event.getPlayer()));
    }

    @Getter
    @AllArgsConstructor
    public static class Data {
        private final Player player;
        private final long time;
        private final int max;
    }
}
