package net.lixis.outofbound.entity;

import net.lixis.outofbound.BoundedcowEjectHandler;
import net.lixis.outofbound.entity.ai.BoundedcowChasePlayerGoal;
import net.lixis.outofbound.init.OutofboundModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class BoundedcowEntity extends Cow {
	public BoundedcowEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(OutofboundModEntities.BOUNDEDCOW.get(), world);
	}

	public BoundedcowEntity(EntityType<BoundedcowEntity> type, Level world) {
		super(type, world);
		setMaxUpStep(1.0F);
		xpReward = 0;
		setNoAi(false);
		setCustomName(Component.literal("Bounded Cow"));
		setCustomNameVisible(true);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new BoundedcowChasePlayerGoal(this));
	}

	@Override
	public void playerTouch(Player player) {
		super.playerTouch(player);
		BoundedcowEjectHandler.onPlayerTouch(player, this);
	}

	@Override
	public boolean onClimbable() {
		return this.horizontalCollision;
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	public double getPassengersRidingOffset() {
		return super.getPassengersRidingOffset() + 1.5;
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.85);
		builder = builder.add(Attributes.MAX_HEALTH, 10);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 6);
		builder = builder.add(Attributes.FOLLOW_RANGE, 64);
		return builder;
	}
}
