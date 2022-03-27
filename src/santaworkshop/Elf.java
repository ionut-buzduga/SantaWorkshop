package santaworkshop;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * moves and creates gifts
 */
public class Elf extends Thread{
	/**
	 * current number of presents created by elves. When this is equal with the goal, all threads except reindeers can stop working
	 */
	public static volatile int elf_current_number_of_presents = 0;
	/**
	 * a lock used to ensure exclusive access to the number o gifts variable
	 */
	public static Lock gift_lock = new ReentrantLock();
	/**
	 * a lock used to ensure exclusive access to the elf positions in a map
	 */
	public static Lock move_lock = new ReentrantLock();
	/**
	 * position on x and y coordinated of an elf in a factory
	 */
	private ElfLocation position;
	/**
	 * queue of gifts; common with a factory, elves and reindeers
	 */
	public IGiftQueue gift_queue;
	/**
	 * dimmension of the matrix factory the elf belongs to
	 */
	public int factory_size;/**
	 * map that contains all elves positions
	 */
	HashMap<String,Elf> elf_map_position;
	/**
	 * identifier of the factory the elf belongs to
	 */
	public int factory_id; 
	/**
	 * cyclic barrier used to make elves wait when they are on the main diagonal untill there are elves on all positions of the main diagonal
	 */
	public OwnCyclicBarrier barrier;
	/**
	 * Constructor of the Elf class. Used to initialize fields
	 * @param position Coordinates x and y of elf in factory
	 * @param factory_size factory size
	 * @param elf_map_position Map that has the coordinates of all elves in factor
	 * @param gift_queue queue where elves write gifts
	 * @param factory_id factory identifier
	 * @param barrier cyclic barrier used to make elves wait when they are on the main diagonal untill there are elves on all positions of the main diagonal
	 */
	Elf( ElfLocation position, int factory_size, HashMap<String,Elf> elf_map_position, IGiftQueue gift_queue, int factory_id, OwnCyclicBarrier barrier){
		this.position = position;
		this.factory_size = factory_size;
		this.elf_map_position = elf_map_position;
		this.gift_queue = gift_queue;
		this.factory_id = factory_id;
		this.barrier = barrier;
	}
	
	/**
	 * Returns the position of an elf in the factory
	 * @param position Coordinates x and y of elf in factory
	*/
	public ElfLocation getPosition() {
		return this.position;
	}
	/**
	 * Checks if the elf moved on the main diagonal. If so, generate a random value 1 or 0 in order to
	 * choose if the elf waits at the barrier or makes another move. 
	 * If the choice is 1, call await method of the barrier
	 */
	private void checkIfOnDiagonalAndRestOrJump() {
        Random rand = new Random();
		int choice = rand.nextInt(2);
        if (this.position.x == this.position.y) {
            if (choice == 0) {
            	System.out.println("elf at position "+position.x +" "+position.y+" is at barrier ---------------------------------------------");
                barrier.await();
            }else {
            	moveElf();
            	checkIfOnDiagonalAndRestOrJump();
            }
        }
    }
	/**
	 * Main method called when thread starts
	 * Move elf, check if it is on diagonal and try to stop elf
	 */
	public void run() {
		while((Elf.elf_current_number_of_presents < Workshop.NO_OF_PRESENTS_TO_ACHIEVE_GOAL)) {
			moveElf();
			checkIfOnDiagonalAndRestOrJump();
			tryToStopElf();
		}		
		System.out.println("elf in factory "+factory_id +" finished");
		
	}

