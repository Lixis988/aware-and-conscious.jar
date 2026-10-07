package net.lixis.outofbound.feature.corruption.util;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.feature.corruption.corruptors.MobSwapCorruptor;
import net.lixis9.eventjar.EventjarMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class CorruptionEntityPools {

	public static final long MOD_MOB_POOL_DAY = 20L;

	private static final Set<ResourceLocation> COMPLEX_BOSSES = Set.of(
			new ResourceLocation("minecraft", "ender_dragon"),
			new ResourceLocation("minecraft", "wither"),
			new ResourceLocation("minecraft", "elder_guardian"),
			new ResourceLocation("minecraft", "warden")
	);

	private static List<EntityType<?>> baseSwapPool;
	private static List<EntityType<?>> modInternalPool;
	private static List<EntityType<?>> flashPool;

	private CorruptionEntityPools() {
	}

	public static boolean isModInternal(EntityType<?> type) {
		ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(type);
		return id != null && OutofboundMod.MODID.equals(id.getNamespace());
	}

	public static boolean isEventjar(EntityType<?> type) {
		ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(type);
		return id != null && EventjarMod.MODID.equals(id.getNamespace());
	}

	public static boolean isProtectedHelper(EntityType<?> type) {
		return isEventjar(type) || isModInternal(type);
	}

	public static boolean isComplexBoss(EntityType<?> type) {
		ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(type);
		return id != null && COMPLEX_BOSSES.contains(id);
	}

	public static boolean isSwapTarget(LivingEntity entity) {
		if (entity.getPersistentData().getBoolean("outofbound_corruption_flash")) {
			return false;
		}
		if (entity.getPersistentData().getBoolean(MobSwapCorruptor.SWAP_TAG)) {
			return false;
		}
		EntityType<?> type = entity.getType();
		if (type == EntityType.PLAYER || isComplexBoss(type) || isModInternal(type) || isEventjar(type)) {
			return false;
		}
		return entity.isAlive() && !entity.isRemoved() && type.canSerialize();
	}

	@Deprecated
	public static boolean isSwapCandidate(LivingEntity entity) {
		return isSwapTarget(entity);
	}

	@Nullable
	public static EntityType<?> pickSwapType(EntityType<?> avoid, long daysSince, ThreadLocalRandom random) {
		List<EntityType<?>> pool = buildSwapPool(daysSince);
		if (pool.isEmpty()) {
			return null;
		}
		for (int attempt = 0; attempt < 8; attempt++) {
			EntityType<?> picked = pool.get(random.nextInt(pool.size()));
			if (picked != avoid && !isEventjar(picked) && !isModInternal(picked)) {
				return picked;
			}
		}
		EntityType<?> fallback = pool.get(random.nextInt(pool.size()));
		return (isEventjar(fallback) || isModInternal(fallback)) ? null : fallback;
	}

	@Nullable
	public static EntityType<?> pickSimpleSwapType(EntityType<?> avoid, ThreadLocalRandom random) {
		return pickSwapType(avoid, 0L, random);
	}

	@Nullable
	public static EntityType<?> pickFlashType(ThreadLocalRandom random) {
		List<EntityType<?>> pool = getFlashPool();
		if (pool.isEmpty()) {
			return null;
		}
		for (int attempt = 0; attempt < 8; attempt++) {
			EntityType<?> picked = pool.get(random.nextInt(pool.size()));
			if (!isEventjar(picked)) {
				return picked;
			}
		}
		return null;
	}

	private static List<EntityType<?>> buildSwapPool(long daysSince) {
		List<EntityType<?>> pool = new ArrayList<>(getBaseSwapPool());
		if (daysSince >= MOD_MOB_POOL_DAY) {
			pool.addAll(getModInternalPool());
		}
		return pool;
	}

	private static boolean isReplacementCandidate(EntityType<?> type) {
		if (type == EntityType.PLAYER || !type.canSerialize() || isComplexBoss(type)
				|| isModInternal(type) || isEventjar(type)) {
			return false;
		}
		MobCategory category = type.getCategory();
		return category == MobCategory.MONSTER
				|| category == MobCategory.CREATURE
				|| category == MobCategory.AMBIENT
				|| category == MobCategory.WATER_CREATURE
				|| category == MobCategory.UNDERGROUND_WATER_CREATURE
				|| category == MobCategory.WATER_AMBIENT;
	}

	private static List<EntityType<?>> getBaseSwapPool() {
		if (baseSwapPool != null) {
			return baseSwapPool;
		}
		List<EntityType<?>> pool = new ArrayList<>();
		for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES.getValues()) {
			if (isReplacementCandidate(type)) {
				pool.add(type);
			}
		}
		baseSwapPool = pool;
		return pool;
	}

	private static List<EntityType<?>> getModInternalPool() {
		if (modInternalPool != null) {
			return modInternalPool;
		}

		modInternalPool = List.of();
		return modInternalPool;
	}

	private static List<EntityType<?>> getFlashPool() {
		if (flashPool != null) {
			return flashPool;
		}
		List<EntityType<?>> pool = new ArrayList<>(getBaseSwapPool());
		for (ResourceLocation bossId : COMPLEX_BOSSES) {
			EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(bossId);
			if (type != null && !pool.contains(type)) {
				pool.add(type);
			}
		}
		flashPool = pool;
		return flashPool;
	}
}
