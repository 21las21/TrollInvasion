package com.company;

public class Player {
    String name;
    boolean ready;

    public Player(String name, boolean ready) {
        this.name = name;
        this.ready = ready;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        //if (ready != player.ready) return false;
        return name != null ? name.equals(player.name) : player.name == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (ready ? 1 : 0);
        return result;
    }
}
