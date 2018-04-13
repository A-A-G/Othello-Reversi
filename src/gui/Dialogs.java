/**
 * 
 */
package gui;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author AAG
 *
 */
public class Dialogs
{
	private static final String DEFAULT_DIALOG_CSS = "dialog.css";
	private static final String DEFAULT_DIALOG_STYLE = "dialog";
	
	private static final String DIALOG_CSS = Othello.getCSSFolder() + DEFAULT_DIALOG_CSS;

	public static Alert buildAlert(AlertType alertType, String title, String headerText, String contentText)
	{
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		DialogPane dialogPane = alert.getDialogPane();
		styleDialog(dialogPane);
		return alert;
	}

	public static Alert buildAlert(AlertType alertType, String title, String headerText, String contentText, ButtonType... buttons)
	{
		Alert alert = Dialogs.buildAlert(alertType, title, headerText, contentText);
		alert.getButtonTypes().removeAll(alert.getButtonTypes());
		alert.getButtonTypes().addAll(buttons);
		return alert;
	}

	public static Alert buildAlert(AlertType alertType, String title, String headerText, String contentText, Modality modality, ButtonType... buttons)
	{
		Alert alert = Dialogs.buildAlert(alertType, title, headerText, contentText, buttons);
		alert.initModality(modality);
		return alert;
	}

	public static void styleDialog(DialogPane dialogPane)
	{
		dialogPane.getStylesheets().add(Dialogs.class.getResource(DIALOG_CSS).toExternalForm());
		dialogPane.getStyleClass().addAll(DEFAULT_DIALOG_STYLE);
		((Stage) dialogPane.getScene().getWindow()).getIcons().add(new Image(Othello.getGameIconPath()));
	}

	public static TextInputDialog buildNumberDialog(String title, String header, String text, int defaultValue)
	{
		TextInputDialog dialog = new TextInputDialog("" + defaultValue);
		DialogPane dialogPane = dialog.getDialogPane();
		TextField content = (TextField) ((GridPane) dialogPane.getContent()).getChildren().get(1);
		content.textProperty().addListener((obs, oldValue, newValue) ->
		{
			if (!newValue.matches("\\d*"))
			{
				content.setText(newValue.replaceAll("[^\\d]", ""));
			}
			if (!content.getText().isEmpty())
			{
				content.getStyleClass().removeAll("error");
			}
		});
		Button btOk = (Button) dialogPane.lookupButton(ButtonType.OK);
		btOk.addEventFilter(ActionEvent.ACTION, e ->
		{
			if (content.getText().isEmpty())
			{
				if (!content.getStyleClass().contains("error"))
				{
					content.getStyleClass().add("error");
				}
				e.consume();
			}
		});
		styleDialog(dialogPane);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(text);
		return dialog;
	}
}
