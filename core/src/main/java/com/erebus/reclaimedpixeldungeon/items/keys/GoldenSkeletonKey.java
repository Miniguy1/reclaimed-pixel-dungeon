package com.erebus.reclaimedpixeldungeon.items.keys;

import com.erebus.reclaimedpixeldungeon.actors.hero.Hero;
import com.erebus.reclaimedpixeldungeon.items.Item;
import com.erebus.reclaimedpixeldungeon.sprites.ItemSprite;
import com.erebus.reclaimedpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

/**
 * The Golden Skeleton Key: opens every lock (doors, vaults, crystal doors, the way out, and every kind of locked chest), on
 * any floor, and is never used up. It is an ordinary item, not a floor's key, so it goes with the hero from floor to floor. It
 * cannot be dropped or sold.
 */
public class GoldenSkeletonKey extends Item {

	{
		image = ItemSpriteSheet.GOLDEN_SKELETON_KEY;
		stackable = false;
		unique = true;
		bones = false;
	}

	/** does this hero carry the master key? */
	public static boolean carriedBy( Hero hero ) {
		return hero != null && hero.belongings != null && hero.belongings.getItem( GoldenSkeletonKey.class ) != null;
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		return new ArrayList<>();
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

	@Override
	public ItemSprite.Glowing glowing() {
		return new ItemSprite.Glowing( 0xFFD24A, 1.6f );
	}
}
