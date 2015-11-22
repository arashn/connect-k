/**
 *   Connect-K AI - The algorithm for an AI player in the game Connect-K
 *   Copyright (C) 2015  Arash Nabili, Navninder Kaur Yadev
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * ConnectK AI
 * Designed by Team Eureka:
 * 	Navninder Kaur Yadev
 * 	Arash Nabili
 * CS 171 Winter 2015
 * 
 * The TeamEurekaStateNode class is a wrapper class for BoardModel objects.
 * It is used for storing a board state, along with its utility value
 * and the player that made the last move on the current board state.
 * It also provides a tree data structure for referencing a node's
 * children, as well as its parent, for tracking changes to a board state.
 * Additionally, each node stores a reference to the child node with
 * the highest or lowest utility value, for use with minimax.
 * 
 * There is a method for adding child nodes to the current node.
 * There are also accessors for getting the current node's board
 * state, the list of child nodes, the parent node, the current
 * board state's utility value, the player that made the last move,
 * and the child node with the best move. Additionally, there are
 * mutators for setting the utility value and the best child.
 */
import connectK.BoardModel;

public class TeamEurekaStateNode {
	private BoardModel state; // Board state enclosed by the node
	private TeamEurekaStateNode[] children; // Array of child nodes
	private int h; // Board state's utility value
	private int bestChild; // Index of child node with best heuristic value 
	private int lastMove; // Indicates which player moved last for current state
	private boolean timeOver; // Indicates if time has run out while at this node
	private boolean quiet; // Indicates if this node is quiescent
	
	public TeamEurekaStateNode(BoardModel s, int b, int last) {
		state = s;
		h = 0;
		children = new TeamEurekaStateNode[b];
		timeOver = false;
		quiet = true;
		bestChild = 0;
		lastMove = last;
	}
	
	public BoardModel getState() {
		return state;
	}
	
	public TeamEurekaStateNode[] getChildren() {
		return children;
	}
	
	public int getH() {
		return h;
	}
	
	public int getBestChild() {
		return bestChild;
	}
	
	public int getLastMove() {
		return lastMove;
	}
	
	public boolean isTimeOver() {
		return timeOver;
	}
	
	public boolean isQuiet() {
		return quiet;
	}
	
	public void setH(int heur) {
		h = heur;
	}
	
	public void setBestChild(int best) {
		bestChild = best;
	}
	
	public void setTimeOver() {
		timeOver = true;
	}
	
	public void setQuiescence(boolean quiescence) {
		quiet = quiescence;
	}
	
	/**
	 * This function adds a TeamEurekaStateNode object as a child
	 * of the current TeamEurekaStateNode object.
	 * @param child child node for the current node
	 */
	public void addChild(TeamEurekaStateNode child) {
		int i = 0;
		while (i < children.length && children[i] != null) {
			i++;
		}
		if (i < children.length) {
			children[i] = child;
		}
	}
}
