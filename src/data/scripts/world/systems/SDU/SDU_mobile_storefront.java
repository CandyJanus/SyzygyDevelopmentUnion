package data.scripts.world.systems.SDU;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import data.scripts.utils.SDU_util_sysgen;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * this script is mostly based off of Anargaia's script cuz nicke said ok and im
 * p sure this is the only way to do it
 */
public class SDU_mobile_storefront {
    /* -- BASIC GENERATION SETTINGS -- */
    // - Waypoints on world map to move across; loops around (1->2->3->1->2 ...)
    private static final List<Vector2f> WAYPOINTS = new ArrayList<>();

    static {
        WAYPOINTS.add(new Vector2f(-18000f, -30000f));
        WAYPOINTS.add(new Vector2f(-26000f, 0f));
        WAYPOINTS.add(new Vector2f(-18000f, 30000f));
        WAYPOINTS.add(new Vector2f(18000f, 30000f));
        WAYPOINTS.add(new Vector2f(26000f, 0f));
        WAYPOINTS.add(new Vector2f(18000f, -30000f));
    }
    // - Speed on the world sector map
    private static final float SPEED = 10f;

    /* -- STAR PARTICLE SETTINGS -- */
    //Streaking stars to spawn on average each second
    private static final float STAR_AMOUNT_PER_SECOND = 150f;
    //Speed of streaking stars (average: parralax is also applied here)
    private static final float STAR_PARTICLE_SPEED = 250f;
    //Size of streaking stars (average: parralax also applies)
    private static final float STAR_PARTICLE_SIZE = 7f;
    /* -- ENGINE PLUME SETTINGS -- */
    //Particles for the engine plume each second
    private static final float ENGINE_PARTICLES_PER_SECOND = 250f;
    //Speed of engine plume particles, minimum and maximum
    private static final float ENGINE_PARTICLE_SPEED_MIN = 75f;
    private static final float ENGINE_PARTICLE_SPEED_MAX = 150;
    //Size of the engine plume particles, minimum and maximum
    private static final float ENGINE_PARTICLE_SIZE_MIN = 8f;
    private static final float ENGINE_PARTICLE_SIZE_MAX = 12f;
    //Distance from the center of fundament that the engine plume starts spawning
    private static final float ENGINE_SPAWN_DISTANCE = 70f;
    //How "wide" the plume of the engine is at the base of the engine
    private static final float ENGINE_BASE_WIDTH = 20f;
    //Angle width of the engine plume
    private static final float ENGINE_ANGLE = 5f;
    //Minimum and maximum lifetime of the engine particles
    private static final float ENGINE_PARTICLE_DURATION_MIN = 1.4f;
    private static final float ENGINE_PARTICLE_DURATION_MAX = 2.2f;
    //Particle color of the engine plume (due to rendering, some white will be mixed in as well)
    private static final Color ENGINE_PARTICLE_COLOR = new Color(255, 255, 255);

    private static int STOREFRONTCOUNT = 5;

