/**
 * 
 */
package gui;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import logic.GameState;
import logic.Points;

/**
 * @author AAG
 *
 */
public class GameStatePane extends VBox
{
	private Label player1Label;
	private Label pointsPlayer1Label;
	private Label player2Label;
	private Label pointsPlayer2Label;

	private String p1Style;
	private String p2Style;

	/**
	 * 
	 */
	public GameStatePane()
	{
		super();
		Region spacerTop = new Region();
		setVgrow(spacerTop, Priority.ALWAYS);
		Region spacerBottom = new Region();
		setVgrow(spacerBottom, Priority.ALWAYS);
		GridPane gPane = new GridPane();
		gPane.setVgap(10);
		player1Label = new Label(GameState.PLAYER1 + ": ");
		player1Label.setMaxWidth(Double.MAX_VALUE);
		gPane.add(player1Label, 0, 0);
		pointsPlayer1Label = new Label("");
		pointsPlayer1Label.setMaxWidth(Double.MAX_VALUE);
		gPane.add(pointsPlayer1Label, 1, 0);
		player2Label = new Label(GameState.PLAYER2 + ": ");
		player2Label.setMaxWidth(Double.MAX_VALUE);
		gPane.add(player2Label, 0, 1);
		pointsPlayer2Label = new Label("");
		pointsPlayer2Label.setMaxWidth(Double.MAX_VALUE);
		gPane.add(pointsPlayer2Label, 1, 1);
		getChildren().addAll(spacerTop, gPane, spacerBottom);
		this.setMinWidth(100);
		p1Style = GameState.PLAYER1.getHighlightStyle();
		p2Style = GameState.PLAYER2.getHighlightStyle();
	}

	public void colorMapChanged(GameState state)
	{
		highlightPlayer1(p1Style, false);
		highlightPlayer2(p2Style, false);
		player1Label.setText(GameState.PLAYER1 + ": ");
		player2Label.setText(GameState.PLAYER2 + ": ");
		updateState(state);
	}

	public void updateState(GameState state)
	{
		if (state == GameState.PLAYER1)
		{
			p1Style = state.getHighlightStyle();
			if (!player1Label.getStyleClass().contains(p1Style))
			{
				highlightPlayer1(p1Style, true);
			}
			highlightPlayer2(p2Style, false);
		}
		else if (state == GameState.PLAYER2)
		{
			p2Style = state.getHighlightStyle();
			if (!player2Label.getStyleClass().contains(p2Style))
			{
				highlightPlayer2(p2Style, true);
			}
			highlightPlayer1(p1Style, false);
		}
		else if (state == GameState.NO)
		{
			highlightPlayer1(p1Style, false);
			highlightPlayer2(p2Style, false);
		}
	}

	private void highlightPlayer1(String style, boolean highlight)
	{
		if (highlight)
		{
			player1Label.getStyleClass().add(style);
			pointsPlayer1Label.getStyleClass().add(style);
		}
		else
		{
			player1Label.getStyleClass().remove(style);
			pointsPlayer1Label.getStyleClass().remove(style);
		}
	}

	private void highlightPlayer2(String style, boolean highlight)
	{

		if (highlight)
		{
			player2Label.getStyleClass().add(style);
			pointsPlayer2Label.getStyleClass().add(style);
		}
		else
		{
			player2Label.getStyleClass().remove(style);
			pointsPlayer2Label.getStyleClass().remove(style);
		}
	}

	public void updatePoints(Points p)
	{
		pointsPlayer1Label.setText("" + p.getPlayer1Points());
		pointsPlayer2Label.setText("" + p.getPlayer2Points());
	}
}
