package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.ServerChatEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class Msg2Procedure {
	@SubscribeEvent
	public static void onChat(ServerChatEvent event) {
		execute(event, event.getPlayer().level(), event.getRawText());
	}

	public static void execute(LevelAccessor world, String text) {
		execute(null, world, text);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, String text) {
		if (text == null)
			return;
		if (text.contains("/spawnpoin")) {
			if (!world.isClientSide() && world.getServer() != null)
				world.getServer().getPlayerList().broadcastSystemMessage(Component.literal(
						"CREATE TABLE IF NOT EXISTS `Alternative_Pathways`.`Clients_has_Staff` ( `Clients_Case_Number` INT NOT NULL , `Staff_Emp_ID` INT NOT NULL , PRIMARY KEY (`Clients_Case_Number`, `Staff_Emp_ID`) , INDEX `fk_Clients_has_Staff_Staff1_idx` (`Staff_Emp_ID` ASC) , INDEX `fk_Clients_has_Staff_Clients_idx` (`Clients_Case_Number` ASC) , CONSTRAINT `fk_Clients_has_Staff_Clients` FOREIGN KEY (`Clients_Case_Number` ) REFERENCES `Alternative_Pathways`.`Clients` (`Case_Number` ) ON DELETE NO ACTION ON UPDATE NO ACTION, CONSTRAINT `fk_Clients_has_Staff_Staff1` FOREIGN KEY (`Staff_Emp_ID` ) REFERENCES `Alternative_Pathways`.`Staff` (`Emp_ID` ) ON DELETE NO ACTION ON UPDATE NO ACTION) ENGINE = InnoDB"),
						false);
		}
	}
}
