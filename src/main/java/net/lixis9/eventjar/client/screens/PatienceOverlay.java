
package net.lixis9.eventjar.client.screens;

import org.checkerframework.checker.units.qual.h;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

import net.lixis9.eventjar.procedures.Risperidone9Procedure;
import net.lixis9.eventjar.procedures.Risperidone8Procedure;
import net.lixis9.eventjar.procedures.Risperidone7Procedure;
import net.lixis9.eventjar.procedures.Risperidone6Procedure;
import net.lixis9.eventjar.procedures.Risperidone5Procedure;
import net.lixis9.eventjar.procedures.Risperidone4Procedure;
import net.lixis9.eventjar.procedures.Risperidone3Procedure;
import net.lixis9.eventjar.procedures.Risperidone2Procedure;
import net.lixis9.eventjar.procedures.Risperidone10Procedure;
import net.lixis9.eventjar.procedures.Rasperidone1Procedure;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class PatienceOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getWindow().getGuiScaledWidth();
		int h = event.getWindow().getGuiScaledHeight();
		Level world = null;
		double x = 0;
		double y = 0;
		double z = 0;
		Player entity = Minecraft.getInstance().player;
		if (entity != null) {
			world = entity.level();
			x = entity.getX();
			y = entity.getY();
			z = entity.getZ();
		}
		if (true) {
			event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.eventjar.patience.label_patience"), 4, 3, -1, false);
			if (Rasperidone1Procedure.execute(entity))
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.eventjar.patience.label_10"), 48, 3, -1, false);
			if (Risperidone2Procedure.execute(entity))
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.eventjar.patience.label_9"), 60, 3, -1, false);
			if (Risperidone3Procedure.execute(entity))
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.eventjar.patience.label_8"), 67, 3, -1, false);
			if (Risperidone4Procedure.execute(world, x, y, z, entity))
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.eventjar.patience.label_7"), 73, 3, -1, false);
			if (Risperidone5Procedure.execute(entity))
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.eventjar.patience.label_6"), 80, 3, -1, false);
			if (Risperidone6Procedure.execute(entity))
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.eventjar.patience.label_5"), 87, 3, -1, false);
			if (Risperidone7Procedure.execute(entity))
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.eventjar.patience.label_4"), 93, 3, -1, false);
			if (Risperidone8Procedure.execute(entity))
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.eventjar.patience.label_3"), 100, 3, -1, false);
			if (Risperidone9Procedure.execute(entity))
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.eventjar.patience.label_2"), 107, 3, -1, false);
			if (Risperidone10Procedure.execute(entity))
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.eventjar.patience.label_1"), 114, 3, -1, false);
		}
	}
}
