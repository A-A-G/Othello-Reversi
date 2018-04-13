/**
 * 
 */
package gui;

import java.util.Optional;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.AiDifficulty.Difficulty;
import logic.GameState;
import logic.PlayingField;
import logic.PlayingField.StartConfig;
import logic.Points;

/**
 * @author AAG
 *
 */
public class Board extends GridPane
{
	private final static Duration DEFAULT_TURN_DURATION = Duration.millis(1000);

	private Duration turnDuration = DEFAULT_TURN_DURATION;

	private Square[][] squares = null;

	private PlayingField playingField = null;

	private GameStatePane gameStatePane = null;

	private Highscore highscore = new Highscore();

	RadioMenuItem aiColor1Item = null;

	RadioMenuItem aiColor2Item = null;

	/**
	 * @param game
	 * @param playingField
	 */
	public Board(Label statusLabel, MenuBar menuBar, ToolBar toolbar, GameStatePane gameStatePane, PlayingField playingField)
	{
		super();
		this.gameStatePane = gameStatePane;
		assert playingField == null : "invalid playing field (squares not initialized)";
		logic.GameState[][] field = playingField.getSquares();
		assert field == null : "invalid playing field (squares not initialized)";
		this.playingField = playingField;
		playingField.setTurnDuration(turnDuration);
		playingField.setBoard(this);
		playingField.setStatusLabel(statusLabel);
		createMenu(menuBar, playingField);
		updateAllSquares();
	}

	private void createMenu(MenuBar menuBar, PlayingField playingField)
	{
		Menu game = new Menu("Game");
		MenuItem startGame = new MenuItem("Start");
		MenuItem restartGame = new MenuItem("Restart");
		MenuItem endGame = new MenuItem("End");
		startGame.setOnAction(e ->
		{
			playingField.startGame();
			startGame.setDisable(true);
			restartGame.setDisable(false);
			endGame.setDisable(false);
		});
		restartGame.setDisable(true);
		restartGame.setOnAction(e ->
		{
			playingField.endGame();
			endGame.setDisable(false);
			playingField.clearGame();
			playingField.startGame();
		});
		endGame.setDisable(true);
		endGame.setOnAction(e ->
		{
			playingField.endGame();
			endGame.setDisable(true);
		});
		MenuItem clearGame = new MenuItem("Clear");
		clearGame.setOnAction(e ->
		{
			playingField.endGame();
			playingField.clearGame();
			startGame.setDisable(false);
			restartGame.setDisable(true);
			endGame.setDisable(true);
		});
		MenuItem exitGame = new MenuItem("Exit");
		exitGame.setOnAction(e -> Platform.exit());
		game.getItems().addAll(startGame, restartGame, endGame, clearGame, new SeparatorMenuItem(), exitGame);
		Menu settings = new Menu("Settings");
		Menu playerSettings = new Menu("Player");
		ToggleGroup playerGroup = new ToggleGroup();
		// @formatter:off
		playerSettings.getItems().addAll(createToggleItem(playerGroup, "One Player", playingField.getNumberOfPlayers() == 1, pf->pf.setNumberOfPlayers(1)),
				createToggleItem(playerGroup, "Two Player", playingField.getNumberOfPlayers() == 2, pf-> pf.setNumberOfPlayers(2))); // @formatter:on
		Menu aiDifficultySettings = new Menu("AI Difficulty");
		ToggleGroup aiDifficultyGroup = new ToggleGroup();
		// @formatter:off
		MenuItem aiDifficulty1Item = createToggleItem(aiDifficultyGroup, Difficulty.EASY.toString(), playingField.getAi_difficulty() == Difficulty.EASY, pf-> pf.setAi_difficulty(Difficulty.EASY));
		MenuItem aiDifficulty2Item = createToggleItem(aiDifficultyGroup, Difficulty.HARD.toString(), playingField.getAi_difficulty() == Difficulty.HARD, pf-> pf.setAi_difficulty(Difficulty.HARD));
		aiDifficultySettings.getItems().addAll(aiDifficulty1Item, aiDifficulty2Item);
		Menu aiColorSettings = new Menu("AI Color");
		ToggleGroup aiColorGroup = new ToggleGroup();
		// @formatter:off
		aiColor1Item = createToggleItem(aiColorGroup, GameState.PLAYER1.toString(), playingField.getAi_state() == GameState.PLAYER1, pf-> pf.setAi_state(GameState.PLAYER1));
		aiColor2Item = createToggleItem(aiColorGroup, GameState.PLAYER2.toString(), playingField.getAi_state() == GameState.PLAYER2, pf-> pf.setAi_state(GameState.PLAYER2));
		aiColorSettings.getItems().addAll(aiColor1Item, aiColor2Item);
		Menu startSettings = new Menu("Start Configuration"); // @formatter:on
		ToggleGroup startGroup = new ToggleGroup();
		// @formatter:off
		startSettings.getItems().addAll(createToggleItem(startGroup, StartConfig.EMPTY.toString(), playingField.getStartConfig() == StartConfig.EMPTY, pf-> pf.setStartConfig(StartConfig.EMPTY)),
				createToggleItem(startGroup, StartConfig.CROSS.toString(), playingField.getStartConfig() == StartConfig.CROSS, pf-> pf.setStartConfig(StartConfig.CROSS)),
				createToggleItem(startGroup, StartConfig.PARALLEL.toString(), playingField.getStartConfig() == StartConfig.PARALLEL, pf-> pf.setStartConfig(StartConfig.PARALLEL))); 
		// @formatter:on
		Menu sizeSettings = new Menu("Playfield Size"); // @formatter:on
		MenuItem squareSizeItem = new MenuItem("Set NxN");
		squareSizeItem.setOnAction(e -> changeSquareSize());
		MenuItem widthSizeItem = new MenuItem("Set Width");
		widthSizeItem.setOnAction(e -> changeWidth());
		MenuItem heightSizeItem = new MenuItem("Set Height");
		heightSizeItem.setOnAction(e -> changeHeight());
		sizeSettings.getItems().addAll(squareSizeItem, widthSizeItem, heightSizeItem);
		Menu colorSettings = new Menu("Color Map"); // @formatter:on
		MenuItem redGreenItem = new MenuItem("Red / Green");
		redGreenItem.setOnAction(e -> colorMapChanged(ColorMap.RedGreenMap()));
		MenuItem whiteBlackItem = new MenuItem("White / Black");
		whiteBlackItem.setOnAction(e -> colorMapChanged(ColorMap.WhiteBlackMap()));
		MenuItem yellowBlueItem = new MenuItem("Yellow / Blue");
		yellowBlueItem.setOnAction(e -> colorMapChanged(ColorMap.BlueYellowMap()));
		colorSettings.getItems().addAll(redGreenItem, whiteBlackItem, yellowBlueItem);
		MenuItem turnDurationItem = new MenuItem("Turn Duration");
		turnDurationItem.setOnAction(e -> changeTurnDuration());
		settings.getItems().addAll(playerSettings, aiDifficultySettings, aiColorSettings, startSettings, sizeSettings, colorSettings, turnDurationItem);
		Menu highscoreMenu = new Menu("Highscore");
		MenuItem showHighscore = new MenuItem("Show Highscore");
		showHighscore.setOnAction(e -> highscore.showHighscore());
		MenuItem loadHighscore = new MenuItem("Load Highscores...");
		loadHighscore.setOnAction(e -> highscore.loadFromFile());
		highscoreMenu.getItems().addAll(showHighscore, loadHighscore);
		Menu help = new Menu("Help");
		MenuItem info = new MenuItem("Info");
		info.setOnAction(e -> showInfo());
		MenuItem gameRules = new MenuItem("Rules");
		gameRules.setOnAction(e -> showRules());
		help.getItems().addAll(info, gameRules);
		menuBar.getMenus().addAll(game, settings, highscoreMenu, help);
	}

