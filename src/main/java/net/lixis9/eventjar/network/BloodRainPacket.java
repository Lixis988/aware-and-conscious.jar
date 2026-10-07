package net.lixis9.eventjar.network;

import net.lixis9.eventjar.EventjarMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class BloodRainPacket {

	private final int durationTicks;

	public BloodRainPacket(int durationTicks) {
		this.durationTicks = Math.max(0, durationTicks);
	}

	public static void encode(BloodRainPacket packet, FriendlyByteBuf buffer) {
		buffer.writeVarInt(packet.durationTicks);
	}

	public static BloodRainPacket decode(FriendlyByteBuf buffer) {
		return new BloodRainPacket(buffer.readVarInt());
	}

	public static void handle(BloodRainPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		int duration = packet.durationTicks;
		context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
				() -> () -> net.lixis9.eventjar.client.BloodRainClientHandler.trigger(duration)));
		context.setPacketHandled(true);
	}

	public static void sendToDimension(ServerLevel level, int durationTicks) {
		EventjarMod.PACKET_HANDLER.send(
				PacketDistributor.DIMENSION.with(level::dimension),
				new BloodRainPacket(durationTicks));
	}

	public static void sendToPlayer(net.minecraft.server.level.ServerPlayer player, int durationTicks) {
		EventjarMod.PACKET_HANDLER.send(
				PacketDistributor.PLAYER.with(() -> player),
				new BloodRainPacket(durationTicks));
	}
}
