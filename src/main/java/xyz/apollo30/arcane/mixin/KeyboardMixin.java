package xyz.apollo30.arcane.mixin;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Shadow @Final private MinecraftClient client;

    @SuppressWarnings("deprecation")
    @Inject(method = "onKey", at = @At("HEAD"))
    private void onKeyPress(long window, int key, int scancode, int action, int mods, CallbackInfo ci) {
        if (this.client.player == null || this.client.currentScreen != null || this.client.isConnectedToLocalServer()) return;

        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(new Identifier("arcane:key"), new PacketByteBuf(Unpooled.buffer().writeInt(action).writeInt(key)));
        ClientSidePacketRegistry.INSTANCE.sendToServer(packet);
    }
}
