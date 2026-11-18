package com.erebus.reclaimedpixeldungeon.items.skills;

import com.erebus.reclaimedpixeldungeon.Dungeon;
import com.erebus.reclaimedpixeldungeon.actors.Actor;
import com.erebus.reclaimedpixeldungeon.actors.Char;
import com.erebus.reclaimedpixeldungeon.actors.buffs.Invisibility;
import com.erebus.reclaimedpixeldungeon.actors.hero.Hero;
import com.erebus.reclaimedpixeldungeon.items.wands.WandOfBlastWave;
import com.erebus.reclaimedpixeldungeon.mechanics.Ballistica;
import com.erebus.reclaimedpixeldungeon.messages.Messages;
import com.erebus.reclaimedpixeldungeon.scenes.CellSelector;
import com.erebus.reclaimedpixeldungeon.scenes.GameScene;
import com.erebus.reclaimedpixeldungeon.scenes.PixelScene;
import com.erebus.reclaimedpixeldungeon.sprites.ItemSpriteSheet;
import com.erebus.reclaimedpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;

/**
 * Dash: rush the first enemy in a line (up to 6 tiles away) and strike it for 120% of the hero's weapon damage. The blow is
 * not rolled against its defense, so it cannot be dodged, and it ignores armor. It recharges for 20 turns.
 */
public class DashSkill extends WarriorSkill {

	public static final int COOLDOWN = 20;
	public static final int RANGE = 6;
	public static final float POWER = 1.2f;

	{
		image = ItemSpriteSheet.SKILL_DASH;
	}

	@Override
	public int cooldownTurns() {
		return COOLDOWN;
	}

	/** the blow's damage: not rolled against defense, and armor does not reduce it */
	public static int strike( Hero hero, Char target ) {
		if (hero == null || target == null || !target.isAlive()) return 0;
		int dmg = Math.max( 1, Math.round( hero.damageRoll() * POWER ) );
		target.damage( dmg, hero );
		return dmg;
	}

	@Override
	public void execute( final Hero hero, String action ) {
		if (!AC_CAST.equals( action )) {
			super.execute( hero, action );
			return;
		}
		if (!ready()) {
			GLog.w( Messages.get( this, "recharging", cooldown ) );
			return;
		}
		if (hero.rooted) {
			PixelScene.shake( 1, 1f );
			return;
		}
		GameScene.selectCell( new CellSelector.Listener() {
			@Override
			public void onSelect( Integer cell ) {
				if (cell == null) return;
				if (Dungeon.level.distance( hero.pos, cell ) > RANGE) {
					GLog.w( Messages.get( DashSkill.class, "too_far" ) );
					return;
				}
				Ballistica route = new Ballistica( hero.pos, cell, Ballistica.PROJECTILE );
				final Char target = Actor.findChar( route.collisionPos );
				if (target == null || target == hero || target.alignment != Char.Alignment.ENEMY) {
					GLog.w( Messages.get( DashSkill.class, "no_target" ) );
					return;
				}
				//stop beside the enemy (or where we stand, if it is already beside us)
				int beside = route.dist >= 1 ? route.path.get( route.dist - 1 ) : hero.pos;
				final int dest = (Actor.findChar( beside ) == null || beside == hero.pos) ? beside : hero.pos;

				hero.busy();
				final Runnable land = new Runnable() {
					@Override
					public void run() {
						if (dest != hero.pos) {
							hero.move( dest );
							Dungeon.level.occupyCell( hero );
							Dungeon.observe();
							GameScene.updateFog();
						}
						startCooldown();
						strike( hero, target );
						WandOfBlastWave.BlastWave.blast( dest );
						PixelScene.shake( 2, 0.5f );
						Invisibility.dispel();
						hero.spendAndNext( Actor.TICK );
					}
				};
				if (dest == hero.pos) {
					land.run();
				} else {
					hero.sprite.jump( hero.pos, dest, new Callback() {
						@Override
						public void call() {
							land.run();
						}
					} );
				}
			}

			@Override
			public String prompt() {
				return Messages.get( DashSkill.class, "prompt" );
			}
		} );
	}
}
