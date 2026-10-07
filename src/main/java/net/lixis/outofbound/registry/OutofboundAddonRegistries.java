package net.lixis.outofbound.registry;

import net.lixis.outofbound.block.OutofboundBlocks;
import net.minecraftforge.eventbus.api.IEventBus;

public final class OutofboundAddonRegistries {

	private OutofboundAddonRegistries() {
	}

	public static void register(IEventBus bus) {
		OutofboundExtraSounds.REGISTRY.register(bus);
		OutofboundExtraEntities.REGISTRY.register(bus);
		OutofboundExtraItems.REGISTRY.register(bus);
		OutofboundBlocks.BLOCKS.register(bus);
		OutofboundBlocks.BLOCK_ITEMS.register(bus);
	}
}
