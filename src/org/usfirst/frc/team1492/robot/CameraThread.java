package org.usfirst.frc.team1492.robot;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.ColorMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.Range;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.image.ColorImage;

public class CameraThread extends Thread {
	
	int session;
	Image frame;
	
	boolean stop = false;

	public CameraThread() {
		
		frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
		
		session = NIVision.IMAQdxOpenCamera("cam0", NIVision.IMAQdxCameraControlMode.CameraControlModeController);
		NIVision.IMAQdxConfigureGrab(session);
		
		
		this.start();
	}
	
	@Override
	public void run(){
		NIVision.IMAQdxStartAcquisition(session);
		while(!stop){
			NIVision.IMAQdxGrab(session, frame, 1);
			
			Image filteredFrame = null;
			
			NIVision.imaqColorThreshold(filteredFrame, frame, 0, ColorMode.HSL, new Range(0, 255), new Range(0, 255), new Range(128, 255));
			
			CameraServer.getInstance().setImage(filteredFrame);
			
			Timer.delay(0.1);
		}
		NIVision.IMAQdxStopAcquisition(session);
	}

	public void finish() {
		stop = true;
	}

}
