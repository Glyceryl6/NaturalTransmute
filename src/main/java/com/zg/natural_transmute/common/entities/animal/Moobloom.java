package com.zg.natural_transmute.common.entities.animal;

import com.zg.natural_transmute.registry.NTBlocks;
import com.zg.natural_transmute.registry.NTEntityTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class Moobloom extends MushroomCow {

    public Moobloom(EntityType<? extends Moobloom> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void shear(SoundSource category) {
        this.level().playSound(null, this, SoundEvents.MOOSHROOM_SHEAR, category, 1.0F, 1.0F);
        if (!this.level().isClientSide()) {
            Cow cow = EntityType.COW.create(this.level());
            if (cow != null) {
                ((ServerLevel)this.level()).sendParticles(ParticleTypes.EXPLOSION,
                        this.getX(), this.getY(0.5), this.getZ(),
                        1, 0.0D, 0.0D, 0.0D, 0.0D);
                this.discard();
                cow.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                cow.setHealth(this.getHealth());
                cow.yBodyRot = this.yBodyRot;
                if (this.hasCustomName()) {
                    cow.setCustomName(this.getCustomName());
                    cow.setCustomNameVisible(this.isCustomNameVisible());
                }

                if (this.isPersistenceRequired()) {
                    cow.setPersistenceRequired();
                }

                cow.setInvulnerable(this.isInvulnerable());
                this.level().addFreshEntity(cow);
                for (int i = 0; i < 5; i++) {
                    ItemEntity item = this.spawnAtLocation(new ItemStack(NTBlocks.BUTTERCUP.get()), this.getBbHeight());
                    if (item != null) {
                        item.setNoPickUpDelay();
                    }
                }
            }
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(NTBlocks.BUTTERCUP.get().asItem());
    }

    @Override
    public @Nullable MushroomCow getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return NTEntityTypes.MOOBLOOM.get().create(level);
    }

}