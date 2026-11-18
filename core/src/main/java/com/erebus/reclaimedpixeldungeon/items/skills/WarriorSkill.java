package com.erebus.reclaimedpixeldungeon.items.skills;

import com.erebus.reclaimedpixeldungeon.Dungeon;
import com.erebus.reclaimedpixeldungeon.actors.hero.Hero;
import com.erebus.reclaimedpixeldungeon.items.Item;

import java.util.ArrayList;

/** A Warrior's skill button: a permanent item that sits in a quickslot, cannot be dropped, and shows the hero's mana. */
public abstract class WarriorSkill extends Item {

	public static final String AC_CAST = "CAST";

	{
		stackable = false;
		unique = true;
		bones = false;
		defaultAction = AC_CAST;
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

	/** the number shown on the button: the hero's mana */
	@Override
	public String status() {
		return Dungeon.hero == null ? null : Integer.toString( Dungeon.hero.MP );
	}
}
