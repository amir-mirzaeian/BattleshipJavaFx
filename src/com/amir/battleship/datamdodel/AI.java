package com.amir.battleship.datamdodel;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class AI {

    private int lastX;
    private int lastY;

    private Stack<Integer> stack = new Stack<>();
    private int direction = 0;
    private List<Integer> list = new ArrayList<>();
    private Random random = new Random();

    private int x;
    private int y;


    private boolean isFound = false;

    public void setFound(boolean found) {
        isFound = found;
    }

    public AI() {
        this.createARandom();
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void createARandom() {

        if (!isFound && stack.empty()) {
            x = random.nextInt(10);
            y = random.nextInt(10);
            return;
        }

        if (isFound && stack.empty()) {

            lastX = x;
            lastY = y;


            fillStack();
            nextMove();

            return;

        }
        if (!isFound && !stack.empty()) {

            nextMove();
            return;

        }
        if (isFound && !stack.empty()) {
            findTheNextHit();
            return;
        }


    }

    /*

    if the next right cell found, make the next
    move in its direction.

     */
    private void findTheNextHit() {


        direction = list.get(list.size() - 1);

        if (direction == 1)
            if (y != 9) {
                y = y + 1;
            } else y = lastY - 1;

        if (direction == 2)
            if (x != 9) {
                x = x + 1;
            } else x = lastX - 1;

        if (direction == 3)
            if (y != 0) {
                y = y - 1;
            } else y = lastY + 1;

        if (direction == 4)
            if (x != 0) {
                x = x - 1;
            } else x = lastX + 1;
    }

    /*

    Stack contains all the 4 sides of the intended cell
    to be checked one by one.

     */

    private void fillStack() {


        if (y != 9) {
            //front cell
            stack.push(x);
            stack.push(y + 1);
            direction = 1;
            list.add(direction);


        }

        if (x != 9) {
            //bottom cell
            stack.push(x + 1);
            stack.push(y);
            direction = 2;
            list.add(direction);


        }


        if (y != 0) {
            //rear cell
            stack.push(x);
            stack.push(y - 1);
            direction = 3;
            list.add(direction);


        }

        if (x != 0) {
            //above cell
            stack.push(x - 1);
            stack.push(y);
            direction = 4;
            list.add(direction);


        }
        list.add(5);

    }

    /*

    The next move based on the x and y in stack

     */
    private void nextMove() {

        y = stack.pop();
        x = stack.pop();

        list.remove(list.size() - 1);
    }
}