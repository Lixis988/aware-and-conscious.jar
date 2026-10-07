package net.lixis.outofbound;

import net.lixis.outofbound.entity.NotexturemanEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class NotexturemanResponsePacket {

	private final int entityId;
	private final boolean accept;

	public NotexturemanResponsePacket(int entityId, boolean accept) {
		this.entityId = entityId;
		this.accept = accept;
	}

	public static void encode(NotexturemanResponsePacket packet, FriendlyByteBuf buffer) {
		buffer.writeVarInt(packet.entityId);
		buffer.writeBoolean(packet.accept);
	}

	public static NotexturemanResponsePacket decode(FriendlyByteBuf buffer) {
		return new NotexturemanResponsePacket(buffer.readVarInt(), buffer.readBoolean());
	}

	public static void handle(NotexturemanResponsePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		ServerPlayer player = context.getSender();
		if (player != null) {
			context.enqueueWork(() -> {
				Entity entity = player.serverLevel().getEntity(packet.entityId);
				if (entity instanceof NotexturemanEntity notextureman) {
					notextureman.handlePlayerResponse(player, packet.accept);
				}
			});
		}
		context.setPacketHandled(true);
	}
}
