package controller;

import java.rmi.RemoteException;

import drive.Drive;
import drive.Manual;
import gui.Gui;
import lejos.hardware.Sound;
import lejos.remote.ev3.RemoteEV3;
import lejos.utility.Delay;
import sensor.ColorSensor;
import sensor.TouchSensor;
import sensor.UltraSonicSensor;


public class Controller extends Thread {

    static final float SPEED_PERCENT = 100;

    // 0 = Ausweichen, 1 = Kolonne
    static final int MODE = 0;

    static RemoteEV3 ev = null;
    static ColorSensor cs = null;
    static TouchSensor ts = null;
    static UltraSonicSensor us = null;
    static Drive drive = null;
    static float actIntensity;
    static float intensityWhite;
    static float intensityBlack;
    static float integral = 0;
    static float oldIntensity = 899f;
    static long timeLastIntegral = 0;

    public Controller() {
        try {
            ev = new RemoteEV3("192.168.2.44");
            ev.setDefault();
            ts = new TouchSensor(ev);
            us = new UltraSonicSensor(ev);
            drive = new Drive(ev);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static float getProportional(float intensity) {
        return (((50 - intensity) * 2));
    }

    /**
     * Staerkeres Gegenlenken nach einer bestimmten Zeit
     * @param percent
     */
    public static void setIntegral(float percent) {
        long timeElapsed = System.currentTimeMillis() - timeLastIntegral;
        if (percent > -10 && percent < 10) {
            integral = 0;
        }
        if (integral <= 20) {
            integral += 0.1;
        }
        timeLastIntegral = System.currentTimeMillis();
    }

    /**
     * 
     * @param percent
     * @return float
     */
    public static float getDifferential(float percent) {
        float result = percent - oldIntensity;
        oldIntensity = percent;
        result = result < 0 ? -result : result;
        return 0;
    }

    /**
     * Nimmt den Intensivitaet Wert fuer Schwarz und Weiß auf durchs druecken des
     * Buttons und berechnet ein Sollwert
     * 
     * @throws RemoteException
     * @throws InterruptedException
     */
    public static void calibrate() throws RemoteException, InterruptedException {
        System.out.println("Auf Weiß stellen und Button bestätigen: ");
        Sound.beep();
        while (!ts.isTouched());
        intensityWhite = cs.getIntensity();
        Sound.beep();
        Delay.msDelay(1000);
        System.out.println("Wert fuer weiß: " + intensityWhite);
        System.out.println("Auf Schwarz stellen und Button betätigen: ");
        while(!ts.isTouched());
        Sound.beep;
        intensityBlack = cs.getIntensity();
        Delay.msDelay(1000);
        System.out.println("Wert fuer Schwarz: " + intensityBlack);
        actIntensity = ((intensityWhite + intensityBlack) / 1.2f);
    }

    /**
     * Fahr-Methode
     * TouchSensor als Abbruchbedingung um das Programm zu beenden
     * Bekommen und verarbeiten der Werte aus dem ColorSensor und uebergeben an die Steuerung
     * switch fuer Modus (Kolonne oder Ausweichen)
     * @throws RemoteException
     * @throws InterruptedException
     */
    public static void driveLine() throws RemoteException, InterruptedException {
        cs = new ColorSensor(ev);
        calibrate();
        while(!ts.isTouched()) {
            float intensity = cs.getIntensity();
            float intPercent = ((intensity - intensityBlack) / (intensityWhite - intensityBlack)) * 100;
            if (intensity > 100) {
                intensity = 100;
            }
            if (intensity < 0) {
                intensity = 0;
            }
            float percent = getProportional(intPercent);
            percent = intPercent < 50 ? percent / 1.7f : percent;
            setIntegral(intPercent);
            System.out.println(integral);
            float differential = getDifferential(intensity);
            percent += integral;

            if (percent < 0) {
                percent -= differential;
            } else {
                percent += differential;
            }

            float speed = SPEED_PERCENT;
            percent = percent > 100 ? 100 : percent;
            percent = percent < -100 ? -100 : percent;
            switch(Mode) {
                case 0:
                    drive.drive(speed, percent);
                    if (us.getDistance() < 0.15) {
                        avoidProtocol();
                    }
                    break;
                case 1:
                    drive.drive(us.getDistanceMult(), percent); // Zum Kolonne fahren
                    break;
            }
        }
    }

    /**
     * Ausweichen eines anderen Roboters 
     */
    public static void avoidProtocol {
        try {
            drive.drive(200, 100);
            Delay.msDelay(700);
            drive.drive(200, 0);
            Delay.msDelay(2000);
            drive.drive(200, -100);
            Delay.msDelay(700);
            drive.drive(200, 0);
            float intensity = cs.getIntensity();
            float intPercent = ((intensity - intensityBlack) / (intensityWhite - intensityBlack)) * 100;
            long startTime = System.currentTimeMillis();
            long currentTime = System.currentTimeMillis();
            while ((currentTime - startTime) <= 3000 && (intPercent > 55) && (!ts.isTouched())) {
                intensity = cs.getIntensity();
                intPercent = ((intensity - intensityBlack) / (intensityWhite - intensityBlack)) * 100;
                currentTime = System.currentTimeMillis();

            }
            if ((intPercent > 55) && (!ts.isTouched())) {
                drive.drive(200, -100);
                Delay.msDelay(700);
            }
            drive.drive(80, 0);
            while ((intPercent > 55) && (!ts.isTouched())) {
                intensity = cs.getIntensity();
                intPercent = ((intensity - intensityBlack) / (intensityWhite - intensityBlack)) * 100;
                currentTime = System.currentTimeMillis();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Steuern des Robots mithilfe der Tastatur
     * @throws RemoteException
     */
    public static void manualSteering() throws RemoteException {
        Gui gui = new Gui();
        Manual keyControl = new Manual();
        gui.addKeyListener(keyControl);
        while (!ts.isTouched()) {
            drive.drive(keyControl.getSpeed(), keyControl.getTurn());
            if (keyControl.getHonk() == true) {
                Sound.beep();
            }
        }
    }

    /**
     * legt fest, ob an einer Linie gefahren werden soll oder manuell gesteuert
     */
    public void run() {
        try {
            driveLine();
            // manualSteering();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            try {
                drive.close();
                ts.close();
                cs.close();
                us.close();
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }
    /**
     * ShutdownHook um sicher zu gehen das alle Sensoren geschlossen werden,
     * falls das Programm nicht richtig beendet wird
     * @param args
     * @throws RemoteException
     */
    public static void main(String[] args) throws RemoteException {
		Controller thread = new Controller();
		thread.start();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					System.out.println("SHUTDOWN HOOK");
					drive.close();
					ts.close();
					cs.close();
					us.close();
				} catch (Exception e) {
					System.err.println(e);
				}
			}
		});
	}

}