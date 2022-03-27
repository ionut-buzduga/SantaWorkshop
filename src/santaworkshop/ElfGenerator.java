package santaworkshop;

import java.util.Random;
/**
 * adds elves to their factory
 */
public class ElfGenerator extends Thread {
	/**
	 * minimum sleeping time
	 */
	final int MAXIMUM_SLEEPING_TIME = 1000;
	/**
	 * maximum sleeping time
	 */
	final int MINIMUM_SLEEPING_TIME = 500;
	/**
	 * array of existing factories where elves may be spawned
	 */
	public Factory[] factory;
	/**
	 * current number of factories
	 */
	int no_of_factories;
	/**
	 * Constructor. Initializes fields
	 */
	ElfGenerator(Thread[] factory, int no_of_factories){
		this.factory = (Factory[]) factory;
		this.no_of_factories = no_of_factories;
	}
	/**
	 * Method called when thread starts.
	 * The threads sleeps for a random ammount of time, then it picks a random factory and calls a method in the factory to 
	 * spawn elves there. The thread stops this routine when the goal is achieved and prints the final position of elves.
	 */
	public void run() {
		Random rand = new Random();
		int random_factory;
		int sleeping_time;
		while(Elf.elf_current_number_of_presents  < Workshop.NO_OF_PRESENTS_TO_ACHIEVE_GOAL) {
			sleeping_time = rand.nextInt(MAXIMUM_SLEEPING_TIME - MINIMUM_SLEEPING_TIME) + MINIMUM_SLEEPING_TIME;
			try {
				Thread.sleep(sleeping_time);
			} catch (InterruptedException e) {
				System.out.println("Elf spawner can't sleep");
				e.printStackTrace();
			}
			random_factory = rand.nextInt(no_of_factories);
			factory[random_factory].addElfToFactory(); 
		}
		for(int i = 0 ; i < no_of_factories; i++) {
			factory[i].printAllElvesInFactory();
		}
		System.out.println("elf spawner finished");
	}
}
