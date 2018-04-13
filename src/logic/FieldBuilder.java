/**
 * 
 */
package logic;

/**
 * @author AAG
 *
 */
public class FieldBuilder
{
	public static void initField(PlayingField field)
	{
		switch (field.getStartConfig())
		{
			case EMPTY:
			{
				FieldBuilder.initEmptyField(field);
				break;
			}
			case CROSS:
			{
				FieldBuilder.initCrossField(field);
				break;
			}
			case PARALLEL:
			{
				FieldBuilder.initParallelField(field);
				break;
			}
			default:
			{
				System.out.println("Start Config not defined... Using EMPTY.");
				FieldBuilder.initEmptyField(field);
				break;
			}
		}
	}
	
	
	public static void initEmptyField(PlayingField field)
	{
		GameState[][] squares = field.getSquares();
		for (int i = 0; i < squares.length; i++)
		{
			for (int j = 0; j < squares[i].length; j++)
			{
				field.changeSquare(i, j, GameState.NO);
			}
		}
	}

	public static void initCrossField(PlayingField field)
	{
		GameState[][] squares = field.getSquares();
		for (int i = 0; i < squares.length; i++)
		{
			for (int j = 0; j < squares[i].length; j++)
			{
				if (((j == (int) (squares[i].length / 2.0 - 1)) && (i == (int) (squares.length / 2.0 - 1))) || ((j == (int) (squares[i].length / 2.0)) && (i == (int) (squares.length / 2.0))))
				{
					field.changeSquare(i, j, GameState.PLAYER2);
				}
				else if (((j == (int) (squares[i].length / 2.0 - 1)) && (i == (int) (squares.length / 2.0))) || ((j == (int) (squares[i].length / 2.0)) && (i == (int) (squares.length / 2.0 - 1))))
				{
					field.changeSquare(i, j, GameState.PLAYER1);
				}
				else
				{
					field.changeSquare(i, j, GameState.NO);
				}
			}
		}
	}

	public static void initParallelField(PlayingField field)
	{
		GameState[][] squares = field.getSquares();
		for (int i = 0; i < squares.length; i++)
		{
			for (int j = 0; j < squares[i].length; j++)
			{
				if (((j == (int) (squares[i].length / 2.0 - 1)) && (i == (int) (squares.length / 2.0 - 1))) || ((j == (int) (squares[i].length / 2.0)) && (i == (int) (squares.length / 2.0 - 1))))
				{
					field.changeSquare(i, j, GameState.PLAYER1);
				}
				else if (((j == (int) (squares[i].length / 2.0 - 1)) && (i == (int) (squares.length / 2.0))) || ((j == (int) (squares[i].length / 2.0)) && (i == (int) (squares.length / 2.0))))
				{
					field.changeSquare(i, j, GameState.PLAYER2);
				}
				else
				{
					field.changeSquare(i, j, GameState.NO);
				}
			}
		}
	}

	public static void printField(GameState[][] squares)
	{
		for (int i = 0; i < squares.length; i++)
		{
			for (int j = 0; j < squares[i].length; j++)
			{
				if (j != squares[i].length)
				{
					System.out.print(squares[i][j].state + " ");
				}
				else
				{
					System.out.print(squares[i][j].state);
				}
			}
			System.out.println();
		}
		System.out.println();
	}
}
