package net.lixis.outofbound.dimension.theme;

import net.lixis.outofbound.OutofboundMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class MazeBlockPool {

	public enum Role {
		FLOOR,
		CEILING,
		WALL,
		PILLAR,
		LIGHT,
		ACCENT
	}

	private static final ResourceLocation DIMENSION_PORTAL =
			new ResourceLocation(OutofboundMod.MODID, "dimension_portal");

	private static final BlockGetter EMPTY = EmptyBlockGetter.INSTANCE;
	private static final BlockPos ZERO = BlockPos.ZERO;

	private static volatile Map<Role, BlockState[]> pools;

	private MazeBlockPool() {
	}

	public static BlockState pickRandom(Random rng, Role role) {
		BlockState[] states = pool(role);
		if (states.length == 0) {
			return fallback(role);
		}
		return states[rng.nextInt(states.length)];
	}

	public static int size(Role role) {
		return pool(role).length;
	}

	private static BlockState[] pool(Role role) {
		ensureBuilt();
		BlockState[] states = pools.get(role);
		return states != null ? states : new BlockState[0];
	}

	private static void ensureBuilt() {
		Map<Role, BlockState[]> cached = pools;
		if (cached != null) {
			return;
		}
		synchronized (MazeBlockPool.class) {
			cached = pools;
			if (cached == null) {
				pools = cached = buildPools();
			}
		}
	}

	private static Map<Role, BlockState[]> buildPools() {
		EnumMap<Role, List<BlockState>> building = new EnumMap<>(Role.class);
		for (Role role : Role.values()) {
			building.put(role, new ArrayList<>());
		}

		for (Block block : BuiltInRegistries.BLOCK) {
			if (!isAllowedBlock(block)) {
				continue;
			}
			BlockState state = block.defaultBlockState();
			if (!isAllowedState(state)) {
				continue;
			}

			if (isFloorMaterial(state)) {
				building.get(Role.FLOOR).add(state);
				building.get(Role.CEILING).add(state);
			}
			if (isWallMaterial(state)) {
				building.get(Role.WALL).add(state);
			}
			if (isPillarMaterial(state)) {
				building.get(Role.PILLAR).add(state);
			}
			if (state.getLightEmission() > 0) {
				building.get(Role.LIGHT).add(state);
			}
			if (isAccentMaterial(block)) {
				building.get(Role.ACCENT).add(state);
			}
		}

		EnumMap<Role, BlockState[]> built = new EnumMap<>(Role.class);
		for (Role role : Role.values()) {
			List<BlockState> list = building.get(role);
			list.sort(Comparator.comparing(s -> BuiltInRegistries.BLOCK.getKey(s.getBlock()).toString()));
			if (list.isEmpty()) {
				list.add(fallback(role));
			}
			built.put(role, list.toArray(new BlockState[0]));
			OutofboundMod.LOGGER.info("[outofbound] Chaotic palette {}: {} blocks", role, list.size());
		}
		return built;
	}

	private static boolean isFloorMaterial(BlockState state) {
		Block block = state.getBlock();
		if (isAttachableOrPartial(block)) {
			return false;
		}
		return isFullSolid(state);
	}

	private static boolean isWallMaterial(BlockState state) {
		Block block = state.getBlock();
		if (block instanceof WallBlock || block instanceof FenceBlock) {
			return true;
		}
		if (isAttachableOrPartial(block)) {
			return false;
		}
		return isFullSolid(state);
	}

	private static boolean isPillarMaterial(BlockState state) {
		Block block = state.getBlock();
		if (block instanceof RotatedPillarBlock) {
			return true;
		}
		if (isAttachableOrPartial(block)) {
			return false;
		}
		return isFullSolid(state);
	}

	private static boolean isAccentMaterial(Block block) {
		if (block instanceof TorchBlock
				|| block instanceof WallTorchBlock
				|| block instanceof LanternBlock
				|| block instanceof ChainBlock
				|| block instanceof LadderBlock
				|| block instanceof DoorBlock
				|| block instanceof TrapDoorBlock
				|| block instanceof IronBarsBlock
				|| block instanceof FenceGateBlock
				|| block instanceof SignBlock) {
			return true;
		}
		String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
		return path.contains("sign") || path.contains("candle");
	}

	private static boolean isAttachableOrPartial(Block block) {
		return block instanceof StairBlock
				|| block instanceof TorchBlock
				|| block instanceof WallTorchBlock
				|| block instanceof DoorBlock
				|| block instanceof TrapDoorBlock
				|| block instanceof SignBlock
				|| block instanceof LadderBlock
				|| block instanceof LanternBlock
				|| block instanceof ChainBlock
				|| isAccentMaterial(block);
	}

	private static boolean isFullSolid(BlockState state) {
		return state.isCollisionShapeFullBlock(EMPTY, ZERO) && state.isSolidRender(EMPTY, ZERO);
	}

	private static BlockState fallback(Role role) {
		return switch (role) {
			case FLOOR, CEILING, WALL, PILLAR -> Blocks.STONE.defaultBlockState();
			case LIGHT -> Blocks.GLOWSTONE.defaultBlockState();
			case ACCENT -> Blocks.TORCH.defaultBlockState();
		};
	}

	private static boolean isAllowedBlock(Block block) {
		if (block == Blocks.AIR || block == Blocks.CAVE_AIR || block == Blocks.VOID_AIR) {
			return false;
		}
		if (block == Blocks.WATER || block == Blocks.LAVA) {
			return false;
		}
		if (block == Blocks.NETHER_PORTAL || block == Blocks.END_PORTAL || block == Blocks.END_GATEWAY) {
			return false;
		}

		if (block instanceof EntityBlock
				|| block instanceof MovingPistonBlock
				|| block instanceof PistonHeadBlock) {
			return false;
		}
		if (block == Blocks.MOVING_PISTON
				|| block == Blocks.PISTON_HEAD
				|| block == Blocks.BARRIER
				|| block == Blocks.STRUCTURE_VOID
				|| block == Blocks.STRUCTURE_BLOCK
				|| block == Blocks.JIGSAW
				|| block == Blocks.COMMAND_BLOCK
				|| block == Blocks.CHAIN_COMMAND_BLOCK
				|| block == Blocks.REPEATING_COMMAND_BLOCK
				|| block == Blocks.LIGHT
				|| block == Blocks.FIRE
				|| block == Blocks.SOUL_FIRE
				|| block == Blocks.BUBBLE_COLUMN) {
			return false;
		}
		ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
		if (DIMENSION_PORTAL.equals(id)) {
			return false;
		}
		String path = id.getPath();
		return !(path.contains("piston")
				|| path.contains("portal")
				|| path.contains("command_block")
				|| path.contains("structure")
				|| path.contains("jigsaw")
				|| path.equals("barrier")
				|| path.equals("light")
				|| path.contains("spawner"));
	}

	private static boolean isAllowedState(BlockState state) {
		if (state.isAir() || !isAllowedBlock(state.getBlock())) {
			return false;
		}
		if (state.hasBlockEntity()) {
			return false;
		}
		if (!state.getFluidState().isEmpty()) {
			if (state.getFluidState().is(FluidTags.WATER) || state.getFluidState().is(FluidTags.LAVA)) {
				return false;
			}
		}
		return true;
	}
}
