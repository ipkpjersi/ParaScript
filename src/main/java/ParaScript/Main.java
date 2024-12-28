package ParaScript;

import ParaScript.data.Variables;
import ParaScript.strategies.*;
import ParaScript.ui.UI;
import org.parabot.environment.api.interfaces.Paintable;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.api.utils.Timer;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.scripts.framework.Strategy;
import org.parabot.environment.scripts.Category;
import org.parabot.environment.scripts.ScriptManifest;
import org.rev317.min.api.events.MessageEvent;
import org.rev317.min.api.events.listeners.MessageListener;
import org.rev317.min.api.methods.Skill;

import java.awt.*;
import java.util.ArrayList;

@ScriptManifest(author = "RedSparr0w & Dark98", category = Category.OTHER, description = "ParaScript", name = "ParaScript", servers = { "2006rebotted" }, version = 1.4)
public class Main extends Script implements MessageListener, Paintable {

    private final ArrayList<Strategy> strategies = new ArrayList<Strategy>();
    private UI ui = new UI();
    public static Timer SCRIPT_TIMER;

    @Override
    public boolean onExecute() {
        ui.setVisible(true);
        while (!Variables.running) {
            Time.sleep(300);
        }

        SCRIPT_TIMER = new Timer();

        Variables.setBaseExp();

        // These strategies should always be running
        strategies.add(new UpdateBank());
        strategies.add(new UpdateExperience());
        strategies.add(new ScriptState());

        // if(Variables.skill_to_train == Skill.CRAFTING) {
        //     strategies.add(new Crafting());
        // }
        if(Variables.skill_to_train == Skill.WOODCUTTING) {
            strategies.add(new Fletch());
            strategies.add(new WoodcutTree());
            strategies.add(new Bank());
            strategies.add(new Walk());
        }
        if(Variables.skill_to_train == Skill.MINING) {
            strategies.add(new Mine());
            strategies.add(new Bank());
            strategies.add(new Walk());
        }
        // if(Variables.skill_to_train == Skill.SMITHING) {
        //     strategies.add(new Smelt());
        //     strategies.add(new BankSmithing());
        // }
        if(Variables.skill_to_train == Skill.THIEVING) {
            strategies.add(new Thieving());
        }
        if(Variables.skill_to_train == Skill.ATTACK) {
            // Activate auto retaliate
            org.rev317.min.api.methods.Menu.clickButton(150);
            strategies.add(new Eat());
            strategies.add(new PickupClues());
            strategies.add(new PickupItems());
            strategies.add(new BuryBones());
            strategies.add(new FightingReturnToCoords());
            strategies.add(new LoadCannon());
            strategies.add(new Fighting());
            //In order to implement banking for combat training, we would need to add something like a combat_method dropdown for bank, or something like a checkbox for banking items
            //strategies.add(new Bank());
            //strategies.add(new Walk());
        }
        if(Variables.skill_to_train == Skill.FISHING) {
            strategies.add(new Bank());
            strategies.add(new Walk());
            strategies.add(new Fish());
        }
        if(Variables.skill_to_train == null) {
            strategies.add(new Bank());
            strategies.add(new Walk());
            strategies.add(new PickupItems());
        }
        if(Variables.skill_to_train == Skill.PRAYER) {
            strategies.add(new Prayer());
        }
        
        // These strategies should always be running
        strategies.add(new Drop());
        strategies.add(new HandleLogin());
        provide(strategies);
        return true;
    }

    @Override
    public void onFinish() {
        Variables.desktopTray.removeTray();
        ui.dispose();
        System.out.println("ParaScript Stopped");
    }

    @Override
    public void paint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        Color c2=new Color(0f,.749f,1.0f,.3f );
        g.setColor(c2);
        g.setBackground(c2);
        g.fillRect(355, 232, 160, 20);

        Color c=new Color(.686f,.933f,.933f,.3f );
        g.setColor(c);
        g.setBackground(c);
        g.fillRect(355, 252, 160, 85);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("ParaScript", 360, 247);
        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.drawString("Status: " + Variables.getStatus(), 360, 270);
        if (SCRIPT_TIMER == null) return;
        g.drawString("Items(P/H): " + Methods.formatNumber(Variables.items_gained) + "(" + Methods.formatNumber(SCRIPT_TIMER.getPerHour(Variables.items_gained)) + ")", 360, 290);
        g.drawString("EXP(P/H): " + Methods.formatNumber((int) Variables.exp_gained) + "(" + Methods.formatNumber(SCRIPT_TIMER.getPerHour((int) Variables.exp_gained)) + ")", 360, 310);
        g.drawString("Runtime: " + SCRIPT_TIMER.toString(), 360, 330);
    }

    public void messageReceived(MessageEvent message) {
        switch (message.getType()) {
            case 0:
                if (message.getMessage().startsWith("You manage to ")   // Woodcutting, Mining
                    || message.getMessage().startsWith("You catch ")    // Fishing
                    || message.getMessage().startsWith("You receive a") // Smelting
                    || message.getMessage().startsWith("You pick the ") // Pickpocketing
                    ) {
                        Variables.addItemGained(1);
                        Variables.updateExpGained();
                }
                if (message.getMessage().startsWith("Congratulations, you've advanced a level")) {
                    Variables.desktopTray.displayNotification(Variables.getAccountUsername() + " | Level up!", message.getMessage());
                    // add in level up to paint
                }
                if (message.getMessage().startsWith("You completed your slayer task")) {
                    Variables.desktopTray.displayNotification(Variables.getAccountUsername() + " | Slayer task complete!", message.getMessage());
                }
                break;
            case 4:
                if(Variables.skill_to_train == null) {
                    if (message.getMessage().startsWith(Variables.slave_master.toLowerCase() + " wishes to trade with you")) {
                        // accept trade
                        // take items, give items if smithing or similar
                        // goto bank, deposit/withdraw items
                        // go back to user
                    }
                }
                break;
        }
    }
}
