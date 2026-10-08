package net.lixis.outofbound;

import net.lixis.outofbound.client.WorldProgressClientState;
import net.lixis.outofbound.world.WorldGameStage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WorldProgressSyncPacket {

	private final boolean boundedcowCollision;
	private final byte gameStage;
	private final long sinkUntilTick;
	private final long worldGameTime;
	private final long worldDayTime;
	private final long mazeIndex;
	private final boolean undefiendMaze;
	private final long boundedcowCollisionTick;
	private final long boundedcowCollisionDayTime;
	private final boolean meatGameMode;

	public WorldProgressSyncPacket(boolean boundedcowCollision, WorldGameStage gameStage, long sinkUntilTick,
			long worldGameTime, long worldDayTime, long mazeIndex, boolean undefiendMaze,
			long boundedcowCollisionTick, long boundedcowCollisionDayTime, boolean meatGameMode) {
		this.boundedcowCollision = boundedcowCollision;
		this.gameStage = gameStage.id();
		this.sinkUntilTick = sinkUntilTick;
		this.worldGameTime = worldGameTime;
		this.worldDayTime = worldDayTime;
		this.mazeIndex = mazeIndex;
		this.undefiendMaze = undefiendMaze;
		this.boundedcowCollisionTick = boundedcowCollisionTick;
		this.boundedcowCollisionDayTime = boundedcowCollisionDayTime;
		this.meatGameMode = meatGameMode;
	}

	public static void encode(WorldProgressSyncPacket packet, FriendlyByteBuf buffer) {
		buffer.writeBoolean(packet.boundedcowCollision);
		buffer.writeByte(packet.gameStage);
		buffer.writeLong(packet.sinkUntilTick);
		buffer.writeLong(packet.worldGameTime);
		buffer.writeLong(packet.worldDayTime);
		buffer.writeLong(packet.mazeIndex);
		buffer.writeBoolean(packet.undefiendMaze);
		buffer.writeLong(packet.boundedcowCollisionTick);
		buffer.writeLong(packet.boundedcowCollisionDayTime);
		buffer.writeBoolean(packet.meatGameMode);
	}

	public static WorldProgressSyncPacket decode(FriendlyByteBuf buffer) {
		return new WorldProgressSyncPacket(
				buffer.readBoolean(),
				WorldGameStage.fromId(buffer.readByte()),
				buffer.readLong(),
				buffer.readLong(),
				buffer.readLong(),
				buffer.readLong(),
				buffer.readBoolean(),
				buffer.readLong(),
				buffer.readLong(),
				buffer.readBoolean());
	}

	public static void handle(WorldProgressSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> WorldProgressClientState.apply(
				packet.boundedcowCollision,
				WorldGameStage.fromId(packet.gameStage),
				packet.sinkUntilTick,
				packet.worldGameTime,
				packet.worldDayTime,
				packet.mazeIndex,
				packet.undefiendMaze,
				packet.boundedcowCollisionTick,
				packet.boundedcowCollisionDayTime,
				packet.meatGameMode)));
		context.setPacketHandled(true);
	}
}
