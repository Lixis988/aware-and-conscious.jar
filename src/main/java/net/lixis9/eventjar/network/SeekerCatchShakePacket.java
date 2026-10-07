package net.lixis9.eventjar.network;

import net.lixis9.eventjar.EventjarMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public final class SeekerCatchShakePacket {

	private final boolean active;

	public SeekerCatchShakePacket(boolean active) {
		this.active = active;
	}

	public static void encode(SeekerCatchShakePacket packet, FriendlyByteBuf buf) {
		buf.writeBoolean(packet.active);
	}

	public static SeekerCatchShakePacket decode(FriendlyByteBuf buf) {
		return new SeekerCatchShakePacket(buf.readBoolean());
	}

	public static void handle(SeekerCatchShakePacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
				() -> () -> net.lixis9.eventjar.client.SeekerWindowShakeClient.setActive(packet.active)));
		ctx.setPacketHandled(true);
	}

	public static void send(ServerPlayer player, boolean active) {
		if (player == null) {
			return;
		}
		EventjarMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new SeekerCatchShakePacket(active));
	}

	public static void sendToAll(ServerLevel level, boolean active) {
		if (level == null) {
			return;
		}
		for (ServerPlayer player : level.players()) {
			send(player, active);
		}
	}
}
