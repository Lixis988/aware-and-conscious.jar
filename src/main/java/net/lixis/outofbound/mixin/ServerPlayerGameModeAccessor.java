package net.lixis.outofbound.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayerGameMode.class)
public interface ServerPlayerGameModeAccessor {

	@Accessor("isDestroyingBlock")
	boolean outofbound$isDestroyingBlock();

	@Accessor("destroyPos")
	BlockPos outofbound$getDestroyPos();
}
