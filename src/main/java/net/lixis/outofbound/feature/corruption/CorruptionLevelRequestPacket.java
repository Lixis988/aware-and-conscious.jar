package net.lixis.outofbound.feature.corruption;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CorruptionLevelRequestPacket {

	public static final byte TOGGLE = 0;
	public static final byte OFF = 1;
	public static final byte SET = 2;

	private final byte mode;
	private final float level;

	public CorruptionLevelRequestPacket(byte mode, float level) {
		this.mode = mode;
		this.level = level;
	}

	public static void encode(CorruptionLevelRequestPacket packet, FriendlyByteBuf buffer) {
		buffer.writeByte(packet.mode);
		buffer.writeFloat(packet.level);
	}

	public static CorruptionLevelRequestPacket decode(FriendlyByteBuf buffer) {
		return new CorruptionLevelRequestPacket(buffer.readByte(), buffer.readFloat());
	}

	public static void handle(CorruptionLevelRequestPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		ServerPlayer player = context.getSender();
		if (player != null) {
			context.enqueueWork(() -> {
				switch (packet.mode) {
					case TOGGLE -> {
						float current = CorruptionAPI.getLevel(player.getUUID());
						if (current > 0.0F) {
							CorruptionAPI.clearManualAndApplyWorldBaseline(player);
						} else {
							CorruptionAPI.setLevelManual(player, 50.0F);
						}
					}
					case OFF -> CorruptionAPI.clearManualAndApplyWorldBaseline(player);
					case SET -> CorruptionAPI.setLevelManual(player, packet.level);
					default -> {
					}
				}
			});
		}
		context.setPacketHandled(true);
	}
}
