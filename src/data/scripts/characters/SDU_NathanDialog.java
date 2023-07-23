package data.scripts.characters;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireAll;
import org.lazywizard.lazylib.MathUtils;

import java.awt.*;
import java.util.Map;

public class SDU_NathanDialog implements InteractionDialogPlugin {

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
        
        PersonAPI character = Global.getSector().getImportantPeople().getPerson("epta_ashley");

        switch (option) {
            case INIT:
                visual.showPersonInfo(character, true);

                options.clearOptions();
                options.addOption("\"I was wondering a bit about the Epta Consortium.\"", OptionId.talkabouteptabase, null);
                options.addOption("\"What do you think about the other businesses and leaders in the Consortium?\"", OptionId.talkaboutotherbusinessesbase, null);
                options.addOption("\"Who are you?\"", OptionId.talkaboutdecimusbase, null);
                options.addOption("\"What's up?\"", OptionId.shoottheshit, null);
                options.addOption("\"Bye\"", OptionId.CONT, null);
                break;

                //talking about epta
            case talkabouteptabase:
                textPanel.addParagraph("\"Sure! Totally! I love talking about Epta!.\"");

                options.clearOptions();
                options.addOption("\"So what exactly do you people do?\"", OptionId.talkabouteptaoverview, null);
                break;

            case talkabouteptaoverview:
                textPanel.addParagraph("\"We like, sell stuff! And stuff! It's alot of fun!.\"");

                options.clearOptions();
                options.addOption("\"Right\"", OptionId.INIT, null);
                break;


                //talking about other businesses and leaders
            case talkaboutotherbusinessesbase:
                textPanel.addParagraph("\"I know tonnes about this one! Which company?\"");

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
                textPanel.addParagraph("\"... He's got a good heart\" Ashley leans back in her chair a bit \"as far as AI go he's a pretty stand up guy. Every AI is obviously capable of deceit executed far better than any human ever could, but Decimus I'm at least confident has the best interests of the consortium at heart\".");

                options.clearOptions();
                options.addOption("\"Let's talk about something else\"", OptionId.INIT, null);
                break;

            //talking about protectors
            case talkaboutprotectors:
                textPanel.addParagraph("Ashley furrows her brow, \"Oh I dont know? They shoot people and stuff!\"");

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
                textPanel.addParagraph("\"Hah well I'm not here just to be a pretty face if that's what you're asking, I'm actually quite good at handling people. It may not look it, but Shooting Stars negotiates contracts for the majority of our smaller transactions and handles the accounts for food and other basic amenity imports.\"");

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
                textPanel.addParagraph("\"She's a shady grandma of course... don't tell her I said that.\"");

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
                textPanel.addParagraph("\"Well I am Ashley Saint Tesserae.\"");

                options.clearOptions();
                options.addOption("\"What are your qualifications then?\"", OptionId.talkaboutdecimustwo, null);
                options.addOption("\"Let's talk about something else\"", OptionId.INIT, null);
                break;

            case talkaboutashleytwo:
                textPanel.addParagraph("\"Well for starters I run on an Alpha Core base which already puts me leagues ahead of most in terms of decisionmaking, plus I got tinkered with by Gia himself for another pretty big boost, and on top of both of those I've even got some custom made combat protocols programmed specifically for yours truly. I hold the number 1 spots in the consortium for simulated fleet victories and score in combat scenarios 1 through 80 and top 10 ranks in the remaining 20 scenarios. I also have the most hours by far logged in Proelefsi combat scenarios and even have a few hundred in actual combat with it.\"");

                options.clearOptions();
                options.addOption("\"Let's talk about something else\"", OptionId.INIT, null);
                break;


                //talking about random shit
            case shoottheshit:
                int randomdialog = MathUtils.getRandomNumberInRange(1,1);
                if(randomdialog==1) {
                    textPanel.addParagraph("\"I just totally figured out a new tech in Field of Fire 7. Super excited to abuse it.\"");

                    options.clearOptions();
                    if(!memory.contains("$SDU_ownsfof")) {
                        options.addOption("\"Field of Fire?\"", OptionId.shoottheshitfofneverplayed, null);
                    }else{
                        options.addOption("\"Oh really? looking to play?\"", OptionId.shoottheshitfof, null);
                    }
                    options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                }
                break;

            case shoottheshitfofneverplayed:
                textPanel.addParagraph("\"Yea, it's a game, kinda a classic at this point, but any real fan of the series knows that this is the best entry. Wanna try it out? I can send you a digital console and some game files if you want.\"");

                options.clearOptions();
                options.addOption("\"Alright, I'll give it a try.\"", OptionId.shoottheshitfofneverplayedtwo, null);
                options.addOption("\"No thanks, let's talk about something else.\"", OptionId.INIT, null);
                break;

            case shoottheshitfofneverplayedtwo:
                textPanel.addParagraph("\"Awesome, I'll send em over.\" Decimus pumps his fist and a few moments later several applications begin installing themselves on your data terminal. The game boots up and a holocontroller manifests in front of you as the opening cinematic plays. Your eyes are instantly assailed by short clips of domain era conflicts depicted in utterly tasteless style. After a few brief dialogues that are automatically filled in the words \"Connected to EGN Servers, Account Validation Complete\"");
                memory.set("$SDU_ownsfof","-");

                options.clearOptions();
                options.addOption("Continue", OptionId.shoottheshitfof, null);
                break;

            case shoottheshitfof:
                textPanel.addParagraph("Decimus sends you an invite on FoF, \"Alright captain, what're we doing, duo stack multiplayer or 1v1?\"");

                options.clearOptions();
                options.addOption("\"Duo stack multiplayer.\"", OptionId.shoottheshitfofstack, null);
                //options.addOption("\"Let's 1v1.\"", OptionId.shoottheshitfof1v1, null);
                break;

            case shoottheshitfofstack:
                if(!memory.contains("$SDU_player_fofskill")){
                    memory.set("$SDU_player_fofskill",0);
                }
                int playerskill = memory.getInt("$SDU_player_fofskill");
                int playerscore = MathUtils.getRandomNumberInRange(playerskill/10+1,playerskill/2);
                int decimusscore = MathUtils.getRandomNumberInRange(25,51);
                if(playerskill==0){
                    textPanel.addParagraph("A deluge of sights and sounds washes over you as dozens of briefly mentioned tutorial messages haphazardly guide you through what it is you're supposed to be doing. You take the place of an invasion marine in what appears to be a jungle setting, the graphics are incredibly lifelike, though you can't help but be bothered by the fact that your marine has seemingly forgone the traditionally heavy armor of the time for... style maybe? While you try to figure out the controls of the game Decimus' marine sprints forwards with a few others and a short time later you hear intense gunfire followed by a feed of the casualties from the last engagement, most of it being Decimus' handiwork");
                }
                if(playerskill<=20){
                    textPanel.addParagraph("The match plays out mostly as you'd expect; the players in this lobby are all much better at the game than you are and it's somewhat difficult to tell what exactly you're supposed to be doing or shooting at.");
                }else if(playerskill<=40){
                    textPanel.addParagraph("By this point you've gotten a bit more proficient at the game, not quite the glorified punching bag that you used to be. Still not quite up to the level as the rest of the players here, but at least you've got a sense of what to do now.");
                }else if(playerskill<=60){
                    textPanel.addParagraph("This match goes alright for you, finally you can call yourself a somewhat decent player.");
                }else if(playerskill<=80){
                    textPanel.addParagraph("Business as usual, bing, bang, boom. You deftly switch between weapons and get the drop on most of your enemies before they even see you coming");
                }else {
                    textPanel.addParagraph("Between you and Decimus the match turns from a battle into a slaughter.");
                }
                if(playerscore<=10){
                    textPanel.addParagraph("The match didn't quite go your way. Most of the time you were getting killed with barely any idea of how. You finished off with just "+playerscore+" kills.");
                }else if(playerscore<=20){
                    textPanel.addParagraph("This match definitely wasn't the greatest for you and you only barely broke even. You finished off with "+playerscore+" kills.");
                }else if(playerscore<=30){
                    textPanel.addParagraph("You did fairly decently in this match, ending it with a solid "+playerscore+" kills.");
                }else if(playerscore<=44){
                    textPanel.addParagraph("This was a great match for you, you tore through most of the enemy like they were tissue paper and scored a whopping "+playerscore+" kills.");
                }else {
                    textPanel.addParagraph("You were on fire for the whole match, a veritable god of battle rivalled only by your teammate. You destroyed this match with "+playerscore+" kills.");
                }
                memory.set("$SDU_player_fofskill",playerskill+1);
                textPanel.addParagraph("At the end of the match Decimus' avatar mimics a contented sigh \"Nice... Alright captain, another match or nah?\"");

                options.clearOptions();
                options.addOption("\"Yea let's go one more.\"", OptionId.shoottheshitfofstack, null);
                options.addOption("\"I'm ok for now. Let's talk about something.\"", OptionId.INIT, null);
                break;

            case CONT:
                if(Global.getSector().getPersistentData().get("seven_originaldialog")!=null) {
                    InteractionDialogPlugin original = (InteractionDialogPlugin) Global.getSector().getPersistentData().get("seven_originaldialog");
                    dialog.setPlugin(original);
                    options.clearOptions();
                    FireAll.fire(null, dialog, original.getMemoryMap(), "PopulateOptions");
                    Global.getSector().getPersistentData().remove("seven_originaldialog");
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
