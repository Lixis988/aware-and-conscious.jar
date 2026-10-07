package net.lixis9.eventjar.network;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import net.lixis9.eventjar.world.inventory.AreyouwillingtoshareyourpersonalnformationwithusMenu;
import net.lixis9.eventjar.procedures.KillYourselfProcedure;
import net.lixis9.eventjar.EventjarMod;

import java.util.function.Supplier;
import java.util.HashMap;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AreyouwillingtoshareyourpersonalnformationwithusButtonMessage {
	private final int buttonID, x, y, z;

	public AreyouwillingtoshareyourpersonalnformationwithusButtonMessage(FriendlyByteBuf buffer) {
		this.buttonID = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
	}

	public AreyouwillingtoshareyourpersonalnformationwithusButtonMessage(int buttonID, int x, int y, int z) {
		this.buttonID = buttonID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static void buffer(AreyouwillingtoshareyourpersonalnformationwithusButtonMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}

	public static void handler(AreyouwillingtoshareyourpersonalnformationwithusButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			Player entity = context.getSender();
			int buttonID = message.buttonID;
			int x = message.x;
			int y = message.y;
			int z = message.z;
			handleButtonAction(entity, buttonID, x, y, z);
		});
		context.setPacketHandled(true);
	}

	public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
		if (entity == null)
			return;
		Level world = entity.level();
		HashMap guistate = AreyouwillingtoshareyourpersonalnformationwithusMenu.guistate;

		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (buttonID == 0) {

			KillYourselfProcedure.execute(world, x, y, z, entity);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		EventjarMod.addNetworkMessage(AreyouwillingtoshareyourpersonalnformationwithusButtonMessage.class, AreyouwillingtoshareyourpersonalnformationwithusButtonMessage::buffer, AreyouwillingtoshareyourpersonalnformationwithusButtonMessage::new,
				AreyouwillingtoshareyourpersonalnformationwithusButtonMessage::handler);
	}
}
