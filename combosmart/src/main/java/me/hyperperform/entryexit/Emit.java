//package me.hyperperform.entryexit;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.io.*;

public class Emit
{
	ArrayList<String> barcodes;
	ArrayList<String> eventData;

	Emit()
	{
		loadData();
	}

	public void loadData()
	{
		barcodes = new ArrayList<String>();
		eventData = new ArrayList<String>();
		try {
		    BufferedReader in = new BufferedReader(new FileReader("database.db"));
		    String str;
		    while ((str = in.readLine()) != null)
	        {
				String[] a = str.split("`");
						
				barcodes.add(a[0]);
				eventData.add(a[1]);       	
	        }
		    in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printData()
	{
		for (int i = 0; i < barcodes.size(); i++)
		{
			System.out.println("Barcode: " + barcodes.get(i));
			System.out.println("Event: " + eventData.get(i));
			System.out.println();
		}
	}

	public Boolean sendEvent(String bar)
	{
		System.out.println("Sending event .... " + bar);
		loadData();
//		printData();
		String ev = bar;
		boolean flag  = false;
		for (int i = 0; i < barcodes.size(); i++)
		{
			System.out.println(barcodes.get(i) + " <>  " + bar);
			if (barcodes.get(i).equals(bar))
			{
				ev = eventData.get(i);
				ev += "\"timestamp\":\"" + new Timestamp(new Date().getTime()).toString().replace(" ", "T") + "\",\"day\":1 }";
				System.out.println("Barcode");

				return send(ev);
			}
		}

		return qrCode(bar);

//		return true;
	}

	public Boolean qrCode(String bar)
	{
		System.out.println("QRCode");
		Timestamp t = new Timestamp(new Date().getTime());
		String time = t.toString();
		bar = "{" + bar;
		bar += ", \"deviceID\": \"COMBOSMART-RSL156340\"";
		bar += ", \"timestamp\": \"" + time.replace(" ", "T") ;
		bar += "\"}";

		send(bar);

		return true;
	}


	public Boolean send(String ev)
	{

		try
		{
			System.out.println("Sending event");
			URL url = new URL("http://localhost:8080/hyperperform-system-1.0-SNAPSHOT/rs/AccessEvent");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("content-type", "application/json");

			connection.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			System.out.println(ev);
			out.writeBytes(ev);
			out.flush();
			out.close();

			System.out.println("Response code: " + connection.getResponseCode());
			System.out.println("Response message: " + connection.getResponseMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}