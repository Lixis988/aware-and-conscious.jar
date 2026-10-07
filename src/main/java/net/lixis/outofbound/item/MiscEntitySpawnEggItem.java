package net.lixis.outofbound.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.function.Supplier;

public class MiscEntitySpawnEggItem extends Item {

	private final Supplier<? extends EntityType<? extends Entity>> entityTypeSupplier;
	private final int backgroundColor;
	private final int highlightColor;

	public MiscEntitySpawnEggItem(Supplier<? extends EntityType<? extends Entity>> entityTypeSupplier, int backgroundColor,
			int highlightColor, Properties properties) {
		super(properties);
		this.entityTypeSupplier = entityTypeSupplier;
		this.backgroundColor = backgroundColor;
		this.highlightColor = highlightColor;
	}

	public int getBackgroundColor() {
		return backgroundColor;
	}

	public int getHighlightColor() {
		return highlightColor;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		if (!(level instanceof ServerLevel serverLevel)) {
			return InteractionResult.SUCCESS;
		}

		EntityType<? extends Entity> entityType = entityTypeSupplier.get();
		Entity entity = entityType.create(serverLevel);
		if (entity == null) {
			return InteractionResult.FAIL;
		}

		BlockPos pos = context.getClickedPos();
		Direction direction = context.getClickedFace();
		BlockPos spawnPos = pos.relative(direction);
		entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, 0.0F, 0.0F);
		serverLevel.addFreshEntity(entity);
		level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, spawnPos);

		if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
			context.getItemInHand().shrink(1);
		}

		return InteractionResult.CONSUME;
	}
}
