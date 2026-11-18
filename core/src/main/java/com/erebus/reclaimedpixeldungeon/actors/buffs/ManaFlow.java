package com.erebus.reclaimedpixeldungeon.actors.buffs;

import com.erebus.reclaimedpixeldungeon.Dungeon;
import com.erebus.reclaimedpixeldungeon.actors.hero.Hero;
import com.erebus.reclaimedpixeldungeon.actors.hero.HeroClass;
import com.erebus.reclaimedpixeldungeon.items.skills.DashSkill;
import com.erebus.reclaimedpixeldungeon.items.skills.LifetakerSkill;
import com.erebus.reclaimedpixeldungeon.items.skills.WarriorSkill;
import com.erebus.reclaimedpixeldungeon.QuickSlot;
import com.erebus.reclaimedpixeldungeon.ui.QuickSlotButton;
import com.watabou.utils.Bundle;

/**
 * The Warrior's mana: a pool of 20 plus 2 for each level after the first, that refills by 1 every 3 turns. It also makes sure
 * the Warrior has his skill buttons (and puts them in a free quickslot the first time).
 */
public class ManaFlow extends Buff {

	{
		revivePersists = true;
	}

	public static final int TURNS_PER_MANA = 3;

	private int ticks = 0;

	/** the size of the pool for a hero of this level */
	public static int maxFor( int level ) {
		return 20 + 2 * Math.max( 0, level - 1 );
	}

	@Override
	public boolean act() {
		if (target instanceof Hero && ((Hero) target).heroClass == HeroClass.WARRIOR) {
			Hero h = (Hero) target;
			boolean first = h.MT == 0;
			h.MT = maxFor( h.lvl );
			if (first) h.MP = h.MT;
			if (h.MP > h.MT) h.MP = h.MT;
			if (++ticks >= TURNS_PER_MANA) {
				ticks = 0;
				if (h.MP < h.MT) {
					h.MP++;
					QuickSlotButton.refresh();
				}
			}
			ensureSkills( h );
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

	private static final String TICKS = "ticks";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( TICKS, ticks );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		ticks = bundle.getInt( TICKS );
	}
}
