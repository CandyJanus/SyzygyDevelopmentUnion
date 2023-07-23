package data.scripts;

import com.fs.starfarer.api.*;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.campaign.intel.deciv.DecivTracker;
import data.scripts.characters.SDU_CharacterFactory;
import data.scripts.utils.SDU_util_sysgen;
import data.scripts.world.systems.SDU.SDU_fundament_mainscript;
import data.scripts.world.systems.SDU.SDU_mobile_storefront;
//import data.weapons.weaponAI.seven_sminos_autofireAI;
import exerelin.campaign.SectorManager;
import org.magiclib.util.MagicSettings;
import data.scripts.utils.interactionUI.SDU_OfficerExt_List;

import java.util.List;
import java.util.Map;

//nuke: Sometimes you can't safely import kotlin libraries into .java files. Importing extension methods won't work, as java don't have them. But importing regular classes should work.

public class SDU_ModPlugin extends BaseModPlugin {

    private static org.apache.log4j.Logger log = Global.getLogger(SDU_ModPlugin.class);

    // note: These variables MUST be initialized as false or else the faction enabling config stops working. While generation won't occur, the disabled factions will be added to intel, and without the correct relationships.

    // note: Part of the issue was caused by the use of OnEnabled. Breakpoints show that onNewGameAfterEconomyLoad() runs *before* onEnabled and have the correct config-derived variables there, but then OnEnabled runs and has the default variables there. Why? No fucking clue.
    // note: Even without OnEnabled, if the variables are initialized as true, then some other process adds the factions to intel anyway.
    // note: Testing with Nex enabled/not enabled shows that Nex has nothing to do with these bugs.

    // note: onNewGame is also dogfucking me - the check for whether Meiyo is null ain't working

    // note: I tried a complete reversion to RESTART. Got surveylevel bugs. I'll just try to patch that out and then call it there.

    // note: saatana perkele vittu, it's the config reading that's causing the crashes. going to switch from one OnApplicationLoad read to just reading it anew in each function in case that's causing memleaks somehow.

    @Override
    public void onNewGame() {
        Map<String, Object> data = Global.getSector().getPersistentData();
        boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        boolean haveSOTF = Global.getSettings().getModManager().isModEnabled("secretsofthefrontieralt");

        //nuke: I'm doing this in modplugin because I think it'll be faster than doing the isModEnabled call elsewhere. I'm not sure if it really is.
        if(haveSOTF){
            data.put("have_SOTF", "-");
        }

        if (!haveNexerelin || exerelin.campaign.SectorManager.getManager().isCorvusMode()) {
            MarketAPI proelefsi = new SDU_fundament_mainscript().generate(Global.getSector());
            new SDU_mobile_storefront().generate(Global.getSector());

            SDU_CharacterFactory.createDecimus(proelefsi);
            SDU_CharacterFactory.createAshley(proelefsi);
            //eptaCharacterFactory.createAnodyne(proelefsi);
            //eptaCharacterFactory.createTriela(proelefsi);

            SDU_util_sysgen.exploreAll(proelefsi.getStarSystem());

            data.put("SDU_generated", "1.0.0");
        }
    }

    //nuke: no idea why onEnabled is only spawning the faction in intel but not the systems

    //nuke: I'm pretty sure checking by getEntityByID doesn't work, I've left it in for legacy reasons.


