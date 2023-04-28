

package net.shadow.utils;

public class Rectangle {
    private double x, y, x1, y1;

    public Rectangle(double x, double y, double x1, double y1){
        this.x = x;
        this.y = y;
        this.x1 = x1;
        this.y1 = y1;
    }

    public boolean contains(double x, double y) {
        return x >= this.x && x <= this.x1 && y >= this.y && y <= this.y1;
    }


    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public double getX1(){
        return x1;
    }

    public double getY1(){
        return y1;
    }
}
