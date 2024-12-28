package ParaScript.strategies;

import ParaScript.data.Variables;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.api.methods.*;
import org.rev317.min.api.wrappers.Npc;
import org.rev317.min.api.wrappers.Tile;
import org.rev317.min.api.wrappers.TilePath;

public class Fish implements Strategy {
    private static Npc fishingSpot = null;

    @Override
    public boolean activate() {
        fishingSpot = fishingSpot(); // set the local Variable
        if (Variables.running
                && fishingSpot != null
                && (Variables.getStatus() == "none" || Variables.getStatus() == "fishing")
                && !Players.getMyPlayer().isInCombat()
                && Players.getMyPlayer().getAnimation() == -1
                && !Inventory.isFull()) {
            Variables.setStatus("fishing");
            return true;
        }
        Variables.setStatus("none");
        return false;
    }

    @Override
    public void execute() {
        try {
            if (fishingSpot != null) {
                if (Variables.shouldBankItems()) {
                    Tile startingTile = new Tile(fishingSpot.getLocation().getX(), fishingSpot.getLocation().getY());
                    //Check for 14 tiles distance, since we can easily click within 14 tiles, otherwise we have to walk to it. 
                    //This works at Fishing Guild and Catherby.
                    while (startingTile != null && startingTile.distanceTo() > 14) {
                        if (!Game.isLoggedIn()) new HandleLogin().execute();
                        Walking.walkTo(startingTile);
                        Time.sleep(5000);
                    }
                }

                fishingSpot.interact(Variables.fishing_spot_selected.actionType);
                Time.sleep(1000);

                // Wait for the Player to finish fishing (max 60 seconds)
                Time.sleep(() -> Players.getMyPlayer().getAnimation() == -1, 60000);
            } else {
                System.out.println("Error: fishingSpot is null somehow!");
            }
        } catch (Exception ಠ_ಠ){
            System.out.println("Fishing error: ¯\\_(ツ)_/¯");
        }
    }

    private static Npc fishingSpot(){
        //Cache the fishingSpot by returning it early if it's set, that way in fishing guild if we want to fish sharks, we only go to the shark spot by starting the script at the shark spot and returning early here.
        if (fishingSpot != null) {
            return fishingSpot;
        }
        try {
            for(Npc spot : Npcs.getNearest(Variables.fishing_spot_selected.getIDs())){
                if (spot != null)
                    return spot;
            }
        } catch (Exception ಠ_ಠ){}
        return null;
    }
}