package cmsc433;

/**
 * We create all food objects used by the simulation in one place, here.
 * This allows us to safely check equality via reference, rather than by
 * structure/values.
 */
public class FoodType {
	public static final Food fries = new Food("fries", 30);
	public static final Food pizza = new Food("pizza", 90);
	public static final Food subs = new Food("subs", 50);
	public static final Food soda = new Food("soda", 2);
}
