package santaworkshop;
/**
 * row and collumn number atwhcih elves or gifts are located
 */
public class ElfLocation {
	/**
	 * x coordinate
	 */
	public int x;
	/**
	 * y coordinate
	 */
	public int y;
	/**
	 * Constuctor. Initializes fields
	 * @param x row coordinate
	 * @param y column coordinate
	 */
	public ElfLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
}