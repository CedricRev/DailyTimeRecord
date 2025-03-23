package fdtr.files;

import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

import javax.swing.*;
import java.awt.*;

public class PreviewPDF {

    public void generatePdf()  {
        String fileName = "src/resources/files/fdtr.pdf";
        SwingController controller = new SwingController();

        SwingViewBuilder factory = new SwingViewBuilder(controller);

        JPanel viewerComponentPanel = factory.buildViewerPanel();

        controller.getDocumentViewController().setAnnotationCallback(
                new org.icepdf.ri.common.MyAnnotationCallback(
                        controller.getDocumentViewController()));

        JFrame applicationFrame = new JFrame();
        applicationFrame.setTitle("Faculty Daily Time Record - Print Preview");
        applicationFrame.setPreferredSize(new Dimension(1000, 1000));
        applicationFrame.setAlwaysOnTop (true);
        applicationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        applicationFrame.getContentPane().add(viewerComponentPanel);

        controller.openDocument(fileName);

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SwingUtilities.invokeLater( new Runnable( )
                {
                    @Override
                    public void run( )
                    {
                        try {
                            // Set System L&F
                            UIManager.setLookAndFeel(
                                    UIManager.getSystemLookAndFeelClassName());
                            Font f = new Font("sans-serif", Font.BOLD, 12);
                            UIManager.put("Menu.font", f);
                            UIManager.put("MenuItem.font", f);
                            UIManager.put("Button.font", f);
                        }
                        catch (UnsupportedLookAndFeelException | InstantiationException | ClassNotFoundException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        applicationFrame.pack();
                        applicationFrame.setVisible(true);
                    }

                } );

            }
        });




    }


}
