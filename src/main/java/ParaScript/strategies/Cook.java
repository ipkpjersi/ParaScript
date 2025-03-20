package ParaScript.strategies;

import java.awt.Point;

import ParaScript.data.Variables;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.api.methods.Inventory;
import org.rev317.min.api.methods.*;
import org.rev317.min.api.wrappers.SceneObject;


public class Cook implements Strategy {
	
	SceneObject stove = SceneObjects.getClosest(2728);
	Point p = new Point(246,409);
	
	/***************************************************************************************************************************************/
	
	@Override
	public boolean activate() {
		if (stove != null
				//&& Walking.stoveSpot.distanceTo() <= 3
				&& Inventory.isFull()
				&& Inventory.getCount(Variables.food_to_cook) > 0) {

			System.out.println("Cooking...");
			return true;
		}
		System.out.println("Cook check passed...");
		return false;
	}
	
	/***************************************************************************************************************************************/
	
	@Override
	public void execute() {
		while (Inventory.containts(Variables.food_to_cook)) {
			if (stove != null) {
				System.out.println("Range Found...");
				//use
				Menu.sendAction(447, Variables.food_to_cook - 1, getInventorySlot(Variables.food_to_cook), 3214);
				Time.sleep(400);
				//on range
				System.out.println("Using on range");
				Menu.sendAction(62,stove.getHash(), stove.getLocalRegionX(), stove.getLocalRegionY(),2728,1);
				Time.sleep(400);
				//Cook All
				Menu.sendAction(315,stove.getHash(), stove.getLocalRegionX(), 13717,2728,1);
				Time.sleep(2000);
				while (Players.getMyPlayer().getAnimation() != -1 && Game.isLoggedIn()) {
					Time.sleep(200);
				}
			}
		}
	}
	
	/***************************************************************************************************************************************/
	
	private static int getInventorySlot(int item) {
		org.rev317.min.api.wrappers.Item[] items = Inventory.getItems(item);
		if (items != null) {
			return items[0].getSlot();
		}
		return 0;
	}

}