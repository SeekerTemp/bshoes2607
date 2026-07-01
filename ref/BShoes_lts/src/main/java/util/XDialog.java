package poly.cafe.util;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class XDialog {
    public static void alert(String message){
        XDialog.alert(message, "Thông báo!");
    }
    public static void alert(String message, String title){
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }
    
    public static boolean confirm(String message){
        return XDialog.confirm(message, "Xác nhận!");
    }
    public static boolean confirm(String message, String title){
        int result = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return (result == JOptionPane.YES_OPTION);
    }
    
    public static String prompt(String message){
        return XDialog.prompt(message, "Nhập vào!");
    }
    public static String prompt(String message, String title){
        return JOptionPane.showInputDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
        public static void autoClose(String message, int delayMs) {
        JOptionPane pane = new JOptionPane(
                message,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{}
        );

        JDialog dialog = pane.createDialog("Thông báo");
        dialog.setModal(false); // không chặn UI
        dialog.setVisible(true);

        new Timer(delayMs, e -> {
            dialog.setVisible(false);
            dialog.dispose();
        }).start();
    } 
}