/**
 * 
 */
package logic;

/**
 * @author AAG
 *
 */
public class AiDifficulty
{
	private static int CORNER_VALUE = 200;
	private static int INNER_NEXT_CORNER_VALUE = -150;
	private static int EDGE_NEXT_CORNER_VALUE = -100;
	private static int EDGE_NEXT_NEXT_CORNER_VALUE = 10;
	private static int EDGE_VALUE = 5;
	private static int INNER_VALUE = 1;

	public enum Difficulty
	{
		EASY
		{
			@Override
			public int discValue(int i, int j, int height, int width)
			{
				return INNER_VALUE;
			}
		},
		HARD
		{
			@Override
			public int discValue(int i, int j, int height, int width)
			{
				// Corner
				if (((i == 0) || (i == height - 1)) && ((j == 0) || (j == width - 1)))
				{
					return CORNER_VALUE;
				}
				// Inner next Corner
				if (((i == 1) || (i == height - 2)) && ((j == 1) || (j == width - 2)))
				{
					return INNER_NEXT_CORNER_VALUE;
				}
				// Edge next Corner
				if ((((i == 0) || (i == height - 1)) && ((j == 1) || (j == width - 2))) || (((i == 1) || (i == height - 2)) && ((j == 0) || (j == width - 1))))
				{
					return EDGE_NEXT_CORNER_VALUE;
				}
				// Edge next next Corner
				if ((((i == 0) || (i == height - 1)) && ((j == 2) || (j == width - 3))) || (((i == 2) || (i == height - 3)) && ((j == 0) || (j == width - 1))))
				{
					return EDGE_NEXT_NEXT_CORNER_VALUE;
				}
				// Corner
				if ((i == 0) || (i == height - 1) || (j == 0) || (j == width - 1))
				{
					return EDGE_VALUE;
				}
				return INNER_VALUE;
			}
		};

		public String toString()
		{
			return name().charAt(0) + name().substring(1).toLowerCase();
		}

		public abstract int discValue(int i, int j, int height, int width);
	}
}
