package com.amir.battleship;

/*

The class provides link between fxml and classes for the
play game page.

 */

import com.amir.battleship.datamdodel.AI;
import com.amir.battleship.datamdodel.Ship;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.scene.Node;

import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.*;


public class Controller {


    private boolean nameIsPresent = false;
    private AI ai = new AI();
    private int player = 0, enemy = 0, gameNo = 0, playerTotal = 0, enemyTotal = 0;
    private Random random = new Random();
    private static Map<Ship, HBox> shipList = new HashMap<>();
    private static int shipsAddedNo = 0; // checks if all the ships added to the player board so that the game can be started


    @FXML
    private GridPane playerBoard;
    @FXML
    private GridPane enemyBoard;

    @FXML
    private GridPane shipModelGrid;

    @FXML
    private HBox carrier;
    @FXML
    private HBox battleship;
    @FXML
    private HBox submarine;
    @FXML
    private HBox cruiser;
    @FXML
    private HBox destroyer;
    @FXML
    private TextArea noticeBoard;


    @FXML
    private Label playerScore;
    @FXML
    private Label enemyScore;
    @FXML
    private Label instructionLabel;

    @FXML
    private Button startGameBtn;
    @FXML
    private Button resetBtn;

    @FXML
    private Label playerNameLbl;
    @FXML
    private Label gamePlayedLbl;
    @FXML
    private Label playerTotalLbl;
    @FXML
    private Label enemyTotalLbl;


    @FXML
    public void initialize() {

        noticeBoard.clear();
        gamePlayedLbl.setText(gameNo + " has been played so far.");
        playerTotalLbl.setText(Integer.toString(playerTotal));
        enemyTotalLbl.setText(Integer.toString(enemyTotal));
        if (!nameIsPresent) createPlayerInf();
        StringBuilder sb = new StringBuilder();
        sb.append("Welcome to Battleship Game....\n\n");
        sb.append("This is the instruction of the game:\n");
        sb.append("*You need too add each ship to your board!\n");
        sb.append("*Each ship has specific size which shown below:\n");
        sb.append("*for vertical->primary mouse click\n");
        sb.append("*For horizontal->secondary mouse click\n");
        sb.append("\n****** Now It is time to select the ships\n and add it to the board ******\n");
        noticeBoard.setText(sb.toString());
        noticeBoard.setEditable(false);
        addShips();
        startGameBtn.setDisable(true);
        enemyBoard.setDisable(true);
    }

    /*
     which with key -> ship and value -> the corresponding Hbox in the UI
     */


    private void addShips() {

        Ship cruiserS = Ship.createShip("cruiser", 3);
        Ship destroyerS = Ship.createShip("destroyer", 2);
        Ship battleshipS = Ship.createShip("battleship", 4);
        Ship carrierS = Ship.createShip("carrier", 5);
        Ship submarineS = Ship.createShip("submarine", 3);

        shipList = new HashMap<>();

        shipList.put(carrierS, carrier);
        shipList.put(destroyerS, destroyer);
        shipList.put(battleshipS, battleship);
        shipList.put(submarineS, submarine);
        shipList.put(cruiserS, cruiser);

    }

    @FXML
    private void selectShipModel(MouseEvent event) {

        String shipName = event.getPickResult().getIntersectedNode().getParent().getId();
        playerBoard.setDisable(false);

        for (Ship ship : shipList.keySet()) {
            if (ship.getName().equals(shipName)) {
                selectShipModel(shipList.get(ship));
                successfullyAddedToBoard(ship);
            }
        }
    }


    private void selectShipModel(HBox model) {

        ObservableList<Node> hBoxList = shipModelGrid.getChildren();

        for (Node node : hBoxList) {
            HBox hBox = (HBox) node;
            for (Node n : hBox.getChildren()) {
                if (n instanceof Rectangle) {
                    ((Rectangle) n).setFill(Color.DODGERBLUE);
                }
            }
        }

        for (Node node : model.getChildren()) {
            if (node instanceof Rectangle) {
                ((Rectangle) node).setFill(Color.RED);
            }
        }
    }


