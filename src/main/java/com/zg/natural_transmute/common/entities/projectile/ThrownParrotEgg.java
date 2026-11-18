package com.zg.natural_transmute.common.entities.projectile;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ThrownParrotEgg extends LuckyEgg {
    @Getter
    @Setter
    private Parrot.Variant variant = Parrot.Variant.GREEN;

    public ThrownParrotEgg(EntityType<? extends ThrownEgg> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownParrotEgg(Level level, double x, double y, double z) {
        super(level, x, y, z);
    }

    public ThrownParrotEgg(Level level, LivingEntity shooter) {
        super(level, shooter);
    }

    @Override
    protected boolean isLucky(HitResult result) {
        return this.random.nextInt(4) == 0;
    }

    @Override
    protected void onLucky(HitResult result) {
        Parrot parrot = EntityType.PARROT.create(this.level());
        if (parrot != null) {
            parrot.setVariant(this.variant);
            parrot.setAge(-24000);
            parrot.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            if (!parrot.fudgePositionAfterSizeChange(ThrownEgg.ZERO_SIZED_DIMENSIONS)) {
                return;
            }
            this.level().addFreshEntity(parrot);
        }
    }
}
