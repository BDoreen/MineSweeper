package mines;

import java.io.IOException;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MinesFX extends Application {
	private HBox hbox;
	private MineController controller;
	private Mines board;
	private GridPane gridpane;
	private int height = 10, width = 10, mines = 10;
	private Button rstBtn;
	private VBox vbox;
	private TranslateTransition translate;
	boolean transflag = false;
	private Stage stage;

	@Override
	public void start(Stage stage) {
		this.stage = stage;

		initRoot();
		initLogic();
		initBoard();
		initRstBtn();
		vbox = controller.getVBox();
		Scene scene = new Scene(hbox);
		Image icon = new Image("icon.png");
		stage.getIcons().add(icon);
		stage.setTitle("MineMeoper");
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(event -> {
			// pressing X will not interrupt [event](close window)
			// consuming the event will prevent the program
			// from closing the window when hitting that X.
			event.consume();
			exit(stage);
		});
	}

	// create root of program
	private void initRoot() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("myFXML.fxml"));
			hbox = loader.load();
			hbox.setAlignment(Pos.CENTER);
			hbox.setPadding(new Insets(20, 20, 20, 20));
			controller = loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	// create logic
	private void initLogic() {
		board = new Mines(height, width, mines);
		rstBtn = controller.getResetButton();
	}

	// create the gridpane with buttons to play
	private void initBoard() {
		gridpane = new GridPane();
		gridpane.setAlignment(Pos.CENTER);

		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++) {
				GridBtn btn = new GridBtn(i, j);
				gridpane.add(btn.button, j, i);
			}
		hbox.getChildren().add(gridpane);
		stage.sizeToScene();
	}

	// class depicts a button which belongs to the GridPane [gridPane]
	// it sets its coordinates, handler, and graphics(icon, size and disable)
	private class GridBtn {
		private Button button;
		private int x, y;

		// constructor, sets handler and graphics
		public GridBtn(int x, int y) {
			this.x = x;
			this.y = y;
			button = new Button();
			setGraphics();
			button.setOnMouseClicked(new GridHandler());
		}

		// sets icon, size and disable of a gridpane button
		private void setGraphics() {
			String name = getName(board.get(x, y));
			Image im = new Image(getClass().getResourceAsStream(name));
			ImageView imv = new ImageView(im);
			button.setGraphic(imv);
			button.setPrefHeight(imv.getFitHeight());
			button.setPrefWidth(imv.getFitWidth());

			// disable buttons after their clicked
			if (!(board.get(x, y).equals(".") || board.get(x, y).equals("F"))) {
				button.setDisable(true);
				button.setStyle("-fx-opacity: 1");
			}
		}

		// inner class to handle MouseClick events
		// takes of the cases:
		// 1. mouse right click (implement a flag at spot (x,y)
		// 2. mouse left click (open spot (x,y))
		private class GridHandler implements EventHandler<MouseEvent> {

			@Override
			public void handle(MouseEvent e) {
				if (e.getButton() == MouseButton.SECONDARY)
					board.toggleFlag(x, y);
				// if opens a mines
				else if (!(board.open(x, y))) {
					board.setShowAll(true);
					resetTranslate();
					PopMsg("HISS", "LOOSER!\nDARE YOU TO TRY AGAIN", "/loose.jpeg");
					transflag = true;
				}
				// if win the game
				else if (board.isDone()) {
					board.setShowAll(true);
					resetTranslate();
					PopMsg("WINNER", "WINNER!\nCAN'T BELIEVE YOU WON MY PAWSOME CAT!", "/win.png");
					transflag = true;
				}
				// after every new mouse click, initial new board.
				gridpane.getChildren().clear();
				initBoard();
			}
		}
	}

	private void initRstBtn() {

		// handler of reset button
		class RstBtnHandler implements EventHandler<MouseEvent> {

			// constructor - get current values of height,width,mines
			public RstBtnHandler() {
				height = controller.getHeight2();
				width = controller.getWidth2();
				mines = controller.getMines2();
			}

			@Override
			public void handle(MouseEvent event) {
				controller.getHeight(new ActionEvent());
				height = controller.getHeight2();

				controller.getWidth(new ActionEvent());
				width = controller.getWidth2();

				controller.getMines(new ActionEvent());
				mines = controller.getMines2();

				board = new Mines(height, width, mines);
				gridpane.getChildren().clear();

				// remove green button
				if (transflag) {
					vbox.getChildren().remove(vbox.getChildren().size() - 1);
					transflag = false;
				}
				// initial board after every mouse click on the reset button
				initBoard();
			}
		}
		// set the handler
		rstBtn.setOnMouseClicked(new RstBtnHandler());
	}

	private String getName(String get) {
		if (get.equals("."))
			return "/dot.png";
		else if (get.equals(" "))
			return "/empty.png";
		else
			return "/" + get + ".png";
	}

	// animation arrow.
	// it moves from and away of the [resetbtn],
	// implicating to the player to click reset
	private void resetTranslate() {
		Image im = new Image(getClass().getResourceAsStream("/clickHere.png"));
		ImageView imv = new ImageView(im);
		imv.setRotate(180);

		translate = new TranslateTransition();
		translate.setNode(imv);
		translate.setDuration(Duration.millis(600));
		translate.setCycleCount(TranslateTransition.INDEFINITE);
		translate.setByY(170);
		translate.setAutoReverse(true);
		vbox.getChildren().add(imv);
		translate.play();
	}

	// pops an Alert if the player won or lost
	private void PopMsg(String title, String header, String image) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		Image im = new Image(getClass().getResourceAsStream(image));
		ImageView imv = new ImageView(im);
		imv.setFitWidth(350);
		imv.setFitHeight(200);
		alert.setGraphic(imv);
		alert.show();
	}

	// warning the user that it exits the game.
	private void exit(Stage stage) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("HISS");
		alert.setHeaderText("So, you are giving up huh? loser..");
		if (alert.showAndWait().get() == ButtonType.OK)
			stage.close();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
