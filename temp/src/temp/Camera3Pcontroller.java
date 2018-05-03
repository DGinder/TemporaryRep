package temp;

import ray.rml.Angle;
import ray.rml.Degreef;
import ray.rml.Vector3;
import ray.rml.Vector3f;
import net.java.games.input.Event;
import ray.input.InputManager;
import ray.input.action.*;
import ray.rage.scene.*;

public class Camera3Pcontroller{
	private SceneNode cameraN; //the node the camera is attached to
	private SceneNode target; //the target the camera looks at
	private float cameraAzimuth; //rotation of camera around Y axis
	private float cameraElevation; //elevation of camera above target
	private float radias; //distance between camera and target
	private Vector3 worldUpVec;
	private boolean mode = false;
	
	public Camera3Pcontroller(Camera cam, SceneNode camN, SceneNode targ, String[] kbNames, int kb, InputManager im){
		cameraN = camN;
		target = targ;
		cameraAzimuth = 225.0f; // start from BEHIND and ABOVE the target
		cameraElevation = 20.0f; // elevation is in degrees
		radias = 2.0f;
		worldUpVec = Vector3f.createFrom(0.0f, 1.0f, 0.0f);
		setupKeyboardInput(im, kbNames, kb);
		updateCameraPosition();
	}
	
	public Camera3Pcontroller(Camera cam, SceneNode camN, SceneNode targ, String gpName, InputManager im){
		cameraN = camN;
		target = targ;
		cameraAzimuth = 225.0f; // start from BEHIND and ABOVE the target
		cameraElevation = 20.0f; // elevation is in degrees
		radias = 2.0f;
		worldUpVec = Vector3f.createFrom(0.0f, 1.0f, 0.0f);
		setupControllerInput(im, gpName);
		updateCameraPosition();
	}
	// Updates camera position: computes azimuth, elevation, and distance
// relative to the target in spherical coordinates, then converts those
// to world Cartesian coordinates and setting the camera position
	public void updateCameraPosition(){
		
		double theta = Math.toRadians(cameraAzimuth); // rot around target
		double phi = Math.toRadians(cameraElevation); // altitude angle
		double x = radias * Math.cos(phi) * Math.sin(theta);
		double y = radias * Math.sin(phi);
		double z = radias * Math.cos(phi) * Math.cos(theta);
		cameraN.setLocalPosition(Vector3f.createFrom((float)x, (float)y, (float)z).add(target.getWorldPosition()));
		cameraN.lookAt(target, worldUpVec);
	}
	