	private RadioMenuItem createToggleItem(ToggleGroup tg, String label, boolean selected, Consumer<PlayingField> func)
	{
		RadioMenuItem radioItem = new RadioMenuItem(label);
		radioItem.setSelected(selected);
		radioItem.setOnAction(e -> func.accept(playingField));
		radioItem.setToggleGroup(tg);
		return radioItem;
	}

	public void gameEndPoints(Points p)
	{
		highscore.gameEnded(p);
	}

	public void updatePoints(Points p)
	{
		if (gameStatePane != null)
		{
			gameStatePane.updatePoints(p);
		}
	}

	public void updateState(GameState state)
	{
		if (gameStatePane != null)
		{
			gameStatePane.updateState(state);
		}
	}

	public void updateSquare(int i, int j, GameState state)
	{
		if ((squares != null) && (i < squares.length) && (j < squares[i].length))
		{
			squares[i][j].update(state);
		}
		else
		{
			System.out.println("Cannot update square at position i = " + i + " and j = " + j + " .");
		}
	}

	public void updateAllSquares()
	{
		this.getChildren().clear();
		GameState[][] field = playingField.getSquares();
		squares = new Square[field.length][field[0].length];
		for (int i = 0; i < field.length; i++)
		{
			for (int j = 0; j < field[i].length; j++)
			{
				squares[i][j] = new Square(this, field[i][j], i, j, turnDuration);
				squares[i][j].getStyleClass().add(GameState.getSquareStyle());
				squares[i][j].prefWidthProperty().bind(widthProperty().divide(field[i].length));
				squares[i][j].prefHeightProperty().bind(heightProperty().divide(field.length));
				this.add(squares[i][j], j, i);
			}
		}
	}

	public void squareClicked(int i, int j)
	{
		playingField.squareClicked(i, j);
	}

	public void updateTurnDuration(Duration duration)
	{
		turnDuration = duration;
		if (squares != null)
		{
			for (int i = 0; i < squares.length; i++)
			{
				for (int j = 0; j < squares[i].length; j++)
				{
					squares[i][j].setTurnDuration(turnDuration);
				}
			}
		}
		playingField.setTurnDuration(turnDuration);
	}

