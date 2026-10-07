package net.lixis.outofbound.feature.corruption.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public final class ChunkNumberUtil {

	private ChunkNumberUtil() {
	}

	public static int replaceAllDigits(int value, int fromDigit, int toDigit) {
		if (fromDigit < 0 || fromDigit > 9 || toDigit < 0 || toDigit > 9 || fromDigit == toDigit) {
			return value;
		}

		char from = (char) ('0' + fromDigit);
		char to = (char) ('0' + toDigit);
		String text = Integer.toString(Math.abs(value));
		boolean containsFrom = false;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == from) {
				containsFrom = true;
				break;
			}
		}
		if (!containsFrom) {
			return value;
		}

		StringBuilder builder = new StringBuilder(text.length());
		for (int i = 0; i < text.length(); i++) {
			char current = text.charAt(i);
			builder.append(current == from ? to : current);
		}

		int result = Integer.parseInt(builder.toString());
		return value < 0 ? -result : result;
	}

	public static int nearestValidPropertyValue(int raw, IntegerProperty property, int current) {
		if (property.getPossibleValues().contains(raw)) {
			return raw;
		}

		int best = current;
		int bestDistance = Integer.MAX_VALUE;
		for (int candidate : property.getPossibleValues()) {
			int distance = Math.abs(candidate - raw);
			if (distance < bestDistance) {
				bestDistance = distance;
				best = candidate;
			}
		}
		return best;
	}

	public static int replaceAllNumericTags(CompoundTag tag, int fromDigit, int toDigit) {
		int changes = 0;
		for (String key : tag.getAllKeys()) {
			changes += replaceInTag(tag, key, tag.get(key), fromDigit, toDigit);
		}
		return changes;
	}

	private static int replaceInTag(CompoundTag parent, String key, Tag value, int fromDigit, int toDigit) {
		if (value instanceof NumericTag numericTag) {
			int oldValue = numericTag.getAsInt();
			int newValue = replaceAllDigits(oldValue, fromDigit, toDigit);
			if (newValue == oldValue) {
				return 0;
			}
			writeNumericTag(parent, key, value, newValue);
			return 1;
		}
		if (value instanceof CompoundTag compoundTag) {
			return replaceAllNumericTags(compoundTag, fromDigit, toDigit);
		}
		if (value instanceof ListTag listTag) {
			int changes = 0;
			for (int i = 0; i < listTag.size(); i++) {
				Tag element = listTag.get(i);
				if (element instanceof CompoundTag compoundTag) {
					changes += replaceAllNumericTags(compoundTag, fromDigit, toDigit);
				} else if (element instanceof NumericTag numericTag) {
					int oldValue = numericTag.getAsInt();
					int newValue = replaceAllDigits(oldValue, fromDigit, toDigit);
					if (newValue != oldValue) {
						listTag.set(i, copyNumericTag(element, newValue));
						changes++;
					}
				}
			}
			return changes;
		}
		return 0;
	}

	private static NumericTag copyNumericTag(Tag original, int newValue) {
		if (original instanceof net.minecraft.nbt.ByteTag) {
			return net.minecraft.nbt.ByteTag.valueOf((byte) newValue);
		}
		if (original instanceof net.minecraft.nbt.ShortTag) {
			return net.minecraft.nbt.ShortTag.valueOf((short) newValue);
		}
		if (original instanceof net.minecraft.nbt.LongTag) {
			return net.minecraft.nbt.LongTag.valueOf(newValue);
		}
		return net.minecraft.nbt.IntTag.valueOf(newValue);
	}

	private static void writeNumericTag(CompoundTag parent, String key, Tag original, int newValue) {
		if (original instanceof net.minecraft.nbt.ByteTag) {
			parent.putByte(key, (byte) newValue);
		} else if (original instanceof net.minecraft.nbt.ShortTag) {
			parent.putShort(key, (short) newValue);
		} else if (original instanceof net.minecraft.nbt.LongTag) {
			parent.putLong(key, newValue);
		} else {
			parent.putInt(key, newValue);
		}
	}
}
