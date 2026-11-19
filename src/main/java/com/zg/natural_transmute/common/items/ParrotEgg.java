package com.zg.natural_transmute.common.items;

import com.zg.natural_transmute.common.entities.projectile.ThrownParrotEgg;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ParrotEgg extends ThrowableItem {
    private final Parrot.Variant variant;

    public ParrotEgg(Properties properties, Parrot.Variant variant) {
        super(properties);
        this.variant = variant;
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        var egg = new ThrownParrotEgg(level, pos.x(), pos.y(), pos.z());
        egg.setItem(stack);
        egg.setVariant(variant);
        return egg;
    }

    @Override
    public Projectile asShootProjectile(Level level, LivingEntity shooter, ItemStack stack) {
        var egg = new ThrownParrotEgg(level, shooter);
        egg.setItem(stack);
        egg.setVariant(variant);
        egg.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0, 1.5F, 1);
        return egg;
    }
}
