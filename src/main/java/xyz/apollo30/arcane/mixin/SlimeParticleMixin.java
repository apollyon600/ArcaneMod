package xyz.apollo30.arcane.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlimeEntity.class)
public class SlimeParticleMixin extends MobEntity implements Monster {

    protected SlimeParticleMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "getParticles", cancellable = true)
    private void getParticles(CallbackInfoReturnable<ParticleEffect> cir) {
        cir.setReturnValue(new BlockStateParticleEffect(ParticleTypes.BLOCK, this.getSteppingBlockState()));
    }

}
