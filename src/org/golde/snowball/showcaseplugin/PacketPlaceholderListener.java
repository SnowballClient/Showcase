package org.golde.snowball.showcaseplugin;

import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.golde.snowball.api.Keyboard;
import org.golde.snowball.showcaseplugin.objs.KeyGui;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.gmail.filoghost.holographicdisplays.bridge.protocollib.current.packet.WrapperPlayServerEntityMetadata;
import com.gmail.filoghost.holographicdisplays.bridge.protocollib.current.packet.WrapperPlayServerSpawnEntityLiving;

public class PacketPlaceholderListener extends PacketAdapter {

    private static final int CUSTOM_NAME_WATCH_INDEX = 2;

    Showcase pl;
    
    public PacketPlaceholderListener(Showcase showcase, AdapterParameteters params) {
        super(params);
        this.pl = showcase;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY_LIVING) {
            WrapperPlayServerSpawnEntityLiving spawnEntityPacket = new WrapperPlayServerSpawnEntityLiving(packet.deepClone());
            WrappedWatchableObject customNameWatchableObject = spawnEntityPacket.getMetadata().getWatchableObject(2);
            replacePlaceholders(customNameWatchableObject, player);
            event.setPacket(spawnEntityPacket.getHandle());
        }  else if (packet.getType() == PacketType.Play.Server.ENTITY_METADATA) {

            WrapperPlayServerEntityMetadata entityMetadataPacket = new WrapperPlayServerEntityMetadata(packet.deepClone());
            List<WrappedWatchableObject> dataWatcherValues = entityMetadataPacket.getEntityMetadata();

            for (WrappedWatchableObject watchableObject : dataWatcherValues) {
                if (watchableObject.getIndex() == 2) {
                    if (replacePlaceholders(watchableObject, event.getPlayer())) {
                        event.setPacket(entityMetadataPacket.getHandle());
                    }

                    return;
                }
            }
        }
    }

    private boolean replacePlaceholders(WrappedWatchableObject customNameWatchableObject, Player player) {
        if (customNameWatchableObject == null) return true;

        Object customNameWatchableObjectValue = customNameWatchableObject.getValue();
        String customName;

        customName = (String) customNameWatchableObjectValue;

        KeyGui keyGui = pl.getPlayerLastPressedKeys().get(player.getUniqueId());
        String key = (keyGui == null ?  "null" : Keyboard.getKeyName(keyGui.key));
        String keyInGui = (keyGui == null ?  "null" : toString().valueOf(keyGui.inGui));
        customName = pl.getHd_key().replace(customName, key);
        customName = pl.getHd_key_gui().replace(customName, keyInGui);

        customNameWatchableObject.setValue(customName);

        return true;
    }

}
