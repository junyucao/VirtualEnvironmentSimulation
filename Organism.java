//package com.jetbrain;

import java.security.SecureRandom;
public  abstract class Organism {
    private static SecureRandom Rand = new SecureRandom();
    private int age;
    private int energy;
    boolean moved;

    public Organism () {
        energy = Rand.nextInt(31) + 80;
        age = 1;
        moved = false;
    }
    @Override
    public abstract String toString();

    public abstract void energy_modify ();
    public abstract boolean status_check();


    public void set_moved(boolean moved) {
        this.moved = moved;
    }
    public boolean get_moved() {
        return moved;
    }

    public void set_energy(int energy) {
        this.energy = energy;
    }
    public int get_energy () {
        return energy;
    }
    public int get_age() {
        return age;
    }
    public void add_age(int growth) {
        age += growth;
    }



}


