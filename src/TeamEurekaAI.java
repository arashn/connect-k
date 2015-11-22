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
 */
import connectK.CKPlayer;
import connectK.BoardModel;

import java.awt.Point;
import java.util.Arrays;

public class TeamEurekaAI extends CKPlayer {
	int thisPlayerTurn; // Player number for TeamEurekaAI (1 or 2)
	int opponentTurn; // Player number for other player (1 or 2)
	public TeamEurekaAI(byte player, BoardModel state) {
		super(player, state);
		teamName = "Team Eureka";
		thisPlayerTurn = player;
		opponentTurn = thisPlayerTurn == 1 ? 2 : 1;
	}

	@Override
	public Point getMove(BoardModel state) {
		return getMove(state, 5000);
	}

	/**
	 * This method extends the game search tree to the specified depth. It
	 * requires that the tree have depth (depth - 1). At any node in the tree,
	 * if time runs out, the method sets the timeOver field of the TeamEurekaStateNode
	 * object to true, and immediately exits all the way back to the first method
	 * call, which is in the getMove method. On the current board state, starting
	 * from the bottom left cell, it checks every cell, left to right and bottom
	 * to top, and looks for the first empty cell on the board. If there is one,
	 * it will create a new board state with an added piece for one of the players,
	 * depending on which player made the last move. The method will then wrap the
	 * new board state in a TeamEurekaStateNode object, and will add the new TeamEurekaStateNode object
	 * as a child of the current board state, recursively calling itself on the new
	 * TeamEurekaStateNode object.
	 * 
	 * @param node root node of the subtree to be generated
	 * @param depth depth of the subtree to be generated
	 * @param maximizingPlayer indicates if the current node is a max node
	 * @param startTime start time
	 * @param deadlineBuffer deadline with buffer
	 */
	public void extendTree(TeamEurekaStateNode node, int depth, boolean maximizingPlayer, long startTime, double deadlineBuffer) {
		if (System.currentTimeMillis() - startTime < deadlineBuffer) {
			if (depth > 1) {
				if (maximizingPlayer) {
					for (int i = 0; i < node.getChildren().length; i++) {
						if (System.currentTimeMillis() - startTime < deadlineBuffer) {
							extendTree(node.getChildren()[i], depth - 1, false, startTime, deadlineBuffer);
							if (node.getChildren()[i].isTimeOver()) {
								node.setTimeOver();
								break;
							}
						}
						else {
							node.setTimeOver();
							break;
						}
					}
				}
				else {
					for (int i = 0; i < node.getChildren().length; i++) {
						if (System.currentTimeMillis() - startTime < deadlineBuffer) {
							extendTree(node.getChildren()[i], depth - 1, true, startTime, deadlineBuffer);
							if (node.getChildren()[i].isTimeOver()) {
								node.setTimeOver();
								break;
							}
						}
						else {
							node.setTimeOver();
							break;
						}
					}
				}
			}
			else {
				// Starting from the bottom left corner, going left to right and bottom to top,
				// Find the first empty cell on the board, if one exists
				if (maximizingPlayer) {
					for (int j = 0; j < node.getState().getHeight(); j++) {
						for (int i = 0; i < node.getState().getWidth(); i++) {
							if (System.currentTimeMillis() - startTime < deadlineBuffer) {
								if (node.getState().getSpace(i, j) == 0) {
									BoardModel newState = node.getState().placePiece(new Point(i, j), (byte) thisPlayerTurn);
									TeamEurekaStateNode newNode = new TeamEurekaStateNode(newState, newState.spacesLeft, thisPlayerTurn);
									node.addChild(newNode);
								}
							}
							else {
								node.setTimeOver();
								break;
							}
						}
					}
				}
				else {
					for (int j = 0; j < node.getState().getHeight(); j++) {
						for (int i = 0; i < node.getState().getWidth(); i++) {
							if (System.currentTimeMillis() - startTime < deadlineBuffer) {
								if (node.getState().getSpace(i, j) == 0) {
									BoardModel newState = node.getState().placePiece(new Point(i, j), (byte) opponentTurn);
									TeamEurekaStateNode newNode = new TeamEurekaStateNode(newState, newState.spacesLeft, opponentTurn);
									node.addChild(newNode);
								}
							}
							else {
								node.setTimeOver();
								break;
							}
						}
					}
				}
			}
		}
		else {
			node.setTimeOver();
		}
	}

