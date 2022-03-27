package santaworkshop;

import java.util.LinkedList;
import java.util.Queue;

class GiftQueue implements IGiftQueue {
	/**
	 * gift queue
	 */
	Queue<Gift> queue = new LinkedList<Gift>();
	/**
	 * variable that keeps trakc if the elf is writing in queue or not
	 */
	boolean isElfPushingGift = false;
	

	/**
	 * adds a gift to the queue
	 * @param value gift to be added in queue
	 */
	public void push(Gift value) {
		synchronized (queue) {
			isElfPushingGift = true;
			queue.add(value);
			System.out.println("Elf added in queue gift at position " + value.position.x+" "+value.position.y);
			isElfPushingGift = false;
		}

	}
	/**
	 * Removes the gift from queue if noone is writing, otherwise returns null
	 * @param position coordinates at which the gift was created
	 */
	public Gift pop() {
		Gift item = null;	
		synchronized (queue) {
			if (!isElfPushingGift) {				
				item = queue.poll();
				if (null == item ) {			
					return item;
							
				}				
			}
			System.out.println( "Reindeer received gift at position " + item.position.x+" "+item.position.y);
			return item;
		}

	}

	}