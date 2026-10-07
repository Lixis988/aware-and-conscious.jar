package net.lixis.outofbound;

import net.lixis.outofbound.client.EvilNotexturemanScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EvilNotexturemanOfferPacket {

	private final int entityId;

	public EvilNotexturemanOfferPacket(int entityId) {
		this.entityId = entityId;
	}

	public static void encode(EvilNotexturemanOfferPacket packet, FriendlyByteBuf buffer) {
		buffer.writeVarInt(packet.entityId);
	}

	public static EvilNotexturemanOfferPacket decode(FriendlyByteBuf buffer) {
		return new EvilNotexturemanOfferPacket(buffer.readVarInt());
	}

	public static void handle(EvilNotexturemanOfferPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
			minecraft.setScreen(new EvilNotexturemanScreen(packet.entityId));
		}));
		context.setPacketHandled(true);
	}
}