    /*

    Adds ship to the board. if primary mouse button is clicked
    places the ship horizontally and if the mouse button is
    secondary, the is ship is positioned vertically.

     */

    @FXML
    private void successfullyAddedToBoard(Ship ship) {

        EventHandler<MouseEvent> eventHandler = event -> {

            // when the ship is places successfully, it gets disabled
            if (placeShipsOnBoard(ship, event, playerBoard)) shipList.get(ship).setDisable(true);

        };
        playerBoard.setOnMouseClicked(eventHandler);
    }


    @FXML
    private void handleButtonClick(ActionEvent event) {

        if (event.getSource().equals(startGameBtn)) {
            createEnemyBoard();
            /*
            To check if the enemy board has been created right
             */
            for (Node node : enemyBoard.getChildren()) {
                String value = node.getId();
                if (value == "notPermitted") ((Rectangle) node).setFill(Color.GREEN);
                else if (value == null) ((Rectangle) node).setFill(Color.PURPLE);
                else ((Rectangle) node).setFill(Color.LIGHTBLUE);

            }
            startGameBtn.setDisable(true);
            playerBoard.setDisable(true);
        }
        if (event.getSource().equals(resetBtn)) {
            reset();
        }

    }

    /*

    Creates a random-based enemy board after clicking start button

     */

    private void createEnemyBoard() {


        enemyBoard.setDisable(false);
        int x = random.nextInt(10);
        int y = random.nextInt(10);

        addShips();
        for (Ship ship : shipList.keySet()) {
            ship.setStartX(x);
            ship.setStartY(y);
            while (!placeShipsOnBoard(ship, null, enemyBoard)) {

                x = random.nextInt(10);
                y = random.nextInt(10);
                ship.setStartX(x);
                ship.setStartY(y);
            }

        }

    }

    /*

    It creates the player and the enemy board.

     */

    private boolean placeShipsOnBoard(Ship ship, MouseEvent event, GridPane board) {

        int index = ship.getSize();
        boolean isHorizontal;


        if (board == playerBoard) { // to place the ships on the player board
            if (!ship.isAdded()) {
                Rectangle rectangle = (Rectangle) event.getPickResult().getIntersectedNode();
                int x = GridPane.getRowIndex(rectangle);
                int y = GridPane.getColumnIndex(rectangle);
                isHorizontal = event.getButton().equals(MouseButton.PRIMARY);
                if (checkSpace(x, y, ship.getSize(), isHorizontal, playerBoard)) {
                    ship.setAdded(true);
                    shipsAddedNo++;
                    noticeBoard.appendText("\n*" + ship.getName().toUpperCase() + "  has been added.*\n");
                    for (Node node : playerBoard.getChildren()) {
                        if (index > 0)
                            if ((GridPane.getColumnIndex(node) == y) && (GridPane.getRowIndex(node) == x)) {
                                ((Rectangle) node).setFill(Color.RED);
                                node.setId(ship.getName());
                                createBoundaries(x, y, playerBoard);
                                index--;
                                if (isHorizontal)
                                    y++;
                                else
                                    x++;

                            }
                    }

                } else return false;

            }
            if (shipsAddedNo == 5) { // in case all the ships are placed, the selection mode will be disabled
                startGameBtn.setDisable(false);
                shipModelGrid.setDisable(true);
                noticeBoard.clear();
                noticeBoard.setText("Good Job! All the ships are placed successfully\n");
                noticeBoard.setText("*********************************************" +
                        "\nPress start button To create Enemy's Board");
                instructionLabel.setText("Online Result");

            }

            return true;
        } else { // to place the ship in enemy's board
            int x = ship.getStartX();
            int y = ship.getStartY();
            isHorizontal = random.nextBoolean();

            if (checkSpace(x, y, ship.getSize(), isHorizontal, enemyBoard)) {
                ship.setAdded(true);

                for (Node node : enemyBoard.getChildren())
                    if (index > 0)
                        if ((GridPane.getColumnIndex(node) == y) && (GridPane.getRowIndex(node) == x)) {
                            node.setId(ship.getName());
                            createBoundaries(x, y, enemyBoard);
                            index--;
                            if (isHorizontal)
                                y++;
                            else
                                x++;
                        }

                return true;
            } else {

                return false;

            }

        }

    }

