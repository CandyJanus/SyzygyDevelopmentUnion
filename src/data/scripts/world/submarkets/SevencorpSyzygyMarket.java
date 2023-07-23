package data.scripts.world.submarkets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.fleet.ShipRolePick;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.ShipRoles;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.submarkets.BaseSubmarketPlugin;
import com.fs.starfarer.api.impl.campaign.submarkets.OpenMarketPlugin;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.util.Highlights;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import exerelin.utilities.StringHelper;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SevencorpSyzygyMarket extends BaseSubmarketPlugin {
    private static WeightedRandomPicker<String> hullmodPicker = new WeightedRandomPicker();
    public static float ECON_UNIT_MULT_EXTRA;
    public static float ECON_UNIT_MULT_PRODUCTION;
    public static float ECON_UNIT_MULT_IMPORTS;
    public static float ECON_UNIT_MULT_DEFICIT;
    public static Set<String> SPECIAL_COMMODITIES;

    public SevencorpSyzygyMarket() {
    }

    @Override
    public void updateCargoPrePlayerInteraction()
    {
        //log.info("Days since update: " + sinceLastCargoUpdate);
        if (sinceLastCargoUpdate < 30) return;
        sinceLastCargoUpdate = 0f;

        CargoAPI cargo = getCargo();

        //pruneWeapons(1f);

        // 50% chance per stack in the cargo to remove that stack
        for (CargoStackAPI s : cargo.getStacksCopy()) {
            if (itemGenRandom.nextFloat() > 0.5f) {
                float qty = s.getSize();
                cargo.removeItems(s.getType(), s.getData(), qty );
            }
        }
        addWeapons(30,60,8,"sevencorp");
        if(Math.random()>0.7){
            cargo.addWeapons("SDU_relativitycannon",1);
        }
        if(Math.random()>0.3){
            cargo.addWeapons("SDU_kolofon",1);
        }
        cargo.removeEmptyStacks();
        addWeapons();
        cargo.sort();
    }

    public boolean isWeaponAllowed(WeaponSpecAPI spec)
    {
        if (spec.getTier() < 2 || spec.getTier() > 4) return false;
        if (spec.hasTag(Tags.RESTRICTED)) return false;
        String specId = spec.getWeaponId();
        return true;
    }

    // similar to old vanilla one except without inflated weights for some weapons like HMG and Light Needler
    protected void addRandomWeapons(int max) {
        CargoAPI cargo = getCargo();
        List<String> weaponIds = Global.getSector().getAllWeaponIds();

        WeightedRandomPicker<String> picker = new WeightedRandomPicker<>(itemGenRandom);

        for (String id : weaponIds) {
            WeaponSpecAPI spec = Global.getSettings().getWeaponSpec(id);
            if (isWeaponAllowed(spec)) {
                float weight = 10 - spec.getTier();    // so 6-8

                weight *= spec.getRarity();

                // chance to be picked twice
                picker.add(spec.getWeaponId(), weight);
                picker.add(spec.getWeaponId(), weight);
            }
        }
        for (int i = 0; i < max; i++) {
            if (picker.isEmpty()) break;
            String weaponId = picker.pickAndRemove();
            int quantity = 2;
            WeaponSpecAPI spec = Global.getSettings().getWeaponSpec(weaponId);
            if (spec.getSize() == WeaponAPI.WeaponSize.SMALL)
                quantity = 8;
            else if (spec.getSize() == WeaponAPI.WeaponSize.MEDIUM)
                quantity = 4;
            cargo.addWeapons(weaponId, quantity);
        }
    }

    protected void addWeapons()
    {
        int present = getCargo().getWeapons().size();
        float variation = (float)itemGenRandom.nextFloat() * 0.5f + 0.75f;
        addRandomWeapons(100 - present);
    }

    @Override
    public float getTariff() {
        RepLevel level = submarket.getFaction().getRelationshipLevel(Global.getSector().getFaction(Factions.PLAYER));
        float mult = 1f;
        switch (level)
        {
            case NEUTRAL:
                mult = 1f;
                break;
            case SUSPICIOUS:
                mult = 1.1f;
                break;
            case INHOSPITABLE:
                mult = 1.3f;
                break;
            case HOSTILE:
                mult = 1.5f;
                break;
            case VENGEFUL:
                mult = 1.8f;
                break;
            default:
                mult = 0.8f;
        }
        return mult;
    }

    @Override
    public boolean isBlackMarket() {
        return false;
    }
}

