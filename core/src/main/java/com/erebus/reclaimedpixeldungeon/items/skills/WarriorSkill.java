package com.erebus.reclaimedpixeldungeon.items.skills;

import com.erebus.reclaimedpixeldungeon.actors.hero.Hero;
import com.erebus.reclaimedpixeldungeon.items.Item;
import com.erebus.reclaimedpixeldungeon.ui.QuickSlotButton;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

/**
 * A Warrior's skill button: a permanent item that sits in a quickslot and cannot be dropped. After it is used it recharges:
 * the button shows the turns left, and the cooldown ticks down once every turn (see SkillKit).
 */
public abstract class WarriorSkill extends Item {

	public static final String AC_CAST = "CAST";

	/** the turns left before it can be used again */
	public int cooldown = 0;

	{
		stackable = false;
		unique = true;
		bones = false;
		defaultAction = AC_CAST;
	}

	/** how long it recharges after use */
	public abstract int cooldownTurns();

	public boolean ready() {
		return cooldown <= 0;
	}

	public void startCooldown() {
		cooldown = cooldownTurns();
		QuickSlotButton.refresh();
	}

	/** one turn goes by */
	public void tick() {
		if (cooldown > 0) {
			cooldown--;
			QuickSlotButton.refresh();
		}
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = new ArrayList<>();
		actions.add( AC_CAST );
		return actions;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return 0;
	}

	/** the number shown on the button: the turns left to recharge */
	@Override
	public String status() {
		return cooldown > 0 ? Integer.toString( cooldown ) : null;
	}

	private static final String COOLDOWN = "cooldown";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( COOLDOWN, cooldown );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		cooldown = bundle.getInt( COOLDOWN );
	}
}