    /*

    Start to hit the enemy board an in return the
    enemy attack the player board

     */
    @FXML

    private void hitEnemy(MouseEvent event) {


        boolean bonus = false; // in case the right cell has been hit, there will be bonus

        Rectangle hitRectangle = (Rectangle) event.getPickResult().getIntersectedNode();
        int x = GridPane.getRowIndex(hitRectangle);
        int y = GridPane.getColumnIndex(hitRectangle);
        for (Node node : enemyBoard.getChildren()) {
            if (GridPane.getRowIndex(node) == x & GridPane.getColumnIndex(node) == y) {

                String value = node.getId();
                if ((value != null) && !node.getId().equals("notPermitted")) {
                    bonus = true;
                    ((Rectangle) node).setFill(Color.RED);
                    node.setId("notPermitted"); // the already pointed cell can not be pointed again
                    noticeBoard.appendText("\nYessss! You hit a right place.");
                    noticeBoard.appendText("\nBonus ! You can hit once more.");
                    player++;
                    playerScore.setText(Integer.toString(player));
                    if (player == 17) {
                        showWinner(player);
                    }

                } else if (((Rectangle) node).getFill().equals(Color.BLACK))
                    bonus = true; // if the user click already chosen black cell
                else if (((Rectangle) node).getFill().equals(Color.RED))
                    bonus = true; // if the user click already chosen red cell
                else {
                    ((Rectangle) node).setFill(Color.BLACK);
                    noticeBoard.appendText("\nPlayer->Unfortunately, It was a miss...");
                }

            }
        }
        if (!bonus) {
            createEnemyMove();

        }

    }

    /*

    Enemy can attack the player board

     */
    private void createEnemyMove() {

        ai.createARandom();

        int x = ai.getX();
        int y = ai.getY();

        for (Node node : playerBoard.getChildren()) { // if the enemy attacks the right cell
            if (GridPane.getRowIndex(node) == x & GridPane.getColumnIndex(node) == y) {
                if (((Rectangle) node).getFill().equals(Color.RED)) {
                    ((Rectangle) node).setFill(Color.BLACK);
                    enemy++;
                    if (enemy == 17) showWinner(enemy);
                    enemyScore.setText(Integer.toString(enemy));
                    noticeBoard.appendText("\nOHH The enemy fired the right place.");
                    noticeBoard.appendText("\nBonus: The enemy can hit once more.");


                    ai.setFound(true);
                    createEnemyMove();


                } else if (((Rectangle) node).getFill().equals(Color.BLACK)) { // if the random number already chosen
                    ai.setFound(false);
                    createEnemyMove();

                } else { // if the enemy attacks the wrong cell
                    ((Rectangle) node).setFill(Color.BLACK);
                    ai.setFound(false);
                    noticeBoard.appendText("\nEnemy->Luckily, it was a miss...");

                }

            }
        }
    }

    /*
    show the winner and asks the player if he wants to replay or exit
     */

