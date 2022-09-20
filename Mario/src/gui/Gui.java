package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Gui extends JFrame {
    
    /**
     * Erstellen einer "GUI" ohne eine Oberflaeche
     * Diese wird benoetigt um dort den Keylistener anzuhaengen
     */
    private JPanel drawingArea = new JPanel(null);

    public Gui() {
        super("Mario");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
