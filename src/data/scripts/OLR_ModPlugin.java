package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import exerelin.campaign.SectorManager;

public class OLR_ModPlugin extends BaseModPlugin {
    @Override
    public void onApplicationLoad() throws Exception {
//   waq
    }

    @Override
    public void onNewGame() {
        super.onNewGame();

        // The code below requires that Nexerelin is added as a library (not a dependency, it's only needed to compile the mod).
       boolean isNexerelinEnabled = Global.getSettings().getModManager().isModEnabled("nexerelin");

//        if (!isNexerelinEnabled || SectorManager.getManager().isCorvusMode()) {
//            throw new RuntimeException("One Last Round is not intended for use with Nexerelin randomly generated sectors, as the narrative and mechanics of the mod are designed around the locations of existing Starsector worlds.");
//            //       new MySectorGen().generate(Global.getSector());
//            // Add code that creates a new star system (will only run if Nexerelin's Random (corvus) mode is disabled).
//        }
    }
}
