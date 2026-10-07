package net.lixis.outofbound;

import net.lixis.outofbound.client.LiminalGameLauncher;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SeraphFinaleEndPacket {

	public SeraphFinaleEndPacket() {
	}

	public static void encode(SeraphFinaleEndPacket packet, FriendlyByteBuf buffer) {
	}

	public static SeraphFinaleEndPacket decode(FriendlyByteBuf buffer) {
		return new SeraphFinaleEndPacket();
	}

	public static void handle(SeraphFinaleEndPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> LiminalGameLauncher.launchAndExit()));
		context.setPacketHandled(true);
	}
}
