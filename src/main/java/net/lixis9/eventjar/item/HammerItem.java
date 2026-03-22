
package net.lixis9.eventjar.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lixis9.eventjar.procedures.HammerPriVzmakhieSushchnostiPriedmietomProcedure;
import net.lixis9.eventjar.procedures.HammerPriUdariePoSushchnostiInstrumientomProcedure;

import java.util.List;

public class HammerItem extends SwordItem {
	public HammerItem() {
		super(new Tier() {
			public int getUses() {
				return 0;
			}

			public float getSpeed() {
				return 4f;
			}

			public float getAttackDamageBonus() {
				return -3f;
			}

			public int getLevel() {
				return 1;
			}

			public int getEnchantmentValue() {
				return 2;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of();
			}
		}, 3, -3f, new Item.Properties());
	}

	@Override
	public boolean hurtEnemy(@Nonnull ItemStack itemstack, @Nonnull LivingEntity entity, @Nonnull LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
		HammerPriUdariePoSushchnostiInstrumientomProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
		return retval;
	}

	@Override
	public void appendHoverText(@Nonnull ItemStack itemstack, @Nullable Level level, @Nonnull List<Component> list, @Nonnull TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.literal("psychopomp?"));
	}

	@Override
	public boolean onEntitySwing(ItemStack itemstack, LivingEntity entity) {
		boolean retval = super.onEntitySwing(itemstack, entity);
		HammerPriVzmakhieSushchnostiPriedmietomProcedure.execute(entity.level());
		return retval;
	}
}
