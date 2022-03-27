package santaworkshop;

import java.util.concurrent.Semaphore;
/**
 * releases a semaphore once in a while. elve try to ge the semapgore in order to retire
 */
public class ElfRetire extends Thread{
	/**
	 * semaphore initially blocked. used for elf retirement
	 */
	static Semaphore stop_elf_semaphore = new Semaphore(0);
	
	/**
	 * Method called when thread starts.
	 * The threads sleeps for a random amount of time,it releases a permit to the semaphore. The elves will try to get it in order
	 * to retire
	 */
	public void run() {
		while(Elf.elf_current_number_of_presents  < Workshop.NO_OF_PRESENTS_TO_ACHIEVE_GOAL ) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				System.out.println("Elf stopper can't sleep");
				e.printStackTrace();
			}
			stop_elf_semaphore.release();
		}
		System.out.println("elf stopper finished");
	}
}
