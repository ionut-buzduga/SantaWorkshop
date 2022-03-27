package santaworkshop;

import java.util.Random;

/**
 * application entry point
 */
public class Workshop {
	/**
	 * minimum number of factories
	 */
	static final int MINIMUM_NO_OF_FACTORIES = 2;
	/**
	 * maximum number of factories
	 */
	static final int MAXIMUM_NO_OF_FACTORIES = 5;
	/**
	 * minimum factory size
	 */
	static final int MINIMUM_FACTORY_DIMENSION = 100;
	/**
	 * maximum factory size
	 */
	static final int MAXIMUM_FACTORY_DIMENSION = 500;
	/**
	 * minimum number of reindeers
	 */
	static final int MINMUM_NO_OF_REINDEERS = 8;
	/**
	 * maximum number of reindeers
	 */
	static final int MAXIMUM_NO_OF_REINDEERS = 12;
	/**
	 * number of presents that must be created and sent to santa in order to stop the application
	 */
	static final int NO_OF_PRESENTS_TO_ACHIEVE_GOAL = 10000;
	/**
	 * current number of present that were created so far
	 */
	static volatile int CURRENT_NO_OF_PRESENTS = 0;
	/**
	 * <p>Instantiate objects of type Factory, Reindeer, GiftQueue, ElfStopper, ElfStarter, start and join threads
	 */
	public static void main(String[] args) {
		Random rand = new Random();
		//rand.nextInt((max - min) + 1) + min;
		int number_of_factories = rand.nextInt(MAXIMUM_NO_OF_FACTORIES - MINIMUM_NO_OF_FACTORIES) + MINIMUM_NO_OF_FACTORIES;
		int number_of_reindeers = rand.nextInt(MAXIMUM_NO_OF_REINDEERS - MINMUM_NO_OF_REINDEERS) + MINMUM_NO_OF_REINDEERS;
		System.out.println("number of reindeers: "+number_of_reindeers);
		int[] factory_dimension = new int[number_of_factories];
		Factory[] factory =  new Factory[number_of_factories];
		Thread[] reindeer = new Thread[number_of_reindeers];
		IGiftQueue[] giftQueue = new IGiftQueue[number_of_factories];
		ElfGenerator elf_spawner = new ElfGenerator(factory, number_of_factories);
		
		ElfRetire elf_stopper = new ElfRetire();
		for(int i = 0; i < number_of_factories; i++) {
			giftQueue[i] = new GiftQueue();
		}		
		for(int i = 0; i < number_of_factories; i++) {
			factory_dimension[i] = rand.nextInt(MAXIMUM_FACTORY_DIMENSION - MINIMUM_FACTORY_DIMENSION) + MINIMUM_FACTORY_DIMENSION;
			factory[i] = new Factory(i, factory_dimension[i], giftQueue[i]);
			factory[i].start();
		}		
		for(int i = 0; i < number_of_reindeers; i++) {
			reindeer[i] = new Reindeer(i, giftQueue, number_of_factories);
			reindeer[i].start();
		}	
		elf_spawner.start();
		elf_stopper.start();
		for(int i = 0; i < number_of_factories; i++) {
			try {
				factory[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.out.println("could not join factory "+i);
			}
		}
		try {
			elf_spawner.join();
		} catch (InterruptedException e) {
			System.out.println("Could not join elf_spawners");
			e.printStackTrace();
		}

		try {
			elf_stopper.join();
		} catch (InterruptedException e) {
			System.out.println("Colud not join  elf stopper");
			e.printStackTrace();
		}
		for(int i = 0; i < number_of_reindeers; i++) {
			try {
				reindeer[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.out.println("could not join reindeer "+i);
			}
		}
		System.out.println("workshop finished");
	}

}
