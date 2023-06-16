package _v1_ImageProcessing;

import java.io.File;
import org.rev6.scf.ScpFile;
import org.rev6.scf.ScpUpload;
import org.rev6.scf.SshConnection;
import org.rev6.scf.SshException;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class JschConnection {
	
	private static final String HOST     = "169.254.27.254";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "robot";
	
	
	//Transfers imageDataFile to printer
	public void transferData() {
		
		SshConnection ssh = null;
		
		try {
			
			//Connects to Lejos Printer
			ssh = new SshConnection(HOST, USERNAME, PASSWORD);
			ssh.connect();
			
			System.out.println("Connected to printer...");
			
			//Transfers file to printer
			ScpFile scpFile = new ScpFile(new File("./imgData.txt") , "/home/lejos/programs/imgData.txt");
			ssh.executeTask(new ScpUpload(scpFile));
			
		} catch (SshException e) {
			
			System.out.println(e);
			
		} finally {
			
			if (ssh != null) ssh.disconnect();
			
		}
	}
	
	
	//Start printer
	public void runProgram() {
		
		JSch jsch = null;
		
		Session session = null;
		
		try {
			
			//Connect to printer
			jsch = new JSch();
			session = jsch.getSession(USERNAME, HOST, 22);
			session.setPassword(PASSWORD);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			
			ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
			
			//Run jar file on printer to begin printing
			channelExec.setCommand("jrun -jar /home/lejos/programs/Printer.jar");
			channelExec.connect();
			
			
			channelExec.disconnect();
			
		} catch (JSchException e) {
			
			System.out.println(e);
			
		} finally {
			
			if (session != null) session.disconnect();
        
		}
		
	}
		

}
