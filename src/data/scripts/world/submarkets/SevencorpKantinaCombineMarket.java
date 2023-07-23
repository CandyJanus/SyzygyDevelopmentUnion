package data.scripts.world.submarkets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.submarkets.BaseSubmarketPlugin;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.lazywizard.lazylib.MathUtils;

import java.util.Set;

public class SevencorpKantinaCombineMarket extends BaseSubmarketPlugin {
    private static WeightedRandomPicker<String> hullmodPicker = new WeightedRandomPicker();
    public static float ECON_UNIT_MULT_EXTRA;
    public static float ECON_UNIT_MULT_PRODUCTION;
    public static float ECON_UNIT_MULT_IMPORTS;
    public static float ECON_UNIT_MULT_DEFICIT;
    public static Set<String> SPECIAL_COMMODITIES;

    public SevencorpKantinaCombineMarket() {
    }


    @Override
    public void updateCargoPrePlayerInteraction()
    {
        //log.info("Days since update: " + sinceLastCargoUpdate);
        if (sinceLastCargoUpdate < 30) return;
        sinceLastCargoUpdate = 0f;

        CargoAPI cargo = getCargo();

        for (CargoStackAPI s : cargo.getStacksCopy()) {
            float qty = s.getSize();
            cargo.removeItems(s.getType(), s.getData(), qty );
        }

        cargo.clear();

        cargo.removeEmptyStacks();
        cargo.addCommodity("organs", MathUtils.getRandomNumberInRange(800,2500));
        cargo.addCommodity("drugs",MathUtils.getRandomNumberInRange(800,2500));
        if(Global.getSettings().getModManager().isModEnabled("HMI")){
            cargo.addCommodity("red_water",MathUtils.getRandomNumberInRange(100,800));
        }
        if(Global.getSettings().getModManager().isModEnabled("alcoholism")){
            cargo.addCommodity("alcoholism_freedom_c",MathUtils.getRandomNumberInRange(25,150));
        }
        cargo.sort();
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
