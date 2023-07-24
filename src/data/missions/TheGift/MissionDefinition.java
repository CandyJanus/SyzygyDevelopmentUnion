package data.missions.TheGift;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.List;

public class MissionDefinition implements MissionDefinitionPlugin {

    public void defineMission(MissionDefinitionAPI api) {

        api.initFleet(FleetSide.PLAYER, "ECS", FleetGoal.ATTACK, false);
        api.initFleet(FleetSide.ENEMY, "DVFS", FleetGoal.ATTACK, true);

//        api.getDefaultCommander(FleetSide.PLAYER).getStats().setSkillLevel(Skills.COORDINATED_MANEUVERS, 3);
//		api.getDefaultCommander(FleetSide.PLAYER).getStats().setSkillLevel(Skills.ELECTRONIC_WARFARE, 3);

        // Set a small blurb for each fleet that shows up on the mission detail and
        // mission results screens to identify each side.
        api.setFleetTagline(FleetSide.PLAYER, "An Accursed Warrior.");
        api.setFleetTagline(FleetSide.ENEMY, "Father Albright's Divine Spear.");

        // These show up as items in the bulleted list under
        // "Tactical Objectives" on the mission detail screen
        api.addBriefingItem("Ananke's Gift must survive.");
        api.addBriefingItem("Father Albright has brought a massive force to bear and his ship is heavily modified.");
        api.addBriefingItem("Luddic fanatics will attempt to take you down with them.");
        api.addBriefingItem("A Tri Tachyon hostile requisitions squad has been spotted in the rabble. Intentions unknown.");

        // Set up the player's fleet.  Variant names come from the
        // files in data/variants and data/variants/fighters
        FleetMemberAPI member = api.addToFleet(FleetSide.PLAYER, "SDU_ananke_gift", FleetMemberType.SHIP, "Ananke's Gift", true);

        FactionAPI SDU = Global.getSector().getFaction("SDU");
        FactionAPI ludd = Global.getSector().getFaction("luddic_path");
        FactionAPI TT = Global.getSector().getFaction("tritachyon");
        PersonAPI officer = SDU.createRandomPerson(FullName.Gender.MALE);
        officer.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
        officer.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE,2);
        officer.getStats().setSkillLevel("gunnery_implants",2);
        officer.getStats().setLevel(1);
        officer.setFaction(SDU.getId());
        officer.setPersonality(Personalities.STEADY);
        officer.getName().setFirst("Accursed");
        officer.getName().setLast("Warrior");
        officer.setPortraitSprite("graphics/portraits/portrait43.png");
        officer.getName().setGender(FullName.Gender.MALE);
        member.setCaptain(officer);

        // Set up the enemy fleet.
        member = api.addToFleet(FleetSide.ENEMY, "atlas2_Standard", FleetMemberType.SHIP, "The Biggun", true);
        member.getVariant().clearSlot("WS 014");
        member.getVariant().clearSlot("WS 015");
        member.getVariant().clearSlot("WS 017");
        member.getVariant().clearSlot("WS 018");
        member.getVariant().addWeapon("WS 014","hephag");
        member.getVariant().addWeapon("WS 015","hephag");
        member.getVariant().addWeapon("WS 017","hephag");
        member.getVariant().addWeapon("WS 018","hephag");
        member.getVariant().addPermaMod("heavyarmor",true);
        member.getVariant().addPermaMod("hardened_subsystems",true);
        member.getVariant().addPermaMod("reinforcedhull",true);
        officer = ludd.createRandomPerson(FullName.Gender.MALE);
        officer.getName().setFirst("Harakone");
        officer.getName().setLast("Albright");
        officer.getStats().setLevel(9);
        officer.setPersonality("aggressive");
        officer.getStats().setSkillLevel("helmsmanship",2);
        officer.getStats().setSkillLevel("combat_endurance",2);
        officer.getStats().setSkillLevel("impact_mitigation",2);
        officer.getStats().setSkillLevel("damage_control",2);
        officer.getStats().setSkillLevel("target_analysis",2);
        officer.getStats().setSkillLevel("ballistic_mastery",2);
        officer.getStats().setSkillLevel("support_doctrine",1);
        member.setCaptain(officer);

