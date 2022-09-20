package sensor;

import java.rmi.RemoteException;
import lejos.remote.ev3.RMISampleProvider;
import lejos.remote.ev3.RemoteEV3;


public class ColorSensor {
    
    private RMISampleProvider sampleProvider;

    /**
     * Initalisiert den ColorSensor
     * @param ev
     */
    public ColorSensor(RemoteEV3 ev) {
        sampleProvider = ev.createSampleProvider("S3", "lejos.hardware.sensor.EV3ColorSensor", "Red");
    }

    /**
     * liest den aktuellen Intensitaetswert aus
     * @return liefert Intesitaet als float zwischen 0 un 1 zurueck
     * @throws RemoteException
     */
    public float getIntensity() throws RemoteException {
        float[] sample = { 0 };
        try {
            sample = sampleProvider.fetchSample();
        } catch (Exception e) {
            System.err.print(e);
        }
        return sample[0];
    }

    /**
     * Schließt den Colorsensor
     */
    public void close() {
        try {
            sampleProvider.close();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
