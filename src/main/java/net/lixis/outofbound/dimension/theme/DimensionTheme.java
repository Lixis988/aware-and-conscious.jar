package net.lixis.outofbound.dimension.theme;

import net.lixis.outofbound.dimension.gen.MazeComposer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public final class DimensionTheme {

	private static final String[] WALLS = {
			"eventjar:meat", "eventjar:meat2", "eventjar:meat3",
			"eventjar:meat4", "eventjar:meat6", "eventjar:meat7"
	};

	private static final String[] FLOORS = {
			"eventjar:meat", "eventjar:meat2", "eventjar:meat3",
			"eventjar:meat4", "eventjar:meat6", "eventjar:meat7"
	};

	private static final String[] CEILINGS = {
			"eventjar:meat", "eventjar:meat2", "eventjar:meat3",
			"eventjar:meat4", "eventjar:meat6", "eventjar:meat7"
	};

	private static final String[] PILLARS = {
			"eventjar:meat", "eventjar:meat2", "eventjar:meat3",
			"eventjar:meat4", "eventjar:meat6", "eventjar:meat7"
	};

	private static final String[] LIGHTS = {
			"minecraft:glowstone", "minecraft:sea_lantern", "minecraft:shroomlight",
			"minecraft:ochre_froglight", "minecraft:verdant_froglight", "minecraft:redstone_lamp",
			"minecraft:pearlescent_froglight",
			"minecraft:magma_block", "minecraft:crying_obsidian", "minecraft:beacon"
	};

	private static final String[] ACCENTS = {
			"minecraft:torch", "minecraft:soul_torch", "minecraft:lantern",
			"minecraft:chain", "minecraft:iron_bars", "minecraft:cobweb",
			"minecraft:flower_pot", "minecraft:scaffolding", "minecraft:ladder"
	};

	private static final String[] NAMES = {
			"Yellow Rooms", "Concrete Halls", "Office Block", "Technical Floor", "Abandoned Wing",
			"Industrial Zone", "Endless Stairs", "Dark Section", "Anomalous Space", "Liminal Expanse",
			"Service Tunnels", "Forgotten Archive", "Maintenance Stack", "Flooded Archive",
			"Cold Storage", "Server Farm", "Clinic Wing", "Transit Hub", "Boiler Room",
			"Staff Quarters", "Parking Deck", "Observation Deck", "Underpass", "Glass Atrium",
			"Rust Corridor", "Paper Maze", "Silent Gallery", "Emergency Exit", "Loading Bay",
			"Sublevel Theta", "Night Shift", "Locked Wing", "Mirror Floor", "Dead Mall",
			"Crawlspace", "Ventilation Shaft", "Basement Annex", "Roof Access"
	};

	private static final String[] CHAOTIC_NAMES = {
			"Material Storm", "Block Salad", "Registry Overflow", "Glitch Vault", "Chaos Wing",
			"Impossible Masonry", "Palette Rupture", "Fragmented Hall", "Entropy Sector", "Noise Floor",
			"Hash Collision", "Texture Bleed", "Voxel Soup", "Broken Schema", "Null Corridor",
			"Garbage Collection", "Undefined Room", "Corrupted Prefab", "Stack Smash", "Bit Rot"
	};

	private static final int CHAOTIC_ROLL_DENOMINATOR = 4;

	public final long index;
	public final String name;
	public final boolean chaotic;

	public final MazeComposer composer;

	public final int corridorWidth;
	public final int ceilingHeight;
	public final int levelCount;

	public final float openness;
	public final float roomChance;
	public final float shaftChance;
	public final float lightChance;
	public final float wallBias;
	public final float voidPitChance;

	public final float materialMixChance;
	public final float brokenFloorChance;
	public final float mezzanineChance;
	public final float hangingChance;
	public final float accentChance;
	public final float debrisChance;
	public final int stripePeriod;

	public final double featureScale;
	public final double freqA;
	public final double freqB;
	public final double freqC;

	public final BlockState wall;
	public final BlockState wallAlt;
	public final BlockState floor;
	public final BlockState floorAlt;
	public final BlockState ceiling;
	public final BlockState pillar;
	public final BlockState light;
	public final BlockState accent;

	public final int fogColor;
	public final float fogStart;
	public final float fogEnd;

	private DimensionTheme(long index) {
		this.index = index;

		Random rng = new Random(index * 0x9E3779B97F4A7C15L ^ 0xD1B54A32D192ED03L);

		this.chaotic = rng.nextInt(CHAOTIC_ROLL_DENOMINATOR) == 0;
		this.name = chaotic
				? CHAOTIC_NAMES[rng.nextInt(CHAOTIC_NAMES.length)]
				: NAMES[rng.nextInt(NAMES.length)];
		this.composer = MazeComposer.forIndex(index);

		this.corridorWidth = 2 + rng.nextInt(7);
		this.ceilingHeight = 2 + rng.nextInt(9);
		this.levelCount = 2 + rng.nextInt(9);

		this.openness = 0.25F + rng.nextFloat() * 0.65F;
		this.roomChance = 0.05F + rng.nextFloat() * 0.45F;
		this.shaftChance = 0.01F + rng.nextFloat() * 0.11F;
		this.lightChance = 0.10F + rng.nextFloat() * 0.45F;
		this.wallBias = 0.20F + rng.nextFloat() * 0.55F;
		this.voidPitChance = 0.004F + rng.nextFloat() * 0.036F;

		this.materialMixChance = 0.08F + rng.nextFloat() * 0.42F;
		this.brokenFloorChance = rng.nextFloat() < 0.55F ? 0.02F + rng.nextFloat() * 0.10F : 0.0F;
		this.mezzanineChance = rng.nextFloat() < 0.50F ? 0.03F + rng.nextFloat() * 0.12F : 0.0F;
		this.hangingChance = 0.01F + rng.nextFloat() * 0.06F;
		this.accentChance = chaotic ? 0.04F + rng.nextFloat() * 0.06F : 0.01F + rng.nextFloat() * 0.04F;
		this.debrisChance = 0.01F + rng.nextFloat() * 0.05F;
		this.stripePeriod = rng.nextFloat() < 0.35F ? 2 + rng.nextInt(7) : 0;

		this.featureScale = 0.03 + rng.nextDouble() * 0.35;
		this.freqA = 0.03 + rng.nextDouble() * 0.40;
		this.freqB = 0.03 + rng.nextDouble() * 0.40;
		this.freqC = 0.03 + rng.nextDouble() * 0.40;

		if (chaotic) {
			this.wall = block(WALLS[rng.nextInt(WALLS.length)]);
			this.wallAlt = block(WALLS[rng.nextInt(WALLS.length)]);
			this.floor = block(FLOORS[rng.nextInt(FLOORS.length)]);
			this.floorAlt = block(FLOORS[rng.nextInt(FLOORS.length)]);
			this.ceiling = block(CEILINGS[rng.nextInt(CEILINGS.length)]);
			this.pillar = block(PILLARS[rng.nextInt(PILLARS.length)]);
			this.light = MazeBlockPool.pickRandom(rng, MazeBlockPool.Role.LIGHT);
			this.accent = MazeBlockPool.pickRandom(rng, MazeBlockPool.Role.ACCENT);
		} else {
			this.wall = block(WALLS[rng.nextInt(WALLS.length)]);
			this.wallAlt = block(WALLS[rng.nextInt(WALLS.length)]);
			this.floor = block(FLOORS[rng.nextInt(FLOORS.length)]);
			this.floorAlt = block(FLOORS[rng.nextInt(FLOORS.length)]);
			this.ceiling = block(CEILINGS[rng.nextInt(CEILINGS.length)]);
			this.pillar = block(PILLARS[rng.nextInt(PILLARS.length)]);
			this.light = block(LIGHTS[rng.nextInt(LIGHTS.length)]);
			this.accent = block(ACCENTS[rng.nextInt(ACCENTS.length)]);
		}

		int r = 72 + rng.nextInt(56);
		int g = 22 + rng.nextInt(26);
		int b = 12 + rng.nextInt(18);
		this.fogColor = (r << 16) | (g << 8) | b;

		float density = 0.15F + rng.nextFloat() * 0.80F;
		this.fogEnd = 12.0F + (1.0F - density) * 110.0F;
		this.fogStart = this.fogEnd * (0.08F + rng.nextFloat() * 0.25F);
	}

	private static final ConcurrentHashMap<Long, DimensionTheme> CACHE = new ConcurrentHashMap<>();

	public static DimensionTheme forIndex(long index) {
		return CACHE.computeIfAbsent(index, DimensionTheme::new);
	}

	public float fogRed() {
		return ((fogColor >> 16) & 0xFF) / 255.0F;
	}

	public float fogGreen() {
		return ((fogColor >> 8) & 0xFF) / 255.0F;
	}

	public float fogBlue() {
		return (fogColor & 0xFF) / 255.0F;
	}

	private static BlockState block(String id) {
		Block b = BuiltInRegistries.BLOCK.get(new ResourceLocation(id));
		return b != null ? b.defaultBlockState() : Blocks.STONE.defaultBlockState();
	}
}
