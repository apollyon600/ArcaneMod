package xyz.apollo30.arcane.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AllayEntity.class)
public class AllayMixin extends PathAwareEntity {

    protected AllayMixin(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "interactMob", cancellable = true)
    private void onInteractMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        super.interactMob(player, hand);
        cir.setReturnValue(ActionResult.FAIL);
    }

}
