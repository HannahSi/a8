package student;

/** Time spent: 
 * 4/28: 1.5 hrs
 * separately 1 hr
 * 4/29 1 hr 
 * 4/30 1.75 hrs */

import controllers.Spaceship;

import models.Edge;
import models.Node;
import models.NodeStatus;
import student.Paths.SF;
import controllers.SearchPhase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.omg.CORBA.Current;

import controllers.RescuePhase;

/** An instance implements the methods needed to complete the mission. */
public class MySpaceship implements Spaceship {
	
	ArrayList<Integer> visited = new ArrayList<Integer>();
	HashMap<Node, SF> minPaths = new HashMap<Node, SF>();

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
		
		if (state.onPlanetX()) return;
		
		List<NodeStatus> neighbors = Arrays.asList(state.neighbors());
		Collections.sort(neighbors);
		
		for (int i = neighbors.size()-1; i >= 0; i--) {
			
			if (state.onPlanetX()) return;
			
			if (!visited.contains(neighbors.get(i).id())) {
				
				state.moveTo(neighbors.get(i).id());
				
				search(state);
				
				if (!state.onPlanetX())
					state.moveTo(current);
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
		
		minPaths = Paths.allMinPaths(state.earth());
		
	}
	
	/** Moves to neighbor with the most worth
	 *  worth = neighbor.gem / distance from current node to neighbor
	 */
	private void moveToBestNeighbor(RescuePhase state) {
		Node current = state.currentNode();
		Set<Node> neighborsSet = current.neighbors().keySet();
		ArrayList<Node> neighbors = new ArrayList<Node>();
		
		for (Node n: neighborsSet) 
			neighbors.add(n);
		sortNeighbors(current, neighbors);
		
		int i = neighbors.size()-1;
		int currentToNeighbor;
		int neighborToEarth;
		do {
			currentToNeighbor = current.getEdge(neighbors.get(i)).length;
			neighborToEarth = minPaths.get(neighbors.get(i)).distance();
			i--;
		} while (i > 0 && currentToNeighbor + neighborToEarth > state.fuelRemaining());
		
		state.moveTo(neighbors.get(i));
		
		

	}
	
	private void sortNeighbors(Node current, ArrayList<Node> neighbors) {
		//insertion sort
		//inv: neighbors[0..i-1] is sorted in increasing order of worth
		for (int i = 0; i < neighbors.size(); i++) {
			//inv: neighbors[0..i] is sorted, except that the neighbors[k] might < neighbors[k-1]
			int k = i;
			while (k > 0 && worth(current, neighbors.get(k)) <= worth(current, neighbors.get(k-1))) {
				if (worth(current, neighbors.get(k)) != worth(current, neighbors.get(k-1)) ||
						neighbors.get(k).gems() < neighbors.get(k-1).gems()) { //tie-breaking condition
					Node temp = neighbors.get(k);
					neighbors.set(k, neighbors.get(k-1));
					neighbors.set(k-1, temp);
					k--;
				}
			}
		}
	}
	
	private double worth(Node current, Node neighbor) {
		return (double) neighbor.gems()/current.getEdge(neighbor).fuelNeeded();
	}
	
}
