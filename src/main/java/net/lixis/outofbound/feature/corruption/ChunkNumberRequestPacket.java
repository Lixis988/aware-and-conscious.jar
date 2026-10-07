package net.lixis.outofbound.feature.corruption;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ChunkNumberRequestPacket {

	private final int fromDigit;
	private final int toDigit;
	private final boolean hasChunkPos;
	private final int chunkX;
	private final int chunkZ;

	public ChunkNumberRequestPacket(int fromDigit, int toDigit) {
		this(fromDigit, toDigit, false, 0, 0);
	}

	public ChunkNumberRequestPacket(int fromDigit, int toDigit, int chunkX, int chunkZ) {
		this(fromDigit, toDigit, true, chunkX, chunkZ);
	}

	private ChunkNumberRequestPacket(int fromDigit, int toDigit, boolean hasChunkPos, int chunkX, int chunkZ) {
		this.fromDigit = fromDigit;
		this.toDigit = toDigit;
		this.hasChunkPos = hasChunkPos;
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}

	public static void encode(ChunkNumberRequestPacket packet, FriendlyByteBuf buffer) {
		buffer.writeByte(packet.fromDigit);
		buffer.writeByte(packet.toDigit);
		buffer.writeBoolean(packet.hasChunkPos);
		if (packet.hasChunkPos) {
			buffer.writeInt(packet.chunkX);
			buffer.writeInt(packet.chunkZ);
		}
	}

	public static ChunkNumberRequestPacket decode(FriendlyByteBuf buffer) {
		int fromDigit = buffer.readByte();
		int toDigit = buffer.readByte();
		boolean hasChunkPos = buffer.readBoolean();
		if (hasChunkPos) {
			return new ChunkNumberRequestPacket(fromDigit, toDigit, buffer.readInt(), buffer.readInt());
		}
		return new ChunkNumberRequestPacket(fromDigit, toDigit);
	}

	public static void handle(ChunkNumberRequestPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		ServerPlayer player = context.getSender();
		if (player != null) {
			context.enqueueWork(() -> {
				if (packet.fromDigit == packet.toDigit) {
					player.sendSystemMessage(Component.literal(
							"[outofbound] from and to must be different digits (0-9)"));
					return;
				}

				ChunkPos requested = packet.hasChunkPos ? new ChunkPos(packet.chunkX, packet.chunkZ) : null;
				ChunkNumberHandler.Result result = ChunkNumberHandler.corruptLoadedChunk(player, packet.fromDigit,
						packet.toDigit, requested);

				if (requested != null && !player.serverLevel().hasChunk(requested.x, requested.z)) {
					player.sendSystemMessage(Component.literal(
							"[outofbound] Chunk [" + requested.x + ", " + requested.z + "] is not loaded"));
					return;
				}

				player.sendSystemMessage(Component.literal(
						result.numericFieldsChanged() == 0
								? "[outofbound] Chunk [" + result.chunkX() + ", " + result.chunkZ()
										+ "]: no digit " + result.fromDigit() + " found in numeric data"
								: "[outofbound] Chunk [" + result.chunkX() + ", " + result.chunkZ() + "]: replaced digit "
										+ result.fromDigit() + " -> " + result.toDigit()
										+ " in " + result.numericFieldsChanged() + " numeric fields ("
										+ result.blockStatesChanged() + " blocks, "
										+ result.blockEntitiesChanged() + " block entities)"));
			});
		}
		context.setPacketHandled(true);
	}
}
