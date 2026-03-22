package net.lixis9.eventjar.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.lixis9.eventjar.entity.FaultEntity;
import net.lixis9.eventjar.entity.ScavengerEntity;
import net.lixis9.eventjar.entity.ErrundefineEntity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerDeathHandler {
    
    private static boolean isShowingError = false;
    
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            // Серверная часть (работает в синглплеере и мультиплеере)
            handleServerDeath(event);
        } else if (FMLEnvironment.dist.isClient() && event.getEntity() instanceof Player) {
            // Клиентская часть (только для синглплеера)
            handleSingleplayerDeath(event);
        }
    }
    
    private static void handleServerDeath(LivingDeathEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        
        if (event.getSource().getEntity() instanceof FaultEntity || event.getSource().getEntity() instanceof ScavengerEntity || event.getSource().getEntity() instanceof ErrundefineEntity) {
            // Отключаем игрока от сервера с простым сообщением об ошибке
            player.connection.disconnect(
                Component.literal("§4§lITS ALL YOUR FAULT")
            );
        }
    }
    
    private static void handleSingleplayerDeath(LivingDeathEvent event) {
        Player player = (Player) event.getEntity();
        
        if ((event.getSource().getEntity() instanceof FaultEntity || event.getSource().getEntity() instanceof ScavengerEntity || event.getSource().getEntity() instanceof ErrundefineEntity) && 
            player == Minecraft.getInstance().player && 
            !isShowingError) {
            
            isShowingError = true;
            
            Minecraft.getInstance().execute(() -> {
                // Небольшая задержка для корректной обработки смерти
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // Игнорируем прерывание
                }
                
                // Закрываем мир и показываем экран отключения
                if (Minecraft.getInstance().isLocalServer()) {
                    Minecraft.getInstance().clearLevel();
                }
                
                // Показываем экран отключения с простым сообщением
                Component disconnectMessage = Component.literal("§4§lITS ALL YOUR FAULT");
                
                Minecraft.getInstance().setScreen(
                    new DisconnectedScreen(
                        Minecraft.getInstance().screen,
                        Component.literal("§4§lDISCONNECTED"),
                        disconnectMessage
                    )
                );
                
                // Добавляем задержку перед показом диалогов
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // Игнорируем прерывание
                }
                
                // Показываем серию диалогов ошибки
                SwingUtilities.invokeLater(() -> {
                    showErrorSequence();
                });
            });
        }
    }
    
    private static void showErrorSequence() {
        // Настраиваем внешний вид диалогов
        setupErrorDialogStyle();
        
        // Первый диалог - предупреждение
        JOptionPane.showMessageDialog(null,
            "WARNING: CRITICAL SYSTEM ERROR DETECTED\n\n" +
            "Entity 'ERR.UNDEFINE' has terminated player process.\n" +
            "System integrity compromised.\n\n" +
            "Press OK to continue...",
            "SYSTEM WARNING",
            JOptionPane.WARNING_MESSAGE);
        
        // Второй диалог - ошибка
        JOptionPane.showMessageDialog(null,
            "FATAL ERROR: PLAYER TERMINATION\n\n" +
            "ITS ALL YOUR FAULT\n\n" +
            "You have been disconnected from the world.\n" +
            "Game will now close.\n\n" +
            "Error Code: ERRUNDEFINE_ENTITY_TERMINATION",
            "FATAL ERROR",
            JOptionPane.ERROR_MESSAGE);
        
        // Третий диалог - финальное сообщение
        JOptionPane.showMessageDialog(null,
            "GOODBYE\n\n" +
            "You shouldn't have let it find you.\n" +
            "Now you're gone forever.\n\n" +
            "Game terminating...",
            "FINAL MESSAGE",
            JOptionPane.ERROR_MESSAGE);
        
        // Создаем финальное окно с эффектом
        createFinalErrorWindow();
    }
    
    private static void setupErrorDialogStyle() {
        // Настраиваем внешний вид диалогов
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
        
        // Делаем окно поверх всех остальных
        errorFrame.setAlwaysOnTop(true);
        
        // Настраиваем внешний вид окна
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
        
        // Показываем окно
        errorFrame.setVisible(true);
        
        // Запускаем таймер для закрытия
        Timer timer = new Timer(5000, e -> {
            errorFrame.dispose();
            System.exit(1);
        });
        timer.setRepeats(false);
        timer.start();
        
        // Добавляем обработчик закрытия окна
        errorFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(1);
            }
        });
    }
}



