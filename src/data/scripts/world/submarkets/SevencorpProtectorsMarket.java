package data.scripts.world.submarkets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.combat.ShipAPI;
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
import org.lazywizard.lazylib.MathUtils;

import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SevencorpProtectorsMarket extends BaseSubmarketPlugin {

    public SevencorpProtectorsMarket() {
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
        cargo.removeEmptyStacks();
        cargo.addCommodity("marines", MathUtils.getRandomNumberInRange(500,2500));
        cargo.addCommodity("hand_weapons", MathUtils.getRandomNumberInRange(500,2500));
        addShips();
        cargo.sort();
    }

    public boolean isShipAllowed(FleetMemberAPI member, float requiredFP)
    {
        if (member.getHullSpec().isDHull()) return false;
        if (member.getHullSpec().hasTag(Tags.RESTRICTED)) return false;
        if (member.getFleetPointCost() < requiredFP) return false; //quality check
        if (member.getHullSpec().getHints().contains(ShipHullSpecAPI.ShipTypeHints.STATION)) return false;
        if (!member.getHullSpec().getBuiltInMods().contains("strikeCraft")) return false;

        return true;
    }

    protected void addShips() {

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

        WeightedRandomPicker<FactionAPI> factionPicker = new WeightedRandomPicker<>(itemGenRandom);
        SectorAPI sector = Global.getSector();
        for (FactionAPI factionId: sector.getAllFactions()) {
            FactionAPI faction = sector.getFaction(factionId.getId());
            if (faction == null) continue;
            factionPicker.add(sector.getFaction(factionId.getId()));
        }

        //renew the stock
        float variation=(float)itemGenRandom.nextFloat() * 0.5f + 0.75f;
        int tries = 0;
        int target = Math.round(10*variation);
        for (int i=0; i<target; i=cargo.getMothballedShips().getNumMembers()){
            //pick the role and faction
            java.util.List<ShipRolePick> picks = null;
            do {
                FactionAPI faction = factionPicker.pick();
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
                if (isShipAllowed(member, FP))
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
            if (tries > 40 + target && picks.size() > 5) break;
        }
    }

    @Override
    public float getTariff() {
        RepLevel level = submarket.getFaction().getRelationshipLevel(Global.getSector().getFaction(Factions.PLAYER));
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

