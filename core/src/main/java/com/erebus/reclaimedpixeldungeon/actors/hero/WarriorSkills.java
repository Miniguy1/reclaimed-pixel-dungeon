package com.erebus.reclaimedpixeldungeon.actors.hero;

import com.erebus.reclaimedpixeldungeon.Dungeon;
import com.erebus.reclaimedpixeldungeon.actors.Char;
import com.erebus.reclaimedpixeldungeon.actors.buffs.Barrier;
import com.erebus.reclaimedpixeldungeon.actors.buffs.Buff;
import com.erebus.reclaimedpixeldungeon.actors.buffs.RampageBuff;
import com.erebus.reclaimedpixeldungeon.actors.buffs.SkillKit;
import com.erebus.reclaimedpixeldungeon.actors.buffs.Weakness;
import com.erebus.reclaimedpixeldungeon.actors.mobs.Mob;
import com.erebus.reclaimedpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

/**
 * The Warrior's skill tree. A hero earns one skill point for each level after the first; each level of a skill costs points.
 * Dash and Lifetaker are known from the start (their first level is free). The levels persist when the hero returns to town and
 * are lost when he dies (with his own level).
 *
 * Fury: Dash, Rampage. Iron: Stone Skin, Last Stand. Blood: Lifetaker, Bloodthirst.
 */
public final class WarriorSkills {

	private WarriorSkills(){}

	public static final int DASH = 0, LIFETAKER = 1, STONE_SKIN = 2, LAST_STAND = 3, RAMPAGE = 4, BLOODTHIRST = 5;
	public static final int COUNT = 6;

	/** what each level of each skill costs in points */
	private static final int[][] COST = { {0, 1, 2}, {0, 1, 2}, {1, 2}, {2}, {3}, {2} };

	public static final String[] NAME = { "Dash", "Lifetaker", "Stone Skin", "Last Stand", "Rampage", "Bloodthirst" };

	/** what each level does, in words (level 1 first) */
	public static final String[][] WHAT = {
			{ "rush an enemy up to 6 tiles away for 120% weapon damage that cannot be dodged. Recharge 20 turns.",
				"recharge 14 turns, range 8.",
				"the landing also hits every other enemy beside you for half." },
			{ "each kill you make heals 5% of your health (recharge 5 turns after a heal). A toggle.",
				"each kill heals 8%.",
				"recharge 3 turns." },
			{ "take 8% less damage.",
				"take 15% less damage." },
			{ "below a quarter of your health, a barrier of 20% of your health forms (once every 40 turns)." },
			{ "for 8 turns, each melee hit also strikes every other enemy beside you for 60%. A button; recharge 60 turns." },
			{ "your melee hits have a 25% chance to weaken the enemy for 4 turns." } };

	public static int[] fresh() {
		int[] l = new int[COUNT];
		l[DASH] = 1;
		l[LIFETAKER] = 1;
		return l;
	}

	public static int level( Hero h, int skill ) {
		if (h == null || h.skillLevels == null || skill >= h.skillLevels.length) return 0;
		return h.skillLevels[skill];
	}

	public static int maxLevel( int skill ) {
		return COST[skill].length;
	}

	/** one point for each level after the first */
	public static int pointsEarned( Hero h ) {
		return Math.max( 0, h.lvl - 1 );
	}

	public static int pointsSpent( Hero h ) {
		int spent = 0;
		for (int s = 0; s < COUNT; s++) {
			for (int l = 0; l < level( h, s ); l++) spent += COST[s][l];
		}
		return spent;
	}

	public static int pointsLeft( Hero h ) {
		return Math.max( 0, pointsEarned( h ) - pointsSpent( h ) );
	}

	/** the points the next level costs, or -1 at the top */
	public static int nextCost( Hero h, int skill ) {
		int l = level( h, skill );
		return l >= maxLevel( skill ) ? -1 : COST[skill][l];
	}

	public static boolean canBuy( Hero h, int skill ) {
		int cost = nextCost( h, skill );
		return cost >= 0 && cost <= pointsLeft( h );
	}

	public static boolean buy( Hero h, int skill ) {
		if (!canBuy( h, skill )) return false;
		h.skillLevels[skill]++;
		return true;
	}

	private static boolean warrior( Hero h ) {
		return h != null && h.heroClass == HeroClass.WARRIOR;
	}

	//---- the numbers each skill level gives ----
	public static int dashCooldown( Hero h ) { return level( h, DASH ) >= 2 ? 14 : 20; }
	public static int dashRange( Hero h ) { return level( h, DASH ) >= 2 ? 8 : 6; }
	public static boolean dashShockwave( Hero h ) { return level( h, DASH ) >= 3; }
	public static float lifetakerHeal( Hero h ) { return level( h, LIFETAKER ) >= 2 ? 0.08f : 0.05f; }
	public static int lifetakerCooldown( Hero h ) { return level( h, LIFETAKER ) >= 3 ? 3 : 5; }

	public static float stoneSkin( Hero h ) {
		int l = level( h, STONE_SKIN );
		return l <= 0 ? 0f : l == 1 ? 0.08f : 0.15f;
	}

	//---- the hooks into the hero ----

	/** a blow about to land on the hero: Stone Skin takes some of it */
	public static int incoming( Hero h, int dmg ) {
		if (!warrior( h ) || dmg <= 0) return dmg;
		float cut = stoneSkin( h );
		return cut > 0 ? Math.max( 1, Math.round( dmg * (1f - cut) ) ) : dmg;
	}

	/** the hero has just been hurt: Last Stand raises a barrier when he is nearly gone */
	public static void afterDamage( Hero h ) {
		if (!warrior( h ) || level( h, LAST_STAND ) <= 0 || !h.isAlive() || h.HP * 4 > h.HT) return;
		SkillKit kit = h.buff( SkillKit.class );
		if (kit == null || kit.lastStand > 0) return;
		kit.lastStand = 40;
		Buff.affect( h, Barrier.class ).setShield( Math.max( 1, Math.round( h.HT * 0.2f ) ) );
		GLog.p( "Last Stand!" );
	}

	/** a hit by the hero: Rampage strikes the others beside him, Bloodthirst may weaken the one hit */
	public static int onAttack( Hero h, Char enemy, int damage ) {
		if (!warrior( h ) || enemy == null || Dungeon.level == null || !Dungeon.level.adjacent( h.pos, enemy.pos )) return damage;
		if (h.buff( RampageBuff.class ) != null) {
			int spill = Math.max( 1, Math.round( damage * 0.6f ) );
			for (Mob m : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (m != enemy && m.alignment == Char.Alignment.ENEMY && m.isAlive() && Dungeon.level.adjacent( h.pos, m.pos )) {
					m.damage( spill, h );
				}
			}
		}
		if (level( h, BLOODTHIRST ) > 0 && enemy.isAlive() && Random.Int( 4 ) == 0) {
			Buff.prolong( enemy, Weakness.class, 4f );
		}
		return damage;
	}
}
