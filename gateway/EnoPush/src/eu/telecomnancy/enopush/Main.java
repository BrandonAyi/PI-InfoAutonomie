package eu.telecomnancy.enopush;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.aleon.aleoncean.packet.ESP3Packet;
import eu.aleon.aleoncean.packet.EnOceanId;
import eu.aleon.aleoncean.packet.PacketType;
import eu.aleon.aleoncean.packet.RadioPacket;
import eu.aleon.aleoncean.packet.ResponsePacket;
import eu.aleon.aleoncean.rxtx.ESP3Connector;
import eu.aleon.aleoncean.rxtx.ReaderShutdownException;
import eu.aleon.aleoncean.rxtx.USB300;
import eu.telecomnancy.enopush.telegram.DataManager;
import eu.telecomnancy.enopush.telegram.TeachedDevices;

/**
 * Main class running the Gateway.
 * @author Mickael
 *
 */
public class Main {
	/**
	 * Logger to use to write logs while running.
	 */
	private static final Logger log = Logger.getLogger( Main.class.getName() );
	/**
	 * Timeout in seconds to wait a radio packet on the interface. This freezes the main thread.
	 */
	private static final long TIMEOUT = 2;
	/**
	 * The serial interface.
	 */
	public static ESP3Connector serialConnection = new USB300();
	/**
	 * Boolean to know if the ESP3Connector is ready to quit.
	 */
	public static boolean ready = true;

	/**
	 * Runs the program.
	 * @param args you have to provide in args on which serial interface you would like to connect (example : /dev/ttyAMA0).
	 * Make sure the serial interface provided is available and not in use by the system.
	 */
	public static void main(String[] args) {
		
		Settings.init();
		String serialDevice = Settings.getProperty("default_serial");
		
		if(args.length > 0) {
			serialDevice = args[0];
		}
		
		System.setProperty("gnu.io.rxtx.SerialPorts", serialDevice);
		System.setProperty("java.library.path", Settings.getProperty("lib_path"));
		
		if(!serialConnection.connect(serialDevice)) {
			log.log(Level.SEVERE, "Impossible to connect to the specified serial interface");
			System.exit(1);
		}
		log.log(Level.INFO, "Connected to the serial interface " + serialDevice);
		
		System.out.println("Base ID: " + getBaseId());
		
		if(TeachedDevices.loadDevices() > 0) {
			log.log(Level.INFO, "Loaded some devices from file: \n" + TeachedDevices.devicesToString());
			DataManager.sendDevices();
		}
		
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	if(ready)
		    		serialConnection.disconnect();
				else {
					try {
						Thread.sleep(1000*TIMEOUT);
					} catch (InterruptedException e) {
					}
				}
		    }
		 });
		
		while(true) {
			try {
				ready = false;
				ESP3Packet packet = serialConnection.read(TIMEOUT, TimeUnit.SECONDS);
				ready = true;
				if(packet != null) {
					// System.out.println(packet.toString());
					
					if(packet.getPacketType() == PacketType.RADIO) {
						RadioPacket radioPacket = new RadioPacket(packet.getData()[0]);
						radioPacket.setData(packet.getData());
						radioPacket.setOptionalData(packet.getOptionalData());
						DataManager.process(radioPacket);
						TeachedDevices.addDevice(radioPacket);
					}
				}
			} catch (ReaderShutdownException e) {
				e.printStackTrace();
			}
		}

	}

	public static EnOceanId getBaseId() {
		if(serialConnection == null)
			return null;
		ESP3Packet packet = new ESP3Packet();
		packet.setPacketType((byte) 0x05);
		byte[] query = {0x08};
		packet.setData(query);
		ResponsePacket response = serialConnection.write(packet);
		if(response.getReturnCode() == 0 && response.getData().length == 5)
		{
			byte addr[] = new byte[4];
			addr[0] = response.getData()[1];
			addr[1] = response.getData()[2];
			addr[2] = response.getData()[3];
			addr[3] = response.getData()[4];
			EnOceanId baseId = new EnOceanId(addr);
			
			return baseId;
		}
		
		return null;
	}

}
