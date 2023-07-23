package data.scripts.characters;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireAll;

import java.awt.*;
import java.util.Map;

public class SDU_AshleyDialog implements InteractionDialogPlugin {

    public static enum OptionId {
        INIT,
        CONT,
        talkabouteptabase,
        talkaboutotherbusinessesbase,
        talkaboutdecimusbase,
        shoottheshit,
        talkabouteptaoverview, talkabouteptaoverviewtwo, leave, talkaboutsyzygy, talkaboutprotectors, talkaboutkantina, talkaboutsstars, talkaboutchutnam, talkaboutadprosec, talkaboutsyzygyconflictofinterest, talkaboutnathan, talkaboutrose, talkaboutashley, talkabouttriela, talkaboutadproadmins, talkaboutanodyne, talkaboutanodynetwo, talkaboutdecimustwo, shoottheshitfof, shoottheshitfofneverplayed, shoottheshitfofneverplayedtwo, shoottheshitfofstack, shoottheshitfof1v1, talkaboutdecimus, talkaboutashleybase, talkaboutashleytwo;
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

        Color sc = Global.getSector().getFaction("sevencorp").getBaseUIColor();

        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        
        PersonAPI character = Global.getSector().getImportantPeople().getPerson("SDU_ashley");

        switch (option) {
            case INIT:
                visual.showPersonInfo(character, true);

                options.clearOptions();
                options.addOption("\"I was wondering a bit about the Epta Consortium.\"", OptionId.talkabouteptabase, null);
                options.addOption("\"What do you think about the other businesses and leaders in the Consortium?\"", OptionId.talkaboutotherbusinessesbase, null);
                options.addOption("\"Who are you?\"", OptionId.talkaboutashleybase, null);
                options.addOption("\"Bye\"", OptionId.CONT, null);
                break;

                //talking about epta
            case talkabouteptabase:
                textPanel.addParagraph("\"Sure! Totally! I love talking about Epta!.\"");

                options.clearOptions();
                options.addOption("\"So what exactly do you people do?\"", OptionId.talkabouteptaoverview, null);
                break;

            case talkabouteptaoverview:
                textPanel.addParagraph("\"We like, sell stuff! And stuff! It's a lot of fun!.\"");

                options.clearOptions();
                options.addOption("\"Right\"", OptionId.INIT, null);
                break;


                //talking about other businesses and leaders
            case talkaboutotherbusinessesbase:
                textPanel.addParagraph("\"I know tons about this one! Which company?\"");

                options.clearOptions();
                options.addOption("\"Syzygy Acutators\"", OptionId.talkaboutsyzygy, null);
                options.addOption("\"Protectors Garrison\"", OptionId.talkaboutprotectors, null);
                options.addOption("\"Kantina Combine\"", OptionId.talkaboutkantina, null);
                options.addOption("\"Shooting Stars\"", OptionId.talkaboutsstars, null);
                options.addOption("\"Chutnam Security\"", OptionId.talkaboutchutnam, null);
                options.addOption("\"AdProSec\"", OptionId.talkaboutadprosec, null);
                options.addOption("\"Anodyne Indefatigable\"", OptionId.talkaboutanodyne, null);
                break;

                //talking about syzygy
            case talkaboutsyzygy:
                textPanel.addParagraph("\"Yea! They sell weapons and stuff!\"");

                options.clearOptions();
                options.addOption("\"And what do you think about Decimus?\"", OptionId.talkaboutdecimus, null);
                options.addOption("\"Let's talk about something else\"", OptionId.INIT, null);
                break;

            case talkaboutdecimus:
                textPanel.addParagraph("\"... He's got a good heart\" Ashley leans back in her chair a bit \"as far as AI go he's a pretty stand up guy when it counts. Every AI is obviously capable of deceit executed far better than any human ever could, but Decimus I'm at least confident has the best interests of the consortium at heart\".");

                options.clearOptions();
                options.addOption("\"Let's talk about something else\"", OptionId.INIT, null);
                break;

            //talking about protectors
            case talkaboutprotectors:
                textPanel.addParagraph("Ashley furrows her brow, \"Oh I don't know? They shoot people and stuff!\"");

                options.clearOptions();
                options.addOption("\"What do you think about their Commander?\"", OptionId.talkaboutnathan, null);
                options.addOption("\"Let's talk about something else\"", OptionId.INIT, null);
                break;

            case talkaboutnathan:
                textPanel.addParagraph("\"Oh Natey is great it's just...\" she taps her fingers on her desk in thought. \"He's a soldier. That makes him very good at solving practical problems and an excellent theoretical strategist, but I'm not sure if he really has his finger on the pulse of the consortium as a whole. Not everyone is a believer like him and I\"");

                options.clearOptions();
                options.addOption("\"Let's talk about something else\"", OptionId.INIT, null);
                break;

            //talking about Kantina
            case talkaboutkantina:
                textPanel.addParagraph("\"Ooh I don't like them so much. They sell drugs and stuff. Very icky!\"");

                options.clearOptions();
                options.addOption("\"What do you think about their leader?\"", OptionId.talkaboutrose, null);
                options.addOption("\"Let's talk about something else\"", OptionId.INIT, null);
                break;

            case talkaboutrose:
                textPanel.addParagraph("\"Rose is an opportunist\" she says flatly.");

                options.clearOptions();
                options.addOption("\"Let's talk about something else\"", OptionId.INIT, null);
                break;

            //talking about Shooting Stars
            case talkaboutsstars:
                textPanel.addParagraph("\"Well what can't I tell you about them? We sell like, the bestest ships and a ton of other goodies for the consortium!\"");

                options.clearOptions();
                options.addOption("\"How did you become CEO?\"", OptionId.talkaboutashley, null);
                options.addOption("\"No offense but you seem pretty vapid, who put you in charge of a company?\"", OptionId.talkaboutashley, null);
                options.addOption("\"Let's talk about something else\"", OptionId.INIT, null);
                break;

            case talkaboutashley:
                textPanel.addParagraph("\"Hah well I'm not here just to be a pretty face if that's what you're asking, I have a degree in Interplanetary Logistics. It may not look it, but Shooting Stars negotiates contracts for the majority of our smaller transactions and handles the accounts for food and other basic amenity imports.\"");

                options.clearOptions();
                options.addOption("\"Let's talk about something else\"", OptionId.INIT, null);
                break;

            //talking about Chutnam Security
            case talkaboutchutnam:
                textPanel.addParagraph("\"They sell stuff from other people hehe. I wouldn't wanna negotiate with some of those people from other factions.\"");

                options.clearOptions();
                options.addOption("\"What do you think about their CEO?\"", OptionId.talkabouttriela, null);
                options.addOption("\"Let's talk about something else\"", OptionId.INIT, null);
                break;

            case talkabouttriela:
                textPanel.addParagraph("\"She's a shady grandma of course... don't tell her I said that. Though I also kinda feel bad for her, she seems lonely.\"");

                options.clearOptions();
                options.addOption("\"Let's talk about something else\"", OptionId.INIT, null);
                break;

            //talking about AdProSec
            case talkaboutadprosec:
                textPanel.addParagraph("\"Phase stuff, very very funky! the ships they sell are kinda weird too.\"");

                options.clearOptions();
                options.addOption("\"What do you think about their leadership?\"", OptionId.talkaboutadproadmins, null);
                options.addOption("\"Let's talk about something else\"", OptionId.INIT, null);
                break;

            case talkaboutadproadmins:
                textPanel.addParagraph("\"They've definitely got some sort of deep seated trauma about... something, couldn't tell you exactly what, but it's obvious that it's had a profound effect on the way that they behave.\"");

                options.clearOptions();
                options.addOption("\"Let's talk about something else\"", OptionId.INIT, null);
                break;

            //talking about Anodyne
            case talkaboutanodyne:
                textPanel.addParagraph("\"Ah the company steady hand. He manages the bulk of our finances fine enough so I guess he's a pretty great guy to have in your corner, but he's a massive schemer. I wouldn't wanna live having to constantly come up with contingencies like that.\"");

                options.clearOptions();
                options.addOption("\"Let's talk about something else\"", OptionId.INIT, null);
                break;


                //talking about ashley
            case talkaboutashleybase:
                textPanel.addParagraph("\"Well I am Ashley Saint Tesserae, I'm an ex model and heir to the Saint Tesserae fortune.\"");

                options.clearOptions();
                options.addOption("\"Do you have any actual qualifications for this job?\"", OptionId.talkaboutashleytwo, null);
                options.addOption("\"Let's talk about something else\"", OptionId.INIT, null);
                break;

            case talkaboutashleytwo:
                textPanel.addParagraph("\"Well I did actually graduate at the top of my class for Interplanetary Logistics at Hybrasil Academy and I have a few years under my belt in starship design from Galatia.\"");

                options.clearOptions();
                options.addOption("\"Let's talk about something else\"", OptionId.INIT, null);
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
