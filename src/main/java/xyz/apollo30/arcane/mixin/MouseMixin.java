package xyz.apollo30.arcane.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.apollo30.arcane.ArcaneMod;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseScroll", at = @At("HEAD"))
    public void on(long window, double horizontal, double vertical, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        double d = (client.options.getDiscreteMouseScroll().getValue() ? Math.signum(vertical) : vertical) * client.options.getMouseWheelSensitivity().getValue();
        if (client.player == null || client.currentScreen != null || client.isConnectedToLocalServer()) return;
        if (d > 0)
            ArcaneMod.sendKeybindPress(1, 30);
        else
            ArcaneMod.sendKeybindPress(1, 31);
    }

}