    //Main generation function
    public void generate(SectorAPI sector) {

        for (int i = 0; i < STOREFRONTCOUNT; i++) {

            String systemname = "";
            String stationid = "";
            String stationname = "";
            String jumpid = "";
            String jumpname = "";
            String submarket = "";
            int subfaction = 1;

            switch (i){
                case 0:
                    systemname = "Syzygy Actuators Tradebase";
                    stationid = "SDU_syzygyacutators_base";
                    stationname = "SDU Trading Station: Syzygy Actuators";
                    jumpid = "syzygy_base_rift";
                    jumpname = "Syzygy Actuators Rift";
                    submarket = "SDUSyzygyMarket";
                    subfaction = 1;
                    break;

                case 1:
                    systemname = "Kantina Combine Tradebase";
                    stationid = "SDU_kantinacombine_base";
                    stationname = "SDU Trading Station: Kantina Combine";
                    jumpid = "kantina_base_rift";
                    jumpname = "Kantina Combine Rift";
                    submarket = "SDUKantinaCombineMarket";
                    subfaction = 3;
                    break;

                case 2:
                    systemname = "Chutnam Security Tradebase";
                    stationid = "SDU_chutnamsecurity_base";
                    stationname = "SDU Trading Station: Chutnam Security";
                    jumpid = "chutnam_base_rift";
                    jumpname = "Chutnam Security Rift";
                    submarket = "SDUChutnamSecurityMarket";
                    subfaction = 4;
                    break;

                case 3:
                    systemname = "AdProSec Tradebase";
                    stationid = "SDU_adprosec_base";
                    stationname = "SDU Trading Station: AdProSec";
                    jumpid = "adprosec_base_rift";
                    jumpname = "AdProSec Rift";
                    submarket = "SDUAdProSecMarket";
                    subfaction = 5;
                    break;

                case 4:
                    systemname = "Shooting Stars Tradebase";
                    stationid = "SDU_shootingstars_base";
                    stationname = "SDU Trading Station: Shooting Stars";
                    jumpid = "shootingstars_base_rift";
                    jumpname = "Shooting Stars Rift";
                    submarket = "SDUShootingStarsMarket";
                    subfaction = 6;
                    break;

                case 5:
                    systemname = "Protectors Garrison Tradebase";
                    stationid = "SDU_protectorsgarrison_base";
                    stationname = "SDU Trading Station: Protectors Garrison";
                    jumpid = "protectors_base_rift";
                    jumpname = "Protectors Garrison Rift";
                    submarket = "SDUProtectorsMarket";
                    subfaction = 2;
                    break;

            }

            StarSystemAPI system = sector.createStarSystem(systemname);

            system.getLocation().set(WAYPOINTS.get(0));
            LocationAPI hyper = Global.getSector().getHyperspace();

            system.setBackgroundTextureFilename("graphics/backgrounds/background3.jpg");

            // Create a centerpoint in the system, for everything to rotate around.
            SectorEntityToken centerpoint = system.initNonStarCenter();

            //Change our type to NEBULA to indicate we have no centerpoint. Might have some consequences, but this is the only way I found that is neat to do
            system.setType(StarSystemGenerator.StarSystemType.NEBULA);

            // Sets light color in entire system, affects all entities
            system.setLightColor(new Color(255, 255, 255));

            // Adds the giant space station fundament
            SectorEntityToken tradebase = system.addCustomEntity(stationid, stationname, "tradestation_station", "SDU");
            tradebase.setCircularOrbitPointingDown(centerpoint, 0f, 50f, 99999999999f);

            // Add the marketplace to fundament ---------------
            MarketAPI tradebase_market = SDU_util_sysgen.addMarketplace("SDU",
                    tradebase,
                    null,
                    "SDU Tradestation Market", // name of the market
                    3, // size of the market
                    new ArrayList<>(
                            Arrays.asList( // list of market conditions
                                    Conditions.POPULATION_3,
                                    Conditions.OUTPOST,
                                    Conditions.FREE_PORT
                            )
                    ),
                    new ArrayList<>(
                            Arrays.asList( // which submarkets to generate
                                    Submarkets.GENERIC_MILITARY,
                                    Submarkets.SUBMARKET_BLACK,
                                    Submarkets.SUBMARKET_OPEN,
                                    Submarkets.SUBMARKET_STORAGE,
                                    "SDUEliteBuyersMarket",
                                    submarket
                            )
                    ),
                    new ArrayList<>(
                            Arrays.asList(
                                    Industries.POPULATION,
                                    Industries.SPACEPORT,
                                    Industries.WAYSTATION,
                                    Industries.HEAVYBATTERIES,
                                    Industries.STARFORTRESS_HIGH,
                                    "SDU_station_manufactory"
                            )
                    ),
                    false,
                    false
            );

            tradebase_market.setHidden(false);

            // Jump point : has a miniature script setting its rotation to be oppositely-locked to fundament ---------------
            JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint(jumpid, jumpname);
            jumpPoint.setCircularOrbit(centerpoint, 180f, 900f, 99999999999f);
            system.addEntity(jumpPoint);
            new OrbitOppositePlugin(jumpPoint, tradebase);

            // generates hyperspace destinations for in-system jump points
            system.autogenerateHyperspaceJumpPoints(false, false);
            system.generateAnchorIfNeeded();

            tradebase.getLocationInHyperspace().set(system.getLocation());

            sector.addScript(new MovingStarsystemScript(tradebase, system, subfaction));

            Global.getSector().getIntelManager().addIntel(new SDU_spawnstorefrontintel(tradebase_market, "SDU",subfaction));
        }
    }

    private class OrbitOppositePlugin implements EveryFrameScript {

        SectorEntityToken orbiter;
        SectorEntityToken target;

        OrbitOppositePlugin(SectorEntityToken orbiter, SectorEntityToken target) {
            this.orbiter = orbiter;
            this.target = target;
            orbiter.getStarSystem().addScript(this);
        }

