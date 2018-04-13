/**
 * 
 */
package gui;

import javafx.scene.paint.Color;
import logic.GameState;

/**
 * @author AAG
 *
 */
public class ColorMap
{
	public static ColorMap RedGreenMap()
	{
		return new ColorMap(Color.RED, Color.GREEN, "Red", "Green", "highred", "highgreen", "squareYellow");
	}

	public static ColorMap WhiteBlackMap()
	{
		return new ColorMap(Color.WHITE, Color.BLACK, "White", "Black", "highwhite", "highwhite", "squareGreen");
	}

	public static ColorMap BlueYellowMap()
	{
		return new ColorMap(Color.YELLOW, Color.BLUE, "Yellow", "Blue", "highyellow", "highblue", "squareGrey");
	}

	Color player1Color;
	Color player2Color;

	String player1ColorString;
	String player2ColorString;

	String player1HighlightStyle;
	String player2HighlightStyle;

	String squareStyle;

	/**
	 * @param player1Color
	 * @param player2Color
	 * @param player1ColorString
	 * @param player2ColorString
	 * @param player1HighlightStyle
	 * @param player2HighlightStyle
	 * @param squareStyle
	 */
	public ColorMap(Color player1Color, Color player2Color, String player1ColorString, String player2ColorString, String player1HighlightStyle, String player2HighlightStyle, String squareStyle)
	{
		super();
		this.player1Color = player1Color;
		this.player2Color = player2Color;
		this.player1ColorString = player1ColorString;
		this.player2ColorString = player2ColorString;
		this.player1HighlightStyle = player1HighlightStyle;
		this.player2HighlightStyle = player2HighlightStyle;
		this.squareStyle = squareStyle;
	}

	public Color getColor(GameState gs)
	{
		switch (gs)
		{
			case NO:
			{
				return null;
			}
			case PLAYER1:
			{
				return player1Color;
			}
			case PLAYER2:
			{
				return player2Color;
			}
			default:
			{
				return null;
			}
		}
	}

	public String toString(GameState gs)
	{
		switch (gs)
		{
			case NO:
			{
				return "";
			}
			case PLAYER1:
			{
				return player1ColorString;
			}
			case PLAYER2:
			{
				return player2ColorString;
			}
			default:
			{
				return "";
			}
		}
	}

	public String getHighlightStyle(GameState gs)
	{
		switch (gs)
		{
			case NO:
			{
				return "";
			}
			case PLAYER1:
			{
				return player1HighlightStyle;
			}
			case PLAYER2:
			{
				return player2HighlightStyle;
			}
			default:
			{
				return "";
			}
		}
	}

	/**
	 * @return the squareStyle
	 */
	public String getSquareStyle()
	{
		return squareStyle;
	}
}
