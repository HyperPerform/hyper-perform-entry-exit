package me.hyperperform.entryexit;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Main
{
    public Main()
    {
        super();
    }
    
    void connect (  ) throws Exception
    {

    }
    
    public static void main(String[] args)
    {
        try
        {
            (new Main()).connect();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }
}