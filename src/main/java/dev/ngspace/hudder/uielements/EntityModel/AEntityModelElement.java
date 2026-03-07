package dev.ngspace.hudder.uielements.EntityModel;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.main.HudderRenderer;
import dev.ngspace.hudder.uielements.AUIElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EnderDragonRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.painting.Painting;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class AEntityModelElement extends AUIElement {

    protected final int i, j;
    protected final float bodyRot, xRot, yRot, xHitboxRot, zHitboxRot;
    protected final float scale;

    protected AEntityModelElement(int x, int y, float bodyRot, float xRot, float yRot,
                                  float xHitboxRot, float zHitboxRot, float scale) {
        this.i = x;
        this.j = y;
        this.bodyRot = bodyRot;
        this.xRot = xRot;
        this.yRot = yRot;
        this.xHitboxRot = xHitboxRot;
        this.zHitboxRot = zHitboxRot;
        this.scale = scale;
    }

    protected abstract Entity getEntity(Minecraft mc);

    @Override
    public void renderElement(GuiGraphics context, HudderRenderer renderer, DeltaTracker tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        Entity entity = getEntity(mc);

        Hudder.log(entity);

        if (entity instanceof EnderDragon dragon) {
            renderDragon(context, mc, dragon, tickDelta.getGameTimeDeltaTicks());
        } else if (entity instanceof LivingEntity living) {
            renderLivingEntity(context, mc, living, tickDelta.getGameTimeDeltaTicks());
        } else if (entity instanceof HangingEntity hanging) {
            renderHangingEntity(context, mc, hanging, tickDelta.getGameTimeDeltaTicks());
        } else if (entity instanceof AbstractBoat boat) {
            renderBoatEntity(context, mc, boat, tickDelta.getGameTimeDeltaTicks());
        } else if (entity instanceof  ExperienceOrb orb) {
            renderExperienceEntity(context, mc, orb, tickDelta.getGameTimeDeltaTicks());
        }
    }

    private void renderLivingEntity(GuiGraphics context, Minecraft mc, LivingEntity living, float tickDelta) {
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        EntityRenderer<? super LivingEntity, ?> renderer = dispatcher.getRenderer(living);

        LivingEntityRenderState state = (LivingEntityRenderState) renderer.createRenderState(living, tickDelta);

        // Apply rotations
        Quaternionf rotation = new Quaternionf()
                .rotateZ((float) Math.toRadians(180 + zHitboxRot))
                .rotateX((float) Math.toRadians(xHitboxRot));
        state.bodyRot = 180.0F + bodyRot;
        state.shadowRadius = 0;

        // Adjust bounding box and scale
        state.boundingBoxWidth /= state.scale;
        state.boundingBoxHeight /= state.scale;
        state.scale = 1.0F;

        // Compute pivot offset
        Vector3f pivot = new Vector3f(0, state.boundingBoxHeight / 2f, 0);
        Vector3f rotatedPivot = new Vector3f(pivot).rotate(rotation);
        Vector3f offset = new Vector3f(pivot).sub(rotatedPivot);
        Vector3f finalOffset = new Vector3f(offset.x, offset.y - pivot.y, offset.z);

        // Submit entity
        context.submitEntityRenderState(
                state, 40 * scale, finalOffset, rotation, rotation,
                -context.guiWidth() + i, -context.guiHeight() + j,
                context.guiWidth() + i, context.guiHeight() + j
        );
    }

    private void renderBoatEntity(GuiGraphics context, Minecraft mc, AbstractBoat boat, float tickDelta) {
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        EntityRenderer<? super AbstractBoat, ?> renderer = dispatcher.getRenderer(boat);

        EntityRenderState state = renderer.createRenderState(boat, tickDelta);

        // Apply rotations
        Quaternionf rotation = new Quaternionf()
                .rotateZ((float) Math.toRadians(180 + zHitboxRot))
                .rotateX((float) Math.toRadians(xHitboxRot));
        state.shadowRadius = 0;

        boat.setPaddleState(true, false);

        // Compute pivot offset
        Vector3f pivot = new Vector3f(0, state.boundingBoxHeight / 2f, 0);
        Vector3f rotatedPivot = new Vector3f(pivot).rotate(rotation);
        Vector3f offset = new Vector3f(pivot).sub(rotatedPivot);
        Vector3f finalOffset = new Vector3f(offset.x, offset.y - pivot.y, offset.z);

        // Submit entity
        context.submitEntityRenderState(
                state, 40 * scale, finalOffset, rotation, rotation,
                -context.guiWidth() + i, -context.guiHeight() + j,
                context.guiWidth() + i, context.guiHeight() + j
        );
    }

    private void renderExperienceEntity(GuiGraphics context, Minecraft mc, ExperienceOrb experienceOrb, float tickDelta) {
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        EntityRenderer<? super ExperienceOrb, ?> renderer = dispatcher.getRenderer(experienceOrb);

        ExperienceOrb newOrb = new ExperienceOrb(mc.level, 20d, 0, 0, 1);

        EntityRenderState state = renderer.createRenderState(newOrb, tickDelta);

        // Apply rotations
        Quaternionf rotation = new Quaternionf()
                .rotateZ((float) Math.toRadians(180 + zHitboxRot))
                .rotateX((float) Math.toRadians(xHitboxRot));
        state.shadowRadius = 0;

        // Compute pivot offset
        Vector3f pivot = new Vector3f(0, state.boundingBoxHeight / 2f, 0);
        Vector3f rotatedPivot = new Vector3f(pivot).rotate(rotation);
        Vector3f offset = new Vector3f(pivot).sub(rotatedPivot);
        Vector3f finalOffset = new Vector3f(offset.x, offset.y - pivot.y, offset.z);

        // Submit entity
        context.submitEntityRenderState(
                state, 40 * scale, finalOffset, rotation, rotation,
                -context.guiWidth() + i, -context.guiHeight() + j,
                context.guiWidth() + i, context.guiHeight() + j
        );
    }

    private void renderHangingEntity(GuiGraphics context, Minecraft mc, Entity hanging, float tickDelta) {

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        EntityRenderer<? super Entity, ?> renderer = dispatcher.getRenderer(hanging);

        hanging.setXRot(0);
        hanging.setYRot(0);

        int rot = 0;
        ItemStack stack = null;

        EntityRenderState state = null;

        if (hanging instanceof ItemFrame frame) {
            rot = frame.getRotation();
            stack = frame.getItem();
            frame.setItem(new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("minecraft","diamond"))));
            frame.setRotation(1);
            state = renderer.createRenderState(frame, tickDelta);
            frame.setRotation(rot);
            frame.setItem(stack);
        } else if (hanging instanceof Painting) {
            Registry<PaintingVariant> paintingRegistry = mc.level.registryAccess()
                    .lookupOrThrow(Registries.PAINTING_VARIANT);

            ResourceKey<PaintingVariant> variantKey = ResourceKey.create(
                    Registries.PAINTING_VARIANT,
                    Identifier.fromNamespaceAndPath("minecraft", "kebab")
            );

            PaintingVariant variant = paintingRegistry.getValueOrThrow(variantKey);
            Holder<PaintingVariant> Variant = paintingRegistry.wrapAsHolder(variant);

            BlockPos pos = new BlockPos(0, 0, 0);
            Direction facing = Direction.SOUTH;
            Painting painting = new Painting(mc.level, pos, facing, Variant);
            state = renderer.createRenderState(painting, tickDelta);
        }

        // Apply rotations
        Quaternionf rotation = new Quaternionf()
                .rotateZ((float) Math.toRadians(180 + zHitboxRot))
                .rotateX((float) Math.toRadians(xHitboxRot));
        state.shadowRadius = 0;

        // Compute pivot offset
        Vector3f pivot = new Vector3f(0, state.boundingBoxHeight / 2f, 0);
        Vector3f rotatedPivot = new Vector3f(pivot).rotate(rotation);
        Vector3f offset = new Vector3f(pivot).sub(rotatedPivot);
        Vector3f finalOffset = new Vector3f(offset.x, offset.y - pivot.y, offset.z);

        // Submit entity
        context.submitEntityRenderState(
                state, 40 * scale, finalOffset, rotation, rotation,
                -context.guiWidth() + i, -context.guiHeight() + j,
                context.guiWidth() + i, context.guiHeight() + j
        );
    }

    private void renderDragon(GuiGraphics context, Minecraft mc, EnderDragon dragon, float tickDelta) {
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        EntityRenderer<? super EnderDragon, ?> renderer = dispatcher.getRenderer(dragon);

        EnderDragonRenderState state = (EnderDragonRenderState) renderer.createRenderState(dragon, tickDelta);

        // Hide the healing beam
        state.beamOffset = Vec3.ZERO;

        // Apply rotations
        Quaternionf rotation = new Quaternionf()
                .rotateZ((float) Math.toRadians(180 + zHitboxRot))
                .rotateX((float) Math.toRadians(xHitboxRot));

        // Compute pivot offset
        float halfHeight = dragon.getBbHeight() / 2f;
        Vector3f pivot = new Vector3f(0, halfHeight, 0);
        Vector3f rotatedPivot = new Vector3f(pivot).rotate(rotation);
        Vector3f offset = new Vector3f(pivot).sub(rotatedPivot);
        Vector3f finalOffset = new Vector3f(offset.x, offset.y - halfHeight, offset.z);

        // Submit entity
        context.submitEntityRenderState(
                state, 40 * scale, finalOffset, rotation, rotation,
                -context.guiWidth() + i, -context.guiHeight() + j,
                context.guiWidth() + i, context.guiHeight() + j
        );
    }
}