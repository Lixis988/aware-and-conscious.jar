package net.lixis9.eventjar.procedures;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class RndGodisloveyouProcedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			execute(event, event.player.level(), event.player.getX(), event.player.getZ());
		}
	}

	public static void execute(LevelAccessor world, double x, double z) {
		execute(null, world, x, z);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double z) {
		if (Math.random() < (1) / ((float) 100000)) {
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, new BlockPos(Mth.nextInt(RandomSource.create(), 1, 8), world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z), Mth.nextInt(RandomSource.create(), 1, 7)),
							ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:godisloveyou")), SoundSource.NEUTRAL, 1, 1);
				} else {
					_level.playLocalSound((Mth.nextInt(RandomSource.create(), 1, 8)), (world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z)), (Mth.nextInt(RandomSource.create(), 1, 7)),
							ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:godisloveyou")), SoundSource.NEUTRAL, 1, 1, false);
				}
			}
		}
	}
}
