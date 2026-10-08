package net.lixis9.eventjar.network;

import net.lixis.outofbound.client.LiminalGameLauncher;
import net.lixis9.eventjar.AacConfig;
import net.lixis9.eventjar.EventjarMod;
import net.lixis9.eventjar.procedures.Batch1Procedure;
import net.lixis9.eventjar.procedures.BatchProcedure;
import net.lixis9.eventjar.procedures.CalculatorFindProcedure;
import net.lixis9.eventjar.procedures.Iseeyou3Procedure;
import net.lixis9.eventjar.procedures.MakeYarlikProcedure;
import net.lixis9.eventjar.procedures.MeetcraftProcedure;
import net.lixis9.eventjar.procedures.PaintWriteTextProcedure;
import net.lixis9.eventjar.procedures.TxtondesktopProcedure;
import net.lixis9.eventjar.procedures.WinmessegeProcedure;
import net.lixis9.eventjar.procedures.WorkspacereplaceProcedure;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public final class ClientOsEffectPacket {

	public enum Effect {
		CALC,
		WINMSG,
		CMD,
		YARLIK,
		BATCH,
		BATCH1,
		TXT_DESKTOP,
		PAINT,
		WALLPAPER,
		MEETCRAFT,
		MEAT_LIMINAL
	}

	private final Effect effect;

	public ClientOsEffectPacket(Effect effect) {
		this.effect = effect;
	}

	public static void encode(ClientOsEffectPacket packet, FriendlyByteBuf buf) {
		buf.writeEnum(packet.effect);
	}

	public static ClientOsEffectPacket decode(FriendlyByteBuf buf) {
		return new ClientOsEffectPacket(buf.readEnum(Effect.class));
	}

	public static void handle(ClientOsEffectPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> runOnClient(packet.effect)));
		ctx.setPacketHandled(true);
	}

	public static void send(ServerPlayer player, Effect effect) {
		if (player == null || effect == null) {
			return;
		}
		if (AacConfig.SAFE_MODE && !isMinigame(effect)) {
			EventjarMod.LOGGER.debug("Skipped OS effect {} — Safe Mode is on", effect);
			return;
		}
		EventjarMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new ClientOsEffectPacket(effect));
	}

	public static void sendOrWarn(ServerPlayer player, Effect effect) {
		if (player == null) {
			return;
		}
		send(player, effect);
	}

	public static boolean sendFromCommand(ServerPlayer player, Effect effect) {
		if (player == null) {
			return false;
		}
		send(player, effect);
		return true;
	}

	private static void runOnClient(Effect effect) {
		if (AacConfig.SAFE_MODE && !isMinigame(effect)) {
			EventjarMod.LOGGER.debug("Blocked OS effect {} on client — Safe Mode", effect);
			return;
		}
		Thread t = new Thread(() -> {
			try {
				switch (effect) {
					case CALC -> CalculatorFindProcedure.execute();
					case WINMSG -> WinmessegeProcedure.execute();
					case CMD -> Iseeyou3Procedure.execute();
					case YARLIK -> MakeYarlikProcedure.execute();
					case BATCH -> BatchProcedure.execute();
					case BATCH1 -> Batch1Procedure.execute();
					case TXT_DESKTOP -> TxtondesktopProcedure.execute();
					case PAINT -> PaintWriteTextProcedure.execute();
					case WALLPAPER -> WorkspacereplaceProcedure.execute();
					case MEETCRAFT -> MeetcraftProcedure.execute();
					case MEAT_LIMINAL -> LiminalGameLauncher.launchMeatHunt();
				}
			} catch (Throwable t1) {
				EventjarMod.LOGGER.warn("Client OS effect {} failed: {}", effect, t1.toString());
			}
		}, "eventjar-os-" + effect.name().toLowerCase());
		t.setDaemon(true);
		t.start();
	}

	private static boolean isMinigame(Effect effect) {
		return effect == Effect.MEETCRAFT || effect == Effect.MEAT_LIMINAL;
	}

	private ClientOsEffectPacket() {
		this.effect = Effect.WINMSG;
	}
}
