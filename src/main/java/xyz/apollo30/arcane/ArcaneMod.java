package xyz.apollo30.arcane;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Unique;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ArcaneMod implements ModInitializer {

    public static final TranslatableTextContent ARCANE_IP = new TranslatableTextContent("arcane.ip", null, TranslatableTextContent.EMPTY_ARGUMENTS);

    @Unique
    private boolean[] isMousePressed = new boolean[3];
    @Unique
    private boolean hasSentModList = false;

    @Override
    public void onInitialize() {
        System.out.println("Loaded ArcaneMod v1.0.0!");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.currentScreen != null || client.isConnectedToLocalServer()) {
                if (this.hasSentModList) this.hasSentModList = false;
                Arrays.fill(isMousePressed, false);
                return;
            }

            checkMousePress(client, client.options.attackKey, 0);
            checkMousePress(client, client.options.useKey, 1);
            checkMousePress(client, client.options.pickItemKey, 2);

            if (!hasSentModList) {
                hasSentModList = true;

                List<String> loadedMods = FabricLoader.getInstance().getAllMods().stream().map(mod -> mod.getMetadata().getName()).toList();
                sendModList(loadedMods);
            }
        });
    }

    private void checkMousePress(MinecraftClient client, KeyBinding key, int index) {
        assert client.player != null;
        if (key.isPressed()) {
            if (!isMousePressed[index]) {
                isMousePressed[index] = true;
                sendMousePacket(1, index);
            }
        } else {
            if (isMousePressed[index]) {
                isMousePressed[index] = false;
                sendMousePacket(0, index);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void sendMousePacket(int action, int code) {
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(new Identifier("arcane:mouse"), new PacketByteBuf(Unpooled.buffer().writeInt(action).writeInt(code)));
        ClientSidePacketRegistry.INSTANCE.sendToServer(packet);
    }

    @SuppressWarnings("deprecation")
    private void sendModList(List<String> modList) {
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(new Identifier("arcane:mods"), new PacketByteBuf(Unpooled.wrappedBuffer(listToByteArray(modList))));
        ClientSidePacketRegistry.INSTANCE.sendToServer(packet);
    }

    @SuppressWarnings("deprecation")
    private void sendPackList(List<String> packList) {
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(new Identifier("arcane:packs"), new PacketByteBuf(Unpooled.wrappedBuffer(listToByteArray(packList))));
        ClientSidePacketRegistry.INSTANCE.sendToServer(packet);
    }

    private static byte @NotNull [] listToByteArray(List<String> stringList) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(stringList);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
