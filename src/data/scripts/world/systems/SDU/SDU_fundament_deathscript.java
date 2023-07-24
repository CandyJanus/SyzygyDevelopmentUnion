package data.scripts.world.systems.SDU;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;

import java.awt.*;

/**
 * this script is mostly based off of Anargaia's script cuz nicke said ok and im p sure this is the only way to do it
 */
public class SDU_fundament_deathscript implements FleetEventListener, EveryFrameScript {

    private SectorEntityToken stationEntity;
    private MarketAPI market;
    private StarSystemAPI system;

    private CampaignFleetAPI addedListenerTo = null;

    private boolean isDying = false;

    public static Logger log = Global.getLogger(SDU_fundament_deathscript.class);

    public SDU_fundament_deathscript(SectorEntityToken stationEntity, MarketAPI market) {
        this.stationEntity = stationEntity;
        this.market = market;
        this.system = stationEntity.getStarSystem();
        Global.getSector().addScript(this);
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        SectorAPI sector = Global.getSector();
        //Failsafe, in case we somehow run when paused
        if (sector.isPaused()) {
            amount = 0f;
        }

        //Are we currently dying? In that case, track our death progress and apply appropriate effects
        if (isDying) {
            sector.removeScript(this);
        }
        //Otherwise, make sure the correct fleet has our listener
        else if (Misc.getStationFleet(market) != null) {
            CampaignFleetAPI stationFleet = Misc.getStationFleet(market);
            if (stationFleet != addedListenerTo) {
                if (addedListenerTo != null) {
                    addedListenerTo.removeEventListener(this);
                }
                addedListenerTo = stationFleet;
                addedListenerTo.addEventListener(this);
            }
        }
    }

    @Override
    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {

        //Also, only trigger on our own station fleet
        if (addedListenerTo != null && fleet == addedListenerTo) {

            if(reason.equals(CampaignEventListener.FleetDespawnReason.DESTROYED_BY_BATTLE)) {
                //Expires the station
                Misc.fadeAndExpire(stationEntity, 2f);
                Misc.fadeAndExpire(addedListenerTo, 2f);
                log.info("dying");
                system.addHitParticle(stationEntity.getLocation(), Misc.ZERO, 500, 1f, 0.5f, Color.white);
                system.getStar().setRadius(100f);
                //system.initStar("SDU_fundament_star", // unique id for this star
                //                        "star_white", // id in planets.json
                //                        200f,        // radius (in pixels at default zoom)
                //                        100, // corona radius, from star edge
                //                        5f, // solar wind burn level
                //                        1f, // flare probability
                //                        2f); // cr loss mult
            }

            Global.getSector().getEconomy().removeMarket(market);
            Misc.removeRadioChatter(market);
            market.advance(0f);
            if (market.getId().equals("SDU_fundament_stationmarket") || (Global.getSector().getStarSystem("fundament").getEntityById("SDU_fundament_station") == null)) {
                market.setFactionId(Factions.NEUTRAL);
                addedListenerTo.setFaction(Factions.NEUTRAL);
                market = null;
                addedListenerTo = null;
            }

            //Also indicate that our death sequence has begun...
            isDying = true;
        }
    }

    @Override
    public void reportBattleOccurred(CampaignFleetAPI fleet, CampaignFleetAPI primaryWinner, BattleAPI battle) {

    }
}
