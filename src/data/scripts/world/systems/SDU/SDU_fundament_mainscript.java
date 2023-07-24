package data.scripts.world.systems.SDU;

import java.awt.Color;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.util.Misc;
import data.scripts.utils.SDU_util_sysgen;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * this script is mostly based off of Anargaia's script cuz nicke said ok and im
 * p sure this is the only way to do it
 */
public class SDU_fundament_mainscript {
    /* -- Governor/Admin settings -- */
    // The path to the portrait of the AGI admin. I think it also needs to be added to the settings.json file to show up like some other custom sprites, but I'm not sure

    public static final String ADMIN_PORTRAIT_PATH = "graphics/portraits/decimusmaximus.png";
    // The first and last name of the AGI core (last should probably be left empty, unless you want it to have one)
    public static final String ADMIN_FIRST_NAME = "Decimus";
    public static final String ADMIN_LAST_NAME = "Maximus";

    /* -- BASIC GENERATION SETTINGS -- */
    // - Waypoints on world map to move across; loops around (1->2->3->1->2 ...)
    private static final List<Vector2f> WAYPOINTS = new ArrayList<>();

    static {
        WAYPOINTS.add(new Vector2f(9000f, -15000f));
        WAYPOINTS.add(new Vector2f(13000f, 0f));
        WAYPOINTS.add(new Vector2f(9000f, 15000f));
        WAYPOINTS.add(new Vector2f(-9000f, 15000f));
        WAYPOINTS.add(new Vector2f(-13000f, 0f));
        WAYPOINTS.add(new Vector2f(-9000f, -15000f));
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
    //Distance from the center of Fundament that the engine plume starts spawning
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

    //Main generation function
    public MarketAPI generate(SectorAPI sector) {
        StarSystemAPI system = sector.createStarSystem("Fundament");

        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("SDU");

        FactionAPI SDU = sector.getFaction("SDU");
        FactionAPI player = sector.getFaction(Factions.PLAYER);
        FactionAPI hegemony = sector.getFaction(Factions.HEGEMONY);
        FactionAPI tritachyon = sector.getFaction(Factions.TRITACHYON);
        FactionAPI pirates = sector.getFaction(Factions.PIRATES);
        FactionAPI independent = sector.getFaction(Factions.INDEPENDENT);
        FactionAPI church = sector.getFaction(Factions.LUDDIC_CHURCH);
        FactionAPI path = sector.getFaction(Factions.LUDDIC_PATH);
        FactionAPI diktat = sector.getFaction(Factions.DIKTAT);
        FactionAPI kol = sector.getFaction(Factions.KOL);
        FactionAPI persean = sector.getFaction(Factions.PERSEAN);
        FactionAPI guard = sector.getFaction(Factions.LIONS_GUARD);
        FactionAPI remnant = sector.getFaction(Factions.REMNANTS);
        FactionAPI derelict = sector.getFaction(Factions.DERELICT);

        //vanilla factions
        SDU.setRelationship(hegemony.getId(), -0.2f);
        SDU.setRelationship(player.getId(), 0);
        SDU.setRelationship(pirates.getId(), -0.2f);

        SDU.setRelationship(independent.getId(), 0.4f);

        SDU.setRelationship(tritachyon.getId(), 0.1f);

        SDU.setRelationship(kol.getId(), -0.8f);
        SDU.setRelationship(path.getId(), -0.8f);
        SDU.setRelationship(church.getId(), -0.6f);

        SDU.setRelationship(persean.getId(), 0.25f);
        SDU.setRelationship(guard.getId(), 0.25f);
        SDU.setRelationship(diktat.getId(), 0.25f);


        //environment
        SDU.setRelationship(remnant.getId(), RepLevel.COOPERATIVE);
        SDU.setRelationship(derelict.getId(), RepLevel.COOPERATIVE);

        // mod factions
        SDU.setRelationship("vic", 0f);
        SDU.setRelationship("ironsentinel", -0.2f);
        SDU.setRelationship("ironshell", -0.2f);

        SDU.setRelationship("sylphon", 0.49f);
        SDU.setRelationship("uaf", 0.49f);
        SDU.setRelationship("osiris", 0.5f);
        SDU.setRelationship("mayorate", 0.3f);
        SDU.setRelationship("apex_design", -0.3f);
        SDU.setRelationship("dassault_mikoyan", 0.2f);
        SDU.setRelationship("MVS", 0.3f);
        SDU.setRelationship("vri", 0.8f);

        SDU.setRelationship("Coalition", -0.3f);
        SDU.setRelationship("tiandong", 0f);
        SDU.setRelationship("kadur_remnant", -0.3f);
        SDU.setRelationship("blackrock_driveyards", 0.25f);
        SDU.setRelationship("interstellarimperium", 0.25f);
        SDU.setRelationship("HMI", 0.0f);
        SDU.setRelationship("al_ars", -0.25f);
        SDU.setRelationship("SCY", 0.25f);
        SDU.setRelationship("blade_breakers", -0.5f);
        SDU.setRelationship("diableavionics", -0.5f);
        SDU.setRelationship("ORA", 0.25f);
        SDU.setRelationship("gmda", -0.25f);
        SDU.setRelationship("gmda_patrol", -0.25f);

        SDU.setRelationship("tahlan_legioinfernalis", -0.15f);
        SDU.setRelationship("yrxp", 0f);

        SDU.setRelationship("cabal", -0.5f);

        // the below are just copied from xhan, but might be fine?
        SDU.setRelationship("shadow_industry", -0.6f);
        SDU.setRelationship("roider", -0.6f);
        SDU.setRelationship("exipirated", -0.6f);
        SDU.setRelationship("draco", -0.6f);
        SDU.setRelationship("fang", -0.6f);
        SDU.setRelationship("junk_pirates", -0.6f);
        SDU.setRelationship("junk_pirates_hounds", -0.6f);
        SDU.setRelationship("junk_pirates_junkboys", -0.6f);
        SDU.setRelationship("junk_pirates_technicians", -0.6f);
        SDU.setRelationship("the_cartel", -0.6f);
        SDU.setRelationship("nullorder", -0.6f);
        SDU.setRelationship("templars", -0.6f);
        SDU.setRelationship("crystanite_pir", -0.6f);
        SDU.setRelationship("infected", -0.6f);
        SDU.setRelationship("new_galactic_order", -0.6f);
        SDU.setRelationship("TF7070_D3C4", -0.6f);
        SDU.setRelationship("minor_pirate_1", -0.6f);
        SDU.setRelationship("minor_pirate_2", -0.6f);
        SDU.setRelationship("minor_pirate_3", -0.6f);
        SDU.setRelationship("minor_pirate_4", -0.6f);
        SDU.setRelationship("minor_pirate_5", -0.6f);
        SDU.setRelationship("minor_pirate_6", -0.6f);


        system.getLocation().set(WAYPOINTS.get(0));
        LocationAPI hyper = Global.getSector().getHyperspace();

        PlanetAPI fundamentstar = system.initStar("SDU_fundament_star", // unique id for this star
                "star_white", // id in planets.json
                0f,        // radius (in pixels at default zoom)
                0f, // corona radius, from star edge
                0f, // solar wind burn level
                0f, // flare probability
                0f); // cr loss mult

        fundamentstar.setName("Fundament");
        fundamentstar.setLocation(0f,0f);

        system.setBackgroundTextureFilename("graphics/backgrounds/background3.jpg");

        // Create a centerpoint in the system, for everything to rotate around.
        SectorEntityToken centerpoint = fundamentstar;

        //Change our type to NEBULA to indicate we have no centerpoint. Might have some consequences, but this is the only way I found that is neat to do
        //system.setType(StarSystemGenerator.StarSystemType.NEBULA);
        system.setType(StarSystemGenerator.StarSystemType.SINGLE);

        // Sets light color in entire system, affects all entities
        system.setLightColor(new Color(255, 255, 255));

        // Adds the giant space station Fundament
        SectorEntityToken fundament = system.addCustomEntity("SDU_fundament_station", "Fundament", "fundament_cap", "SDU");
        fundament.setCircularOrbitPointingDown(centerpoint, 0f, 50f, 99999999999f);

        // Add the marketplace to Fundament ---------------
        MarketAPI fundament_market = SDU_util_sysgen.addMarketplace(
                "SDU",
                fundament,
                null,
                "Fundament", // name of the market
                4, // size of the market
                new ArrayList<>(
                        Arrays.asList( // list of market conditions
                                Conditions.POPULATION_4,
                                Conditions.INDUSTRIAL_POLITY,
                                Conditions.FREE_PORT)),
                new ArrayList<>(
                        Arrays.asList( // which submarkets to generate
                                Submarkets.GENERIC_MILITARY,
                                Submarkets.SUBMARKET_BLACK,
                                Submarkets.SUBMARKET_OPEN,
                                Submarkets.SUBMARKET_STORAGE)),
                new ArrayList<>(
                        Arrays.asList(
                                Industries.POPULATION,
                                Industries.MEGAPORT,
                                Industries.ORBITALWORKS,
                                Industries.WAYSTATION,
                                Industries.HEAVYBATTERIES,
                                "SDU_fundament_station",
                                Industries.HIGHCOMMAND,
                                "SDU_manufactory")),
                true,
                false);

        fundament_market.getIndustry(Industries.POPULATION).setAICoreId(Commodities.ALPHA_CORE);
        fundament_market.getIndustry(Industries.MEGAPORT).setAICoreId(Commodities.ALPHA_CORE);
        fundament_market.getIndustry(Industries.ORBITALWORKS).setAICoreId(Commodities.ALPHA_CORE);
        fundament_market.getIndustry(Industries.ORBITALWORKS).setSpecialItem(new SpecialItemData(Items.PRISTINE_NANOFORGE, null));
        fundament_market.getIndustry(Industries.WAYSTATION).setAICoreId(Commodities.ALPHA_CORE);
        fundament_market.getIndustry(Industries.HEAVYBATTERIES).setAICoreId(Commodities.ALPHA_CORE);
        fundament_market.getIndustry(Industries.HIGHCOMMAND).setAICoreId(Commodities.ALPHA_CORE);

        if (!fundament_market.hasTag("fundament_escorts_not_there")) {
            fundament_market.addTag("fundament_escorts_not_there");
        }

        //fundament_market.getTariff().setBaseValue(); //nuke: in case you wanted Proel to have a different tariff value from the base, for some reason. not sure why you'd want it at 30%, which is significantly higher than the normal freeport rate.
        fundament_market.setHidden(false);

        fundament_market.getMemoryWithoutUpdate().set("$nex_unbuyable", true);

        //Adds our death manager, so we can keep track of when we are going to DIE
        Global.getSector().addScript(new SDU_fundament_deathscript(fundament, fundament_market));

        // Jump point : has a miniature script setting its rotation to be oppositely-locked to fundament ---------------
        JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint("SDU_fundament_jump_point", "Lamperocheri Rift");
        jumpPoint.setCircularOrbit(centerpoint, 180f, 900f, 99999999999f);
        system.addEntity(jumpPoint);
        new OrbitOppositePlugin(jumpPoint, fundament);

        // generates hyperspace destinations for in-system jump points
        system.autogenerateHyperspaceJumpPoints(false, false);
        system.generateAnchorIfNeeded();

        sector.addScript(new MovingStarsystemScript(fundament, system));

        Global.getSector().getIntelManager().addIntel(new SDU_spawnstorefrontintel(fundament_market, "SDU",7));

        return fundament_market;
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

        private MovingStarsystemScript(SectorEntityToken starStation, StarSystemAPI system) {
            this.starStation = starStation;
            this.system = system;

            //Spawns us in a random spot on our "track"
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

            Global.getSector().addListener(new RegisterOpenMarketListener(this));
        }

        @Override
        public void advance(float amount) {
            if (Global.getSector().isPaused()) {
                amount = 0f;
            }
            //If we are in a market, don't run normal stuff: instead, randomize our location every frame to confound people looking in the commodity screen
            if (storedLoc != null) {
                system.getLocation().x = storedLoc.x;
                system.getLocation().y = storedLoc.y;
                storedLoc = null;
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

                //system.setMapGridHeightOverride(system.getLocation().y);
                //system.setMapGridWidthOverride(system.getLocation().x);
            }

            //Always adjust our angle to point towards our "correct" facing: if the player is in the system, we only adjust rotation once
            if (Global.getSector().getPlayerFleet().getContainingLocation().equals(starStation.getContainingLocation())) {
                if (runOnce) {
                    runOnce = false;
                    starStation.setCircularOrbitAngle(currentFacing + 90f);
                }

                //We also spawn streaking stars when the player is in the system
                spawnStreakingStars(amount);

                //We *also* spawn a particle plume behind fundament itself
                spawnEnginePlume(amount, starStation);
            } else {
                runOnce = true;
                currentFacing = VectorUtils.getAngle(system.getLocation(), WAYPOINTS.get(currentWaypoint));
            }
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

    private class RegisterOpenMarketListener extends BaseCampaignEventListener {

        MovingStarsystemScript script;

        public RegisterOpenMarketListener(MovingStarsystemScript script) {
            super(true);
            this.script = script;
        }

        @Override
        public void reportPlayerOpenedMarket(MarketAPI market) {
            if (!"SDU_fundament_station_market".equals(market.getId())) {
                script.marketIsOpen = true;
            }
        }

        @Override
        public void reportPlayerClosedMarket(MarketAPI market) {
            if (!"SDU_fundament_station_market".equals(market.getId())) {
                script.marketIsOpen = false;
            }
        }
    }
}