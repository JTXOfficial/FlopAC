package me.jtx.flopac.base.event;

import me.jtx.flopac.base.user.User;

public interface CallableEvent {
    void onPacket(PacketEvent event);
    void setupTimers(User user);
    void onConnection(User user);
}
