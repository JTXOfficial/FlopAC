package me.jtx.flopac.tinyprotocol.packet.out;

import lombok.Getter;
import me.jtx.flopac.tinyprotocol.api.NMSObject;
import me.jtx.flopac.tinyprotocol.reflection.FieldAccessor;

@Getter
public class WrappedOutEntityDestroy extends NMSObject {
    private static final String packet = Server.ENTITY_DESTROY;


    private static final FieldAccessor<int[]> fieldId = fetchField(packet, int[].class, 0);

    private int[] entities;

    public WrappedOutEntityDestroy(Object packet) {
        super(packet);
    }

    @Override
    public void updateObject() {
        this.entities = fetch(fieldId);
    }
}