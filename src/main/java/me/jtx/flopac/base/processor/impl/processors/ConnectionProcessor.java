package me.jtx.flopac.base.processor.impl.processors;

import lombok.Getter;
import lombok.Setter;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.processor.api.Processor;
import me.jtx.flopac.base.processor.api.ProcessorInformation;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInKeepAlivePacket;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInTransactionPacket;
import me.jtx.flopac.util.MathUtil;
import me.jtx.flopac.util.evicting.EvictingMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ProcessorInformation(name = "Connection")
@Getter @Setter
public class ConnectionProcessor extends Processor {

    private final Map<Long, Long> sentKeepAlives = new EvictingMap<>(100);
    private final Map<Short, Long> sentTransactions = new EvictingMap<>(100);
    private int ping, transPing, lastTransPing, dropTransTime;
    private int clientTick, flyingTick;
    private boolean isLagging = false;
    private int dropTick, averageTransactionPing;
    private short id = Short.MAX_VALUE;

    private List<Integer> pingList = new ArrayList<>();

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {

            case Packet.Client.KEEP_ALIVE: {
                WrappedInKeepAlivePacket wrappedInKeepAlivePacket = new WrappedInKeepAlivePacket(
                        event.getPacket(), event.getUser().getPlayer());

                this.processK(user, wrappedInKeepAlivePacket.getTime());
                break;
            }

            case Packet.Client.TRANSACTION: {
                WrappedInTransactionPacket wrappedInTransactionPacket = new WrappedInTransactionPacket(
                        event.getPacket(), event.getUser().getPlayer());

                this.processT(user, wrappedInTransactionPacket.getAction());
                break;
            }
        }
    }

    void processT(User user, short time) {
        if (this.user.getConnectionMap().containsKey(time)) {

            this.lastTransPing = transPing;
            this.transPing = (int) (System.currentTimeMillis() - this.user.getConnectionMap()
                    .get(time));
            this.dropTransTime = Math.abs(transPing - lastTransPing);
            this.sentTransactions.put(time, System.currentTimeMillis());
            this.clientTick = (int) Math.ceil(this.transPing / 50.0);

            this.flyingTick = 0;
            this.dropTick++;

            pingList.add(transPing);

            if (pingList.size() > 250) {
                averageTransactionPing = (int) MathUtil.getAverage(pingList);
                pingList.clear();
            }

            user.getConnectionMap().remove(time);
            user.getCheckManager().getCheckList().forEach(check -> check.onConnection(user));
        }
    }

    void processK(User user, long time) {
        if (this.user.getConnectionMap2().containsKey(time)) {

            this.ping = (int) (System.currentTimeMillis() - this.user.getConnectionMap2()
                    .get(time));

            this.sentKeepAlives.put(time, System.currentTimeMillis());
           // this.clientTick = (int) Math.ceil(this.ping / 50.0);

            user.getConnectionMap2().remove(time);
            user.getCheckManager().getCheckList().forEach(check -> check.onConnection(user));
        }
    }
}
