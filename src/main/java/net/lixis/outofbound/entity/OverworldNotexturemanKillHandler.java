package net.lixis.outofbound.entity;

import net.lixis.outofbound.EvilNotexturemanOfferPacket;
import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.registry.OutofboundExtraEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class OverworldNotexturemanKillHandler {

	private static final double SPAWN_CHANCE = 0.10D;
	private static final double SPAWN_DISTANCE = 4.0D;

	private OverworldNotexturemanKillHandler() {
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		if (event.getEntity().level().isClientSide) {
			return;
		}

		LivingEntity victim = event.getEntity();
		DamageSource source = event.getSource();
		if (!(source.getEntity() instanceof ServerPlayer killer)) {
			return;
		}
		if (killer.level().dimension() != Level.OVERWORLD) {
			return;
		}
		if (victim instanceof ServerPlayer) {
			return;
		}
		if (ThreadLocalRandom.current().nextDouble() >= SPAWN_CHANCE) {
			return;
		}

		ServerLevel level = killer.serverLevel();
		if (hasManagedNearby(level, killer)) {
			return;
		}

		Vec3 spawnPos = computeSpawnInFront(level, killer);
		if (spawnPos == null) {
			return;
		}

		OverworldNotexturemanEntity entity = OutofboundExtraEntities.OVERWORLD_NOTEXTUREMAN.get().create(level);
		if (entity == null) {
			return;
		}

		entity.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, killer.getYRot() + 180.0F, 0.0F);
		entity.getPersistentData().putBoolean(OverworldNotexturemanEntity.MANAGED_TAG, true);
		entity.setTargetPlayer(killer.getUUID());
		level.addFreshEntity(entity);

		OutofboundMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> killer),
				new EvilNotexturemanOfferPacket(entity.getId()));
	}

	private static boolean hasManagedNearby(ServerLevel level, ServerPlayer player) {
		AABB box = player.getBoundingBox().inflate(64.0D);
		return !level.getEntitiesOfClass(OverworldNotexturemanEntity.class, box,
				mob -> mob.getPersistentData().getBoolean(OverworldNotexturemanEntity.MANAGED_TAG) && !mob.isRemoved()).isEmpty();
	}

	private static Vec3 computeSpawnInFront(ServerLevel level, ServerPlayer player) {
		float yawRad = player.getYRot() * ((float) Math.PI / 180.0F);
		double aheadX = player.getX() - Math.sin(yawRad) * SPAWN_DISTANCE;
		double aheadZ = player.getZ() + Math.cos(yawRad) * SPAWN_DISTANCE;
		int blockX = Mth.floor(aheadX);
		int blockZ = Mth.floor(aheadZ);
		int feetY = player.getBlockY();

		if (!level.hasChunk(blockX >> 4, blockZ >> 4)) {
			return null;
		}

		BlockPos ground = new BlockPos(blockX, feetY - 1, blockZ);
		BlockPos feet = new BlockPos(blockX, feetY, blockZ);
		BlockPos head = feet.above();
		if (!isValidSpawnSpace(level, ground, feet, head)) {
			return new Vec3(aheadX, player.getY(), aheadZ);
		}
		return new Vec3(feet.getX() + 0.5D, feet.getY(), feet.getZ() + 0.5D);
	}

	private static boolean isValidSpawnSpace(ServerLevel level, BlockPos ground, BlockPos feet, BlockPos head) {
		BlockState floor = level.getBlockState(ground);
		if (!floor.isSolidRender(level, ground)) {
			return false;
		}
		return level.getBlockState(feet).isAir() && level.getBlockState(head).isAir();
	}
}