    private void showWinner(int point) {

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        gameNo++;
        if (point == enemy) {
            alert.setHeaderText("Enemy Won");
            alert.setContentText("The Game Has been finished.Better Luck Next Time.Replay?");
            noticeBoard.setText("Unfortunately you lost the game.");
            enemyTotal++;
        } else {
            alert.setHeaderText("Player Won");
            alert.setContentText("Congratulation.You Won the Game.Replay?");
            noticeBoard.setText("Great....You won the game.");
            playerTotal++;
        }
        playerBoard.setDisable(true);
        enemyBoard.setDisable(true);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            reset();
        } else {
            System.exit(-1);
        }
    }

    /*

    check the boarder of each board and the forbidden
    cells for a cell to be positioned

     */

    private boolean checkSpace(int x, int y, int shipSize, boolean isHorizontal, GridPane board) {

        if (isHorizontal) {
            if ((y + shipSize) > 10)
                return false;
        } else {
            if ((x + shipSize) > 10)
                return false;
        }
        if (board == playerBoard) {
            for (Node node : playerBoard.getChildren()) {
                if (isHorizontal) {

                    for (int i = 0; i < shipSize; i++) { // in proportion of ship size checks if all the cells are available horizontally
                        if (GridPane.getRowIndex(node) == x && GridPane.getColumnIndex(node) == y + i) {
                            if (((Rectangle) node).getFill().equals(Color.LIGHTGRAY) ||
                                    ((Rectangle) node).getFill().equals(Color.RED)) return false;
                        }
                    }
                } else {
                    for (int i = 0; i < shipSize; i++) { // in proportion of ship size checks if all the cells are available vertically
                        if (GridPane.getRowIndex(node) == x + i && GridPane.getColumnIndex(node) == y) {
                            if (((Rectangle) node).getFill().equals(Color.LIGHTGRAY) ||
                                    ((Rectangle) node).getFill().equals(Color.RED)) return false;
                        }
                    }
                }
            }
            return true;
        } else {
            for (Node node : enemyBoard.getChildren())
                if (isHorizontal) {
                    for (int i = 0; i < shipSize; i++) {
                        if (GridPane.getRowIndex(node) == x && GridPane.getColumnIndex(node) == y + i) {
                            String value = node.getId();
                            if (value != null) {
                                return false;
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < shipSize; i++) {
                        if (GridPane.getRowIndex(node) == x + i && GridPane.getColumnIndex(node) == y) {
                            String value = node.getId();
                            if (value != null) {
                                return false;
                            }
                        }
                    }
                }
            return true;
        }

    }


    /*

    Creates limit around each ship so that the
    new ship can not be added to its boundaries.
     */
    private void createBoundaries(int x, int y, GridPane board) {

        createLimitation(x + 1, y, board);
        createLimitation(x - 1, y, board);
        createLimitation(x, y - 1, board);
        createLimitation(x, y + 1, board);
        createLimitation(x - 1, y - 1, board);
        createLimitation(x + 1, y + 1, board);
        createLimitation(x + 1, y - 1, board);
        createLimitation(x - 1, y + 1, board);

    }

    private void createLimitation(int x, int y, GridPane board) {

        if (board == playerBoard) {
            for (Node node : playerBoard.getChildren()) {
                if ((GridPane.getRowIndex(node) == x && GridPane.getColumnIndex(node) == y)) {
                    if (!((Rectangle) node).getFill().equals(Color.RED))
                        ((Rectangle) node).setFill(Color.LIGHTGRAY);
                }
            }
        } else {
            for (Node node : enemyBoard.getChildren()) {
                if ((GridPane.getRowIndex(node) == x && GridPane.getColumnIndex(node) == y)) {
                    String value = node.getId();
                    if ((value == null)) {
                        node.setId("notPermitted");
                    }
                }
            }
        }
    }

    /*

    Functionality for reset button -
    recreates boards and initialize the
    needed variables
     */
    private void reset() {
        initialize();

        for (Node node : playerBoard.getChildren()) { // recreates the playerboard
            ((Rectangle) node).setFill(Color.WHITE);
        }
        for (Node node : enemyBoard.getChildren()) { // recreates the enemyboard
            ((Rectangle) node).setFill(Color.WHITE);
        }
        for (Node node : enemyBoard.getChildren()) {
            node.setId(null);
        }

        for (Ship ship : shipList.keySet()) { // enable the ship model selections
            shipList.get(ship).setDisable(false);
        }
        shipsAddedNo = 0;
        playerBoard.setDisable(true); // first an event from shipModelGrid needs creating
        startGameBtn.setDisable(true); // first all the ships should be positioned and enemy board created
        shipModelGrid.setDisable(false);
        enemyBoard.setDisable(true);
        enemy = 0;
        player = 0;
        playerScore.setText("0");
        enemyScore.setText("0");

    }

    private void createPlayerInf() {
        TextInputDialog dialog = new TextInputDialog("Your name");
        dialog.setTitle("Player's Name");
        dialog.setHeaderText("I would like to call you by your name.");
        dialog.setContentText("Please enter your name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> playerNameLbl.setText(name.toUpperCase()));
        nameIsPresent = true;

    }
}
