package net.shadow.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.shadow.Shadow;
import net.shadow.feature.CommandRegistry;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.font.FontRenderers;
import net.shadow.gui.etc.RoundTextField;
import net.shadow.utils.ClipUtils;
import net.shadow.utils.MSAAFramebuffer;
import net.shadow.utils.Rectangle;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.TransitionUtils;
import net.shadow.widgets.RoundButton;

import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConsoleScreen extends Screen {
    public static final Color BACKGROUND = new Color(60, 60, 60);
    public static final Color RED = new Color(255, 50, 50);
    public static final Color GREEN = new Color(50, 255, 50);
    private static ConsoleScreen instance;
    final Color background = new Color(0, 0, 0, 120);
    final List<LogEntry> logs = new ArrayList<>();
    RoundTextField command;
    double scroll = 0;
    double smoothScroll = 0;
    double lastLogsHeight = 0;

    private ConsoleScreen() {
        super(Text.of("real"));
    }

    public static ConsoleScreen instance() {
        if (instance == null) {
            instance = new ConsoleScreen();
        }
        return instance;
    }

    double padding() {
        return 5;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        smoothScroll = TransitionUtils.transition(smoothScroll, scroll, 7, 0);
        Shadow.c.keyboard.setRepeatEvents(true);
        MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> {
            command.setFocused(true);
            RenderUtils.fill(stack, background, 0, 0, width, height);
    
            // log field
            RenderUtils.renderRoundedQuad(stack, new Color(20, 20, 20), padding(), padding(), width - padding(), height - padding() - 20 - padding(), 5);
            ClipUtils.globalInstance.addWindow(stack, new Rectangle(padding(), padding(), width - padding(), height - padding() - 20 - padding()));
            //RenderUtils.beginScissor(stack, padding(), padding(), width - padding(), height - padding() - 20 - padding());
    
            // logs
            float startingY = (float) (padding() + 5);
            float startingX = (float) (padding() + 5);
            double availWidth = width - padding() - 10;
            double availHeight = height - padding() - 20 - padding() - 13.5;

            List<LogEntryIntern> processedLogs = new ArrayList<>();
            while (logs.size() > 1000) {
                logs.remove(0); // max log size of 1000 before we clear
            }
            for (LogEntry log : logs) {
                List<String> logSplitToWidth = new ArrayList<>();
                StringBuilder currentLog = new StringBuilder();
                char[] logChr = log.text.toCharArray();
                for (int i = 0; i < logChr.length; i++) {
                    char current = logChr[i];
                    currentLog.append(current);
                    if (FontRenderers.getRenderer().getStringWidth(currentLog.toString()) > availWidth) {
                        currentLog.delete(currentLog.length() - 2, currentLog.length());
                        while (currentLog.charAt(currentLog.length() - 1) == ' ') {
                            currentLog.deleteCharAt(currentLog.length() - 1); // clear trailing whitespaces
                        }
                        currentLog.append("-");
                        logSplitToWidth.add(currentLog.toString());
                        currentLog = new StringBuilder();
                        i -= 2;
                    }
                }
                logSplitToWidth.add(currentLog.toString());
                processedLogs.add(new LogEntryIntern(logSplitToWidth.toArray(String[]::new), log.color));
            }
            double logsHeight = processedLogs.stream().map(logEntryIntern -> logEntryIntern.text.length * FontRenderers.getRenderer().getMarginHeight()).reduce(Float::sum).orElse(0f);
            lastLogsHeight = logsHeight;
            if (logsHeight > availHeight) {
                startingY -= (logsHeight - availHeight); // scroll up to fit
            }
            startingY += smoothScroll;
            for (LogEntryIntern processedLog : processedLogs) {
                for (String s : processedLog.text) {
                    // we're in bounds, render
                    if (startingY + FontRenderers.getRenderer().getMarginHeight() >= padding() && startingY <= height - padding() - 20 - padding()) {
                        FontRenderers.getRenderer().drawString(stack, s, startingX, startingY, processedLog.color.getRGB(), false);
                    }
                    // else, just add
                    startingY += FontRenderers.getRenderer().getMarginHeight();
                }
            }
    
            ClipUtils.globalInstance.popWindow();
            //RenderUtils.endScissor();
    
            if (logsHeight > availHeight) {
                double viewportHeight = (height - padding() - 20 - padding()) - padding();
                double contentHeight = processedLogs.stream().map(logEntryIntern -> logEntryIntern.text.length * FontRenderers.getRenderer().getMarginHeight()).reduce(Float::sum).orElse(0f);
                double per = viewportHeight / contentHeight;
                double barHeight = (height - padding() - 20 - padding() * 2) - padding() * 2;
                double innerbarHeight = barHeight * per;
                double perScrolledIndex = smoothScroll / Math.max(1, lastLogsHeight - (height - padding() - 20 - padding() - 13.5));
                perScrolledIndex = 1 - perScrolledIndex;
                double wid = 3;
                double cursorY = MathHelper.lerp(perScrolledIndex, padding() * 2, height - padding() - 20 - padding() * 2 - innerbarHeight);
                double cursorX = width - padding() * 2 - 3;
    
                RenderUtils.renderRoundedQuad(stack, new Color(10, 10, 10, 150), cursorX - wid / 2d, padding() * 2, cursorX + wid / 2d, padding() * 2 + barHeight, wid / 2d);
    
                //            RenderUtils.renderCircle(stack, new Color(50, 50, 50, 150), cursorX, cursorY, 3, 10);
                RenderUtils.renderRoundedQuad(stack, new Color(50, 50, 50, 150), cursorX - wid / 2d, cursorY, cursorX + wid / 2d, cursorY + per * barHeight, wid / 2d);
            }
    
            renderSuggestions(stack);
        });
        super.render(stack, mouseX, mouseY, delta);
    }

    @Override
    protected void init() {
        super.init();
        double widgetWidthA = width - padding() * 2;
        double buttonWidth = 60;
        double inputWidth = widgetWidthA - buttonWidth - padding();
        command = this.addDrawableChild(new RoundTextField(padding(), height - padding() - 20, inputWidth, 20D, "Command", 2D));
        RoundButton submit = new RoundButton(new Color(40, 40, 40), padding() * 2 + inputWidth, height - padding() - 20, buttonWidth, 20, "Execute", this::execute);
        addDrawableChild(submit);
        setInitialFocus(command);
    }

    void execute() {
        String cmd = command.get();
        command.setText("");
        if (cmd.isEmpty()) {
            return;
        }
        addLog(new LogEntry("> " + cmd, BACKGROUND));
        //add line to run here
        String[] args = cmd.split(" ");
        Command co = CommandRegistry.find(args[0].toLowerCase());
        if (co == null) {
            addLog(new LogEntry("> " + cmd, RED));
            return;
        }
        addLog(new LogEntry("> " + cmd, GREEN));
        args = Arrays.copyOfRange(args, 1, args.length);
        co.call(args);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            execute();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_TAB) {
            autocomplete();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
    }

    void autocomplete() {
        String cmd = command.get();
        if (cmd.isEmpty()) {
            return;
        }
        List<String> suggestions = getSuggestions(cmd);
        if (suggestions.isEmpty()) {
            return;
        }
        String[] cmdSplit = cmd.split(" +");
        if (cmd.endsWith(" ")) {
            String[] cmdSplitNew = new String[cmdSplit.length + 1];
            System.arraycopy(cmdSplit, 0, cmdSplitNew, 0, cmdSplit.length);
            cmdSplitNew[cmdSplitNew.length - 1] = "";
            cmdSplit = cmdSplitNew;
        }
        cmdSplit[cmdSplit.length - 1] = suggestions.get(0);
        command.setText(String.join(" ", cmdSplit) + " ");
    }

    List<String> getSuggestions(String command) {
        List<String> a = new ArrayList<>();
        String[] args = command.split(" +");
        String cmd = args[0].toLowerCase();
        args = Arrays.copyOfRange(args, 1, args.length);
        if (command.endsWith(" ")) { // append empty arg when we end with a space
            String[] args1 = new String[args.length + 1];
            System.arraycopy(args, 0, args1, 0, args.length);
            args1[args1.length - 1] = "";
            args = args1;
        }
        if (args.length > 0) {
            Command c = CommandRegistry.find(cmd);
            if (c != null) {
                a = c.completions(args.length - 1, args);
            } else {
                return new ArrayList<>(); // we have no command to ask -> we have no suggestions
            }
        } else {
            for (Command command1 : CommandRegistry.getList()) {
                if(command1.getName().startsWith(cmd)){
                    a.add(command1.getName());
                }
            }
        }
        String[] finalArgs = args;
        return finalArgs.length > 0 ? a.stream().filter(s -> s.toLowerCase().startsWith(finalArgs[finalArgs.length - 1].toLowerCase())).collect(Collectors.toList()) : a;
    }

    void renderSuggestions(MatrixStack stack) {
        String cmd = command.get();
        float cmdTWidth = FontRenderers.getRenderer().getStringWidth(cmd);
        double cmdXS = command.getX() + 5 + cmdTWidth;
        if (cmd.isEmpty()) {
            return;
        }
        List<String> suggestions = getSuggestions(cmd);
        if (suggestions.isEmpty()) {
            return;
        }
        double probableHeight = suggestions.size() * FontRenderers.getRenderer().getMarginHeight() + padding();
        float yC = (float) (height - padding() - 20 - padding() - probableHeight) - 5;
        double probableWidth = 0;
        for (String suggestion : suggestions) {
            probableWidth = Math.max(probableWidth, FontRenderers.getRenderer().getStringWidth(suggestion) + 1);
        }
        float xC = (float) (cmdXS);
        RenderUtils.renderRoundedQuad(stack, new Color(30, 30, 30, 255), xC - padding(), yC - padding(), xC + probableWidth + padding(), yC + probableHeight, 5);
        for (String suggestion : suggestions) {
            FontRenderers.getRenderer().drawString(stack, suggestion, xC, yC, 0xFFFFFF, false);
            yC += FontRenderers.getRenderer().getMarginHeight();
        }
    }

    public void addLog(LogEntry le) {
        logs.add(le);
        if (scroll != 0) {
            scroll += FontRenderers.getRenderer().getMarginHeight(); // keep up when not at 0
            smoothScroll += FontRenderers.getRenderer().getMarginHeight();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll += (amount * 10);
        scroll = MathHelper.clamp(scroll, 0, Math.max(0, lastLogsHeight - (height - padding() - 20 - padding() - 13.5)));
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public record LogEntry(String text, Color color) {

    }

    protected record LogEntryIntern(String[] text, Color color) {
    }
}
