//package com.jetbrain;

public abstract class Animal extends Organism {
    int chase_time;
    boolean action;
    public Animal() {
        super();
        chase_time = 0;
        action = false;

    }

    @Override
    public abstract String toString();

    public void setAction(boolean action) {
        this.action = action;
    }
    public boolean checkAction() {
        return action;
    }
    public void eat(Organism food) { //override
    }
    public boolean give_birth () { //override
        return false;
    }
    public int check_chase_time () {
        return chase_time;
    }
    public void set_chase_time (int time) {
        chase_time = time;
    }

}


