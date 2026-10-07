package net.lixis.outofbound.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.UUID;

public class SkyPainterEntity extends Entity {

	private static final String TARGET_TAG = "outofbound_sky_painter_target";
	private static final String AGE_TAG = "outofbound_sky_painter_age";
	private static final String PLACED_TAG = "outofbound_sky_painter_placed";

	private static final double FLY_SPEED = 0.9D;
	private static final double REACH_DISTANCE = 1.05D;
	private static final int MAX_LIFETIME_TICKS = 9600;
	private static final int MAX_PLACED_BLOCKS = 2200;
	private static final double MAX_ANCHOR_HORIZONTAL = 48.0D;
	private static final int MIN_HEIGHT_ABOVE_TERRAIN = 45;
	private static final int MAX_HEIGHT_ABOVE_TERRAIN = 95;

	private static final BlockState[] PALETTE = new BlockState[] {
			Blocks.OBSIDIAN.defaultBlockState(),
			Blocks.CRYING_OBSIDIAN.defaultBlockState(),
			Blocks.BLACK_CONCRETE.defaultBlockState(),
			Blocks.SCULK.defaultBlockState(),
			Blocks.GLOWSTONE.defaultBlockState(),
			Blocks.SOUL_SAND.defaultBlockState(),
			Blocks.NETHERRACK.defaultBlockState(),
			Blocks.RED_CONCRETE.defaultBlockState(),
			Blocks.BONE_BLOCK.defaultBlockState(),
			Blocks.END_STONE.defaultBlockState(),
			Blocks.WHITE_CONCRETE.defaultBlockState(),
	};

	private UUID targetUuid;
	private int age;
	private int placedBlocks;
	private final Deque<BlockPos> queue = new ArrayDeque<>();
	private BlockState currentBlock = PALETTE[0];

	public SkyPainterEntity(EntityType<? extends SkyPainterEntity> type, Level level) {
		super(type, level);
		this.noPhysics = true;
		this.setNoGravity(true);
		this.setInvulnerable(true);
	}

	public void setTarget(UUID uuid) {
		this.targetUuid = uuid;
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide) {
			return;
		}
		if (!(level() instanceof ServerLevel serverLevel)) {
			return;
		}

		if (serverLevel.isDay()) {
			discard();
			return;
		}

		age++;
		if (age > MAX_LIFETIME_TICKS || placedBlocks > MAX_PLACED_BLOCKS) {
			discard();
			return;
		}

		if (queue.isEmpty()) {
			generateFigure(serverLevel);
			if (queue.isEmpty()) {
				discard();
				return;
			}
		}

