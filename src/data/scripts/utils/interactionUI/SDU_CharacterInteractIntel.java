//Commented out this, bcs this is from other mod or such, so, better not use for now

package data.scripts.utils.interactionUI;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SDU_CharacterInteractIntel extends BaseIntelPlugin {

    public static Logger log = Global.getLogger(SDU_CharacterInteractIntel.class);
    protected PersonAPI member;
    protected int pilot;

    protected IntervalUtil updateInterval = new IntervalUtil(0.25f, 0.25f);

    public SDU_CharacterInteractIntel() {
        Global.getSector().getIntelManager().addIntel(this);
        Global.getSector().addScript(this);
        this.setImportant(true);
    }

    public List<FleetMemberAPI> getAllShips() {
        return Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy();
    }

    public float getCredits() {
        return 1;
    }

    @Override
    public String getSmallDescriptionTitle() {
        return "getName";
    }

    protected String getName() {
        if (listInfoParam != null && listInfoParam instanceof List)
            return "titleV2Expire";
        return "Officer Interaction";
    }

    @Override
    public boolean hasSmallDescription() {
        return false;
    }

    @Override
    public boolean hasLargeDescription() {
        return true;
    }

    @Override
    public boolean isPlayerVisible() {
        return true;
    }

    public static int TAB_BUTTON_HEIGHT = 20;
    public static int TAB_BUTTON_WIDTH = 180;
    public static int ENTRY_HEIGHT = 80;
    public static int ENTRY_WIDTH = 300;
    public static int IMAGE_WIDTH = 80;
    public static int MANAGE_BUTTON_WIDTH = 120;
    public static int IMAGE_DESC_GAP = 12;

    protected void createFleetView(CustomPanelAPI panel, TooltipMakerAPI info, float width)
    {
        float pad = 3;
        float opad = 10;
        Color h = Misc.getHighlightColor();
        CampaignFleetAPI player = Global.getSector().getPlayerFleet();
        if (player == null) return;

        PersonAPI playerChar = player.getFleetData().getCommander();

        List<PersonAPI> officers = new ArrayList();
        for (OfficerDataAPI member : player.getFleetData().getOfficersCopy())
        {
            officers.add(member.getPerson());
        }

        float heightPerItem = ENTRY_HEIGHT + opad;
        int numItems = officers.size();
        float itemPanelHeight = heightPerItem * numItems;
        CustomPanelAPI itemPanel = panel.createCustomPanel(ENTRY_WIDTH, itemPanelHeight, null);
        float yPos = opad;

        for (PersonAPI member : officers)
        {
            if(!member.equals(Global.getSector().getPlayerPerson())) {
                makeEntry(itemPanel,member,yPos);
                yPos += ENTRY_HEIGHT + opad;
            }

        }
        info.addCustom(itemPanel, 0);
        member = null;
    }

    public void makeEntry(CustomPanelAPI itemPanel, PersonAPI member, float yPos){
        float pad = 3;
        float opad = 10;
        Color h = Misc.getHighlightColor();


        TooltipMakerAPI image = itemPanel.createUIElement(80f, 80f, false);
        image.addImage(member.getPortraitSprite(), 80f, 0);
        itemPanel.addUIElement(image).inTL(4, yPos);

        TooltipMakerAPI entry = itemPanel.createUIElement(300f - IMAGE_WIDTH - IMAGE_DESC_GAP,
                ENTRY_HEIGHT, true);
        entry.addPara("Name: " + member.getName().getFullName(), opad, h, member.getName().getFullName());
        entry.addPara("Rank: " + member.getRank(), pad, h, member.getRank());

        //itemPanel.addUIElement(entry).inTL(4 + IMAGE_WIDTH + IMAGE_DESC_GAP, yPos);

        itemPanel.addUIElement(entry).rightOfTop(image, IMAGE_DESC_GAP);

        TooltipMakerAPI buttonHolder = itemPanel.createUIElement(MANAGE_BUTTON_WIDTH, 16, false);
        String name = "Interact";
        ButtonAPI manage = buttonHolder.addButton(name, member, 120, 16, 0);
        itemPanel.addUIElement(buttonHolder).inTL(4 + IMAGE_WIDTH + IMAGE_DESC_GAP + ENTRY_WIDTH, yPos + ENTRY_HEIGHT / 4);

    }


    @Override
    public void createLargeDescription(CustomPanelAPI panel, float width, float height) {
        float opad = 10;
        float pad = 3;
        Color h = Misc.getHighlightColor();

        TooltipMakerAPI info = panel.createUIElement(width, height - TAB_BUTTON_HEIGHT - 4, true);
        FactionAPI faction = Global.getSector().getPlayerFaction();

        info.addSectionHeading("Officer Interaction", faction.getBaseUIColor(),
                faction.getDarkUIColor(), Alignment.MID, opad);

        createFleetView(panel, info, width);

        panel.addUIElement(info).inMid();
    }


    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
        if (buttonId instanceof PersonAPI) {
            PersonAPI officer = (PersonAPI) buttonId;
            member = officer;

            log.info("trying to make dialogue");

            if(SDU_OfficerExt_List.getExtendedOfficer(officer)!=null){
                SDU_CharacterDialog dialog = new SDU_CharacterDialog();
                dialog.setofficer(officer);
                ui.showDialog(null,dialog);

            }else{
                SDU_OfficerExt EOfficer = new SDU_OfficerExt();
                EOfficer.create(officer,officer.getId(),true,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
                SDU_CharacterDialog dialog = new SDU_CharacterDialog();
                dialog.setofficer(officer);
                ui.showDialog(null,dialog);
                log.info("made new eofficer");

            }

            return;
        }
    }

    @Override
    public boolean doesButtonHaveConfirmDialog(Object buttonId) {

        return false;
    }

    @Override
    public String getConfirmText(Object buttonId) {
        return super.getConfirmText(buttonId);
    }

    @Override
    public String getCancelText(Object buttonId) {
        return super.getCancelText(buttonId);
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add("Officer Interaction");
        return tags;
    }

    @Override
    public String getIcon() {
        return ("graphics/icons/markets/lobster_pens.png");
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    protected void notifyEnded() {
        Global.getSector().getIntelManager().removeIntel(this);
        Global.getSector().removeScript(this);
    }

    protected static String getString(String id) {
        return "GetString";
    }

}