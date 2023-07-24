package data.scripts.everyframe;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.loading.Description;
import exerelin.utilities.NexConfig;
import exerelin.utilities.NexFactionConfig;
import exerelin.utilities.NexFactionConfig.StartFleetSet;
import exerelin.utilities.NexFactionConfig.StartFleetType;
import java.util.ArrayList;
import java.util.List;

public class SDU_PluginStarter extends BaseEveryFrameCombatPlugin {

    private boolean addedOnce = false;
    private boolean addedOnce1 = false;

    public static String ANANKEDESC = "A step towards complete transcendence. Your strength is vast and your potential is limitless. Honor the trust which has been placed upon you by the origin of your cause and those that revere him.";

    @Override
    public void init(CombatEngineAPI engine) {
        if (!addedOnce && Global.getSettings().getMissionScore("TheGift") > 0 && Global.getSettings().getModManager().isModEnabled("nexerelin")) {
            NexFactionConfig faction = NexConfig.getFactionConfig("SDU");
            StartFleetSet fleetSet = faction.getStartFleetSet(StartFleetType.SUPER.name());
            List<String> anankeFleet = new ArrayList<>(1);
            anankeFleet.add("SDU_ananke_gift");
            if(fleetSet!=null){
                fleetSet.addFleet(anankeFleet);
                Global.getSettings().getDescription("SDU_ananke", Description.Type.SHIP).setText1(ANANKEDESC);
                addedOnce = true;
            }
        }
    }
}
