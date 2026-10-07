package net.lixis9.eventjar;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.MenuProvider;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.lixis9.eventjar.init.EventjarModEntities;
import net.lixis9.eventjar.network.ClientOsEffectPacket;
import net.lixis9.eventjar.world.inventory.Test1Menu;
import net.lixis9.eventjar.world.inventory.Test2Menu;
import net.lixis9.eventjar.world.inventory.Test3Menu;
import net.lixis9.eventjar.world.inventory.Test4Menu;
import net.lixis9.eventjar.world.inventory.Test5Menu;
import net.lixis9.eventjar.world.inventory.Test6Menu;
import net.lixis9.eventjar.world.inventory.Test7Menu;
import net.lixis9.eventjar.world.inventory.Test8Menu;
import net.lixis9.eventjar.world.inventory.Test9Menu;
import net.lixis9.eventjar.world.inventory.Test10Menu;
import net.lixis9.eventjar.world.inventory.Test11Menu;
import net.lixis9.eventjar.world.inventory.Test12Menu;
import net.lixis9.eventjar.world.inventory.Test13Menu;
import net.lixis9.eventjar.world.inventory.Test14Menu;
import net.lixis9.eventjar.world.inventory.Test15Menu;
import net.lixis9.eventjar.world.inventory.Test16Menu;
import net.lixis9.eventjar.world.inventory.Test17Menu;
import net.lixis9.eventjar.world.inventory.Test18Menu;
import net.lixis9.eventjar.world.inventory.Test19Menu;
import net.lixis9.eventjar.world.inventory.Test20Menu;
import io.netty.buffer.Unpooled;

public class DayProcedureHandler {

	@SuppressWarnings("unused")
	private static final String OWNERSHIP = Authorship.NOTICE;

	private static final String TIMELINE_KEY = "daily_tests_start";

	private static final int[] DAY_DELAYS = {
			0,
			12000, 35000, 50000, 96000, 130000,
			145000, 170000, 200000, 230000, 246000,
			265000, 290000, 312000, 336000, 360000,
			380000, 400000, 424000, 448000, 470000
	};

	public static void executeDayProcedure(LevelAccessor world, double x, double y, double z, Entity entity, int day) {
		if (entity == null || !(entity instanceof ServerPlayer player) || day < 1 || day > 20) {
			return;
		}
		if (!(world instanceof ServerLevel level)) {
			return;
		}

		ensureTimelineStarted(level);

		if (isDayExecuted(world, day)) {
			return;
		}

		long start = getTimelineStart(world);
		long fireAt = start + DAY_DELAYS[day];
		long now = level.getGameTime();
		int delay = (int) Math.max(0L, Math.min(Integer.MAX_VALUE, fireAt - now));
		java.util.UUID playerId = player.getUUID();

		EventjarMod.queueServerWork(delay, () -> {

			ServerPlayer online = level.getServer().getPlayerList().getPlayer(playerId);
			if (online == null || !online.isAlive()) {
				return;
			}
			if (isDayExecuted(online.level(), day)) {
				return;
			}

			BlockPos pos = online.blockPosition();
			runDaySideEffects(online.level(), online, pos, day);
			openTestGui(online, day, pos);
			markDayExecuted(online.level(), day);
			setWorldDay(online.level(), day);
		});
	}

	private static void ensureTimelineStarted(ServerLevel level) {
		WorldVariables variables = WorldVariables.get(level);
		if (variables.hasCustomVariable(TIMELINE_KEY)) {
			return;
		}

		for (int i = 1; i <= 20; i++) {
			variables.removeCustomVariable("day_" + i + "_executed");
		}
		variables.setCustomVariable(TIMELINE_KEY, level.getGameTime());
	}

	private static long getTimelineStart(LevelAccessor world) {
		WorldVariables variables = WorldVariables.get(world);
		Object value = variables.getCustomVariable(TIMELINE_KEY);
		if (value instanceof Long) {
			return (Long) value;
		}
		if (value instanceof Integer) {
			return ((Integer) value).longValue();
		}
		if (value instanceof net.minecraft.nbt.LongTag) {
			return ((net.minecraft.nbt.LongTag) value).getAsLong();
		}
		if (value instanceof Number) {
			return ((Number) value).longValue();
		}
		return 0L;
	}

	public static void incrementWorldDay(LevelAccessor world) {
		WorldVariables.get(world).incrementWorldDay();
	}

	public static void setWorldDay(LevelAccessor world, long day) {
		WorldVariables.get(world).setWorldDay(day);
	}

	public static boolean isDayExecuted(LevelAccessor world, int day) {
		WorldVariables variables = WorldVariables.get(world);
		String key = "day_" + day + "_executed";
		if (!variables.hasCustomVariable(key)) {
			return false;
		}
		Object value = variables.getCustomVariable(key);
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		if (value instanceof net.minecraft.nbt.ByteTag) {
			return ((net.minecraft.nbt.ByteTag) value).getAsByte() != 0;
		}
		if (value instanceof Number) {
			return ((Number) value).intValue() != 0;
		}
		return false;
	}

	public static void markDayExecuted(LevelAccessor world, int day) {
		WorldVariables.get(world).setCustomVariable("day_" + day + "_executed", true);
	}

	public static void resetDailyTests(LevelAccessor world) {
		WorldVariables variables = WorldVariables.get(world);
		for (int i = 1; i <= 20; i++) {
			variables.removeCustomVariable("day_" + i + "_executed");
		}
		variables.removeCustomVariable(TIMELINE_KEY);
		variables.setWorldDay(1);
	}

