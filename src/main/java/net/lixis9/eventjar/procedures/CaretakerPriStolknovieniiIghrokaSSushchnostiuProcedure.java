package net.lixis9.eventjar.procedures;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

public class CaretakerPriStolknovieniiIghrokaSSushchnostiuProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (!entity.level().isClientSide())
			entity.discard();
		if (world instanceof ServerLevel _level)
			_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
					"tellraw @a [\"\",{\"color\":\"#ff0000\",\"text\":\"UIAUIWO;GAAIOEWHG'PAIE3JFHI'AJHU3I9'8FJHUYQ'0P8I9FJUQ'0P9I8UFJQ-9UJQ-P9I3OUJHQ-9[3OUJQ\"},{\"color\":\"#ff0000\",\"text\":\"\\n-[90OUJQ-9UJQ-9UJQ-9UJQ-9UJ)0010010010102591AJSFHA89999999923YHHYASUHYF8JAVAFMLLLERRRRRRRRRRRRRRRRRRUNDERFINE=null\"}]");
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:scaremusic_ogg")), SoundSource.NEUTRAL, 1, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:scaremusic_ogg")), SoundSource.NEUTRAL, 1, 1, false);
			}
		}
	}
}