	/**
	 * This method performs minimax search on the game search tree
	 * generated by the makeTree() method. It uses depth-first search to
	 * reach the leaf nodes, and assigns utility values to the leaf nodes
	 * using the eval() method. Then, the method moves to the previous level,
	 * and for each node, finds the child node with the highest or lowest
	 * utility value, depending on the last move on the board at the current
	 * node, and sets that child node as the current node's best child.
	 * The method continues this process for every level of the tree, up to
	 * the root node of the tree. Additionally, each leaf node is checked
	 * for quiescence, based on the value set by the eval method. If the
	 * opponent can win on the very next move, the quiescence method is
	 * called on the leaf node.
	 * 
	 * @param node root node of the subtree to be searched
	 * @param depth depth of the subtree to be searched
	 * @param maximizingPlayer indicates if the current node is a max node
	 * @param startTime start time
	 * @param deadlineBuffer deadline with buffer
	 */
	public void minimax(TeamEurekaStateNode node, int depth, boolean maximizingPlayer, long startTime, double deadlineBuffer) {
		if (System.currentTimeMillis() - startTime < deadlineBuffer) {
			if (terminalTest(node, startTime, deadlineBuffer)) {
				node.setH(eval(node, startTime, deadlineBuffer));
			}
			else if (depth == 0) {
				node.setH(eval(node, startTime, deadlineBuffer));
				if (!node.isQuiet()) {
					//System.out.println("Quiescence test invoked");
					quiescence(node, maximizingPlayer, startTime, deadlineBuffer);
				}
			}
			else if (maximizingPlayer) {
				int best = Integer.MIN_VALUE;
				for (int i = 0; i < node.getChildren().length; i++) {
					TeamEurekaStateNode tempNode = node.getChildren()[i];
					minimax(tempNode, depth - 1, false, startTime, deadlineBuffer);
					if(System.currentTimeMillis() - startTime < deadlineBuffer) {
						if (tempNode.isTimeOver()) {
							node.setTimeOver();
							break;
						}
						if (tempNode.getH() >= best) {
							best = tempNode.getH();
							node.setBestChild(i);
						}
					} else {
						node.setTimeOver();
						break;
					}
				}
				node.setH(best);
			}
			else {
				int best = Integer.MAX_VALUE;
				for (int i = 0; i < node.getChildren().length; i++) {
					TeamEurekaStateNode tempNode = node.getChildren()[i];
					minimax(tempNode, depth - 1, true, startTime, deadlineBuffer);
					if(System.currentTimeMillis() - startTime < deadlineBuffer) {
						if (tempNode.isTimeOver()) {
							node.setTimeOver();
							break;
						}
						if (tempNode.getH() <= best) {
							best = tempNode.getH();
							node.setBestChild(i);
						}
					} else {
						node.setTimeOver();
						break;
					}
				}
				node.setH(best);
			}
		}
		else {
			node.setTimeOver();
		}
	}

