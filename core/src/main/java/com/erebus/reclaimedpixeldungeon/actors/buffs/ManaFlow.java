package com.erebus.reclaimedpixeldungeon.actors.buffs;

/** Retired: the Warrior's skills use cooldowns now. Kept so that a save made with the old mana buff still loads; it removes itself. */
public class ManaFlow extends Buff {

	@Override
	public boolean act() {
		detach();
		return true;
	}
}
