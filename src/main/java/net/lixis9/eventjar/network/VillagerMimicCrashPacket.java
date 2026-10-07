package net.lixis9.eventjar.network;

import net.lixis9.eventjar.EventjarMod;
import net.lixis9.eventjar.client.VillagerMimicCrashClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public final class VillagerMimicCrashPacket {

	public VillagerMimicCrashPacket() {
	}

	public static void encode(VillagerMimicCrashPacket packet, FriendlyByteBuf buf) {
	}

	public static VillagerMimicCrashPacket decode(FriendlyByteBuf buf) {
		return new VillagerMimicCrashPacket();
	}

	public static void handle(VillagerMimicCrashPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> VillagerMimicCrashClient::begin));
		ctx.setPacketHandled(true);
	}

	public static void send(ServerPlayer player) {
		EventjarMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new VillagerMimicCrashPacket());
	}
}