	/**
	 * This method performs minimax search with alpha-beta pruning.
	 * It works in the same manner as the minimax method, except that
	 * it also keeps track of alpha and beta values as each node in
	 * the game tree is being considered. Also, since heuristic values
	 * from the previous depth limit are stored with the TeamEurekaStateNode
	 * object, this method can order the children of each internal
	 * node, based on heuristic values from the search at the previous
	 * depth limit. If a node is a max node, the method uses a
	 * TeamEurekaMaxNodeComparator object to order children.
	 * Conversely, if a node is a min node, the method uses a
	 * TeamEurekaMinNodeComparator object to order children.
	 * Additionally, each leaf node is checked for quiescence, based
	 * on the value set by the eval method. If the opponent can win
	 * on the very next move, the quiescence method is called on the
	 * leaf node.
	 * 
	 * @param node root node of the subtree to be searched
	 * @param depth depth of the subtree to be searched
	 * @param alpha alpha value for the game tree
	 * @param beta beta value for the game tree
	 * @param maximizingPlayer indicates if the current node is a max node
	 * @param startTime start time
	 * @param deadlineBuffer deadline with buffer
	 */
	public void alphaBeta(TeamEurekaStateNode node, int depth, int alpha, int beta, boolean maximizingPlayer, long startTime, double deadlineBuffer) {
		if (System.currentTimeMillis() - startTime < deadlineBuffer) {
			if (terminalTest(node, startTime, deadlineBuffer)) {
				node.setH(eval(node, startTime, deadlineBuffer));
			}
			else if (depth == 0) {
				node.setH(eval(node, startTime, deadlineBuffer));
				if (!node.isQuiet()) {
					//System.out.println("Quiescence test invoked");
					quiescence(node, maximizingPlayer, startTime, deadlineBuffer);
				}
			}
			else if (maximizingPlayer) {
				int best = Integer.MIN_VALUE;
				Arrays.sort(node.getChildren(), new TeamEurekaMaxNodeComparator());
				for (int i = 0; i < node.getChildren().length; i++) {
					if (System.currentTimeMillis() - startTime < deadlineBuffer) {
						TeamEurekaStateNode tempNode = node.getChildren()[i];
						alphaBeta(tempNode, depth - 1, alpha, beta, false, startTime, deadlineBuffer);
						if (tempNode.isTimeOver()) {
							node.setTimeOver();
							break;
						}
						if (tempNode.getH() > best) {
							best = tempNode.getH();
							node.setBestChild(i);
						}
						if (best > alpha) {
							alpha = best;
						}
						if (beta <= alpha) {
							break;
						}
					} else {
						node.setTimeOver();
						break;
					}
				}
				node.setH(best);
			}
			else {
				int best = Integer.MAX_VALUE;
				Arrays.sort(node.getChildren(), new TeamEurekaMinNodeComparator());
				for (int i = 0; i < node.getChildren().length; i++) {
					if (System.currentTimeMillis() - startTime < deadlineBuffer) {
						TeamEurekaStateNode tempNode = node.getChildren()[i];
						alphaBeta(tempNode, depth - 1, alpha, beta, true, startTime, deadlineBuffer);
						if (tempNode.isTimeOver()) {
							node.setTimeOver();
							break;
						}
						if (tempNode.getH() < best) {
							best = tempNode.getH();
							node.setBestChild(i);
						}
						if (best < beta) {
							beta = best;
						}
						if (beta <= alpha) {
							break;
						}
					} else {
						node.setTimeOver();
						break;
					}
				}
				node.setH(best);
			}
		}
		else {
			node.setTimeOver();
		}
	}
	
	/**
	 * This method is used on nodes where the opponent can win
	 * in the very next move. It uses the extendTree method to
	 * extend the subtree rooted at the specified node by 1 level,
	 * and recursively calls itself on each of the child nodes.
	 * This process is repeated until a quiescent node is reached,
	 * or until time runs out.
	 * 
	 * @param node node that is not quiescent
	 * @param maximizingPlayer indicates if the current node is a max node
	 * @param startTime start time
	 * @param deadlineBuffer deadline with buffer
	 */
	public void quiescence(TeamEurekaStateNode node, boolean maximizingPlayer, long startTime, double deadlineBuffer) {
		if (System.currentTimeMillis() - startTime < deadlineBuffer) {
			if (node.isQuiet()) {
				node.setH(eval(node, startTime, deadlineBuffer));
			}
			else {
				if (maximizingPlayer) {
					int best = Integer.MIN_VALUE;
					extendTree(node, 1, false, startTime, deadlineBuffer);
					for (int i = 0; i < node.getChildren().length; i++) {
						quiescence(node.getChildren()[i], false, startTime, deadlineBuffer);
						if (node.isTimeOver()) {
							node.setTimeOver();
							break;
						}
						if (node.getChildren()[i].getH() >= best) {
							best = node.getChildren()[i].getH();
							node.setBestChild(i);
						}
					}
					node.setH(best);
				}
				else {
					int best = Integer.MAX_VALUE;
					extendTree(node, 1, true, startTime, deadlineBuffer);
					for (int i = 0; i < node.getChildren().length; i++) {
						quiescence(node.getChildren()[i], true, startTime, deadlineBuffer);
						if (node.isTimeOver()) {
							node.setTimeOver();
							break;
						}
						if (node.getChildren()[i].getH() <= best) {
							best = node.getChildren()[i].getH();
							node.setBestChild(i);
						}
					}
					node.setH(best);
				}
			}
		}
		else {
			node.setTimeOver();
		}
	}

