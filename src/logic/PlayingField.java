/**
 * 
 */
package logic;

import gui.Board;
import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;
import logic.AiDifficulty.Difficulty;

/**
 * @author AAG
 *
 */
public class PlayingField
{

	public class Coordinate
	{
		public final int x;
		public final int y;

		public Coordinate(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
	};

	public enum StartConfig
	{
		EMPTY, CROSS, PARALLEL;

		public String toString()
		{
			return name().charAt(0) + name().substring(1).toLowerCase();
		}
	}

	private static StartConfig DEFAULT_START_CONFIG = StartConfig.PARALLEL;
	private StartConfig startConfig = DEFAULT_START_CONFIG;

	private GameState[][] squares;

	private GameState gameState = GameState.NO;

	private Points points = new Points();;

	private Board board = null;
	private Label statusLabel = null;

	private Duration turnDuration = Duration.millis(0);

	private int numberOfPlayers = 1;

	private GameState ai_state = GameState.PLAYER2;

	private Difficulty ai_difficulty = Difficulty.HARD;

	private int passCounter = 0;

	/**
	 * 
	 */
	public PlayingField(int size)
	{
		this(size, size);
	}

	/**
	 * 
	 */
	public PlayingField(int sizeTB, int sizeLR)
	{
		super();
		updateSize(sizeTB, sizeLR);
	}

	public void startGame()
	{
		passCounter = 0;
		gameState = Math.random() > 0.5 ? GameState.PLAYER1 : GameState.PLAYER2;
		status("New game started. " + gameState + " goes fist.", false);
		updateState();
		if ((numberOfPlayers == 1) && (gameState == ai_state))
		{
			if (startConfig != StartConfig.EMPTY)
			{
				playAI();
			}
			else
			{
				playStartAI();
			}
		}
	}

	public void endGame()
	{
		if (gameState != GameState.NO)
		{
			gameState = GameState.NO;
			status(points.getGameEndMessage(), false);
			if ((board != null) && ((numberOfPlayers == 2) || (points.getPoints(ai_state) < points.getPoints(swapState(ai_state)))))
			{
				board.gameEndPoints(points);
			}
		}
	}

	public void clearGame()
	{
		FieldBuilder.initField(this);
		status("", false);
		FieldBuilder.printField(squares);
	}

	public void squareClicked(int i, int j)
	{
		if (gameState == GameState.NO)
		{
			status("Start a new game first!", false);
			return;
		}
		if ((1 == numberOfPlayers) && (gameState == ai_state))
		{
			status("AIs turn!", true);
			return;
		}
		if (squares[i][j] != GameState.NO)
		{
			status("You can't do that. Already taken!", true);
			return;
		}
		if ((points.getTotalPoints() < 4) && (startConfig == StartConfig.EMPTY))
		{
			if (((j == (int) (squares[i].length / 2.0 - 1)) || (j == (int) (squares[i].length / 2.0))) && ((i == (int) (squares.length / 2.0)) || (i == (int) (squares.length / 2.0 - 1))))
			{
				status("Good!", false);
				playTurnAndSwap(i, j, gameState);
			}
			else
			{
				status("You can't do that. Invalid position. Only the inner four squares are allowed at start.", true);
			}
			return;
		}
		int changed = PlayToken.play(i, j, this);
		System.out.println(getAi_difficulty().discValue(i, j, squares.length, squares[0].length));
		if (changed == 0)
		{
			status("You can't do that. Invalid position. Nothing to turn.", true);
			return;
		}
		status("Good!", false);
		playTurn(i, j, gameState);
		if ((1 == numberOfPlayers) && (gameState != ai_state))
		{
			PauseTransition wait = new PauseTransition(turnDuration);
			wait.setOnFinished((e) ->
			{
				swapGameState();
			});
			wait.play();
		}
		else
		{
			swapGameState();
		}
	}

	private void checkForAiTurn()
	{
		if ((numberOfPlayers == 1) && (gameState == ai_state))
		{
			if (points.getTotalPoints() < 4)
			{
				playStartAI();
			}
			else
			{
				playAI();
			}
		}
	}

	private void playAI()
	{
		Coordinate optCoord = PlayToken.checkForMove(this);
		if (optCoord != null)
		{
			PlayToken.play(optCoord.x, optCoord.y, this);
			playTurnAndSwap(optCoord.x, optCoord.y, gameState);
		}
		else
		{
			System.out.println("Error: No turn possible for AI.");
			passCounter++;
			swapGameState();
		}
	}

	private void playStartAI()
	{
		Coordinate coord = getStartCoordinate();
		if (coord != null)
		{
			playTurnAndSwap(coord.x, coord.y, gameState);
		}
		else
		{
			status("AI error! Restart!");
		}
	}

	private void playTurnAndSwap(int i, int j, GameState state)
	{
		playTurn(i, j, state);
		swapGameState();
	}

	private void playTurn(int i, int j, GameState state)
	{
		changeSquare(i, j, state);
		passCounter = 0;
		FieldBuilder.printField(squares);
	}

	private Coordinate getStartCoordinate()
	{
		int i = (int) (squares.length / 2.0);
		int j = (int) (squares[i].length / 2.0);
		if (squares[i][j] == GameState.NO)
		{
			return new Coordinate(i, j);
		}
		else if (squares[i - 1][j] == GameState.NO)
		{
			return new Coordinate(i - 1, j);
		}
		else if (squares[i][j - 1] == GameState.NO)
		{
			return new Coordinate(i, j - 1);
		}
		else if (squares[i - 1][j - 1] == GameState.NO)
		{
			return new Coordinate(i - 1, j - 1);
		}
		return null;
	}

