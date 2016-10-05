//package me.hyperperform.entryexit;

import Pr22.DocumentReaderDevice;
import Pr22.Engine;
import Pr22.Events.*;
import Pr22.Imaging.RawImage;
import Pr22.Processing.*;
import PrIns.Exceptions.*;
import PrIns.Exceptions.NoSuchDevice;
import java.io.IOException;
import java.util.*;
import java.io.*;
//import me.hyperperform.entryexit.Emit;


public class Main {

    DocumentReaderDevice pr = null;
    String device = "Combo";
    public static Emit e = new Emit();

    public int open() throws General {

        System.out.println("Opening a device");
        System.out.println();
        pr = new DocumentReaderDevice();

        addDeviceEvents();

        try {
            pr.useDevice(0);
        } catch (PrIns.Exceptions.NoSuchDevice e) {
            System.out.println("No device found!");
            return 1;
        }
        device = pr.getDeviceName();
        System.out.println("The device " + pr.getDeviceName() + " is opened.");
        System.out.println();
        return 0;
    }
    //--------------------------------------------------------------------------

    public int program() throws General {

        //Devices can be manipulated only after opening.

        try {
            pr.useDevice(0);
        } catch (PrIns.Exceptions.NoSuchDevice e) {
            System.out.println("No device found!");
            return 1;
        }
        // addScanEvents();
        Pr22.DocScanner scanner = pr.getScanner();
        Engine ocrEngine = pr.getEngine();

        System.out.println("Capturing some images to read from.");
        Pr22.Task.DocScannerTask scanTask = new Pr22.Task.DocScannerTask();

        Page docPage;
        System.out.println();

        System.out.println("Capturing more images for VIZ reading and image authentication.");
        scanTask = new Pr22.Task.DocScannerTask();
        // //Reading from VIZ -except face photo- is available in special OCR engines only.
        scanTask.add(Pr22.Imaging.Light.All);
        docPage = scanner.scan(scanTask, Pr22.Imaging.PagePosition.Current);
        System.out.println();

        System.out.println("Reading all the textual and graphical field data as well as "
                + "authentication result from the Visual Inspection Zone.");
        Pr22.Task.EngineTask vizReadingTask = new Pr22.Task.EngineTask();
        vizReadingTask.add(FieldSource.Viz, FieldId.All);
        Document vizDoc = ocrEngine.analyze(docPage, vizReadingTask);

        System.out.println();

        System.out.println("Reading barcodes.");
        Pr22.Task.EngineTask bcReadingTask = new Pr22.Task.EngineTask();
        bcReadingTask.add(FieldSource.Barcode, FieldId.All);
        Document bcrDoc = ocrEngine.analyze(docPage, bcReadingTask);

        System.out.println();
        printDocFields(bcrDoc);
        // try {
        //     bcrDoc.save(Document.FileFormat.Xml).save("BCR.xml");
        // } catch (IOException e) {
        // }

        return 0;
    }

    public void init() throws General
    {
        if (open() != 0) {
            System.out.println("No device found to connect to exiting ...");
            System.exit(1);
        }



    }

    public void destroy() throws General
    {
        // pr.close();
    }
    //--------------------------------------------------------------------------

    /**
     * Prints a hexa dump line from a part of an array.
     *
     * @param arr The whole array.
     * @param pos Position of the first item to print.
     * @param sz Number of items to print.
     */
    static void printBinary(byte[] arr, int pos, int sz) {

        int p0;
        for (p0 = pos; p0 < arr.length && p0 < pos + sz; p0++) {
            System.out.printf("%02X ", arr[p0]);
        }
        for (; p0 < pos + sz; p0++) {
            System.out.print("   ");
        }
        for (p0 = pos; p0 < arr.length && p0 < pos + sz; p0++) {
            System.out.print(arr[p0] < 0x21 || arr[p0] > 0x7e ? '.' : (char) arr[p0]);
        }
        System.out.println();
    }
    //--------------------------------------------------------------------------