		flyAndPaint(serverLevel);
	}

	private void flyAndPaint(ServerLevel level) {
		BlockPos target = queue.peek();
		if (target == null) {
			return;
		}

		Vec3 targetVec = Vec3.atCenterOf(target);
		Vec3 delta = targetVec.subtract(position());
		double dist = delta.length();

		if (dist <= REACH_DISTANCE) {
			setPos(targetVec.x, targetVec.y, targetVec.z);
			paint(level, target);
			queue.poll();
		} else {
			Vec3 step = delta.scale(FLY_SPEED / dist);
			setPos(getX() + step.x, getY() + step.y, getZ() + step.z);
			float yaw = (float) (Mth.atan2(-step.x, step.z) * (180.0D / Math.PI));
			setYRot(yaw);
			setYHeadRot(yaw);
		}
		setDeltaMovement(Vec3.ZERO);
	}

	private void paint(ServerLevel level, BlockPos pos) {
		if (pos.getY() <= level.getMinBuildHeight() || pos.getY() >= level.getMaxBuildHeight() - 1) {
			return;
		}
		if (!level.hasChunkAt(pos)) {
			return;
		}
		if (!level.getBlockState(pos).isAir()) {
			return;
		}
		level.setBlock(pos, currentBlock, 3);
		placedBlocks++;
	}

	private void generateFigure(ServerLevel level) {
		RandomSource rng = this.random;
		BlockPos anchor = pickAnchor(level, rng);
		this.currentBlock = PALETTE[rng.nextInt(PALETTE.length)];

		int size = 5 + rng.nextInt(11);
		boolean planeXY = rng.nextBoolean();
		List<BlockPos> points = new ArrayList<>();

		switch (rng.nextInt(8)) {
			case 0 -> ring(points, anchor, size, planeXY);
			case 1 -> filledSquare(points, anchor, size, planeXY);
			case 2 -> hollowRect(points, anchor, size, size - 2, planeXY);
			case 3 -> cross(points, anchor, size, planeXY);
			case 4 -> diagonalX(points, anchor, size, planeXY);
			case 5 -> pillar(points, anchor, size + 6);
			case 6 -> sphereShell(points, anchor, Math.max(3, size / 2));
			default -> glyph(points, anchor, size, planeXY, rng);
		}

		queue.addAll(points);
	}

	private BlockPos pickAnchor(ServerLevel level, RandomSource rng) {
		double baseX = getX();
		double baseZ = getZ();
		ServerPlayer player = resolveTarget(level);
		if (player != null) {
			baseX = player.getX();
			baseZ = player.getZ();
		}
		int ax = Mth.floor(baseX + (rng.nextDouble() * 2.0D - 1.0D) * MAX_ANCHOR_HORIZONTAL);
		int az = Mth.floor(baseZ + (rng.nextDouble() * 2.0D - 1.0D) * MAX_ANCHOR_HORIZONTAL);
		int terrain = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, ax, az);
		int minY = terrain + MIN_HEIGHT_ABOVE_TERRAIN;
		int maxY = Math.min(level.getMaxBuildHeight() - 6, terrain + MAX_HEIGHT_ABOVE_TERRAIN);
		int ay = maxY <= minY ? minY : minY + rng.nextInt(maxY - minY);
		return new BlockPos(ax, ay, az);
	}

	private ServerPlayer resolveTarget(ServerLevel level) {
		if (targetUuid != null) {
			if (level.getEntity(targetUuid) instanceof ServerPlayer player && player.isAlive()) {
				return player;
			}
		}
		Entity nearest = level.getNearestPlayer(this, 256.0D);
		return nearest instanceof ServerPlayer player ? player : null;
	}

	private static BlockPos place(BlockPos anchor, int a, int b, int c, boolean planeXY) {
		if (planeXY) {
			return anchor.offset(a, b, c);
		}
		return anchor.offset(c, b, a);
	}

	private static void ring(List<BlockPos> out, BlockPos anchor, int radius, boolean planeXY) {
		int steps = Math.max(16, radius * 8);
		for (int i = 0; i < steps; i++) {
			double t = (Math.PI * 2.0D) * i / steps;
			int a = (int) Math.round(Math.cos(t) * radius);
			int b = (int) Math.round(Math.sin(t) * radius);
			out.add(place(anchor, a, b, 0, planeXY));
		}
	}

	private static void filledSquare(List<BlockPos> out, BlockPos anchor, int size, boolean planeXY) {
		int half = size / 2;
		for (int a = -half; a <= half; a++) {
			for (int b = -half; b <= half; b++) {
				out.add(place(anchor, a, b, 0, planeXY));
			}
		}
	}

	private static void hollowRect(List<BlockPos> out, BlockPos anchor, int width, int height, boolean planeXY) {
		int hw = Math.max(1, width / 2);
		int hh = Math.max(1, height / 2);
		for (int a = -hw; a <= hw; a++) {
			out.add(place(anchor, a, -hh, 0, planeXY));
			out.add(place(anchor, a, hh, 0, planeXY));
		}
		for (int b = -hh; b <= hh; b++) {
			out.add(place(anchor, -hw, b, 0, planeXY));
			out.add(place(anchor, hw, b, 0, planeXY));
		}
	}

	private static void cross(List<BlockPos> out, BlockPos anchor, int size, boolean planeXY) {
		int half = size / 2;
		for (int a = -half; a <= half; a++) {
			out.add(place(anchor, a, 0, 0, planeXY));
		}
		for (int b = -half; b <= half; b++) {
			out.add(place(anchor, 0, b, 0, planeXY));
		}
	}

	private static void diagonalX(List<BlockPos> out, BlockPos anchor, int size, boolean planeXY) {
		int half = size / 2;
		for (int d = -half; d <= half; d++) {
			out.add(place(anchor, d, d, 0, planeXY));
			out.add(place(anchor, d, -d, 0, planeXY));
		}
	}

	private static void pillar(List<BlockPos> out, BlockPos anchor, int height) {
		for (int b = 0; b < height; b++) {
			out.add(anchor.offset(0, b, 0));
		}
	}

	private static void sphereShell(List<BlockPos> out, BlockPos anchor, int radius) {
		int r2 = radius * radius;
		int inner = (radius - 1) * (radius - 1);
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					int d = x * x + y * y + z * z;
					if (d <= r2 && d >= inner) {
						out.add(anchor.offset(x, y, z));
					}
				}
			}
		}
	}

	private static void glyph(List<BlockPos> out, BlockPos anchor, int size, boolean planeXY, RandomSource rng) {
		int half = Math.max(2, size / 2);
		int strokes = 3 + rng.nextInt(4);
		for (int s = 0; s < strokes; s++) {
			int a0 = rng.nextInt(size) - half;
			int b0 = rng.nextInt(size) - half;
			int a1 = rng.nextInt(size) - half;
			int b1 = rng.nextInt(size) - half;
			int len = Math.max(Math.abs(a1 - a0), Math.abs(b1 - b0));
			for (int i = 0; i <= len; i++) {
				float t = len == 0 ? 0.0F : (float) i / len;
				int a = Math.round(Mth.lerp(t, a0, a1));
				int b = Math.round(Mth.lerp(t, b0, b1));
				out.add(place(anchor, a, b, 0, planeXY));
			}
		}
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		if (tag.hasUUID(TARGET_TAG)) {
			this.targetUuid = tag.getUUID(TARGET_TAG);
		}
		this.age = tag.getInt(AGE_TAG);
		this.placedBlocks = tag.getInt(PLACED_TAG);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		if (targetUuid != null) {
			tag.putUUID(TARGET_TAG, targetUuid);
		}
		tag.putInt(AGE_TAG, age);
		tag.putInt(PLACED_TAG, placedBlocks);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
