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

import java.util.List;
import java.util.Set;

public class SDUEliteBuyersMarket extends BaseSubmarketPlugin {
    private static WeightedRandomPicker<String> hullmodPicker = new WeightedRandomPicker();
    public static float ECON_UNIT_MULT_EXTRA;
    public static float ECON_UNIT_MULT_PRODUCTION;
    public static float ECON_UNIT_MULT_IMPORTS;
    public static float ECON_UNIT_MULT_DEFICIT;
    public static Set<String> SPECIAL_COMMODITIES;

    private final RepLevel MIN_STANDING = RepLevel.COOPERATIVE;

    public SDUEliteBuyersMarket() {
    }

    @Override
    public void updateCargoPrePlayerInteraction()
    {
        //log.info("Days since update: " + sinceLastCargoUpdate);
        if (sinceLastCargoUpdate < 30) return;
        sinceLastCargoUpdate = 0f;

        CargoAPI cargo = getCargo();

        cargo.clear();
        cargo.removeEmptyStacks();
        cargo.addCommodity("alpha_core", MathUtils.getRandomNumberInRange(0,2));
        cargo.addCommodity("beta_core", MathUtils.getRandomNumberInRange(2,4));
        cargo.addCommodity("gamma_core", MathUtils.getRandomNumberInRange(4,6));
        cargo.addHullmods("SDU_AIcoremod_gamma",1);
        cargo.addHullmods("SDU_AIcoremod_beta",1);
        cargo.addHullmods("SDU_AIcoremod_alpha",1);
        cargo.sort();
    }

    @Override
    public String getTooltipAppendix(CoreUIAPI ui) {
        RepLevel level = market.getFaction().getRelationshipLevel(Global.getSector().getFaction(Factions.PLAYER));

        if (!level.isAtWorst(MIN_STANDING)) {
            return "Requires: " + market.getFaction().getDisplayName() + " - "
                    + MIN_STANDING.getDisplayName().toLowerCase();
        }

        return super.getTooltipAppendix(ui);
    }

    @Override
    public boolean isEnabled(CoreUIAPI ui) {
        RepLevel level = market.getFaction().getRelationshipLevel(Global.getSector().getFaction(Factions.PLAYER));
        return level.isAtWorst(MIN_STANDING);
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
        return 1f;
    }

    @Override
    public boolean isBlackMarket() {
        return false;
    }
}

