
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.mimicevent.init;

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

import net.mcreator.mimicevent.entity.ScaryDefaultVillagerEntity;
import net.mcreator.mimicevent.entity.DefaultVillagerEntity;
import net.mcreator.mimicevent.MimiceventMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MimiceventModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MimiceventMod.MODID);
	public static final RegistryObject<EntityType<DefaultVillagerEntity>> DEFAULT_VILLAGER = register("default_villager",
			EntityType.Builder.<DefaultVillagerEntity>of(DefaultVillagerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(1000).setUpdateInterval(3).setCustomClientFactory(DefaultVillagerEntity::new)

					.sized(0.6f, 1.95f));
	public static final RegistryObject<EntityType<ScaryDefaultVillagerEntity>> SCARY_DEFAULT_VILLAGER = register("scary_default_villager",
			EntityType.Builder.<ScaryDefaultVillagerEntity>of(ScaryDefaultVillagerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(1000).setUpdateInterval(3)
					.setCustomClientFactory(ScaryDefaultVillagerEntity::new)

					.sized(0.6f, 1.8f));

	private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			DefaultVillagerEntity.init();
			ScaryDefaultVillagerEntity.init();
		});
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(DEFAULT_VILLAGER.get(), DefaultVillagerEntity.createAttributes().build());
		event.put(SCARY_DEFAULT_VILLAGER.get(), ScaryDefaultVillagerEntity.createAttributes().build());
	}
}
