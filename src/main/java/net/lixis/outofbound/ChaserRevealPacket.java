package net.lixis.outofbound;

import net.lixis.outofbound.entity.ChaserEntity;
import net.lixis.outofbound.entity.ChaserRevealHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ChaserRevealPacket {

	private final int entityId;

	public ChaserRevealPacket(int entityId) {
		this.entityId = entityId;
	}

	public static void encode(ChaserRevealPacket packet, FriendlyByteBuf buffer) {
		buffer.writeVarInt(packet.entityId);
	}

	public static ChaserRevealPacket decode(FriendlyByteBuf buffer) {
		return new ChaserRevealPacket(buffer.readVarInt());
	}

	public static void handle(ChaserRevealPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			if (player == null) {
				return;
			}
			ServerLevel level = player.serverLevel();
			Entity entity = level.getEntity(packet.entityId);
			if (!(entity instanceof Mob mob) || !(entity instanceof ChaserEntity)) {
				return;
			}
			if (!mob.isAlive() || mob.isRemoved() || player.distanceTo(mob) > 160.0D) {
				return;
			}
			ChaserRevealHandler.markRevealed(mob);
		});
		context.setPacketHandled(true);
	}
}
