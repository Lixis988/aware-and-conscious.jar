package net.lixis.outofbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenConfigScreenPacket {

	public OpenConfigScreenPacket() {
	}

	public static void encode(OpenConfigScreenPacket packet, FriendlyByteBuf buffer) {
	}

	public static OpenConfigScreenPacket decode(FriendlyByteBuf buffer) {
		return new OpenConfigScreenPacket();
	}

	public static void handle(OpenConfigScreenPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
			minecraft.setScreen(new net.lixis9.eventjar.client.gui.AacConfigScreen());
		}));
		context.setPacketHandled(true);
	}
}
