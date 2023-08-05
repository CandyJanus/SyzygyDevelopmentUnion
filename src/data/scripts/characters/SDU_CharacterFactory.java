package data.scripts.characters;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Skills;

public class SDU_CharacterFactory {

    public static void createDecimus(MarketAPI market) {

        PersonAPI decimusmaximus = Global.getFactory().createPerson();
        decimusmaximus.setFaction("SDU");
        decimusmaximus.setGender(FullName.Gender.MALE);
        decimusmaximus.setPostId(Ranks.POST_FACTION_LEADER);
        decimusmaximus.setRankId(Ranks.FACTION_LEADER);
        decimusmaximus.getName().setFirst("Decimus");
        decimusmaximus.getName().setLast("Maximus");
        decimusmaximus.setPortraitSprite("graphics/portraits/decimusmaximus.png");
        decimusmaximus.setAICoreId(Commodities.ALPHA_CORE);
        decimusmaximus.setId("SDU_decimus");
        decimusmaximus.getStats().setLevel(20);
        OfficerManagerEvent.addEliteSkills(decimusmaximus, 100, null);
        if (decimusmaximus.getStats().getSkillLevel(Skills.GUNNERY_IMPLANTS) == 0) {
            decimusmaximus.getStats().increaseSkill(Skills.GUNNERY_IMPLANTS);}
        decimusmaximus.setImportance(PersonImportance.VERY_HIGH);
        market.getCommDirectory().addPerson(decimusmaximus);

        if(!Global.getSector().getImportantPeople().containsPerson(decimusmaximus)) Global.getSector().getImportantPeople().addPerson(decimusmaximus);
    }

    public static void createAnodyne(MarketAPI market){
        PersonAPI anodyne = Global.getFactory().createPerson();
        anodyne.setFaction("SDU");
        anodyne.setGender(FullName.Gender.MALE);
        anodyne.setPostId(Ranks.POST_FACTION_LEADER);
        anodyne.setRankId(Ranks.FACTION_LEADER);
        anodyne.getName().setFirst("Anodyne");
        anodyne.getName().setLast("Indefatigable");
        anodyne.setPortraitSprite("graphics/portraits/anodyne.png");
        anodyne.setAICoreId(Commodities.ALPHA_CORE);
        anodyne.setId("SDU_anodyne");
        anodyne.getStats().increaseSkill("hypercognition");
        market.setAdmin(anodyne);
        market.getCommDirectory().addPerson(anodyne);

        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        memory.set("$AnodyneRemembersInsult", false);
        memory.set("$AnodyneRefusingToTalk", false);
//        memory.set("$AnodyneIsAnnoyed", false);

        if(!Global.getSector().getImportantPeople().containsPerson(anodyne)) Global.getSector().getImportantPeople().addPerson(anodyne);

    }

    public static void createAshley(MarketAPI market){
        PersonAPI ashley = Global.getFactory().createPerson();
        ashley.setFaction("SDU");
        ashley.setGender(FullName.Gender.FEMALE);
        ashley.setPostId(Ranks.POST_FACTION_LEADER);
        ashley.setRankId(Ranks.FACTION_LEADER);
        ashley.getName().setFirst("Ashley");
        ashley.getName().setLast("St. Tesserae");
        ashley.setPortraitSprite("graphics/portraits/ashleysttesserae.png");
        ashley.setId("SDU_ashley");
        market.getCommDirectory().addPerson(ashley);

        if(!Global.getSector().getImportantPeople().containsPerson(ashley)) Global.getSector().getImportantPeople().addPerson(ashley);
    }

    public static void createNathan(MarketAPI market){
        PersonAPI nathan = Global.getFactory().createPerson();
        nathan.setFaction("SDU");
        nathan.setGender(FullName.Gender.MALE);
        nathan.setPostId(Ranks.POST_FACTION_LEADER);
        nathan.setRankId(Ranks.FACTION_LEADER);
        nathan.getName().setFirst("Nathan");
        nathan.getName().setLast("Burke");
        nathan.setPortraitSprite("graphics/portraits/nathan.png");
        nathan.setId("SDU_nathan");
        market.getCommDirectory().addPerson(nathan);

        if(!Global.getSector().getImportantPeople().containsPerson(nathan)) Global.getSector().getImportantPeople().addPerson(nathan);
    }

}


