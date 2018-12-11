//package com.jetbrain;

public class Plants extends Organism {
    private int spawn_radius;

    public Plants () {
        super();
        this.spawn_radius = 2;
    }

    @Override
    public String toString() {
        return String.format("#");
    }

    public void energy_modify() {
        set_energy(get_energy()  + 5);
    }
    public boolean status_check() {
        if(this.get_age() >= 30) {
            return true;
        }
        return false;
    }
    public int get_spawn_radius() { return spawn_radius; }

}



