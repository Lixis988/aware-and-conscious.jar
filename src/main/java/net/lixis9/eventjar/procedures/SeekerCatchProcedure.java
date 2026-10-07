package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.EventjarMod;
import net.lixis9.eventjar.network.SeekerCatchShakePacket;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class SeekerCatchProcedure {

	private static final String CATCHING_TAG = "seeker_catching";
	private static final int HOLD_TICKS = 28;

	private SeekerCatchProcedure() {
	}

	public static void execute(Entity seeker, Player player) {
		if (seeker == null || player == null || player.level().isClientSide) {
			return;
		}
		if (player.getPersistentData().getBoolean(CATCHING_TAG)) {
			return;
		}
		if (!player.isAlive()) {
			return;
		}

		player.getPersistentData().putBoolean(CATCHING_TAG, true);

		if (player instanceof ServerPlayer serverPlayer) {
			serverPlayer.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket(5, 40, 10));
			serverPlayer.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket(
					net.minecraft.network.chat.Component.literal("§cLOSS")));
			SeekerCatchShakePacket.send(serverPlayer, true);
		}

		for (int tick = 0; tick <= HOLD_TICKS; tick++) {
			final int t = tick;
			EventjarMod.queueServerWork(t, () -> {
				if (!player.isAlive() || seeker.isRemoved()) {
					return;
				}
				Vec3 eyes = seeker instanceof LivingEntity living
						? living.getEyePosition()
						: seeker.position().add(0.0D, 1.5D, 0.0D);
				player.lookAt(EntityAnchorArgument.Anchor.EYES, eyes);
				player.setDeltaMovement(Vec3.ZERO);
				player.hurtMarked = true;
				if (player instanceof ServerPlayer sp) {
					sp.connection.resetPosition();
				}
			});
		}

		EventjarMod.queueServerWork(HOLD_TICKS + 2, () -> {
			player.getPersistentData().remove(CATCHING_TAG);
			if (player instanceof ServerPlayer serverPlayer) {
				SeekerCatchShakePacket.send(serverPlayer, false);
			}
			if (!player.isAlive() || seeker.isRemoved()) {
				if (!seeker.isRemoved()) {
					seeker.discard();
				}
				return;
			}
			DamageSource source = player.damageSources().mobAttack((LivingEntity) seeker);
			player.hurt(source, Float.MAX_VALUE);
			if (player.isAlive()) {
				player.kill();
			}
			if (!seeker.isRemoved()) {
				seeker.discard();
			}
		});
	}
}
