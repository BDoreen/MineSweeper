package mines;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Mines {

	private Spot[][] board;
	private int height, width, mines;
	private boolean showAll;

	// initials board of the given size with Spot
	public Mines(int height, int width, int mines) {
		board = new Spot[height][width];
		this.height = height;
		this.width = width;
		this.mines = mines;
		initBoard();
	}

	// initial class Spot at each board element
	private void initBoard() {
		// Initial board with Spot
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++)
				board[i][j] = new Spot(i, j);
		}

		// calculate set of neighbors of each spot on board
		for (Spot[] arr : board)
			for (Spot spot : arr)
				spot.neighborsSet();

		// initial random mines
		if (mines <= 0 || (width <= 0 && height <= 0))
			return;
		Random rand = new Random();
		int x = 0, y = 0;
		for (; mines > 0; mines--) {
			x = rand.nextInt() % height;
			y = rand.nextInt() % width;
			if (addMine(x, y) == false)
				mines++;
		}
	}

	// check if given indexes are inside board boarders
	private boolean inside(int i, int j) {
		if (i >= height || j >= width || i < 0 || j < 0)
			return false;
		return true;
	}

	// add a mine to board
	public boolean addMine(int i, int j) {
		if (inside(i, j) == false)
			return false;
		if (board[i][j].isMine == true)
			return false;
		return (board[i][j].isMine = true);
	}

	// return true if Spot is not a MINE
	// also, if neighbors of Spot are not MINEs
	// open them.
	public boolean open(int i, int j) {

		// check restrictions
		if (inside(i, j) == false) // outside of board
			return false;
		if (board[i][j].isOpen == true) // already opened
			return true;
		if (board[i][j].isMine == true) // is a mine
			return false;
		if(board[i][j].isFlag == true)
			return true;

		// set current spot to open
		board[i][j].isOpen = true;

		// check if some neighbor is a mine
		for (Spot spot : board[i][j].neighbours)
			// if (spot != null)
			if (spot.isMine == true)
				return true; // stop opening neighbors

		// call open recursively on all neighbors
		for (Spot spot : board[i][j].neighbours)
			// if (spot != null)
			open(spot.i, spot.j);
		return true;
	}

	// puts flag at (x,y)
	// if already flagged, removes it.
	public void toggleFlag(int x, int y) {
		board[x][y].isFlag = !board[x][y].isFlag;
	}

	// true if all Spots which are not Mines are open
	public boolean isDone() {
		for (Spot[] arr : board)
			for (Spot spot : arr)
				if (!spot.isMine && !spot.isOpen)
					return false;
		return true;
	}

	// print current spot
	public String get(int i, int j) {
		if (inside(i, j))
			return board[i][j].toString();
		return null;
	}

	// if showAll=true than relate to all Spots as open.
	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}

	// print the board according to get
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Spot[] arr : board) {
			for (Spot spot : arr)
				sb.append(spot.toString());
			sb.append('\n');
		}
		return sb.toString();
	}

	private class Spot {
		private int i, j, numOfMineNeighbors;
		private boolean isMine, isOpen, isFlag;
		private Set<Spot> neighbours;

		private Spot(int i, int j) {
			this.i = i;
			this.j = j;
		}

		// initial neighbors of current Spot
		private void neighborsSet() {
			neighbours = new HashSet<>();
			neighbours.add(checkNeigh(i + 1, j));// right
			neighbours.add(checkNeigh(i - 1, j));// left
			neighbours.add(checkNeigh(i, j + 1));// up
			neighbours.add(checkNeigh(i, j - 1));// down
			neighbours.add(checkNeigh(i - 1, j + 1));// top main diagonal ('\')
			neighbours.add(checkNeigh(i + 1, j - 1));// bottom main diagonal ('\')
			neighbours.add(checkNeigh(i + 1, j + 1));// top secondary diagonal ('/')
			neighbours.add(checkNeigh(i - 1, j - 1));// bottom secondary diagonal ('/')
			neighbours.remove(null);
		}

		// check if allegedly neighbor is inside board
		private Spot checkNeigh(int dx, int dy) {
			if (inside(dx, dy) == false)
				return null;
			return board[dx][dy];
		}

		// A. if Spot is closed(wasn't clicked)
		// 1. it is flagged 'F'
		// 2. it is anonymous '.'
		// B. if spot is open(was clicked)
		// 1. it is a mine 'X'
		// 2. give hint 'numOfMine'
		@Override
		public String toString() {

			// calculate number of mines around Spot
			numOfMineNeighbors = 0;
			if (neighbours != null)
				for (Spot spot : neighbours)
					// if (spot != null)
					if (spot.isMine)
						numOfMineNeighbors++;

			if (!isOpen && !showAll) {
				if (isFlag)
					return "F";
				else
					return ".";
			} else if (isOpen || showAll) {
				if (isMine)
					return "X";
				else if (numOfMineNeighbors == 0)
					return " ";
			}
			return Integer.toString(numOfMineNeighbors);
		}
	}
}
