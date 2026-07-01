package camera;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 * @author Nguyen Trung Nghia
 */
public class qr_scanner extends JFrame implements Runnable {
    private Webcam webcam;
    private WebcamPanel panel;
    private Thread scanThread;
    private volatile boolean running = false;
    private volatile boolean shouldClose = false;

    public interface QRListener {
        void onDetected(String text);
        void onCancelled();
    }

    private QRListener listener;

    public qr_scanner(QRListener listener) {
        this.listener = listener;
        init();
    }

    private void init() {
        setTitle("QR Scanner");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                handleCancel();
            }
        });

        java.util.List<Webcam> cams = Webcam.getWebcams();

        if (cams.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No camera found on this computer!", 
                "Camera Error", 
                JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        System.out.println("Available cameras:");
        for (int i = 0; i < cams.size(); i++) {
            System.out.println(i + ": " + cams.get(i).getName());
        }

        webcam = cams.get(0); 
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        if (!initializeWebcam()) {
            JOptionPane.showMessageDialog(this, 
                "Failed to open camera. Make sure:\n" +
                "1. Camera is not in use by another app\n" +
                "2. You have permission to use the camera\n" +
                "3. Camera drivers are installed", 
                "Camera Error", 
                JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setMirrored(true);

        add(panel);
        setVisible(true);


        running = true;
        shouldClose = false;
        scanThread = new Thread(this, "QR-Scanner-Thread");
        scanThread.setDaemon(false);
        scanThread.start();
    }

    private boolean initializeWebcam() {
        try {
            Thread.sleep(500);
            webcam.open();
            
            if (!webcam.isOpen()) {
                System.err.println("Camera failed to open");
                return false;
            }

            System.out.println("✓ Camera opened successfully");
            return true;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            System.err.println("✗ Error opening camera: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void run() {
        while (running && !shouldClose) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            if (webcam == null || !webcam.isOpen()) {
                continue;
            }

            BufferedImage image = null;
            try {
                image = webcam.getImage();
            } catch (Exception e) {
                System.err.println("Error capturing frame: " + e.getMessage());
                continue;
            }

            if (image == null) {
                continue;
            }

            try {
                BinaryBitmap bitmap = new BinaryBitmap(
                        new HybridBinarizer(new BufferedImageLuminanceSource(image))
                );

                Result result = new MultiFormatReader().decode(bitmap);

                if (result != null && !shouldClose) {
                    System.out.println("✓ QR Code detected: " + result.getText());
                    running = false;
                    shouldClose = true;
                    
                    // Notify listener before cleanup
                    if (listener != null) {
                        try {
                            listener.onDetected(result.getText());
                        } catch (Exception ex) {
                            System.err.println("Error in listener: " + ex.getMessage());
                        }
                    }
                    
                    javax.swing.SwingUtilities.invokeLater(this::closeScanner);
                    return;
                }

            } catch (NotFoundException ignored) {

            } catch (Exception e) {
                System.err.println("Decoding error: " + e.getMessage());
            }
        }
    }

    private void handleCancel() {
        System.out.println("✓ Scanner cancelled by user");
        running = false;
        shouldClose = true;
        
        if (listener != null) {
            try {
                listener.onCancelled();
            } catch (Exception ex) {
                System.err.println("Error in listener: " + ex.getMessage());
            }
        }
        
        closeScanner();
    }

    public void closeScanner() {
        try {
            running = false;
            shouldClose = true;

            System.out.println("Closing scanner...");

            // Stop scanning thread
            if (scanThread != null && scanThread.isAlive()) {
                scanThread.interrupt();
                try {
                    scanThread.join(3000); // Wait max 3 seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                if (scanThread.isAlive()) {
                    System.err.println("⚠ Scan thread did not terminate properly");
                }
            }
            scanThread = null;

            // Close webcam - CRITICAL for reusability
            if (webcam != null && webcam.isOpen()) {
                try {
                    webcam.close();
                    System.out.println("✓ Camera closed successfully");
                } catch (Exception e) {
                    System.err.println("Error closing camera: " + e.getMessage());
                }
            }
            webcam = null;

            // Dispose panel
            if (panel != null) {
                try {
                    remove(panel);
                    panel.stop();
                } catch (Exception e) {
                    System.err.println("Error disposing panel: " + e.getMessage());
                }
                panel = null;
            }

            // Dispose frame
            try {
                dispose();
            } catch (Exception e) {
                System.err.println("Error disposing frame: " + e.getMessage());
            }

            System.out.println("✓ Scanner closed completely");

        } catch (Exception e) {
            System.err.println("Critical error in closeScanner: " + e.getMessage());
            e.printStackTrace();
        }
    }
}