        @Override
        public void advance(float amount) {

            if(target!=null) {
                if (!target.getFaction().equals(Global.getSector().getFaction("SDU")) && Global.getSector().getStarSystem("fundament").getEntityById("SDU_fundament_station") != null) {
                    target.getMarket().getHazard().modifyFlat("hostilemachinery", 2f);
                    if(!Global.getSector().getPersistentData().containsKey(target.getId()+"datetaken")) {
                        Global.getSector().getPersistentData().put(target.getId()+"datetaken",Global.getSector().getClock().getTimestamp());
                    }else if(Global.getSector().getPersistentData().containsKey(target.getId()+"datetaken")) {
                        float red = Math.max(Math.round(-(Global.getSector().getClock().getTimestamp()-(long)Global.getSector().getPersistentData().get(target.getId()+"datetaken"))*0.45f/100000000f),-10);

                        target.getMarket().getStability().modifyFlat("hostilemachinery", red, "hostile machinery");

                        if(red == -10) {
                            target.setFaction("SDU");
                            target.getMarket().setFactionId("SDU");
                            target.getMarket().setAdmin(OfficerManagerEvent.createAdmin(Global.getSector().getFaction("SDU"),2,null));
                        }

                        //if(target.getMarket().getStability().getModifiedValue()<=1f){
                        //                            target.setFaction("SDU");
                        //                            target.getMarket().setFactionId("SDU");
                        //                            target.getMarket().getStability().removeTemporaryMod("hostilemachinery");
                        //                            Global.getSector().getPersistentData().remove(target.getId()+"datetaken");
                        //                        }

                    }
                } else {
                    target.getMarket().getHazard().unmodifyFlat("hostilemachinery");
                    target.getMarket().getStability().unmodifyFlat("hostilemachinery");
                }
            }

            //Only run if the player is in our system
            if (Global.getSector().getPlayerFleet() != null
                    && Global.getSector().getPlayerFleet().getContainingLocation() == orbiter.getContainingLocation()) {
                //Ensure our orbits line up opposite to one another
                OrbitAPI orbiterOrbit = orbiter.getOrbit();
                OrbitAPI targetOrbit = target.getOrbit();
                if (orbiterOrbit != null && targetOrbit != null) {
                    orbiter.setCircularOrbitAngle(target.getCircularOrbitAngle() + 180f);
                }
            }
        }

        @Override
        public boolean runWhilePaused() {
            return false;
        }

        @Override
        public boolean isDone() {
            return false;
        }
    }

    //Manages moving the starsystem itself: turns to face the right direction, and spawns particle effects for a "speed" feeling
    private class MovingStarsystemScript implements EveryFrameScript {

        private SectorEntityToken starStation;
        private StarSystemAPI system;
        private float currentFacing = 0f;
        private int currentWaypoint = 0;
        private boolean runOnce = false;
        boolean marketIsOpen = false;
        private Vector2f storedLoc = null;

        private MovingStarsystemScript(SectorEntityToken starStation, StarSystemAPI system, int subfaction) {
            this.starStation = starStation;
            this.system = system;

            /*
            Spawns us in a random spot on our "track"
            currentWaypoint = MathUtils.getRandomNumberInRange(0, WAYPOINTS.size() - 1);
                        Vector2f targetPos = new Vector2f(WAYPOINTS.get(currentWaypoint));

                        float distanceAlongToTarget = MathUtils.getRandomNumberInRange(0f, 1f);
                        int previousPoint = currentWaypoint - 1;
                        if (previousPoint < 0) {
                            previousPoint = WAYPOINTS.size() - 1;
                        }
                        Vector2f previousPos = new Vector2f(WAYPOINTS.get(previousPoint));
                        Vector2f spawnPos = Misc.interpolateVector(previousPos, targetPos, distanceAlongToTarget);
                        system.getLocation().x = spawnPos.x;
                        system.getLocation().y = spawnPos.y;
            */
            Vector2f spawnPos = WAYPOINTS.get(subfaction-1);
            system.getLocation().x = spawnPos.x;
            system.getLocation().y = spawnPos.y;
            system.getLocation().set(spawnPos.x, spawnPos.y);
        }