    static void printDocFields(Document doc) {

        List<FieldReference> fields = null;
        try {
            fields = doc.getFields();
        } catch (Exception e) {
        }

        for (FieldReference currentFieldRef : fields)
        {
            try
            {
                Field currentField = doc.getField(currentFieldRef);
                String value = "", formattedValue = "", standardizedValue = "";
                byte[] binValue = null;
                try
                {
                    value = currentField.getRawStringValue();
                }
                catch (PrIns.Exceptions.EntryNotFound e)
                {
                }
                catch (PrIns.Exceptions.InvalidParameter e)
                {
                    binValue = currentField.getBinaryValue();
                }
                try
                {
                    formattedValue = currentField.getFormattedStringValue();
                }
                catch (PrIns.Exceptions.EntryNotFound e)
                {
                }
                try
                {
                    standardizedValue = currentField.getStandardizedStringValue();
                }
                catch (PrIns.Exceptions.EntryNotFound e)
                {
                }
                Status status = Status.NoChecksum;
                try
                {
                    status = currentField.getStatus();
                }
                catch (PrIns.Exceptions.EntryNotFound e)
                {
                }
                String fieldName = currentFieldRef.toString();
                if (binValue != null)
                {
                    System.out.printf("  %1$-20s%2$-17sBinary%n", fieldName, status);
                    for (int cnt = 0; cnt < binValue.length; cnt += 16)
                    {
                        printBinary(binValue, cnt, 16);
                    }
                } else
                {
                    if (value != "")
                    {
                        System.out.println("Success");
                        e.sendEvent(value);
                    }
                }

                //     try {
                //         currentField.getImage().save(RawImage.FileFormat.Png).save(fieldName + ".png");
                //     } catch (Exception e) {
                //     }
                // } catch (Exception e) {
                // }
            }
            catch (Exception e)
            {
            }
            System.out.println();

            // try {
            //     for (FieldCompare comp : doc.getFieldCompareList()) {
            //         System.out.println("Comparing " + comp.field1 + " vs. "
            //                 + comp.field2 + " results " + comp.confidence);
            //     }
            //     System.out.println();
            // } catch (General ex) {
            // }
            }
            // return arr;
        }
        //--------------------------------------------------------------------------
        // Event handlers
        //--------------------------------------------------------------------------

    void addDeviceEvents() throws General {

        //----------------------------------------------------------------------

        pr.addEventListener(new Connection() {

//            @Override
            public void onConnection(ConnectionEventArgs e) {
                System.out.println("Connection event. Device number:" + e.deviceNumber);
            }
        });
        //----------------------------------------------------------------------

        pr.addEventListener(new DeviceUpdate() {

            public void onDeviceUpdate(UpdateEventArgs e) {
                System.out.println("Update event.");
                switch (e.part) {
                    case 1:
                        System.out.println("  Reading calibration file from device.");
                        break;
                    case 2:
                        System.out.println("  Scanner firmware update.");
                        break;
                    case 4:
                        System.out.println("  RFID reader firmware update.");
                        break;
                }
            }
        });
    }
    //--------------------------------------------------------------------------

    void addScanEvents() throws General {

        //----------------------------------------------------------------------

        pr.addEventListener(new ScanStarted() {

            public void onScanStart(PageEventArgs e) {
                System.out.println("Scan started. Page:" + e.page);
            }
        });
        //----------------------------------------------------------------------

        pr.addEventListener(new ImageScanned() {

//            @Override
            public void onImageScanned(ImageEventArgs e) {
                System.out.println("Image scanned. Page:" + e.page + " Light:" + e.light);
            }
        });
        //----------------------------------------------------------------------

        pr.addEventListener(new ScanFinished() {

            public void onScanFinished(PageEventArgs e) {
                System.out.println("Page scanned. Page:" + e.page + " Status:" + e.status);
            }
        });
        //----------------------------------------------------------------------

        pr.addEventListener(new DocFrameFound() {

            public void onDocFrameFound(PageEventArgs e) {
                System.out.println("Document frame found. Page:" + e.page);
            }
        });
    }
    //--------------------------------------------------------------------------

    public static void main(String[] args) {
        try {
            Main prog = new Main();
            prog.init();
            Boolean flag = true;
            BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
            while (flag)
            {

                System.out.print("Scan: ");

                String in = br.readLine();
                System.out.println(in);

                if (!in.equals("q"))
                {

                    prog.program();
                }
                else flag = false;
            }

            prog.destroy();

        } catch (Exception e) {
            System.err.println(e.getMessage());

        }
//        System.out.println("Press any key to exit!");
//        try {
//            System.in.read();
//        } catch (IOException e) {
//        }
    }
    //--------------------------------------------------------------------------
}
