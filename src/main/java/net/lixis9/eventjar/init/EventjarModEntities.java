package net.lixis9.eventjar.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;

import net.lixis9.eventjar.entity.WhoamiEntity;
import net.lixis9.eventjar.entity.WatcherEntity;
import net.lixis9.eventjar.entity.SonOfgodEntity;
import net.lixis9.eventjar.entity.SeekeractEntity;
import net.lixis9.eventjar.entity.SeekerEntity;
import net.lixis9.eventjar.entity.ScavengerEntity;
import net.lixis9.eventjar.entity.RageThen60sEntity;
import net.lixis9.eventjar.entity.PlayerEntity;
import net.lixis9.eventjar.entity.NoiseEntityEntity;
import net.lixis9.eventjar.entity.MeetboyfastEntity;
import net.lixis9.eventjar.entity.MeetboyLongEntity;
import net.lixis9.eventjar.entity.MeetboyEntity;
import net.lixis9.eventjar.entity.MeatboydistortedEntity;
import net.lixis9.eventjar.entity.Lixis9Entity;
import net.lixis9.eventjar.entity.InvisibleWeirdEntityEntity;
import net.lixis9.eventjar.entity.InvisibleDistEntity;
import net.lixis.outofbound.entity.SeraphWrathEntity;
import net.lixis9.eventjar.entity.MeetboyGlitchEntity;
import net.lixis9.eventjar.entity.MeetboyElongatedEntity;
import net.lixis9.eventjar.entity.MeetboyStubbyEntity;
import net.lixis9.eventjar.entity.FaultEntity;
import net.lixis9.eventjar.entity.EyesindarkEntity;
import net.lixis9.eventjar.entity.EyesEntity;
import net.lixis9.eventjar.entity.EyeEntity;
import net.lixis9.eventjar.entity.ErrundefineEntity;
import net.lixis9.eventjar.entity.EntitywhowatchyouEntity;
import net.lixis9.eventjar.entity.EntityCentipedeEntity;
import net.lixis9.eventjar.entity.Entity000125Entity;
import net.lixis9.eventjar.entity.CavemeetboyEntity;
import net.lixis9.eventjar.entity.CaretakerEntity;
import net.lixis9.eventjar.entity.ScaryDefaultVillagerEntity;
import net.lixis9.eventjar.entity.DefaultVillagerEntity;
import net.lixis9.eventjar.entity.VillagerMimicEntity;
import net.lixis9.eventjar.entity.MimicTendrilEntity;
import net.lixis9.eventjar.EventjarMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventjarModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, EventjarMod.MODID);
	public static final RegistryObject<EntityType<MeetboyEntity>> MEETBOY = register("meetboy",
			EntityType.Builder.<MeetboyEntity>of(MeetboyEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(MeetboyEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<MeetboyGlitchEntity>> MEETBOY_GLITCH = register("meetboy_glitch",
			EntityType.Builder.<MeetboyGlitchEntity>of(MeetboyGlitchEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(MeetboyGlitchEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<MeetboyElongatedEntity>> MEETBOY_ELONGATED = register("meetboy_elongated",
			EntityType.Builder.<MeetboyElongatedEntity>of(MeetboyElongatedEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(MeetboyElongatedEntity::new).fireImmune().sized(0.55f, 2.6f));
	public static final RegistryObject<EntityType<MeetboyStubbyEntity>> MEETBOY_STUBBY = register("meetboy_stubby",
			EntityType.Builder.<MeetboyStubbyEntity>of(MeetboyStubbyEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(MeetboyStubbyEntity::new).fireImmune().sized(0.75f, 1.15f));
	public static final RegistryObject<EntityType<WhoamiEntity>> WHOAMI = register("whoami",
			EntityType.Builder.<WhoamiEntity>of(WhoamiEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(200).setUpdateInterval(3).setCustomClientFactory(WhoamiEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<ErrundefineEntity>> ERRUNDEFINE = register("errundefine", EntityType.Builder.<ErrundefineEntity>of(ErrundefineEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(500).setUpdateInterval(3).setCustomClientFactory(ErrundefineEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<Entity000125Entity>> ENTITY_000125 = register("entity_000125", EntityType.Builder.<Entity000125Entity>of(Entity000125Entity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(200).setUpdateInterval(3).setCustomClientFactory(Entity000125Entity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<EntitywhowatchyouEntity>> ENTITYWHOWATCHYOU = register("entitywhowatchyou", EntityType.Builder.<EntitywhowatchyouEntity>of(EntitywhowatchyouEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(200).setUpdateInterval(3).setCustomClientFactory(EntitywhowatchyouEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<EntityCentipedeEntity>> ENTITY_CENTIPEDE = register("entity_centipede", EntityType.Builder.<EntityCentipedeEntity>of(EntityCentipedeEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(200).setUpdateInterval(3).setCustomClientFactory(EntityCentipedeEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<Lixis9Entity>> LIXIS_9 = register("lixis_9",
			EntityType.Builder.<Lixis9Entity>of(Lixis9Entity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(1000).setUpdateInterval(3).setCustomClientFactory(Lixis9Entity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<RageThen60sEntity>> RAGE_THEN_60S = register("rage_then_60s", EntityType.Builder.<RageThen60sEntity>of(RageThen60sEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(1000).setUpdateInterval(3).setCustomClientFactory(RageThen60sEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<MeetboyfastEntity>> MEETBOYFAST = register("meetboyfast", EntityType.Builder.<MeetboyfastEntity>of(MeetboyfastEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(MeetboyfastEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<CaretakerEntity>> CARETAKER = register("caretaker", EntityType.Builder.<CaretakerEntity>of(CaretakerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(1000)
			.setUpdateInterval(3).setCustomClientFactory(CaretakerEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<SeraphWrathEntity>> GOD = register("god",
			EntityType.Builder.<SeraphWrathEntity>of(SeraphWrathEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(1000).setUpdateInterval(3)
					.setCustomClientFactory((spawn, level) -> new SeraphWrathEntity(EventjarModEntities.GOD.get(), level)).fireImmune().sized(1.6f, 1.6f));
	public static final RegistryObject<EntityType<PlayerEntity>> PLAYER = register("player",
			EntityType.Builder.<PlayerEntity>of(PlayerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(1000).setUpdateInterval(3).setCustomClientFactory(PlayerEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<EyesEntity>> EYES = register("eyes",
			EntityType.Builder.<EyesEntity>of(EyesEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(EyesEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<EyeEntity>> EYE = register("eye",
			EntityType.Builder.<EyeEntity>of(EyeEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(EyeEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<CavemeetboyEntity>> CAVEMEETBOY = register("cavemeetboy", EntityType.Builder.<CavemeetboyEntity>of(CavemeetboyEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(CavemeetboyEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<InvisibleWeirdEntityEntity>> INVISIBLE_WEIRD_ENTITY = register("invisible_weird_entity", EntityType.Builder.<InvisibleWeirdEntityEntity>of(InvisibleWeirdEntityEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(InvisibleWeirdEntityEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<NoiseEntityEntity>> NOISE_ENTITY = register("noise_entity", EntityType.Builder.<NoiseEntityEntity>of(NoiseEntityEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(NoiseEntityEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<InvisibleDistEntity>> INVISIBLE_DIST = register("invisible_dist", EntityType.Builder.<InvisibleDistEntity>of(InvisibleDistEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(InvisibleDistEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<SeekerEntity>> SEEKER = register("seeker",
			EntityType.Builder.<SeekerEntity>of(SeekerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(3000).setUpdateInterval(3).setCustomClientFactory(SeekerEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<SeekeractEntity>> SEEKERACT = register("seekeract", EntityType.Builder.<SeekeractEntity>of(SeekeractEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(1000)
			.setUpdateInterval(3).setCustomClientFactory(SeekeractEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<WatcherEntity>> WATCHER = register("watcher",
			EntityType.Builder.<WatcherEntity>of(WatcherEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(1000).setUpdateInterval(3).setCustomClientFactory(WatcherEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<MeetboyLongEntity>> MEETBOY_LONG = register("meetboy_long", EntityType.Builder.<MeetboyLongEntity>of(MeetboyLongEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(MeetboyLongEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<FaultEntity>> FAULT = register("fault",
			EntityType.Builder.<FaultEntity>of(FaultEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(FaultEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<ScavengerEntity>> SCAVENGER = register("scavenger",
			EntityType.Builder.<ScavengerEntity>of(ScavengerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ScavengerEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<MeatboydistortedEntity>> MEATBOYDISTORTED = register("meatboydistorted", EntityType.Builder.<MeatboydistortedEntity>of(MeatboydistortedEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(MeatboydistortedEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<EyesindarkEntity>> EYESINDARK = register("eyesindark",
			EntityType.Builder.<EyesindarkEntity>of(EyesindarkEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(EyesindarkEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<SonOfgodEntity>> SON_OFGOD = register("son_ofgod",
			EntityType.Builder.<SonOfgodEntity>of(SonOfgodEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(SonOfgodEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<DefaultVillagerEntity>> DEFAULT_VILLAGER = register("default_villager",
			EntityType.Builder.<DefaultVillagerEntity>of(DefaultVillagerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(1000).setUpdateInterval(3).setCustomClientFactory(DefaultVillagerEntity::new)
					.sized(0.6f, 1.95f));
	public static final RegistryObject<EntityType<ScaryDefaultVillagerEntity>> SCARY_DEFAULT_VILLAGER = register("scary_default_villager",
			EntityType.Builder.<ScaryDefaultVillagerEntity>of(ScaryDefaultVillagerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(1000).setUpdateInterval(3)
					.setCustomClientFactory(ScaryDefaultVillagerEntity::new)
					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<VillagerMimicEntity>> VILLAGER_MIMIC = register("villager_mimic",
			EntityType.Builder.<VillagerMimicEntity>of(VillagerMimicEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)
					.setCustomClientFactory(VillagerMimicEntity::new).sized(0.6f, 1.95f));
	public static final RegistryObject<EntityType<MimicTendrilEntity>> MIMIC_TENDRIL = register("mimic_tendril",
			EntityType.Builder.<MimicTendrilEntity>of(MimicTendrilEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)
					.setCustomClientFactory(MimicTendrilEntity::new).fireImmune().sized(0.45f, 3.6f));

	private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			MeetboyEntity.init();
			MeetboyGlitchEntity.init();
			MeetboyElongatedEntity.init();
			MeetboyStubbyEntity.init();
			WhoamiEntity.init();
			ErrundefineEntity.init();
			Entity000125Entity.init();
			EntitywhowatchyouEntity.init();
			EntityCentipedeEntity.init();
			Lixis9Entity.init();
			RageThen60sEntity.init();
			MeetboyfastEntity.init();
			CaretakerEntity.init();
			SeraphWrathEntity.init();
			PlayerEntity.init();
			EyesEntity.init();
			EyeEntity.init();
			CavemeetboyEntity.init();
			InvisibleWeirdEntityEntity.init();
			NoiseEntityEntity.init();
			InvisibleDistEntity.init();
			SeekerEntity.init();
			SeekeractEntity.init();
			WatcherEntity.init();
			MeetboyLongEntity.init();
			FaultEntity.init();
			ScavengerEntity.init();
			MeatboydistortedEntity.init();
			EyesindarkEntity.init();
			SonOfgodEntity.init();
			DefaultVillagerEntity.init();
			ScaryDefaultVillagerEntity.init();
			VillagerMimicEntity.init();
			MimicTendrilEntity.init();
		});
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(MEETBOY.get(), MeetboyEntity.createAttributes().build());
		event.put(MEETBOY_GLITCH.get(), MeetboyGlitchEntity.createAttributes().build());
		event.put(MEETBOY_ELONGATED.get(), MeetboyElongatedEntity.createAttributes().build());
		event.put(MEETBOY_STUBBY.get(), MeetboyStubbyEntity.createAttributes().build());
		event.put(WHOAMI.get(), WhoamiEntity.createAttributes().build());
		event.put(ERRUNDEFINE.get(), ErrundefineEntity.createAttributes().build());
		event.put(ENTITY_000125.get(), Entity000125Entity.createAttributes().build());
		event.put(ENTITYWHOWATCHYOU.get(), EntitywhowatchyouEntity.createAttributes().build());
		event.put(ENTITY_CENTIPEDE.get(), EntityCentipedeEntity.createAttributes().build());
		event.put(LIXIS_9.get(), Lixis9Entity.createAttributes().build());
		event.put(RAGE_THEN_60S.get(), RageThen60sEntity.createAttributes().build());
		event.put(MEETBOYFAST.get(), MeetboyfastEntity.createAttributes().build());
		event.put(CARETAKER.get(), CaretakerEntity.createAttributes().build());
		event.put(GOD.get(), SeraphWrathEntity.createAttributes().build());
		event.put(PLAYER.get(), PlayerEntity.createAttributes().build());
		event.put(EYES.get(), EyesEntity.createAttributes().build());
		event.put(EYE.get(), EyeEntity.createAttributes().build());
		event.put(CAVEMEETBOY.get(), CavemeetboyEntity.createAttributes().build());
		event.put(INVISIBLE_WEIRD_ENTITY.get(), InvisibleWeirdEntityEntity.createAttributes().build());
		event.put(NOISE_ENTITY.get(), NoiseEntityEntity.createAttributes().build());
		event.put(INVISIBLE_DIST.get(), InvisibleDistEntity.createAttributes().build());
		event.put(SEEKER.get(), SeekerEntity.createAttributes().build());
		event.put(SEEKERACT.get(), SeekeractEntity.createAttributes().build());
		event.put(WATCHER.get(), WatcherEntity.createAttributes().build());
		event.put(MEETBOY_LONG.get(), MeetboyLongEntity.createAttributes().build());
		event.put(FAULT.get(), FaultEntity.createAttributes().build());
		event.put(SCAVENGER.get(), ScavengerEntity.createAttributes().build());
		event.put(MEATBOYDISTORTED.get(), MeatboydistortedEntity.createAttributes().build());
		event.put(EYESINDARK.get(), EyesindarkEntity.createAttributes().build());
		event.put(SON_OFGOD.get(), SonOfgodEntity.createAttributes().build());
		event.put(DEFAULT_VILLAGER.get(), DefaultVillagerEntity.createAttributes().build());
		event.put(SCARY_DEFAULT_VILLAGER.get(), ScaryDefaultVillagerEntity.createAttributes().build());
		event.put(VILLAGER_MIMIC.get(), VillagerMimicEntity.createAttributes().build());
		event.put(MIMIC_TENDRIL.get(), MimicTendrilEntity.createAttributes().build());
	}
}
