package net.lixis9.eventjar.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

import net.lixis9.eventjar.init.EventjarModEntities;

public class KillYourselfProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (!world.isClientSide() && world.getServer() != null)
			world.getServer().getPlayerList().broadcastSystemMessage(Component.literal(
					"&kAOI:UJGBHF:OUP(I#Y!*)@Y#HT*)_QP{YIHGF)A*PIYHGF*#W_)IPYHG)*PU!QYHG#F)*{P!Q#YHG#)*{IYH#FG*IQAUYHG*IUO)QYHG*)IPQYHG*)IOUQYHGF*:OUI)QYH*FGUQAHSGHNAUIKJJEHGIUEJRKAHBIUJGHAUJHEGUIJAHGNOIUAEHYNGOU:AEHGNOUIAHGO:IUA?HJEGOUJAHNENGOUJJQAHYEGYOULOEQ*HYGO:LU*IHYAGO:*IU(((Q(YHE:G*UIYHQ:G*IQYHG:O*IU("),
					false);
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = EventjarModEntities.ERRUNDEFINE.get().spawn(_level, BlockPos.containing(30 + x, y, z), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setDeltaMovement(0, 0, 0);
			}
		}
		if (world instanceof ServerLevel _level)
			_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(), "weather thunder");
		if (entity instanceof Player _player)
			_player.closeContainer();
	}
}
