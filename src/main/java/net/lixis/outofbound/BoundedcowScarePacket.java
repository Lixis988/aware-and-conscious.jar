package net.lixis.outofbound;

import net.lixis.outofbound.client.BoundedcowMainMenuHandler;
import net.lixis.outofbound.client.WindowsScareNotification;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BoundedcowScarePacket {

	public BoundedcowScarePacket() {
	}

	public static void encode(BoundedcowScarePacket packet, FriendlyByteBuf buffer) {
	}

	public static BoundedcowScarePacket decode(FriendlyByteBuf buffer) {
		return new BoundedcowScarePacket();
	}

	public static void handle(BoundedcowScarePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			WindowsScareNotification.show();
			BoundedcowMainMenuHandler.returnToMainMenu();
		}));
		context.setPacketHandled(true);
	}
}
