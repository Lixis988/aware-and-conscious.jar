package net.lixis9.eventjar.world.teleporter;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.BlockUtil;

import net.lixis9.eventjar.init.EventjarModBlocks;

import java.util.Optional;

public class WierdPortalShape {
	private final Level level;
	private final Direction.Axis axis;
	private final Direction rightDir;
	public int numPortalBlocks;
	public BlockPos bottomLeft;
	public int height;
	public int width;

	public WierdPortalShape(Level level, BlockPos pos, Direction.Axis axis) {
		this.level = level;
		this.axis = axis;
		this.rightDir = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
		this.bottomLeft = this.calculateBottomLeft(pos);
		if (this.bottomLeft == null) {
			this.bottomLeft = pos;
			this.width = 1;
			this.height = 1;
		} else {
			this.width = this.calculateWidth();
			if (this.width > 0) {
				this.height = this.calculateHeight();
			}
		}
	}

	public static Optional<WierdPortalShape> findEmptyPortalShape(Level level, BlockPos pos, Direction.Axis axis) {
		return findPortalShape(level, pos, (portalShape) -> portalShape.isValid() && portalShape.numPortalBlocks == 0, axis);
	}

	public static Optional<WierdPortalShape> findPortalShape(Level level, BlockPos pos, java.util.function.Predicate<WierdPortalShape> predicate, Direction.Axis axis) {
		Optional<WierdPortalShape> optional = Optional.of(new WierdPortalShape(level, pos, axis)).filter(predicate);
		if (optional.isPresent()) {
			return optional;
		} else {
			Direction.Axis direction$axis = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
			return Optional.of(new WierdPortalShape(level, pos, direction$axis)).filter(predicate);
		}
	}

	public static Vec3 getRelativePosition(BlockUtil.FoundRectangle portal, Direction.Axis axis, Vec3 entityPos, net.minecraft.world.entity.EntityDimensions entitySize) {

		double d0 = axis == Direction.Axis.X ? 2.0 : 3.0;
		double d1 = axis == Direction.Axis.X ? 3.0 : 2.0;
		double d2 = entityPos.get(axis) - portal.minCorner.get(axis);
		double d3 = entitySize.width / 2.0D;
		return new Vec3(d2 / d0 - 0.5D, 0.0D, d3 / d1);
	}

	public static PortalInfo createPortalInfo(ServerLevel server, BlockUtil.FoundRectangle portal, Direction.Axis axis, Vec3 relativePos, Entity entity, Vec3 velocity, float yRot, float xRot) {
		BlockPos blockpos = portal.minCorner;
		BlockState blockstate = server.getBlockState(blockpos);
		Direction.Axis direction$axis = blockstate.getOptionalValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_AXIS).orElse(Direction.Axis.X);

		double d0 = 2.0;
		double d1 = 3.0;
		int i = axis == direction$axis ? 0 : 90;
		Vec3 vector3d = axis == direction$axis ? velocity : new Vec3(velocity.z, velocity.y, -velocity.x);
		double d2 = d0 - (double) entity.getBbWidth();
		double d3 = d1 - (double) entity.getBbHeight();
		BlockPos blockpos1 = blockpos.relative(direction$axis, (int)(d2 / 2.0D + 0.5D));
		return new PortalInfo(new Vec3((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D + d3 * relativePos.y(), (double) blockpos1.getZ() + 0.5D), vector3d, yRot + (float) i, xRot);
	}

	private BlockPos calculateBottomLeft(BlockPos pos) {
		for (int i = Math.max(this.level.getMinBuildHeight(), pos.getY() - 21); pos.getY() > i && this.isEmpty(this.level.getBlockState(pos.below())); pos = pos.below()) {
		}
		Direction direction = this.rightDir.getOpposite();
		int j = this.getDistanceUntilEdge(pos, direction) - 1;
		return j < 0 ? null : pos.relative(direction, j);
	}

	private int calculateWidth() {
		int i = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);
		return i >= 2 && i <= 21 ? i : 0;
	}

	private int getDistanceUntilEdge(BlockPos pos, Direction direction) {
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
		for (int i = 0; i <= 21; ++i) {
			blockpos$mutableblockpos.set(pos).move(direction, i);
			BlockState blockstate = this.level.getBlockState(blockpos$mutableblockpos);
			if (!this.isEmpty(blockstate)) {
				if (blockstate.is(EventjarModBlocks.WIERD_PORTAL.get())) {
					return i;
				}
				break;
			}
			BlockState blockstate1 = this.level.getBlockState(blockpos$mutableblockpos.move(Direction.DOWN));
			if (!this.isEmpty(blockstate1)) {
				break;
			}
		}
		return 0;
	}

	private int calculateHeight() {
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
		int i = this.getDistanceUntilTop(blockpos$mutableblockpos);
		return i >= 3 && i <= 21 && this.hasTopEdge(blockpos$mutableblockpos, i) ? i : 0;
	}

	private int getDistanceUntilTop(BlockPos.MutableBlockPos pos) {
		for (int i = 0; i < 21; ++i) {
			pos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, -1);
			if (!this.isEmpty(this.level.getBlockState(pos))) {
				return i;
			}
			pos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, this.width);
			if (!this.isEmpty(this.level.getBlockState(pos))) {
				return i;
			}
			for (int j = 0; j < this.width; ++j) {
				pos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, j);
				BlockState blockstate = this.level.getBlockState(pos);
				if (!this.isEmpty(blockstate)) {
					return i;
				}
				if (blockstate.is(EventjarModBlocks.WIERD_PORTAL.get())) {
					++this.numPortalBlocks;
				}
			}
		}
		return 21;
	}

	private boolean hasTopEdge(BlockPos.MutableBlockPos pos, int upDistance) {
		for (int i = 0; i < this.width; ++i) {
			BlockPos.MutableBlockPos blockpos$mutableblockpos = pos.set(this.bottomLeft).move(Direction.UP, upDistance).move(this.rightDir, i);
			if (!this.isEmpty(this.level.getBlockState(blockpos$mutableblockpos))) {
				return false;
			}
		}
		return true;
	}

	private boolean isEmpty(BlockState state) {
		return state.isAir() || state.is(EventjarModBlocks.WIERD_PORTAL.get());
	}

	public boolean isValid() {
		return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
	}

	public void createPortalBlocks() {
		BlockState blockstate = EventjarModBlocks.WIERD_PORTAL.get().defaultBlockState().setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_AXIS, this.axis);
		BlockPos.betweenClosed(this.bottomLeft, this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1)).forEach((pos) -> {
			this.level.setBlock(pos, blockstate, 18);
		});
	}

	public boolean isComplete() {
		return this.isValid() && this.numPortalBlocks == this.width * this.height;
	}

	public Direction getRightDir() {
		return this.rightDir;
	}
}
