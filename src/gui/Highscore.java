/**
 * 
 */
package gui;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.util.Callback;
import logic.GameState;
import logic.Points;

/**
 * @author AAG
 *
 */
public class Highscore
{
	protected static class Player implements Serializable, Comparable<Player>
	{
		private static final long serialVersionUID = 5580095125756494973L;

		private final String name;

		private final int score;

		public Player(String name, int score)
		{
			this.name = name;
			this.score = score;
		}

		public String getName()
		{
			return name;
		}

		public int getScore()
		{
			return score;
		}

		@Override
		public int compareTo(Player p)
		{
			return score - p.getScore();
		}

	}

	private static final Comparator<Player> playerComparator = (p1, p2) -> p2.compareTo(p1); // highest to lowest

	private final static String DEFAULT_TABLE_CSS = "table.css";

	private final static String TABLE_CSS = Othello.getCSSFolder() + DEFAULT_TABLE_CSS;

	private static final String INITIAL_FILENAME = "highscores.rod";
	private String filePath = INITIAL_FILENAME;

	private static int MAX_ENTRIES = 15;

	private ObservableList<Player> data;

	private Player lastAdded = null;

	public Highscore()
	{
		loadData();
	}

	public void showHighscore()
	{
		// Table
		TableView<Player> table = new TableView<Player>();
		TableColumn<Player, Integer> indexColumn = new TableColumn<Player, Integer>("#");
		indexColumn.setCellValueFactory(new Callback<CellDataFeatures<Player, Integer>, ObservableValue<Integer>>()
		{
			@Override
			public ObservableValue<Integer> call(CellDataFeatures<Player, Integer> p)
			{
				return new ReadOnlyObjectWrapper<Integer>((table.getItems().indexOf(p.getValue()) + 1));
			}
		});
		indexColumn.setSortable(false);
		table.getColumns().add(indexColumn);
		TableColumn<Player, String> columnName = new TableColumn<Player, String>("Player");
		columnName.setCellValueFactory(new PropertyValueFactory<Player, String>("name"));
		columnName.setSortable(false);
		table.getColumns().add(columnName);
		TableColumn<Player, Integer> columnScore = new TableColumn<Player, Integer>("Points");
		columnScore.setCellValueFactory(new PropertyValueFactory<Player, Integer>("score"));
		columnScore.setSortable(false);
		table.getColumns().add(columnScore);
		table.getStylesheets().add(Dialogs.class.getResource(TABLE_CSS).toExternalForm());
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setEditable(false);
		table.setPrefWidth(280);
		table.setPrefHeight(500);
		SortedList<Player> sList = new SortedList<Player>(data, playerComparator);
		table.setItems(sList);
		if (lastAdded != null)
		{
			table.getSelectionModel().select(lastAdded);
			lastAdded = null;
		}
		Dialog<Void> dialog = new Dialog<>();
		dialog.setTitle("Highscores");
		dialog.setHeaderText("TOP " + MAX_ENTRIES);
		dialog.initModality(Modality.NONE);
		dialog.setResizable(true);
		DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.getButtonTypes().removeAll(dialog.getDialogPane().getButtonTypes());
		dialogPane.getButtonTypes().add(new ButtonType("Close", ButtonData.OK_DONE));
		Dialogs.styleDialog(dialogPane);
		dialogPane.getStyleClass().add("highscore-dialog");
		dialogPane.setContent(table);
		dialog.show();
	}

	public void gameEnded(Points p)
	{
		GameState winner = p.getLeader();
		if (winner == GameState.NO)
		{
			return;
		}
		SortedList<Player> sList = new SortedList<Player>(data, playerComparator);
		if ((data.size() != MAX_ENTRIES) || (p.getPoints(winner) > sList.get(sList.size() - 1).score))
		{
			if (data.size() == MAX_ENTRIES)
			{
				data.remove(data.size() - 1);
			}
			addHighscore(winner.toString(), p.getPoints(winner));
		}
	}

	private void addHighscore(String player, int score)
	{
		TextInputDialog dialog = new TextInputDialog(player);
		DialogPane dialogPane = dialog.getDialogPane();
		Dialogs.styleDialog(dialogPane);
		dialog.setTitle("Othello / Reversi");
		dialog.setHeaderText(player + " got a new Highscore!");
		dialog.setContentText("Please enter your name:");
		dialog.setOnHidden(e ->
		{
			lastAdded = new Player(dialog.getResult(), score);
			data.add(lastAdded);
			writeData();
			showHighscore();
		});
		dialog.show();
	}

	public void loadFromFile()
	{
		if (chooseFile(false))
		{
			loadData();
		}
	}

	private void writeData()
	{
		try
		{
			File dataFile = new File(filePath);
			dataFile.createNewFile();
			filePath = dataFile.getAbsolutePath();
			FileOutputStream fos = new FileOutputStream(dataFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(new ArrayList<Player>(data));
			oos.close();
			System.out.println("New highscore stored to file " + filePath);
			System.out.println();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			if (dataError("Can't save highscores!", e, true))
			{
				writeData();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void loadData()
	{
		try
		{
			FileInputStream fis = new FileInputStream(filePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			data = FXCollections.observableList((List<Player>) ois.readObject());
			ois.close();
			System.out.println("Highscores loaded from file " + filePath);
			System.out.println();
		}
		catch (EOFException e)
		{
			data = FXCollections.observableArrayList();
		}
		catch (IOException | ClassNotFoundException e)
		{
			if (dataError("Can't load highscores!", e, false))
			{
				loadData();
			}
			else
			{
				data = FXCollections.observableArrayList();
			}
		}
	}

	private boolean dataError(String message, Exception e, boolean save)
	{
		System.out.println(message + "\n" + e);
		System.out.println();
		ButtonType selectFile = new ButtonType(save ? "Select File" : "Open File", ButtonBar.ButtonData.OK_DONE);
		Alert alert = Dialogs.buildAlert(AlertType.ERROR, "Othello / Reversi", message, e.toString(), selectFile, ButtonType.CANCEL);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && (result.get() == selectFile))
		{
			return chooseFile(save);
		}
		return false;
	}

	private boolean chooseFile(boolean save)
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		fileChooser.setInitialFileName(INITIAL_FILENAME);
		File file = null;
		if (save)
		{
			file = fileChooser.showSaveDialog(Othello.getPrimaryStage());
		}
		else
		{
			file = fileChooser.showOpenDialog(Othello.getPrimaryStage());
		}
		if (file != null)
		{
			filePath = file.getAbsolutePath();
			System.out.println("New file path: " + filePath);
			System.out.println();
			return true;
		}
		return false;
	}

}
