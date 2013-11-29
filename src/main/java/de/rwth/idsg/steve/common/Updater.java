package de.rwth.idsg.steve.common;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileOutputStream;
import de.schlichtherle.truezip.file.TVFS;
import de.schlichtherle.truezip.fs.archive.zip.JarDriver;
import de.schlichtherle.truezip.socket.sl.IOPoolLocator;


/**
 * This class updates SteVe without losing context descriptor (context.xml) modifications done by the user.
 *
 */
public class Updater {

	public static void doUpdate(String steveDeployed, String newWar) {
		
		// Point to the context.xml of deployed SteVe
		String contentXmlPath = "META-INF" + File.separator + "context.xml";
		File steveDeployedXML = new File(steveDeployed, contentXmlPath);		
		
		// Load the file contents into byte array
		FileInputStream in = null;
		byte[] inByteArray = new byte[(int) steveDeployedXML.length()];
		try {
			in = new FileInputStream(steveDeployedXML);
			in.read(inByteArray);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
		
		TArchiveDetector det = new TArchiveDetector(TArchiveDetector.NULL, 
				new Object[][] {{"war", new JarDriver(IOPoolLocator.SINGLETON)}});

		// Overwrite the context.xml in WAR: Change it with the loaded one
		TFile file = new TFile(newWar, contentXmlPath, det);
		TFileOutputStream out = null;		
		try {
			out = new TFileOutputStream(file);
			out.write(inByteArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				TVFS.umount();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		
		// Put the modified WAR under Tomcat's webapps folder. Tomcat will recognize a change,
		// undeploy the installed version and deploy the new version from WAR.
		String steveWar = steveDeployed + ".war";	
		try {
			Files.move(new File(newWar).toPath(), new File(steveWar).toPath(), REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}