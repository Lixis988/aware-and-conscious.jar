package net.lixis.outofbound.entity;

import net.lixis.outofbound.registry.OutofboundExtraEntities;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.network.PlayMessages;

public class Entity1Entity extends AbstractChaserEntity {

	public Entity1Entity(PlayMessages.SpawnEntity packet, Level world) {
		this(OutofboundExtraEntities.ENTITY1.get(), world);
	}

	public Entity1Entity(EntityType<Entity1Entity> type, Level world) {
		super(type, world);
	}

	public static void init() {
		SpawnPlacements.register(OutofboundExtraEntities.ENTITY1.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				(entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(world, pos, random) && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return createChaserAttributes();
	}
}
