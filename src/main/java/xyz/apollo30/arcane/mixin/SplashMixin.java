package xyz.apollo30.arcane.mixin;

import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(TitleScreen.class)
public abstract class SplashMixin extends Screen {

    @Unique
    private ServerInfo selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName"), "", false);
    @Unique
    private static final String ADDRESS = "23.139.82.220:23040";
    @Shadow
    @Nullable
    private SplashTextRenderer splashText;
    @Unique
    private static final Text COPYRIGHT = Text.literal("Copyright Mojang AB. Do not distribute!");

    protected SplashMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("HEAD"), method = "init()V", cancellable = true)
    private void init(CallbackInfo info) {
        if (this.client == null) return;

        this.splashText = new SplashTextRenderer("Loaded ArcaneMod v1.0.0");

        int i = this.textRenderer.getWidth(COPYRIGHT);
        int j = this.width - i - 2;
        int l = this.height / 2;

        // Multiplayer
        this.addDrawableChild(ButtonWidget.builder(Text.of("Connect to Arcane"), (button) -> {
            if (Objects.equals(this.client.getSession().getUsername(), "Apollo30")) {
                this.client.setScreen(new DirectConnectScreen(this, this::directConnect, this.selectedEntry));
            } else {
                ConnectScreen.connect(this, this.client, ServerAddress.parse(ADDRESS), new ServerInfo("Arcane", ADDRESS, false), false);
            }
        }).dimensions(this.width / 2 - 100, l, 200, 20).build());

        // Essentials
        this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 124, l + 25, 20, 20, 0, 106, 20, ButtonWidget.WIDGETS_TEXTURE, 256, 256, (button) -> {
            this.client.setScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager()));
        }, Text.translatable("narrator.button.language")));
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.options"), (button) -> {
            this.client.setScreen(new OptionsScreen(this, this.client.options));
        }).dimensions(this.width / 2 - 100, l + 25, 98, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.quit"), (button) -> {
            this.client.scheduleStop();
        }).dimensions(this.width / 2 + 2, l + 25, 98, 20).build());
        this.addDrawableChild(new TexturedButtonWidget(this.width / 2 + 104, l + 25, 20, 20, 0, 0, 20, ButtonWidget.ACCESSIBILITY_TEXTURE, 32, 64, (button) -> {
            this.client.setScreen(new AccessibilityOptionsScreen(this, this.client.options));
        }, Text.translatable("narrator.button.accessibility")));
        this.addDrawableChild(new PressableTextWidget(j, this.height - 10, i, 10, COPYRIGHT, (button) -> {
            this.client.setScreen(new CreditsAndAttributionScreen(this));
        }, this.textRenderer));

        this.client.setConnectedToRealms(false);

        if (info.isCancellable()) info.cancel();

    }

    @Unique
    private void directConnect(boolean confirmedAction) {
        if (this.client == null) return;

        if (confirmedAction) {
            this.connect(this.selectedEntry);
        } else {
            this.client.setScreen(this);
        }

    }

    @Unique
    private void connect(ServerInfo entry) {
        if (this.client == null) return;
        ConnectScreen.connect(this, this.client, ServerAddress.parse(entry.address), entry, false);
    }

    @Inject(method = "initWidgetsNormal", at = @At("HEAD"), cancellable = true)
    private void initWidgetsNormal(int y, int spacingY, CallbackInfo ci) {
        if (ci.isCancellable()) ci.cancel();
    }
}