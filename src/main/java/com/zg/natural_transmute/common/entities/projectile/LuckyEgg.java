package com.zg.natural_transmute.common.entities.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public abstract class LuckyEgg extends ThrownEgg {
    public LuckyEgg(EntityType<? extends ThrownEgg> entityType, Level level) {
        super(entityType, level);
    }

    public LuckyEgg(Level level, double x, double y, double z) {
        super(level, x, y, z);
    }

    public LuckyEgg(Level level, LivingEntity shooter) {
        super(level, shooter);
    }

    @Override
    protected void onHit(HitResult result) {
        if (!this.level().isClientSide) {
            if (isLucky(result)) {
                onLucky(result);
            }

            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }
    }

    protected abstract boolean isLucky(HitResult result);

    protected abstract void onLucky(HitResult result);
}
