package net.lixis.outofbound.client;

import net.lixis.outofbound.OutofboundMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID, value = Dist.CLIENT)
public final class SeraphFinaleClientHandler {

	private static boolean active;
	private static int targetEntityId;

	private SeraphFinaleClientHandler() {
	}

	public static void begin(int entityId) {
		active = true;
		targetEntityId = entityId;
	}

	@SubscribeEvent
	public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
		if (!active) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null || minecraft.level == null) {
			return;
		}

		Entity target = minecraft.level.getEntity(targetEntityId);
		if (target == null) {
			return;
		}

		Vec3 eyePos = minecraft.player.getEyePosition((float) event.getPartialTick());
		Vec3 targetPos = target.getBoundingBox().getCenter();
		double dx = targetPos.x - eyePos.x;
		double dy = targetPos.y - eyePos.y;
		double dz = targetPos.z - eyePos.z;
		double horiz = Math.sqrt(dx * dx + dz * dz);
		float yaw = (float) (Mth.RAD_TO_DEG * Math.atan2(dx, dz));
		float pitch = (float) (Mth.RAD_TO_DEG * Math.atan2(-dy, horiz));

		event.setYaw(yaw);
		event.setPitch(pitch);
	}

	@SubscribeEvent
	public static void onMovementInput(MovementInputUpdateEvent event) {
		if (!active) {
			return;
		}

		Input input = event.getInput();
		input.up = false;
		input.down = false;
		input.left = false;
		input.right = false;
		input.jumping = false;
		input.shiftKeyDown = false;
	}
}
