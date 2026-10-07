package net.lixis9.eventjar.block;

import net.lixis9.eventjar.init.EventjarModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.sounds.SoundEvent;

import javax.annotation.Nullable;

public class MeatBlock extends Block {

	private static SoundType meatSoundType;

	public MeatBlock() {
		super(BlockBehaviour.Properties.of()
				.mapColor(MapColor.COLOR_RED)
				.sound(SoundType.SLIME_BLOCK)
				.strength(1.2F, 4.0F));
	}

	private static SoundType meatSounds() {
		if (meatSoundType == null) {
			SoundEvent step = EventjarModSounds.MEAT_STEP.get();
			meatSoundType = new SoundType(1.0F, 0.95F, step, step, step, step, step);
		}
		return meatSoundType;
	}

	@Override
	@Deprecated
	public SoundType getSoundType(BlockState state) {
		return meatSounds();
	}

	@Override
	public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
		return meatSounds();
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}
}
