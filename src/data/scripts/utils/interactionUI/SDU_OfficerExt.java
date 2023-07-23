package data.scripts.utils.interactionUI;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class SDU_OfficerExt {

    public PersonAPI person;

    public String id;

    public ArrayList<String> tags;

    public String faction;
    public String location;
    public String caste;
    public String alignment;
    public String personality;
    public String greeting;
    public String cabin;
    public String homedescription;
    public String faith;
    public String shortgoal;
    public String longgoal;
    public String stayreason;
    public String leavereason;
    public String upbringing;
    public String parents;
    public String occupation;
    public String romance;

    public ArrayList<String> hobbies;
    public ArrayList<String> skills;
    public ArrayList<String> randomtopics;
    public ArrayList<String> likes;
    public ArrayList<String> dislikes;
    public boolean standardformat;


    public SDU_OfficerExt create(PersonAPI person, String id, boolean standardformat, String faction, String location, String caste, String alignment, String personality, String greeting, String cabin, String homedescription, String faith, String shortgoal, String longgoal, String stayreason, String leavereason, String upbringing, String parents, String occupation, ArrayList<String> hobbies, ArrayList<String> skills, ArrayList<String> randomtopics, ArrayList<String> likes, ArrayList<String> dislikes, String romance) {
    //public seven_OfficerExt create(PersonAPI person, String id, Pairs<String,Object>){
        this.person=person;
        this.id=id;

        if(faction!=null){
            this.faction=faction;
        }else{
            this.faction=randtag("faction", this);
        }

        if(location!=null){
            this.location=location;
        }else{
            this.location=randtag("location", this);
        }

        if(caste!=null){
            this.caste=caste;
        }else{
            this.caste=randtag("caste", this);
        }

        if(alignment!=null){
            this.alignment=alignment;
        }else{
            this.alignment=randtag("alignment", this);
        }

        if(personality!=null){
            this.personality=personality;
        }else{
            this.personality=randtag("personality", this);
        }

        if(greeting!=null){
            this.greeting=greeting;
        }else{
            this.greeting=randtag("greeting", this);
        }

        if(cabin!=null){
            this.cabin=cabin;
        }else{
            this.cabin=randtag("cabin", this);
        }

        if(homedescription!=null){
            this.homedescription=homedescription;
        }else{
            this.homedescription=randtag("homedescription", this);
        }

        if(faith!=null){
            this.faith=faith;
        }else{
            this.faith=randtag("faith", this);
        }

        if(shortgoal!=null){
            this.shortgoal=shortgoal;
        }else{
            this.shortgoal=randtag("shortgoal", this);
        }

        if(longgoal!=null){
            this.longgoal=longgoal;
        }else{
            this.longgoal=randtag("longgoal", this);
        }

        if(stayreason!=null){
            this.stayreason=stayreason;
        }else{
            this.stayreason=randtag("stayreason", this);
        }

        if(leavereason!=null){
            this.leavereason=leavereason;
        }else{
            this.leavereason=randtag("leavereason", this);
        }

        if(upbringing!=null){
            this.upbringing=upbringing;
        }else{
            this.upbringing=randtag("upbringing", this);
        }

        if(parents!=null){
            this.parents=parents;
        }else{
            this.parents=randtag("parents", this);
        }

        if(occupation!=null){
            this.occupation=occupation;
        }else{
            this.occupation=randtag("occupation", this);
        }

        if(hobbies!=null){
            this.hobbies=hobbies;
        }else{
            this.hobbies=randtags("hobbies",1, this);
        }

        if(skills!=null){
            this.skills=skills;
        }else{
            this.skills=randtags("skills",1, this);
        }

        if(randomtopics!=null){
            this.randomtopics=randomtopics;
        }else{
            this.randomtopics=randtags("randomtopics",1, this);
        }

        //cabin description (faction tag) homeworld description tag (location tag) faith tag (location tag) occupation/parent occupation tags (class+location tags) and the institution tag (location tag) and both types of goal tag (alignment) rely on other tags

        SDU_OfficerExt_List.getOEList().add(this);

        return this;
    }

    public String drandtag(String category){
        WeightedRandomPicker pool = new WeightedRandomPicker();
        try {
            JSONObject file = Global.getSettings().loadJSON("OfficerDialogue.json", true);
            for (Iterator it = file.keys(); it.hasNext(); ) {
                String s = (String) it.next();
                String cat = file.getJSONObject(s).getString("category");
                int weight = file.getJSONObject(s).getInt("weight");
                String tag = file.getJSONObject(s).getString("tag");
                if(cat.equals(category)){
                    pool.add(tag,weight);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        if(!pool.isEmpty()){
            return (String)pool.pick();
        }else{
            return null;
        }
    }


    public static String randtag(String category, SDU_OfficerExt officer){
        WeightedRandomPicker pool = new WeightedRandomPicker();
        try {
            JSONObject file = file = Global.getSettings().loadJSON("OfficerDialogue.json", true);
            for (Iterator it = file.keys(); it.hasNext(); ) {
                String s = (String) it.next();
                String cat = file.getJSONObject(s).getString("category");
                int weight = file.getJSONObject(s).getInt("weight");
                String tag = file.getJSONObject(s).getString("tag");

                boolean add = true;
                //if it has dependencies then check to make sure it has any of them
                //first check fromtagsany
                boolean anyadd = false;
                if(file.getJSONObject(s).has("fromtagsany")) {
                    if (file.getJSONObject(s).getJSONArray("fromtagsany") != null) {
                        ArrayList<String> fromtags = toStringArray(file.getJSONObject(s).getJSONArray("fromtagsany"));

                        if (!fromtags.isEmpty()) {
                            for (String r : fromtags) {
                                if (hastag(r, officer) || r.equals("universal")) {
                                    anyadd = true;
                                }
                            }
                        } else {
                            anyadd = true;
                        }
                    }else {
                        anyadd = true;
                    }
                }else {
                    anyadd = true;
                }

                //then check fromtagsall
                boolean alladd = true;
                if(file.getJSONObject(s).has("fromtagsall")) {
                    if (file.getJSONObject(s).getJSONArray("fromtagsall") != null) {
                        ArrayList<String> fromtags = toStringArray(file.getJSONObject(s).getJSONArray("fromtagsall"));

                        if (!fromtags.isEmpty()) {
                            for (String r : fromtags) {
                                if (!hastag(r, officer) && !r.equals("universal")) {
                                    alladd = false;
                                }
                            }
                        }
                    }
                }

                //then check fromtagsnone
                boolean noneadd = true;
                if(file.getJSONObject(s).has("fromtagsnone")) {
                    if (file.getJSONObject(s).getJSONArray("fromtagsnone") != null) {
                        ArrayList<String> fromtags = toStringArray(file.getJSONObject(s).getJSONArray("fromtagsnone"));

                        if (!fromtags.isEmpty()) {
                            for (String r : fromtags) {
                                if (hastag(r, officer) || r.equals("universal")) {
                                    noneadd = false;
                                }
                            }
                        }
                    }
                }

                if(anyadd&&alladd&&noneadd){
                    add = true;
                }else{
                    add = false;
                }

                if(cat.equals(category)&&add){
                    pool.add(tag,weight);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(!pool.isEmpty()){
            return (String)pool.pick();
        }else{
            return null;
        }
    }

    public static boolean hastag(String tag, SDU_OfficerExt officer){
        if(officer.hobbies==null){
            officer.hobbies = new ArrayList<>();
        }
        if(officer.skills==null){
            officer.skills = new ArrayList<>();
        }
        if(officer.randomtopics==null){
            officer.randomtopics = new ArrayList<>();
        }
        if(tag.equals(officer.faction)||
                tag.equals(officer.location)||
                tag.equals(officer.caste)||
                tag.equals(officer.alignment)||
                tag.equals(officer.personality)||
                tag.equals(officer.greeting)||
                tag.equals(officer.cabin)||
                tag.equals(officer.homedescription)||
                tag.equals(officer.faith)||
                tag.equals(officer.shortgoal)||
                tag.equals(officer.longgoal)||
                tag.equals(officer.stayreason)||
                tag.equals(officer.leavereason)||
                tag.equals(officer.upbringing)||
                tag.equals(officer.parents)||
                tag.equals(officer.occupation)||
                officer.hobbies.contains(tag)||
                officer.randomtopics.contains(tag)||
                officer.skills.contains(tag)
        ){
            return true;
        }
        return false;
    }

    public static ArrayList<String> randtags(String category, int tagcount, SDU_OfficerExt officer){
        ArrayList<String> tags = new ArrayList<>();

        for(int i=0; i< tagcount; i++){
                    String ptag = randtag(category, officer);
                    if(!tags.isEmpty()) {
                        while (tags.contains(ptag)) {
                            ptag = randtag(category, officer);
                        }
                    }
                    tags.add(ptag);
                }

        if(tags.isEmpty()||tags.get(0)==null){
            return null;
        }else{
            return tags;
        }
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

    public static ArrayList<ArrayList<String>> toString2dArray(JSONArray array) throws JSONException {
        if(array==null)
            return new ArrayList<ArrayList<String>>();

        ArrayList<ArrayList<String>> arr=new ArrayList<ArrayList<String>>();
        for(int i=0; i< array.length(); i++) {
            JSONArray sarr = array.getJSONArray(i);
            ArrayList<String> tarr = new ArrayList<String>();
            for(int j=0; j< sarr.length(); j++) {
                tarr.add(sarr.optString(j));
            }
            arr.add(tarr);
        }
        return arr;
    }



}

