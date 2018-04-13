package gui;

import javafx.application.Application;
import javafx.stage.Stage;
import logic.PlayingField;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * @author AAG
 *
 */
public class Othello extends Application
{
	private final static int SIZE = 8; // default playing field size 8x8
	
	private final static String CSS_FOLDER = "/css/";
	
	public static String getCSSFolder()
	{
		return CSS_FOLDER;
	}

	private final static String IMAGE_FOLDER = "/images/";
	
	private final static String GAME_ICON = "app_icon.png";
	
	public static String getGameIconPath()
	{
		return Othello.class.getResource(IMAGE_FOLDER + GAME_ICON).toExternalForm();
	}
	
	private final static String DEFAULT_APPLICATION_CSS = "application.css";

	private static Stage PRIMARY_STAGE = null;

	public static Stage getPrimaryStage()
	{
		return PRIMARY_STAGE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			Othello.PRIMARY_STAGE = primaryStage;
			BorderPane borderPane = new BorderPane();
			VBox topBars = new VBox();
			MenuBar menuBar = new MenuBar();
			topBars.getChildren().add(menuBar);
			ToolBar toolBar = new ToolBar();
			topBars.getChildren().add(toolBar);
			borderPane.setTop(topBars);
			Label statusbar = new Label("");
			statusbar.setPrefWidth(Double.MAX_VALUE);
			borderPane.setBottom(statusbar);
			GameStatePane gameStatePane = new GameStatePane();
			borderPane.setRight(gameStatePane);
			Board board = new Board(statusbar, menuBar, toolBar, gameStatePane, new PlayingField(SIZE));
			borderPane.setCenter(board);
			Scene scene = new Scene(borderPane, 800, 800);
			scene.getStylesheets().add(getClass().getResource(getCSSFolder() + DEFAULT_APPLICATION_CSS).toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Othello / Reversi");
			primaryStage.getIcons().add(new Image(getGameIconPath()));
			primaryStage.show();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
