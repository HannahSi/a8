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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import controllers.RescuePhase;

/** An instance implements the methods needed to complete the mission. */
public class MySpaceship implements Spaceship {
	
	HashMap<Integer, Integer> visited = new HashMap<Integer, Integer>();
	HashMap<Node, SF> minPaths = new HashMap<Node, SF>();
	LinkedList<Node> shortestPath = new LinkedList<Node>();

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
		moveToBestNeighborArray(state);
	}
	
	private void moveToBestNeighborArray(RescuePhase state) {
		/* 1.base case */
		if (state.currentNode() == state.earth()) return;
		
		Node current = state.currentNode();
		
		/* 2.creates an ArrayList of neighboring nodes from current node */
		Set<Node> neighborsSet = current.neighbors().keySet();
		ArrayList<Node> neighbors = new ArrayList<Node>();
		for (Node n: neighborsSet) {
			int currentToNeighbor = current.getEdge(n).length;
			int neighborToEarth = minPaths.get(n).distance();
			if (currentToNeighbor + neighborToEarth < state.fuelRemaining())
				neighbors.add(n);
		}
		
		/* 3.sorts neighbors based on worth: ratio of gems and edge distance */
		neighbors.sort((n1, n2) -> compareWorth(current, n1, n2) ? -1:1);
		
		/* 4.iterate through the neighbors from most worth to least until a neighbor node is found 
			where there is enough fuel to travel the distance to the node and then to Earth */
		Node n;
		int i = neighbors.size()-1;
		
		if (neighbors.get(i).gems() == 0 && i > 0)  {
			int randomIndex = (int) (Math.random()*neighbors.size());
			while (neighbors.get(randomIndex) == state.earth())
				randomIndex = (int) (Math.random()*neighbors.size());
			
			n = neighbors.get(randomIndex);
		} else 
			n = neighbors.get(i);
		
		/* 5.move to the neighbor with highest worth that can reach to Earth with current fuel */
		state.moveTo(n);
		
		/* 6.recursive step */
		moveToBestNeighborArray(state);
	}
	
	public boolean compareWorth(Node current, Node n1, Node n2){
		//max gems is 5000
		return (worth(current, n1) + n1.gems()/5000.0) <= (worth(current, n2) + n2.gems()/5000.0);
	}
	private double worth(Node current, Node neighbor) {
		return (double) neighbor.gems()/current.getEdge(neighbor).fuelNeeded();
	}
<<<<<<< HEAD
=======
	
	private int minDistance(List<Node> neighbors, Node current) {
		int minDistance = current.getEdge(neighbors.get(0)).fuelNeeded();
		for (Node n : neighbors) {
			int distance = current.getEdge(n).fuelNeeded();
			if (distance < minDistance)
				minDistance = distance;
		}	
		return minDistance;
	}
	
>>>>>>> bbca51c76c8df79b041d16d64e28cac846eb0b31
}