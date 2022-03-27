package santaworkshop;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
/**
 * queue gift consummer. client
 */
public class Reindeer extends Thread {
	/**
	 * total number of factories (random between 2 and 5)
	 */
	int number_of_factories;
	/**
	 * gift queue, synchronized with monitors, common with elves and factories
	 */
	public IGiftQueue gift_queue[];
	/**
	 * reindeer identifier
	 */
	int id;
	/**
	 * array that keeps track of the number of reindeers that read every factory (can't be greater than 10 at a time)
	 */
	int[] reindeers_per_factory;
	/**
	 * maximum  number of reindeers that can read a factory at a time
	 */
	int max_no_of_reindeers_that_can_read_factory = 10;
	/**
	 * lock to protect incrementation and decrementation of the number of reindeers per factory variables
	 */
	static ReentrantLock reindeer_lock = new ReentrantLock();
	/**
     * Constructor of the Reindeer class
     * <p>It initializes private members
     * @param id reindeer identifier
     * @param gift_queue common for elves, reindeers and factories; reindeer read gifts from here
     * @param number_of_factories numbe of existing factories        
     */
	Reindeer(int id, IGiftQueue gift_queue[], int number_of_factories){
		this.id = id;
		this.gift_queue = gift_queue;
		this.number_of_factories = number_of_factories;
		reindeers_per_factory = new int[number_of_factories];
		for(int i = 0; i < number_of_factories; i++) {
			reindeers_per_factory[i] = 0;
		}
	}
	/**
     * method called when each thread is started. 
     * <p>
     * each reindeer is a client to the santa server. he randomly picks a free factory and reads gifts from its queue
     * when elves are not writing. When they succeed they send the gift by tcp/ip to the server.
     * they stop when the presents goal is achieved.
     * </p>
     */
	public void run() {
		Socket s = null;
		int serverPort = 6789;		
		Random rand = new Random();
		while(Workshop.CURRENT_NO_OF_PRESENTS < Workshop.NO_OF_PRESENTS_TO_ACHIEVE_GOAL) {
			int current_factory = rand.nextInt(number_of_factories);
			while(reindeers_per_factory[current_factory] > max_no_of_reindeers_that_can_read_factory && Workshop.CURRENT_NO_OF_PRESENTS < Workshop.NO_OF_PRESENTS_TO_ACHIEVE_GOAL) {
				current_factory = rand.nextInt(number_of_factories);
			}
			reindeer_lock.lock();
			reindeers_per_factory[current_factory]++;
			reindeer_lock.unlock();
			Gift gift = gift_queue[current_factory].pop();
			if(gift!=null){				
				 try {
					 s = new Socket("localhost", serverPort);
					 DataInputStream in = new DataInputStream(s.getInputStream());
					 DataOutputStream out = new DataOutputStream(s.getOutputStream());
					 out.writeUTF("Reindeer  sent one gift to Santa"); // UTF is a string encoding.
					 String data = in.readUTF();
					 System.out.println("Received: "+ data) ;
					 Workshop.CURRENT_NO_OF_PRESENTS++;
					 System.out.println(Workshop.CURRENT_NO_OF_PRESENTS);
				 }
				 catch (UnknownHostException e) {
					 System.out.println("Socket:"+e.getMessage());
				 } catch (EOFException e) {
					 System.out.println("EOF:"+e.getMessage());
				 } catch (IOException e) {
					 System.out.println("readline:"+e.getMessage());
				 } finally {
					 if (s != null)
					 try {
						 s.close();
					 } catch (IOException e) {
						 System.out.println("close:"+e.getMessage());
					 }
				 }
				
				}
			reindeer_lock.lock();
			reindeers_per_factory[current_factory]--;
			reindeer_lock.unlock();
			}
		System.out.println("reindeer "+id+" finished");
	}
	
}
