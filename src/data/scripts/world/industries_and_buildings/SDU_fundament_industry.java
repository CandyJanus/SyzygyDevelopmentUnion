package data.scripts.world.industries_and_buildings;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.OrbitalStation;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactory;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactory.PatrolType;


public class SDU_fundament_industry extends OrbitalStation {
    //The defense bonus provided by the industry: 0.5f = 50% bonus (Station level), 2f = 200% bonus (Star Fortress level) etc.
    private static final float DEFENSE_BONUS = 4f;
    private static PatrolType MEGALOS_ESCORT_TYPE= PatrolType.HEAVY;

    private static final String MEGALOS_DEFENDER_VARIANT_1= "seven_megalos_kidemonas_Guardian";
    private static final String MEGALOS_DEFENDER_VARIANT_2= "seven_megalos_kidemonas_AssaultGuardian";
    private static final String MEGALOS_CORE_TYPE= "betacore";

    private static final int DEFENDERCOUNT = 5;

    private static org.apache.log4j.Logger log = Global.getLogger(SDU_fundament_industry.class);

    @Override
    public boolean isAvailableToBuild() {
        return false;
    }

    @Override
    public String getUnavailableReason() {
        return "Station type unavailable.";
    }

    @Override
    public boolean showWhenUnavailable() {
        return false;
    }


    //Changes the cost and bonuses provided by the station
    //Copied from vanilla, and adjusted to use our custom defense levels
    @Override
    public void apply() {
        super.apply(false);

        for(Industry I : market.getIndustries()){
            I.getDemandReduction().modifyFlat("fundamentreduction", 10);
        }

        int size = 7; // To match a star fortress

        modifyStabilityWithBaseMod();

        applyIncomeAndUpkeep(size);

        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD)
                .modifyMult(getModId(), 1f + DEFENSE_BONUS, getNameForModifier());

        matchCommanderToAICore(aiCoreId);

        if (!isFunctional()) {
            supply.clear();
            unapply();
        } else {
            applyCRToStation();
        }
    }

    // Adds the special AI commander core. Mostly copied from the vanilla implementation, except we don't care about AI core
    //  -- TODO: Double-check that this matches up to vanilla expectations nowadays
    @Override
    protected void matchCommanderToAICore(String aiCore) {
        if (stationFleet == null) return;

        //log.info(Global.getSector().getImportantPeople().getPerson("sdu_decimus").getName().getFullName());
        //log.info(stationFleet.getFlagship().getHullId());

        if(Global.getSector().getImportantPeople().getPerson("sdu_decimus")!=null && stationFleet!=null && stationFleet.getFlagship()!=null){
            stationFleet.getFlagship().setCaptain(Global.getSector().getImportantPeople().getPerson("sdu_decimus"));
        }
        //for(String h : stationFleet.getFlagship().getVariant().getHullMods()){
        //            if(Global.getSettings().getHullModSpec(h).hasTag("dmod"));
        //            stationFleet.getFlagship().getVariant().removeMod(h);
        //        }
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if (Global.getSector().getEconomy().isSimMode()) return;
        if (!isFunctional()) return;
    }

    public static CampaignFleetAPI spawnProelFleet(MarketAPI market) {

        PatrolType type = MEGALOS_ESCORT_TYPE;

        CampaignFleetAPI fleet=FleetFactory.createPatrol(MEGALOS_ESCORT_TYPE, market.getFaction(), market.getStabilityValue(), market.getShipQualityFactor(), null);

        FactionAPI sdu = Global.getSector().getFaction("SDU");

        FleetDataAPI fleetData=fleet.getFleetData();
        fleetData.clear();
        for (int i = 0; i < DEFENDERCOUNT; i++) {
            FleetMemberAPI ship = fleetData.addFleetMember(MEGALOS_DEFENDER_VARIANT_1);
            ship.setCaptain(OfficerManagerEvent.createOfficer(sdu, 10));
        }

        if (fleet == null || fleet.isEmpty()) return null;

        fleet.setFaction("SDU", true);
        fleet.setNoFactionInName(true);

        if (type == PatrolType.COMBAT) {
            fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_CUSTOMS_INSPECTOR, true);
        }

        String postId = Ranks.POST_PATROL_COMMANDER;
        String rankId = Ranks.SPACE_COMMANDER;
        switch (type) {
            case COMBAT:
                rankId = Ranks.SPACE_COMMANDER;
                break;
            case HEAVY:
                rankId = Ranks.SPACE_CAPTAIN;
                break;
        }

        fleet.getCommander().setPostId(postId);
        fleet.getCommander().setRankId(rankId);

        market.getContainingLocation().addEntity(fleet);
        fleet.setFacing((float) Math.random() * 360f);
        fleet.setLocation(market.getPrimaryEntity().getLocation().x, market.getPrimaryEntity().getLocation().y);

        fleet.addAssignment(FleetAssignment.HOLD, market.getPrimaryEntity(), 99999f);
        return fleet;
    }

}