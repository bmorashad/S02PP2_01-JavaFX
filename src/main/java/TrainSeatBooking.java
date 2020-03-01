import com.mongodb.client.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TrainSeatBooking extends Application {
    final int NUM_SEATS = 42;
    final List<Button> SEATS = makeSeats();
    List<Button> reserved = new ArrayList<>();
    List<String> names = new ArrayList<>();

    MongoDatabase database;
    MongoCollection<Document> collection;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        setDatabase("mongodb://localhost:27017", "trainBooking");
        setCollection("trainSeatReservations");
        menu();
    }
    public void setDatabase(String url, String databaseName) {
        database = MongoClients.create(url).getDatabase(databaseName);
    }
    public void setCollection(String collectionName) {
        collection = database.getCollection(collectionName);
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
    public List<Button> makeSeats() {
        List<Button> seats = new ArrayList<>();
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
        return seats;
    }
    public void menu() {
        Scanner sc = new Scanner(System.in);
        Character[] options = {'A', 'V', 'E', 'D', 'F', 'S', 'L', 'O', 'Q'};
        List<Character> optionsArrLst = Arrays.asList(options);
        char option = sc.nextLine().toUpperCase().charAt(0);

        if (optionsArrLst.contains(option)) {
            switch (option) {
                case 'A':
                    addSeats();
                    break;
                case 'E':
                    emptySeats();
                    break;
                case 'V':
                    viewSeat();
                    break;
                case 'D':
                    deleteSeat();
                    break;
                case 'F':
                    findSeat();
                    break;
                case 'S':
                    saveToDatabase();
                    break;
                case 'L':
                    loadFromDatabas();
                    break;
                default:
                    Platform.exit();
                    break;
            }
        } else {
            menu();
        }
    }
    public void viewSeat() {
        Stage stage = new Stage();

        GridPane grid = makeGrid();
        Label lbl = makeLabel();

        lbl.setText("View Empty Seats");

        Button quit = new Button("Quit");
        quit.setMinWidth(80);
        quit.setMaxWidth(150);
        GridPane.setConstraints(quit, 5, 8);
        GridPane.setColumnSpan(quit, 2);
        grid.getChildren().add(quit);

        grid.getChildren().add(lbl);
        for(Button seat : SEATS) {
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
                    menu();
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
        for(Button seat : SEATS) {
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
                    menu();
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
        GridPane.setConstraints(add, 5, 8);
        grid.getChildren().add(add);

        Button quit = new Button("Quit");
        GridPane.setConstraints(quit, 6, 8);
        grid.getChildren().add(quit);

        final Button[] toBeReserved = {null};
        for(Button seat : SEATS) {
            if(reserved.contains(seat)) {
                seat.setOnAction(null);
                seat.setStyle("-fx-background-color: #ff9ca7");
            } else {
                seat.setStyle("");
                seat.setOnAction(e ->  {
                    if(toBeReserved[0] != null){
                        toBeReserved[0].setStyle("");}
                    if( toBeReserved[0] != seat) {
                        seat.setStyle("-fx-background-color: #5cff9d");
                        toBeReserved[0] = seat;
                    }
                    else { seat.setStyle("");
                        toBeReserved[0] = null;};
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
            if(toBeReserved[0] != null){
                if(!(name.getText().trim().isEmpty() && name.getText().length() != 1)) { // to stop giving q as the name
                    name.setStyle("-fx-border-color: silver");
                    reserved.add(toBeReserved[0]);
                    names.add(name.getText());
                    toBeReserved[0] = null;
                    stage.close();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            menu();
                        }
                    });
                }
            }
        });
        quit.setOnAction(e -> {
            toBeReserved[0] = null;
            stage.close();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    menu();
                }
            });
        });
    }
    public void deleteSeat() {
        Scanner sc = new Scanner(System.in);
            System.out.print("Enter your name: ");
            String getName = sc.nextLine();
            if(!getName.toLowerCase().equals("q")) {
                if (names.indexOf(getName) != -1) {
                    reserved.remove(names.indexOf(getName));
                    names.remove(getName);
                    menu();
                } else {
                    System.out.println("No Seat Booked For The Provided Name!");
                    deleteSeat();
                }
            } else {
                menu();
            }
    }
    public void findSeat() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String getName = sc.nextLine();
        if(!getName.toLowerCase().equals("q")) {
            if (names.indexOf(getName) != -1) {
                String seat = reserved.get(names.indexOf(getName)).getText();
                System.out.println("Your Seat Is: " + seat);
                menu();
            } else {
                System.out.println("No Seat Booked On Given Info");
                findSeat();
            }
        }
        menu();
    }
    public void saveToDatabase() {
        collection.drop();
        for (Button seat : reserved) {
            String name = names.get(reserved.indexOf(seat));
            String seatNum = seat.getText();
            Document doc = new Document("name", name).append("seat", seatNum);
            collection.insertOne(doc);
        }
    }
    public void loadFromDatabas() {
        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            Button seat = SEATS.get((int) doc.get("seat") - 1);
            String name = (String) doc.get("name");
            if(!reserved.contains(seat)) {
                reserved.add(seat);
                names.add(name);
            }
        }
    }
}
