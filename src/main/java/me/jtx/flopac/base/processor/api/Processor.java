package me.jtx.flopac.base.processor.api;

import lombok.Getter;
import me.jtx.flopac.base.event.CallableEvent;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;

@Getter
public class Processor implements CallableEvent, ProcessorInterface {
    public User user;

    public void setup(User user) {
        this.user = user;
        this.setupTimers(user);
    }

    @Override
    public void onPacket(PacketEvent event) {
        //
    }

    @Override
    public void setupTimers(User user) {
        //
    }

    @Override
    public void onConnection(User user) {
        //
    }
}
