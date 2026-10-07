package net.lixis.outofbound.dimension.gen;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.dimension.MazeConfig;
import net.lixis.outofbound.dimension.theme.DimensionTheme;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.registries.ForgeRegistries;

public final class MazeShape {

	public static final int MIN_Y = 0;

	private static final long LIGHT_SALT = 0x3333333333333333L;
	private static final long SHAFT_SALT = 0x4444444444444444L;
	private static final long VOID_SALT = 0x5555555555555555L;
	private static final long PORTAL_SALT = 0x6666666666666666L;
	private static final long ACCENT_SALT = 0xCCCCCCCCCCCCCCCCL;
	private static final long MIX_SALT = 0x7777777777777777L;
	private static final long BROKEN_SALT = 0x8888888888888888L;
	private static final long MEZZ_SALT = 0x9999999999999999L;
	private static final long HANG_SALT = 0xAAAAAAAAAAAAAAAAL;
	private static final long DEBRIS_SALT = 0xBBBBBBBBBBBBBBBBL;

	private static BlockState cachedPortalFloor;

	private MazeShape() {
	}

	public static int levelCount(DimensionTheme theme) {
		return Math.max(1, Math.min(theme.levelCount, MazeConfig.maxLevelsCap));
	}

	public static int totalHeight(DimensionTheme theme) {
		int levelTotal = theme.ceilingHeight + 1;
		return levelCount(theme) * levelTotal + 1;
	}

	public static BlockState blockAt(DimensionTheme theme, long seed, int x, int y, int z) {
		int relY = y - MIN_Y;
		if (relY < 0) {
			return theme.pillar;
		}

		int levels = levelCount(theme);
		int levelTotal = theme.ceilingHeight + 1;
		int topFloorRelY = levels * levelTotal;
		if (relY > topFloorRelY) {
			return null;
		}

		int level = relY / levelTotal;
		int localY = relY % levelTotal;

		int spacing = theme.corridorWidth + 1;
		int lx = Math.floorMod(x, spacing);
		int lz = Math.floorMod(z, spacing);
		long cx = Math.floorDiv(x, spacing);
		long cz = Math.floorDiv(z, spacing);

		if (localY == 0) {
			if (level == 0) {
				if (isVoidPit(theme, seed, cx, cz) && isShaftInterior(lx, lz)) {
					return null;
				}
				return maybePortalFloor(theme, seed, x, z, level, pickFloor(theme, seed, x, z, level));
			}

			if (level < levels
					&& theme.composer.column(seed, theme, x, z, level) == MazeStyle.ColumnType.OPEN
					&& MazeHash.hashFloat(seed, BROKEN_SALT, cx, cz, level) < theme.brokenFloorChance) {
				return null;
			}

			BlockState slab = (level == levels)
					? theme.ceiling
					: pickFloor(theme, seed, x, z, level);

			if (level < levels && isShaftCell(theme, seed, cx, cz, level) && isShaftInterior(lx, lz)) {
				return null;
			}
			return maybePortalFloor(theme, seed, x, z, level, slab);
		}

		if (level >= levels) {
			return null;
		}

		MazeStyle.ColumnType type = theme.composer.column(seed, theme, x, z, level);
		if (type == MazeStyle.ColumnType.PILLAR) {
			return theme.pillar;
		}
		if (type == MazeStyle.ColumnType.WALL) {
			return pickWall(theme, seed, x, z, level);
		}

		if (isShaftCell(theme, seed, cx, cz, level) && lx == 0 && lz == 0) {
			return Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH);
		}

		if (theme.ceilingHeight >= 4
				&& localY == theme.ceilingHeight / 2
				&& MazeHash.hashFloat(seed, MEZZ_SALT, cx, cz, level) < theme.mezzanineChance) {
			return pickFloor(theme, seed, x, z, level);
		}

		if (localY == theme.ceilingHeight - 1
				&& theme.ceilingHeight >= 3
				&& MazeHash.hashFloat(seed, HANG_SALT, cx, cz, level) < theme.hangingChance) {
			return theme.pillar;
		}

		if (MazeConfig.enableLights
				&& localY == theme.ceilingHeight
				&& lx == (spacing - 1) / 2
				&& lz == (spacing - 1) / 2
				&& MazeHash.hashFloat(seed, LIGHT_SALT, cx, cz, level) < theme.lightChance) {
			return theme.light;
		}

