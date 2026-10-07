package net.lixis.outofbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RandomMessagePacket {

	public static final int MESSAGE_COUNT = 4;

	private final int messageIndex;
	private final int durationTicks;

	public RandomMessagePacket(int messageIndex, int durationTicks) {
		this.messageIndex = messageIndex;
		this.durationTicks = Math.max(1, durationTicks);
	}

	public int messageIndex() {
		return messageIndex;
	}

	public int durationTicks() {
		return durationTicks;
	}

	public static void encode(RandomMessagePacket packet, FriendlyByteBuf buffer) {
		buffer.writeByte(packet.messageIndex);
		buffer.writeVarInt(packet.durationTicks);
	}

	public static RandomMessagePacket decode(FriendlyByteBuf buffer) {
		int index = buffer.readByte() & 0xFF;
		int duration = buffer.readVarInt();
		return new RandomMessagePacket(index, duration);
	}

	public static void handle(RandomMessagePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
				() -> () -> net.lixis.outofbound.client.RandomMessageOverlay.trigger(packet.messageIndex,
						packet.durationTicks)));
		context.setPacketHandled(true);
	}
}
