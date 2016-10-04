package me.hyperperform.entryexit;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Main
{
    
    void connect ( String portName ) throws Exception
    {
    	System.out.println("Connecting...");
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            System.out.println("Checking if serial port instance...");
            if ( commPort instanceof SerialPort )
            {
            	System.out.println("Is serial port...");

                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();



                //---------------------------------------------------------------------------------------------
                byte[] buffer = new byte[1024];
	            int len = -1;
	            try
	            {	
	            	System.out.println("Started reading from device...");
//	                while ((len = in.read(buffer)) > -1)
//	                    	System.out.println(new String(buffer, 0, len));

                    while(true)
                    {
                        if (in.available() > 1)
                        {
                            len = in.read(buffer);
                            System.out.println(new String(buffer, 0, len));
                        }

                    }
	            }
	            catch ( IOException e )
	            {
	                e.printStackTrace();
	           		System.out.println("Ended reading by exception...");
	            }            
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