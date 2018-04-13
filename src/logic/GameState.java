/**
 * 
 */
package logic;

import gui.ColorMap;
import javafx.scene.paint.Color;

/**
 * @author AAG
 *
 */
public enum GameState
{
	NO(0), PLAYER1(1), PLAYER2(2);

	private static final ColorMap DEFAULT_COLOR_MAP = ColorMap.RedGreenMap();
	private static ColorMap colorMap = DEFAULT_COLOR_MAP;

	public final int state;

	GameState(int state)
	{
		this.state = state;
	}

	public Color getColor()
	{
		return colorMap.getColor(this);
	}

	public String getHighlightStyle()
	{
		return colorMap.getHighlightStyle(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString()
	{
		return colorMap.toString(this);
	}

	/**
	 * @param colorMap
	 *            the colorMap to set
	 */
	public static void setColorMap(ColorMap colorMap)
	{
		GameState.colorMap = colorMap;
	}
	
	public static String getSquareStyle()
	{
		return colorMap.getSquareStyle();
	}

}
