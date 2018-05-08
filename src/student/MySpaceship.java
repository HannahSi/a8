package student;

/**Names: Clara Song (cs2274), Hannah Si (hs649) */

import controllers.Spaceship;

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

import controllers.RescuePhase;

/** An instance implements the methods needed to complete the mission. */
public class MySpaceship implements Spaceship {
	
	// For search. HashMap of nodes that have already been visited in the dfsWalkSearch method
	HashMap<Integer, Integer> visited = new HashMap<Integer, Integer>();
	
	/* For rescue. HashMap of nodes to SF objects to find distance of the minimum path from 
	a node to Earth */
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
		dfsWalkSearch(state);
	}
	
	/**Modeled after depth-first walk of a graph and its specifications from the JavaHyperText
	 * Major difference: visit neighbors in order of strongest to lowest signal strength until planet X is
	 * reached
	 * Precondition: state.currentID() corresponds to Node that is not contained in the HashMap visited
	 * (the spaceship is visiting an unvisited planet)
	 */
	public void dfsWalkSearch(SearchPhase state) {
		if (state.onPlanetX()) return;	//base case
		
		int current = state.currentID();
		visited.put(current, 0);
				
		List<NodeStatus> neighbors = Arrays.asList(state.neighbors());
		Collections.sort(neighbors); //sorted in order of increasing signal strength
		
		int i = neighbors.size()-1;
		while (i >= 0 && !state.onPlanetX()){
			//if neighbor node has not been visited yet
			if (!visited.containsKey(neighbors.get(i).id())) {
				state.moveTo(neighbors.get(i).id());
				dfsWalkSearch(state);
				if (!state.onPlanetX())
					state.moveTo(current);
			}
			i--;
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
		moveToBestNeighbor(state);
	}
	
	/** The spaceship moves to neighbor with highest gems-to-fuel needed ratio that the ship can reach 
	 * with enough fuel to go back to Earth (calculated using the minimum path distance). 
	 * If there is more than one "plausible" neighboring planet (enough fuel to go to planet and 
	 * then to Earth) but they all have zero gems, then choose to go to a random planet that's not Earth.
	 * Stops once the ship is on Earth.
	 * Precondition: there is enough fuel to return to Earth from state.currentNode(), unless
	 * state.currentNode() is earth.
	 */
	private void moveToBestNeighbor(RescuePhase state) {
		/* 1. Base case */
		if (state.currentNode() == state.earth()) return;
		
		Node current = state.currentNode();
		Set<Node> neighborsSet = current.neighbors().keySet();
		
		/* 2. Create an ArrayList of neighbors from which the spaceship has enough fuel to return
		 * to earth if it travels to those neighbors (determined for each neighbor) */
		ArrayList<Node> neighbors = new ArrayList<Node>();
		for (Node n: neighborsSet) {
			int currentToNeighbor = current.getEdge(n).length;
			int neighborToEarth = minPaths.get(n).distance();
			if (currentToNeighbor + neighborToEarth < state.fuelRemaining())
				neighbors.add(n);
		}
		
		/* 3. Sort neighbors by increasing worth (ratio of gems and fuel needed). Tie-breaker is number of gems.*/
		neighbors.sort((n1, n2) -> compareWorth(current, n1, n2) ? -1:1);
		
		/* 4. If the neighbor of highest worth (the last neighbor in the list) has 0 gems and there
		 * is more than one neighbor the spaceship can move to, choose and move to a random neighbor that is 
		 * not Earth. In any other case, choose and move to the last neighbor in the list.*/
		Node n;
		int i = neighbors.size()-1;
		
		if (neighbors.get(i).gems() == 0 && i > 0)  {
			int randomIndex = (int) (Math.random()*neighbors.size());
			while (neighbors.get(randomIndex) == state.earth())
				randomIndex = (int) (Math.random()*neighbors.size());
			n = neighbors.get(randomIndex);
		} else 
			n = neighbors.get(i);
		
		state.moveTo(n);
		
		/* 5. Recursive step */
		moveToBestNeighbor(state);
	}
	
	/** Return true if Node n1 is less than or equal to Node n2 in its worth and false otherwise. Worth is 
	 * determined by the gem-to-fuel needed ratio, plus, as a tie breaker, the number of gems of the node
	 * divided by the max number of gems (a constant in the PlanetX class). 
	 * Precondition: n1 and n2 are neighbors of current. */
	public boolean compareWorth(Node current, Node n1, Node n2){
		return (worth(current, n1) + n1.gems()/5000.0) <= (worth(current, n2) + n2.gems()/5000.0);
	}
	
	/** The ratio of the gems on a neighbor to the fuel needed to reach the neighbor from the current planet
	 *  Precondition: neighbor is a neighbor of current. */
	private double worth(Node current, Node neighbor) {
		return (double) neighbor.gems()/current.getEdge(neighbor).fuelNeeded();
	}
}