package net.lixis.outofbound.dimension.gen;

import com.mojang.serialization.Codec;
import net.lixis.outofbound.OutofboundMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class MazeGeneratorRegistry {

	public static final DeferredRegister<Codec<? extends ChunkGenerator>> CHUNK_GENERATORS =
			DeferredRegister.create(Registries.CHUNK_GENERATOR, OutofboundMod.MODID);

	public static final RegistryObject<Codec<? extends ChunkGenerator>> MAZE =
			CHUNK_GENERATORS.register("maze", () -> MazeChunkGenerator.CODEC);

	private MazeGeneratorRegistry() {
	}
}
