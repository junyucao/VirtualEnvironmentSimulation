import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.lang.Math;
import java.security.SecureRandom;

public class Simulatefx extends Application {
    Stage window;

    //Load the images
    Image blank = new Image("BlankIcon.png");
    Image plant = new Image("PlantIcon.png");
    Image carnivore = new Image("CarnivoreIcon.png");
    Image herbivore = new Image("HerbivoreIcon.png");

    ImageView[][] iconImageView= new ImageView[16][16];

    /////////////////////////////////////////////////////////////////////////////////////////////////
    private Organism [][] board;
    private int[][] plant_root;
    public static final String Parser = "0000000000001";

    public Simulatefx () {
        board = new Organism[16][16];
        plant_root = new int[16][16];
    }

    public Organism[][] getBoard() {
        return board;
    }

    public void setBoard(Organism[][] board) {
        this.board = board;
    }

    public void setPlant_root(int[][] plant_root) {
        this.plant_root = plant_root;
    }

    public void resetBoard() {
        board = new Organism[16][16];
        plant_root = new int[16][16];
    }

    public static int[] shuffle_move() {
    //shuffling the direction to move
        int array[] = {0,1,2,3};
        int index, temp;
        SecureRandom random = new SecureRandom();
        for (int i = array.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
        return array;
    }

    public void populate (int count, SecureRandom random ) {
        int counter =0;
        while (counter < count) {
            int x = random.nextInt(16);
            int y = random.nextInt(16);
            if(board[x][y] == null) {
                board[x][y] = new Plants();
                counter++;
            }
        }
        counter = 0;
        while (counter < count-1) {
            int x = random.nextInt(16);
            int y = random.nextInt(16);
            if(board[x][y] == null) {
                board[x][y] = new Herbivore();
                counter++;
            }
        }
        counter = 0;
        while (counter < count-2) {
            int x = random.nextInt(16);
            int y = random.nextInt(16);
            if(board[x][y] == null) {
                board[x][y] = new Carnivore();
                counter++;
            }
        }
    }

    public void turn_move(int row, int col, Simulatefx Environment) { //THIS METHOD FIRST CHECK THE ANIMAL CONDITION FROM //OTHER METHOD THEN DO THE MOVEMENT
        boolean status = Environment.check_status(row , col);     //status true if object is animal, it also check if the organism is //dying.
        if(status) {
            if(herbivore_move(row,col)) { /*This method is added to see if herbivore is hungry, it will begin to search for food base on memory.*/
                return;
            }
            if(board[row][col] instanceof Carnivore && sensor(row,col)) {
/*If a carnivore is hungry, it will activate sensor to sense herbivore/food around its vicinity and chase it.*/
                return;
            }
            SecureRandom random = new SecureRandom();
            int speed = random.nextInt(board[row][col].get_energy() / 20 + 1) + 2;
            Randomized_move(row,col,speed);
        }
    }

    public boolean herbivore_move (int row, int col) {
        if(board[row][col] instanceof Herbivore && board[row][col].get_energy() > 80) {
            ((Animal)board[row][col]).set_chase_time(1);
        }
        if(board[row][col] instanceof Herbivore && board[row][col].get_energy() < 60
                && ((Animal)board[row][col]).check_chase_time() == 1 ) {
            int x = ((Herbivore)board[row][col]).get_mem_x();
            int y = ((Herbivore)board[row][col]).get_mem_y();
            if(x == row && y == col) {
                ((Animal)board[row][col]).set_chase_time(0);
                return false;
            }
            search_food(row, col, x, y);    //hungry mode move
            return true;
        }
        return false;
    }

    public boolean sensor(int row, int col) {
        int search_radius = 2;
        if(board[row][col].get_energy() < 40) {
            int a;
            int b;
            for (int x = -search_radius; x <= search_radius; x++ ){
                for (int y = -Math.abs((Math.abs(x)-search_radius)); y <= Math.abs((Math.abs(x)-search_radius)); y++){
                    if(x == 0 && y == 0) {
                        break;
                    }
                    a = row + x;
                    b = col + y;
                    if (a >= 16 || a < 0 || b >=16 || b < 0) {
                        continue;
                    }
                    if(board[a][b] instanceof Herbivore) {
                        search_food(row, col, a, b);
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    public void plant_respawn(int row, int col) {
        if(plant_root[row][col] > 0) { ++plant_root[row][col]; }
        if(plant_root[row][col] > 10 && board[row][col] == null) {    //at least 10 turns to respawn eaten plant
            board[row][col] = new Plants();
            System.out.println("A plant is spawned");
        }
    }

    public void plant_spawn(SecureRandom random, int row, int col) {
        if(board[row][col] instanceof Plants ) {
            int spawn_radius = ((Plants)board[row][col]).get_spawn_radius();  //diff types of plants diff radius
            int new_row, new_col;
            for(int i = -spawn_radius; i <= spawn_radius; ++i) {
                for(int j = -spawn_radius; j <= spawn_radius; ++j) {
                    new_row = row + i;
                    new_col = col + j;
                    if (new_row >= 16 || new_row < 0 || new_col >=16 || new_col < 0) {
                        continue;
                    }
                    if(board[new_row][new_col] == null) {
                        if( (random.nextInt(200) - 1) < 0 ) { // (1/100) chance grow plant
                            board[new_row][new_col] = new Plants();
                            System.out.println("A plant is spawned");
                        }
                    }
                }
            }
        }
    }

    public boolean check_status(int row, int col) {
        if( board[row][col] != null) {
            if(!board[row][col].get_moved()) {
                board[row][col].set_moved(true);
                board[row][col].add_age(1); //growing every turn, all species
                board[row][col].energy_modify(); //animals lose energy every turn, plants gain energy every turn.
                if (board[row][col].status_check()) {
                    System.out.printf("%s died %n", board[row][col].getClass().getName() );
                    board[row][col] = null;  //they die due to age or no energy, including plants, or they will over populate;
                    return false;
                }
                if (board[row][col] instanceof Plants) {  //The statement returns false because in the turn_move,
                    // plants does not need to move or have further action
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public void search_food (int row , int col, int x, int y) { //The search food algorithem is similar to the normal move.
        // It moves block by block for a number of time call speed, and each block gets you closer to the target location.
        int speed = board[row][col].get_energy() / 15 + 1;
        for(int i = 0; i<speed; i++) {
            if(((Animal)board[row][col]).checkAction()) {
                return;
            }
            boolean moved = false;
            int counter = 0;
            int compass[] = shuffle_move();
            while (!moved && counter < 4) {
                int x1 = 0;
                int y1 = 0;
                switch (compass[counter % 4]) {
                    case 1:
                        if (x - row > 0) {
                            x1 = row + 1;
                            moved = check_move(row, col, x1, col);
                            if (moved)
                                row = x1;
                        } else if (x - row < 0) {
                            x1 = row - 1;
                            moved = check_move(row, col, x1, col);
                            if (moved)
                                row = x1;
                        }
                        break;
                    case 2:
                        if (y - col > 0) {
                            y1 = col + 1;
                            moved = check_move(row, col, row, y1);
                            if (moved)
                                col = y1;
                        } else if (y - col < 0) {
                            y1 = col - 1;
                            moved = check_move(row, col, row, y1);
                            if (moved)
                                col = y1;
                        }
                        break;
                }
                counter++;
            }
        }
    }

    public void Randomized_move(int row, int col, int speed) {
        for (int block = 0; block < speed; block++) {
            if(((Animal)board[row][col]).checkAction()) {
                return;
            }
            int counter = 0;
            int compass[] = shuffle_move();
            boolean moved = false;
            int x = 0;
            int y = 0;
            while (!moved && counter < 4) {
                x = row;
                y = col;
                switch (compass[counter % 4]) {
                    case 0:
                        x += 1;
                        if (x >= 16) {
                            break;
                        }
                        //The check_move method also uses check_birth, check_food method and move() method inside.
                        moved = check_move(row, col, x, y);
                        break;
                    case 1:
                        x -= 1;
                        if (x < 0) {
                            break;
                        }
                        moved = check_move(row, col, x, y);
                        break;
                    case 2:
                        y += 1;
                        if (y >= 16) {
                            break;
                        }
                        moved = check_move(row, col, x, y);
                        break;
                    case 3:
                        y -= 1;
                        if (y < 0) {
                            break;
                        }
                        moved = check_move(row, col, x, y);
                        break;
                }
                ++counter;
            }
            if(moved) {
                row = x;
                col = y;
            }
        }
    }

    public boolean check_move(int j, int k, int x, int y) { //This method check if the animal that is moving needs to give birth,
        // or the location it lands on has food or something is blocking its path.
        // It will return true if move, eat or give birth is done, other wise return false.
        if (board[x][y] == null) {
            boolean birth = birth_check(j, k, x, y);
            if(birth) {
                return true;
            }
            else {
                this.move(j, k, x, y);
                return  true;
            }
        }
        else {
            return check_food(j, k, x, y);
        }
    }

    public boolean birth_check(int j, int k ,int x ,int y) {//Simply checking if birth condition reached and will give birth.
        // It will return true if it does give birth
        boolean birth = ((Animal)board[j][k]).give_birth();
        if (birth) {
            if (board[j][k] instanceof Carnivore) {
                board[x][y] = new Carnivore(); board[x][y].set_moved(true);
                System.out.println("A carnivore is born");
                ((Animal)board[j][k]).setAction(true);
                ((Animal)board[x][y]).setAction(true);
            }
            else {
                board[x][y] = new Herbivore(); board[x][y].set_moved(true);
                System.out.println("A herbivore is born");
                ((Animal)board[x][y]).setAction(true);
            }
        }
        return birth;
    }

    public void move(int j, int k, int x, int y) {
        board[x][y] = board[j][k];
        board[j][k] = null;
    }

    public boolean check_food (int j, int k, int x, int y) {
        if (board[j][k] instanceof Carnivore && board[x][y] instanceof Herbivore) {
            ((Animal)board[j][k]).setAction(true);
            ((Animal)board[j][k]).eat(board[x][y]);
            this.move(j, k, x, y);
            System.out.println("A herbivore is eaten");
            return true;
        }
        if (board[j][k] instanceof Herbivore && board[x][y] instanceof Plants) {
            ((Animal)board[j][k]).setAction(true);
            ((Animal)board[j][k]).eat(board[x][y]);
            plant_root[x][y] = 1;
            ((Herbivore)board[j][k]).set_memory(x,y);
            this.move(j, k, x, y);
            System.out.println("A plant is eaten");
            return true;
        }
        return false;
    }

    public void reset() {
        for(int j = 0; j < 16; ++j) {
            for(int k = 0; k < 16; ++k) {
                if(board[j][k] != null) {
                    board[j][k].set_moved(false);
                    if(board[j][k] instanceof Animal) {
                        ((Animal)board[j][k]).setAction(false);
                    }
                }
            }
        }
    }


    public void Save_Game() {
        String SaveString = "";
        for (int row = 0; row < 16; ++row) {
            for (int col = 0; col < 16; ++col) {
                if (board[row][col] == null) {
                    SaveString = SaveString + "0000"+ Parser;
                }
                if (board[row][col] instanceof Plants) {
                    SaveString = SaveString + "1000"+ Parser+ Integer.toBinaryString(board[row][col].get_energy())
                            + Parser + Integer.toBinaryString(board[row][col].get_age()) + Parser;
                }
                if (board[row][col] instanceof Herbivore) {
                    SaveString = SaveString + "1010"+ Parser+ Integer.toBinaryString(board[row][col].get_energy())
                            + Parser + Integer.toBinaryString(board[row][col].get_age()) + Parser;
                }
                if (board[row][col] instanceof Carnivore) {
                    SaveString = SaveString + "1001"+ Parser+ Integer.toBinaryString(board[row][col].get_energy())
                            + Parser + Integer.toBinaryString(board[row][col].get_age()) + Parser;
                }
            }
        }
        SaveFile.SaveGame(SaveString.getBytes());
        byte[] trial = SaveString.getBytes();
        for (byte a : trial ) {
            System.out.print((char)a);
        }
        System.out.println();
    }

    public void Load_Game() {
        System.out.printf("%n%nPrinting out Save_file%n");
        String LoadString = LoadFile.LoadGame();
        String Segments[] = LoadString.split(Parser);
        int SegLength = Segments.length;
        System.out.println(LoadString);
        int index = 0;
        int length = LoadString.length();
        System.out.printf("The length is %d%n", length);
        for (int row = 0; row < 16; ++row) {
            for (int col = 0; col < 16; ++col) {
                int counter = 0;
                while (counter < 3) {
                    switch (counter) {
                        case 0:
                            if (Segments[index].equals("0000")) {
                                board[row][col] = null;
                                counter = 4;
                                //System.out.println("got a null");
                            }
                            if (Segments[index].equals("1000")) {
                                board[row][col] = new Plants();
                                //System.out.println("got a plant");

                            }
                            if (Segments[index].equals("1010")) {
                                board[row][col] = new Herbivore();
                                //System.out.println("got a herb");

                            }
                            if (Segments[index].equals("1001")) {
                                board[row][col] = new Carnivore();
                                //System.out.println("got a carn");
                            }
                            break;
                        case 1:
                            if(board[row][col] == null) break;
                            int energy = Integer.parseInt(Segments[index], 2);
                            board[row][col].set_energy(energy);
                            break;
                        case 2:
                            if(board[row][col] == null) break;
                            int age = Integer.parseInt(Segments[index], 2);
                            board[row][col].add_age(age - 1);
                            break;
                    }
                    counter++;
                    index++;
                }
            }
        }
        print_board();
    }


    public void print_board () {
        for (int row = 0; row < 16; ++row) {
            for (int col = 0; col < 16; ++col) {
                if(board[row][col] != null)
                    System.out.printf("%s ", board[row][col]);
                else
                    System.out.print("- ");
            }
            System.out.println();
        }
        System.out.printf("%n%n");
    }

    public void update_GUI(Simulatefx enviroment) {
        for (int row = 0; row < 16; ++row) {
            for(int col = 0; col < 16; ++col) {
                iconImageView[row][col].setImage(blank);
                if(enviroment.getBoard()[row][col] instanceof Plants) {
                    iconImageView[row][col].setImage(plant);
                }
                if(enviroment.getBoard()[row][col] instanceof Carnivore) {
                    iconImageView[row][col].setImage(carnivore);
                }
                if(enviroment.getBoard()[row][col] instanceof Herbivore) {
                    iconImageView[row][col].setImage(herbivore);
                }
            }
        }
    }


////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) {
        launch(args);   //from Application sets up prog as javafx
    }

    @Override
    public void start(Stage primaryStage) throws Exception{ //our main javafx code
        window = primaryStage;
        window.setTitle("Animal Simulation");



        GridPane grid = new GridPane();
        grid.setPadding( new Insets(10, 10, 10, 10) ); //padding around the borders

        Simulatefx Environment = new Simulatefx(); //generate the environment
        SecureRandom random = new SecureRandom();
        int count1 = random.nextInt(15)+10;
        final int count = 5;
        Environment.populate(count , random);
        Environment.print_board();

        for (int row = 0; row < 16; ++row) {
            for(int col = 0; col < 16; ++col) {
                iconImageView[row][col] = new ImageView(blank);
                if(Environment.getBoard()[row][col] instanceof Plants) {
                    iconImageView[row][col] = new ImageView(plant);
                }
                if(Environment.getBoard()[row][col] instanceof Carnivore) {
                    iconImageView[row][col] = new ImageView(carnivore);
                }
                if(Environment.getBoard()[row][col] instanceof Herbivore) {
                    iconImageView[row][col] = new ImageView(herbivore);
                }
                iconImageView[row][col].setFitWidth(30);
                iconImageView[row][col].setPreserveRatio(true);
                grid.add(iconImageView[row][col], col, row);
            }
        }

        HBox topMenu = new HBox(10);
        Button nextButton = new Button("Next");
        Button saveButton = new Button("Save");
        Button loadButton = new Button("Load");
        Button resetButton = new Button("Reset");
        Button exitButton = new Button("Exit");

        //event handlers
        nextButton.setOnAction(event -> {
            for(int row = 0; row < 16; ++row) {
                for(int col = 0; col < 16; ++col) {
                    Environment.turn_move(row,  col, Environment);
                    Environment.plant_spawn(random, row, col);
                    Environment.plant_respawn(row, col);
                }
            }
            Environment.reset();
            update_GUI(Environment);
        });

        saveButton.setOnAction(event -> {
            Environment.Save_Game();
            update_GUI(Environment);
        });

        loadButton.setOnAction(event -> {
            Environment.Load_Game();
            update_GUI(Environment);
        });

        resetButton.setOnAction(event -> {
            Environment.resetBoard();
            Environment.populate(count , random); //MAY WANT TO INITIATE COUNT HERE
            update_GUI(Environment);
        });



        exitButton.setOnAction(event -> window.close() );

        topMenu.getChildren().addAll(nextButton, saveButton, loadButton, resetButton, exitButton);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(topMenu);
        borderPane.setCenter(grid);


        Scene scene = new Scene(borderPane, 600, 600);
        window.setScene(scene);
        window.show();

    }



}
