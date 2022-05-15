/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package me.jtx.flopac.tinyprotocol.packet.types;

import lombok.Getter;
import me.jtx.flopac.tinyprotocol.api.NMSObject;
import me.jtx.flopac.tinyprotocol.api.ProtocolVersion;
import me.jtx.flopac.tinyprotocol.reflection.FieldAccessor;
import me.jtx.flopac.tinyprotocol.reflection.Reflection;
import me.jtx.flopac.util.box.ReflectionUtil;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class WrappedGameProfile extends NMSObject {
    private static final String type = Type.GAMEPROFILE;

    // Fields
    private static final FieldAccessor<UUID> fieldId = fetchField(type, UUID.class, 0);
    private static final FieldAccessor<String> fieldName = fetchField(type, String.class, 0);
    private static final FieldAccessor<?> fieldPropertyMap = fetchField(type, Reflection.getClass(Type.PROPERTYMAP), 0);

    // Decoded data
    public UUID id;
    public String name;
    public Object propertyMap;

    public WrappedGameProfile(Object type) {
        super(type);
    }

    @Override
    public void updateObject() {

    }

    public WrappedGameProfile(Player player) {
        Object entityPlayer = ReflectionUtil.getEntityPlayer(player);
        FieldAccessor<Object> gameProfileAcessor = fetchField("EntityHuman", Reflection.NMS_PREFIX + type, 0);
        setObject(fetch(gameProfileAcessor));
        id = fieldId.get(getObject());
        name = fieldName.get(getObject());
        propertyMap = fieldPropertyMap.get(getObject());
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        id = fieldId.get(getObject());
        name = fieldName.get(getObject());
        propertyMap = fieldPropertyMap.get(getObject());
    }
}
