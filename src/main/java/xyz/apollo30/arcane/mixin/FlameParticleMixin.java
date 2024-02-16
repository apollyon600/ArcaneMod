package xyz.apollo30.arcane.mixin;

import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SnowflakeParticle;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlameParticle.class)
public class FlameParticleMixin extends SpriteBillboardParticle {

    protected FlameParticleMixin(ClientWorld clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
    }

    @Inject(at = @At("HEAD"), method = "getType")
    private void init(CallbackInfoReturnable<ParticleTextureSheet> cir) {
        this.maxAge = 2;
        this.scale(2f);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

}
