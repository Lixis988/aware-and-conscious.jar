package net.lixis.outofbound.init;

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

import net.lixis.outofbound.entity.UndefiendEntity;
import net.lixis.outofbound.entity.NormalcowEntity;
import net.lixis.outofbound.entity.BoundedcowEntity;
import net.lixis.outofbound.entity.CaveUndefiendEntity;
import net.lixis.outofbound.entity.OverworldUndefiendEntity;
import net.lixis.outofbound.OutofboundMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class OutofboundModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, OutofboundMod.MODID);
	public static final RegistryObject<EntityType<BoundedcowEntity>> BOUNDEDCOW = register("boundedcow",
			EntityType.Builder.<BoundedcowEntity>of(BoundedcowEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(BoundedcowEntity::new)

					.sized(1.2f, 1.1f));
	public static final RegistryObject<EntityType<NormalcowEntity>> NORMALCOW = register("normalcow",
			EntityType.Builder.<NormalcowEntity>of(NormalcowEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(NormalcowEntity::new)

					.sized(0.9f, 1.4f));
	public static final RegistryObject<EntityType<UndefiendEntity>> UNDEFIEND = register("undefiend",
			EntityType.Builder.<UndefiendEntity>of(UndefiendEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(160).setUpdateInterval(3).setCustomClientFactory(UndefiendEntity::new)

					.sized(0.9f, 2f));
	public static final RegistryObject<EntityType<OverworldUndefiendEntity>> OVERWORLD_UNDEFIEND = register("overworld_undefiend",
			EntityType.Builder.<OverworldUndefiendEntity>of(OverworldUndefiendEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(160).setUpdateInterval(3).setCustomClientFactory(OverworldUndefiendEntity::new)

					.sized(0.9f, 2f));
	public static final RegistryObject<EntityType<CaveUndefiendEntity>> CAVE_UNDEFIEND = register("cave_undefiend",
			EntityType.Builder.<CaveUndefiendEntity>of(CaveUndefiendEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(160).setUpdateInterval(3).setCustomClientFactory(CaveUndefiendEntity::new)

					.sized(0.9f, 2f));

	private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			BoundedcowEntity.init();
			NormalcowEntity.init();
			UndefiendEntity.init();
		});
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(BOUNDEDCOW.get(), BoundedcowEntity.createAttributes().build());
		event.put(NORMALCOW.get(), NormalcowEntity.createAttributes().build());
		event.put(UNDEFIEND.get(), UndefiendEntity.createAttributes().build());
		event.put(OVERWORLD_UNDEFIEND.get(), OverworldUndefiendEntity.createAttributes().build());
		event.put(CAVE_UNDEFIEND.get(), CaveUndefiendEntity.createAttributes().build());
	}
}
