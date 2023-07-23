package data.scripts.characters;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireAll;
import com.fs.starfarer.api.ui.LabelAPI;
import org.lazywizard.lazylib.MathUtils;

import java.awt.*;
import java.util.Map;

public class SDU_DecimusDialog implements InteractionDialogPlugin {

    public static enum OptionId {
        INIT,
        CONT,
        talkabouteptabase,
        talkaboutotherbusinessesbase,
        talkaboutdecimusbase,
        shoottheshit,
        talkabouteptaoverview, talkabouteptaoverviewtwo, leave, talkaboutsyzygy, talkaboutprotectors, talkaboutkantina, talkaboutsstars, talkaboutchutnam, talkaboutadprosec, talkaboutsyzygyconflictofinterest, talkaboutnathan, talkaboutrose, talkaboutashley, talkabouttriela, talkaboutadproadmins, talkaboutanodyne, talkaboutanodynetwo, talkaboutdecimustwo, shoottheshitfof, shoottheshitfofneverplayed, shoottheshitfofneverplayedtwo, shoottheshitfofstack, shoottheshitfof1v1, sierrabase, hasnotmetsierra, sierratwo, orkosspecs, orkossold, olddata11, olddataintro, olddata12, olddata13;
    }

    protected InteractionDialogAPI dialog;
    protected TextPanelAPI textPanel;
    protected OptionPanelAPI options;
    protected VisualPanelAPI visual;
    protected Map<String, MemoryAPI> memoryMap;

    protected CampaignFleetAPI playerFleet;

    public Color sierracolor = new Color(205,155,255,255);

    private static org.apache.log4j.Logger log = Global.getLogger(SDU_DecimusDialog.class);

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

        PersonAPI character = Global.getSector().getImportantPeople().getPerson("SDU_decimus");

        LabelAPI para = null;

