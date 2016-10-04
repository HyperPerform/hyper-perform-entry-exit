package me.hyperperform.entryexit;

import gnu.io.*;
import java.io.InputStream;


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
                        System.out.println(new String(buffer, 0, buffer.length));
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
    	System.out.println("Connecting to device");
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            System.out.println("Checking if serial port instance");
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
            (new Main()).connect("COM3");
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }
}