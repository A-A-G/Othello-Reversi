/**
 * 
 */
package logic;

/**
 * @author AAG
 *
 */
public class Points
{
	private int pointsPlayer1 = 0;
	private int pointsPlayer2 = 0;

	public void clearPoints()
	{
		pointsPlayer1 = 0;
		pointsPlayer2 = 0;
	}

	public GameState getLeader()
	{
		if (pointsPlayer1 == pointsPlayer2)
		{
			return GameState.NO;
		}
		return pointsPlayer1 > pointsPlayer2 ? GameState.PLAYER1 : GameState.PLAYER2;
	}

	public String getGameEndMessage()
	{
		String s = "";
		if (pointsPlayer1 > pointsPlayer2)
		{
			s = "Player " + GameState.PLAYER1 + " won with " + pointsPlayer1 + " vs " + pointsPlayer2 + " points.";
		}
		else if (pointsPlayer2 > pointsPlayer1)
		{
			s = "Player " + GameState.PLAYER2 + " won with " + pointsPlayer2 + " vs " + pointsPlayer1 + " points.";
		}
		else
		{
			s = "A draw! " + pointsPlayer2 + " vs " + pointsPlayer1 + ".";
		}
		return s;
	}

	public void addPoints(int points, GameState gameState)
	{
		if (gameState == GameState.PLAYER1)
		{
			pointsPlayer1 = pointsPlayer1 + points;
		}
		else if (gameState == GameState.PLAYER2)
		{
			pointsPlayer2 = pointsPlayer2 + points;
		}
	}

	public int getPoints(GameState gameState)
	{
		if (gameState == GameState.PLAYER1)
		{
			return pointsPlayer1;
		}
		else if (gameState == GameState.PLAYER2)
		{
			return pointsPlayer2;
		}
		return -1;
	}

	public int getTotalPoints()
	{
		return pointsPlayer1 + pointsPlayer2;
	}

	public int getPlayer1Points()
	{
		return pointsPlayer1;
	}

	public int getPlayer2Points()
	{
		return pointsPlayer2;
	}

}
