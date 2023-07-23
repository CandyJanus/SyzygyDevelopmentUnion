package data.scripts.world.systems.SDU;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.mission.FleetSide;
import org.lwjgl.util.vector.Vector2f;

import java.util.List;

/**
 * Simple script that deploys Anargaia into battle should it somehow end up in reserves
 * @author Nicke535
 */
public class SDU_fundamentdeploy extends BaseEveryFrameCombatPlugin {
    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        CombatEngineAPI engine = Global.getCombatEngine();

        //Check the reserves of both fleets
        for (FleetMemberAPI member : engine.getFleetManager(FleetSide.PLAYER).getReservesCopy()) {
            //If the reserve is Fundament, deploy it
            if (member.getHullId().contains("SDU_fundament")) {
                ShipAPI anargaia = engine.getFleetManager(FleetSide.PLAYER).spawnFleetMember(member,
                        new Vector2f(0f, engine.getFleetManager(FleetSide.PLAYER).getDeploymentYOffset()/2f - engine.getMapHeight()/3f),
                        90f, 0f);
                //On the player side, anargaia is always an ally
                anargaia.setAlly(true);
            }
        }
        for (FleetMemberAPI member : engine.getFleetManager(FleetSide.ENEMY).getReservesCopy()) {
            //If the reserve is Fundament, deploy it
            if (member.getHullId().contains("SDU_fundament")) {
                engine.getFleetManager(FleetSide.ENEMY).spawnFleetMember(member,
                        new Vector2f(0f, engine.getFleetManager(FleetSide.ENEMY).getDeploymentYOffset()/2f + engine.getMapHeight()/3f),
                        -90f, 0f);
            }
        }
    }
}