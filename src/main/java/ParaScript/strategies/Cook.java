package ParaScript.strategies;

import java.awt.Point;

import ParaScript.data.Variables;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.api.methods.Inventory;
import org.rev317.min.api.methods.*;
import org.rev317.min.api.wrappers.Npc;
import org.rev317.min.api.wrappers.SceneObject;



public class Cook implements Strategy {

    private SceneObject stove;

    @Override
    public boolean activate() {
        stove = SceneObjects.getClosest(2728, 12268, 12269, 12270);

        if (!Variables.running) {
            return false;
        }

		//System.out.println("Cooking script is running.");
        // If inventory is full and we have raw food, cook
        if (stove != null && Inventory.getCount(Variables.food_to_cook + 1) > 0) {
			System.out.println("Food found and range found, to cook!");
            Variables.setStatus("cooking");
            return true;
        }
		
		if (stove == null) {
			System.out.println("Could not find stove!");
		}

        // If we have no food left, go to bank
        if (Inventory.getCount(Variables.food_to_cook + 1) == 0) {
			System.out.println("Food not found, going to bank");
            Variables.setStatus("banking");
            return true;
        }
		//System.out.println("Food not found and couldn't withdraw from bank, cancelling cooking!");
        Variables.setStatus("none");
        return false;
    }

    @Override
    public void execute() {
        try {
            if (Variables.getStatus().equals("banking")) {
                handleBanking();
            } else if (Variables.getStatus().equals("cooking")) {
                cookFood();
            }
        } catch (Exception e) {
            System.out.println("Cooking error: " + e);
        }
    }

    private void cookFood() {
        while (Inventory.contains(Variables.food_to_cook + 1)) {
            if (stove != null) {
                System.out.println("Range Found...");

                // Use raw food on range
                Menu.sendAction(447, Variables.food_to_cook, getInventorySlot(Variables.food_to_cook + 1), 3214);
                Time.sleep(400);

                // Interact with range
                System.out.println("Using on range");
                Menu.sendAction(62, stove.getHash(), stove.getLocalRegionX(), stove.getLocalRegionY(), 2728, 1);
                Time.sleep(400);

                // Cook All
                Menu.sendAction(315, stove.getHash(), stove.getLocalRegionX(), 13717, 2728, 1);
                Time.sleep(2000);

                // Wait for cooking animation to stop
                while (Players.getMyPlayer().getAnimation() != -1 && Game.isLoggedIn()) {
                    Time.sleep(200);
                }
            }
        }
    }

    private void handleBanking() {
        try {
            SceneObject bank_booth = SceneObjects.getClosest(11338, 3045, 5276, 6084, 11758, 14367, 4483, 3194, 10517, 2213);
            if (bank_booth != null) {
                bank_booth.interact(SceneObjects.Option.USE_QUICKLY);
                Time.sleep(() -> Bank.isBankOpen(), 5000);
            }

            Npc banker = Npcs.getClosest(953, 166, 1702, 495, 496, 497, 498, 499, 567, 1036, 1360, 2163, 2164, 2354, 2355, 2568, 2569, 2570, 2271, 494, 2619);
            if (bank_booth == null && banker != null) {
                banker.interact(Npcs.Option.BANK);
                Time.sleep(() -> Bank.isBankOpen(), 5000);
            }

            if (Bank.isBankOpen()) {
                if (Inventory.getCount(Variables.food_to_cook + 1) == 0) {
					System.out.println("Need raw food, withdrawing then closing bank.");
					Bank.depositItem(Variables.food_to_cook + 2, 10);
					Time.sleep(() -> Bank.isBankOpen(), 1000);
					Bank.depositItem(Variables.food_to_cook + 4, 10);
					Time.sleep(() -> Bank.isBankOpen(), 2000);
					Bank.depositItem(Variables.food_to_cook + 2, 10);
					Time.sleep(() -> Bank.isBankOpen(), 2000);
					Bank.depositItem(Variables.food_to_cook + 2, 5);
                    Bank.withdrawItem(Variables.food_to_cook, 10);
                    Time.sleep(() -> Bank.isBankOpen(), 2000);
                    Bank.withdrawItem(Variables.food_to_cook, 10);
                    Time.sleep(() -> Bank.isBankOpen(), 2000);
                    Bank.withdrawItem(Variables.food_to_cook, 5);
                    Time.sleep(() -> Inventory.getItem(Variables.food_to_cook + 1) != null, 5000);
					System.out.println("Should have enough raw food now!");
                } else {
					System.out.println("Have enough raw food, closing bank.");
				}
                Bank.closeBank();
            }
            Variables.setStatus("none");
        } catch (Exception ಠ_ಠ) {
            System.out.println("Banking error: ¯\\_(ツ)_/¯");
        }
    }

    private static int getInventorySlot(int item) {
        org.rev317.min.api.wrappers.Item[] items = Inventory.getItems(item);
        if (items != null) {
            return items[0].getSlot();
        }
        return 0;
    }
}