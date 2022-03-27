package santaworkshop;

/**
 * elves add to a queue instances of this class. reindeers read them and send them to server
 */
public class Gift {
	/**
	 * coordinates at which the gift was created
	 */
	ElfLocation position;
	/**
	 * Constructor. Initializes fields.
	 * @param position coordinates at which the gift was created
	 */
	Gift(ElfLocation position){
		this.position = position;
	}
}
