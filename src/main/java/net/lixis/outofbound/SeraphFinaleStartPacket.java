package net.lixis.outofbound;

import net.lixis.outofbound.client.SeraphFinaleClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SeraphFinaleStartPacket {

	private final int entityId;

	public SeraphFinaleStartPacket(int entityId) {
		this.entityId = entityId;
	}

	public static void encode(SeraphFinaleStartPacket packet, FriendlyByteBuf buffer) {
		buffer.writeVarInt(packet.entityId);
	}

	public static SeraphFinaleStartPacket decode(FriendlyByteBuf buffer) {
		return new SeraphFinaleStartPacket(buffer.readVarInt());
	}

	public static void handle(SeraphFinaleStartPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> SeraphFinaleClientHandler.begin(packet.entityId)));
		context.setPacketHandled(true);
	}
}
