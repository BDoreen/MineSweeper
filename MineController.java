package mines;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class MineController {
	@FXML
	private TextField heightText;

	@FXML
	private TextField minesText;

	@FXML
	private Button restBtn;

	@FXML
	private TextField widthText;
	
	@FXML VBox VBoxcontainer;
	
	private int height=10,width=10,mines=10;
	

	@FXML
	void getHeight(ActionEvent e) {
		height = Integer.parseInt(heightText.getText());
	}
	
	public int getHeight2() {
		return height;
	}

	@FXML
	void getMines(ActionEvent event) {
		mines = Integer.parseInt(minesText.getText());
	}
	
	int getMines2() {
		return mines;
	}

	@FXML
	void getWidth(ActionEvent event) {
		width = Integer.parseInt(widthText.getText());
	}
	
	public int getWidth2() {
		return width;
	}
	
	@FXML
	void restBtn(ActionEvent event) {
	}
	
	Button getResetButton() {
		return restBtn;
	}	
	
	VBox getVBox() {
		return VBoxcontainer;
	}
}
