package data.scripts.utils.interactionUI;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireAll;
import org.lazywizard.lazylib.MathUtils;

import java.awt.*;
import java.util.Map;

public class SDU_convertorExecDialog implements InteractionDialogPlugin {

    public static enum OptionId {
        INIT,
        CONT,
        askaboutraces, askaboutracers, askabouttrack, racer1, racer2, racer3, racer4, racer5, racer6, makebet, betracer1, betracer2, betracer3, betracer4, betracer5, betracer6, money0, money1, money2, money3, money4, money5, watchracetune, askaboutconversions1, exoshelllist;
    }

    protected InteractionDialogAPI dialog;
    protected TextPanelAPI textPanel;
    protected OptionPanelAPI options;
    protected VisualPanelAPI visual;
    protected Map<String, MemoryAPI> memoryMap;

    protected CampaignFleetAPI playerFleet;

    public void init(InteractionDialogAPI dialog) {
        this.dialog = dialog;
        textPanel = dialog.getTextPanel();
        options = dialog.getOptionPanel();
        visual = dialog.getVisualPanel();

        playerFleet = Global.getSector().getPlayerFleet();

        optionSelected(null, OptionId.INIT);
    }

    public Map<String, MemoryAPI> getMemoryMap() {
        return null;
    }

    public void backFromEngagement(EngagementResultAPI result) {

    }

    public void optionSelected(String text, Object optionData) {
        if (optionData == null) return;

        OptionId option = (OptionId) optionData;

        if (text != null) {
            textPanel.addParagraph(text, Global.getSettings().getColor("buttonText"));
        }

        Color sc = Global.getSector().getFaction("SDU").getBaseUIColor();

        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();

        PersonAPI pilot = null;
        FleetMemberAPI car = null;
        Object[] exoarray;
        float playermoney = playerFleet.getCargo().getCredits().get();

        switch (option) {
            case INIT:
                textPanel.addParagraph("\"We have an exciting new line of products to choose from which will most assuredly satisfy your every mechanical need.\n");

                options.clearOptions();
                options.addOption("\"What are exoshell conversions?\"", OptionId.askaboutconversions1, null);
                options.addOption("\"I'm interested, which models do you have available for purchase?\"", OptionId.exoshelllist, null);
                options.addOption("\"No thanks, I'm just browsing\"", OptionId.CONT, null);
                break;

            case askaboutconversions1:
                textPanel.addParagraph("\"Exoshell Conversions are a revolutionary new product developed in conjunction with Syzygy Actuators, AdProSec, as well as generous contributions from outside sponsors. With our patented technique the ability to create androids capable of housing and granting full humanoid movement to artificial intelligences. Simply select your desired model of Exoshell, bring your AI core to our secure conversion facility, and wire through our payment and within a few hours you will have access to one of the latest and greatest technologies in the sector.\n" +
                        "\n Please do keep in mind that these Exoshells are approved for usage with delta level AI cores only. However, our diagnostics programs are currently not operational and we will therefore be unable to verify that said cores are indeed delta level and below, we apologize for any inconvenience. Additionally, due to several customer complaints, I am obligated to inform you that this process will strip out several major internals from the AI core used and thereafter render it inoperable.");

                options.clearOptions();
                options.addOption("\"Interesting, I'll have to think about it.\"", OptionId.INIT, null);
                break;

            case exoshelllist:
                textPanel.addParagraph("\"You are welcome to browse our catalogue of available exoshells.\n");

                if(!memory.contains("$SDU_exoupdatedate")||!memory.get("$SDU_exoupdatedate").equals(Global.getSector().getClock().getDay())) {
                    Object[] exoarrayinit = new Object[6];
                    for (int i = 0; i < 6; i++) {
                        exoarrayinit[i] = OfficerManagerEvent.createOfficer(Global.getSector().getFaction("pirates"), MathUtils.getRandomNumberInRange(3, 8));
                    }

                    memory.set("$exoarray", exoarrayinit);
                    memory.set("$SDU_exoupdatedate", Global.getSector().getClock().getDay());
                }

                options.clearOptions();
                options.addOption("\"What are exoshell conversions?\"", OptionId.askaboutconversions1, null);
                options.addOption("\"I'm interested, which models do you have available for purchase?\"", OptionId.exoshelllist, null);
                options.addOption("\"No thanks, I'm just browsing\"", OptionId.CONT, null);
                break;

            case CONT:
                if(Global.getSector().getPersistentData().get("SDU_originaldialog")!=null) {
                    InteractionDialogPlugin original = (InteractionDialogPlugin) Global.getSector().getPersistentData().get("SDU_originaldialog");
                    dialog.setPlugin(original);
                    options.clearOptions();
                    FireAll.fire(null, dialog, original.getMemoryMap(), "PopulateOptions");
                    Global.getSector().getPersistentData().remove("SDU_originaldialog");
                }else{
                    dialog.dismiss();
                }
                break;

        }
    }

    public void optionMousedOver(String optionText, Object optionData) {

    }

    public void advance(float amount) {

    }

    public Object getContext() {
        return null;
    }

}