        api.addToFleet(FleetSide.ENEMY, "prometheus2_Standard", FleetMemberType.SHIP, "Saigor", false).getCaptain().setPersonality("steady");
        api.addToFleet(FleetSide.ENEMY, "prometheus2_Standard", FleetMemberType.SHIP, "2.1k Virgins", false).getCaptain().setPersonality("steady");
        //api.addToFleet(FleetSide.ENEMY, "dominator_Outdated", FleetMemberType.SHIP, "Shieldcrash", false).getCaptain().setPersonality("reckless");
        //api.addToFleet(FleetSide.ENEMY, "dominator_Outdated", FleetMemberType.SHIP, "Allowance", false).getCaptain().setPersonality("reckless");
        api.addToFleet(FleetSide.ENEMY, "manticore_pirates_Assault", FleetMemberType.SHIP, "Wham", false).getCaptain().setPersonality("reckless");
        api.addToFleet(FleetSide.ENEMY, "manticore_pirates_Assault", FleetMemberType.SHIP, "Bam", false).getCaptain().setPersonality("reckless");
        api.addToFleet(FleetSide.ENEMY, "manticore_pirates_Assault", FleetMemberType.SHIP, "Laughing", false).getCaptain().setPersonality("reckless");
        api.addToFleet(FleetSide.ENEMY, "manticore_pirates_Assault", FleetMemberType.SHIP, "Crying", false).getCaptain().setPersonality("reckless");
        api.addToFleet(FleetSide.ENEMY, "brawler_pather_Raider", FleetMemberType.SHIP, "Ludd's Light", false).getCaptain().setPersonality("reckless");
        api.addToFleet(FleetSide.ENEMY, "brawler_pather_Raider", FleetMemberType.SHIP, "Servant", false).getCaptain().setPersonality("reckless");
        api.addToFleet(FleetSide.ENEMY, "brawler_pather_Raider", FleetMemberType.SHIP, "Servitude", false).getCaptain().setPersonality("reckless");
        api.addToFleet(FleetSide.ENEMY, "brawler_pather_Raider", FleetMemberType.SHIP, "Ludd's Hope", false).getCaptain().setPersonality("reckless");
        //api.addToFleet(FleetSide.ENEMY, "brawler_pather_Raider", FleetMemberType.SHIP, "Dogmatic", false).getCaptain().setPersonality("reckless");
        //api.addToFleet(FleetSide.ENEMY, "brawler_pather_Raider", FleetMemberType.SHIP, "Pragmatic", false).getCaptain().setPersonality("reckless");
        api.addToFleet(FleetSide.ENEMY, "colossus2_Pather", FleetMemberType.SHIP, "Last Lash", false).getCaptain().setPersonality("reckless");
        api.addToFleet(FleetSide.ENEMY, "colossus3_Pirate", FleetMemberType.SHIP, "Penance", false).getCaptain().setPersonality("steady");
        api.addToFleet(FleetSide.ENEMY, "colossus3_Pirate", FleetMemberType.SHIP, "God's Light", false).getCaptain().setPersonality("steady");
        api.addToFleet(FleetSide.ENEMY, "colossus3_Pirate", FleetMemberType.SHIP, "Flamer", false).getCaptain().setPersonality("steady");
        api.addToFleet(FleetSide.ENEMY, "lasher_luddic_path_Raider", FleetMemberType.SHIP, "Judgement", false).getCaptain().setPersonality("reckless");
        api.addToFleet(FleetSide.ENEMY, "lasher_luddic_path_Raider", FleetMemberType.SHIP, "Excommunicate", false).getCaptain().setPersonality("reckless");
        api.addToFleet(FleetSide.ENEMY, "hound_luddic_path_Attack", FleetMemberType.SHIP, "Divine Light", false).getCaptain().setPersonality("reckless");
        api.addToFleet(FleetSide.ENEMY, "hound_luddic_path_Attack", FleetMemberType.SHIP, "Flames of Rebirth", false).getCaptain().setPersonality("reckless");
        api.addToFleet(FleetSide.ENEMY, "kite_luddic_path_Raider", FleetMemberType.SHIP, "Lighthammer", false).getCaptain().setPersonality("reckless");
        api.addToFleet(FleetSide.ENEMY, "kite_luddic_path_Strike", FleetMemberType.SHIP, "Devotions", false).getCaptain().setPersonality("reckless");
        api.addToFleet(FleetSide.ENEMY, "kite_luddic_path_Raider", FleetMemberType.SHIP, "Crusader", false).getCaptain().setPersonality("reckless");
        api.addToFleet(FleetSide.ENEMY, "kite_luddic_path_Strike", FleetMemberType.SHIP, "God is Good", false).getCaptain().setPersonality("reckless");

        member = api.addToFleet(FleetSide.ENEMY, "harbinger_Strike", FleetMemberType.SHIP, "Boggis", false);
        officer = TT.createRandomPerson(FullName.Gender.MALE);
        officer.getStats().setLevel(5);
        officer.setPersonality("aggressive");
        member.getVariant().addPermaMod("fluxcoil",true);
        officer.getStats().setSkillLevel("gunnery_implants",2);
        officer.getStats().setSkillLevel("systems_expertise",2);
        officer.getStats().setSkillLevel("phase_corps",2);
        member.setCaptain(officer);

        member = api.addToFleet(FleetSide.ENEMY, "harbinger_Strike", FleetMemberType.SHIP, "Bunce", false);
        officer = TT.createRandomPerson(FullName.Gender.MALE);
        officer.getStats().setLevel(5);
        officer.setPersonality("aggressive");
        member.getVariant().addPermaMod("fluxcoil",true);
        officer.getStats().setSkillLevel("gunnery_implants",2);
        officer.getStats().setSkillLevel("systems_expertise",2);
        officer.getStats().setSkillLevel("phase_corps",2);
        member.setCaptain(officer);