	public boolean changeSquare(int index, int index2, boolean vertical, GameState state)
	{
		return vertical ? changeSquare(index, index2, state) : changeSquare(index2, index, state);
	}

	public boolean changeSquare(int i, int j, GameState state)
	{
		points.addPoints(-1, squares[i][j]);
		squares[i][j] = state;
		points.addPoints(1, state);
		updateBoard(i, j, state);
		return true;
	}

	private void swapGameState()
	{
		if (passCounter == 2)
		{
			status("No futher move possible. Game finished!");
			endGame();
			return;
		}
		gameState = swapState(gameState);
		updateState();
		if ((points.getTotalPoints() < 4) && (startConfig == StartConfig.EMPTY))
		{
			if ((numberOfPlayers == 1) && (gameState == ai_state))
			{
				playStartAI();
			}
			return;
		}
		if (PlayToken.checkForMove(this) == null)
		{
			status("No move for " + gameState + " possible.");
			passCounter++;
			swapGameState();
		}
		else if ((numberOfPlayers == 1) && (gameState == ai_state))
		{
			playAI();
		}
	}

	private static GameState swapState(GameState state)
	{
		if (state == GameState.PLAYER1)
		{
			return GameState.PLAYER2;
		}
		else if (state == GameState.PLAYER2)
		{
			return GameState.PLAYER1;
		}
		return state;
	}

	private void updateBoard(int i, int j, GameState state)
	{
		updateBoardSquare(i, j, state);
		updatePoints();
		updateState();
	}

	private void updateState()
	{
		if (board != null)
		{
			board.updateState(gameState);
		}
	}

	private void updatePoints()
	{
		if (board != null)
		{
			board.updatePoints(points);
		}
	}

	private void updateBoardSquare(int i, int j, GameState state)
	{
		if (board != null)
		{
			board.updateSquare(i, j, state);
		}
	}

	private void status(String s, boolean error)
	{
		status(s);
		if (statusLabel != null)
		{
			if (error)
			{
				if (!statusLabel.getStyleClass().contains("error"))
				{
					statusLabel.getStyleClass().add("error");
				}
			}
			else
			{
				statusLabel.getStyleClass().remove("error");
			}
		}
	}

	private void status(String s)
	{
		if (statusLabel != null)
		{
			statusLabel.setText(s);
		}
		System.out.println(s);
		System.out.println();
	}

	private void updateSize(int sizeTB, int sizeLR)
	{
		if (gameState != GameState.NO)
		{
			status("Cannot change playing field size while a game is running!", true);
			return;
		}
		if (sizeTB < 2)
		{
			status("Minimum height is 2!");
			sizeTB = 2;
		}
		if (sizeLR < 2)
		{
			status("Minimum width is 2!");
			sizeLR = 2;
		}
		squares = new GameState[sizeTB][sizeLR];
		points.clearPoints();
		clearGame();
		if (board != null)
		{
			board.updateAllSquares();
		}
	}

	public void changeSquareSize(int N)
	{
		updateSize(N, N);
	}

	public void changeWidth(int width)
	{
		updateSize(squares.length, width);
	}

	public void changeHeight(int height)
	{
		updateSize(height, squares[0].length);
	}

	/**
	 * @param board
	 *            the board to set
	 */
	public void setBoard(Board board)
	{
		this.board = board;
		if (board != null)
		{
			board.updatePoints(points);
			board.updateState(gameState);
		}
	}

	/**
	 * @param statusLabel
	 *            the statusLabel to set
	 */
	public void setStatusLabel(Label statusLabel)
	{
		this.statusLabel = statusLabel;
	}

	/**
	 * @param startConfig
	 *            the startConfig to set
	 */
	public void setStartConfig(StartConfig startConfig)
	{
		this.startConfig = startConfig;
		if (gameState == GameState.NO)
		{
			clearGame();
		}
	}

	/**
	 * @return the startConfig
	 */
	public StartConfig getStartConfig()
	{
		return startConfig;
	}

	/**
	 * @return the squares
	 */
	public GameState[][] getSquares()
	{
		return squares;
	}

	/**
	 * @return the points
	 */
	public Points getPoints()
	{
		return points;
	}

	/**
	 * @param playerNumber
	 *            the playerNumber to set
	 */
	public void setNumberOfPlayers(int playerNumber)
	{
		this.numberOfPlayers = playerNumber;
		checkForAiTurn();
	}

	/**
	 * @return the playerNumber
	 */
	public int getNumberOfPlayers()
	{
		return numberOfPlayers;
	}

	/**
	 * @return the gameState
	 */
	public GameState getGameState()
	{
		return gameState;
	}

	/**
	 * @param turnDuration
	 *            the turnDuration to set
	 */
	public void setTurnDuration(Duration turnDuration)
	{
		this.turnDuration = turnDuration;
	}

	/**
	 * @param ai_state
	 *            the ai_state to set
	 */
	public void setAi_state(GameState ai_state)
	{
		this.ai_state = ai_state;
		checkForAiTurn();
	}

	/**
	 * @return the ai_state
	 */
	public GameState getAi_state()
	{
		return ai_state;
	}

	/**
	 * @return the ai_difficulty
	 */
	public Difficulty getAi_difficulty()
	{
		return ai_difficulty;
	}

	/**
	 * @param ai_difficulty
	 *            the ai_difficulty to set
	 */
	public void setAi_difficulty(Difficulty ai_difficulty)
	{
		this.ai_difficulty = ai_difficulty;
	}

}
