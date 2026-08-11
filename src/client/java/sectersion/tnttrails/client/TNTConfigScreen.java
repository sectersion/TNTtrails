package sectersion.tnttrails.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TNTConfigScreen extends Screen {
    private final Screen parent;

    public TNTConfigScreen(Screen parent) {
        super(Component.literal("TNT Trails"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int left = width / 2 - 100;
        addRenderableWidget(Button.builder(lifetimeText(), button -> {
            TNTConfig.cycleLifetime();
            button.setMessage(lifetimeText());
        }).bounds(left, height / 2 - 45, 200, 20).build());
        addRenderableWidget(Button.builder(startText(), button -> {
            TNTConfig.cycleStartColor();
            button.setMessage(startText());
        }).bounds(left, height / 2 - 15, 200, 20).build());
        addRenderableWidget(Button.builder(endText(), button -> {
            TNTConfig.cycleEndColor();
            button.setMessage(endText());
        }).bounds(left, height / 2 + 15, 200, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
                .bounds(left, height / 2 + 55, 200, 20).build());
    }

    private static Component lifetimeText() { return Component.literal("Trail time: " + TNTConfig.lifetimeSeconds() + "s"); }
    private static Component startText() { return Component.literal("Trail start: " + TNTConfig.startColorName()); }
    private static Component endText() { return Component.literal("Trail end: " + TNTConfig.endColorName()); }

    @Override
    public void onClose() { minecraft.setScreenAndShow(parent); }
}