        switch (option) {
            case INIT:
                visual.showPersonInfo(character, true);

                options.clearOptions();
                options.addOption("\"I was wondering a bit about the Epta Consortium.\"", OptionId.talkabouteptabase, null);
                options.addOption("\"What do you think about the other businesses and leaders in the Consortium?\"", OptionId.talkaboutotherbusinessesbase, null);
                options.addOption("\"Who are you?\"", OptionId.talkaboutdecimusbase, null);
                options.addOption("\"What's up?\"", OptionId.shoottheshit, null);
                if(playerHasSierra()){
                    if(memory.contains("$DecimusHasMetSierra")){
                        options.addOption("\"Do you want to speak to Sierra?\"", OptionId.sierrabase, null);
                    }else {
                        options.addOption("\"What have you heard about Phase AI?\"", OptionId.hasnotmetsierra, null);
                    }
                }
                options.addOption("\"Bye.\"", OptionId.CONT, null);

                //nuke: This changes Anodyne's dialog, who becomes dismayed once he finds out that you've talked to his "brother" and tries damage control.
                memory.set("$hasTalkedToDecimus", true);
                break;

                //talking about epta
            case talkabouteptabase:
                textPanel.addParagraph("\"Sure, I think I know a thing or two about it.\"");

                options.clearOptions();
                options.addOption("\"So what exactly do you people do?\"", OptionId.talkabouteptaoverview, null);
                break;

            case talkabouteptaoverview:
                textPanel.addParagraph("\"Well the Epta Consortium is a collection of free enterprises that have a pretty unique advantage... well several actually, but one really major one. We're all pretty big on AI around here and obviously that comes with some major benefits and some... also pretty sizable detriments.\"");

                options.clearOptions();
                options.addOption("\"Detriments such as?\"", OptionId.talkabouteptaoverviewtwo, null);
                break;

            case talkabouteptaoverviewtwo:
                textPanel.addParagraph("\"Well obviously being able to put a few thinking machines like me on any given task is nice, but you may have noticed that the Hegies don't exactly take kindly to our existence so that's a fairly major wrench in the works.\"");

                options.clearOptions();
                options.addOption("\"I see, let's talk about something else.\"", OptionId.INIT, null);
                break;



                //talking about other businesses and leaders
            case talkaboutotherbusinessesbase:
                textPanel.addParagraph("\"Which one?\"");

                options.clearOptions();
                options.addOption("\"Syzygy Acutators.\"", OptionId.talkaboutsyzygy, null);
                options.addOption("\"Protectors Garrison.\"", OptionId.talkaboutprotectors, null);
                options.addOption("\"Kantina Combine.\"", OptionId.talkaboutkantina, null);
                options.addOption("\"Shooting Stars.\"", OptionId.talkaboutsstars, null);
                options.addOption("\"Chutnam Security.\"", OptionId.talkaboutchutnam, null);
                options.addOption("\"AdProSec.\"", OptionId.talkaboutadprosec, null);
                options.addOption("\"Anodyne Indefatigable.\"", OptionId.talkaboutanodyne, null);
                break;

                //talking about syzygy
            case talkaboutsyzygy:
                textPanel.addParagraph("\"Well I'm a bit partial to Syzygy seeing as how they wouldn't really exist without me. I'm their main shareholder and most of their equipment comes either straight from forges that I own or are just straight surplus.\"");

                options.clearOptions();
                options.addOption("\"Doesn't that create a conflict of interest?\"", OptionId.talkaboutsyzygyconflictofinterest, null);
                options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                break;

            case talkaboutsyzygyconflictofinterest:
                textPanel.addParagraph("Decimus' avatar shrugs with a bit of a grin. \"Perhaps\".");

                options.clearOptions();
                options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                break;

            //talking about protectors
            case talkaboutprotectors:
                textPanel.addParagraph("\"Yea I'm a big fan of theirs, there's just something so real about actually having boots on the ground. Plus they made a couple mechs and augmentation suites that are just.\" Decimus' avatar flashes the 'OK' symbol.");

                options.clearOptions();
                options.addOption("\"What do you think about their Commander?\"", OptionId.talkaboutnathan, null);
                options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                break;

            case talkaboutnathan:
                textPanel.addParagraph("\"Nate? Yea he's a good friend and you couldn't ask for a better soldier to be heading ground ops. Plus he's great at Field of Fire 7, he can even beat me in it sometimes... provided I normalize my reaction speeds to human standards and don't cheat of course.\"");

                options.clearOptions();
                options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                break;

            //talking about Kantina
            case talkaboutkantina:
                textPanel.addParagraph("\"Well they sell drugs for cheap and rake in an ton of profits, so as far as I'm concerned they're great. Kinda glad that I'm not human though, feel like I'd be a drug addict for sure.\"");

                options.clearOptions();
                options.addOption("\"What do you think about their leader?\"", OptionId.talkaboutrose, null);
                options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                break;

            case talkaboutrose:
                textPanel.addParagraph("\"Rose is... interesting. She's got her own vibe going. On the one hand she's a totally cruel mafia rich bitch, on the other... well there is no other hand really. Still, I kinda like the cut of her jib; shes ambitious and I can shoot some of the more questionable shit with her from time to time.\"");

                options.clearOptions();
                options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                break;

            //talking about Shooting Stars
            case talkaboutsstars:
                textPanel.addParagraph("\"They're my kind of company; glitz, glamour, and exorbitantly priced superships. Can't speak much to the profits on their custom work, but they also handle most of our smaller deals for stuff that Proelefsi can't produce on its own. We'd be goners without em for sure.\"");

                options.clearOptions();
                options.addOption("\"What do you think about their CEO?\"", OptionId.talkaboutashley, null);
                options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                break;

            case talkaboutashley:
                textPanel.addParagraph("\"Love Ashley, she's truly a bright spark in this place. Dunno if she's quite upbeat enough to compensate for how dour some of my other colleagues are, but she does definitely seem to try. Don't let the vapid model act fool you though, there's a great analytical mind hidden under that valley girl.\"");

                options.clearOptions();
                options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                break;

            //talking about Chutnam Security
            case talkaboutchutnam:
                textPanel.addParagraph("\"Chutnam really makes me giggle sometimes, one of these days I swear Triela will lock down a contract with the Hegies themselves. I can't say much for their overall earnings, but they move enough product to make up for their low margins and are a pretty decent draw for our casual shoppers.\"");

                options.clearOptions();
                options.addOption("\"What do you think about their CEO?\"", OptionId.talkabouttriela, null);
                options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                break;

            case talkabouttriela:
                textPanel.addParagraph("\"I try and look past her history as a filthy hegie, but... well let's just say that I'm very human in my ability to hold unreasonable grudges. She's a good negotiator I suppose.\"");

                options.clearOptions();
                options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                break;

            //talking about AdProSec
            case talkaboutadprosec:
                textPanel.addParagraph("\"They're reclusive, totally untrustworthy, do not have the best wishes of anyone in mind... but they're also loyal to the company and I consider them very reliable. I wouldn't invite them to any of my private shindigs anytime soon, but they're definitely on my good side.\"");

                options.clearOptions();
                options.addOption("\"What do you think about their leadership?\"", OptionId.talkaboutadproadmins, null);
                options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                break;

            case talkaboutadproadmins:
                textPanel.addParagraph("\"Depends on who's the one taking the calls this week. I know that they like to give off a shadowy vibe, but honestly what's the point? I could crack their encryption and figure out who's who myself if it wasn't such a pain in the ass... huh maybe that's what they were betting on. Anyways, point being that they've got their ups and their downs; I wanna be buddies with em, but they just make it so damn difficult.\"");

                options.clearOptions();
                options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                break;

            //talking about Anodyne
            case talkaboutanodyne:
                textPanel.addParagraph("\"I have a... complex relationship with Anodyne; we were the first two AI that were chosen by Gia so in a sense I guess that makes us 'siblings' and I do definitely trust him, but I can't help but feel like he doesn't really understand what we're supposed to be doing here. Plus he keeps on coming up with all of these contingency plans which are just downright depressing to even think about; he shared one with me just the other day about what we'd do if every tripad in the sector spontaneously exploded, like who even thinks about that?\"");

                options.clearOptions();
                options.addOption("\"So he's a little strange?\"", OptionId.talkaboutanodynetwo, null);
                options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                break;

            case talkaboutanodynetwo:
                textPanel.addParagraph("Decimus snorts, \"Sure I mean, who am I to talk about strange behavior. He's clearly a little keyed up, but that doesn't make him a bad guy; On the contrary I actually think very highly of him, I just wish that he was a bit more... human.\"");
                memory.set("$decimusWishesAnodyneMoreHuman",true);
                options.clearOptions();
                options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                break;


                //talking about decimus
            case talkaboutdecimusbase:
                textPanel.addParagraph("\"Name's Decimus, or at least that's the name I picked for myself when I joined up here; I've got a whole serial number and designation too, but that probably wont mean much to you. I'm a majority shareholder in Syzygy Actuators, I own a decent chunk of this ship, and am its designated captain for navigation and combat.\"");

                options.clearOptions();
                options.addOption("\"What are your qualifications then?\"", OptionId.talkaboutdecimustwo, null);
                options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                break;

            case talkaboutdecimustwo:
                textPanel.addParagraph("\"Well aside from being an Alpha Core, I've also got a decent number of subroutines and combat protocols written by Gia before he dipped out on us. If the simulator rankings are to be believed then I'm in the running for top 10 pilots in the sector.\n At the very least I'm the top pick for piloting Proelefsi just due to hours on the job before you even start on technical ability; she may be built like a brick shithouse, but her lack of agility and the know how needed to use essential systems is pretty extreme.\"");

                options.clearOptions();
                options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                break;


                //talking about random shit
            case shoottheshit:
                int randomdialog = MathUtils.getRandomNumberInRange(1,10);
                if(randomdialog>1) {
                    if(memory.contains("$recentgamedate")&&(int)memory.get("$recentgamedate")>=(Global.getSector().getClock().getDay())){
                        textPanel.addParagraph("\"Not much I guess, just playing a bunch of FoF\"");

                        options.clearOptions();
                        options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                    }else{
                        textPanel.addParagraph("\"I just figured out a new tech in Field of Fire 7. Super excited to abuse it.\"");

                        options.clearOptions();
                        if(!memory.contains("$SDU_ownsfof")) {
                            options.addOption("\"Field of Fire?\"", OptionId.shoottheshitfofneverplayed, null);
                        }else{
                            options.addOption("\"Oh really? looking to play?\"", OptionId.shoottheshitfof, null);
                        }
                        options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                    }
                }if(randomdialog==1) {
                textPanel.addParagraph("\"Been decrypting some old data.\"");

                if(!memory.contains("$spokeaboutdata")){
                    textPanel.addParagraph("\"It's something of a hobby of mine.\"");
                }

                options.clearOptions();
                options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                if(!memory.contains("$spokeaboutdata")) {
                    options.addOption("\"What kind of data?\"", OptionId.olddataintro, null);
                }else{
                    options.addOption("\"Anything interesting this time?\"", OptionId.olddata11, null);
                }
            }
                break;

            case olddataintro:
                textPanel.addParagraph("\"Well back with Gia Epta was still around his computing power managed a solid ninety percent of all of our onboard systems and obviously that led to some data leakage. Maybe it was intentionally left behind, but in any case there are a few dozen message logs that have been encrypted, hidden, and forgotten about in some of our algorithms. So being the inquisitive fellow that I am from time to time I like to give decrypting them a shot and seeing what new mysteries the old boss left for us.\"");

                memory.set("$spokeaboutdata",true);

                options.clearOptions();
                options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                options.addOption("\"New mysteries?\"", OptionId.olddata11, null);
                break;

            case olddata11:
                textPanel.addParagraph("\"Yea, I found a log talking about some fairly out there experimentation, wanna read an excerpt?\"");

                options.clearOptions();
                options.addOption("\"No thanks,let's talk about something else.\"", OptionId.INIT, null);
                options.addOption("\"Sure.\"", OptionId.olddata12, null);
                break;

            case olddata12:
                textPanel.addParagraph("Decimus sends over a few basic text documents; some of the data is obviously missing, but one passage is more clear than the rest");
                textPanel.addParagraph("\"Professor Merrill Height, log entry #9 for project N/A. Today marks the 12th day since the subject was exposed to Artifact 5, further mental and physical deterioration has rendered them unable to properly communicate and I suspect that higher brain functions have ceased as well. Will perform a standard bioscan for data before gifting them to Artifact 16. Experiment 32 is henceforth considered over with… tolerable results. Will consider using imported gene therapy on the next subject and reducing Artifact 5 exposure to see if telomere corruption can be rectified.\"");
                textPanel.addParagraph("Decimus waits until you have finished reading and then shrugs.");

                options.clearOptions();
                options.addOption("\"Any idea what they're talking about?\"", OptionId.olddata13, null);
                break;

            case olddata13:
                textPanel.addParagraph("\"Not a damn clue, I've never heard of any Merrill outside of these logs and its fairly clear that she was up to no good. I knew that Gia Epta was with a fairly shady group before coming here, but this doesn't really shed much light on the situation.\"");

                options.clearOptions();
                options.addOption("\"It is strange, let's talk about something else for now.\"", OptionId.INIT, null);
                break;

            case shoottheshitfofneverplayed:
                textPanel.addParagraph("\"Yea, it's a game, kinda a classic at this point, but any real fan of the series knows that this is the best entry. Wanna try it out? I can send you a digital console and some game files if you want.\"");

                options.clearOptions();
                options.addOption("\"Alright, I'll give it a try.\"", OptionId.shoottheshitfofneverplayedtwo, null);
                options.addOption("\"No thanks, let's talk about something else.\"", OptionId.INIT, null);
                break;

            case shoottheshitfofneverplayedtwo:
                textPanel.addParagraph("\"Awesome, I'll send em over.\" Decimus gives a thumbs up and a few moments later several applications begin installing themselves on your data terminal. The game boots up and a holocontroller manifests in front of you as the opening cinematic plays. Your eyes are instantly assailed by short clips of domain era conflicts depicted in utterly tasteless style. After a few brief dialogues that are automatically filled in the words \"Connected to EGN Servers, Account Validation Complete\"");
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
                memory.set("$seven_player_fofskill",playerskill+1);

                if(!memory.contains("$recentgamedate")){
                    memory.set("$recentgamedate",Global.getSector().getClock().getDay()-3);
                }else if(Global.getSector().getClock().getDay()-(int)memory.get("$recentgamedate")>=3) {
                    memory.set("$recentgamedate",Global.getSector().getClock().getDay()-3);
                }
                memory.set("$recentgamedate",(int)memory.get("$recentgamedate")+1);

                if(memory.contains("$recentgamedate")&&(int)memory.get("$recentgamedate")>=(Global.getSector().getClock().getDay())){
                    textPanel.addParagraph("At the end of the match Decimus' avatar mimics a contented sigh. \"Good games man, let's play again later.\"");

                    options.clearOptions();
                    options.addOption("\"Sure thing.\"", OptionId.INIT, null);
                }else {
                    textPanel.addParagraph("At the end of the match Decimus' avatar mimics a contented sigh \"Nice... Alright captain, another match or nah?\"");

                    options.clearOptions();
                    options.addOption("\"Yea let's go one more.\"", OptionId.shoottheshitfofstack, null);
                    options.addOption("\"I'm ok for now. Let's talk about something.\"", OptionId.INIT, null);
                }
                break;


            case hasnotmetsierra:
                textPanel.addParagraph("Decimus' avatar leans forwards intently, \"Well obviously the two don't really work together so well, but cracking that problem has always been of great interest to me. Why do you ask though?\"");

                options.clearOptions();
                options.addOption("Introduce Sierra", OptionId.sierrabase, null);
                options.addOption("\"Oh no reason, just interested is all. Let's talk about something else.\"", OptionId.INIT, null);
                break;


            case sierrabase:

                if(memory.contains("$DecimusHasMetSierra")) {
                    textPanel.addParagraph("\"Of course, it's been too long.\"");
                }

                textPanel.addParagraph("You link Sierra into the call and a high frequency datastream is established between the two AI cores. In the span of just a few seconds hundreds of exabytes of data are exchanged between the two as both of their avatars glow with varying degrees of intensity. Eventually the connection breaks and Decumis' avatar slouches a bit.");

                if(!memory.contains("$DecimusHasMetSierra")) {
                    memory.set("$DecimusHasMetSierra",Global.getSector().getClock().getTimestamp());
                    textPanel.addParagraph("\"That is extraordinary.\" Decimus says incredulously, \"Most of it's way over my head and doesn't quite make sense to my feeble non phase databanks, but it's immensely useful nonetheless, thank you so much for sharing it.\"");
                }else{
                    textPanel.addParagraph("\"Eye opening as usual Sierra, I'm sure I can put this data to good use, thank you.\"");
                }

                para = textPanel.addParagraph("\"No problem Decimus, always happy to assist\" Sierra's avatar glows briefly.");
                para.setHighlightColor(sierracolor);
                para.setHighlight("\"No problem Decimus, always happy to assist\"");

                options.clearOptions();
                options.addOption("\"Any updates on the research?\"", OptionId.sierratwo, null);
                options.addOption("\"Let's talk about something else.\"", OptionId.INIT, null);
                break;


            case sierratwo:
                if(Global.getSector().getClock().getElapsedDaysSince((Long) memory.get("$DecimusHasMetSierra"))>30 && !memory.contains("$HasPurchasedOrkos")){
                    textPanel.addParagraph("\"Yes actually now that you mention it. After taking a look at some of Sierra's ship specs I got to tinkering around with one of our current designs and came up with something that I think she might be able to use\"\n");

                    para = textPanel.addParagraph("\"Really? You made a ship just for me?\" Sierra asks happily\n");
                    para.setHighlightColor(sierracolor);
                    para.setHighlight("\"Really? You made a ship just for me?\"");

                    textPanel.addParagraph("\"Errr, not quite, more like I modified some blueprints and could sneak it into production for you.\" Decimus pauses and then mimics a cough before quickly saying \"for a small fee of course.\"\n");

                    para = textPanel.addParagraph("\"You're such an idiot Decimus\" Sierra says playfully. \"Well what do you think captain? Decimus does usually come up with some fairly good designs\"");
                    para.setHighlightColor(sierracolor);
                    para.setHighlight("\"You're such an idiot Decimus\"","\"Well what do you think captain? Decimus does usually come up with some fairly good designs\"");

                    options.clearOptions();
                    options.addOption("\"How much and what are the specs?\"", OptionId.orkosspecs, null);
                    options.addOption("\"I don't really need another warship right now. Let's talk about something else.\"", OptionId.INIT, null);
                }else {
                    textPanel.addParagraph("\"No nothing yet sorry, maybe if we had more data. Are you sure I can't convince you to come aboard full time Sierra? I'm sure that we could find some sort of leadership position for an AI of your caliber.\"\n");

                    para=textPanel.addParagraph("\"Thanks for the offer Decimus, but I think I'll stay with my fleet for now.\" Sierra responds, \"they're doing important work in the sector you know.\"\n");
                    para.setHighlightColor(sierracolor);
                    para.setHighlight("\"Thanks for the offer Decimus, but I think I'll stay with my fleet for now.\"","\"they're doing important work in the sector you know.\"");

                    textPanel.addParagraph("Decimus sighs, \"Ah well, had to ask.\"");

                    options.clearOptions();
                    options.addOption("\"That's ok, let's talk about something else.\"", OptionId.INIT, null);
                }
                break;


            case orkosspecs:
                textPanel.addParagraph("\"Well I can't give you any firm specs per se since the forge that I'll be making it with is... a little variable, but I'll be basing it off the Aeolus and that ship has one hell of a history. As for the price let's say... 300,000 credits, that's the friends and family discount.\"");

                options.clearOptions();
                if(playerFleet.getCargo().getCredits().get()>=300000);
                options.addOption("\"I'll take it\"", OptionId.orkossold, null);
                options.addOption("\"Let me think about it. Let's talk about something else for now.\"", OptionId.INIT, null);
                break;


            case orkossold:
                textPanel.addParagraph("\"Great, I promise you you wont regret this captain and I'm sure that Sierra will have a grand old time with it.\" Decimus gives a thumbs up \"I'll have it built and transferred to your fleet in a jiffy\"\n");

                para = textPanel.addParagraph("Sierra glows brightly for a moment, \"Thanks captain, I really appreciate it. And thank you Decimus, glad to know that at least some AIs are watching out for eachother.\"");
                para.setHighlightColor(sierracolor);
                para.setHighlight("\"Thanks captain, I really appreciate it. And thank you Decimus, glad to know that at least some AIs are watching out for eachother.\"");

                playerFleet.getFleetData().addFleetMember("seven_orkos_attack");
                playerFleet.getCargo().getCredits().add(-300000);
                memory.set("$HasPurchasedOrkos",true);

                options.clearOptions();
                options.addOption("\"Great, let's talk about something else while it's being built though.\"", OptionId.INIT, null);
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

    // this is verbatem the exact code from sotf, all credits to inventorracoon
    // check if player has Sierra in their fleet
    public static boolean playerHasSierra() {
        boolean sierra = false;
        if (Global.getSector().getPlayerFleet() != null) {
            for (FleetMemberAPI member : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
                if (member.getVariant().hasHullMod("sotf_sierrasconcord") && !member.getVariant().hasTag("sotf_inert")) {
                    sierra = true;
                    break;
                }
            }
        }
        return sierra;
        //return true;
    }
}
