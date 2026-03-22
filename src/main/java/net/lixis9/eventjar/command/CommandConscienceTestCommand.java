
package net.lixis9.eventjar.command;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.common.util.FakePlayerFactory;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.commands.Commands;

import net.lixis9.eventjar.procedures.CommandConscienceProcedure;

@Mod.EventBusSubscriber
public class CommandConscienceTestCommand {
	@SubscribeEvent
	public static void registerCommand(ServerStartingEvent event) {
		event.getServer().getCommands().getDispatcher().register(Commands.literal("conscience")

				.executes(arguments -> {
					Level world = arguments.getSource().getUnsidedLevel();
					double x = arguments.getSource().getPosition().x();
					double y = arguments.getSource().getPosition().y();
					double z = arguments.getSource().getPosition().z();
					Entity entity = arguments.getSource().getEntity();
					if (entity == null && world instanceof ServerLevel _servLevel)
						entity = FakePlayerFactory.getMinecraft(_servLevel);
					Direction direction = Direction.DOWN;
					if (entity != null)
						direction = entity.getDirection();

					CommandConscienceProcedure.execute(entity);
					return 0;
				}));
	}
}
