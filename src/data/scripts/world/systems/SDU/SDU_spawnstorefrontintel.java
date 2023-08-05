package data.scripts.world.systems.SDU;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.Set;

public class SDU_spawnstorefrontintel extends BaseIntelPlugin {

    //In-script variables
    private MarketAPI target;
    private String intendedFaction;
    private int subcorp;

    MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();

    public SDU_spawnstorefrontintel(MarketAPI target, String intendedFaction, int subcorp) {
        this.target = target;
        this.intendedFaction = intendedFaction;
        this.subcorp = subcorp;
    }

    @Override
    public void advance(float amount) {
        if (!intendedFaction.equals(target.getFactionId())) {
            endImmediately();
        }
    }

    //Handles the bullet-points on the intel screen. Only adds the title for now, that seems most compact and neat
    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color c = getTitleColor(mode);
        float opad = 10f;
        info.setParaSmallInsignia();
        info.addPara(getName(), c, 0f);
    }

    @Override
    public void addDays(TooltipMakerAPI info, String after, float days, Color c, float pad) {
        super.addDays(info, after, days, c, pad);
    }

    // The description shown on the intel screen summary
    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        Color tc = Misc.getTextColor();
        float pad = 3f;
        float opad = 10f;

        if(subcorp==1) {
            info.addPara("Buy buy buy! Treat yourself to the finest things that Syzygy Actuators has in stock; everything from Cryptodeimos heavy gauss cannons to Acaulis Arrays to even top shelf weaponry from our competitors can be found at our storefront for low LOW prices. But don't wait, this special offer will only be available for a short time.", pad);
        }else if(subcorp==2){
            info.addPara("The new Protectors Garrison tradebase has just been set up and we have a fresh stock of all new strikecraft, mecha, marine equipment, and troop transports. Hurry on down for your one stop shop for all of your planetary tactical needs.", pad);
        }else if(subcorp==3){
            info.addPara("Word on the street is that you know a good deal when you see one; in this case a deal is guaranteed, real off the books high profit margin type stuff. If you've got a taste for things that the hegies don't want you to have then swing by the new Kantina Combine storefront.", pad);
        }else if(subcorp==4){
            info.addPara("Chutnam Security has just secured a key business contract with an insider at another faction. Buy quickly while this offer is available, stocks are limited.", pad);
        }else if(subcorp==5){
            info.addPara("Keep it on the down low, but AdProSec has just procured a new stock of phase ships and other advanced tech. Now is the time to buy if you're a customer that wants quality.", pad);
        }else if(subcorp==6){
            info.addPara("We at the Shooting Stars believe in quality above all else; that's why we've procured one of the finest superships in the sector for your captaining enjoyment. As usual this is on a first come first serve basis so hurry down so that you don't miss this rare opportunity.", pad);
        }else if(subcorp==7){
            info.addPara("Make sure to come visit our primary manufactory and crown jewel of SDU's holdings; Fundament, where all your technological dreams come true.", pad);
        }

    }

    //Gets the icon to display in the intel screen
    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("misc", "SDU_icon");
    }


    @Override
    protected void notifyEnded() {
        super.notifyEnded();
        Global.getSector().removeScript(this);
    }

    @Override
    public void endAfterDelay() {
        super.endAfterDelay();
    }

    @Override
    protected void notifyEnding() {
        super.notifyEnding();
    }

    //Tags in the intel screen
    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(target.getFactionId());
        return tags;
    }

    @Override
    public IntelSortTier getSortTier() {
        return IntelSortTier.TIER_6;
    }

    public String getSortString() {
        return target.getFaction().getDisplayName();
    }

    // What the intel is called
    public String getName() {
        if(subcorp==1) {
            return "Syzygy Actuators Tradebase";
        }else if(subcorp==2){
            return "Protectors Garrison Tradebase";
        }else if(subcorp==3){
            return "Kantina Combine Tradebase";
        }else if(subcorp==4){
            return "Chutnam Security Tradebase";
        }else if(subcorp==5){
            return "AdProSec Tradebase";
        }else if(subcorp==6){
            return "Shooting Stars Tradebase";
        }else if(subcorp==7) {
            return "Fundament";
        }else{
            return "sth went wrong";
        }
    }

    @Override
    public FactionAPI getFactionForUIColors() {
        return target.getFaction();
    }

    public String getSmallDescriptionTitle() {
        return getName();
    }

    @Override
    public boolean shouldRemoveIntel() {
        return super.shouldRemoveIntel();
    }

    //The noise to play when a new message shows up
    @Override
    public String getCommMessageSound() {
        return getSoundMinorMessage();
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        return target.getPrimaryEntity();
    }
}
