package net.lixis9.eventjar.procedures;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.level.BlockEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.arguments.EntityAnchorArgument;

import net.lixis9.eventjar.init.EventjarModEntities;
import net.lixis9.eventjar.init.EventjarModBlocks;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class MeetblockPriRazrushieniiBlokaIghrokomProcedure {
	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getPlayer(), event.getPlayer().getX(), event.getPlayer().getY(), event.getPlayer().getZ());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, double px, double py, double pz) {
		execute(null, world, x, y, z, entity, px, py, pz);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity, double px, double py, double pz) {
		if (entity == null)
			return;
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == EventjarModBlocks.MEETBLOCK.get()) {
			entity.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(px, (2 + py), (-1 + pz)));
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = EventjarModEntities.MEETBOY.get().spawn(_level, BlockPos.containing(px, py, -1 + pz), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:cavesound")), SoundSource.NEUTRAL, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:cavesound")), SoundSource.NEUTRAL, 1, 1, false);
				}
			}
			if (Math.random() < (1) / ((float) 5)) {
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:godisloveumusic")), SoundSource.NEUTRAL, 1, 1);
					} else {
						_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:godisloveumusic")), SoundSource.NEUTRAL, 1, 1, false);
					}
				}
			}
		}
	}
}
