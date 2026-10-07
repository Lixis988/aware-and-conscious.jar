package net.lixis.outofbound;

import net.lixis.outofbound.client.BoundedOneAtmosphereClientState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BoundedOneAtmospherePacket {

	public enum Effect {
		GLITCH_PULSE(0),
		TITLE_FLICKER(1);

		private final int id;

		Effect(int id) {
			this.id = id;
		}

		public int id() {
			return id;
		}

		public static Effect fromId(int id) {
			for (Effect effect : values()) {
				if (effect.id == id) {
					return effect;
				}
			}
			return GLITCH_PULSE;
		}
	}

	private final Effect effect;
	private final int durationTicks;

	public BoundedOneAtmospherePacket(Effect effect, int durationTicks) {
		this.effect = effect;
		this.durationTicks = Math.max(1, durationTicks);
	}

	public Effect effect() {
		return effect;
	}

	public int durationTicks() {
		return durationTicks;
	}

	public static void encode(BoundedOneAtmospherePacket packet, FriendlyByteBuf buffer) {
		buffer.writeByte(packet.effect.id());
		buffer.writeVarInt(packet.durationTicks);
	}

	public static BoundedOneAtmospherePacket decode(FriendlyByteBuf buffer) {
		Effect effect = Effect.fromId(buffer.readByte() & 0xFF);
		int duration = buffer.readVarInt();
		return new BoundedOneAtmospherePacket(effect, duration);
	}

	public static void handle(BoundedOneAtmospherePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			switch (packet.effect) {
				case GLITCH_PULSE -> BoundedOneAtmosphereClientState.triggerGlitch(packet.durationTicks);
				case TITLE_FLICKER -> BoundedOneAtmosphereClientState.triggerTitleFlicker(packet.durationTicks);
			}
		}));
		context.setPacketHandled(true);
	}
}
