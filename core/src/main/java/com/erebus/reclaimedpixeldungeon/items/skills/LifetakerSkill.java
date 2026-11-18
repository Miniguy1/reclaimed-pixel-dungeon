package com.erebus.reclaimedpixeldungeon.items.skills;

import com.erebus.reclaimedpixeldungeon.Dungeon;
import com.erebus.reclaimedpixeldungeon.actors.hero.Hero;
import com.erebus.reclaimedpixeldungeon.actors.hero.WarriorSkills;
import com.erebus.reclaimedpixeldungeon.effects.Speck;
import com.erebus.reclaimedpixeldungeon.messages.Messages;
import com.erebus.reclaimedpixeldungeon.sprites.CharSprite;
import com.erebus.reclaimedpixeldungeon.sprites.ItemSpriteSheet;
import com.erebus.reclaimedpixeldungeon.ui.QuickSlotButton;
import com.erebus.reclaimedpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

/**
 * Lifetaker: a toggle. While it is on, an enemy the hero kills heals 5% of their maximum health. After a heal it recharges for
 * 5 turns, and kills in that time do not heal (so a whole pack does not heal for each one). Nothing happens at full health.
 */
public class LifetakerSkill extends WarriorSkill {

	public static final int COOLDOWN = 5;
	public static final float HEAL = 0.05f;

	public boolean active = false;

	{
		image = ItemSpriteSheet.SKILL_LIFETAKER;
	}

	@Override
	public int cooldownTurns() {
		return Dungeon.hero == null ? COOLDOWN : WarriorSkills.lifetakerCooldown( Dungeon.hero );
	}

	@Override
	public void execute( Hero hero, String action ) {
		if (!AC_CAST.equals( action )) {
			super.execute( hero, action );
			return;
		}
		active = !active;
		GLog.i( Messages.get( this, active ? "on" : "off" ) );
		QuickSlotButton.refresh();
	}

	/** the turns left while it recharges, otherwise on or off */
	@Override
	public String status() {
		if (cooldown > 0) return Integer.toString( cooldown );
		return active ? "on" : "off";
	}

	/** how much a kill heals this hero (their maximum health, a twentieth, at least 1) */
	public static int healFor( Hero hero ) {
		return Math.max( 1, Math.round( hero.HT * WarriorSkills.lifetakerHeal( hero ) ) );
	}

	/** an enemy has died by the hero's hand */
	public static void onKill( Hero hero ) {
		if (hero == null || hero.belongings == null) return;
		LifetakerSkill skill = hero.belongings.getItem( LifetakerSkill.class );
		if (skill == null || !skill.active || !skill.ready() || hero.HP >= hero.HT) return;
		int heal = healFor( hero );
		hero.HP = Math.min( hero.HT, hero.HP + heal );
		if (hero.sprite != null) {
			hero.sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
			hero.sprite.showStatus( CharSprite.POSITIVE, Integer.toString( heal ) );
		}
		skill.startCooldown();
	}

	private static final String ACTIVE = "active";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ACTIVE, active );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		active = bundle.getBoolean( ACTIVE );
	}
}
