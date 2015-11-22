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
 * The TeamEurekaMinNodeComparator class is a comparator that is used
 * for ordering children of min nodes in order of increasing heuristic
 * value. It implements the Comparator interface.
 */
import java.util.Comparator;
public class TeamEurekaMinNodeComparator implements Comparator<TeamEurekaStateNode> {
	public int compare(TeamEurekaStateNode node1, TeamEurekaStateNode node2) {
		if (node1.getH() < node2.getH()) {
			return -1;
		}
		else if (node1.getH() == node2.getH()) {
			return 0;
		}
		else {
			return 1;
		}
	}
}
