package net.lixis9.eventjar;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;

import net.minecraftforge.server.ServerLifecycleHooks;

import net.lixis9.eventjar.init.EventjarModTabs;
import net.lixis9.eventjar.init.EventjarModSounds;
import net.lixis9.eventjar.init.EventjarModParticleTypes;
import net.lixis9.eventjar.init.EventjarModPlacementModifiers;
import net.lixis9.eventjar.init.EventjarModMobEffects;
import net.lixis9.eventjar.init.EventjarModMenus;
import net.lixis9.eventjar.init.EventjarModItems;
import net.lixis9.eventjar.init.EventjarModEntities;
import net.lixis9.eventjar.init.EventjarModBlocks;

import java.util.function.Supplier;
import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.AbstractMap;

@Mod("eventjar")
public class EventjarMod {
	public static final Logger LOGGER = LogManager.getLogger(EventjarMod.class);
	public static final String MODID = "eventjar";

	public EventjarMod() {

		LOGGER.info(Authorship.notice());

		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(net.lixis9.eventjar.BoatExplosionHandler.class);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		EventjarModSounds.REGISTRY.register(bus);
		EventjarModBlocks.REGISTRY.register(bus);

		EventjarModItems.REGISTRY.register(bus);
		EventjarModEntities.REGISTRY.register(bus);

		EventjarModTabs.REGISTRY.register(bus);

		EventjarModMobEffects.REGISTRY.register(bus);

		EventjarModParticleTypes.REGISTRY.register(bus);

		EventjarModMenus.REGISTRY.register(bus);
		EventjarModPlacementModifiers.REGISTRY.register(bus);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DarknessConfig.SPEC, "eventjar-client.toml");
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AacConfig.SPEC, "aac-config.toml");
		bus.addListener((ModConfigEvent.Loading event) -> {
			DarknessConfig.bake();
			AacConfig.bake();
			syncJumbledTextFlags();
		});
		bus.addListener((ModConfigEvent.Reloading event) -> {
			DarknessConfig.bake();
			AacConfig.bake();
			syncJumbledTextFlags();
		});

		net.lixis.outofbound.OutofboundMod.init(bus);

	}

	private static void syncJumbledTextFlags() {
		try {
			DarknessConfig.setRandomLabels(AacConfig.TEXT_DISTORTION);
		} catch (Throwable ignored) {

		}
	}

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	private static int messageID = 0;

	public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
		PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
		messageID++;
	}

	private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public static void queueServerWork(int tick, Runnable action) {
		if (action == null) {
			return;
		}
		AbstractMap.SimpleEntry<Runnable, Integer> entry =
				new AbstractMap.SimpleEntry<>(action, Math.max(tick, 1));
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
			workQueue.add(entry);
			return;
		}
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null) {
			server.execute(() -> workQueue.add(entry));
			return;
		}
		if (FMLEnvironment.dist == Dist.CLIENT) {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
					() -> () -> net.lixis9.eventjar.client.IntegratedWorkEnqueue.addIfSingleplayer(workQueue, entry));
		} else {
			workQueue.add(entry);
		}
	}

	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
			workQueue.forEach(work -> {
				int next = work.getValue() - 1;
				work.setValue(next);
				if (next <= 0)
					actions.add(work);
			});
			actions.forEach(e -> e.getKey().run());
			workQueue.removeAll(actions);
		}
	}
}
