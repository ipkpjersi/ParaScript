package ParaScript.strategies;

import ParaScript.data.Variables;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.api.methods.*;

public class Eat implements Strategy {
    private int currentHealth = 0;

    @Override
    public boolean activate() {
        currentHealth = Players.getMyPlayer().getHealth();

        String mode = Variables.getEatingMode();
        int foodHeals = mode.equals("Thieving") ? Variables.thieving_food_heals_amount : Variables.fighting_food_heals_amount;
        int foodToEat = mode.equals("Thieving") ? Variables.thieving_food_to_eat : Variables.fighting_food_to_eat;

        if (Variables.running
                && hasRequiredItems(foodToEat)
                && currentHealth > 0
                && currentHealth <= (Skill.HITPOINTS.getRealLevel() - foodHeals)
        ) {
            Variables.setStatus("eating food");
            return true;
        }
        Variables.setStatus("none");
        return false;
    }

    @Override
    public void execute() {
        String mode = Variables.getEatingMode();
        int foodToEat = mode.equals("Thieving") ? Variables.thieving_food_to_eat : Variables.fighting_food_to_eat;

        try {
            Inventory.getItem(foodToEat + 1).interact(Items.Option.CONSUME);
            Time.sleep(() -> Players.getMyPlayer().getHealth() != currentHealth, 5000);
            Variables.setStatus("none");
        } catch (Exception ಠ_ಠ) {
            System.out.println("Eating error: ¯\\_(ツ)_/¯");
        }
    }

    private boolean hasRequiredItems(int foodToEat) {
        return Inventory.getItem(foodToEat + 1) != null;
    }
}