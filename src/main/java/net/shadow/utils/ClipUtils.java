

package net.shadow.utils;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vector4f;
import net.shadow.mixin.MatrixStackAccessor;

import java.awt.Color;
import java.util.Deque;
import java.util.Stack;

public class ClipUtils {
    public static final ClipUtils globalInstance = new ClipUtils();
    final Stack<TransformationEntry> clipStack = new Stack<>();

    //Rectangle lastStack = null;
    public void addWindow(MatrixStack stack, Rectangle r1) {
        Matrix4f matrix = stack.peek().getPositionMatrix();
        Vector4f coord = new Vector4f((float) r1.getX(), (float) r1.getY(), 0, 1);
        Vector4f end = new Vector4f((float) r1.getX1(), (float) r1.getY1(), 0, 1);
        coord.transform(matrix);
        end.transform(matrix);
        double x = coord.getX();
        double y = coord.getY();
        double endX = end.getX();
        double endY = end.getY();
        Rectangle r = new Rectangle(x, y, endX, endY);
        if (clipStack.empty()) {
            clipStack.push(new TransformationEntry(r, stack.peek()));
            //            renderDebug(r.getX(),r.getY(),r.getX1(),r.getY1());

            RenderUtils.beginScissor(new MatrixStack(), r.getX(), r.getY(), r.getX1(), r.getY1());
        } else {
            Rectangle lastClip = clipStack.peek().rect;
            double lsx = lastClip.getX();
            double lsy = lastClip.getY();
            double lstx = lastClip.getX1();
            double lsty = lastClip.getY1();
            double nsx = MathHelper.clamp(r.getX(), lsx, lstx);
            double nsy = MathHelper.clamp(r.getY(), lsy, lsty);
            double nstx = MathHelper.clamp(r.getX1(), nsx, lstx);
            double nsty = MathHelper.clamp(r.getY1(), nsy, lsty); // totally intended varname
            clipStack.push(new TransformationEntry(new Rectangle(nsx, nsy, nstx, nsty), stack.peek()));


            //            renderDebug(nsx,nsy,nstx,nsty);

            RenderUtils.beginScissor(stack, nsx, nsy, nstx, nsty);
        }
    }

    void renderDebug(double x, double y, double x1, double y1) {
        MatrixStack stack = new MatrixStack();
        RenderUtils.lineScreenD(stack, Color.RED, x, y, x1, y);
        RenderUtils.lineScreenD(stack, Color.RED, x1, y, x1, y1);
        RenderUtils.lineScreenD(stack, Color.RED, x1, y1, x, y1);
        RenderUtils.lineScreenD(stack, Color.RED, x, y1, x, y);

        RenderUtils.lineScreenD(stack, Color.RED, x, y, x1, y1);

    }

    public void popWindow() {

        TransformationEntry e = clipStack.pop();
        if (clipStack.empty()) {
            RenderUtils.endScissor();
        } else {
            TransformationEntry r1 = clipStack.peek();
            Rectangle r = r1.rect;
            //            RenderUtils.lineScreenD(stack,Color.BLUE,r.getX1(),r.getY(),r.getX(),r.getY1());
            MatrixStack s = new MatrixStack();
            Deque<MatrixStack.Entry> p = ((MatrixStackAccessor) s).getStack();
            p.clear();
            p.add(r1.transformationEntry);
            RenderUtils.beginScissor(s, r.getX(), r.getY(), r.getX1(), r.getY1());

        }
    }

    public void renderOutsideClipStack(Runnable e) {
        if (clipStack.empty()) {
            e.run();
        } else {
            RenderUtils.endScissor();
            e.run();
            Rectangle r = clipStack.peek().rect;
            RenderUtils.beginScissor(new MatrixStack(), r.getX(), r.getY(), r.getX1(), r.getY1());
        }
    }

    record TransformationEntry(Rectangle rect, MatrixStack.Entry transformationEntry) {
    }
}
