package drive;

import java.rmi.RemoteException;


import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.remote.ev3.RemoteEV3;


public class Drive {
    
    RMIRegulatedMotor leftMotor = null; // Motor.B;
    RMIRegulatedMotor rightMotor = null; // Motor.C;
    // Geschwindigkeit, die bei 100% erreicht wird
    private int MAX_SPEED = 100;

    /**
     * Initialisiert beide Motoren
     * @param ev
     */
    public Drive(RemoteEV3 ev) {
        leftMotor = ev.createRegulatedMotor("B", "L");
        rightMotor = ev.createRegulatedMotor("C", "L");
    }

    /**
     * Setzt die Geschwindigkeit, der Motoren.
     * Bei negativen Werten fuehrt der Motor rueckwaerts
     * @param speedL Geschwindigkeit linker Motor
     * @param speedR Geschwindigkeit rechter Motor
     */
    public void setEngines(int speedL, int speedR) throws RemoteException {
        if (speedL < 0) {
            leftMotor.setSpeed(-speedL);
            leftMotor.backward();
        } else {
            leftMotor.setSpeed(speedL);
            leftMotor.forward();
        }

        if (speedR < 0) {
            rightMotor.setSpeed(-speedR);
            rightMotor.backward();
        } else {
            rightMotor.setSpeed(speedR);
            rightMotor.forward();
        }
    }

    /**
     * Berechnet die Geschwindigkeit der beiden Motoren in Abhaengigkeit der Geschwindigkeit und Lenkung.
     * Die Werte werden an setEngines uebergeben.
     * @param speed Geschwindigkeit des Roboters
     */
    public void drive(float speed, float turn) throws RemoteException {
        int speedAct = Math.round((MAX_SPEED * (speed / 100)));
        int leftMotor = speedAct;
        int rightMotor = speedAct;
        if (turn < 0) {
            leftMotor = Math.round((speedAct * (1 - (turn / 50))));
        } else if (turn > 0) {
            rightMotor = Math.round((speedAct * (1 - (turn / 50))));
        }
        setEngines(leftMotor, rightMotor);
    }

    /**
     * Stoppt beide Motoren
     * @throws RemoteException
     */
    public void stop() throws RemoteException {
        leftMotor.stop(true);
        rightMotor.stop(true);
    }

    /**
     * Schließt beide Motoren
     * Dafuer werden die Motoren mithilfe von close() erst einmal gestoppt.
     */
    public void close() {
        try {
            stop();
            leftMotor.close();
            rightMotor.close();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}