	private void setupControllerInput(InputManager im, String cn){
		Action orbitArAction = new OrbitAroundAction();
		Action orbitAbAction = new OrbitAboutAction();
		Action orbitRAction = new OrbitRadiusAction();
		Action modeAction = new ModeSwitchtAction();
		im.associateAction(cn, net.java.games.input.Component.Identifier.Axis.RX, orbitArAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn, net.java.games.input.Component.Identifier.Axis.RY, orbitAbAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn, net.java.games.input.Component.Identifier.Axis.Z, orbitRAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn, net.java.games.input.Component.Identifier.Button._8, modeAction, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
	// similar input set up for OrbitRadiasAction, OrbitElevationAction
	}
	
	private void setupKeyboardInput(InputManager im, String[] cn, int kb){
		Action orbitLeftAction = new OrbitAroundLeftAction();
		Action orbitRightAction = new OrbitAroundRightAction();
		Action orbitUpAction = new OrbitAroundUpAction();
		Action orbitDownAction = new OrbitAroundDownAction();
		Action orbitZoomInAction = new OrbitZoomInAction();
		Action orbitZoomOutAction = new OrbitZoomOutAction();
		Action modeAction = new ModeSwitchtAction();
		for(int i = 0; i <= kb; i++) {
	    	if(cn[i] != null){
				im.associateAction(cn[i], net.java.games.input.Component.Identifier.Key.LEFT, orbitLeftAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(cn[i], net.java.games.input.Component.Identifier.Key.RIGHT, orbitRightAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(cn[i], net.java.games.input.Component.Identifier.Key.UP, orbitUpAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(cn[i], net.java.games.input.Component.Identifier.Key.DOWN, orbitDownAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(cn[i], net.java.games.input.Component.Identifier.Key.Z, orbitZoomInAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(cn[i], net.java.games.input.Component.Identifier.Key.X, orbitZoomOutAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(cn[i], net.java.games.input.Component.Identifier.Key.R, modeAction, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
	    	}
		}
	// similar input set up for OrbitRadiasAction, OrbitElevationAction
	}
	
	private class OrbitAroundAction extends AbstractInputAction{
		// Moves the camera around the target (changes camera azimuth).
			public void performAction(float time, Event e){
				if(mode == false) {
					float rotAmount;
					if (e.getValue() < -0.5)
						{ rotAmount=-1f; }
					else{
						if (e.getValue() > 0.5)
							{ rotAmount=1f; }
						else
							{ rotAmount=0.0f; }
					}
					cameraAzimuth += rotAmount;
					cameraAzimuth = cameraAzimuth % 360;
					updateCameraPosition();
				}
				else {
					float rotAmount;
					Angle angle  = Degreef.createFrom(0f);
					if (e.getValue() < -0.5){
						rotAmount=1f; 
						angle = Degreef.createFrom(1f);
					}
					else{
						if (e.getValue() > 0.5){
							rotAmount=-1f;
							angle = Degreef.createFrom(-1f);
						}
						else{
							rotAmount=0.0f;
						}
					}
					cameraAzimuth += rotAmount;
					target.yaw(angle);
					updateCameraPosition();
				}
			}
	}
	
	private class OrbitAboutAction extends AbstractInputAction{
		// Moves the camera around the target (changes camera azimuth).
			public void performAction(float time, Event e){
				float rotAmount = 0f;
				if (e.getValue() < -0.5){
					if(cameraElevation < 89) {
						rotAmount=1f;
					}
				}
				else{
					if (e.getValue() > 0.5){
						float temp = (float) Math.toDegrees(-Math.asin(0.8f/radias));
						if(temp <= -90f || Float.isNaN(temp))
							temp = -89.0f;
						if(cameraElevation > temp) {
							rotAmount = -1f;
						}
					}
					else
						{ rotAmount=0.0f; }
				}
				cameraElevation += rotAmount;
				cameraElevation = cameraElevation % 360;
				updateCameraPosition();
			}
	}
	private class OrbitRadiusAction extends AbstractInputAction{
		// Moves the camera around the target (changes camera azimuth).
			public void performAction(float time, Event e){
				float rotAmount = 0f;
				if (e.getValue() < -0.2){
					if(radias > 1) {
						rotAmount = -0.05f;
					}
				}
				else{
					if (e.getValue() > 0.2){
						
						if(radias< 10) {
								if(cameraElevation < -5f) {
									float tempTheta = (float) Math.sin(cameraElevation);
									float tempRadi = 1f/tempTheta;
									if(tempRadi < 10) {
										if(radias < Math.abs(tempRadi)) {
											rotAmount = 0.05f;
										}
									}
									else {
										rotAmount = 0.05f;
									}
									
								}
								else {
									rotAmount = 0.05f;
								}
						}
					}
					else
						{ rotAmount=0.0f; }
				}
				radias += rotAmount;
				radias = radias % 360;
				updateCameraPosition();
			}
			
	}
	
	private class OrbitAroundLeftAction extends AbstractInputAction{
		// Moves the camera around the target (changes camera azimuth).
			public void performAction(float time, Event e){
				float rotAmount = -1f;
				if(mode == false) {
					cameraAzimuth += rotAmount;
					cameraAzimuth = cameraAzimuth % 360;
					updateCameraPosition();
				}
				else {
					cameraAzimuth -= rotAmount;
					cameraAzimuth = cameraAzimuth % 360;
					Angle angle  = Degreef.createFrom(1f);
					target.yaw(angle);
					updateCameraPosition();
				}
			}
	}
	private class OrbitAroundRightAction extends AbstractInputAction{
		// Moves the camera around the target (changes camera azimuth).
			public void performAction(float time, Event e){
				float rotAmount = 1f;
				if(mode == false) {
					cameraAzimuth += rotAmount;
					cameraAzimuth = cameraAzimuth % 360;
					updateCameraPosition();
				}
				else {
					cameraAzimuth -= rotAmount;
					cameraAzimuth = cameraAzimuth % 360;
					Angle angle  = Degreef.createFrom(-1f);
					target.yaw(angle);
					updateCameraPosition();
				}
			}
	}
	private class OrbitAroundUpAction extends AbstractInputAction{
		// Moves the camera around the target (changes camera azimuth).
			public void performAction(float time, Event e){
				if(cameraElevation < 89) {
					float rotAmount =1f;
					cameraElevation += rotAmount;
					cameraElevation = cameraElevation % 360;
					updateCameraPosition();
				}
			}
	}
	private class OrbitAroundDownAction extends AbstractInputAction{
		// Moves the camera around the target (changes camera azimuth).
			public void performAction(float time, Event e){
				float temp = (float) Math.toDegrees(-Math.asin(0.8f/radias));
				if(temp <= -90f || Float.isNaN(temp))
					temp = -89.0f;
				if(cameraElevation > temp) {
					float rotAmount = -1f;
					cameraElevation += rotAmount;
					cameraElevation = cameraElevation % 360;
					updateCameraPosition();
				}
			}
	}
	private class OrbitZoomInAction extends AbstractInputAction{
		// Moves the camera around the target (changes camera azimuth).
			public void performAction(float time, Event e){
				if(radias > 1) {
					float rotAmount = -0.05f;
					radias += rotAmount;
					radias = radias % 360;
					updateCameraPosition();
				}
			}
	}
	private class OrbitZoomOutAction extends AbstractInputAction{
		// Moves the camera around the target (changes camera azimuth).
			public void performAction(float time, Event e){
				if(radias< 10) {
					if(radias < 10) {
						if(cameraElevation < -5f) {
							float tempTheta = (float) Math.sin(cameraElevation);
							float tempRadi = 1f/tempTheta;
							if(tempRadi < 10) {
								if(radias < Math.abs(tempRadi)) {
									float rotAmount = 0.05f;
									radias += rotAmount;
									radias = radias % 360;
									updateCameraPosition();
								}
							}
							else {
								float rotAmount = 0.05f;
								radias += rotAmount;
								radias = radias % 360;
								updateCameraPosition();
							}
							
						}
						else {
							float rotAmount = 0.05f;
							radias += rotAmount;
							radias = radias % 360;
							updateCameraPosition();
						}
						
					}
				}
			}
	}
	
	private class ModeSwitchtAction extends AbstractInputAction{
		// Moves the camera around the target (changes camera azimuth).
			public void performAction(float time, Event e){
				mode = !mode;
			}
	}
	
 // similar for OrbitRadiasAction, OrbitElevationAction
}