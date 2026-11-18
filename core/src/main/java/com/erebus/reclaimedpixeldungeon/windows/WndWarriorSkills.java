package com.erebus.reclaimedpixeldungeon.windows;

import com.erebus.reclaimedpixeldungeon.Dungeon;
import com.erebus.reclaimedpixeldungeon.actors.buffs.SkillKit;
import com.erebus.reclaimedpixeldungeon.actors.hero.Hero;
import com.erebus.reclaimedpixeldungeon.actors.hero.WarriorSkills;
import com.erebus.reclaimedpixeldungeon.scenes.GameScene;
import com.erebus.reclaimedpixeldungeon.sprites.ItemSprite;
import com.erebus.reclaimedpixeldungeon.sprites.ItemSpriteSheet;
import com.erebus.reclaimedpixeldungeon.ui.ScrollingListPane;
import com.erebus.reclaimedpixeldungeon.ui.Window;
import com.erebus.reclaimedpixeldungeon.utils.GLog;

/**
 * The Warrior's skill tree: the skill points left, then the three branches (Fury, Iron, Blood). Tap a skill to buy its next level.
 */
public class WndWarriorSkills extends Window {

	private static final String[] ROMAN = { "", "I", "II", "III" };

	private final ScrollingListPane list;

	public WndWarriorSkills() {
		list = new ScrollingListPane();
		add( list );
		Hero hero = Dungeon.hero;

		list.addTitle( "Skill points: " + WarriorSkills.pointsLeft( hero ) + "  (one for each level)" );
		list.addTitle( "Fury" );
		addSkill( WarriorSkills.DASH, ItemSpriteSheet.SKILL_DASH );
		addSkill( WarriorSkills.RAMPAGE, ItemSpriteSheet.SKILL_RAMPAGE );
		list.addTitle( "Iron" );
		addSkill( WarriorSkills.STONE_SKIN, ItemSpriteSheet.SKILL_STONE_SKIN );
		addSkill( WarriorSkills.LAST_STAND, ItemSpriteSheet.SKILL_LAST_STAND );
		list.addTitle( "Blood" );
		addSkill( WarriorSkills.LIFETAKER, ItemSpriteSheet.SKILL_LIFETAKER );
		addSkill( WarriorSkills.BLOODTHIRST, ItemSpriteSheet.SKILL_BLOODTHIRST );

		resize( 120, Math.min( 144, (int) list.content().height() ) );
		list.setRect( 0, 0, width, height );
	}

	private void addSkill( final int skill, int image ) {
		final Hero hero = Dungeon.hero;
		int level = WarriorSkills.level( hero, skill );
		int max = WarriorSkills.maxLevel( skill );
		String text = WarriorSkills.NAME[skill] + (level > 0 ? " " + ROMAN[level] : "") + " (" + level + "/" + max + ")";
		if (level < max) {
			text += "\n" + (level == 0 ? "Learn" : "Next") + " for " + WarriorSkills.nextCost( hero, skill ) + " pt: "
					+ WarriorSkills.WHAT[skill][level];
		} else {
			text += "\nFully learned.";
		}
		ScrollingListPane.ListItem item = new ScrollingListPane.ListItem( new ItemSprite( image ), null, text ) {
			@Override
			public boolean onClick( float x, float y ) {
				if (inside( x, y )) {
					if (WarriorSkills.buy( hero, skill )) {
						GLog.p( WarriorSkills.NAME[skill] + " is now level " + WarriorSkills.level( hero, skill ) + "." );
						SkillKit.ensureSkills( hero );
						hide();
						GameScene.show( new WndWarriorSkills() );
					} else if (WarriorSkills.nextCost( hero, skill ) < 0) {
						GLog.i( WarriorSkills.NAME[skill] + " is fully learned." );
					} else {
						GLog.w( "Not enough skill points." );
					}
					return true;
				}
				return false;
			}
		};
		list.addItem( item );
	}
}
