//package com.jetbrain;

public class Herbivore extends Animal {
    int x;
    int y;
    public Herbivore () {
        super();
        x = 0;
        y = 0;

    }

    @Override
    public String toString(){
        return String.format("&");
    }

    public void set_memory (int x, int y ) {
        this.x = x;
        this.y = y;
    }
    public int get_mem_x() {
        return x;
    }
    public int get_mem_y () {
        return y;
    }

    @Override
    public void eat(Organism food) {
        set_energy(get_energy()+food.get_energy());
    }
    @Override
    public void energy_modify() {
        set_energy(get_energy() - 4 );
    }
    @Override
    public boolean give_birth () {
        if (this.get_energy() >= 125 && this.get_age() >= 10) {
            set_energy(get_energy() - 80);
            return true;
        }
        return false;
    }
    public boolean status_check() {
        if(this.get_energy() <= 0 || this.get_age() >= 25) {
            return true;
        }
        return false;
    }
}


