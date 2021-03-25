package cmsc433;

/**
 * Food is what is prepared by Cooks, and ordered by Customers. Food
 * is defined by its name, and the amount of time it takes to prepare
 * by Machine. It is an immutable class.
 */
public class Food {
	public final String name;
	public final int cookTime10S;

	public Food(String name, int cookTime10S) {
		this.name = name;
		this.cookTime10S = cookTime10S;
	}

	public String toString() {
		return name;
	}
}
