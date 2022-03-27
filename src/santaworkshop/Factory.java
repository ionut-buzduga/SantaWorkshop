package santaworkshop;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
/**
 * asks elves for their coordinates
 */
public class Factory extends Thread {
	/**
	 * factory identifier
	 */
	int id;
	/**
	 * factory size
	 */
	public int factory_dimension;
	/**
	 * gift queue common with elves and reindeers
	 */
	IGiftQueue gift_queue;
	/**
	 * maximum number of elves that can be spawned in a factory
	 */
	public int max_number_of_elves;
	/**
	 * vector of elves in factory
	 */
	Vector<Elf> elf;
	/**
	 * current number of elves in factory
	 */
	public int current_number_of_elves = 0;
	/**
	 * barrier that will be injected to all elves in factory
	 */
	OwnCyclicBarrier barrier;
	/**
	 * hash map of elves and their position in factory
	 */
	HashMap<String,Elf> elf_map_position = new HashMap<String, Elf>();
	/**
	 * Constructor. Initializez fiels
	 * @param id factory identifier
	 * @param factory_dimmension factory  size
	 * @param gift_queue queue commeon with elves and reindeers
	 */
	Factory(int id, int factory_dimmension, IGiftQueue gift_queue){
		this.id = id;
		this.factory_dimension = factory_dimmension;
		this.gift_queue = gift_queue;
		max_number_of_elves = factory_dimmension/2;
		elf = new Vector<Elf>(max_number_of_elves);
		barrier = new OwnCyclicBarrier(factory_dimmension);
		System.out.println("Created factory "+id+" whith maximum no of elves = "+max_number_of_elves);
	}
	/**
	 * Method checks if there is enough space to add another elf. If so, it generates a random position untill
	 * it gets a valid one and places the elf in that position.
	 */
	public synchronized void addElfToFactory() {
		if(current_number_of_elves < max_number_of_elves) {
			Random rand = new Random();
			int x = rand.nextInt(factory_dimension);
			int y = rand.nextInt(factory_dimension);
			while(elf_map_position.get(x+" "+y) != null) {
				 x = rand.nextInt(factory_dimension);
				 y = rand.nextInt(factory_dimension);
			}
			ElfLocation pos = new ElfLocation(x,y);
			elf.add(current_number_of_elves, new Elf(pos ,factory_dimension,elf_map_position, gift_queue,id,barrier));
			elf_map_position.put((pos.x+" "+pos.y), elf.get(current_number_of_elves));
			elf.get(current_number_of_elves).start();
			current_number_of_elves++;
			System.out.println("added elf to factory "+this.id+" at position "+pos.x+" "+pos.y+" current count = "+current_number_of_elves);
		
		}
	}
	
	/**
	 * asks the elves for teir positions
	 */
	public void run() {
		while(Elf.elf_current_number_of_presents  < Workshop.NO_OF_PRESENTS_TO_ACHIEVE_GOAL) {
			for(int i = 0 ; i < current_number_of_elves; i++) {
			elf.get(i).getPosition();
		}
		}
		System.out.println("Factory "+id+" finished");
	}
	
	/**
	 * print the map with elves and corresponding positions
	 */
	public synchronized void printAllElvesInFactory() {
		System.out.println("Factory "+id+" of size "+factory_dimension+" with "+current_number_of_elves+" elves");
		for (String name: elf_map_position.keySet()){
            String key =name;
            String value = elf_map_position.get(name).toString();  
            System.out.println(key + " " + value);  
		} 
	}
}
