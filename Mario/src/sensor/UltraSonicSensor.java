package sensor;

import java.rmi.RemoteException;
import lejos.remote.ev3.RMISampleProvider;
import lejos.remote.ev3.RemoteEV3;

public class UltraSonicSensor {
	
	private RMISampleProvider sampleProvider;
	
	/**
	 * Erstellt sampleProvider fr den UltrasonicSensor auf den Port S2
	 * @param ev
	 */
	public UltraSonicSensor(RemoteEV3 ev) {
		sampleProvider = ev.createSampleProvider("S2", "lejos.hardware.sensor.EV3UltrasonicSensor", "Distance");
	}

	public float getDistance() throws RemoteException {
		float[] sample = { 0 };
		try {
			sample = sampleProvider.fetchSample();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return sample[0];
	}
	
	/**
	 * Ermittelt den Abstand zu einem Objekt,
	 * passt die Abstandswerte auf einer Prozentskala von 0 - 100 % an
	 * @return float
	 * @throws RemoteException
	 */
	public float getDistanceMult() throws RemoteException {
		//00.5min 0.2max 
		float tmp = (float) ((((getDistance())-0.05)*(100/0.15)));
		if(tmp < 0.0)
			return 0;
		else if(tmp > 100)
			return 100;
		else return tmp;
	}
	
	public void close() {
		try {
			sampleProvider.close();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
