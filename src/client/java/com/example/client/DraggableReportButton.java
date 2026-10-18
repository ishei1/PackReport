
package com.example.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.narration.NarrationElementOutput;

import java.util.function.BiConsumer;

public class DraggableReportButton extends AbstractWidget {    
    
    public boolean isDragging() {
    return this.dragging;
    }

    public void moveTo(
            double mouseX,
            double mouseY,
            int screenWidth,
            int screenHeight
    ) {
        if (!this.dragging) {
            return;
        }

        int newX = (int) (mouseX - this.dragOffsetX);
        int newY = (int) (mouseY - this.dragOffsetY);

        newX = Math.max(0, Math.min(
                newX, Math.max(0, screenWidth - this.width)
        ));

        newY = Math.max(0, Math.min(
                newY, Math.max(0, screenHeight - this.height)
        ));

        this.setPosition(newX, newY);
        this.onMove.accept(newX, newY);
    }

    
    public void beginDrag(double mouseX, double mouseY) {
        this.dragging = true;
        this.dragOffsetX = mouseX - this.getX();
        this.dragOffsetY = mouseY - this.getY();
    }

    public void endDrag() {
        this.dragging = false;
    }

    private final Runnable onCopy;
    private final BiConsumer<Integer, Integer> onMove;

    private boolean dragging = false;
    private double dragOffsetX;
    private double dragOffsetY;

    public DraggableReportButton(
            int x,
            int y,
            int width,
            int height,
            Component message,
            Runnable onCopy,
            BiConsumer<Integer, Integer> onMove
    ) {
        super(x, y, width, height, message);
        this.onCopy = onCopy;
        this.onMove = onMove;
    }

    @Override
    protected void renderWidget(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        int background = this.isHoveredOrFocused()
                ? 0xFF454545
                : 0xFF202020;

        graphics.fill(
                this.getX(),
                this.getY(),
                this.getX() + this.width,
                this.getY() + this.height,
                background
        );

        graphics.drawCenteredString(
                Minecraft.getInstance().font,
                this.getMessage(),
                this.getX() + this.width / 2,
                this.getY() + (this.height - 8) / 2,
                0xFFFFFFFF
        );
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (!this.active || !this.visible
                || !this.isMouseOver(mouseX, mouseY)) {
            return false;
        }

        if (button == 0) {
            this.onCopy.run();
            return true;
        }

        if (button == 1) {
            this.dragging = true;
            this.dragOffsetX = mouseX - this.getX();
            this.dragOffsetY = mouseY - this.getY();
            this.setFocused(true);
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button,
            double dragX,
            double dragY
    ) {
        if (!this.dragging || button != 1) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();

        int screenWidth =
                minecraft.getWindow().getGuiScaledWidth();
        int screenHeight =
                minecraft.getWindow().getGuiScaledHeight();

        int newX = (int) (mouseX - this.dragOffsetX);
        int newY = (int) (mouseY - this.dragOffsetY);

        newX = Math.max(0, Math.min(
                newX, Math.max(0, screenWidth - this.width)
        ));

        newY = Math.max(0, Math.min(
                newY, Math.max(0, screenHeight - this.height)
        ));

        this.setPosition(newX, newY);
        this.onMove.accept(newX, newY);

        return true;
    }

    @Override
    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (button == 1 && this.dragging) {
            this.dragging = false;
            return true;
        }

        return false;
    }

    @Override
    protected void updateWidgetNarration(
            NarrationElementOutput narrationElementOutput
    ) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}
