package xyz.apollo30.arcane.client;

import net.fabricmc.api.ClientModInitializer;

public class ArcaneClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        System.out.println("ArcaneMod v1.0.0 has successfully injected into the Client!");
    }

}
