package data.scripts.world.submarkets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.fleet.ShipRolePick;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.ShipRoles;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.submarkets.BaseSubmarketPlugin;
import com.fs.starfarer.api.util.WeightedRandomPicker;

import java.util.ArrayList;
import java.util.List;

public class SDUShootingStarsMarket extends BaseSubmarketPlugin {

    public SDUShootingStarsMarket() {
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
            float qty = s.getSize();
            cargo.removeItems(s.getType(), s.getData(), qty );
        }

        cargo.getMothballedShips().clear();


        cargo.clear();
        String[][] supershippicker = new String[41][2];
        supershippicker[0][0]=null;
        supershippicker[0][1]="hyperion_Hull";

        supershippicker[1][0]="underworld";
        supershippicker[1][1]="uw_venomx_Hull";

        supershippicker[2][0]="blackrock_driveyards";
        supershippicker[2][1]="brdy_imaginos_Hull";

        supershippicker[3][0]="Imperium";
        supershippicker[3][1]="ii_maximus_Hull";

        supershippicker[4][0]="SCY";
        supershippicker[4][1]="SCY_stymphalianbird_Hull";

        supershippicker[5][0]="diableavionics";
        supershippicker[5][1]="diableavionics_versant_Hull";

        supershippicker[6][0]="ORA";
        supershippicker[6][1]="ora_ascension_Hull";

        supershippicker[7][0]="XhanEmpire";
        supershippicker[7][1]="XHAN_Kassarek_Hull";

        supershippicker[8][0]="XhanEmpire";
        supershippicker[8][1]="XHAN_Pharrek_Hull";

        supershippicker[9][0]="vic";
        supershippicker[9][1]="vic_nybbas_Hull";

        supershippicker[10][0]="tahlan_scalartech";
        supershippicker[10][1]="tahlan_skirt_Hull";

        supershippicker[11][0]="blackrock_driveyards";
        supershippicker[11][1]="brdy_dynastos_Hull";

        supershippicker[12][0]="istl_dassaultmikoyan";
        supershippicker[12][1]="istl_snowgoose_Hull";

        supershippicker[13][0]="diableavionics";
        supershippicker[13][1]="diableavionics_miniGust_Hull";

        supershippicker[14][0]=null;
        supershippicker[14][1]="SDU_aeolus_Hull";

        supershippicker[15][0]="tahlan";
        supershippicker[15][1]="tahlan_Vale_Hull";

        supershippicker[16][0]="tahlan";
        supershippicker[16][1]="tahlan_Izanami_Hull";

        supershippicker[17][0]="HMI";
        supershippicker[17][1]="marinas_Hull";

        supershippicker[18][0]="hcok";
        supershippicker[18][1]="hcok_hetepes_Hull";

        supershippicker[19][0]="nbj_ice";
        supershippicker[19][1]="sun_ice_nightseer_Hull";

        supershippicker[20][0]="Imperium";
        supershippicker[20][1]="ii_adamas_Hull";

        supershippicker[21][0]="tahlan";
        supershippicker[21][1]="tahlan_Dominator_PH_Hull";

        supershippicker[22][0]="exshippack";
        supershippicker[22][1]="expsp_ascalon_Hull";

        supershippicker[23][0]="exshippack_armaacompat";
        supershippicker[23][1]="expsp_asdf1_frig_Hull";

        supershippicker[24][0]="Neutrino";
        supershippicker[24][1]="neutrino_causality_Hull";

        supershippicker[25][0]="oas";
        supershippicker[25][1]="oas_purjo_Hull";

        supershippicker[26][0]="prv";
        supershippicker[26][1]="prv_sinne_Hull";

        supershippicker[27][0]="prv";
        supershippicker[27][1]="prv_fasvinge_Hull";

        supershippicker[28][0]="prv";
        supershippicker[28][1]="prv_brand_Hull";

        supershippicker[29][0]="roider";
        supershippicker[29][1]="roider_roach_Hull";

        supershippicker[30][0]="shadow_ships";
        supershippicker[30][1]="ms_shamash_Hull";

        supershippicker[31][0]="Csp";
        supershippicker[31][1]="csp_LG_Mech_Hull";

        supershippicker[32][0]="star_federation";
        supershippicker[32][1]="fed_superdestroyer_Hull";

        supershippicker[33][0]="SEEKER";
        supershippicker[33][1]="SKR_endymion_Hull";

        supershippicker[34][0]="SEEKER";
        supershippicker[34][1]="SKR_augur_Hull";

        supershippicker[35][0]="vanidad";
        supershippicker[35][1]="vanidad_cargador_Hull";

        supershippicker[36][0]="ness_saw";
        supershippicker[36][1]="nes_carnelian_Hull";

        supershippicker[37][0]="edshipyard";
        supershippicker[37][1]="edshipyard_doog_Hull";

        supershippicker[38][0]="diableavionics";
        supershippicker[38][1]="diableavionics_stolas_Hull";

        supershippicker[39][0]=null;
        supershippicker[39][1]="SDU_zaku2_Hull";

        supershippicker[40][0]=null;
        supershippicker[40][1]="SDU_harbinger_Hull";

        WeightedRandomPicker<String> superships = new WeightedRandomPicker<>(itemGenRandom);
        for (int i = 0; i < 40; i++) {
            if(supershippicker[i][0]==null || Global.getSettings().getModManager().isModEnabled(supershippicker[i][0])){
                superships.add(supershippicker[i][1]);
            }
        }

        for (int i = 0; i < 3; i++) {
            FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, superships.pick());
            member.getVariant().getHullMods().add("SDU_SSoverhaul");
            member.getVariant().getHullMods().add("SDU_SSoverhauladder");

            if(member.getHullId().equals("SDU_zaku2")){

                member.getVariant().addPermaMod("magazines",true);
                member.getVariant().addPermaMod("targetingunit",true);
                member.getVariant().addPermaMod("auxiliarythrusters",true);
                member.getVariant().addPermaMod("hardenedshieldemitter",true);
            }

            cargo.getMothballedShips().addFleetMember(member);
        }
        cargo.removeEmptyStacks();
        cargo.sort();
    }

    public boolean isShipAllowed(FleetMemberAPI member, float requiredFP)
    {
        if (member.getHullSpec().isDHull()) return false;
        if (member.getHullSpec().hasTag(Tags.RESTRICTED)) return false;
        if (member.getFleetPointCost() < requiredFP) return false; //quality check
        if (member.getHullSpec().getHints().contains(ShipHullSpecAPI.ShipTypeHints.STATION)) return false;
        if (!member.isPhaseShip()) return false;

        return true;
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
                mult = 1.25f;
                break;
            case INHOSPITABLE:
                mult = 1.5f;
                break;
            case HOSTILE:
                mult = 1.75f;
                break;
            case VENGEFUL:
                mult = 2f;
                break;
            default:
                mult = .8f;
        }
        return mult;
    }

    @Override
    public boolean isBlackMarket() {
        return false;
    }
}