	/**
	 * try to acquire a semaphore permit for an amount of time and if you succeed the elf enters a finite retirement state
	 */
	private synchronized void tryToStopElf() {
		try {
			if(ElfRetire.stop_elf_semaphore.tryAcquire(100, TimeUnit.MILLISECONDS)){
				System.out.println("Elf in factory "+factory_id+" is stopping");
				wait(1000,1000);
				System.out.println("Elf in factory "+factory_id+" is restarting");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * check if the elf can move in a direction and if so move, otherwise sleep 
	 */
	private void moveElf() {
		if(canMoveRight()) {
			moveRight();
		}else if(canMoveUp()) {
			moveUp();
		}else if(canMoveDown()) {
			moveDown();
		}else if(canMoveLeft()) {
			moveLeft();
		}else restBecauseYouAreSurrounded();
	}
	/**
	 * Checks if the elf can move right, i.e. there isn't another elf there and the elf is still within the factory boundaries
	 * @return true if he can move, false otherwise 
	 */
	private boolean canMoveRight() {
		if(position.x+1 < factory_size)
			if(elf_map_position.get((position.x+1)+" "+position.y)==null)
				return true;
		return false;
	}
	/**
	 * update position and call methods to increment number of gifts and to rest
	 */
	private void moveRight() {
		removeOldPosition();
		System.out.println(" Elf in factory " + factory_id+" moved right from "+this.position.x+" " +this.position.y);
		this.position.x += 1;
		addNewPosition();
		createGift();
		restAfterCreatingPresent();
	}
	/**
	 * Checks if the elf can move up, i.e. there isn't another elf there and the elf is still within the factory boundaries
	 * @return true if he can move, false otherwise 
	 */
	private boolean canMoveUp() {
		if(position.y-1>0)
			if(elf_map_position.get(position.x+" "+(position.y-1))==null)
				return true;
		return false;
	}
	/**
	 * update position and call methods to increment number of gifts and to rest
	 */
	private void moveUp() {
		removeOldPosition();
		System.out.println(" Elf in factory " + factory_id+" moved up from "+this.position.x+" " +this.position.y);
		this.position.y -= 1;
		addNewPosition();
		createGift();
		restAfterCreatingPresent();
	}
	/**
	 * Checks if the elf can move down, i.e. there isn't another elf there and the elf is still within the factory boundaries
	 * @return true if he can move, false otherwise 
	 */
	private boolean canMoveDown() {
		if(position.y+1 < (factory_size))
			if(elf_map_position.get(position.x+" "+(position.y+1))==null)
				return true;
		return false;
	}
	/**
	 * update position and call methods to increment number of gifts and to rest
	 */
	private void moveDown() {
		removeOldPosition();
		System.out.println(" Elf in factory " + factory_id+" moved down from "+this.position.x+" " +this.position.y);
		this.position.y += 1;
		addNewPosition();
		createGift();
		restAfterCreatingPresent();
	}

	/**
	 * Checks if the elf can move left, i.e. there isn't another elf there and the elf is still within the factory boundaries
	 * @return true if he can move, false otherwise 
	 */
	private boolean canMoveLeft() {
		if(position.x-1>0)
			if(elf_map_position.get((position.x-1)+" "+position.y)==null)
				return true;
		return false;
	}
	/**
	 * update position and call methods to increment number of gifts and to rest
	 */
	private void moveLeft() {
		removeOldPosition();
		System.out.println(" Elf in factory " + factory_id+" moved left from "+this.position.x+" " +this.position.y);
		this.position.x -= 1;
		addNewPosition();
		createGift();
		restAfterCreatingPresent();		
	}
	
	/**
	 * generate a random number and sleep thread
	 */
	private void restBecauseYouAreSurrounded() {
		Random rand = new Random();
		int max_sleep_time = 50;
		int min_sleep_time = 10;
		int sleep_time = rand.nextInt(max_sleep_time - min_sleep_time) + min_sleep_time;
		try {
			System.out.println("elf in factory "+factory_id+" at position "+this.position.x+" " +this.position.y+" is surronded and is resting");
			Thread.sleep(sleep_time);
		} catch (InterruptedException e) {
			System.out.println("Elf could not rest although it's surrounded");
			e.printStackTrace();
		}
		
	}
	/**
	 * generate a random number and sleep thread
	 */
	private void restAfterCreatingPresent() {
		try {
			System.out.println("elf in factory "+factory_id+" created present and is resting   current count = "+Elf.elf_current_number_of_presents);
			Thread.sleep(30);
		} catch (InterruptedException e) {
			System.out.println("Elf could not sleep after creating present ");
			e.printStackTrace();
		}
		
	}
	/**
	 * increment number of gifts
	 */
	private synchronized void createGift() {
		gift_lock.lock();
		gift_queue.push(new Gift(new ElfLocation(position.x,position.y)));
		Elf.elf_current_number_of_presents++;
		gift_lock.unlock();
	}
	/**
	 * update elf position in map
	 */
	private synchronized void addNewPosition() {
		move_lock.lock();
		elf_map_position.put(position.x+" "+position.y, this);	
		move_lock.unlock();
	}
	/**
	 * remove from map the position where there no longer exists an elf
	 */
	private synchronized void removeOldPosition() {
		move_lock.lock();
		elf_map_position.remove(position.x+" "+position.y);
		move_lock.unlock();
	}


}
