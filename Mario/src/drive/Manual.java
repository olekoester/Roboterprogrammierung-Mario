package drive;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
/**
 * Keylistener zum einlesen der Tasten
 * Die Variablen liefern Werte zurueck, welche im Controller an Drive uebergeben werden
 */
public class Manual implements KeyListener {

    private float speed;
    private float turn;
    private boolean honk;
    private boolean exit;

    public Manual() {
        turn = 0;
        speed = 0;
        honk = false;
        exit = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Vergleicht erhaltenden Keycode mit eingestellten Tasten, zum Beeinflussen des Fahrverhaltens
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_D && turn < 90) {
			turn = 49;
        } else if (e.getKeyCode() == KeyEvent.VK_A && turn > -90) {
			turn = -49;
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
			speed = -20;
		} else if (e.getKeyCode() == KeyEvent.VK_W) {
			speed = 80;
        } else if (e.getKeyCode() == KeyEvent.VK_F5) {
			exit = true;
        } else if (e.getKeyCode() == KeyEvent.VK_Q) {
			turn = -91;
			speed = 20;
		} else if (e.getKeyCode() == KeyEvent.VK_E) {
			turn = 91;
			speed = 20;
		} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			honk = true;
		}
    }

    //Getter Methoden
    public float getSpeed() {
        return speed;
    }

    public float getTurn() {
        return turn;
    }

    public boolean getHonk() {
        return honk;
    }

    public boolean getExit() {
        return exit;
    }
}