        @Override
        public void advance(float amount) {
            if (Global.getSector().isPaused()) {
                amount = 0f;
            }

            //Moves our location closer to the next waypoint. If we reach it, set us to aim for the next waypoint, too
            if (MathUtils.getDistance(system.getLocation(), WAYPOINTS.get(currentWaypoint)) < amount * SPEED) {
                //Don't shift end-waypoints if we're in the system itself: just halt in that case
                if (!Global.getSector().getPlayerFleet().getContainingLocation().equals(starStation.getContainingLocation())) {
                    system.getLocation().x = WAYPOINTS.get(currentWaypoint).x;
                    system.getLocation().y = WAYPOINTS.get(currentWaypoint).y;
                    currentWaypoint++;
                    if (currentWaypoint >= WAYPOINTS.size()) {
                        currentWaypoint = 0;
                    }
                }
            } else {
                Vector2f dirVector = VectorUtils.getDirectionalVector(system.getLocation(), WAYPOINTS.get(currentWaypoint));
                dirVector.scale(amount * SPEED);
                system.getLocation().x += dirVector.x;
                system.getLocation().y += dirVector.y;

                system.setMapGridHeightOverride(system.getLocation().y);
                system.setMapGridWidthOverride(system.getLocation().x);
            }

            //Always adjust our angle to point towards our "correct" facing: if the player is in the system, we only adjust rotation once
            if (Global.getSector().getPlayerFleet().getContainingLocation().equals(starStation.getContainingLocation())) {
                if (runOnce) {
                    runOnce = false;
                    starStation.setCircularOrbitAngle(currentFacing + 0f);
                }

                //We also spawn streaking stars when the player is in the system
                spawnStreakingStars(amount);

                //We *also* spawn a particle plume behind fundament itself
                spawnEnginePlume(amount, starStation);
            } else {
                runOnce = true;
                currentFacing = VectorUtils.getAngle(system.getLocation(), WAYPOINTS.get(currentWaypoint));
            }

            starStation.getStarSystem().getCenter().setLocation(system.getLocation().x, system.getLocation().y);
        }

        //Utility function for also spawning small "streaking stars" as a sign that the thing is moving
        private void spawnStreakingStars(float amount) {
            //The stars spawn across a massive area, but only if they are close to the camera viewport
            float starsToSpawnThisFrame = STAR_AMOUNT_PER_SECOND * amount;

            ViewportAPI view = Global.getSector().getViewport();
            Vector2f centerPoint = MathUtils.getPoint(view.getCenter(), STAR_PARTICLE_SPEED * 0.5f, currentFacing);
            for (int i = 0; (i - Math.random()) < starsToSpawnThisFrame; i++) {
                float parralax = MathUtils.getRandomNumberInRange(0.5f, 2f);
                Vector2f point = MathUtils.getRandomPointInCircle(centerPoint, view.getVisibleWidth() * 2f);
                Vector2f particleSpeed = MathUtils.getPoint(new Vector2f(0f, 0f), STAR_PARTICLE_SPEED * parralax, currentFacing);
                particleSpeed.x *= -1f;
                particleSpeed.y *= -1f;
                if (view.isNearViewport(point, 500f)) {
                    starStation.getContainingLocation().addHitParticle(point, particleSpeed, STAR_PARTICLE_SIZE * parralax,
                            parralax, 1.5f, Color.white);
                }
            }
        }

        //Utility function for also spawning an engine plume on our station
        private void spawnEnginePlume(float amount, SectorEntityToken station) {
            //Determine spawn count
            float particlesThisFrame = ENGINE_PARTICLES_PER_SECOND * amount;

            ViewportAPI view = Global.getSector().getViewport();
            Vector2f centerPoint = MathUtils.getPoint(station.getLocation(), ENGINE_SPAWN_DISTANCE, currentFacing + 180f);
            for (int i = 0; (i - Math.random()) < particlesThisFrame; i++) {
                float speed = MathUtils.getRandomNumberInRange(ENGINE_PARTICLE_SPEED_MIN, ENGINE_PARTICLE_SPEED_MAX);
                float sizeVariation = MathUtils.getRandomNumberInRange(0f, 1f);
                float angleDir = MathUtils.getRandomNumberInRange(-0.5f, 0.5f); //Helps make the "cone" into a more traditional engine plume
                float angleFactorMod = 1f - Math.abs(angleDir);
                Vector2f point = MathUtils.getPoint(centerPoint, ENGINE_BASE_WIDTH * (angleDir * -1f), currentFacing + 90f);
                Vector2f particleSpeed = MathUtils.getPoint(new Vector2f(0f, 0f), speed * angleFactorMod,
                        currentFacing + angleDir * ENGINE_ANGLE);
                particleSpeed.x *= -1f;
                particleSpeed.y *= -1f;
                if (view.isNearViewport(point, 500f)) {
                    starStation.getContainingLocation().addHitParticle(point, particleSpeed,
                            ENGINE_PARTICLE_SIZE_MIN * (1f - sizeVariation) + ENGINE_PARTICLE_SIZE_MAX * sizeVariation,
                            sizeVariation, angleFactorMod * MathUtils.getRandomNumberInRange(ENGINE_PARTICLE_DURATION_MIN, ENGINE_PARTICLE_DURATION_MAX),
                            ENGINE_PARTICLE_COLOR);
                }
            }
        }

        @Override
        public boolean runWhilePaused() {
            return true;
        }

        @Override
        public boolean isDone() {
            return false;
        }
    }
}