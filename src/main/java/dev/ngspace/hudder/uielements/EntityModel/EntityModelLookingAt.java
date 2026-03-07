package dev.ngspace.hudder.uielements.EntityModel;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;

public class EntityModelLookingAt extends AEntityModelElement {

    public EntityModelLookingAt(int x, int y, float bodyRot,
                                float xRot, float yRot, float xHitboxRot,
                                float zHitboxRot, float scale) {
        super(x, y, bodyRot, xRot, yRot, xHitboxRot, zHitboxRot, scale);
    }

    @Override
    protected Entity getEntity(Minecraft mc) {
        Entity entity = mc.crosshairPickEntity;
        if (entity instanceof EnderDragonPart) return ((EnderDragonPart) entity).parentMob;
        return mc.crosshairPickEntity;
    }
}