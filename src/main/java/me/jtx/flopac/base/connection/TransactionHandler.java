package me.jtx.flopac.base.connection;

import lombok.Getter;
import lombok.Setter;
import me.jtx.flopac.FlopAC;
import me.jtx.flopac.tinyprotocol.api.ProtocolVersion;
import me.jtx.flopac.tinyprotocol.packet.out.WrappedOutTransaction;
import me.jtx.flopac.util.RunUtils;
import org.bukkit.scheduler.BukkitTask;

@Getter
@Setter
public class TransactionHandler implements Runnable {
    public TransactionHandler() {
        this.start();
    }

    private short time = 32000;
    private BukkitTask bukkitTask;

    public void start() {
        if (this.bukkitTask == null) {
            this.bukkitTask = RunUtils.taskTimerAsync(
                    this, 0L, 0L);
        }
    }

    @Override
    public void run() {

        if (this.time-- < 1) {
            this.time = 32000;
        }

        if (ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_9_4)) {
            //Fix for 1.9+ servers, because keepalives are broken for some reason...?
            this.processTransaction();
        } else {
            this.processTransaction();
        }
    }

   /* void processKeepAlive() {
        WrappedOutKeepAlivePacket wrappedOutKeepAlivePacket = new WrappedOutKeepAlivePacket(this.time);
        FlopAC.getInstance().getUserManager().getUserMap().forEach((uuid, user) -> {
            user.getConnectionMap2().put(this.time, System.currentTimeMillis());
            user.sendPacket(wrappedOutKeepAlivePacket.getObject());
        });
    } */

    void processTransaction() {
        WrappedOutTransaction wrappedOutTransaction = new WrappedOutTransaction(0, this.time,
                false);

        FlopAC.getInstance().getUserManager().getUserMap().forEach((uuid, user) -> {
            user.getConnectionMap().put(this.time, System.currentTimeMillis());
            user.sendPacket(wrappedOutTransaction.getObject());
        });
    }
}
