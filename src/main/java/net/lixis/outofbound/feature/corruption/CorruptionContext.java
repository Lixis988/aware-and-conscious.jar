package net.lixis.outofbound.feature.corruption;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class CorruptionContext {

	private boolean enabled = true;
	private final Map<UUID, Float> playerLevels = new ConcurrentHashMap<>();
	private final Set<UUID> manualOverrides = ConcurrentHashMap.newKeySet();

	public boolean enabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public float getLevel(UUID playerId) {
		return playerLevels.getOrDefault(playerId, 0.0F);
	}

	public boolean isActiveFor(UUID playerId) {
		return enabled && getLevel(playerId) > 0.0F;
	}

	public boolean hasManualOverride(UUID playerId) {
		return manualOverrides.contains(playerId);
	}

	public void setManualOverride(UUID playerId, boolean manual) {
		if (manual) {
			manualOverrides.add(playerId);
		} else {
			manualOverrides.remove(playerId);
		}
	}

	public void clearManualOverride(UUID playerId) {
		manualOverrides.remove(playerId);
	}

	public void setLevel(UUID playerId, float level) {
		float clamped = Math.max(0.0F, Math.min(100.0F, level));
		if (clamped <= 0.0F) {
			playerLevels.remove(playerId);
		} else {
			playerLevels.put(playerId, clamped);
		}
	}

	public float toggle(UUID playerId) {
		float current = getLevel(playerId);
		float next = current > 0.0F ? 0.0F : 50.0F;
		setLevel(playerId, next);
		return next;
	}

	public void clear(UUID playerId) {
		playerLevels.remove(playerId);
		manualOverrides.remove(playerId);
	}

	public int actionCount(float level) {
		if (level <= 0.0F) {
			return 0;
		}
		return level >= 35.0F ? 2 : 1;
	}

	public int effectTtl(float level) {
		return 30 + (int) (level * 0.5F);
	}

	public Random random() {
		return ThreadLocalRandom.current();
	}
}