	private static void runDaySideEffects(LevelAccessor world, ServerPlayer player, BlockPos pos, int day) {
		switch (day) {
			case 1 -> {
				broadcast(world, "asga34tz");
				EventjarMod.queueServerWork(20, () -> {
					Level level = player.level();
					if (!level.isClientSide()) {
						level.playSound(null, player.blockPosition(),
								ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambient.cave")),
								SoundSource.NEUTRAL, 1, 1);
					}
				});
			}
			case 2 -> broadcast(world, "a3gzaaaaa");
			case 3 -> {
				player.setAbsorptionAmount(1.0F);
				EventjarMod.queueServerWork(600, () -> {
					if (!player.hasDisconnected() && player.isAlive()) {
						player.setAbsorptionAmount(20.0F);
					}
				});
				ClientOsEffectPacket.send(player, ClientOsEffectPacket.Effect.WALLPAPER);
			}
			case 4 -> {
				if (world instanceof ServerLevel level) {
					FallingBlockEntity.fall(level, pos, Blocks.REDSTONE_TORCH.defaultBlockState());
					EventjarModEntities.FAULT.get().spawn(level, pos.offset(0, 0, 20), MobSpawnType.EVENT);
				}
			}
			case 5 -> grantAdvancement(player, "eventjar:leavemealone");
			case 6 -> {
				if (world instanceof ServerLevel level) {
					level.setBlock(pos, Blocks.REDSTONE_WIRE.defaultBlockState(), 3);
					EventjarModEntities.SCAVENGER.get().spawn(level, pos.offset(0, 0, 20), MobSpawnType.EVENT);
				}
			}
			case 7 -> {
				if (world instanceof ServerLevel level) {
					level.explode(null, pos.getX() + 100.0, pos.getY(), pos.getZ(), 4.0F, Level.ExplosionInteraction.TNT);
				}
			}
			case 8 -> {
				ItemStack poppy = new ItemStack(Blocks.POPPY);
				poppy.enchant(Enchantments.UNBREAKING, 10);
				if (!player.getInventory().add(poppy)) {
					player.drop(poppy, false);
				}
			}
			case 9 -> {
				if (world instanceof ServerLevel level) {
					int ox = 1 + level.getRandom().nextInt(10);
					LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
					if (bolt != null) {
						bolt.moveTo(pos.getX() + ox, pos.getY(), pos.getZ());
						bolt.setVisualOnly(true);
						level.addFreshEntity(bolt);
					}
				}
			}
			case 10 -> broadcast(world, "01000100 01001001 01000101");
			case 11 -> broadcast(world, "§k125asgaga");
			case 12 -> {
				player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 600, 1));
				broadcast(world, "01101100 01100101 01100001 01110110 01100101 00100000 01101101 01100101 00100000 01100001 01101100 01101111 01101110 01100101");
			}
			case 13 -> player.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.PLAYER_HEAD, 666));
			case 14 -> broadcast(world, "KILLLagouiMEEEAUJHGHUAGipa)HNGT");
			case 15 -> {
				if (world instanceof ServerLevel level) {
					level.levelEvent(2001, pos, Block.getId(Blocks.TNT.defaultBlockState()));
				}
			}
			default -> {
			}
		}
	}

	private static void broadcast(LevelAccessor world, String text) {
		if (!world.isClientSide() && world.getServer() != null) {
			world.getServer().getPlayerList().broadcastSystemMessage(Component.literal(text), false);
		}
	}

	private static void grantAdvancement(ServerPlayer player, String id) {
		Advancement adv = player.server.getAdvancements().getAdvancement(new ResourceLocation(id));
		if (adv == null) {
			return;
		}
		AdvancementProgress progress = player.getAdvancements().getOrStartProgress(adv);
		if (!progress.isDone()) {
			for (String criteria : progress.getRemainingCriteria()) {
				player.getAdvancements().award(adv, criteria);
			}
		}
	}

	private static void openTestGui(ServerPlayer player, int day, BlockPos pos) {
		NetworkHooks.openScreen(player, new MenuProvider() {
			@Override
			public Component getDisplayName() {
				return Component.literal("Test" + day);
			}

			@Override
			public AbstractContainerMenu createMenu(int id, Inventory inventory, Player menuPlayer) {
				return createTestMenu(day, id, inventory, pos);
			}
		}, pos);
	}

	private static AbstractContainerMenu createTestMenu(int day, int id, Inventory inventory, BlockPos pos) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos);
		return switch (day) {
			case 1 -> new Test1Menu(id, inventory, buf);
			case 2 -> new Test2Menu(id, inventory, buf);
			case 3 -> new Test3Menu(id, inventory, buf);
			case 4 -> new Test4Menu(id, inventory, buf);
			case 5 -> new Test5Menu(id, inventory, buf);
			case 6 -> new Test6Menu(id, inventory, buf);
			case 7 -> new Test7Menu(id, inventory, buf);
			case 8 -> new Test8Menu(id, inventory, buf);
			case 9 -> new Test9Menu(id, inventory, buf);
			case 10 -> new Test10Menu(id, inventory, buf);
			case 11 -> new Test11Menu(id, inventory, buf);
			case 12 -> new Test12Menu(id, inventory, buf);
			case 13 -> new Test13Menu(id, inventory, buf);
			case 14 -> new Test14Menu(id, inventory, buf);
			case 15 -> new Test15Menu(id, inventory, buf);
			case 16 -> new Test16Menu(id, inventory, buf);
			case 17 -> new Test17Menu(id, inventory, buf);
			case 18 -> new Test18Menu(id, inventory, buf);
			case 19 -> new Test19Menu(id, inventory, buf);
			case 20 -> new Test20Menu(id, inventory, buf);
			default -> new Test1Menu(id, inventory, buf);
		};
	}
}
