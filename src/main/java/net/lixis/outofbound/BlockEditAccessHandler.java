package net.lixis.outofbound;

import net.lixis.outofbound.dimension.MazeDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.lixis.outofbound.mixin.ServerPlayerGameModeAccessor;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class BlockEditAccessHandler {

	private BlockEditAccessHandler() {
	}

	private static boolean isMaze(LevelAccessor levelAccessor) {
		if (!(levelAccessor instanceof Level level)) {
			return false;
		}
		return MazeDimensions.isMazeDimension(level.dimension().location());
	}

	private static void syncGameModeLevel(ServerPlayer player) {
		player.gameMode.setLevel(player.serverLevel());
	}

	private static void prepareSurvivalMiner(ServerPlayer player) {
		syncGameModeLevel(player);
		if (!isMaze(player.level())) {
			return;
		}
		if (!player.getAbilities().mayBuild) {
			player.getAbilities().mayBuild = true;
			player.onUpdateAbilities();
		}
	}

	@SubscribeEvent
	public static void onServerTickStart(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.START) {
			return;
		}
		for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
			if (player.isSpectator() || player.isCreative()) {
				continue;
			}
			if (isMaze(player.level())) {
				prepareSurvivalMiner(player);
			} else {
				syncGameModeLevel(player);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			syncGameModeLevel(player);
		}
	}

	@SubscribeEvent
	public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			prepareSurvivalMiner(player);
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (!(event.player instanceof ServerPlayer player)) {
			return;
		}
		if (player.isSpectator() || player.isCreative()) {
			return;
		}

		if (event.phase == TickEvent.Phase.START) {
			prepareSurvivalMiner(player);
		} else if (isMaze(player.level())) {
			advanceMazeMining(player);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		Player player = event.getEntity();
		if (player == null || player.isSpectator() || player.isCreative()) {
			return;
		}
		if (!isMaze(player.level())) {
			return;
		}
		if (player instanceof ServerPlayer serverPlayer) {
			prepareSurvivalMiner(serverPlayer);
		}
		event.setCanceled(false);
		event.setUseBlock(Event.Result.ALLOW);
	}

	@SubscribeEvent
	public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		Player player = event.getEntity();
		if (player == null || player.isCreative() || !isMaze(player.level())) {
			return;
		}
		float speed = event.getNewSpeed();
		if (speed <= 0.0F) {
			event.setNewSpeed(1.0F);
		} else {
			event.setNewSpeed(speed * 6.0F);
		}
	}

	@SubscribeEvent
	public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
		Player player = event.getEntity();
		if (player == null || player.isCreative() || !isMaze(player.level())) {
			return;
		}
		event.setCanHarvest(true);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		if (!isMaze(event.getLevel())) {
			return;
		}
		if (event.isCanceled()) {
			event.setCanceled(false);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
		if (!isMaze(event.getLevel())) {
			return;
		}
		if (event.isCanceled()) {
			event.setCanceled(false);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onItemPickup(EntityItemPickupEvent event) {
		Player player = event.getEntity();
		if (player == null || !isMaze(player.level())) {
			return;
		}
		if (event.getResult() == Event.Result.DENY) {
			event.setResult(Event.Result.ALLOW);
		}
	}

	private static void advanceMazeMining(ServerPlayer player) {
		syncGameModeLevel(player);

		ServerPlayerGameMode gameMode = player.gameMode;
		ServerPlayerGameModeAccessor mining = (ServerPlayerGameModeAccessor) gameMode;
		if (!mining.outofbound$isDestroyingBlock()) {
			return;
		}

		BlockPos pos = mining.outofbound$getDestroyPos();
		if (pos == null || pos.equals(BlockPos.ZERO)) {
			return;
		}
		if (!player.canReach(pos, 1.5D)) {
			return;
		}
		if (player.level().getBlockState(pos).isAir()) {
			return;
		}

		gameMode.destroyBlock(pos);
	}
}
