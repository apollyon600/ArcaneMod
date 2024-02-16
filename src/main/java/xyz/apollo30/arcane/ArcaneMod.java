package xyz.apollo30.arcane;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Unique;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

public class ArcaneMod implements ModInitializer {

    @Unique
    private boolean[] isMousePressed = new boolean[50];
    @Unique
    private boolean hasSentModList = false;

    // Static
//    public final KeyBinding mouse4 = new KeyBinding("key.mouse_4", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_4, KeyBinding.GAMEPLAY_CATEGORY);
//    public final KeyBinding mouse5 = new KeyBinding("key.mouse_5", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_5, KeyBinding.GAMEPLAY_CATEGORY);
//    public final KeyBinding mouse6 = new KeyBinding("key.mouse_6", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_6, KeyBinding.GAMEPLAY_CATEGORY);
//    public final KeyBinding mouse7 = new KeyBinding("key.mouse_7", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_7, KeyBinding.GAMEPLAY_CATEGORY);
//    public final KeyBinding mouse8 = new KeyBinding("key.mouse_8", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_8, KeyBinding.GAMEPLAY_CATEGORY);

    @Override
    public void onInitialize() {
        System.out.println("Loaded ArcaneMod v1.0.0!");

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player == null || client.currentScreen != null || client.isConnectedToLocalServer()) {
                if (this.hasSentModList) this.hasSentModList = false;
                Arrays.fill(isMousePressed, false);
                return;
            }

            checkKeybindPress(client, client.options.attackKey, 0);
            checkKeybindPress(client, client.options.useKey, 1);
            checkKeybindPress(client, client.options.pickItemKey, 2);
            checkKeybindPress(client, client.options.forwardKey, 3);
            checkKeybindPress(client, client.options.leftKey, 4);
            checkKeybindPress(client, client.options.backKey, 5);
            checkKeybindPress(client, client.options.rightKey, 6);
            checkKeybindPress(client, client.options.jumpKey, 7);
            checkKeybindPress(client, client.options.sneakKey, 8);
            checkKeybindPress(client, client.options.sprintKey, 9);
            checkKeybindPress(client, client.options.inventoryKey, 10);
            checkKeybindPress(client, client.options.swapHandsKey, 11);
            checkKeybindPress(client, client.options.dropKey, 12);
            checkKeybindPress(client, client.options.chatKey, 13);
            checkKeybindPress(client, client.options.playerListKey, 14);
            checkKeybindPress(client, client.options.screenshotKey, 15);

//            checkKeybindPress(client, mouse4, 20);
//            checkKeybindPress(client, mouse5, 21);
//            checkKeybindPress(client, mouse6, 22);
//            checkKeybindPress(client, mouse7, 23);
//            checkKeybindPress(client, mouse8, 24);

            if (!hasSentModList) {
                hasSentModList = true;

                sendPackList(client.getResourcePackManager().getEnabledNames().stream().toList());

                List<String> loadedMods = FabricLoader.getInstance().getAllMods().stream().map(mod -> mod.getMetadata().getName()).toList();
                sendModList(loadedMods);
            }
        });
    }

    private void checkKeybindPress(MinecraftClient client, KeyBinding key, int index) {
        assert client.player != null;
        if (key.isPressed()) {
            if (!isMousePressed[index]) {
                isMousePressed[index] = true;
                sendKeybindPress(1, index);
            }
        } else {
            if (isMousePressed[index]) {
                isMousePressed[index] = false;
                sendKeybindPress(0, index);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static void sendKeybindPress(int action, int code) {
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(new Identifier("arcane:keybind"), new PacketByteBuf(Unpooled.buffer().writeInt(action).writeInt(code)));
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
