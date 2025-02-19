package net.shadow.alickgui.element;

import net.minecraft.client.util.math.MatrixStack;

public abstract class Element {
    protected double x, y, width, height, popX, popY;

    public Element(double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.popX = x;
        this.popY = y;
        this.width = w;
        this.height = h;
    }

    public boolean inBounds(double cx, double cy) {
        return cx >= x && cx < x + width && cy >= y && cy < y + height;
    }

    abstract public boolean clicked(double x, double y, int button);

    abstract public boolean dragged(double x, double y, double deltaX, double deltaY, int button);

    abstract public boolean released();

    abstract public boolean keyPressed(int keycode, int modifiers);

    abstract public void render(MatrixStack matrices, double mouseX, double mouseY, double scrollBeingUsed);

    abstract public void tickAnim();

    abstract public boolean charTyped(char chr, int modifiers);

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }
}
