package com.erebus.reclaimedpixeldungeon.windows;

import com.erebus.reclaimedpixeldungeon.Dungeon;
import com.erebus.reclaimedpixeldungeon.items.Item;
import com.erebus.reclaimedpixeldungeon.journal.Catalog;
import com.erebus.reclaimedpixeldungeon.scenes.GameScene;
import com.erebus.reclaimedpixeldungeon.sprites.ItemSprite;
import com.erebus.reclaimedpixeldungeon.ui.ScrollingListPane;
import com.erebus.reclaimedpixeldungeon.ui.Window;
import com.watabou.utils.Reflection;

/** The mod menu's item categories: equipment, then consumables, each as the game's own catalog lists them. */
public class WndModItems extends Window {

	private final ScrollingListPane list;

	public WndModItems() {
		list = new ScrollingListPane();
		add( list );

		list.addTitle( "Spawn an item: equipment" );
		for (Catalog c : Catalog.equipmentCatalogs) addCategory( c );
		list.addTitle( "Spawn an item: consumables" );
		for (Catalog c : Catalog.consumableCatalogs) addCategory( c );

		list.addTitle( "Spawn an item: special" );
		list.addItem( new ScrollingListPane.ListItem(
				new ItemSprite( new com.erebus.reclaimedpixeldungeon.items.keys.GoldenSkeletonKey() ), null, "Golden Skeleton Key" ) {
			@Override
			public boolean onClick( float x, float y ) {
				if (inside( x, y )) {
					WndModItemList.spawn( com.erebus.reclaimedpixeldungeon.items.keys.GoldenSkeletonKey.class );
					return true;
				}
				return false;
			}
		} );

		resize( 120, Math.min( 144, (int) list.content().height() ) );
		list.setRect( 0, 0, width, height );
	}

	private void addCategory( final Catalog catalog ) {
		//enchantments and glyphs are not items
		if (catalog == Catalog.ENCHANTMENTS || catalog == Catalog.GLYPHS) return;
		Item sample = null;
		for (Class<?> cls : catalog.items()) {
			Object o = Reflection.newInstance( cls );
			if (o instanceof Item) { sample = (Item) o; break; }
		}
		if (sample == null) return;
		ScrollingListPane.ListItem item = new ScrollingListPane.ListItem(
				new ItemSprite( sample ), null, catalog.title() + " (" + catalog.totalItems() + ")" ) {
			@Override
			public boolean onClick( float x, float y ) {
				if (inside( x, y )) {
					GameScene.show( new WndModItemList( catalog ) );
					return true;
				}
				return false;
			}
		};
		list.addItem( item );
	}
}
