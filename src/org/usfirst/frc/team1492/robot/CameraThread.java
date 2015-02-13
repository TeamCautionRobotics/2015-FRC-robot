package org.usfirst.frc.team1492.robot;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.ColorMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.Range;
import com.ni.vision.NIVision.ROI;
import com.ni.vision.NIVision.CoordinateSystem;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CameraThread extends Thread {
	
	int session;
	Image frame;
	
	boolean sessionStarted = false;

	boolean running;
	int tick;

	public CameraThread() {
		
		frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
		
		session = NIVision.IMAQdxOpenCamera("cam0", NIVision.IMAQdxCameraControlMode.CameraControlModeController);
		NIVision.IMAQdxConfigureGrab(session);

		try {
			session = NIVision
					.IMAQdxOpenCamera(
							"cam0",
							NIVision.IMAQdxCameraControlMode.CameraControlModeController);
			NIVision.IMAQdxConfigureGrab(session);
			System.err.println("Camera Session Started");
			sessionStarted = true;
		} catch (Exception e) {
			System.err.println("Camera Session Failed to start.");
			sessionStarted = false;
			e.printStackTrace();
		}

		running = false;
		tick = 0;
	}
	
	@Override
	public void run(){
		NIVision.IMAQdxStartAcquisition(session);
		running = true;
		while(running){
			tick++;
			SmartDashboard.putNumber("CameraThread Tick", tick);
			
			NIVision.IMAQdxGrab(session, frame, 1);
			
			Image filteredFrame = null;
			
			NIVision.imaqColorThreshold(filteredFrame, frame, 0, ColorMode.HSL, new Range(0, 255), new Range(0, 255), new Range(128, 255));
			
			CameraServer.getInstance().setImage(filteredFrame);
			
			Timer.delay(0.01);
		}
		running = false;
	}

	public void finish() {
		running = false;
	}

}
