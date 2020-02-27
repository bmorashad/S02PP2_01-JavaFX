import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.swing.*;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class TrainSeatBooking extends Application {
    final int NUM_SEATS = 42;

    List<Button> seats = new ArrayList<>();
    List<Button> reserved = new ArrayList<>();
    List<String> names = new ArrayList<>();
    Button toBeReserved = null;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        makeSeats();
        Menu();
    }
    public Label makeLabel() {
        Label lbl = new Label();
        lbl.setTextFill(Color.web("blue"));
        GridPane.setConstraints(lbl, 0, 0);
        GridPane.setColumnSpan(lbl,6);
        return lbl;
    }
    public GridPane makeGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);
        return grid;
    }
    public void makeSeats() {
        for(int i = 0, c = 0, r = 0; i < NUM_SEATS; i++) {
            Button seat = new Button("" + (i+1));
            seat.setMaxSize(100, 100);
            seat.setMinSize(40, 40);
            seat.setStyle("-fx-font-size: 14px; -fx-background-color: #ddd; -fx-font-family: 'Clear Sans';");
            if(i % 7 == 0) {
                r += 1;
                c = 0;
            }
            GridPane.setConstraints(seat, c, r);
            seats.add(seat);
            c++;
        }
    }
    public void Menu() {
        Scanner sc = new Scanner(System.in);
        List<String> options = new ArrayList<>();
        options.add("A");options.add("V");options.add("E");options.add("D");options.add("F");
        options.add("S");options.add("L");options.add("O");options.add("Q");
        String option = sc.nextLine();
        if (options.contains(option.toUpperCase())) {
            switch (option.toUpperCase()) {
                case "A":
                    addSeats();
                    break;
                case "E":
                    emptySeats();
                    break;
                case "V":
                    viewSeat();
                    break;
                case "D":
                    deleteSeat();
                    break;
                case "F":
                    findSeat();
                    break;
                case "Q":
                    Platform.exit();
                    break;
            }
        } else {
            Menu();
        }
    }
    public void viewSeat() {
        Stage stage = new Stage();

        GridPane grid = makeGrid();
        Label lbl = makeLabel();

        lbl.setText("View Empty Seats");
        GridPane.setConstraints(lbl, 0, 0);
        GridPane.setColumnSpan(lbl,6);


        Button quit = new Button("Quit");
        quit.setMinWidth(80);
        quit.setMaxWidth(150);
        GridPane.setConstraints(quit, 5, 8);
        GridPane.setColumnSpan(quit, 2);
        grid.getChildren().add(quit);

        grid.getChildren().add(lbl);
        for(Button seat : seats) {
            seat.setOnAction(null);
            if(!reserved.contains(seat)) {
                seat.setStyle("-fx-background-color: #5cff9d");
            } else {
                seat.setStyle("-fx-background-color: #ff9ca7");
            }
            grid.getChildren().add(seat);
        }
        Scene scene = new Scene(grid, 500, 500);
        stage.setScene(scene);
        stage.setTitle("Book A Seat");
        stage.show();
        quit.setOnAction(e -> {
            stage.close();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Menu();
                }
            });
        });
    }
    public void emptySeats() {
        Stage stage = new Stage();
        GridPane grid = makeGrid();
        Label lbl = makeLabel();
        Button quit = new Button("Quit");
        quit.setMinWidth(80);
        quit.setMaxWidth(150);
        GridPane.setConstraints(quit, 5, 8);
        GridPane.setColumnSpan(quit, 2);
        grid.getChildren().add(quit);

        int i = 0; // to count reserved seats
        for(Button seat : seats) {
            if(!reserved.contains(seat)) {
                seat.setOnAction(null);
                seat.setStyle("");
                grid.getChildren().add(seat);
                i++;
            }
        }
        // different label when all seats are booked
        if(i > 0) {
            lbl.setText("View Empty Seats");
            lbl.setStyle("-fx-font-size: 18px");
        } else {
            lbl.setText("Sorry, All Seats Are Booked");
            lbl.setStyle("-fx-font-size: 26px");
        }
        GridPane.setConstraints(lbl, 0, 0);
        GridPane.setColumnSpan(lbl,6);
        grid.getChildren().add(lbl);

        Scene scene = new Scene(grid, 500, 500);
        stage.setScene(scene);
        stage.setTitle("Book A Seat");
        stage.show();
        quit.setOnAction(e -> {
            stage.close();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Menu();
                }
            });
        });
    }
    public void addSeats() {
        Stage stage = new Stage();

        Label lbl = makeLabel();
        GridPane grid = makeGrid();
        lbl.setText("Book a seat");
        grid.getChildren().add(lbl);

        TextField name = new TextField();
        GridPane.setConstraints(name, 0, 8);
        GridPane.setColumnSpan(name, 5);
        grid.getChildren().add(name);

        Button add = new Button("Add");
        add.setMinWidth(80);
        add.setMaxWidth(150);
        GridPane.setConstraints(add, 5, 8);
        GridPane.setColumnSpan(add, 2);
        grid.getChildren().add(add);

        for(Button seat : seats) {
            if(reserved.contains(seat)) {
                seat.setOnAction(null);
                seat.setStyle("-fx-background-color: #ff9ca7");
            } else {
                seat.setStyle("");
                seat.setOnAction(e ->  {
                    if(toBeReserved != null){toBeReserved.setStyle("");}
                    if( toBeReserved != seat) {
                        seat.setStyle("-fx-background-color: #5cff9d");
                        toBeReserved = seat;
                    }
                    else { seat.setStyle("");toBeReserved = null;};
                });
            }
            grid.getChildren().add(seat);
            // make seats reservable

        }
        Scene scene = new Scene(grid, 500, 500);
        stage.setScene(scene);
        stage.setTitle("Book A Seat");
        stage.show();
        add.setOnAction(e -> {
            if(toBeReserved != null){
                reserved.add(toBeReserved);
                names.add(name.getText());
                toBeReserved = null;
                stage.close();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Menu();
                    }
                });
            }
        });
    }
    public void deleteSeat() {
        Scanner sc = new Scanner(System.in);

            System.out.print("Enter your name: ");
            String getName = sc.nextLine();
            System.out.println(getName.toLowerCase());
            if (names.indexOf(getName) != -1) {
                reserved.remove(names.indexOf(getName));
                names.remove(getName);
            } else {
                System.out.println("No Seat Booked On Given Info");
                deleteSeat();
            }

        Menu();
    }
    public void findSeat() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String getName = sc.nextLine();

        if (names.indexOf(getName) != -1) {
            String seat = reserved.get(names.indexOf(getName)).getText();
            System.out.println("Your Seat Is: " + seat);
        } else {
            System.out.println("No Seat Booked On Given Info");
            findSeat();
        }
        Menu();
    }
}
