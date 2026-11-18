package com.erebus.reclaimedpixeldungeon.actors.buffs;

import com.erebus.reclaimedpixeldungeon.Dungeon;
import com.erebus.reclaimedpixeldungeon.QuickSlot;
import com.erebus.reclaimedpixeldungeon.actors.hero.Hero;
import com.erebus.reclaimedpixeldungeon.actors.hero.HeroClass;
import com.erebus.reclaimedpixeldungeon.actors.hero.WarriorSkills;
import com.erebus.reclaimedpixeldungeon.items.Item;
import com.erebus.reclaimedpixeldungeon.items.skills.DashSkill;
import com.erebus.reclaimedpixeldungeon.items.skills.LifetakerSkill;
import com.erebus.reclaimedpixeldungeon.items.skills.RampageSkill;
import com.erebus.reclaimedpixeldungeon.items.skills.WarriorSkill;
import com.erebus.reclaimedpixeldungeon.ui.QuickSlotButton;
import com.watabou.utils.Bundle;

/**
 * Makes sure the Warrior has the skill buttons he has learned (in a free quickslot the first time), counts their cooldowns down by
 * one every turn, and times Last Stand's recharge.
 */
public class SkillKit extends Buff {

	{
		revivePersists = true;
	}

	/** the turns until Last Stand can raise a barrier again */
	public int lastStand = 0;

	@Override
	public boolean act() {
		if (target instanceof Hero && ((Hero) target).heroClass == HeroClass.WARRIOR) {
			Hero h = (Hero) target;
			ensureSkills( h );
			for (Item item : h.belongings) {
				if (item instanceof WarriorSkill) ((WarriorSkill) item).tick();
			}
			if (lastStand > 0) lastStand--;
		}
		spend( TICK );
		return true;
	}

	/** gives the Warrior the skill buttons he has learned and lacks, into a free quickslot */
	public static void ensureSkills( Hero h ) {
		if (h.belongings == null || Dungeon.quickslot == null) return;
		if (WarriorSkills.level( h, WarriorSkills.DASH ) > 0 && h.belongings.getItem( DashSkill.class ) == null) give( h, new DashSkill() );
		if (WarriorSkills.level( h, WarriorSkills.LIFETAKER ) > 0 && h.belongings.getItem( LifetakerSkill.class ) == null) give( h, new LifetakerSkill() );
		if (WarriorSkills.level( h, WarriorSkills.RAMPAGE ) > 0 && h.belongings.getItem( RampageSkill.class ) == null) give( h, new RampageSkill() );
	}

	private static void give( Hero h, WarriorSkill skill ) {
		if (!skill.collect( h.belongings.backpack )) return;
		for (int i = 0; i < QuickSlot.SIZE; i++) {
			if (Dungeon.quickslot.getItem( i ) == null) {
				Dungeon.quickslot.setSlot( i, skill );
				break;
			}
		}
		QuickSlotButton.refresh();
	}

	private static final String LASTSTAND = "laststand";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LASTSTAND, lastStand );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		lastStand = bundle.getInt( LASTSTAND );
	}
}
