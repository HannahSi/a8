package student;

/** Time spent: 
 * 4/28: 1 hr
 * 4/28-4/29 separately 1 hr
 * 4/29 1 hr */

import controllers.Spaceship;
import models.Edge;
import models.Node;
import models.NodeStatus;

import controllers.SearchPhase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import controllers.RescuePhase;

/** An instance implements the methods needed to complete the mission. */
public class MySpaceship implements Spaceship {
	ArrayList<Integer> visited = new ArrayList<Integer>();

	/** The spaceship is on the location given by parameter state.
	 * Move the spaceship to Planet X and then return (with the spaceship is on
	 * Planet X). This completes the first phase of the mission.
	 * 
	 * If the spaceship continues to move after reaching Planet X, rather than
	 * returning, it will not count. If you return from this procedure while
	 * not on Planet X, it will count as a failure.
	 *
	 * There is no limit to how many steps you can take, but your score is
	 * directly related to how long it takes you to find Planet X.
	 *
	 * At every step, you know only the current planet's ID, the IDs of
	 * neighboring planets, and the strength of the signal from Planet X at
	 * each planet.
	 *
	 * In this rescuePhase,
	 * (1) In order to get information about the current state, use functions
	 * currentID(), neighbors(), and signal().
	 *
	 * (2) Use method onPlanetX() to know if you are on Planet X.
	 *
	 * (3) Use method moveTo(int id) to move to a neighboring planet with the
	 * given ID. Doing this will change state to reflect your new position.
	 */
	@Override
	public void search(SearchPhase state) {
		// TODO: Find the missing spaceship
		int current = state.currentID();
		visited.add(current);
		
		List<NodeStatus> neighbors = Arrays.asList(state.neighbors());
		Collections.sort(neighbors);
		
		for (int i = neighbors.size()-1; i >= 0; i--) {
			if (!visited.contains(neighbors.get(i).id())) {
				state.moveTo(neighbors.get(i).id());
				if (state.onPlanetX()) return;
				
				search(state);
				
				if (state.onPlanetX()) return;
				state.moveTo(current);
			}
		}
	}

	public void ourSearch(SearchPhase s) {
		int current = s.currentID();
		visited.add(current);
		
		List<NodeStatus> neighbors = Arrays.asList(s.neighbors());
		Collections.sort(neighbors);
		
		for (int i = neighbors.size()-1; i >= 0; i--) {
			if (!visited.contains(neighbors.get(i).id())) {
				s.moveTo(neighbors.get(i).id());
				if (s.onPlanetX()) return;
				
				ourSearch(s);
				
				if (s.onPlanetX()) return;
				s.moveTo(current);
			}
		}
	}
	
	/** The spaceship is on the location given by state. Get back to Earth
	 * without running out of fuel and return while on Earth. Your ship can
	 * determine how much fuel it has left via method fuelRemaining().
	 * 
	 * In addition, each Planet has some gems. Passing over a Planet will
	 * automatically collect any gems it carries, which will increase your
	 * score; your objective is to return to earth successfully with as many
	 * gems as possible.
	 * 
	 * You now have access to the entire underlying graph, which can be accessed
	 * through parameter state. Functions currentNode() and earth() return Node
	 * objects of interest, and nodes() returns a collection of all nodes on the
	 * graph.
	 *
	 * Note: Use moveTo() to move to a destination node adjacent to your current
	 * node. */
	@Override
	public void rescue(RescuePhase state) {
		// TODO: Complete the rescue mission and collect gems
	}
}
