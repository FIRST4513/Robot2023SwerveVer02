
package frc.robot.logger;

import frc.robot.Robot;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
//import java.sql.Date;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.Vector;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Joystick;

public class Logger extends SubsystemBase {
    String fileName;
	String base = "/home/lvuser/logs/logfile";
	
	
	//String output_dir = "/U/data_captures/"; // USB drive is mounted to /U on roboRIO
	String output_dir = "/home/lvuser/logs/"; // USB drive is mounted to /U on roboRIO
	boolean log_open = false;
	long log_write_index;
	String log_name = null;
	BufferedWriter log_file = null;


	String msg;  
	java.io.PrintStream outFile;
	String current, last, logDateTime;
	File currentFile, lastFile, logDateTimeFile;
	boolean bool = false;
	String line;
	Vector<String> lines = new Vector<>();
	public Timer logTimer = new Timer();
	public int logFlag = 0;
	double time, lastTime, leftLastDist, rightLastDist, avgLastDist, LogFlag;

    public Logger() {
        createFile();
		//LogFlag = 1;		// Start with detail logging turned off 1=on
		time = 0;
		lastTime = 0;
		leftLastDist = 0;
		rightLastDist = 0;
		avgLastDist = 0;
    }

    @Override
    public void periodic() {

    }

    // ------------------------------------------------------------------------
    // ------------------------- Subsystem Methods ----------------------------
    public void createFile(){
    	System.out.println("Logger Creating file");
    	// Remove log file 15 to make room
    	last = new String(base + "15.csv"); 
    	lastFile = new File(last);

		// -------- Delete oldest file 15 -----------
	    System.out.println("attempting logfile15.csv delete");
   		bool = lastFile.delete();
   		if (bool)
   		    System.out.println("logfile15.csv delete SUCCESS");
   		else
   		    System.out.println("NO logfile15.csv to delete!");
   		
		// ----------------------------------------------------------------
    	// Rename remaining files 9-1 to 10-2 (incrementing the file count)
	    System.out.println("\nRenaming files 1-15\n");
    	for (int i = 14; i > 0; i--){
    		if (i < 10) {
    			current = new String(base + "0" + i + ".csv");
    		} else {
    			current = new String(base + i + ".csv");
    		}
    		System.out.println("i =" + i + " current=" + current + "   rename to " + last);
    		currentFile = new File(current);
   			bool = currentFile.renameTo(lastFile);	// Rename files 9-1
   	   		if (bool) {
   	    		System.out.println("Renamed current file = " + current + "  to " + last);   	   			
   	   		    System.out.println(current + " rename SUCCESS");
            } else {
                      System.out.println("NO " + current + " to rename!");
            }
			//System.out.println("Logger Create file completed ! Current =" + current );
    		last = new String(current);
    		lastFile = new File(last);
			//System.out.println("Logger Create file completed ! last =" + last );
    	}
		System.out.println("Logger Create file completed ! last =" + last );
		appendLog("Line 1");
		appendLog("Line 2");
		appendLog("Line 3");

    	// Open Log file for Output

		//LocalDateTime date = LocalDateTime.now();
		//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
		//logDateTime = formatter.format(date);
		// logDateTime= "2018-03-07 10-29-59"
		//logDateTimeFile = new File(logDateTime);
    	
		// what happens if no file exists !!! empty folder !!!!!
    	try{  
    		// Create a PrintStream attached to a file named last log1.csv.
    		outFile = new java.io.PrintStream(last);
    		System.out.println("success in opening new log file..." + last);
			startTimer();
    	}
    	catch(Exception e){
    		System.out.println("Error in opening new log file..." + last);         	
    		System.out.println(e);
    	}

    	appendLog("****** LOG FILE ( " + last + " )    Created at " + logDateTime + " *******");
    }

    public void saveLogFile(){
    	System.out.println("Logger printlog function called"); 
		System.out.println("  ----------- Lines in log file = "+ lines.size());

    	for (String element : lines) {
			System.out.println("  ----------- Line = "+ element);
			try{  
				outFile.println(element);	
			}
			catch(Exception e) {
				System.out.println("Error in saving log file..." + last);         	
				System.out.println(e);
			} 	
    	}
		try{ 
			System.out.println("  ----------- Trying to flush out file buffer");
			outFile.flush();    	// Flush all buffered data to the file.
		}
		catch(Exception e) {
			System.out.println("Error in flushing log file..." + last);         	
			System.out.println(e);
		}
		lines.clear();
		logFlag = 0;			// Show that there is nothing in buffer	

    	// ????            //outFile.close();		// Close the file (by closing the PrintStream). problems
    }

    public void startTimer(){
    	logTimer.reset();
    	logTimer.start();
    }

    public void appendLog(String s){
    	logFlag = 1;						// we have log entry in buffer
    	time = Robot.sysTimer.get();
    	lines.add(String.valueOf(time)+", " + s);
    }

    public void appendLog(String s, double d){
    	logFlag = 1;						// we have log entry in buffer
    	time = Robot.sysTimer.get();
    	lines.add(String.valueOf(time)+", " + s + ", " + String.valueOf(d));
    }

    public void appendLog(String type, String sys, String s ){
    	logFlag = 1;						// we have log entry in buffer
    	time = Robot.sysTimer.get();
    	lines.add(String.valueOf(time)+", " + type + ", " + sys + ", " + s);
    }


    public void logJoyAxis(Joystick joy){
    	logFlag = 1;						// we have log entry in buffer
    	line = String.valueOf(time) + ", Debug, Joystick";
    	//line += ", Xaxis=," + String.valueOf(Robot.drivetrain.JoyXaxis);
    	//line += ", Yaxis=," + String.valueOf(Robot.drivetrain.JoyYaxis);
    	//line += ", Twist=," + String.valueOf(Robot.drivetrain.JoyTwist);
    	//line += ", Throttle=," + String.valueOf(Robot.drivetrain.JoyThrottle);
    	//line += ", RevBtn=," + String.valueOf(Robot.drivetrain.JoyReverse);
    	lines.add(line);
    }

	public void appendLogPosition(String s ){
    	//logFlag = 1;						// we have log entry in buffer
		//time = Robot.sysTimer.get();
		//double x =			 Robot.drivetrain.getPositionX();
		//double y =  		 Robot.drivetrain.getPositionY();
		//double posTracker = Robot.drivetrain.getTrackerYaw();
		//double gyroYaw = 	 Robot.drivetrain.getGyroYaw();
		//msg = String.format(",Position X=,%g, Y=,%g,GyroYaw=,%g, TrackerYaw=,%g" , x , y, posTracker, gyroYaw);
    	//lines.add(String.valueOf(time) + "," + s + "," + msg);
	}
	
    //public void setLogFlag(double flag)	{  	LogFlag = flag; }
    //public double getLogFlag()			{ 	return LogFlag; }
    public void clearLog()				{  	lines.clear();  }

}

// This is a test comment -Logan