package net.lixis.outofbound.mixin;

import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(WinScreen.class)
public interface WinScreenAccessor {

	@Accessor("lines")
	List<FormattedCharSequence> outofbound$getLines();

	@Invoker("addEmptyLine")
	void outofbound$addEmptyLine();

	@Invoker("addCreditsLine")
	void outofbound$addCreditsLine(Component line, boolean centered);
}
