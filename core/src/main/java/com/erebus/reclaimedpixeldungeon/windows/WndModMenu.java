package com.erebus.reclaimedpixeldungeon.windows;

import com.erebus.reclaimedpixeldungeon.Dungeon;
import com.erebus.reclaimedpixeldungeon.actors.Actor;
import com.erebus.reclaimedpixeldungeon.actors.Char;
import com.erebus.reclaimedpixeldungeon.actors.buffs.Hunger;
import com.erebus.reclaimedpixeldungeon.actors.hero.Hero;
import com.erebus.reclaimedpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.erebus.reclaimedpixeldungeon.levels.Level;
import com.erebus.reclaimedpixeldungeon.scenes.CellSelector;
import com.erebus.reclaimedpixeldungeon.scenes.GameScene;
import com.erebus.reclaimedpixeldungeon.ui.RedButton;
import com.erebus.reclaimedpixeldungeon.ui.Window;
import com.erebus.reclaimedpixeldungeon.utils.GLog;

/**
 * My own mod menu, opened from the in-game menu: fill health, fill hunger, move to any tile, and spawn any item
 * (sorted into the game's own catalog categories).
 */
public class WndModMenu extends Window {

	private static final int WIDTH = 120;
	private static final int BTN_HEIGHT = 20;
	private static final int GAP = 2;

	private int pos;

	public WndModMenu() {
		super();

		addButton( new RedButton( "Fill health" ) {
			@Override
			protected void onClick() {
				fillHealth();
			}
		} );

		addButton( new RedButton( "Fill hunger" ) {
			@Override
			protected void onClick() {
				fillHunger();
			}
		} );

		addButton( new RedButton( "Move to a tile" ) {
			@Override
			protected void onClick() {
				hide();
				pickTile();
			}
		} );

		addButton( new RedButton( "Spawn an item" ) {
			@Override
			protected void onClick() {
				hide();
				GameScene.show( new WndModItems() );
			}
		} );

		resize( WIDTH, pos );
	}

	private void addButton( RedButton btn ) {
		add( btn );
		btn.setRect( 0, pos > 0 ? pos += GAP : 0, WIDTH, BTN_HEIGHT );
		pos += BTN_HEIGHT;
	}

	private static void fillHealth() {
		Hero hero = Dungeon.hero;
		if (hero == null) return;
		hero.HP = hero.HT;
		GLog.p( "Health restored." );
	}

	private static void fillHunger() {
		Hero hero = Dungeon.hero;
		if (hero == null) return;
		Hunger hunger = hero.buff( Hunger.class );
		if (hunger != null) hunger.satisfy( Hunger.STARVING );
		GLog.p( "You are no longer hungry." );
	}

	/** tap any tile on the map and the hero appears there (any floor or doorway; not a wall, not an occupied one) */
	private static void pickTile() {
		GameScene.selectCell( new CellSelector.Listener() {
			@Override
			public void onSelect( Integer cell ) {
				Hero hero = Dungeon.hero;
				Level level = Dungeon.level;
				if (cell == null || hero == null || level == null) return;
				if (cell < 0 || cell >= level.length()) return;
				if (!level.passable[cell] && !level.avoid[cell]) {
					GLog.w( "You can't stand there." );
					return;
				}
				Char other = Actor.findChar( cell );
				if (other != null && other != hero) {
					GLog.w( "Something is in the way." );
					return;
				}
				ScrollOfTeleportation.appear( hero, cell );
				level.occupyCell( hero );
				Dungeon.observe();
				GameScene.updateFog();
				GLog.p( "Moved." );
			}

			@Override
			public String prompt() {
				return "Choose a tile to move to";
			}
		} );
	}
}
