package data.scripts.utils.interactionUI;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Objects;

public class SDU_CDUtils {

    //Checks

    public static boolean isCommed(){
        if (Misc.getCommissionFaction() == null)
            return true;

        return false;
    }

    public static boolean isComFac(String factionID){
        if (Misc.getCommissionFaction() == null)
            return false;

        if (Objects.equals(Misc.getCommissionFaction().getId(), factionID))
            return true;

        return false;
    }

    public static boolean FacRepAbove(String factionID, int rep){
        if(Global.getSector().getFaction(factionID)!=null && Global.getSector().getFaction(factionID).getRelToPlayer()!=null){
            if(Global.getSector().getFaction(factionID).getRelToPlayer().getRel()>rep){
                return true;
            }
        }
        return false;
    }

    public static boolean PerRepAbove(String personFullName, int rep, PersonAPI officer){
        PersonAPI person = null;
        if(personFullName==null || personFullName.equals("null")){
            person = officer;
        }else {
            if (Global.getSector().getImportantPeople() != null) {
                for (ImportantPeopleAPI.PersonDataAPI p : Global.getSector().getImportantPeople().getPeopleCopy()) {
                    if (p.getPerson().getName().getFullName().equals(personFullName)) {
                        person = p.getPerson();
                    }
                }
            }
        }
        if(person!=null && person.getRelToPlayer().getRel()>rep){
            return true;
        }
        return false;
    }

    public static boolean hasStuff(String stuffID, int count){
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        if (playerFleet == null)
            return false;
        List<CargoStackAPI> playerCargoStacks = playerFleet.getCargo().getStacksCopy();

        CargoAPI playerFleetCargo = playerFleet.getCargo();
        switch (stuffID) {
            case "creds":
                return playerFleetCargo.getCredits().get() >= count;
            case "fuel":
                return playerFleetCargo.getFuel() >= count;
            case "crew":
                return playerFleetCargo.getCrew() >= count;
            case "marines":
                return playerFleetCargo.getMarines() >= count;
            case "shipCount":
                return playerFleet.getNumShips() >= count;
            default:
                for (CargoStackAPI cargoStack : playerCargoStacks) {
                    if (cargoStack.isCommodityStack() && cargoStack.getCommodityId().equals(stuffID) && cargoStack.getSize() >= count) {
                        return true;
                    }
                }
                break;
        }

        return false;
    }

    public static boolean isAt(String marketID){
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        if (playerFleet == null)
            return false;

        if(playerFleet.getMarket().getId()!=null && playerFleet.getMarket().getId().equals(marketID)){
            return true;
        }

        return false;
    }

    //Functions

    public static void addStuff(String stuffID, int count){
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        if (playerFleet == null)
            return;

        CargoAPI playerFleetCargo = playerFleet.getCargo();
        if(stuffID.equals("creds")) {
            playerFleetCargo.getCredits().add(count);
        }else if (stuffID.equals("fuel")) {
            playerFleetCargo.addFuel(count);
        }else if (stuffID.equals("crew")) {
            playerFleetCargo.addCrew(count);
        }else if (stuffID.equals("marines")) {
            playerFleetCargo.addMarines(count);
        }else {
            playerFleetCargo.addCommodity(stuffID, count);
        }
    }
}
