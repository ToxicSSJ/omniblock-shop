package net.omniblock.shop.api.object.npc.object;

import java.util.ArrayList;
import java.util.List;

public class InventorySlotter {

	protected SlotLocatorType type;
	protected int pos;
	
	public InventorySlotter(SlotLocatorType type) {
		
		this.type = type;
		this.pos = -1;
		
	}
	
	public boolean hasNext() {
		
		if(pos + 1 >= type.getSlots().size())
			return false;
		
		return true;
			
	}
	
	public int next() {
		
		pos++;
		return type.getSlots().get(pos);
		
	}
	
	public void reset() {
		
		pos = -1;
		return;
		
	}
	
	public static enum SlotLocatorType {
		
		ROUND_SIX(
				10, 11, 12, 13, 14, 15, 16,
				19, 20, 21, 22, 23, 24, 25,
				28, 29, 30, 31, 32, 33, 34
				),
		
		;
		
		private List<Integer> slots = new ArrayList<Integer>();
		
		SlotLocatorType(int...slots){
			
			for(int slot : slots)
				this.slots.add(slot);
			
		}
		
		public List<Integer> getSlots(){
			return slots;
		}
		
	}
	
}
