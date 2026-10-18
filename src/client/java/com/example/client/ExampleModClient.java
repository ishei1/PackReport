
package com.example.client;

import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastIds;
import com.example.ExampleMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ExampleModClient implements ClientModInitializer {
    
    private static void copyReport(
            net.minecraft.client.Minecraft client
    ) {
        String report = ExampleMod.goonscript;

        if (report == null || report.isBlank()) {
            SystemToast.add(
                    client.getToasts(),
                    SystemToastIds.PERIODIC_NOTIFICATION,
                    Component.literal("PackReport"),
                    Component.literal("Report is empty!")
            );
            return;
        }

        client.keyboardHandler.setClipboard(report);

        SystemToast.add(
                client.getToasts(),
                SystemToastIds.PERIODIC_NOTIFICATION,
                Component.literal("PackReport"),
                Component.literal("Report copied!")
        );
    }

    private static final Path CONFIG_FILE =
            FabricLoader.getInstance()
                    .getConfigDir()
                    .resolve("packreport.properties");

    private static final int BUTTON_WIDTH = 280;
    private static final int BUTTON_HEIGHT = 24;

    private static int buttonX = 10;
    private static int buttonY = 10;

    @Override
    public void onInitializeClient() {
        loadPosition();

        ScreenEvents.AFTER_INIT.register(
                (client, screen, scaledWidth, scaledHeight) -> {

                    // Main menu: fixed button at the bottom centre.
                    if (screen instanceof TitleScreen) {
                        int width = 220;
                        int height = 20;
                        int x = (scaledWidth - width) / 2;
                        int y = scaledHeight - 40;

                        Button mainMenuButton = Button.builder(
                                Component.literal("Copy PackReport"),
                                button -> copyReport(client)
                        ).bounds(x, y, width, height).build();

                        Screens.getButtons(screen).add(mainMenuButton);
                    }

                    // Pause menu: draggable button with saved position.
                    if (screen instanceof PauseScreen) {
                        buttonX = Math.max(
                                0,
                                Math.min(
                                        buttonX,
                                        Math.max(0, scaledWidth - BUTTON_WIDTH)
                                )
                        );

                        buttonY = Math.max(
                                0,
                                Math.min(
                                        buttonY,
                                        Math.max(0, scaledHeight - BUTTON_HEIGHT)
                                )
                        );

                        DraggableReportButton copyButton =
                                new DraggableReportButton(
                                        buttonX,
                                        buttonY,
                                        BUTTON_WIDTH,
                                        BUTTON_HEIGHT,
                                        Component.literal(
                                                "Copy PackReport · Right-click to move"
                                        ),
                                        () -> copyReport(client),
                                        (x, y) -> {
                                            buttonX = x;
                                            buttonY = y;
                                        }
                                );

                        Screens.getButtons(screen).add(copyButton);

                        ScreenEvents.afterRender(screen).register(
                                (s, graphics, mouseX, mouseY, tickDelta) -> {
                                    copyButton.moveTo(
                                            mouseX,
                                            mouseY,
                                            s.width,
                                            s.height
                                    );
                                }
                        );

                        ScreenMouseEvents.beforeMouseClick(screen).register(
                                (s, mouseX, mouseY, button) -> {
                                    if (button == 1
                                            && mouseX >= copyButton.getX()
                                            && mouseX <= copyButton.getX()
                                                    + copyButton.getWidth()
                                            && mouseY >= copyButton.getY()
                                            && mouseY <= copyButton.getY()
                                                    + copyButton.getHeight()) {

                                        copyButton.beginDrag(mouseX, mouseY);
                                    }
                                }
                        );

                        ScreenMouseEvents.beforeMouseRelease(screen).register(
                                (s, mouseX, mouseY, button) -> {
                                    if (button == 1) {
                                        copyButton.endDrag();
                                        savePosition();
                                    }
                                }
                        );
                    }
                }
        );
    }

    private static void loadPosition() {
        if (!Files.exists(CONFIG_FILE)) {
            return;
        }

        Properties properties = new Properties();

        try (InputStream input = Files.newInputStream(CONFIG_FILE)) {
            properties.load(input);

            buttonX = Integer.parseInt(
                    properties.getProperty("x", "10")
            );
            buttonY = Integer.parseInt(
                    properties.getProperty("y", "10")
            );

            System.out.println("PackReport: button position loaded.");
        } catch (IOException | NumberFormatException e) {
            System.err.println(
                    "PackReport: couldn't load button position: "
                            + e.getMessage()
            );
        }
    }

    private static void savePosition() {
        Properties properties = new Properties();

        properties.setProperty("x", Integer.toString(buttonX));
        properties.setProperty("y", Integer.toString(buttonY));

        try {
            Files.createDirectories(CONFIG_FILE.getParent());

            try (OutputStream output =
                         Files.newOutputStream(CONFIG_FILE)) {
                properties.store(output, "PackReport button position");
            }

            System.out.println("PackReport: button position saved.");
        } catch (IOException e) {
            System.err.println(
                    "PackReport: couldn't save button position: "
                            + e.getMessage()
            );
        }
    }
}