	private void colorMapChanged(ColorMap map)
	{
		GameState.setColorMap(map);
		aiColor1Item.setText(GameState.PLAYER1.toString());
		aiColor2Item.setText(GameState.PLAYER2.toString());
		updateAllSquares();
		if (gameStatePane != null)
		{
			gameStatePane.colorMapChanged(playingField.getGameState());
		}
	}

	private void changeSquareSize()
	{
		TextInputDialog dialog = Dialogs.buildNumberDialog("Othello / Reversi", "Change size", "Enter new size N (NxN):", squares.length);
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent())
		{
			playingField.changeSquareSize(Integer.parseInt(result.get()));
		}
	}

	private void changeWidth()
	{
		TextInputDialog dialog = Dialogs.buildNumberDialog("Othello / Reversi", "Change size", "Enter new twidth:", squares.length);
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent())
		{
			playingField.changeWidth(Integer.parseInt(result.get()));
		}
	}

	private void changeHeight()
	{
		TextInputDialog dialog = Dialogs.buildNumberDialog("Othello / Reversi", "Change size", "Enter new height:", squares[0].length);
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent())
		{
			playingField.changeHeight(Integer.parseInt(result.get()));
		}
	}

	private void changeTurnDuration()
	{
		TextInputDialog dialog = Dialogs.buildNumberDialog("Othello / Reversi", "Update Turn Duration", "Enter new turn duration (ms):", (int) turnDuration.toMillis());
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent())
		{
			updateTurnDuration(Duration.millis(Integer.parseInt(result.get())));
		}
	}

	private void showInfo()
	{
		Dialogs.buildAlert(AlertType.INFORMATION, "Othello / Reversi", "Othello / Reversi by AAG", "Version 0.1 \n\n mailto: alexander@artigagonzalez.de", Modality.NONE, new ButtonType("Close", ButtonData.OK_DONE)).show();
	}

	private void showRules()
	{
		//@formatter:off
		String rules = 	"A move consists of \"outflanking\" your opponent's disc(s), then flipping the outflanked disc(s) to your colour. " + "To outflank means to place a disc on the board so that your opponent's row (or rows) of disc(s) is bordered at each end by a disc of your colour. (A \"row\" may be made up of one or more discs).\n\n" +
						"If on your turn you cannot outflank and flip at least one opposing disc, your turn is forfeited and your opponent moves again. However, if a move is available to you, you may not forfeit your turn.\n\n" +
						"A disc may outflank any number of discs in one or more rows in any number of directions at the same time - horizontally, vertically or diagonally. (A row is defined as one or more discs in a contiuous straight line ).\n\n" +
						"You may not skip over your own colour disc to outflank an opposing disc.\n\n" +
						"Discs may only be outflanked as a direct result of a move and must fall in the direct line of the disc placed down.\n\n" +
						"All discs outflanked in any one move must be flipped, even if it is to the player's advantage not to flip them at all.\n\n" +
						"When it is no longer possible for either player to move, the game is over. Discs are counted and the player with the majority of his or her colour discs on the board is the winner.\n\n" +
						"NOTE: It is possible for a game to end before all 64 squares are filled.\n" +
						"\n" +
						"Source: http://www.hannu.se/games/othello/rules.htm";	//@formatter:on
		Dialog<Void> dialog = new Dialog<>();
		dialog.setTitle("Othello / Reversi");
		dialog.setHeaderText("Rules");
		dialog.setContentText(rules);
		dialog.initModality(Modality.NONE);
		dialog.setResizable(true);
		DialogPane dialogPane = dialog.getDialogPane();
		Dialogs.styleDialog(dialogPane);
		dialogPane.setMinHeight(Region.USE_PREF_SIZE);
		dialogPane.getStyleClass().add("rule-dialog");
		dialogPane.getButtonTypes().removeAll(dialog.getDialogPane().getButtonTypes());
		dialogPane.getButtonTypes().add(new ButtonType("Close", ButtonData.OK_DONE));
		ButtonType videoButtonType = new ButtonType("Show video...", ButtonData.HELP_2);
		dialogPane.getButtonTypes().add(videoButtonType);
		Button videoButton = (Button) dialogPane.lookupButton(videoButtonType);
		videoButton.addEventFilter(ActionEvent.ACTION, e ->
		{
			e.consume();
			Stage videoStage = new Stage();
			videoStage.setTitle("Othello / Reversi");
			videoStage.centerOnScreen();
			videoStage.getIcons().add(new Image(Othello.getGameIconPath()));
			WebView webview = new WebView();
			webview.getEngine().load("https://www.youtube.com/embed/Ol3Id7xYsY4?autoplay=1&t=31s");
			videoStage.setScene(new Scene(webview));
			videoStage.show();
			videoStage.setOnCloseRequest(event -> webview.getEngine().load(null));
		});
		dialog.show();
	}
}
