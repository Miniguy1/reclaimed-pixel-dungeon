package com.erebus.reclaimedpixeldungeon.items.skills;

import com.erebus.reclaimedpixeldungeon.actors.Actor;
import com.erebus.reclaimedpixeldungeon.actors.buffs.Buff;
import com.erebus.reclaimedpixeldungeon.actors.buffs.RampageBuff;
import com.erebus.reclaimedpixeldungeon.actors.hero.Hero;
import com.erebus.reclaimedpixeldungeon.messages.Messages;
import com.erebus.reclaimedpixeldungeon.sprites.ItemSpriteSheet;
import com.erebus.reclaimedpixeldungeon.utils.GLog;

/** Rampage: for 8 turns each melee hit also strikes every other enemy beside the hero for 60%. It recharges for 60 turns. */
public class RampageSkill extends WarriorSkill {

	public static final int COOLDOWN = 60;

	{
		image = ItemSpriteSheet.SKILL_RAMPAGE;
	}

	@Override
	public int cooldownTurns() {
		return COOLDOWN;
	}

	@Override
	public void execute( Hero hero, String action ) {
		if (!AC_CAST.equals( action )) {
			super.execute( hero, action );
			return;
		}
		if (!ready()) {
			GLog.w( Messages.get( this, "recharging", cooldown ) );
			return;
		}
		Buff.prolong( hero, RampageBuff.class, RampageBuff.DURATION );
		startCooldown();
		GLog.p( Messages.get( this, "go" ) );
		hero.spendAndNext( Actor.TICK );
	}
}
