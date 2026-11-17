package com.erebus.reclaimedpixeldungeon.windows;

import com.erebus.reclaimedpixeldungeon.Dungeon;
import com.erebus.reclaimedpixeldungeon.items.Item;
import com.erebus.reclaimedpixeldungeon.items.armor.Armor;
import com.erebus.reclaimedpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.erebus.reclaimedpixeldungeon.journal.Catalog;
import com.erebus.reclaimedpixeldungeon.messages.Messages;
import com.erebus.reclaimedpixeldungeon.sprites.ItemSprite;
import com.erebus.reclaimedpixeldungeon.ui.ScrollingListPane;
import com.erebus.reclaimedpixeldungeon.ui.Window;
import com.erebus.reclaimedpixeldungeon.utils.GLog;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/** The items of one category: weapons and armor in tier order, the rest by name. Tap one to put it in the backpack. */
public class WndModItemList extends Window {

	private static class Entry {
		Class<?> cls;
		Item sample;
		String name;
		int tier;
	}

	private final ScrollingListPane list;

	public WndModItemList( Catalog catalog ) {
		list = new ScrollingListPane();
		add( list );
		list.addTitle( catalog.title() );

		ArrayList<Entry> entries = new ArrayList<>();
		for (Class<?> cls : catalog.items()) {
			Object o = Reflection.newInstance( cls );
			if (!(o instanceof Item)) continue;
			Entry e = new Entry();
			e.cls = cls;
			e.sample = (Item) o;
			e.name = Messages.titleCase( e.sample.trueName() );
			e.tier = e.sample instanceof MeleeWeapon ? ((MeleeWeapon) e.sample).tier
					: e.sample instanceof Armor ? ((Armor) e.sample).tier : 0;
			entries.add( e );
		}
		Collections.sort( entries, new Comparator<Entry>() {
			@Override
			public int compare( Entry a, Entry b ) {
				if (a.tier != b.tier) return a.tier - b.tier;
				return a.name.compareToIgnoreCase( b.name );
			}
		} );

		int lastTier = -1;
		for (final Entry e : entries) {
			if (e.tier > 0 && e.tier != lastTier) {
				list.addTitle( "Tier " + e.tier );
				lastTier = e.tier;
			}
			ScrollingListPane.ListItem item = new ScrollingListPane.ListItem( new ItemSprite( e.sample ), null, e.name ) {
				@Override
				public boolean onClick( float x, float y ) {
					if (inside( x, y )) {
						spawn( e.cls );
						return true;
					}
					return false;
				}
			};
			list.addItem( item );
		}

		resize( 120, Math.min( 144, (int) list.content().height() ) );
		list.setRect( 0, 0, width, height );
	}

	/** a fresh copy of the item, identified, into the backpack (on the floor beneath the hero when the pack is full) */
	private static void spawn( Class<?> cls ) {
		Object o = Reflection.newInstance( cls );
		if (!(o instanceof Item) || Dungeon.hero == null) return;
		Item item = (Item) o;
		item.identify();
		if (!item.collect()) {
			Dungeon.level.drop( item, Dungeon.hero.pos ).sprite.drop();
			GLog.w( "Pack full: " + item.name() + " dropped at your feet." );
		} else {
			GLog.p( "Spawned " + item.name() + "." );
		}
	}
}
