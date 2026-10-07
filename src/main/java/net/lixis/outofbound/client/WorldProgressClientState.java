package net.lixis.outofbound.client;

import net.lixis.outofbound.client.glitch.GlobalWorldGlitchScheduler;
import net.lixis.outofbound.feature.corruption.render.MemoryCorruptionGate;
import net.lixis.outofbound.world.WorldGameStage;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class WorldProgressClientState {

	private static boolean boundedcowCollision;
	private static WorldGameStage gameStage = WorldGameStage.START;
	private static long sinkUntilTick;
	private static long mazeIndex;
	private static boolean undefiendMaze;
	private static long boundedcowCollisionTick;
	private static long boundedcowCollisionDayTime;
	private static long worldGameTime;
	private static long worldDayTime;

	private WorldProgressClientState() {
	}

	public static void apply(boolean collision, WorldGameStage stage, long sinkUntil, long gameTime, long dayTime,
			long maze, boolean undefiend, long collisionTick, long collisionDayTime) {
		boundedcowCollision = collision;
		gameStage = stage;
		sinkUntilTick = sinkUntil;
		mazeIndex = maze;
		undefiendMaze = undefiend;
		boundedcowCollisionTick = collisionTick;
		boundedcowCollisionDayTime = collisionDayTime;
		worldGameTime = gameTime;
		worldDayTime = dayTime;

		MemoryCorruptionGate.clearManualOverride();
		BoundedcowWindowShaker.reset();

		if (stage == WorldGameStage.SINKING) {
			GlobalWorldGlitchScheduler.onSinkingEnabled();
		} else {
			GlobalWorldGlitchScheduler.reset();
		}
	}

	public static boolean hasBoundedcowCollision() {
		return boundedcowCollision;
	}

	public static WorldGameStage getGameStage() {
		return gameStage;
	}

	public static boolean isSinking() {
		return gameStage == WorldGameStage.SINKING;
	}

	public static boolean isInMaze() {
		return gameStage == WorldGameStage.IN_MAZE;
	}

	public static long getMazeIndex() {
		return mazeIndex;
	}

	public static boolean isUndefiendMaze() {
		return undefiendMaze;
	}

	public static long getBoundedcowCollisionTick() {
		return boundedcowCollisionTick;
	}

	public static long getBoundedcowCollisionDayTime() {
		return boundedcowCollisionDayTime;
	}

	public static long getSyncedWorldGameTime() {
		return worldGameTime;
	}

	public static long getDaysSinceBoundedcowCollision() {
		return getDaysSinceBoundedcowCollision(Minecraft.getInstance());
	}

	public static long getDaysSinceBoundedcowCollision(Minecraft minecraft) {
		if (!boundedcowCollision) {
			return 0L;
		}
		long liveGame = getOverworldGameTime(minecraft);
		long liveDay = getOverworldDayTime(minecraft);

		long daysFromGame = 0L;
		if (boundedcowCollisionTick > 0L && liveGame >= boundedcowCollisionTick) {
			daysFromGame = (liveGame - boundedcowCollisionTick) / WorldInternalConfig.TICKS_PER_DAY;
		}

		long daysFromDay = 0L;
		if (boundedcowCollisionDayTime > 0L && liveDay >= boundedcowCollisionDayTime) {
			daysFromDay = (liveDay - boundedcowCollisionDayTime) / WorldInternalConfig.TICKS_PER_DAY;
		}

		return Math.max(daysFromGame, daysFromDay);
	}

	public static long getOverworldGameTime(Minecraft minecraft) {
		if (minecraft == null) {
			return worldGameTime;
		}
		var server = minecraft.getSingleplayerServer();
		if (server != null) {
			Level overworld = server.overworld();
			if (overworld != null) {
				return overworld.getGameTime();
			}
		}
		if (minecraft.level != null && minecraft.level.dimension() == Level.OVERWORLD) {
			return minecraft.level.getGameTime();
		}
		return worldGameTime;
	}

	public static long getOverworldDayTime(Minecraft minecraft) {
		if (minecraft == null) {
			return worldDayTime;
		}
		var server = minecraft.getSingleplayerServer();
		if (server != null) {
			Level overworld = server.overworld();
			if (overworld != null) {
				return overworld.getDayTime();
			}
		}
		if (minecraft.level != null && minecraft.level.dimension() == Level.OVERWORLD) {
			return minecraft.level.getDayTime();
		}
		return worldDayTime;
	}

	public static float getSinkProgress(long overworldGameTime) {
		if (gameStage != WorldGameStage.SINKING || sinkUntilTick <= 0L) {
			return 0.0F;
		}
		long start = sinkUntilTick - WorldInternalConfig.SINK_DURATION_TICKS;
		float progress = (overworldGameTime - start) / (float) WorldInternalConfig.SINK_DURATION_TICKS;
		return Math.max(0.0F, Math.min(1.0F, progress));
	}

	public static void clear() {
		boundedcowCollision = false;
		gameStage = WorldGameStage.START;
		sinkUntilTick = 0L;
		mazeIndex = 0L;
		undefiendMaze = false;
		boundedcowCollisionTick = 0L;
		boundedcowCollisionDayTime = 0L;
		worldGameTime = 0L;
		worldDayTime = 0L;
		MemoryCorruptionGate.clearManualOverride();
		BoundedcowWindowShaker.reset();
		GlobalWorldGlitchScheduler.reset();
	}
}
