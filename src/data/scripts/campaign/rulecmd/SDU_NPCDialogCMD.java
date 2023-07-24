package data.scripts.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
//import data.scripts.characters.SDU_AnodyneDialog;
import data.scripts.characters.SDU_AshleyDialog;
import data.scripts.characters.SDU_DecimusDialog;
import data.scripts.characters.SDU_NathanDialog;
import data.scripts.utils.interactionUI.*;

import java.util.List;
import java.util.Map;

public class SDU_NPCDialogCMD extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        OptionPanelAPI options = dialog.getOptionPanel();
        options.clearOptions();

        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        String cmd = null;

        cmd = params.get(0).getString(memoryMap);

        Global.getSector().getPersistentData().put("SDU_originaldialog",dialog.getPlugin());

        //nuke: some refactors for neatness and performance, done with Lukas04's help

        InteractionDialogPlugin characterDialog=null;

        switch (cmd) {
//            case "AnodyneDialog":
//                characterDialog= new SDU_AnodyneDialog();
//                break;
            case "DeciumusDialog":
                characterDialog = new SDU_DecimusDialog();
                break;

            case "AshleyDialog":
                characterDialog = new SDU_AshleyDialog();
                break;

            case "NathanDialog":
                characterDialog = new SDU_NathanDialog();
                break;


            case "convertorExecDialog":
                characterDialog = new SDU_convertorExecDialog();
                break;

        }

        dialog.setPlugin(characterDialog);
        characterDialog.init(dialog);

        return true;
    }
}
