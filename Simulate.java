//NOTE: I MODIFIED THE TURN_MOVE METHOD, basically I just put the switch statement in a new function call Randomized_move.

//package com.jetbrain;

import org.omg.CORBA.Environment;
import java.lang.Math;
import java.security.SecureRandom;
import java.util.*;




public class Simulate {
    private Organism [][] environment;
    private int[][] plant_root;
    public static final String Parser = "0000000000001";
    public static final int Number_of_iteration = 60;

    public Simulate () {
        environment = new Organism[16][16];
        plant_root = new int[16][16];
    }

    public void print_environment () {
        for (int row = 0; row < 16; ++row) {
            for (int col = 0; col < 16; ++col) {
                if(environment[row][col] != null)
                    System.out.printf("%s ", environment[row][col]);
                else
                    System.out.print("- ");
            }
            System.out.println();
        }
        System.out.printf("%n%n");
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

    public static void main(String[] args) {

        Simulate Environment = new Simulate(); //generate the environment
        Simulate Environment2 = new Simulate();
        SecureRandom random = new SecureRandom();
        int count = random.nextInt(15)+10;
        count = 5;
        System.out.printf("The number total numbers of organisms on the field is: %d%n", count+count-1+count-2);
        Environment.populate(count , random);
        Environment.print_environment();

        for(int i = 0; i < Number_of_iteration; ++i) {
            for(int row = 0; row < 16; ++row) {
                for(int col = 0; col < 16; ++col) {
                    Environment.turn_move(i, row,  col, Environment);
                    //Each cell calls turn_move to initiate movement, check wuts in the cell etc.
                    Environment.plant_spawn(random, row, col);  //A random chance plant may spawn on this block
                    Environment.plant_respawn(row, col);
                }
            }
            System.out.printf("Iteration %d%n", i + 1);
            Environment.print_environment();
            Environment.reset();
            if(i == Number_of_iteration - 1) {
                Environment.Save_Game();
            }
        }
        Environment2.Load_Game();
    }
    public void populate (int count, SecureRandom random ) {
        int counter =0;
        while (counter < count) {
            int x = random.nextInt(16);
            int y = random.nextInt(16);
            if(environment[x][y] == null) {
                environment[x][y] = new Plants();
                counter++;
            }
        }
        counter = 0;
        while (counter < count-1) {
            int x = random.nextInt(16);
            int y = random.nextInt(16);
            if(environment[x][y] == null) {
                environment[x][y] = new Herbivore();
                counter++;
            }
        }
        counter = 0;
        while (counter < count-2) {
            int x = random.nextInt(16);
            int y = random.nextInt(16);
            if(environment[x][y] == null) {
                environment[x][y] = new Carnivore();
                counter++;
            }
        }
    }

    public void turn_move(int i, int row, int col, Simulate Environment) { //THIS METHOD FIRST CHECK THE ANIMAL CONDITION FROM //OTHER METHOD THEN DO THE MOVEMENT
        boolean status = Environment.check_status(row , col, i +1);     //status true if object is animal, it also check if the organism is //dying.
        if(status) {
            if(herbivore_move(row,col)) { /*This method is added to see if herbivore is hungry, it will begin to search for food base on memory.*/
                return;
            }
            if(environment[row][col] instanceof Carnivore && sensor(row,col)) {
/*If a carnivore is hungry, it will activate sensor to sense herbivore/food around its vicinity and chase it.*/
                return;
            }
            SecureRandom random = new SecureRandom();
            int speed = random.nextInt(environment[row][col].get_energy() / 20 + 1) + 2;
            Randomized_move(row,col,speed);
        }
    }

    public boolean herbivore_move (int row, int col) {
        if(environment[row][col] instanceof Herbivore && environment[row][col].get_energy() > 80) {
            ((Animal)environment[row][col]).set_chase_time(1);
        }
        if(environment[row][col] instanceof Herbivore && environment[row][col].get_energy() < 60
                && ((Animal)environment[row][col]).check_chase_time() == 1 ) {
            int x = ((Herbivore)environment[row][col]).get_mem_x();
            int y = ((Herbivore)environment[row][col]).get_mem_y();
            if(x == row && y == col) {
                ((Animal)environment[row][col]).set_chase_time(0);
                return false;
            }
            search_food(row, col, x, y);    //hungry mode move
            return true;
        }
        return false;
    }

    public boolean sensor(int row, int col) {
        int search_radius = 2;
        if(environment[row][col].get_energy() < 40) {
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
                    if(environment[a][b] instanceof Herbivore) {
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
        if(plant_root[row][col] > 10 && environment[row][col] == null) {    //at least 10 turns to respawn eaten plant
            environment[row][col] = new Plants();
            System.out.println("A plant is spawned");
        }
    }

    public void plant_spawn(SecureRandom random, int row, int col) {
        if(environment[row][col] instanceof Plants ) {
            int spawn_radius = ((Plants)environment[row][col]).get_spawn_radius();  //diff types of plants diff radius
            int new_row, new_col;
            for(int i = -spawn_radius; i <= spawn_radius; ++i) {
                for(int j = -spawn_radius; j <= spawn_radius; ++j) {
                    new_row = row + i;
                    new_col = col + j;
                    if (new_row >= 16 || new_row < 0 || new_col >=16 || new_col < 0) {
                        continue;
                    }
                    if(environment[new_row][new_col] == null) {
                        if( (random.nextInt(200) - 1) < 0 ) { // (1/100) chance grow plant
                            environment[new_row][new_col] = new Plants();
                            System.out.println("A plant is spawned");
                        }
                    }
                }
            }
        }
    }

    public boolean check_status(int row, int col, int round) {
        if( environment[row][col] != null) {
            if(!environment[row][col].get_moved()) {
                environment[row][col].set_moved(true);
                environment[row][col].add_age(1); //growing every turn, all species
                environment[row][col].energy_modify(); //animals lose energy every turn, plants gain energy every turn.
                if (environment[row][col].status_check()) {
                    System.out.printf("%s died %n", environment[row][col].getClass().getName() );
                    environment[row][col] = null;  //they die due to age or no energy, including plants, or they will over populate;
                    return false;
                }
                if (environment[row][col] instanceof Plants) {  //The statement returns false because in the turn_move,
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
        int speed = environment[row][col].get_energy() / 15 + 1;
        for(int i = 0; i<speed; i++) {
            if(((Animal)environment[row][col]).checkAction()) {
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
            if(((Animal)environment[row][col]).checkAction()) {
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
        if (environment[x][y] == null) {
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
        boolean birth = ((Animal)environment[j][k]).give_birth();
        if (birth) {
            if (environment[j][k] instanceof Carnivore) {
                environment[x][y] = new Carnivore(); environment[x][y].set_moved(true);
                System.out.println("A carnivore is born");
                ((Animal)environment[j][k]).setAction(true);
                ((Animal)environment[x][y]).setAction(true);
            }
            else {
                environment[x][y] = new Herbivore(); environment[x][y].set_moved(true);
                System.out.println("A herbivore is born");
                ((Animal)environment[x][y]).setAction(true);
            }
        }
        return birth;
    }
    public void move(int j, int k, int x, int y) {
        environment[x][y] = environment[j][k];
        environment[j][k] = null;
    }

    public boolean check_food (int j, int k, int x, int y) {
        if (environment[j][k] instanceof Carnivore && environment[x][y] instanceof Herbivore) {
            ((Animal)environment[j][k]).setAction(true);
            ((Animal)environment[j][k]).eat(environment[x][y]);
            this.move(j, k, x, y);
            System.out.println("A herbivore is eaten");
            return true;
        }
        if (environment[j][k] instanceof Herbivore && environment[x][y] instanceof Plants) {
            ((Animal)environment[j][k]).setAction(true);
            ((Animal)environment[j][k]).eat(environment[x][y]);
            plant_root[x][y] = 1;
            ((Herbivore)environment[j][k]).set_memory(x,y);
            this.move(j, k, x, y);
            System.out.println("A plant is eaten");
            return true;
        }
        return false;
    }




    public void reset() {
        for(int j = 0; j < 16; ++j) {
            for(int k = 0; k < 16; ++k) {
                if(environment[j][k] != null) {
                    environment[j][k].set_moved(false);
                    if(environment[j][k] instanceof Animal) {
                        ((Animal)environment[j][k]).setAction(false);
                    }
                }
            }
        }
    }

    public void Save_Game() {
        String SaveString = "";
        for (int row = 0; row < 16; ++row) {
            for (int col = 0; col < 16; ++col) {
                if (environment[row][col] == null) {
                    SaveString = SaveString + "0000"+ Parser;
                }
                if (environment[row][col] instanceof Plants) {
                    SaveString = SaveString + "1000"+ Parser+ Integer.toBinaryString(environment[row][col].get_energy())
                            + Parser + Integer.toBinaryString(environment[row][col].get_age()) + Parser;
                }
                if (environment[row][col] instanceof Herbivore) {
                    SaveString = SaveString + "1010"+ Parser+ Integer.toBinaryString(environment[row][col].get_energy())
                            + Parser + Integer.toBinaryString(environment[row][col].get_age()) + Parser;
                }
                if (environment[row][col] instanceof Carnivore) {
                    SaveString = SaveString + "1001"+ Parser+ Integer.toBinaryString(environment[row][col].get_energy())
                            + Parser + Integer.toBinaryString(environment[row][col].get_age()) + Parser;
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
        System.out.println();
        System.out.println();
        System.out.println("Printing out Save_file");
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
                                environment[row][col] = null;
                                counter = 4;
                                //System.out.println("got a null");
                            }
                            if (Segments[index].equals("1000")) {
                                environment[row][col] = new Plants();
                                //System.out.println("got a plant");

                            }
                            if (Segments[index].equals("1010")) {
                                environment[row][col] = new Herbivore();
                                //System.out.println("got a herb");

                            }
                            if (Segments[index].equals("1001")) {
                                environment[row][col] = new Carnivore();
                                //System.out.println("got a carn");
                            }
                            break;
                        case 1:
                            if(environment[row][col] == null) break;
                            int energy = Integer.parseInt(Segments[index], 2);
                            environment[row][col].set_energy(energy);
                            break;
                        case 2:
                            if(environment[row][col] == null) break;
                            int age = Integer.parseInt(Segments[index], 2);
                            environment[row][col].add_age(age - 1);
                            break;
                    }
                    counter++;
                    index++;
                }
            }
        }
        print_environment();
    }
}








