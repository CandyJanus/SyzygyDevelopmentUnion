package data.scripts.utils.interactionUI;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class SDU_CharacterDialog implements InteractionDialogPlugin {

    public static enum OptionId {
        INIT,
        CONT,
        INTRO,
        PAST,
        SKILLS,
        GOALS,
        MISC,
        BACK;
    }

    public static Logger log = Global.getLogger(SDU_CharacterDialog.class);

    protected InteractionDialogAPI dialog;
    protected TextPanelAPI textPanel;
    protected OptionPanelAPI options;
    protected VisualPanelAPI visual;

    protected CampaignFleetAPI playerFleet;

    protected PersonAPI officer;
    protected SDU_OfficerExt officerext;

    public void init(InteractionDialogAPI dialog) {
        this.dialog = dialog;
        textPanel = dialog.getTextPanel();
        options = dialog.getOptionPanel();
        visual = dialog.getVisualPanel();

        playerFleet = Global.getSector().getPlayerFleet();

        optionSelected(null, OptionId.INTRO);
    }

    public void setofficer(PersonAPI officer){
        this.officer = officer;
        this.officerext = SDU_OfficerExt_List.getExtendedOfficer(officer);
    }

    public Map<String, MemoryAPI> getMemoryMap() {
        return null;
    }

    public void backFromEngagement(EngagementResultAPI result) {

    }

    public void optionSelected(String text, Object optionData) {
        if (optionData == null) return;
        String dtext = null;
        ArrayList<String> optionidlist = null;
        ArrayList<String> optiontexts = null;
        JSONObject file = OfficerDialogLoader.getJson();

        //OptionId option = (OptionId) optionData;


        if (text != null) {
            textPanel.addParagraph(text, Global.getSettings().getColor("buttonText"));
        }

        Color sc = Global.getSector().getFaction("sevencorp").getBaseUIColor();

        if(optionData==null||optionData.equals(null)) {
            textPanel.addParagraph("This officer does not have the needed tag to generate this dialogue.");
            options.clearOptions();
            options.addOption("\"Let's talk about something else\"", OptionId.BACK, null);
        }else if(optionData instanceof String){
            dtext = null;
            optionidlist = null;
            optiontexts = null;

            if(TagLoader.getJson((String) optionData).has("optionids")) {
                if (TagLoader.gettoptionstuffs((String) optionData,"optionids") != null) {
                    optionidlist = toStringArray(TagLoader.gettoptionstuffs((String) optionData,"optionids"));
                }
            }

            if(TagLoader.getJson((String) optionData).has("optiontexts")) {
                if (TagLoader.gettoptionstuffs((String) optionData,"optiontexts") != null) {
                    optiontexts = toStringArray(TagLoader.gettoptionstuffs((String) optionData,"optiontexts"));
                }
            }

            if(TagLoader.getJson((String) optionData).has("functions")) {
                if (TagLoader.gettoptionstuffs((String) optionData,"functions") != null) {
                    doFunctions(TagLoader.gettoptionstuffs((String) optionData,"functions"));
                }
            }

            if(TagLoader.getJson((String) optionData).has("text")) {
                dtext = TagLoader.gettext((String) optionData);

                textPanel.addParagraph(refinestring(dtext));
            }

            options.clearOptions();
            if(optionidlist!=null) {
                for (int j = 0; j < optionidlist.size(); j++) {

                    if(TagLoader.getJson((String) optionData).has(optiontexts.get(j)+" requirements")){
                        if(checkFunctions(TagLoader.gettoptionstuffs((String) optionData,optiontexts.get(j)+" requirements"),officer)){
                            options.addOption(optiontexts.get(j), optionidlist.get(j), null);
                        }
                    }else{
                        options.addOption(optiontexts.get(j), optionidlist.get(j), optionidlist.get(j));
                    }
                }
            }

            options.addOption("\"Let's talk about something else\"", OptionId.BACK, null);



        }else if(optionData instanceof ArrayList) {

            options.clearOptions();

            for(String i:((ArrayList<String>)optionData)) {
                if(TagLoader.getJson(i).has("optionids")) {
                    if (TagLoader.gettoptionstuffs(i,"optionids") != null) {
                        optionidlist = toStringArray(TagLoader.gettoptionstuffs(i,"optionids"));
                    }
                }

                if(TagLoader.getJson(i).has("optiontexts")) {
                    if (TagLoader.gettoptionstuffs(i,"optiontexts") != null) {
                        optiontexts = toStringArray(TagLoader.gettoptionstuffs(i,"optiontexts"));
                    }
                }

                if(TagLoader.getJson(i).has("functions")) {
                    if (TagLoader.gettoptionstuffs(i,"functions") != null) {
                        doFunctions(TagLoader.gettoptionstuffs(i,"functions"));
                    }
                }

                if(TagLoader.getJson(i).has("text")) {
                    dtext = TagLoader.gettext(i);

                    textPanel.addParagraph(refinestring(dtext));
                }

                if(optionidlist!=null) {
                    for (int j = 0; j < optionidlist.size(); j++) {
                        if(TagLoader.getJson(i).has(optiontexts.get(j)+" requirements")){
                            if(checkFunctions(TagLoader.gettoptionstuffs(i,optiontexts.get(j)+" requirements"),officer)){
                                options.addOption(optiontexts.get(j), optionidlist.get(j), null);
                            }
                        }else{
                            options.addOption(optiontexts.get(j), optionidlist.get(j), null);
                        }
                    }
                }
            }
            options.addOption("\"Let's talk about something else\"", OptionId.BACK, null);

        }else{
            if(officerext.cabin==null){
                officerext.cabin= SDU_OfficerExt.randtag("cabin",officerext);
            }
            if(officerext.greeting==null){
                officerext.greeting= SDU_OfficerExt.randtag("greeting",officerext);
            }
            if(officerext.homedescription==null){
                officerext.homedescription= SDU_OfficerExt.randtag("homedescription",officerext);
            }
            if(officerext.parents==null){
                officerext.parents= SDU_OfficerExt.randtag("parents",officerext);
            }
            if(officerext.upbringing==null){
                officerext.upbringing= SDU_OfficerExt.randtag("upbringing",officerext);
            }
            if(officerext.leavereason==null){
                officerext.leavereason= SDU_OfficerExt.randtag("leavereason",officerext);
            }
            if(officerext.skills==null){
                officerext.skills= SDU_OfficerExt.randtags("skills",1,officerext);
            }
            if(officerext.occupation==null){
                officerext.occupation= SDU_OfficerExt.randtag("occupation",officerext);
            }
            if(officerext.shortgoal==null){
                officerext.shortgoal= SDU_OfficerExt.randtag("shortgoal",officerext);
            }
            if(officerext.longgoal==null){
                officerext.longgoal= SDU_OfficerExt.randtag("longgoal",officerext);
            }
            if(officerext.stayreason==null){
                officerext.stayreason= SDU_OfficerExt.randtag("stayreason",officerext);
            }
            if(officerext.hobbies==null){
                officerext.hobbies= SDU_OfficerExt.randtags("hobbies",1,officerext);
            }
            if(officerext.likes==null){
                officerext.likes= SDU_OfficerExt.randtags("likes",1,officerext);
            }
            if(officerext.dislikes==null){
                officerext.dislikes= SDU_OfficerExt.randtags("dislikes",1,officerext);
            }
            if(officerext.romance==null){
                officerext.romance= SDU_OfficerExt.randtag("romance",officerext);
            }
            if(officerext.randomtopics==null){
                officerext.randomtopics= SDU_OfficerExt.randtags("randomtopics",1,officerext);
            }

            switch ((OptionId) optionData) {
                case CONT:
                    dialog.dismiss();
                    Global.getSector().setPaused(true);
                    break;

                case INTRO:
                    visual.showPersonInfo(officer, true, true);
                    textPanel.addParagraph(refinestring("You enter Officerfirstname’s quarters on the theirshipname after sending $himorher a brief communique indicating you wish to visit $himorher."));
                    try {
                        textPanel.addParagraph(refinestring(file.getJSONObject(officerext.cabin).getString("text")));
                        textPanel.addParagraph(refinestring(file.getJSONObject(officerext.greeting).getString("text")));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    options.clearOptions();
                    options.addOption("\"Let's talk about your past.\"", OptionId.PAST, null);
                    options.addOption("\"Let's talk about your skillset.\"", OptionId.SKILLS, null);
                    options.addOption("\"Let's talk about your aspirations.\"", OptionId.GOALS, null);
                    options.addOption("\"Let's talk about something else.\"", OptionId.MISC, null);
                    //options.addOption("\"I've been told that...\"", OptionId.CROSS, null);
                    options.addOption("Leave", OptionId.CONT, null);
                    break;

                case BACK:
                    textPanel.addParagraph("\"Sure\"");

                    options.clearOptions();
                    options.addOption("\"Let's talk about your past.\"", OptionId.PAST, null);
                    options.addOption("\"Let's talk about your skillset.\"", OptionId.SKILLS, null);
                    options.addOption("\"Let's talk about your aspirations.\"", OptionId.GOALS, null);
                    options.addOption("\"Let's talk about something else.\"", OptionId.MISC, null);
                    //options.addOption("\"I've been told that...\"", OptionId.CROSS, null);
                    options.addOption("Leave", OptionId.CONT, null);
                    break;

                case PAST:
                    textPanel.addParagraph("\"Sure, what about it?\"");

                    options.clearOptions();
                    options.addOption("\"Tell me about your home.\"", officerext.homedescription, null);
                    options.addOption("\"What were your parents like?\"", officerext.parents, null);
                    options.addOption("\"What was your childhood like?\"", officerext.upbringing, null);
                    options.addOption("\"Why did you leave?\"", officerext.leavereason, null);
                    options.addOption("\"Let's talk about something else\"", OptionId.BACK, null);
                    break;

                case SKILLS:
                    textPanel.addParagraph("\"What do you want to know about them?\"");

                    options.clearOptions();
                    options.addOption("\"Do you have any skills besides being an officer?\"", officerext.skills, null);
                    options.addOption("\"Did you have a profession before this?\"", officerext.occupation, null);
                    options.addOption("\"Let's talk about something else\"", OptionId.BACK, null);
                    break;

                case GOALS:
                    textPanel.addParagraph("\"Okay.\"");

                    options.clearOptions();
                    options.addOption("\"What are your short term goals?\"", officerext.shortgoal, null);
                    options.addOption("\"What are your long term goals?\"", officerext.longgoal, null);
                    options.addOption("\"Why did you decide to join this fleet? What's keeping you here?\"", officerext.stayreason, null);
                    options.addOption("\"Let's talk about something else\"", OptionId.BACK, null);
                    break;

                case MISC:
                    textPanel.addParagraph("\"Like what?\"");

                    options.clearOptions();
                    options.addOption("\"Do you have any hobbies?\"", officerext.hobbies, null);
                    options.addOption("\"What are some things that you like?\"", officerext.likes, null);
                    options.addOption("\"What are some things that you dislike?\"", officerext.dislikes, null);
                    options.addOption("\"Are you seeing anyone right now?\"", officerext.romance, null);
                    options.addOption("\"Why dont you pick a topic?\"", officerext.randomtopics, null);
                    options.addOption("\"Let's talk about something else\"", OptionId.BACK, null);
                    break;


            }
        }
    }

    public String refinestring(String string){
        if(officer.isMale()){
            string = string.replace("$Hisorher","His");
        }else{
            string = string.replace("$Hisorher","Her");
        }

        if(officer.isMale()){
            string = string.replace("$himorher","him");
        }else{
            string = string.replace("$himorher","her");
        }

        string = string.replace("Officerfirstname",officer.getName().getFirst());

        String shipname = playerFleet.getFlagship().getShipName();
        for(FleetMemberAPI s:playerFleet.getFleetData().getMembersListCopy()){
            if(s.getCaptain().equals(officer)){
                shipname = s.getShipName();
            }
        }
        string = string.replace("theirshipname",shipname);
        return string;
    }

    public static ArrayList<String> toStringArray(JSONArray array) {
        if(array==null)
            return new ArrayList<String>();

        ArrayList<String> arr=new ArrayList<String>();
        for(int i=0; i< array.length(); i++) {
            arr.add(array.optString(i));
        }
        return arr;
    }


    public static void doFunctions(JSONArray array) {

        ArrayList<Object> arr=new ArrayList<Object>();
        for(int i=0; i< array.length(); i++) {
            arr.add(array.opt(i));
        }

        for(int i=0; i< arr.size(); i++) {
            /*
            ScriptEvaluator eval = new ScriptEvaluator();
                        eval.setReturnType(void.class);
                        eval.setParentClassLoader(Global.getSettings().getScriptClassLoader());
                        eval.setReturnType(Void.class);
                        eval.setMethodName("dostuff()");
            Class fn = Global.getSettings().getScriptClassLoader().loadClass(arr.get(i).toString().split("\\(")[0]);
            */
            //dostuff();

            /*
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            try {
                engine.eval(arr.get(i).toString());
            } catch (ScriptException e) {
                throw new RuntimeException(e);
            }
             */

            String[] fn = arr.get(i).toString().split(" ");

            switch (fn[0]){
                case "addStuff":
                    SDU_CDUtils.addStuff(fn[1], Integer.parseInt(fn[2]));
            }
        }


    }

    public static boolean checkFunctions(JSONArray array, PersonAPI officer) {

        ArrayList<Object> arr=new ArrayList<Object>();
        for(int i=0; i< array.length(); i++) {
            arr.add(array.opt(i));
        }

        boolean works = true;

        for(int i=0; i< arr.size(); i++) {

            //String[] fn = arr.get(i).toString().split("\\(",2);
            //            String func = fn[0];
            //
            //            String pargs = fn[1];
            //            pargs = pargs.replace(" ","");
            //            pargs = pargs.replace("\"","");
            //            pargs = pargs.replace(")","");
            //            String[] args = pargs.split(",");

            String[] fn = arr.get(i).toString().split(" ");

            switch (fn[0]){

                case "isCommed":
                    if(!SDU_CDUtils.isCommed()){
                        works=false;
                    }
                    break;
                case "isComFac":
                    if(!SDU_CDUtils.isComFac(fn[1])){
                        works=false;
                    }
                    break;
                case "FacRepAbove":
                    if(!SDU_CDUtils.FacRepAbove(fn[1], Integer.parseInt(fn[2]))){
                        works=false;
                    }
                    break;
                case "PerRepAbove":
                    if(!SDU_CDUtils.PerRepAbove(fn[1], Integer.parseInt(fn[2]),officer)){
                        works=false;
                    }
                    break;
                case "hasStuff":
                    if(!SDU_CDUtils.hasStuff(fn[1], Integer.parseInt(fn[2]))){
                        works=false;
                    }
                    break;

            }
        }
        return works;
    }


    public void optionMousedOver(String optionText, Object optionData) {

    }

    public void advance(float amount) {

    }

    public Object getContext() {
        return null;
    }

    public static class OfficerDialogLoader {

        private static JSONObject file;

        public static JSONObject getJson()
        {
            if (file == null)
            {
                try { file = Global.getSettings().loadJSON("OfficerDialogue.json", true);  }
                catch (IOException | JSONException ex) {
                    throw new RuntimeException(ex);
                }
            }
            return file;
        }
    }

    public static class TagLoader {

        private static JSONObject file;

        public static JSONObject getJson(String optionData)
        {
            try { file = OfficerDialogLoader.getJson().getJSONObject(optionData);  }
            catch (JSONException ex)  {
                throw new RuntimeException(ex);
            }
            return file;
        }

        public static JSONArray gettoptionstuffs(String optionData, String optionStuffs)
        {
            try {
                return TagLoader.getJson(optionData).getJSONArray(optionStuffs);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        public static String gettext(String optionData)
        {
            try {
                return TagLoader.getJson(optionData).getString("text");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        public static Object getobj(String optionData, String optionStuffs)
        {
            try {
                return TagLoader.getJson(optionData).get(optionStuffs);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
