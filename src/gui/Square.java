/**
 * 
 */
package gui;

import java.util.LinkedList;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.binding.Bindings;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import logic.GameState;

/**
 * @author AAG
 *
 */
public class Square extends StackPane
{
	private static final String DEFAULT_SQUARE_CSS = "square.css";
	
	private static final String SQUARE_CSS = Othello.getCSSFolder() + DEFAULT_SQUARE_CSS;

	private Circle circle = null;

	private Duration turnDuration;;

	LinkedList<Color> transitionColors = new LinkedList<Color>();

	private boolean transitioning = false;

	/**
	 * @param state
	 */
	public Square(Board board, GameState state, int i, int j, Duration turnDuration)
	{
		super();
		getStylesheets().add(Dialogs.class.getResource(SQUARE_CSS).toExternalForm());
		setMinSize(0, 0);
		setOnMouseClicked(e -> board.squareClicked(i, j));
		this.turnDuration = turnDuration;
		update(state);
	}

	private void updateCircle()
	{
		if (transitionColors.isEmpty())
		{
			return;
		}
		if (transitioning)
		{
			return;
		}
		else
		{
			transitioning = true;
		}
		Color color = transitionColors.poll();
		if (circle != null)
		{
			if (turnDuration.toMillis() > 0)
			{
				ScaleTransition scaleTS = new ScaleTransition(turnDuration.divide(2), circle);
				scaleTS.setFromX(1);
				scaleTS.setToX(0);
				scaleTS.setCycleCount(2);
				scaleTS.setAutoReverse(true);
				FillTransition fillTS = new FillTransition(turnDuration, circle);
				fillTS.setToValue(color);
				ParallelTransition parallelTS = new ParallelTransition();
				parallelTS.getChildren().addAll(scaleTS, fillTS);
				parallelTS.play();
			}
			else
			{
				circle.setFill(color);
			}
		}
		else
		{
			circle = new Circle();
			circle.setFill(color);
			circle.radiusProperty().bind(Bindings.min(widthProperty(), heightProperty()).divide(2).multiply(0.9));
			circle.relocate(0, 0);
			DropShadow dropShadow = new DropShadow();
			dropShadow.setOffsetX(6.0);
			dropShadow.setOffsetY(4.0);
			circle.setEffect(dropShadow);
			getChildren().add(circle);
		}
		PauseTransition wait = new PauseTransition(turnDuration);
		wait.setOnFinished((e) ->
		{
			transitioning = false;
			if (!transitionColors.isEmpty())
			{
				updateCircle();
			}
		});
		wait.play();
	}

	public void update(GameState state)
	{
		switch (state)
		{
			case NO:
			{
				transitionColors.clear();
				getChildren().removeAll(getChildren());
				circle = null;
				return;
			}
			case PLAYER1:
			case PLAYER2:
			{
				transitionColors.add(state.getColor());
				updateCircle();
				return;
			}
			default:
			{
				System.out.println("Missing state! Setting EMPTY...");
				update(GameState.NO);
			}
		}
	}

	/**
	 * @param turnDuration
	 *            the turnDuration to set
	 */
	public void setTurnDuration(Duration turnDuration)
	{
		this.turnDuration = turnDuration;
	}

}
