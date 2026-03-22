
package net.lixis9.eventjar.world.teleporter;

import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.util.ITeleporter;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Vec3i;
import net.minecraft.core.Holder;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.BlockUtil;

import net.lixis9.eventjar.init.EventjarModBlocks;

import java.util.function.Function;
import java.util.Optional;

import com.google.common.collect.ImmutableSet;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class WierdTeleporter implements ITeleporter {
	public static final TicketType<BlockPos> CUSTOM_PORTAL = TicketType.create("wierd_portal", Vec3i::compareTo, 300);
	public static Holder<PoiType> poi = null;

	@SubscribeEvent
	public static void registerPointOfInterest(RegisterEvent event) {
		event.register(ForgeRegistries.Keys.POI_TYPES, registerHelper -> {
			PoiType poiType = new PoiType(ImmutableSet.copyOf(EventjarModBlocks.WIERD_PORTAL.get().getStateDefinition().getPossibleStates()), 0, 1);
			registerHelper.register("wierd_portal", poiType);
			poi = ForgeRegistries.POI_TYPES.getHolder(poiType).get();
		});
	}

	private final ServerLevel level;
	private final BlockPos entityEnterPos;

	public WierdTeleporter(ServerLevel worldServer, BlockPos entityEnterPos) {
		this.level = worldServer;
		this.entityEnterPos = entityEnterPos;
	}

	/* failed to load code for net.minecraft.world.level.portal.PortalForcer */
	/* failed to load code for net.minecraft.world.level.portal.PortalForcer */
	/* failed to load code for net.minecraft.world.level.portal.PortalForcer */
	@Override
	public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel server, float yaw, Function<Boolean, Entity> repositionEntity) {
		PortalInfo portalinfo = getPortalInfo(entity, server);
		if (entity instanceof ServerPlayer player) {
			player.setServerLevel(server);
			server.addDuringPortalTeleport(player);
			player.connection.teleport(portalinfo.pos.x, portalinfo.pos.y, portalinfo.pos.z, portalinfo.yRot, portalinfo.xRot);
			player.connection.resetPosition();
			CriteriaTriggers.CHANGED_DIMENSION.trigger(player, currentWorld.dimension(), server.dimension());
			return entity;
		} else {
			Entity entityNew = entity.getType().create(server);
			if (entityNew != null) {
				entityNew.restoreFrom(entity);
				entityNew.moveTo(portalinfo.pos.x, portalinfo.pos.y, portalinfo.pos.z, portalinfo.yRot, entityNew.getXRot());
				entityNew.setDeltaMovement(portalinfo.speed);
				server.addDuringTeleport(entityNew);
			}
			return entityNew;
		}
	}

	private PortalInfo getPortalInfo(Entity entity, ServerLevel server) {
		WorldBorder worldborder = server.getWorldBorder();
		double d0 = DimensionType.getTeleportationScale(entity.level().dimensionType(), server.dimensionType());
		BlockPos blockpos1 = worldborder.clampToBounds(entity.getX() * d0, entity.getY(), entity.getZ() * d0);
		return this.getExitPortal(entity, blockpos1, worldborder).map(repositioner -> {
			BlockState blockstate = entity.level().getBlockState(this.entityEnterPos);
			Direction.Axis direction$axis;
			Vec3 vector3d;
			if (blockstate.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
				direction$axis = blockstate.getValue(BlockStateProperties.HORIZONTAL_AXIS);
				BlockUtil.FoundRectangle teleportationrepositioner$result = BlockUtil.getLargestRectangleAround(this.entityEnterPos, direction$axis, 21, Direction.Axis.Y, 21, pos -> entity.level().getBlockState(pos) == blockstate);
				vector3d = WierdPortalShape.getRelativePosition(teleportationrepositioner$result, direction$axis, entity.position(), entity.getDimensions(entity.getPose()));
			} else {
				direction$axis = Direction.Axis.X;
				vector3d = new Vec3(0.5, 0, 0);
			}
			return WierdPortalShape.createPortalInfo(server, repositioner, direction$axis, vector3d, entity, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot());
		}).orElse(new PortalInfo(entity.position(), Vec3.ZERO, entity.getYRot(), entity.getXRot()));
	}

	protected Optional<BlockUtil.FoundRectangle> getExitPortal(Entity entity, BlockPos pos, WorldBorder worldBorder) {
		Optional<BlockUtil.FoundRectangle> optional = this.findPortalAround(pos, false, worldBorder);
		if (entity instanceof ServerPlayer) {
			if (optional.isPresent()) {
				return optional;
			} else {
				Direction.Axis direction$axis = entity.level().getBlockState(this.entityEnterPos).getOptionalValue(NetherPortalBlock.AXIS).orElse(Direction.Axis.X);
				return this.createPortal(pos, direction$axis);
			}
		} else {
			return optional;
		}
	}

	private boolean canPortalReplaceBlock(BlockPos.MutableBlockPos pos) {
		BlockState blockstate = this.level.getBlockState(pos);
		return blockstate.canBeReplaced() && blockstate.getFluidState().isEmpty();
	}

	protected Optional<BlockUtil.FoundRectangle> findPortalAround(BlockPos pos, boolean isNether, WorldBorder worldBorder) {
		// Simplified portal finding - just search for existing portal shapes
		Direction.Axis direction$axis = Direction.Axis.X;
		Optional<WierdPortalShape> optional1 = WierdPortalShape.findPortalShape(this.level, pos, (portalShape) -> portalShape.isValid() && portalShape.numPortalBlocks == 0, direction$axis);
		if (optional1.isPresent()) {
			WierdPortalShape portalShape = optional1.get();
			BlockPos blockpos = portalShape.bottomLeft;
			if (worldBorder.isWithinBounds(blockpos) && worldBorder.isWithinBounds(blockpos.relative(portalShape.getRightDir(), portalShape.width - 1))) {
				for (int i = 0; i < portalShape.width; ++i) {
					BlockPos blockpos1 = blockpos.relative(portalShape.getRightDir(), i);
					if (!this.canPortalReplaceBlock(new BlockPos.MutableBlockPos().set(blockpos1))) {
						return Optional.empty();
					}
				}
				return Optional.of(new BlockUtil.FoundRectangle(blockpos, portalShape.width, portalShape.height));
			}
		}
		
		Direction.Axis direction$axis1 = Direction.Axis.Z;
		Optional<WierdPortalShape> optional2 = WierdPortalShape.findPortalShape(this.level, pos, (portalShape) -> portalShape.isValid() && portalShape.numPortalBlocks == 0, direction$axis1);
		if (optional2.isPresent()) {
			WierdPortalShape portalShape = optional2.get();
			BlockPos blockpos = portalShape.bottomLeft;
			if (worldBorder.isWithinBounds(blockpos) && worldBorder.isWithinBounds(blockpos.relative(portalShape.getRightDir(), portalShape.width - 1))) {
				for (int i = 0; i < portalShape.width; ++i) {
					BlockPos blockpos1 = blockpos.relative(portalShape.getRightDir(), i);
					if (!this.canPortalReplaceBlock(new BlockPos.MutableBlockPos().set(blockpos1))) {
						return Optional.empty();
					}
				}
				return Optional.of(new BlockUtil.FoundRectangle(blockpos, portalShape.width, portalShape.height));
			}
		}
		
		return Optional.empty();
	}

	protected Optional<BlockUtil.FoundRectangle> createPortal(BlockPos pos, Direction.Axis axis) {
		Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis);
		double d0 = -1.0D;
		BlockPos blockpos = null;
		double d1 = -1.0D;
		BlockPos blockpos1 = null;
		WorldBorder worldborder = this.level.getWorldBorder();
		int i = Math.min(this.level.getMaxBuildHeight(), this.level.getMinBuildHeight() + this.level.getLogicalHeight()) - 1;
		BlockPos.MutableBlockPos blockpos$mutableblockpos = pos.mutable();
		for (BlockPos.MutableBlockPos blockpos$mutableblockpos1 : BlockPos.spiralAround(pos, 16, Direction.EAST, Direction.SOUTH)) {
			int j = Math.min(i, this.level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, blockpos$mutableblockpos1.getX(), blockpos$mutableblockpos1.getZ()));
			int k = 1;
			if (worldborder.isWithinBounds(blockpos$mutableblockpos1) && worldborder.isWithinBounds(blockpos$mutableblockpos1.move(direction, 1))) {
				blockpos$mutableblockpos1.move(direction.getOpposite(), 1);
				for (int l = j; l >= this.level.getMinBuildHeight(); --l) {
					blockpos$mutableblockpos1.setY(l);
					if (this.level.isEmptyBlock(blockpos$mutableblockpos1)) {
						int i1;
						for (i1 = l; l > this.level.getMinBuildHeight() && this.level.isEmptyBlock(blockpos$mutableblockpos1.move(Direction.DOWN)); --l) {
						}
						if (l + 4 <= i) {
							int j1 = i1 - l;
							if (j1 <= 0 || j1 >= 3) {
								blockpos$mutableblockpos1.setY(l);
								if (this.canPortalReplaceBlock(blockpos$mutableblockpos1)) {
									double d2 = pos.distSqr(blockpos$mutableblockpos1);
									if (this.canPortalReplaceBlock(blockpos$mutableblockpos1) && (d0 == -1.0D || d0 > d2)) {
										d0 = d2;
										blockpos = blockpos$mutableblockpos1.immutable();
									}
									if (d0 == -1.0D && (d1 == -1.0D || d1 > d2)) {
										d1 = d2;
										blockpos1 = blockpos$mutableblockpos1.immutable();
									}
								}
							}
						}
					}
				}
			}
		}
		if (d0 == -1.0D && d1 != -1.0D) {
			blockpos = blockpos1;
			d0 = d1;
		}
		if (d0 == -1.0D) {
			blockpos = (new BlockPos(pos.getX(), net.minecraft.util.Mth.clamp(pos.getY(), this.level.getMinBuildHeight(), this.level.getMinBuildHeight() + this.level.getLogicalHeight() - 1), pos.getZ())).immutable();
			Direction direction1 = direction.getClockWise();
			if (!worldborder.isWithinBounds(blockpos)) {
				return Optional.empty();
			}
			for (int i2 = -1; i2 < 2; ++i2) {
				for (int j2 = 0; j2 < 2; ++j2) {
					for (int k2 = -1; k2 < 3; ++k2) {
						BlockState blockstate1 = k2 < 0 ? EventjarModBlocks.WIERD_PORTAL.get().defaultBlockState() : net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
						blockpos$mutableblockpos.setWithOffset(blockpos, j2 * direction.getStepX() + i2 * direction1.getStepX(), k2, j2 * direction.getStepZ() + i2 * direction1.getStepZ());
						this.level.setBlock(blockpos$mutableblockpos, blockstate1, 3);
					}
				}
			}
		}
		for (int l1 = -1; l1 < 3; ++l1) {
			for (int i3 = -1; i3 < 4; ++i3) {
				if (l1 == -1 || l1 == 2 || i3 == -1 || i3 == 3) {
					blockpos$mutableblockpos.setWithOffset(blockpos, l1 * direction.getStepX(), i3, l1 * direction.getStepZ());
					this.level.setBlock(blockpos$mutableblockpos, net.minecraft.world.level.block.Blocks.OBSIDIAN.defaultBlockState(), 3);
				}
			}
		}
		BlockState blockstate = EventjarModBlocks.WIERD_PORTAL.get().defaultBlockState().setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_AXIS, axis);
		for (int j3 = 0; j3 < 2; ++j3) {
			for (int k3 = 0; k3 < 3; ++k3) {
				blockpos$mutableblockpos.setWithOffset(blockpos, j3 * direction.getStepX(), k3, j3 * direction.getStepZ());
				this.level.setBlock(blockpos$mutableblockpos, blockstate, 18);
			}
		}
		return Optional.of(new BlockUtil.FoundRectangle(blockpos.immutable(), 2, 3));
	}
}
