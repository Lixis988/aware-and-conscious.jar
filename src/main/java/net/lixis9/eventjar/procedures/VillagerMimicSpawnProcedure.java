package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.EventjarMod;
import net.lixis9.eventjar.entity.VillagerMimicEntity;
import net.lixis9.eventjar.init.EventjarModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EventjarMod.MODID)
public class VillagerMimicSpawnProcedure {

	private static final String CHECKED_TAG = "eventjar_mimic_checked";

	private static final float CHANCE = 0.18F;
	private static final double NEARBY_MIMIC_RANGE = 48.0D;

	@SubscribeEvent
	public static void onEntityJoin(EntityJoinLevelEvent event) {
		Level level = event.getLevel();
		if (level.isClientSide()) {
			return;
		}
		Entity entity = event.getEntity();
		if (!(entity instanceof Villager villager) || entity instanceof VillagerMimicEntity) {
			return;
		}
		if (villager.getPersistentData().getBoolean(CHECKED_TAG)) {
			return;
		}
		villager.getPersistentData().putBoolean(CHECKED_TAG, true);

		if (!(level instanceof ServerLevel serverLevel) || level.dimension() != Level.OVERWORLD) {
			return;
		}
		if (!isInVillage(serverLevel, villager.blockPosition())) {
			return;
		}
		if (villager.getRandom().nextFloat() > CHANCE) {
			return;
		}

		EventjarMod.queueServerWork(2, () -> {
			if (!villager.isAlive() || villager.isRemoved()) {
				return;
			}
			if (!isInVillage(serverLevel, villager.blockPosition())) {
				return;
			}
			AABB box = villager.getBoundingBox().inflate(NEARBY_MIMIC_RANGE);
			if (!serverLevel.getEntitiesOfClass(VillagerMimicEntity.class, box).isEmpty()) {
				return;
			}
			VillagerMimicEntity mimic = EventjarModEntities.VILLAGER_MIMIC.get().create(serverLevel);
			if (mimic == null) {
				return;
			}
			mimic.moveTo(villager.getX(), villager.getY(), villager.getZ(), villager.getYRot(), villager.getXRot());
			mimic.setYHeadRot(villager.getYHeadRot());
			serverLevel.addFreshEntity(mimic);
			villager.discard();
		});
	}

	private static boolean isInVillage(ServerLevel level, BlockPos pos) {
		return level.structureManager().getStructureWithPieceAt(pos, StructureTags.VILLAGE).isValid();
	}
}
