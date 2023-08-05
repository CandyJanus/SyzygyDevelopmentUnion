package data.scripts.utils.interactionUI;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;

import java.util.ArrayList;

public class SDU_OfficerExt_List {

    public ArrayList<SDU_OfficerExt> list;
    public SDU_OfficerExt_List create() {
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        this.list = new ArrayList<>();
        memory.set("$SDU_OfficerExt_List",this.list);
        return this;
    }

    public static ArrayList<SDU_OfficerExt> getOEList(){
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        return (ArrayList<SDU_OfficerExt>) memory.get("$SDU_OfficerExt_List");
    }

    public static SDU_OfficerExt getExtendedOfficer(PersonAPI person){
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        ArrayList<SDU_OfficerExt> list = (ArrayList<SDU_OfficerExt>) memory.get("$SDU_OfficerExt_List");
        for(SDU_OfficerExt EO:list){
            if(EO.person.equals(person)||EO.person==person){
                return EO;
            }
        }
        return null;
    }

    public static SDU_OfficerExt getExtendedOfficer(String id){
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        ArrayList<SDU_OfficerExt> list = (ArrayList<SDU_OfficerExt>) memory.get("$SDU_OfficerExt_List");
        for(SDU_OfficerExt EO:list){
            if(EO.id.equals(id)){
                return EO;
            }
        }
        return null;
    }
}