	/**
	 * This method checks if a player has won in the state enclosed by
	 * the TeamEurekaStateNode object. If so, it returns true. Otherwise, it returns
	 * false.
	 * 
	 * @param node node whose state is to be checked for a winner
	 * @param startTime start time
	 * @param deadlineBuffer deadline with buffer
	 * @return the truth value of whether there is a winner in the current state
	 */
	public boolean terminalTest(TeamEurekaStateNode node, long startTime, double deadlineBuffer) {
		if (eval(node, startTime, deadlineBuffer) == Integer.MAX_VALUE
				|| eval(node, startTime, deadlineBuffer) == Integer.MIN_VALUE) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * This method compares two board states and finds the piece on which
	 * the two board states differ.
	 * 
	 * @param currentState current board state
	 * @param newState new board state containing a new piece
	 * @return the new piece on the board
	 */
	public Point stateDiff(BoardModel currentState, BoardModel newState) {
		for (int j = 0; j < currentState.getHeight(); j++) {
			for (int i = 0; i < currentState.getWidth(); i++) {
				if (currentState.getSpace(i, j) != newState.getSpace(i, j)) {
					return new Point(i, j);
				}
			}
		}
		return null;
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		long startTime = System.currentTimeMillis();
		boolean alphaBeta = true;
		int limit = 1;
		Point bestMove = null;
		double deadlineBuffer = deadline * 0.85;
		TeamEurekaStateNode gameTree = new TeamEurekaStateNode(state, state.spacesLeft, opponentTurn);
		TeamEurekaStateNode nextNode = gameTree;
		while (System.currentTimeMillis() - startTime < deadlineBuffer && limit <= state.spacesLeft) {
			extendTree(gameTree, limit, true, startTime, deadlineBuffer);
			if(System.currentTimeMillis() - startTime < deadlineBuffer && limit <= state.spacesLeft) {
				if (alphaBeta) {
					alphaBeta(gameTree, limit, Integer.MIN_VALUE, Integer.MAX_VALUE, true, startTime, deadlineBuffer);
				}
				else {
					minimax(gameTree, limit, true, startTime, deadlineBuffer);
				}
			}
			else {
				break;
			}
			if(System.currentTimeMillis() - startTime < deadlineBuffer && limit <= state.spacesLeft) {
				nextNode = gameTree.getChildren()[gameTree.getBestChild()];
			}
			else {
				break;
			}
			limit++;
		}
		BoardModel nextState = nextNode.getState(); // Board state containing best move
		bestMove = stateDiff(state, nextState); // Return Point object containing best move
		return bestMove;
	}

	// Heuristic Evaluation Function
	/**
	 * This method is the heuristic evaluation function. It evaluates
	 * a given board state for utility. The utility is based on three
	 * features: number of possible winning rows, number of possible
	 * losing rows, and number of threat rows blocked by the AI.
	 * Threat rows consist of a line of K-2 of the opponent's pieces,
	 * not necessarily connected to each other, and none of the AI's
	 * pieces. The threat row is blocked whenever the AI places
	 * a piece in any empty cell in the threat row, preventing
	 * the opponent from having a guaranteed win. The result is a
	 * weighted average of the three features. The number of possible
	 * winning rows has a weight of 1, the number of possible losing
	 * rows has a weight of -1, and the number of blocked threat rows
	 * has a weight of 10, so that the heuristic favors moves which
	 * block the opponent from winning. Also, if the AI has a winning
	 * row, the method immediately returns +infinity, and if the
	 * opponent has a winning row, the method immediately returns
	 * -infinity.
	 * 
	 * @param node the node containing the board state to be evaluated
	 * @param startTime start time
	 * @param deadlineBuffer deadline with buffer
	 * @return the utility value for the given board state
	 */
	public int eval(TeamEurekaStateNode node, long startTime, double deadlineBuffer) {
		BoardModel state = node.getState();
		int a = 0; // Number of possible winning rows
		int b = 0; // Number of possible losing rows
		int c = 0; // Score of how close player's pieces are to the center horizontally
		int d = 0; // Score of how close player's pieces are to the center vertically
		int e = 0; // Score of how close opponent's pieces are to the center horizontally
		int f = 0; // Score of how close opponent's pieces are to the center vertically
		int g = 0; // Number of blocked threat rows
		int numRows = state.getHeight(); // Number of rows
		int numCols = state.getWidth(); // Number of columns
		int k = state.getkLength(); // Value of K
		int tempWin = 0; // Number of non-opponent pieces on the board starting at any cell
		int tempLose = 0; // Number of non-AI pieces on the board starting at any cell
		int win = 0; // Number of AI pieces on the board starting at any cell
		int lose = 0; // Number of opponent pieces on the board starting at any cell
		int[] line = new int[k]; // Stores the combination of k pieces on the board starting at any cell
		String[] threats = new String[k + 3]; // Contains a list of all possible block threat rows
		int[] threat1 = new int[k]; // Combination of pieces that corresponds to a block threat row
		// Create a list of all possible ways to block a threat row
		for (int x = 0; x < k; x++) {
			threat1 = new int[k];
			for (int y = 0; y < k; y++) {
				if (x == y) {
					threat1[y] = thisPlayerTurn;
				}
				else if ((y == k - 1) || (x == k - 1 && y == 0)) {
					threat1[y] = 0;
				}
				else {
					threat1[y] = opponentTurn;
				}
			}
			threats[x] = Arrays.toString(threat1);
		}
		for (int x = k; x < k + 3; x++) {
			threat1 = new int[k];
			for (int y = 0; y < k; y++) {
				if (y == 0) {
					threat1[y] = (x == k || x == k + 1) ? thisPlayerTurn : opponentTurn;
				}
				else if (y == k - 1) {
					threat1[y] = (x == k || x == k + 2) ? thisPlayerTurn : opponentTurn;
				}
				else {
					threat1[y] = opponentTurn;
				}
			}
			threats[x] = Arrays.toString(threat1);
		}
		if (System.currentTimeMillis() - startTime < deadlineBuffer) {
			// Check for horizontal winning and losing rows
			for (int j = 0; j < numRows; j++) {
				for (int i = 0; i <= numCols - k; i++) {
					if (System.currentTimeMillis() - startTime < deadlineBuffer) {
						for (int m = 0; m < k; m++) {
							line[m] = state.getSpace(i + m, j); // Store the combination of pieces for row
							if (state.getSpace(i + m, j) != thisPlayerTurn) {
								// Cell doesn't contain AI piece; Opponent could win
								tempLose++;
							}
							if (state.getSpace(i + m, j) == thisPlayerTurn || state.getSpace(i + m, j) == 0) {
								// Cell doesn't contain opponent piece; AI could win
								tempWin++;
							}
							if (state.getSpace(i + m, j) == thisPlayerTurn) {
								// Cell contains AI piece
								win++;
								if (win == k) { // AI won; return +infinity
									return Integer.MAX_VALUE;
								}
								// c and d are max when player's pieces are close to the center
								// c and d are min when player's pieces are far from the center
								c += (int) (Math.floor(numCols / 2) - Math.abs(Math.floor(numCols / 2) - i));
								d += (int) (Math.floor(numRows / 2) - Math.abs(Math.floor(numRows / 2) - j));
							}
							if (state.getSpace(i + m, j) == opponentTurn) {
								// Cell contains opponent piece
								lose++;
								if (lose == k) { // Opponent won; return -infinity
									return Integer.MIN_VALUE;
								}
								// e and f are max when opponent's pieces are close to the center
								// e and f are min when opponent's pieces are far from the center
								e += (int) (Math.floor(numCols / 2) - Math.abs(Math.floor(numCols / 2) - i));
								f += (int) (Math.floor(numRows / 2) - Math.abs(Math.floor(numRows / 2) - j));
							}
						}
						if (lose == k - 1 && win == 0 && opponentTurn != node.getLastMove()) {
							// Opponent could win in the very next move, so mark node as not quiescent
							node.setQuiescence(false);
						}
						if (tempWin == k) {
							// AI has a possible winning row
							a++;
						}
						if (tempLose == k) {
							// Opponent has a possible winning row
							b++;
						}
						for (int s = 0; s < threats.length; s++) {
							if (threats[s].equals(Arrays.toString(line))) {
								if (i >= 0 && i + k - 2 <= numCols - 2) {
									// Blocked threat detected
									g++;
								}
							}
						}
						win = 0;
						lose = 0;
						tempWin = 0;
						tempLose = 0;
					}
					else {
						return a - b + c + d - e - f + 100*g;
					}
				}
			}
			line = new int[k];
			// Check for vertical winning and losing rows
			for (int i = 0; i < numCols; i++) {
				for (int j = 0; j <= numRows - k; j++) {
					if (System.currentTimeMillis() - startTime < deadlineBuffer) {
						for (int m = 0; m < k; m++) {
							line[m] = state.getSpace(i, j + m); // Store the combination of pieces for row
							if (state.getSpace(i, j + m) != thisPlayerTurn) {
								// Cell doesn't contain AI piece; Opponent could win
								tempLose++;
							}
							if (state.getSpace(i, j + m) == thisPlayerTurn || state.getSpace(i, j + m) == 0) {
								// Cell doesn't contain opponent piece; AI could win
								tempWin++;
							}
							if (state.getSpace(i, j + m) == thisPlayerTurn) {
								// Cell contains AI piece
								win++;
								if (win == k) { // AI won; return +infinity
									return Integer.MAX_VALUE;
								}
								// c and d are max when player's pieces are close to the center
								// c and d are min when player's pieces are far from the center
								c += (int) (Math.floor(numCols / 2) - Math.abs(Math.floor(numCols / 2) - i));
								d += (int) (Math.floor(numRows / 2) - Math.abs(Math.floor(numRows / 2) - j));
							}
							if (state.getSpace(i, j + m) == opponentTurn) {
								// Cell contains opponent piece
								lose++;
								if (lose == k) { // Opponent won; return -infinity
									return Integer.MIN_VALUE;
								}
								// e and f are max when opponent's pieces are close to the center
								// e and f are min when opponent's pieces are far from the center
								e += (int) (Math.floor(numCols / 2) - Math.abs(Math.floor(numCols / 2) - i));
								f += (int) (Math.floor(numRows / 2) - Math.abs(Math.floor(numRows / 2) - j));
							}
						}
						if (lose == k - 1 && win == 0 && opponentTurn != node.getLastMove()) {
							// Opponent could win in the very next move, so mark node as not quiescent
							node.setQuiescence(false);
						}
						if (tempWin == k) {
							// AI has a possible winning row
							a++;
						}
						if (tempLose == k) {
							// Opponent has a possible winning row
							b++;
						}
						for (int s = 0; s < threats.length; s++) {
							if (threats[s].equals(Arrays.toString(line)))  {
								if (j >= 0 && j + k - 2 <= numRows - 2) {
									// Blocked threat detected
									g++;
								}
							}
						}
						win = 0;
						lose = 0;
						tempWin = 0;
						tempLose = 0;
					}
					else {
						return a - b + c + d - e - f + 100*g;
					}
				}
			}
			line = new int[k];

			// Check for diagonal (up and right) winning and losing rows
			for (int i = 0; i <= numCols - k; i++) {
				for (int j = 0; j <= numRows - k; j++) {
					if (System.currentTimeMillis() - startTime < deadlineBuffer) {
						for (int m = 0; m < k; m++) {
							line[m] = state.getSpace(i + m, j + m); // Store the combination of pieces for row
							if (state.getSpace(i + m, j + m) != thisPlayerTurn) {
								// Cell doesn't contain AI piece; Opponent could win
								tempLose++;
							}
							if (state.getSpace(i + m, j + m) == thisPlayerTurn || state.getSpace(i + m, j + m) == 0) {
								// Cell doesn't contain opponent piece; AI could win
								tempWin++;
							}
							if (state.getSpace(i + m, j + m) == thisPlayerTurn) {
								// Cell contains AI piece
								win++;
								if (win == k) { // AI won; return +infinity
									return Integer.MAX_VALUE;
								}
								// c and d are max when player's pieces are close to the center
								// c and d are min when player's pieces are far from the center
								c += (int) (Math.floor(numCols / 2) - Math.abs(Math.floor(numCols / 2) - i));
								d += (int) (Math.floor(numRows / 2) - Math.abs(Math.floor(numRows / 2) - j));
							}
							if (state.getSpace(i + m, j + m) == opponentTurn) {
								// Cell contains opponent piece
								lose++;
								if (lose == k) { // Opponent won; return -infinity
									return Integer.MIN_VALUE;
								}
								// e and f are max when opponent's pieces are close to the center
								// e and f are min when opponent's pieces are far from the center
								e += (int) (Math.floor(numCols / 2) - Math.abs(Math.floor(numCols / 2) - i));
								f += (int) (Math.floor(numRows / 2) - Math.abs(Math.floor(numRows / 2) - j));
							}
						}
						if (lose == k - 1 && win == 0 && opponentTurn != node.getLastMove()) {
							// Opponent could win in the very next move, so mark node as not quiescent
							node.setQuiescence(false);
						}
						if (tempWin == k) {
							// AI has a possible winning row
							a++;
						}
						if (tempLose == k) {
							// Opponent has a possible winning row
							b++;
						}
						for (int s = 0; s < threats.length; s++) {
							if (threats[s].equals(Arrays.toString(line))) {
								if (j >= 0 && j + k - 2 <= numRows - 2) {
									// Blocked threat detected
									g++;
								}
							}
						}
						win = 0;
						lose = 0;
						tempWin = 0;
						tempLose = 0;
					}
					else {
						return a - b + c + d - e - f + 100*g;
					}
				}
			}
			line = new int[k];

			// Check for diagonal (up and left) winning and losing rows
			for (int i = numCols - 1; i >= k - 1; i--) {
				for (int j = 0; j <= numRows - k; j++) {
					if (System.currentTimeMillis() - startTime < deadlineBuffer) {
						for (int m = 0; m < k; m++) {
							line[m] = state.getSpace(i - m, j + m); // Store the combination of pieces for row
							if (state.getSpace(i - m, j + m) != thisPlayerTurn) {
								// Cell doesn't contain AI piece; Opponent could win
								tempLose++;
							}
							if (state.getSpace(i - m, j + m) == thisPlayerTurn || state.getSpace(i - m, j + m) == 0) {
								// Cell doesn't contain opponent piece; AI could win
								tempWin++;
							}
							if (state.getSpace(i - m, j + m) == thisPlayerTurn) {
								// Cell contains AI piece
								win++;
								if (win == k) { // AI won; return +infinity
									return Integer.MAX_VALUE;
								}
								// c and d are max when player's pieces are close to the center
								// c and d are min when player's pieces are far from the center
								c += (int) (Math.floor(numCols / 2) - Math.abs(Math.floor(numCols / 2) - i));
								d += (int) (Math.floor(numRows / 2) - Math.abs(Math.floor(numRows / 2) - j));
							}
							if (state.getSpace(i - m, j + m) == opponentTurn) {
								// Cell contains opponent piece
								lose++;
								if (lose == k) { // Opponent won; return -infinity
									return Integer.MIN_VALUE;
								}
								// e and f are max when opponent's pieces are close to the center
								// e and f are min when opponent's pieces are far from the center
								e += (int) (Math.floor(numCols / 2) - Math.abs(Math.floor(numCols / 2) - i));
								f += (int) (Math.floor(numRows / 2) - Math.abs(Math.floor(numRows / 2) - j));
							}
						}
						if (lose == k - 1 && win == 0 && opponentTurn != node.getLastMove()) {
							// Opponent could win in the very next move, so mark node as not quiescent
							node.setQuiescence(false);
						}
						if (tempWin == k) {
							// AI has a possible winning row
							a++;
						}
						if (tempLose == k) {
							// Opponent has a possible winning row
							b++;
						}
						for (int s = 0; s < threats.length; s++) {
							if (threats[s].equals(Arrays.toString(line))) {
								if (j >= 0 && j + k - 2 <= numRows - 2) {
									// Blocked threat detected
									g++;
								}
							}
						}
						win = 0;
						lose = 0;
						tempWin = 0;
						tempLose = 0;
						
					}
					else {
						return a - b + c + d - e - f + 100*g;
					}
				}
			}
			return a - b + c + d - e - f + 100*g;
		} 
		else {
			return a - b + c + d - e - f + 100*g;
		}
	}
}
