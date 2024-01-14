package xyz.apollo30.arcane.mixin;


import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
    @Unique
    private static final Text SAVING_LEVEL_TEXT = Text.translatable("menu.savingLevel");

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "disconnect", at = @At("HEAD"), cancellable = true)
    private void onDisconnect(CallbackInfo ci) {
        if (this.client == null || this.client.world == null) return;

        boolean bl = this.client.isInSingleplayer();
        this.client.world.disconnect();
        if (bl) {
            this.client.disconnect(new MessageScreen(SAVING_LEVEL_TEXT));
        } else {
            this.client.disconnect();
        }

        TitleScreen titleScreen = new TitleScreen();
        this.client.setScreen(titleScreen);

        if(ci.isCancellable()) ci.cancel();
    }

}

