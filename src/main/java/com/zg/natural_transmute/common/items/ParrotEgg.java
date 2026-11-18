package com.zg.natural_transmute.common.items;

import com.zg.natural_transmute.common.entities.projectile.ThrownParrotEgg;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;

public class ParrotEgg extends Item implements ProjectileItem {
    private final Parrot.Variant variant;

    public ParrotEgg(Properties properties, Parrot.Variant variant) {
        super(properties);
        this.variant = variant;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var item = player.getItemInHand(hand);
        level.playSound(
            null,
            player.getX(),
            player.getY(),
            player.getZ(),
            SoundEvents.EGG_THROW,
            SoundSource.PLAYERS,
            0.5F,
            0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        if (!level.isClientSide) {
            ThrownEgg thrownegg = new ThrownEgg(level, player);
            thrownegg.setItem(item);
            thrownegg.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(thrownegg);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        item.consume(1, player);
        return InteractionResultHolder.sidedSuccess(item, level.isClientSide());
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        ThrownParrotEgg egg = new ThrownParrotEgg(level, pos.x(), pos.y(), pos.z());
        egg.setItem(stack);
        egg.setVariant(variant);
        return egg;
    }
}
