package net.lixis.outofbound;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;

import net.lixis.outofbound.feature.FeatureManager;
import net.lixis.outofbound.feature.corruption.CorruptionContext;
import net.lixis.outofbound.init.OutofboundModTabs;
import net.lixis.outofbound.init.OutofboundModSounds;
import net.lixis.outofbound.init.OutofboundModItems;
import net.lixis.outofbound.init.OutofboundModEntities;

import java.util.function.Supplier;
import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.AbstractMap;

public class OutofboundMod {
	public static final Logger LOGGER = LogManager.getLogger(OutofboundMod.class);

	public static final String MODID = "outofbound";
	public static final CorruptionContext CORRUPTION = new CorruptionContext();

	private static OutofboundMod INSTANCE;

	public static void init(IEventBus bus) {
		LOGGER.info(Authorship.notice());
		INSTANCE = new OutofboundMod();
		INSTANCE.bootstrap(bus);
		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	private void bootstrap(IEventBus bus) {
		FeatureManager.init();
		if (FMLEnvironment.dist == Dist.CLIENT) {
			MinecraftForge.EVENT_BUS.register(NoiseShaderClientHandler.class);
			MinecraftForge.EVENT_BUS.register(TestShaderClientHandler.class);
			MinecraftForge.EVENT_BUS.register(DailyShaderEventHandler.class);
			MinecraftForge.EVENT_BUS.register(ChaseShaderClientHandler.class);
			MinecraftForge.EVENT_BUS.register(net.lixis.outofbound.client.DarknessNoiseClientHandler.class);
			MinecraftForge.EVENT_BUS.register(MenuNoiseClientHandler.class);
			MinecraftForge.EVENT_BUS.register(RandomLabelClientHandler.class);
			MinecraftForge.EVENT_BUS.register(BoundedcowGlitchClientHandler.class);
			MinecraftForge.EVENT_BUS.register(net.lixis.outofbound.client.BoundedOneAtmosphereClientHandler.class);
			MinecraftForge.EVENT_BUS.register(ChaserMusicClientHandler.class);
			MinecraftForge.EVENT_BUS.register(BlackSquareSoundClientHandler.class);
			MinecraftForge.EVENT_BUS.register(net.lixis.outofbound.feature.corruption.CorruptionClientHandler.class);
		}

		OutofboundModSounds.REGISTRY.register(bus);
		OutofboundModItems.REGISTRY.register(bus);
		OutofboundModEntities.REGISTRY.register(bus);
		OutofboundModTabs.REGISTRY.register(bus);
		net.lixis.outofbound.registry.OutofboundAddonRegistries.register(bus);

		addNetworkMessage(OpenConfigScreenPacket.class, OpenConfigScreenPacket::encode, OpenConfigScreenPacket::decode, OpenConfigScreenPacket::handle);
		addNetworkMessage(BoundedcowScarePacket.class, BoundedcowScarePacket::encode, BoundedcowScarePacket::decode, BoundedcowScarePacket::handle);
		addNetworkMessage(BoundedOneAtmospherePacket.class, BoundedOneAtmospherePacket::encode, BoundedOneAtmospherePacket::decode, BoundedOneAtmospherePacket::handle);
		addNetworkMessage(RandomMessagePacket.class, RandomMessagePacket::encode, RandomMessagePacket::decode, RandomMessagePacket::handle);
		addNetworkMessage(WorldProgressSyncPacket.class, WorldProgressSyncPacket::encode, WorldProgressSyncPacket::decode, WorldProgressSyncPacket::handle);
		addNetworkMessage(ChaserRevealPacket.class, ChaserRevealPacket::encode, ChaserRevealPacket::decode, ChaserRevealPacket::handle);
		addNetworkMessage(SeraphFinaleStartPacket.class, SeraphFinaleStartPacket::encode, SeraphFinaleStartPacket::decode, SeraphFinaleStartPacket::handle);
		addNetworkMessage(SeraphFinaleEndPacket.class, SeraphFinaleEndPacket::encode, SeraphFinaleEndPacket::decode, SeraphFinaleEndPacket::handle);
		addNetworkMessage(NotexturemanOfferPacket.class, NotexturemanOfferPacket::encode, NotexturemanOfferPacket::decode, NotexturemanOfferPacket::handle);
		addNetworkMessage(NotexturemanResponsePacket.class, NotexturemanResponsePacket::encode, NotexturemanResponsePacket::decode, NotexturemanResponsePacket::handle);
		addNetworkMessage(EvilNotexturemanOfferPacket.class, EvilNotexturemanOfferPacket::encode, EvilNotexturemanOfferPacket::decode, EvilNotexturemanOfferPacket::handle);
		addNetworkMessage(EvilNotexturemanResponsePacket.class, EvilNotexturemanResponsePacket::encode, EvilNotexturemanResponsePacket::decode, EvilNotexturemanResponsePacket::handle);
		addNetworkMessage(net.lixis.outofbound.feature.corruption.CorruptionSyncPacket.class, net.lixis.outofbound.feature.corruption.CorruptionSyncPacket::encode, net.lixis.outofbound.feature.corruption.CorruptionSyncPacket::decode, net.lixis.outofbound.feature.corruption.CorruptionSyncPacket::handle);
		addNetworkMessage(net.lixis.outofbound.feature.corruption.CorruptionLevelRequestPacket.class, net.lixis.outofbound.feature.corruption.CorruptionLevelRequestPacket::encode, net.lixis.outofbound.feature.corruption.CorruptionLevelRequestPacket::decode, net.lixis.outofbound.feature.corruption.CorruptionLevelRequestPacket::handle);
		addNetworkMessage(net.lixis.outofbound.feature.corruption.ChunkNumberRequestPacket.class, net.lixis.outofbound.feature.corruption.ChunkNumberRequestPacket::encode, net.lixis.outofbound.feature.corruption.ChunkNumberRequestPacket::decode, net.lixis.outofbound.feature.corruption.ChunkNumberRequestPacket::handle);
		net.lixis.outofbound.dimension.gen.MazeGeneratorRegistry.CHUNK_GENERATORS.register(bus);
		net.minecraftforge.fml.ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, net.lixis.outofbound.dimension.MazeConfig.SPEC, "outofbound-maze.toml");
		bus.addListener((net.minecraftforge.fml.event.config.ModConfigEvent.Loading mazeEvent) -> net.lixis.outofbound.dimension.MazeConfig.bake());
		bus.addListener((net.minecraftforge.fml.event.config.ModConfigEvent.Reloading mazeEvent) -> net.lixis.outofbound.dimension.MazeConfig.bake());
		net.minecraftforge.fml.ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, net.lixis.outofbound.ai.BoundedOneAiConfig.SPEC, "outofbound-bounded-ai.toml");
		bus.addListener((net.minecraftforge.fml.event.config.ModConfigEvent.Loading boundedAiEvent) -> net.lixis.outofbound.ai.BoundedOneAiConfig.bake());
		bus.addListener((net.minecraftforge.fml.event.config.ModConfigEvent.Reloading boundedAiEvent) -> net.lixis.outofbound.ai.BoundedOneAiConfig.bake());
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
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
			workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
	}

	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
			workQueue.forEach(work -> {
				work.setValue(work.getValue() - 1);
				if (work.getValue() == 0)
					actions.add(work);
			});
			actions.forEach(e -> e.getKey().run());
			workQueue.removeAll(actions);
			FeatureManager.serverTick(event.getServer());
		}
	}
}
