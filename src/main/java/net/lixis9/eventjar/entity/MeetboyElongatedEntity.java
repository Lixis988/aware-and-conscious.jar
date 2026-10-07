package net.lixis9.eventjar.entity;

import net.lixis9.eventjar.init.EventjarModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;

public class MeetboyElongatedEntity extends AbstractWarpedMeetboyEntity {

	public MeetboyElongatedEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(EventjarModEntities.MEETBOY_ELONGATED.get(), world);
	}

	public MeetboyElongatedEntity(EntityType<? extends MeetboyElongatedEntity> type, Level world) {
		super(type, world, "§kmeatboy!!");
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		return createWarpedAttributes();
	}
}
