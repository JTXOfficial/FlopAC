package me.jtx.flopac.base.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.jtx.flopac.base.user.User;

@Getter @AllArgsConstructor
public class PacketEvent {
    private final User user;
    private final Object packet;
    private final String type;
    private final long timestamp;
}
