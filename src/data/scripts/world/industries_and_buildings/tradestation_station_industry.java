package data.scripts.world.industries_and_buildings;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.econ.impl.MilitaryBase;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import com.fs.starfarer.api.impl.campaign.fleets.*;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.campaign.CampaignEventListener.FleetDespawnReason;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactory.PatrolType;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager.RouteData;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager.RouteFleetSpawner;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;

import java.util.Random;


public class tradestation_station_industry extends BaseIndustry implements RouteFleetSpawner, FleetEventListener {
    //The defense bonus provided by the industry: 0.5f = 50% bonus (Station level), 2f = 200% bonus (Star Fortress level) etc.
    private static final float DEFENSE_BONUS = 6f;
    private static PatrolType MEGALOS_ESCORT_TYPE= PatrolType.HEAVY;

    private static final String MEGALOS_DEFENDER_VARIANT_1= "seven_megalos_kidemonas_Guardian";
    private static final String MEGALOS_DEFENDER_VARIANT_2= "seven_megalos_kidemonas_AssaultGuardian";

    private static final int DEFENDERCOUNT = 7;

    @Override
    public boolean isFunctional() {
        return super.isFunctional();
    }
    public void apply() {
        super.apply(true);

        int size = market.getSize();

        modifyStabilityWithBaseMod();

        MemoryAPI memory = market.getMemoryWithoutUpdate();
        Misc.setFlagWithReason(memory, MemFlags.MARKET_PATROL, getModId(), true, -1);
        Misc.setFlagWithReason(memory, MemFlags.MARKET_MILITARY, getModId(), true, -1);

        if (!isFunctional()) {
            supply.clear();
            unapply();
        }

    }
    @Override
    public void unapply() {
        super.unapply();
        MemoryAPI memory = market.getMemoryWithoutUpdate();
        Misc.setFlagWithReason(memory, MemFlags.MARKET_PATROL, getModId(), false, -1);
        Misc.setFlagWithReason(memory, MemFlags.MARKET_MILITARY, getModId(), false, -1);

        unmodifyStabilityWithBaseMod();
    }
    protected boolean hasPostDemandSection(boolean hasDemand, IndustryTooltipMode mode) {
        return mode != IndustryTooltipMode.NORMAL || isFunctional();
    }
    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        if (mode != IndustryTooltipMode.NORMAL || isFunctional()) {
            addStabilityPostDemandSection(tooltip, hasDemand, mode);
        }
    }
    @Override
    protected int getBaseStabilityMod() {
        return 2;
    }

    public String getNameForModifier() {

        return "Tradebase Industry";
    }
    @Override
    protected Pair<String, Integer> getStabilityAffectingDeficit() {
        return getMaxDeficit(Commodities.SUPPLIES, Commodities.FUEL, Commodities.SHIPS, Commodities.HAND_WEAPONS);
    }
    @Override
    public String getCurrentImage() {
        return super.getCurrentImage();
    }
    public boolean isDemandLegal(CommodityOnMarketAPI com) {
        return true;
    }
    public boolean isSupplyLegal(CommodityOnMarketAPI com) {
        return true;
    }
    protected IntervalUtil tracker = new IntervalUtil(
            Global.getSettings().getFloat("averagePatrolSpawnInterval") * 0.7f,
            Global.getSettings().getFloat("averagePatrolSpawnInterval") * 1.3f);
    protected float returningPatrolValue = 0f;
    @Override
    protected void buildingFinished() {
        super.buildingFinished();

        tracker.forceIntervalElapsed();
    }
    @Override
    protected void upgradeFinished(Industry previous) {
        super.upgradeFinished(previous);

        tracker.forceIntervalElapsed();
    }
    @Override
    public void advance(float amount) {
        super.advance(amount);
        if (Global.getSector().getEconomy().isSimMode()) return;
        if (!isFunctional()) return;

        float days = Global.getSector().getClock().convertToDays(amount);
        float spawnRate = 2f;
        float rateMult = market.getStats().getDynamic().getStat(Stats.COMBAT_FLEET_SPAWN_RATE_MULT).getModifiedValue();
        spawnRate *= rateMult;

        float extraTime = 0f;
        if (returningPatrolValue > 0) {
            // apply "returned patrols" to spawn rate, at a maximum rate of 1 interval per day
            float interval = tracker.getIntervalDuration();
            extraTime = interval * days;
            returningPatrolValue -= days;
            if (returningPatrolValue < 0) returningPatrolValue = 0;
        }
        tracker.advance(days * spawnRate + extraTime);

        if (tracker.intervalElapsed() && getCount(PatrolType.COMBAT) < 1) {
            String sid = getRouteSourceId();

            PatrolType type = PatrolType.COMBAT;

            MilitaryBase.PatrolFleetData custom = new MilitaryBase.PatrolFleetData(type);

            RouteManager.OptionalFleetData extra = new RouteManager.OptionalFleetData(market);
            extra.fleetType = type.getFleetType();

            RouteData route = RouteManager.getInstance().addRoute(sid, market, Misc.genRandomSeed(), extra, this, custom);
            float patrolDays = 35f + (float) Math.random() * 10f;

            route.addSegment(new RouteManager.RouteSegment(patrolDays, market.getPrimaryEntity()));
        }
    }

    public int getCount(PatrolType ... types) {
        int count = 0;
        for (RouteData data : RouteManager.getInstance().getRoutesForSource(getRouteSourceId())) {
            if (data.getCustom() instanceof MilitaryBase.PatrolFleetData) {
                MilitaryBase.PatrolFleetData custom = (MilitaryBase.PatrolFleetData) data.getCustom();
                for (PatrolType type : types) {
                    if (type == custom.type) {
                        count++;
                        break;
                    }
                }
            }
        }
        return count;
    }

    public void reportAboutToBeDespawnedByRouteManager(RouteData route) {
    }

    public boolean shouldRepeat(RouteData route) {
        return false;
    }

    public boolean shouldCancelRouteAfterDelayCheck(RouteData route) {
        return false;
    }

    public void reportBattleOccurred(CampaignFleetAPI fleet, CampaignFleetAPI primaryWinner, BattleAPI battle) {
    }

    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, FleetDespawnReason reason, Object param) {
        if (!isFunctional()) return;

        if (reason == FleetDespawnReason.REACHED_DESTINATION) {
            RouteData route = RouteManager.getInstance().getRoute(getRouteSourceId(), fleet);
            if (route.getCustom() instanceof MilitaryBase.PatrolFleetData) {
                MilitaryBase.PatrolFleetData custom = (MilitaryBase.PatrolFleetData) route.getCustom();
                if (custom.spawnFP > 0) {
                    float fraction  = fleet.getFleetPoints() / custom.spawnFP;
                    returningPatrolValue += fraction;
                }
            }
        }
    }

    public CampaignFleetAPI spawnFleet(RouteData route) {

        MilitaryBase.PatrolFleetData custom = (MilitaryBase.PatrolFleetData) route.getCustom();
        PatrolType type = custom.type;

        Random random = route.getRandom();

        float combat = 0f;
        float tanker = 0f;
        float freighter = 0f;
        String fleetType = type.getFleetType();
        FleetParamsV3 params = new FleetParamsV3(
                market,
                null, // loc in hyper; don't need if have market
                "SDU",
                route.getQualityOverride(), // quality override
                fleetType,
                combat, // combatPts
                freighter, // freighterPts
                tanker, // tankerPts
                0f, // transportPts
                0f, // linerPts
                0f, // utilityPts
                0f // qualityMod - since the Lion's Guard is in a different-faction market, counter that penalty
        );
        params.timestamp = route.getTimestamp();
        params.random = random;
        //params.modeOverride = Misc.getShipPickMode(market);
        //params.modeOverride = ShipPickMode.PRIORITY_THEN_ALL;
        CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);

        FactionAPI sdu = Global.getSector().getFaction("SDU");

        for (int i = 0; i < DEFENDERCOUNT; i++) {
            FleetMemberAPI ship = fleet.getFleetData().addFleetMember(MEGALOS_DEFENDER_VARIANT_1);
            ship.setCaptain(OfficerManagerEvent.createOfficer(sdu, 7));
        }

        if (fleet == null || fleet.isEmpty()) return null;

        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PATROL_FLEET, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_IGNORES_OTHER_FLEETS, true, 0.3f);

        if (type == PatrolType.COMBAT) {
            fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_CUSTOMS_INSPECTOR, true);
        }

        String postId = Ranks.POST_PATROL_COMMANDER;
        String rankId = Ranks.SPACE_COMMANDER;
        switch (type) {
//        case FAST:
//            rankId = Ranks.SPACE_LIEUTENANT;
//            break;
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
        // this will get overridden by the patrol assignment AI, depending on route-time elapsed etc
        fleet.setLocation(market.getPrimaryEntity().getLocation().x, market.getPrimaryEntity().getLocation().y);

        fleet.addScript(new PatrolAssignmentAIV4(fleet, route));

        return fleet;
    }
    public String getRouteSourceId() {
        return getMarket().getId() + "_" + "SDU";
    }
    @Override
    public boolean isAvailableToBuild() {
        return false;
    }
    public boolean showWhenUnavailable() {
        return false;
    }
    @Override
    public boolean canImprove() {
        return false;
    }
    @Override
    public MarketCMD.RaidDangerLevel adjustCommodityDangerLevel(String commodityId, MarketCMD.RaidDangerLevel level) {
        return level.next();
    }
    @Override
    public MarketCMD.RaidDangerLevel adjustItemDangerLevel(String itemId, String data, MarketCMD.RaidDangerLevel level) {
        return level.next();
    }
}