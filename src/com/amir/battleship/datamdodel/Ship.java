package com.amir.battleship.datamdodel;

public class Ship {

    private String name;
    private int size;
    private boolean isAdded;

    private int startX;
    private int startY;
    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }


    public Ship(String name, int size) {
        this.name = name;
        this.size = size;
        this.isAdded = false;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public static Ship createShip(String name, int size) {
        return new Ship(name, size);
    }
}