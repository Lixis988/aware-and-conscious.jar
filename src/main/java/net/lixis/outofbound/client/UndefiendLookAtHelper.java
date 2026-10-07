package net.lixis.outofbound.client;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class UndefiendLookAtHelper {

	private UndefiendLookAtHelper() {
	}

	public static double viewAngleDegrees(Player player, Entity entity) {
		Vec3 view = player.getViewVector(1.0F);
		Vec3 toEntity = entity.getEyePosition(1.0F).subtract(player.getEyePosition(1.0F));
		double length = toEntity.length();
		if (length < 1.0E-4D) {
			return 0.0D;
		}
		toEntity = toEntity.scale(1.0D / length);
		double dot = view.dot(toEntity);
		dot = Math.max(-1.0D, Math.min(1.0D, dot));
		return Math.toDegrees(Math.acos(dot));
	}
}