		if (localY == 1
				&& MazeHash.hashFloat(seed, DEBRIS_SALT, cx, cz, level) < theme.debrisChance) {
			return theme.accent;
		}

		BlockState accent = maybeAccent(theme, seed, x, y, z, level, localY, lx, lz, cx, cz);
		if (accent != null) {
			return accent;
		}

		return null;
	}

	private static BlockState pickWall(DimensionTheme theme, long seed, int x, int z, int level) {
		if (theme.stripePeriod > 0) {
			long stripe = Math.floorDiv(x + z, theme.stripePeriod);
			if ((stripe & 1L) == 0L) {
				return theme.wallAlt;
			}
		}
		if (MazeHash.hashFloat(seed, MIX_SALT, x, z, level) < theme.materialMixChance) {
			return theme.wallAlt;
		}
		return theme.wall;
	}

	private static BlockState pickFloor(DimensionTheme theme, long seed, int x, int z, int level) {
		if (theme.stripePeriod > 0) {
			long stripe = Math.floorDiv(x, theme.stripePeriod);
			if ((stripe & 1L) == 0L) {
				return theme.floorAlt;
			}
		}
		if (MazeHash.hashFloat(seed, MIX_SALT ^ 0x55, x, z, level) < theme.materialMixChance) {
			return theme.floorAlt;
		}
		return theme.floor;
	}

	private static BlockState maybeAccent(
			DimensionTheme theme,
			long seed,
			int x,
			int y,
			int z,
			int level,
			int localY,
			int lx,
			int lz,
			long cx,
			long cz) {
		if (localY < 1 || localY > 2) {
			return null;
		}
		if (lx != 0 && lz != 0) {
			return null;
		}
		float chance = theme.chaotic ? Math.max(theme.accentChance, 0.035F) : theme.accentChance;
		if (MazeHash.hashFloat(seed, ACCENT_SALT, cx, cz, level) > chance) {
			return null;
		}

		BlockState accent = theme.accent;
		if (accent.getBlock() instanceof WallTorchBlock) {
			Direction facing = lx == 0 ? Direction.WEST : Direction.NORTH;
			return accent.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
		}
		return accent;
	}

	private static boolean isShaftCell(DimensionTheme theme, long seed, long cx, long cz, int level) {
		return MazeHash.hashFloat(seed, SHAFT_SALT, cx, cz, level) < theme.shaftChance;
	}

	private static boolean isVoidPit(DimensionTheme theme, long seed, long cx, long cz) {
		return MazeHash.hashFloat(seed, VOID_SALT, cx, cz, 0) < theme.voidPitChance;
	}

	private static boolean isShaftInterior(int lx, int lz) {
		return lx > 0 && lz > 0;
	}

	private static BlockState maybePortalFloor(DimensionTheme theme, long seed, int x, int z, int level, BlockState fallback) {
		int spacing = MazeConfig.portalChunkSpacing;
		if (spacing <= 0) {
			return fallback;
		}

		int walkableLevels = levelCount(theme);
		if (level < 0 || level >= walkableLevels) {
			return fallback;
		}

		int chunkX = Math.floorDiv(x, 16);
		int chunkZ = Math.floorDiv(z, 16);
		if (Math.floorMod(chunkX, spacing) != 0 || Math.floorMod(chunkZ, spacing) != 0) {
			return fallback;
		}

		if (Math.floorMod(x, 16) != 8 || Math.floorMod(z, 16) != 8) {
			return fallback;
		}

		int portalLevel = (int) Math.floorMod(MazeHash.hash(seed, PORTAL_SALT, chunkX, chunkZ), walkableLevels);
		if (level != portalLevel) {
			return fallback;
		}

		if (theme.composer.column(seed, theme, x, z, level) != MazeStyle.ColumnType.OPEN) {
			return fallback;
		}

		BlockState portal = portalFloorState();
		return portal != null ? portal : fallback;
	}

	private static BlockState portalFloorState() {
		if (cachedPortalFloor == null) {
			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(OutofboundMod.MODID, "dimension_portal"));
			cachedPortalFloor = block != null ? block.defaultBlockState() : Blocks.AIR.defaultBlockState();
		}
		return cachedPortalFloor.isAir() ? null : cachedPortalFloor;
	}
}
