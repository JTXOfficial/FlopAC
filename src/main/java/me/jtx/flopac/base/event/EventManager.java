package me.jtx.flopac.base.event;

import lombok.AllArgsConstructor;
import me.jtx.flopac.FlopAC;
import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.util.TimeUtils;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class EventManager {
    private final User user;

    public void processProcessors(PacketEvent packetEvent) {
        this.user.getProcessorManager().getProcessors().forEach(processor ->
                processor.onPacket(packetEvent));
    }

    public void processChecks(PacketEvent packetEvent) {
        this.user.getCheckManager().getCheckList().stream().filter(Check::isEnabled).forEach(check ->
                check.onPacket(packetEvent));
    }

    public void processTime() {
        FlopAC.getInstance().getTimeService().scheduleAtFixedRate(() ->
                FlopAC.getInstance().currentDate = TimeUtils.GetDate(),
                50L, 50L, TimeUnit.MILLISECONDS);

    }
}
