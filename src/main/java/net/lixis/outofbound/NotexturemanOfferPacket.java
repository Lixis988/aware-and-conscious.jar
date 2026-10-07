package net.lixis.outofbound;

import net.lixis.outofbound.client.NotexturemanOfferScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class NotexturemanOfferPacket {

	private final int entityId;
	private final int level;

	public NotexturemanOfferPacket(int entityId, int level) {
		this.entityId = entityId;
		this.level = level;
	}

	public static void encode(NotexturemanOfferPacket packet, FriendlyByteBuf buffer) {
		buffer.writeVarInt(packet.entityId);
		buffer.writeVarInt(packet.level);
	}

	public static NotexturemanOfferPacket decode(FriendlyByteBuf buffer) {
		return new NotexturemanOfferPacket(buffer.readVarInt(), buffer.readVarInt());
	}

	public static void handle(NotexturemanOfferPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
			minecraft.setScreen(new NotexturemanOfferScreen(packet.entityId, packet.level));
		}));
		context.setPacketHandled(true);
	}
}
