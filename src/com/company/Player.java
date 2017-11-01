package com.company;

import java.util.Objects;

public class Player {
    String Name;
    boolean Ready;

    public Player(String name, boolean ready) {
        Name = name;
        Ready = ready;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        //if (Ready != player.Ready) return false;
        return Name != null ? Name.equals(player.Name) : player.Name == null;
    }

    @Override
    public int hashCode() {
        int result = Name != null ? Name.hashCode() : 0;
        result = 31 * result + (Ready ? 1 : 0);
        return result;
    }
}
