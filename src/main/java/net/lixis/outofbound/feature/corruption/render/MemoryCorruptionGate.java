package net.lixis.outofbound.feature.corruption.render;

import net.lixis9.eventjar.DarknessConfig;
import net.lixis.outofbound.client.WorldProgressClientState;
import net.lixis.outofbound.feature.corruption.util.SafeCorruptor;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.concurrent.ThreadLocalRandom;

public final class MemoryCorruptionGate {

	public enum CorruptionType {
		UV,
		VERTEX,
		ATLAS
	}

	private static final float VERTEX_HIT_CHANCE = 0.25F;
	private static final float UV_WEIGHT = 0.70F;
	private static final float VERTEX_WEIGHT = 0.20F;
	private static final long MEMORY_CORRUPTION_DAY = 5L;

	private static volatile boolean manualOverride;

	private static final ThreadLocal<CorruptionType> CURRENT_VERTEX_ROLL = new ThreadLocal<>();

	private MemoryCorruptionGate() {
	}

	public static boolean isEnabled() {
		return isEffectivelyEnabled();
	}

	public static boolean isEffectivelyEnabled() {
		if (!DarknessConfig.ENABLE_MEMORY_CORRUPTION) {
			return false;
		}
		if (manualOverride) {
			return hasActiveWorldCollision();
		}
		return isAutoEnabledFromWorld();
	}

	public static void clearManualOverride() {
		manualOverride = false;
	}

	public static boolean toggleManualOverride() {
		manualOverride = !manualOverride;
		return isEffectivelyEnabled();
	}

	public static boolean isManualOverride() {
		return manualOverride;
	}

	private static boolean hasActiveWorldCollision() {
		if (FMLEnvironment.dist != Dist.CLIENT) {
			return false;
		}
		return WorldProgressClientState.hasBoundedcowCollision();
	}

	private static boolean isAutoEnabledFromWorld() {
		if (FMLEnvironment.dist != Dist.CLIENT) {
			return false;
		}
		if (!WorldProgressClientState.hasBoundedcowCollision()) {
			return false;
		}
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft == null || minecraft.level == null) {
			return false;
		}
		return WorldProgressClientState.getDaysSinceBoundedcowCollision(minecraft) >= MEMORY_CORRUPTION_DAY;
	}

	public static boolean shouldCorruptThisVertex() {
		return isEffectivelyEnabled() && ThreadLocalRandom.current().nextFloat() < VERTEX_HIT_CHANCE;
	}

	public static void clearVertexRoll() {
		CURRENT_VERTEX_ROLL.remove();
	}

	public static boolean shouldCorruptAtlas() {
		if (!isEffectivelyEnabled()) {
			return false;
		}
		if (ThreadLocalRandom.current().nextFloat() >= VERTEX_HIT_CHANCE) {
			return false;
		}
		return rollType() == CorruptionType.ATLAS;
	}

	public static boolean shouldCorruptMatrix() {
		if (!isEffectivelyEnabled()) {
			return false;
		}
		if (ThreadLocalRandom.current().nextFloat() >= VERTEX_HIT_CHANCE) {
			return false;
		}
		return rollType() == CorruptionType.VERTEX;
	}

	public static CorruptionType rollType() {
		float roll = ThreadLocalRandom.current().nextFloat();
		if (roll < UV_WEIGHT) {
			return CorruptionType.UV;
		}
		if (roll < UV_WEIGHT + VERTEX_WEIGHT) {
			return CorruptionType.VERTEX;
		}
		return CorruptionType.ATLAS;
	}

	public static float maybeCorruptUv(float value) {
		CorruptionType type = getOrRollType();
		if (type != CorruptionType.UV) {
			return value;
		}
		return corruptUv(value);
	}

	public static float maybeCorruptVertex(float value) {
		CorruptionType type = getOrRollType();
		if (type != CorruptionType.VERTEX) {
			return value;
		}
		return corruptVertexCoord(value);
	}

	public static CorruptionType getOrRollType() {
		if (!isEffectivelyEnabled()) {
			return null;
		}
		CorruptionType existing = CURRENT_VERTEX_ROLL.get();
		if (existing != null) {
			return existing;
		}
		if (!shouldCorruptThisVertex()) {
			return null;
		}
		CorruptionType rolled = rollType();
		CURRENT_VERTEX_ROLL.set(rolled);
		return rolled;
	}

	public static float corruptUv(float uv) {
		int bits = Float.floatToIntBits(uv);
		bits ^= 1 << ThreadLocalRandom.current().nextInt(8);
		return SafeCorruptor.safeFloat(Float.intBitsToFloat(bits));
	}

	public static float corruptVertexCoord(float coord) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		if (random.nextBoolean()) {
			int bits = Float.floatToIntBits(coord);
			bits ^= 1 << random.nextInt(8);
			return SafeCorruptor.safeFloat(Float.intBitsToFloat(bits));
		}
		return SafeCorruptor.safeFloat(coord + random.nextFloat() * 0.5F);
	}

	public static float corruptMatrixScale(float scale) {
		int bits = Float.floatToIntBits(scale);
		bits ^= 1 << ThreadLocalRandom.current().nextInt(8);
		float corrupted = SafeCorruptor.safeFloat(Float.intBitsToFloat(bits));
		float abs = Math.abs(corrupted);
		if (abs < 0.05F) {
			abs = 0.05F;
		}
		return Math.max(0.05F, Math.min(3.0F, abs));
	}

	public static float corruptAtlasCoord(float coord) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		float offset = (random.nextInt(16) + 1) / 256.0F;
		if (random.nextBoolean()) {
			offset = -offset;
		}
		return SafeCorruptor.safeFloat(coord + offset);
	}
}
