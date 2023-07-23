package data.scripts.world.submarkets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.fleet.ShipRolePick;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.ShipRoles;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.submarkets.BaseSubmarketPlugin;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.lazywizard.lazylib.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SevencorpChutnamSecurityMarket extends BaseSubmarketPlugin {

    private static org.apache.log4j.Logger log = Global.getLogger(SevencorpChutnamSecurityMarket.class);

    public SevencorpChutnamSecurityMarket() {
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

        cargo.getMothballedShips().clear();

        //String[][] whitelist = new String[0][2];
        //        whitelist[0][0]="pirates";
        //        whitelist[0][1]="vanilla";
        //        whitelist[1][0]="hegemony";
        //        whitelist[1][1]="vanilla";
        //        whitelist[2][0]="tritachyon";
        //        whitelist[2][1]="vanilla";
        //        whitelist[0][0]="sindrian_diktat";
        //        whitelist[0][1]="vanilla";
        //        whitelist[0][0]="lions_guard";
        //        whitelist[0][1]="vanilla";
        //        whitelist[0][0]="luddic_church";
        //        whitelist[0][1]="vanilla";
        //        whitelist[0][0]="luddic_path";
        //        whitelist[0][1]="vanilla";
        //        whitelist[0][0]="persean_league";
        //        whitelist[0][1]="vanilla";
        //        whitelist[0][0]="remnants";
        //        whitelist[0][1]="vanilla";
        //        whitelist[0][0]="armaarmatura";
        //        whitelist[0][1]="armaa";
        //        whitelist[0][0]="xhanempire";
        //        whitelist[0][1]="XhanEmpire";
        //        whitelist[0][0]="unitedpamed";
        //        whitelist[0][1]="XhanEmpire";
        //        whitelist[0][0]="hcok";
        //        whitelist[0][1]="hcok";
        //        whitelist[0][0]="magellan_protectorate";
        //        whitelist[0][1]="mag_protect";
        //        whitelist[0][0]="magellan_startigers";
        //        whitelist[0][1]="mag_protect";
        //        whitelist[0][0]="magellan_leveller";
        //        whitelist[0][1]="mag_protect";
        //        whitelist[0][0]="kyeltziv";
        //        whitelist[0][1]="kyeltziv";
        //        whitelist[0][0]="uaf";
        //        whitelist[0][1]="uaf";
        //        whitelist[0][0]="keruvim";
        //        whitelist[0][1]="keruvim_shipyards";
        //        whitelist[0][0]="apex_design";
        //        whitelist[0][1]="apex_design";
        //        whitelist[0][0]="osiris";
        //        whitelist[0][1]="oas";
        //        whitelist[0][0]="ameg";
        //        whitelist[0][1]="oas";
        //        whitelist[0][0]="MVS";
        //        whitelist[0][1]="exshippack";
        //        whitelist[0][0]="warhawk_republic";
        //        whitelist[0][1]="vayrasector";
        //        whitelist[0][0]="ashen_keepers";
        //        whitelist[0][1]="vayrasector";
        //        whitelist[0][0]="almighty_dollar";
        //        whitelist[0][1]="vayrasector";
        //        whitelist[0][0]="communist_clouds";
        //        whitelist[0][1]="vayrasector";

        SectorAPI sector = Global.getSector();

        FactionAPI faction = Global.getSector().getAllFactions().get(MathUtils.getRandomNumberInRange(0,Global.getSector().getAllFactions().size()-1));
        List<FactionAPI> blacklist = new ArrayList<>();
        for(FactionAPI f:sector.getAllFactions()){
            if(f.getKnownShips().isEmpty() || f.getId().equals("scavengers") || f.getId().equals("omega")){
                blacklist.add(f);
            }
        }

        while(!sector.getFaction("sevencorp").getRelationshipLevel(faction).isAtWorst(RepLevel.FAVORABLE) || blacklist.contains(faction)){
            faction = Global.getSector().getAllFactions().get(MathUtils.getRandomNumberInRange(0,Global.getSector().getAllFactions().size()-1));
        }
        log.info("faction picked: "+faction);
        cargo.removeEmptyStacks();
        addShips(faction);
        addWings(faction);
        addWeapons(faction);
        cargo.sort();
    }

    public boolean isShipAllowed(FleetMemberAPI member, float requiredFP, FactionAPI faction)
    {
        if (member.getHullSpec().isDHull()) return false;
        if (member.getHullSpec().hasTag(Tags.RESTRICTED)) return false;
        if (member.getHullSpec().hasTag(Tags.UNRECOVERABLE)) return false;
        if (member.getFleetPointCost() < requiredFP) return false; //quality check
        if (member.getHullSpec().getHints().contains(ShipHullSpecAPI.ShipTypeHints.STATION)) return false;
        if (!faction.getKnownShips().contains(member.getHullSpec().getHullId())) return false;

        return true;
    }

    public boolean isWingAllowed(FighterWingSpecAPI spec, FactionAPI faction)
    {
        if (spec.getTier() < 2) return false;
        if (spec.getTier() >= 5) return false;
        if (spec.hasTag(Tags.WING_NO_SELL)) return false;
        if (spec.hasTag(Tags.RESTRICTED)) return false;
        if (!faction.getKnownFighters().contains(spec.getId())) return false;
        String specId = spec.getId();
        return true;
    }

    public boolean isWeaponAllowed(WeaponSpecAPI spec, FactionAPI faction)
    {
        if (spec.getTier() < 2 || spec.getTier() > 4) return false;
        if (spec.hasTag(Tags.RESTRICTED)) return false;
        if (!faction.getKnownWeapons().contains(spec.getWeaponId())) return false;
        String specId = spec.getWeaponId();
        return true;
    }

    // similar to old vanilla one except without inflated weights for some weapons like HMG and Light Needler
    protected void addRandomWeapons(int max, FactionAPI faction) {
        CargoAPI cargo = getCargo();
        List<String> weaponIds = Global.getSector().getAllWeaponIds();

        WeightedRandomPicker<String> picker = new WeightedRandomPicker<>(itemGenRandom);

        for (String id : weaponIds) {
            WeaponSpecAPI spec = Global.getSettings().getWeaponSpec(id);
            if (isWeaponAllowed(spec, faction)) {
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

    protected void addWeapons(FactionAPI faction)
    {
        int present = getCargo().getWeapons().size();
        float variation = (float)itemGenRandom.nextFloat() * 0.5f + 0.75f;
        addRandomWeapons(50 - present,faction);
    }

    protected void addWings(FactionAPI faction)
    {
        CargoAPI cargo = getCargo();
        WeightedRandomPicker<String> fighterPicker = new WeightedRandomPicker<>(itemGenRandom);
        for (FighterWingSpecAPI spec : Global.getSettings().getAllFighterWingSpecs()) {
            if (isWingAllowed(spec,faction))
                fighterPicker.add(spec.getId());
        }
        int picks = 0;
        for (CargoAPI.CargoItemQuantity<String> quantity : cargo.getFighters())
        {
            picks += quantity.getCount();
        }
        while (!fighterPicker.isEmpty() && picks<15) {
            String id = fighterPicker.pick();
            cargo.addItems(CargoAPI.CargoItemType.FIGHTER_CHIP, id, 1);
            picks++;
        }
    }

    protected void addShips(FactionAPI faction) {

        CargoAPI cargo = getCargo();
        FleetDataAPI data = cargo.getMothballedShips();

        WeightedRandomPicker<String> rolePicker = new WeightedRandomPicker<>(itemGenRandom);
        rolePicker.add(ShipRoles.CIV_RANDOM, 1f);
        rolePicker.add(ShipRoles.FREIGHTER_SMALL, 1f);
        rolePicker.add(ShipRoles.FREIGHTER_MEDIUM, 1f);
        rolePicker.add(ShipRoles.FREIGHTER_LARGE, 5f);
        rolePicker.add(ShipRoles.TANKER_SMALL, 1f);
        rolePicker.add(ShipRoles.TANKER_MEDIUM, 1f);
        rolePicker.add(ShipRoles.TANKER_LARGE, 1f);
        rolePicker.add(ShipRoles.COMBAT_FREIGHTER_SMALL, 1f);
        rolePicker.add(ShipRoles.COMBAT_FREIGHTER_MEDIUM, 1f);
        rolePicker.add(ShipRoles.COMBAT_FREIGHTER_LARGE, 5f);
        rolePicker.add(ShipRoles.COMBAT_SMALL, 25f);
        rolePicker.add(ShipRoles.COMBAT_MEDIUM, 30f);
        rolePicker.add(ShipRoles.COMBAT_LARGE, 25f);
        rolePicker.add(ShipRoles.COMBAT_CAPITAL, 15f);
        rolePicker.add(ShipRoles.CARRIER_SMALL, 5f);
        rolePicker.add(ShipRoles.CARRIER_MEDIUM, 5f);
        rolePicker.add(ShipRoles.CARRIER_LARGE, 5f);

        //renew the stock
        float variation=(float)itemGenRandom.nextFloat() * 0.5f + 0.75f;
        int tries = 0;
        int target = Math.round(10*variation);
        for (int i=0; i<target; i=cargo.getMothballedShips().getNumMembers()){
            //pick the role and faction
            List<ShipRolePick> picks = null;
            do {
                String role = rolePicker.pick();
                //pick the random ship
                try {
                    picks = faction.pickShip(role, FactionAPI.ShipPickParams.priority());
                } catch (NullPointerException npex) {
                    // likely picker picked a role when faction has no ships for that role; do nothing
                }
                //log.info("Add ship role try " + tries2 + ": " + (picks == null));
            } while (picks == null);

            for (ShipRolePick pick : picks) {
                FleetMemberType type = FleetMemberType.SHIP;
                String variantId = pick.variantId;
                //set the ID
                FleetMemberAPI member = Global.getFactory().createFleetMember(type, variantId);
                variantId = member.getHullId() + "_Hull";
                member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, variantId);

                // Fleet point cost threshold
                int FP;
                if (member.isCapital()){
                    FP = 20;
                } else if (member.isCruiser()){
                    FP = 14;
                } else if (member.isDestroyer()){
                    FP = 10;
                } else if (member.isFrigate()){
                    FP = 5;
                } else {
                    FP = 6;
                }
                FP=0;

                //if the variant is not degraded and high end, add it. Else start over
                if (isShipAllowed(member, FP, faction))
                {
                    member.getRepairTracker().setMothballed(true);
                    member.getRepairTracker().setCR(0.5f);
                    getCargo().getMothballedShips().addFleetMember(member);
                } else {
                    i-=1;
                }
            }
            tries++;
            //log.info("Add ship try " + tries);
            if (tries > 40 + target) break;
        }
    }

    @Override
    public float getTariff() {
        RepLevel level = submarket.getFaction().getRelationshipLevel(Global.getSector().getFaction(Factions.PLAYER));
//        RepLevel level = Global.getSector().getImportantPeople().getPerson("epta_triela").getRelToPlayer().getLevel();
        float mult = 1f;
        switch (level)
        {
            case NEUTRAL:
                mult = .1f;
                break;
            case SUSPICIOUS:
                mult = .11f;
                break;
            case INHOSPITABLE:
                mult = .13f;
                break;
            case HOSTILE:
                mult = .15f;
                break;
            case VENGEFUL:
                mult = .18f;
                break;
            default:
                mult = 0.08f;
        }
        return mult;
    }

    @Override
    public boolean isBlackMarket() {
        return false;
    }
}