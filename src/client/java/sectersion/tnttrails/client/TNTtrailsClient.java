package sectersion.tnttrails.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;

public class TNTtrailsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		TNTConfig.load();
		ClientTickEvents.END_CLIENT_TICK.register(client -> TNTTracker.getInstance().pruneOldEntries());
		WorldRenderEvents.AFTER_ENTITIES.register(context -> TNTRenderer.render(context));
		ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
			if (screen instanceof OptionsScreen && Screens.getButtons(screen).stream()
					.noneMatch(widget -> widget instanceof Button button && button.getMessage().getString().equals("TNT Trails"))) {
				Screens.getButtons(screen).add(Button.builder(Component.literal("TNT Trails"),
						button -> client.setScreenAndShow(new TNTConfigScreen(screen)))
						.bounds(width / 2 - 100, 55, 200, 20).build());
			}
		});
	}
}
