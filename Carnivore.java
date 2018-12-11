//package com.jetbrain;

public class Carnivore extends Animal {
    public Carnivore () {
        super();
    }


    @Override
    public String toString(){
        return String.format("@");
    }
    @Override
    public void eat(Organism food) {
        set_energy(get_energy()+food.get_energy());
    }
    @Override
    public void energy_modify() { set_energy(get_energy() - 5); }
    @Override
    public boolean give_birth () {
        if (this.get_energy() >= 155 && this.get_age() >= 12) {
            set_energy(get_energy() - 100);
            return true;
        }
        return false;
    }
    public boolean status_check() {
        if(this.get_energy() <= 0 || this.get_age() >= 30) {
            return true;
        }
        return false;
    }
}


