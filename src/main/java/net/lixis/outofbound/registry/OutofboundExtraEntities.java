package net.lixis.outofbound.registry;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.entity.BackTeethmanEntity;
import net.lixis.outofbound.entity.BlackSquareEntity;
import net.lixis.outofbound.entity.DarkEyeEntity;
import net.lixis.outofbound.entity.Entity1Entity;
import net.lixis.outofbound.entity.Entity2Entity;
import net.lixis.outofbound.entity.Entity3Entity;
import net.lixis.outofbound.entity.HeartDecorEntity;
import net.lixis.outofbound.entity.NotexturemanEntity;
import net.lixis.outofbound.entity.OverworldNotexturemanEntity;
import net.lixis.outofbound.entity.OverworldTeethmanEntity;
import net.lixis.outofbound.entity.SeraphEyeEntity;
import net.lixis.outofbound.entity.SeraphWrathEntity;
import net.lixis.outofbound.entity.SkyFigureEntity;
import net.lixis.outofbound.entity.SkyPainterEntity;
import net.lixis.outofbound.entity.TeethmanEntity;
import net.lixis.outofbound.entity.TheSunEntity;
import net.lixis.outofbound.entity.UnknownEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class OutofboundExtraEntities {

	public static final DeferredRegister<EntityType<?>> REGISTRY =
			DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, OutofboundMod.MODID);

	public static final RegistryObject<EntityType<Entity1Entity>> ENTITY1 = register("entity1",
			EntityType.Builder.<Entity1Entity>of(Entity1Entity::new, MobCategory.MONSTER)
					.setShouldReceiveVelocityUpdates(true)
					.setTrackingRange(160)
					.setUpdateInterval(3)
					.setCustomClientFactory(Entity1Entity::new)
					.sized(1.2f, 2.6f));

	public static final RegistryObject<EntityType<Entity2Entity>> ENTITY2 = register("entity2",
			EntityType.Builder.<Entity2Entity>of(Entity2Entity::new, MobCategory.MONSTER)
					.setShouldReceiveVelocityUpdates(true)
					.setTrackingRange(160)
					.setUpdateInterval(3)
					.setCustomClientFactory(Entity2Entity::new)
					.sized(1.1f, 2.3f));

	public static final RegistryObject<EntityType<Entity3Entity>> ENTITY3 = register("entity3",
			EntityType.Builder.<Entity3Entity>of(Entity3Entity::new, MobCategory.MONSTER)
					.setShouldReceiveVelocityUpdates(true)
					.setTrackingRange(160)
					.setUpdateInterval(3)
					.setCustomClientFactory(Entity3Entity::new)
					.sized(0.6f, 1.8f));

	public static final RegistryObject<EntityType<DarkEyeEntity>> DARK_EYE = register("dark_eye",
			EntityType.Builder.<DarkEyeEntity>of(DarkEyeEntity::new, MobCategory.MISC)
					.setShouldReceiveVelocityUpdates(false)
					.setTrackingRange(96)
					.setUpdateInterval(20)
					.sized(0.5f, 0.5f));

	public static final RegistryObject<EntityType<BlackSquareEntity>> BLACK_SQUARE = register("black_square",
			EntityType.Builder.<BlackSquareEntity>of(BlackSquareEntity::new, MobCategory.MONSTER)
					.setShouldReceiveVelocityUpdates(true)
					.setTrackingRange(160)
					.setUpdateInterval(3)
					.setCustomClientFactory(BlackSquareEntity::new)
					.sized(1.0f, 1.0f));

	public static final RegistryObject<EntityType<SkyFigureEntity>> SKY_FIGURE = register("sky_figure",
			EntityType.Builder.<SkyFigureEntity>of(SkyFigureEntity::new, MobCategory.MISC)
					.setShouldReceiveVelocityUpdates(false)
					.setTrackingRange(160)
					.setUpdateInterval(20)
					.sized(1.0f, 1.0f));

	public static final RegistryObject<EntityType<SeraphEyeEntity>> SERAPH_EYE = register("seraph_eye",
			EntityType.Builder.<SeraphEyeEntity>of(SeraphEyeEntity::new, MobCategory.MISC)
					.setShouldReceiveVelocityUpdates(true)
					.setTrackingRange(160)
					.setUpdateInterval(1)
					.sized(2.4f, 3.2f));

	public static final RegistryObject<EntityType<SeraphWrathEntity>> SERAPH_WRATH = register("seraph_wrath",
			EntityType.Builder.<SeraphWrathEntity>of(SeraphWrathEntity::new, MobCategory.MONSTER)
					.setShouldReceiveVelocityUpdates(true)
					.setTrackingRange(1000)
					.setUpdateInterval(3)
					.setCustomClientFactory(SeraphWrathEntity::new)
					.fireImmune()
					.sized(1.6f, 1.6f));

	public static final RegistryObject<EntityType<TheSunEntity>> THE_SUN = register("the_sun",
			EntityType.Builder.<TheSunEntity>of(TheSunEntity::new, MobCategory.MISC)
					.setShouldReceiveVelocityUpdates(true)
					.setTrackingRange(256)
					.setUpdateInterval(1)
					.sized(3.0f, 3.0f));

	public static final RegistryObject<EntityType<BackTeethmanEntity>> BACK_TEETHMAN = register("back_teethman",
			EntityType.Builder.<BackTeethmanEntity>of(BackTeethmanEntity::new, MobCategory.MISC)
					.setShouldReceiveVelocityUpdates(true)
					.setTrackingRange(160)
					.setUpdateInterval(1)
					.sized(0.9f, 2.0f));

	public static final RegistryObject<EntityType<TeethmanEntity>> TEETHMAN = register("teethman",
			EntityType.Builder.<TeethmanEntity>of(TeethmanEntity::new, MobCategory.MONSTER)
					.setShouldReceiveVelocityUpdates(true)
					.setTrackingRange(160)
					.setUpdateInterval(3)
					.setCustomClientFactory(TeethmanEntity::new)
					.sized(0.9f, 2f));

	public static final RegistryObject<EntityType<OverworldTeethmanEntity>> OVERWORLD_TEETHMAN = register("overworld_teethman",
			EntityType.Builder.<OverworldTeethmanEntity>of(OverworldTeethmanEntity::new, MobCategory.MONSTER)
					.setShouldReceiveVelocityUpdates(true)
					.setTrackingRange(160)
					.setUpdateInterval(3)
					.setCustomClientFactory(OverworldTeethmanEntity::new)
					.sized(0.9f, 2f));

	public static final RegistryObject<EntityType<SkyPainterEntity>> SKY_PAINTER = register("sky_painter",
			EntityType.Builder.<SkyPainterEntity>of(SkyPainterEntity::new, MobCategory.MISC)
					.setShouldReceiveVelocityUpdates(false)
					.setTrackingRange(256)
					.setUpdateInterval(1)
					.sized(1.0f, 1.0f));

	public static final RegistryObject<EntityType<HeartDecorEntity>> HEART_DECOR = register("heart_decor",
			EntityType.Builder.<HeartDecorEntity>of(HeartDecorEntity::new, MobCategory.MISC)
					.setShouldReceiveVelocityUpdates(false)
					.setTrackingRange(64)
					.setUpdateInterval(10)
					.setCustomClientFactory(HeartDecorEntity::new)
					.sized(1.2f, 1.8f)
					.fireImmune()
					.noSummon());

	public static final RegistryObject<EntityType<NotexturemanEntity>> NOTEXTUREMAN = register("notextureman",
			EntityType.Builder.<NotexturemanEntity>of(NotexturemanEntity::new, MobCategory.MISC)
					.setShouldReceiveVelocityUpdates(true)
					.setTrackingRange(96)
					.setUpdateInterval(3)
					.setCustomClientFactory(NotexturemanEntity::new)
					.sized(0.9f, 2.0f));

	public static final RegistryObject<EntityType<OverworldNotexturemanEntity>> OVERWORLD_NOTEXTUREMAN = register("overworld_notextureman",
			EntityType.Builder.<OverworldNotexturemanEntity>of(OverworldNotexturemanEntity::new, MobCategory.MONSTER)
					.setShouldReceiveVelocityUpdates(true)
					.setTrackingRange(96)
					.setUpdateInterval(3)
					.setCustomClientFactory(OverworldNotexturemanEntity::new)
					.sized(0.9f, 2.0f));

	public static final RegistryObject<EntityType<UnknownEntity>> UNKNOWN = register("unknown",
			EntityType.Builder.<UnknownEntity>of(UnknownEntity::new, MobCategory.MONSTER)
					.setShouldReceiveVelocityUpdates(true)
					.setTrackingRange(160)
					.setUpdateInterval(3)
					.setCustomClientFactory(UnknownEntity::new)
					.sized(1.8f, 3.8f));

	private OutofboundExtraEntities() {
	}

	private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> builder) {
		return REGISTRY.register(name, () -> (EntityType<T>) builder.build(name));
	}

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			Entity1Entity.init();
			Entity2Entity.init();
			Entity3Entity.init();
			TeethmanEntity.init();
			SeraphWrathEntity.init();
		});
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(ENTITY1.get(), Entity1Entity.createAttributes().build());
		event.put(ENTITY2.get(), Entity2Entity.createAttributes().build());
		event.put(ENTITY3.get(), Entity3Entity.createAttributes().build());
		event.put(BLACK_SQUARE.get(), BlackSquareEntity.createAttributes().build());
		event.put(SERAPH_WRATH.get(), SeraphWrathEntity.createAttributes().build());
		event.put(TEETHMAN.get(), TeethmanEntity.createAttributes().build());
		event.put(OVERWORLD_TEETHMAN.get(), OverworldTeethmanEntity.createAttributes().build());
		event.put(NOTEXTUREMAN.get(), NotexturemanEntity.createAttributes().build());
		event.put(OVERWORLD_NOTEXTUREMAN.get(), OverworldNotexturemanEntity.createAttributes().build());
		event.put(UNKNOWN.get(), UnknownEntity.createAttributes().build());
	}
}
