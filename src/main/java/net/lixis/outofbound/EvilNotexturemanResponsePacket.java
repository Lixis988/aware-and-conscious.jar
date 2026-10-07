package net.lixis.outofbound;

import net.lixis.outofbound.entity.OverworldNotexturemanEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EvilNotexturemanResponsePacket {

	private final int entityId;

	public EvilNotexturemanResponsePacket(int entityId) {
		this.entityId = entityId;
	}

	public static void encode(EvilNotexturemanResponsePacket packet, FriendlyByteBuf buffer) {
		buffer.writeVarInt(packet.entityId);
	}

	public static EvilNotexturemanResponsePacket decode(FriendlyByteBuf buffer) {
		return new EvilNotexturemanResponsePacket(buffer.readVarInt());
	}

	public static void handle(EvilNotexturemanResponsePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		ServerPlayer player = context.getSender();
		if (player != null) {
			context.enqueueWork(() -> {
				Entity entity = player.serverLevel().getEntity(packet.entityId);
				if (entity instanceof OverworldNotexturemanEntity evil) {
					evil.acceptAndTeleport(player);
				}
			});
		}
		context.setPacketHandled(true);
	}
}