    @Override
    public void onGameLoad(boolean wasEnabledBefore) {

        if(SDU_OfficerExt_List.getOEList()==null){
            log.info("listcreated");
            SDU_OfficerExt_List EOList = new SDU_OfficerExt_List().create();
        }

        //if(!Global.getSector().getIntelManager().hasIntelOfClass(seven_CharacterInteractIntel.class)) {
        //    Global.getSector().getIntelManager().addIntel(new seven_CharacterInteractIntel(), true);
        //}

        boolean loadIntoExistingSave=MagicSettings.getBoolean("SyzygyDevelopmentUnion","loadIntoExistingSave");

        if(loadIntoExistingSave) {

            ModManagerAPI modManager=Global.getSettings().getModManager();
            SectorAPI sector= Global.getSector();
            Map<String, Object> data = sector.getPersistentData();

            boolean haveNexerelin = modManager.isModEnabled("nexerelin");
            boolean haveArmaa = modManager.isModEnabled("armaa");
            boolean haveSOTF = modManager.isModEnabled("secretsofthefrontieralt");



            if (haveSOTF) {
                data.put("have_SOTF", "-");
            }
            if (!haveSOTF && (null != data.get("have_SOTF"))) {
                data.remove("have_SOTF");
            }

            //nuke: put in a ~~second~~ third layer of protection to prevent future double-spawning
            //nuke: I'm pretty sure those duplicate layers of protection don't even work and only the memkey matters.
            if (!haveNexerelin || SectorManager.getManager().isCorvusMode()) {
                if (!data.containsKey("SDU_generated")){
                    MarketAPI proelefsi = new SDU_fundament_mainscript().generate(Global.getSector());
                    SDU_util_sysgen.exploreAll(proelefsi.getStarSystem());
                    new SDU_mobile_storefront().generate(Global.getSector());

                    SDU_CharacterFactory.createDecimus(proelefsi);
                    SDU_CharacterFactory.createAshley(proelefsi);
//                    eptaCharacterFactory.createAnodyne(proelefsi);
//                    eptaCharacterFactory.createTriela(proelefsi);
//                    eptaCharacterFactory.createRoseKanta(proelefsi);
//                    eptaCharacterFactory.createAdProSecSecretary(proelefsi);
                    data.put("SDU_generated", "version 1.0.0");
                }

            }


            if (haveArmaa) {

                // adding the Galevis
                sector.getFaction("sevencorp").getKnownShips().add("seven_galevis");
                sector.getFaction("sevencorp").addUseWhenImportingShip("seven_galevis");
                // adding the Skopefetis
                sector.getFaction("sevencorp").getKnownShips().add("seven_skopefetis");
                sector.getFaction("sevencorp").addUseWhenImportingShip("seven_skopefetis");
                // adding both Zakus
                sector.getFaction("sevencorp").getKnownShips().add("seven_zaku");
                sector.getFaction("sevencorp").addUseWhenImportingShip("seven_zaku");
                sector.getFaction("sevencorp").getKnownShips().add("seven_zaku2");
                sector.getFaction("sevencorp").addUseWhenImportingShip("seven_zaku2");

                //if (!seven_frigateautoforge.ships.contains("seven_galevis_integrated"))
                //    seven_frigateautoforge.ships.add("seven_galevis_integrated");
                //if (!seven_frigateautoforge.ships.contains("seven_skopefetis_integrated"))
                //    seven_frigateautoforge.ships.add("seven_skopefetis_integrated");
            }

            Global.getSector().getFaction("sevencorp").clearShipRoleCache();

            if(haveNexerelin){

            }
        }
    }

    @Override
    public void onNewGameAfterEconomyLoad() {


    }


    @Override
    public void onApplicationLoad() {
        boolean haveArmaa = Global.getSettings().getModManager().isModEnabled("armaa");

        Global.getSettings().resetCached();

        //if (haveArmaa) {
        //            // adding the Galevis
        //            Global.getSettings().getHullSpec("seven_galevis").addTag("sevencorp");
        //            Global.getSettings().getHullSpec("seven_galevis").addTag("epta_phaseship_bp");
        //            // adding the Skopefetis
        //            Global.getSettings().getHullSpec("seven_skopefetis").addTag("sevencorp");
        //            Global.getSettings().getHullSpec("seven_skopefetis").addTag("epta_phaseship_bp");
        //        }
    }

    @Override
    public PluginPick<AutofireAIPlugin> pickWeaponAutofireAI(WeaponAPI weapon) {
        switch (weapon.getId()) {
            case "seven_sminos_leftlight":
            case "seven_sminos_rightlight":
                //return new PluginPick<AutofireAIPlugin>(new seven_sminos_autofireAI(weapon), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            default:
        }
        return null;
    }

}