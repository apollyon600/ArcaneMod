package xyz.apollo30.arcane.mixin;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SnowflakeParticle.class)
public class SnowflakeParticleMixin extends SpriteBillboardParticle {

    protected SnowflakeParticleMixin(ClientWorld clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
    }

    @Inject(at = @At("HEAD"), method = "getType")
    private void init(CallbackInfoReturnable<ParticleTextureSheet> cir) {
        this.maxAge = 3;
        this.scale(0.05f + (this.random.nextFloat() * this.random.nextFloat() * 0.5f + 0.5f));
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

}
