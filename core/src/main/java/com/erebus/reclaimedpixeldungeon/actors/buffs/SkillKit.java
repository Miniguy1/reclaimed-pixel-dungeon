package com.erebus.reclaimedpixeldungeon.actors.buffs;

import com.erebus.reclaimedpixeldungeon.Dungeon;
import com.erebus.reclaimedpixeldungeon.QuickSlot;
import com.erebus.reclaimedpixeldungeon.actors.hero.Hero;
import com.erebus.reclaimedpixeldungeon.actors.hero.HeroClass;
import com.erebus.reclaimedpixeldungeon.items.Item;
import com.erebus.reclaimedpixeldungeon.items.skills.DashSkill;
import com.erebus.reclaimedpixeldungeon.items.skills.LifetakerSkill;
import com.erebus.reclaimedpixeldungeon.items.skills.WarriorSkill;
import com.erebus.reclaimedpixeldungeon.ui.QuickSlotButton;

/**
 * Makes sure the Warrior has his skill buttons (in a free quickslot the first time), and counts their cooldowns down by one
 * every turn.
 */
public class SkillKit extends Buff {

	{
		revivePersists = true;
	}

	@Override
	public boolean act() {
		if (target instanceof Hero && ((Hero) target).heroClass == HeroClass.WARRIOR) {
			Hero h = (Hero) target;
			ensureSkills( h );
			for (Item item : h.belongings) {
				if (item instanceof WarriorSkill) ((WarriorSkill) item).tick();
			}
		}
		spend( TICK );
		return true;
	}

	/** gives the Warrior the skill buttons he lacks, into a free quickslot */
	public static void ensureSkills( Hero h ) {
		if (h.belongings == null || Dungeon.quickslot == null) return;
		if (h.belongings.getItem( DashSkill.class ) == null) give( h, new DashSkill() );
		if (h.belongings.getItem( LifetakerSkill.class ) == null) give( h, new LifetakerSkill() );
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
}
