package me.hyperperform.entryexit;

import gnu.io.*;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Scanner;

@SuppressWarnings("unchecked")
public class Main
{
    InputStream in = null;

    public class DataEventListener implements SerialPortEventListener
    {
        DataEventListener()
        {
            System.out.println("Initialising data event listener");
        }

        public void serialEvent(SerialPortEvent serialPortEvent)
        {
            if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE)
            {
                try
                {
                    if (in.available() > 0)
                    {
                        byte[] buffer = new byte[in.available()];
                        in.read(buffer);

                        System.out.println("");
                        System.out.println("Card scanned event at " + new Date());

                        String event = new String(buffer, 0, buffer.length);
                        event += ", \"deviceID\": \"honeywell\"";
                        event += ", \"day\": \"0\"";
                        event += ", \"timestamp\": \"" + new Timestamp(new Date().getTime()).toString().replace(" ", "T") + "\"";
                        event = "{" + event + "}";

                        System.out.println("Sending event");
                        URL url = new URL("http://localhost:8080/hyperperform-system-1.0-SNAPSHOT/rs/AccessEvent");
                        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("content-type", "application/json");

                        connection.setDoOutput(true);
                        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                        out.writeBytes(event);
                        out.flush();
                        out.close();

                        System.out.println("Response code: " + connection.getResponseCode());
                        System.out.println("Response message: " + connection.getResponseMessage());
                    }
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    
    void connect ( String portName ) throws Exception
    {
    	System.out.println("Connecting to " + portName);
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
            
            System.out.println("Checking if " + portName + " is a serial port instance");
            if ( commPort instanceof SerialPort )
            {
            	System.out.println("Device is serial port instance");

                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                in = serialPort.getInputStream();

                //---------------------------------------------------------------------------------------------

                serialPort.addEventListener(new DataEventListener());
                serialPort.notifyOnDataAvailable(true);

//	                while ((len = in.read(buffer)) > -1)
//	                    	System.out.println(new String(buffer, 0, len));

//                    while(true)
//                    {
//                        if (in.available() > 1)
//                        {
//                            len = in.read(buffer);
//                            System.out.println(new String(buffer, 0, len));
//                        }
//
//                    }

                System.out.println("Initialisation complete");

                //---------------------------------------------------------------------------------------------
            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this Main.");
            }
        }     
    }
    
    public static void main(String[] args)
    {
        try
        {
            System.out.println("Searching for devices");
            ArrayList<String> list = getPorts();

            System.out.println(list.size() + " devices found");

            if (list.size() > 0)
            {
                Scanner sc = new Scanner(System.in);

                System.out.println("Pick CommPort from following list:");
                for (int k = 0; k < list.size(); k++)
                    System.out.println(k + " - " + list.get(k));

                System.out.print(">>> ");
                int selection = sc.nextInt();
                (new Main()).connect(list.get(selection));
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    static ArrayList<String> getPorts()
    {
        Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
        ArrayList<String> list = new ArrayList<String>();

        while(ports.hasMoreElements())
        {
            CommPortIdentifier c = ports.nextElement();
            if (c.getPortType() == CommPortIdentifier.PORT_SERIAL)
                list.add(c.getName());
        }

        return list;
    }
}