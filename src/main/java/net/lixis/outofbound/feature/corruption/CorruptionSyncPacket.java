package net.lixis.outofbound.feature.corruption;

import net.lixis.outofbound.OutofboundMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CorruptionSyncPacket {

	private final float level;
	private final boolean active;
	private final boolean burstActive;
	private final int burstTicks;

	public CorruptionSyncPacket(float level, boolean active, boolean burstActive, int burstTicks) {
		this.level = Math.max(0.0F, Math.min(100.0F, level));
		this.active = active;
		this.burstActive = burstActive;
		this.burstTicks = Math.max(0, burstTicks);
	}

	public float level() {
		return level;
	}

	public boolean active() {
		return active;
	}

	public boolean burstActive() {
		return burstActive;
	}

	public int burstTicks() {
		return burstTicks;
	}

	public static void encode(CorruptionSyncPacket packet, FriendlyByteBuf buffer) {
		buffer.writeFloat(packet.level);
		buffer.writeBoolean(packet.active);
		buffer.writeBoolean(packet.burstActive);
		buffer.writeVarInt(packet.burstTicks);
	}

	public static CorruptionSyncPacket decode(FriendlyByteBuf buffer) {
		return new CorruptionSyncPacket(
				buffer.readFloat(),
				buffer.readBoolean(),
				buffer.readBoolean(),
				buffer.readVarInt());
	}

	public static void handle(CorruptionSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			if (packet.active()) {
				CorruptionClientState.setLevel(packet.level());
			} else {
				CorruptionClientState.setLevel(0.0F);
			}
			if (packet.burstActive() && packet.burstTicks() > 0) {
				CorruptionScheduler.forceBurst(packet.burstTicks());
				CorruptionClientState.extendRenderCorruption(packet.burstTicks() * 4);
			}
		}));
		context.setPacketHandled(true);
	}

	public static void sendToPlayer(net.minecraft.server.level.ServerPlayer player, float level) {
		sendToPlayer(player, level, false, 0);
	}

	public static void sendToPlayer(net.minecraft.server.level.ServerPlayer player, float level,
			boolean burstActive, int burstTicks) {
		boolean active = level > 0.0F;
		OutofboundMod.PACKET_HANDLER.send(
				net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
				new CorruptionSyncPacket(level, active, burstActive, burstTicks));
	}
}
