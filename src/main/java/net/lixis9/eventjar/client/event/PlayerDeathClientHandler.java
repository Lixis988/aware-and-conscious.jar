package net.lixis9.eventjar.client.event;

import net.lixis9.eventjar.entity.ErrundefineEntity;
import net.lixis9.eventjar.entity.FaultEntity;
import net.lixis9.eventjar.entity.ScavengerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@Mod.EventBusSubscriber(modid = "eventjar", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PlayerDeathClientHandler {

    private static boolean isShowingError = false;

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (player instanceof net.minecraft.server.level.ServerPlayer) {
            return;
        }
        if ((event.getSource().getEntity() instanceof FaultEntity
                || event.getSource().getEntity() instanceof ScavengerEntity
                || event.getSource().getEntity() instanceof ErrundefineEntity)
                && player == Minecraft.getInstance().player
                && !isShowingError) {

            isShowingError = true;

            Minecraft.getInstance().execute(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                if (Minecraft.getInstance().isLocalServer()) {
                    Minecraft.getInstance().clearLevel();
                }

                Component disconnectMessage = Component.literal("§4§lITS ALL YOUR FAULT");

                Minecraft.getInstance().setScreen(
                        new DisconnectedScreen(
                                Minecraft.getInstance().screen,
                                Component.literal("§4§lDISCONNECTED"),
                                disconnectMessage
                        )
                );

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                SwingUtilities.invokeLater(PlayerDeathClientHandler::showErrorSequence);
            });
        }
    }

    private static void showErrorSequence() {
        setupErrorDialogStyle();

        JOptionPane.showMessageDialog(null,
                "WARNING: CRITICAL SYSTEM ERROR DETECTED\n\n" +
                        "Entity 'ERR.UNDEFINE' has terminated player process.\n" +
                        "System integrity compromised.\n\n" +
                        "Press OK to continue...",
                "SYSTEM WARNING",
                JOptionPane.WARNING_MESSAGE);

        JOptionPane.showMessageDialog(null,
                "FATAL ERROR: PLAYER TERMINATION\n\n" +
                        "ITS ALL YOUR FAULT\n\n" +
                        "You have been disconnected from the world.\n" +
                        "Game will now close.\n\n" +
                        "Error Code: ERRUNDEFINE_ENTITY_TERMINATION",
                "FATAL ERROR",
                JOptionPane.ERROR_MESSAGE);

        JOptionPane.showMessageDialog(null,
                "GOODBYE\n\n" +
                        "You shouldn't have let it find you.\n" +
                        "Now you're gone forever.\n\n" +
                        "Game terminating...",
                "FINAL MESSAGE",
                JOptionPane.ERROR_MESSAGE);

        createFinalErrorWindow();
    }

    private static void setupErrorDialogStyle() {
        UIManager.put("OptionPane.background", Color.BLACK);
        UIManager.put("Panel.background", Color.BLACK);
        UIManager.put("OptionPane.messageForeground", Color.RED);
        UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.BOLD, 14));
        UIManager.put("Button.background", Color.DARK_GRAY);
        UIManager.put("Button.foreground", Color.RED);
        UIManager.put("Button.focus", Color.RED);
    }

    private static void createFinalErrorWindow() {
        JFrame errorFrame = new JFrame("CRITICAL SYSTEM FAILURE");
        errorFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        errorFrame.setSize(600, 400);
        errorFrame.setLocationRelativeTo(null);
        errorFrame.setResizable(false);
        errorFrame.setAlwaysOnTop(true);
        errorFrame.getContentPane().setBackground(Color.BLACK);

        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setLayout(new BorderLayout());

        JLabel errorLabel = new JLabel("<html><center><font color='red' size='6'>SYSTEM CRASH</font><br><br>" +
                "<font color='white' size='4'>ITS ALL YOUR FAULT</font><br><br>" +
                "<font color='gray' size='3'>Game will close in 5 seconds...</font></center></html>");
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel.setVerticalAlignment(SwingConstants.CENTER);

        panel.add(errorLabel, BorderLayout.CENTER);
        errorFrame.add(panel);
        errorFrame.setVisible(true);

        Timer timer = new Timer(5000, e -> {
            errorFrame.dispose();
            System.exit(1);
        });
        timer.setRepeats(false);
        timer.start();

        errorFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(1);
            }
        });
    }
}
