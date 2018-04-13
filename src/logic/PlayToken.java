/**
 * 
 */
package logic;

import logic.PlayingField.Coordinate;

/**
 * @author AAG
 *
 */
public class PlayToken
{

	public static int play(int i, int j, PlayingField field)
	{
		return checkPlay(i, j, field, false, true);
	}

	public static int simulate(int i, int j, PlayingField field)
	{
		return checkPlay(i, j, field, true, false);
	}

	public static int isValid(int i, int j, PlayingField field)
	{
		return checkPlay(i, j, field, true, true);
	}

	public static int checkPlay(int i, int j, PlayingField field, boolean simulate, boolean checkValid)
	{
		GameState[][] squares = field.getSquares();

		int switched = 0;
		switched += checkOneDirection(field, i + 1, squares.length - 1, false, j, true, simulate, checkValid); // Down
		switched += checkOneDirection(field, i - 1, 0, true, j, true, simulate, checkValid); // Up
		switched += checkOneDirection(field, j - 1, 0, true, i, false, simulate, checkValid); // Left
		switched += checkOneDirection(field, j + 1, squares[i].length - 1, false, i, false, simulate, checkValid); // Right

		switched += checkTwoDirections(field, i + 1, squares.length - 1, false, j + 1, squares[i].length - 1, false, simulate, checkValid); // DownRight
		switched += checkTwoDirections(field, i + 1, squares.length - 1, false, j - 1, 0, true, simulate, checkValid); // DownLeft
		switched += checkTwoDirections(field, i - 1, 0, true, j + 1, squares[i].length - 1, false, simulate, checkValid); // UpRight
		switched += checkTwoDirections(field, i - 1, 0, true, j - 1, 0, true, simulate, checkValid); // UpLeft

		switched += checkValid ? 0 : field.getAi_difficulty().discValue(i, j, squares.length, squares[0].length);
		return switched;
	}

	private static int checkOneDirection(PlayingField field, int index1, int threshold, boolean bigger, int index2, boolean vertical, boolean simulate, boolean checkValid)
	{
		GameState[][] squares = field.getSquares();
		GameState gameState = field.getGameState();

		int switched = 0;
		int startIndex = index1;
		while (compareValues(index1, threshold, bigger) && checkSquareState(index1, index2, vertical, oppositeState(gameState), squares))
		{
			index1 = bigger ? index1 - 1 : index1 + 1;
		}
		if (compareValues(startIndex, index1, bigger) && checkSquareState(index1, index2, vertical, gameState, squares))
		{
			index1 = bigger ? index1 + 1 : index1 - 1;
			startIndex = bigger ? startIndex + 1 : startIndex - 1;
			while (index1 != startIndex)
			{
				if (!simulate)
				{
					field.changeSquare(index1, index2, vertical, gameState);
				}
				switched += checkValid ? 1 : field.getAi_difficulty().discValue(index1, index2, squares.length, squares[0].length);
				index1 = bigger ? index1 + 1 : index1 - 1;
			}
		}
		return switched;
	}

	private static int checkTwoDirections(PlayingField field, int index1, int threshold1, boolean bigger1, int index2, int threshold2, boolean bigger2, boolean simulate, boolean checkValid)
	{
		GameState[][] squares = field.getSquares();
		GameState gameState = field.getGameState();

		int switched = 0;
		int index1Start = index1;
		while (compareValues(index1, threshold1, bigger1) && compareValues(index2, threshold2, bigger2) && (squares[index1][index2] == oppositeState(gameState)))
		{
			index1 = bigger1 ? index1 - 1 : index1 + 1;
			index2 = bigger2 ? index2 - 1 : index2 + 1;
		}
		if (compareValues(index1Start, index1, bigger1) && (squares[index1][index2] == gameState))
		{
			index1 = bigger1 ? index1 + 1 : index1 - 1;
			index2 = bigger2 ? index2 + 1 : index2 - 1;
			index1Start = bigger1 ? index1Start + 1 : index1Start - 1;
			while (index1 != index1Start)
			{
				if (!simulate)
				{
					field.changeSquare(index1, index2, gameState);
				}
				switched += checkValid ? 1 : field.getAi_difficulty().discValue(index1, index2, squares.length, squares[0].length);
				index1 = bigger1 ? index1 + 1 : index1 - 1;
				index2 = bigger2 ? index2 + 1 : index2 - 1;
			}
		}
		return switched;
	}

	private static boolean compareValues(int value1, int value2, boolean bigger)
	{
		return bigger ? value1 > value2 : value1 < value2;
	}

	private static boolean checkSquareState(int index, int index2, boolean vertical, GameState state, GameState[][] squares)
	{
		return vertical ? squares[index][index2] == state : squares[index2][index] == state;
	}

	private static GameState oppositeState(GameState gameState)
	{
		if (gameState == GameState.PLAYER1)
		{
			return GameState.PLAYER2;
		}
		else if (gameState == GameState.PLAYER2)
		{
			return GameState.PLAYER1;
		}
		return GameState.NO;
	}

	public static Coordinate checkForMove(PlayingField field)
	{
		GameState[][] squares = field.getSquares();
		int i_opt = -1;
		int j_opt = -1;
		int gain = Integer.MIN_VALUE;
		for (int i = 0; i < squares.length; i++)
		{
			for (int j = 0; j < squares[i].length; j++)
			{
				if (squares[i][j] == GameState.NO)
				{
					if (PlayToken.isValid(i, j, field) > 0)
					{
						int changed = PlayToken.simulate(i, j, field);
						if (changed > gain)
						{
							i_opt = i;
							j_opt = j;
							gain = changed;
						}
					}
				}
			}
		}
		if (gain > Integer.MIN_VALUE)
		{
			return field.new Coordinate(i_opt, j_opt);
		}
		else
		{
			return null;
		}
	}

}
