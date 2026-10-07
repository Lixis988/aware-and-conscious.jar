package net.lixis.outofbound.feature.corruption.corruptors;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.feature.corruption.CorruptionRunContext;
import net.lixis.outofbound.feature.corruption.CorruptionSnapshots;
import net.lixis.outofbound.block.OutofboundBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.lixis.outofbound.feature.corruption.util.CorruptionScaling;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class ChunkCorruptor extends ServerCorruptor {

	private static final int MIN_BLOCKS = 8;
	private static final int MAX_BLOCKS = 35;
	private static final int HORIZONTAL_RADIUS = 16;
	private static final int VERTICAL_RADIUS = 8;
	private static final float AIR_CORRUPT_LEVEL = 65.0F;

	private static List<Block> damagePalette;
	private static List<Block> solidPalette;

	@Override
	public float minLevel() {
		return 15.0F;
	}

	@Override
	public int weight() {
		return 6;
	}

	@Override
	protected void runServer(ServerLevel level, ServerPlayer player, CorruptionRunContext ctx) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		BlockPos origin = player.blockPosition();
		boolean includeAir = ctx.level() >= AIR_CORRUPT_LEVEL;
		List<BlockPos> candidates = collectBlocks(level, origin, includeAir);
		if (candidates.isEmpty()) {
			return;
		}

		Collections.shuffle(candidates, random);
		int count = Math.min(
				CorruptionScaling.scaledBlockCount(ctx.level(), minLevel(), MIN_BLOCKS, MAX_BLOCKS, random),
				candidates.size());
		List<Block> palette = getDamagePalette();
		int ttl = OutofboundMod.CORRUPTION.effectTtl(ctx.level());

		for (int i = 0; i < count; i++) {
			BlockPos pos = candidates.get(i);
			BlockState original = level.getBlockState(pos);
			if (!includeAir && (original.isAir() || original.getDestroySpeed(level, pos) < 0.0F)) {
				continue;
			}
			if (original.getDestroySpeed(level, pos) < 0.0F && !original.isAir()) {
				continue;
			}

			BlockState replacement = pickReplacement(random, palette, original.isAir());
			CorruptionSnapshots.scheduleBlockRestore(level.dimension(), pos.immutable(), original, replacement, ttl);
			level.setBlockAndUpdate(pos, replacement);
		}
	}

	private static List<BlockPos> collectBlocks(ServerLevel level, BlockPos origin, boolean includeAir) {
		List<BlockPos> candidates = new ArrayList<>();
		for (int dx = -HORIZONTAL_RADIUS; dx <= HORIZONTAL_RADIUS; dx++) {
			for (int dy = -VERTICAL_RADIUS; dy <= VERTICAL_RADIUS; dy++) {
				for (int dz = -HORIZONTAL_RADIUS; dz <= HORIZONTAL_RADIUS; dz++) {
					BlockPos pos = origin.offset(dx, dy, dz);
					if (!level.hasChunkAt(pos)) {
						continue;
					}
					BlockState state = level.getBlockState(pos);
					if (state.getDestroySpeed(level, pos) < 0.0F && !state.isAir()) {
						continue;
					}
					if (state.isAir()) {
						if (includeAir) {
							candidates.add(pos.immutable());
						}
						continue;
					}
					candidates.add(pos.immutable());
				}
			}
		}
		return candidates;
	}

	private static BlockState pickReplacement(ThreadLocalRandom random, List<Block> palette, boolean replacingAir) {
		if (replacingAir) {
			Block block = getSolidPalette().get(random.nextInt(getSolidPalette().size()));
			return block.defaultBlockState();
		}
		int roll = random.nextInt(10);
		if (roll < 3) {
			return Blocks.AIR.defaultBlockState();
		}
		if (roll < 6) {
			return OutofboundBlocks.GLITCH_GRASS.get().defaultBlockState();
		}
		Block block = palette.get(random.nextInt(palette.size()));
		return block.defaultBlockState();
	}

	private static List<Block> getSolidPalette() {
		if (solidPalette != null) {
			return solidPalette;
		}
		List<Block> blocks = new ArrayList<>();
		for (Block block : getDamagePalette()) {
			if (!block.defaultBlockState().isAir()) {
				blocks.add(block);
			}
		}
		if (blocks.isEmpty()) {
			blocks.add(Blocks.STONE);
		}
		solidPalette = blocks;
		return solidPalette;
	}

	private static List<Block> getDamagePalette() {
		if (damagePalette != null) {
			return damagePalette;
		}
		List<Block> blocks = new ArrayList<>();
		blocks.add(Blocks.STONE);
		blocks.add(Blocks.DIRT);
		blocks.add(Blocks.GRASS_BLOCK);
		blocks.add(Blocks.COBBLESTONE);
		blocks.add(Blocks.OBSIDIAN);
		blocks.add(Blocks.SAND);
		blocks.add(Blocks.GRAVEL);
		blocks.add(Blocks.NETHERRACK);
		blocks.add(Blocks.ICE);
		blocks.add(Blocks.CLAY);
		blocks.add(OutofboundBlocks.GLITCH_GRASS.get());
		for (Block block : ForgeRegistries.BLOCKS.getValues()) {
			if (block == null || block.defaultBlockState().isAir()) {
				continue;
			}
			var key = ForgeRegistries.BLOCKS.getKey(block);
			if (key == null) {
				continue;
			}
			String name = key.getNamespace();
			if (!"minecraft".equals(name) && !"outofbound".equals(name)) {
				continue;
			}
			if (blocks.size() > 40) {
				break;
			}
			if (!blocks.contains(block)) {
				blocks.add(block);
			}
		}
		damagePalette = blocks;
		return damagePalette;
	}
}
