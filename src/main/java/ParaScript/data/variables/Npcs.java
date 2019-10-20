package ParaScript.data.variables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Npcs {
    MAN_WOMAN("Man & Woman", new int[]{1, 2, 3, 4, 5, 6}, 8),
    CUSTOM("Custom (specify IDs)", new int[]{-1}, 0);

    private String name;
    private int[] ids;
    private double xp;

    Npcs(String name, int[] ids, double xp) {
        this.name = name;
        this.ids = ids;
        this.xp = xp;
    }

    public static String[] toStringArray() {
        List<Npcs> enumList = Arrays.asList(Npcs.values());
        List<String> npcsArray = new ArrayList<>();
        for (Npcs npc : enumList) {
            npcsArray.add(npc.name);
        }

        String[] simpleArray = new String[ npcsArray.size() ];
        npcsArray.toArray( simpleArray );
        return(simpleArray);
    }

    public String getName() { return this.name; }

    public int[] getIDs() { return this.ids; }

    public void setIDs(int[] ids) {
        if (this == CUSTOM){
            this.ids = ids;
        }
    }

    public double getXP() { return this.xp; }
}
