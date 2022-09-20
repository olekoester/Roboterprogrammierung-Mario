package sensor;

import java.rmi.RemoteException;
import lejos.remote.ev3.RMISampleProvider;
import lejos.remote.ev3.RemoteEV3;




public class TouchSensor {
    
    private  RMISampleProvider sampleProvider;

    /**
     * Erstellt RMISampleprovider fuer den Touchsensor im Modus Touch auf Port S1
     * @param ev RemoteEV3 Objekt
     */
    public TouchSensor(RemoteEV3 ev) {
        sampleProvider = ev.createSampleProvider("S1", "lejos.hardware.sensor.EV3TouchSensor", "Touch");
    }
    
    /**
     * Der Sample Provider liefert immer einen Float zurck welcher auf einen Boolean gemapped wird
     * @throws RemoteException
     * @return true wenn das erste Element im Sample Provider groeßer 0 ist.
     */
    public boolean isTouched() throws RemoteException {
        float[] touched = sampleProvider.fetchSample();
        return touched[0] > 0;
    }

    /**
     * Schließen der Remote Verbindung um den Port wieder frei zu geben.
     */
    public void close() {
        try {
            sampleProvider.close();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
