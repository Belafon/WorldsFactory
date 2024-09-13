package com.belafon.world.visibles.items.itemsSpecialStats;

import com.belafon.world.visibles.creatures.Player;

public class SpecialFoodsProperties {
	private int size;
	SpecialFoodsProperties(int size){
		this.setSize(size);
	}
	public void eat(Player player) {
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
}
