package dev.ngspace.hudder.uielements.EntityModel;

import dev.ngspace.hudder.Hudder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.*;

public class EntityModelElement extends AEntityModelElement {

    private final Identifier entityId;

    private Entity cachedEntity;
    private int cachedWorldHash;

    public EntityModelElement(Identifier entityId, int x, int y, float bodyRot,
                              float xRot, float yRot, float xHitboxRot,
                              float zHitboxRot, float scale) {
        super(x, y, bodyRot, xRot, yRot, xHitboxRot, zHitboxRot, scale);
        this.entityId = entityId;
    }

    @Override
    protected Entity getEntity(Minecraft mc) {
        if (mc.level == null) return null;

        int worldHash = mc.level.hashCode();

        if (cachedEntity == null || cachedWorldHash != worldHash) {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(entityId);
            Entity entity = type.create(mc.level, EntitySpawnReason.COMMAND);
            Hudder.log(entity);

            if (entity instanceof Entity living) {
                cachedEntity = living;
                cachedWorldHash = worldHash;
            }
        }

        return cachedEntity;
    }
}