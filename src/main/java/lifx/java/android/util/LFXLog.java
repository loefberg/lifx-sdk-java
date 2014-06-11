//
//  LFXLog.java
//  LIFX
//
//  Created by Jarrod Boyes on 24/03/14.
//  Copyright (c) 2014 LIFX Labs. All rights reserved.
//

package lifx.java.android.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LFXLog
{
	private static boolean info = true;
	private static boolean error = true;
	private static boolean warning = true;
	private static boolean verbose = false;
	private static boolean debug = false;
	
	public static void info( String input)
	{
		if( info)
		{
			//System.out.println( "Error: " + input);
                    Logger.getLogger(LFXLog.class.getName()).log(Level.INFO, input);
		}
	}
	
	public static void error( String input)
	{
		if( error)
		{
			//System.out.println( "Error: " + input);
                    Logger.getLogger(LFXLog.class.getName()).log(Level.SEVERE, input);
		}
	}
	
	public static void warn( String input)
	{
		if( warning)
		{
			//System.out.println( "Warning: " + input);
                    Logger.getLogger(LFXLog.class.getName()).log(Level.WARNING, input);
		}
	}
	
	public static void verbose( String input)
	{
		if( verbose)
		{
                    Logger.getLogger(LFXLog.class.getName()).log(Level.FINE, input);
		}
	}
	
	public static void debug( String input)
	{
		if( debug)
		{
                    Logger.getLogger(LFXLog.class.getName()).log(Level.FINER, input);
		}
	}
	
	public static void LFXMessage( byte[] data)
	{
                StringBuilder builder = new StringBuilder();
		builder.append( "Size: " + data.length).append("\n");
		
		for( int i = 0; i < data.length; i++)
		{
			builder.append(String.format( "0x%02X ", data[i]));
		}
                Logger.getLogger(LFXLog.class.getName()).log(Level.INFO, builder.toString());
	}
}