        member = api.addToFleet(FleetSide.ENEMY, "doom_Attack", FleetMemberType.SHIP, "Bean", false);
        member.getVariant().addPermaMod("hardened_subsystems",true);
        member.getVariant().addPermaMod("phase_anchor",true);
        member.getVariant().addPermaMod("fluxcoil",true);
        member.getVariant().clearSlot("WS 001");
        member.getVariant().clearSlot("WS 002");
        member.getVariant().addWeapon("WS 001","tachyonlance");
        member.getVariant().addWeapon("WS 002","tachyonlance");

        officer = TT.createRandomPerson(FullName.Gender.MALE);
        officer.getName().setFirst("Durnam");
        officer.getName().setLast("Sechak");
        officer.getStats().setLevel(9);
        officer.setPersonality("aggressive");
        officer.getStats().setSkillLevel("helmsmanship",2);
        officer.getStats().setSkillLevel("impact_mitigation",2);
        officer.getStats().setSkillLevel("damage_control",2);
        officer.getStats().setSkillLevel("energy_weapon_mastery",2);
        officer.getStats().setSkillLevel("gunnery_implants",2);
        officer.getStats().setSkillLevel("phase_corps",2);
        member.setCaptain(officer);

        api.defeatOnShipLoss("Ananke's Gift");

        float width = 24000f;
        float height = 24000f;
        api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);

        float minX = -width/2;
        float minY = -height/2;

        for (int i = 0; i < 5; i++) { //15
            float x = (float) Math.random() * width - width/2;
            float y = (float) Math.random() * height - height/2;
            float radius = 100f + (float) Math.random() * 900f;
            api.addNebula(x, y, radius);
        }

        api.addNebula(minX + width * 0.8f - 1000, minY + height * 0.4f, 2000);
        api.addNebula(minX + width * 0.8f - 1000, minY + height * 0.5f, 2000);
        api.addNebula(minX + width * 0.8f - 1000, minY + height * 0.6f, 2000);
        api.addPlugin(new Plugin(width,height));
    }

    private final class Plugin extends BaseEveryFrameCombatPlugin {

        private boolean done = false;
        private final float mapX;
        private final float mapY;
        private float timer = 5f;

        private Plugin(float mapX, float mapY) {
            this.mapX = mapX;
            this.mapY = mapY;
        }

        @Override
        public void advance(float amount, List<InputEventAPI> events) {
            if (done || Global.getCombatEngine() == null || Global.getCombatEngine().isPaused()) {
                return;
            }

            timer -= amount;
            if (timer <= 0f) {
                for (FleetMemberAPI member : Global.getCombatEngine().getFleetManager(FleetSide.ENEMY).getReservesCopy()) {
                    if (!Global.getCombatEngine().getFleetManager(FleetSide.ENEMY).getDeployedCopy().contains(member)) {
                        Global.getCombatEngine().getFleetManager(FleetSide.ENEMY).spawnFleetMember(member, getSafeSpawn(FleetSide.ENEMY, mapX, mapY), 270f, 1f);
                    }
                }
                done = true;
            }

            for (FleetMemberAPI member : Global.getCombatEngine().getFleetManager(FleetSide.ENEMY).getDeployedCopy()){
                if(!member.getVariant().hasHullMod("SDU_finalrites")&&member.getCaptain()!=null&&member.getCaptain().getPersonalityAPI().getId().equals("reckless")){
                    member.getVariant().addMod("SDU_finalrites");
                }
                if(member.getCaptain().getName().getLast().equals("Albright")){
                    member.getStats().getBallisticRoFMult().modifyMult("lole",3.5f);
                    member.getStats().getBallisticWeaponRangeBonus().modifyMult("lole",2.5f);
                    member.getStats().getBallisticWeaponFluxCostMod().modifyMult("lole",0.1f);
                    member.getStats().getProjectileSpeedMult().modifyMult("lole",3.5f);
                    member.getStats().getArmorDamageTakenMult().modifyMult("lole",0.55f);
                    member.getStats().getMaxTurnRate().modifyMult("lole",2.5f);
                    member.getStats().getTurnAcceleration().modifyMult("lole",2.5f);
                    member.getStats().getTimeMult().modifyMult("lole",2.5f);
                }
            }
        }

        @Override
        public void init(CombatEngineAPI engine) {
        }

        private Vector2f getSafeSpawn(FleetSide side, float mapX, float mapY) {
            Vector2f spawnLocation = new Vector2f();

            spawnLocation.x = MathUtils.getRandomNumberInRange(-mapX / 2, mapX / 2);
            if (side == FleetSide.PLAYER) {
                spawnLocation.y = (-mapY / 2f);

            } else {
                spawnLocation.y = mapY / 2;
            }

            return spawnLocation;
        }
    }
}