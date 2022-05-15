package me.jtx.flopac.tinyprotocol.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.jtx.flopac.tinyprotocol.api.packets.reflections.types.WrappedField;

@Getter
@AllArgsConstructor
public class PacketField<T> {
    private final WrappedField field;
    private final T value;
}
