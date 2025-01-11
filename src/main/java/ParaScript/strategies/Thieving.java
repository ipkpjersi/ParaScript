package ParaScript.strategies;

import ParaScript.data.Variables;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.api.methods.Inventory;
import org.rev317.min.api.methods.Npcs;
import org.rev317.min.api.methods.Players;
import org.rev317.min.api.methods.SceneObjects;
import org.rev317.min.api.wrappers.Npc;
import org.rev317.min.api.wrappers.SceneObject;

public class Thieving implements Strategy {
    private Npc victim;

    @Override
    public boolean activate() {
        victim = victim(); //set the local Variable
        if (Variables.running
                && victim != null
                && !Players.getMyPlayer().isInCombat()
                && Players.getMyPlayer().getAnimation() == -1
                && !Inventory.isFull()) {
            if (!hasRequiredFood()) {
                if (Variables.shouldBankItems()) {
                    Variables.setStatus("banking");
                    return true; //Activate for banking when out of food
                }
                Variables.setStatus("none");
                return false;
            }
            Variables.setStatus("thieving");
            return true;
        }
        Variables.setStatus("none");
        return false;
    }

    @Override
    public void execute() {
        try {
            if (Variables.getStatus().equals("banking")) {
                handleBanking();
            } else if (Variables.getStatus().equals("thieving")) {
                victim.interact(Npcs.Option.PICKPOCKET);
                Time.sleep(1000);
                //Wait for the Player to finish pickpocketing (max 2 seconds)
                Time.sleep(() -> Players.getMyPlayer().getAnimation() == -1, 2000);
            }
        } catch (Exception ಠ_ಠ) {
            System.out.println("Thieving error: ¯\\_(ツ)_/¯");
        }
    }

    private Npc victim() {
        try {
            int[] npc_to_thieve = Variables.thieving_npc_selected.getIDs();
            for (Npc victim : Npcs.getNearest(npc_to_thieve)) {
                if (victim != null) {
                    return victim;
                }
            }
        } catch (Exception ಠ_ಠ) {}
        return null;
    }

    private boolean hasRequiredFood() {
        return Variables.thieving_food_to_eat == -1 || Inventory.getItem(Variables.thieving_food_to_eat + 1) != null;
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
                if (!hasRequiredFood()) {
                    //For some reason, we have to call Bank.withdrawItem with item_id, but Inventory.getItem must be called with item_id + 1. Whatever, it works. What doesn't work is withdraw x, since it never inputs the text just prompts the dialogue, so instead we can withdraw 10 twice and then 5.
                    Bank.withdrawItem(Variables.thieving_food_to_eat, 10);
                    Time.sleep(() -> Bank.isBankOpen(), 2000);
                    Bank.withdrawItem(Variables.thieving_food_to_eat, 10);
                    Time.sleep(() -> Bank.isBankOpen(), 2000);
                    Bank.withdrawItem(Variables.thieving_food_to_eat, 5);
                    Time.sleep(() -> Inventory.getItem(Variables.thieving_food_to_eat + 1) != null, 5000);
                }
                Bank.closeBank();
            }
            Variables.setStatus("none");
        } catch (Exception ಠ_ಠ) {
            System.out.println("Banking error: ¯\\_(ツ)_/¯");
        }
    }
}
