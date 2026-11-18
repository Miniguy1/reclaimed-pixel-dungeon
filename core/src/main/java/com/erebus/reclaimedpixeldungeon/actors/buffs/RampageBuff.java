package com.erebus.reclaimedpixeldungeon.actors.buffs;

import com.erebus.reclaimedpixeldungeon.ui.BuffIndicator;

/** Rampage: while it lasts, each melee hit of the Warrior also strikes every other enemy beside him (see WarriorSkills.onAttack). */
public class RampageBuff extends FlavourBuff {

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	public static final float DURATION = 8f;

	@Override
	public int icon() {
		return BuffIndicator.COMBO;
	}

	@Override
	public float iconFadePercent() {
		return Math.max( 0, (DURATION - visualcooldown()) / DURATION );
	}
}
