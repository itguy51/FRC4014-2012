/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.hightechhighfives.yearone;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.image.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotProgram extends SimpleRobot {
    // The following variables give names to each PWM channel that indicates
    // what the PWM channel is used to control. This way, it's easy to look
    // at this section of code and make sure that the PWM wiring matches
    // where the code thinks it is connected.
    final int PWMChannelLeftMotor = 1;
    final int PWMChannelRightMotor = 2;   //@Ted: Is The left motor on PWM 1 and the right on PWM 2?
    final int PWMChannelPitcher = 9; //The Pitcher
    //The following are the Relay channels.
    //final int Relay1Channel = 1;
    final int VictorChannel = 3;
    final int Relay1Channel = 1;
    final int Relay2Channel = 2;
    
    boolean canRun = true;
    boolean conveyorPressed = false;
    
    final double fullVictor = 1.0;
    final double noVictor = 0;
    
    //These are Limit Switch Channels
    final int limitSwitch1Channel = 1;
    final int limitSwitch2Channel = 2;
    
    int motionSwap = 1;
    // The following variables give a name to each Joystick channel. This makes
    // it easier to know in the driver station which joystick should be matched
    // to which joystick port.
    int JoystickChannelDriver = 1; // driver joystick is on channel 1
    int JoystickChannelManipulator = 2;

    // The following variables define objects that are global to this class
    Jaguar LeftJaguar;
    Jaguar RightJaguar;
    Jaguar PitchJaguar;
    //ConveyorVictor should actually be called RollerVictor, I'm just too damn
    //lazy to rename it.
    Victor ConveyorVictor;
    Relay SpikeRelay1;
    Relay SpikeRelay2;
    Joystick driverStick; // joystick used by the driver
    
    Joystick manipulatorStick;
    LimitSwitch limitSwitch1;
    LimitSwitch limitSwitch2;
    
    RobotDrive drive;
    //Create a driverstation object
    DriverStation ds = DriverStation.getInstance();
    double scaleFactor;
    AxisCamera camera;
    private ColorImage currentImage;
    
    
    //Assign a variable to a specific Analog Input dial    
    public RobotProgram() {
        System.out.println("System Ready.");
       //drive = new RobotDrive(LeftJaguar, RightJaguar);
        // create a joystick to be used by the driver
        getWatchdog().setExpiration(2);
        ConveyorVictor = new Victor(VictorChannel);
        SpikeRelay1 = new Relay(Relay1Channel);
        SpikeRelay2 = new Relay(Relay2Channel);
        driverStick = new Joystick(JoystickChannelDriver);
        manipulatorStick = new Joystick(JoystickChannelManipulator);
        limitSwitch1 = new LimitSwitch(limitSwitch1Channel);
        limitSwitch2 = new LimitSwitch(limitSwitch2Channel);
        camera = AxisCamera.getInstance();
        camera.writeResolution(AxisCamera.ResolutionT.k640x360);
        camera.writeBrightness(10);
        try {
            LeftJaguar = new Jaguar(PWMChannelLeftMotor);
            RightJaguar = new Jaguar(PWMChannelRightMotor);
            PitchJaguar = new Jaguar(PWMChannelPitcher);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() {
         /*LeftJaguar.set(0.5);
         RightJaguar.set(0.5);
         Timer.delay(1);
         LeftJaguar.set(0);
         RightJaguar.set(0);
         */
        //Autonomous Auto-Target (Sort of)
        getWatchdog().setEnabled(true);
        getWatchdog().feed();
        getWatchdog().setExpiration(26);
        getWatchdog().feed();
                if(camera.freshImage()){
                int errLoc = 0;
                try{
                errLoc = 1;
                ColorImage currentImage = camera.getImage();
                errLoc = 2;
                MonoImage greenPlane = currentImage.getGreenPlane();
                errLoc = 3;
                EllipseDescriptor descEllipse = new EllipseDescriptor(0.0, 30.0, 0.0, 30.0);
                errLoc = 4;
                EllipseMatch[] em = greenPlane.detectEllipses(descEllipse);
                if(em[0] != null){
                errLoc = 5;
                System.out.println("INFO: X: " + em[0].m_xPos + "Y: " + em[0].m_yPos);
                errLoc = 6;
                if(em[0].m_xPos > 0 && em[0].m_xPos < 360){
                    System.out.println("INFO: Vertical > Acquired.");
                    if(em[0].m_xPos < 100){
                        PitchJaguar.set(0.5);
                    }else if(em[0].m_xPos > 270){
                        PitchJaguar.set(1.0);
                    }else{
                        PitchJaguar.set(0.7);
                    }
                }
                if(em[0].m_yPos > 210 && em[0].m_yPos < 430){
                    System.out.println("INFO: Horizontal > Acquired.");
                }
                }else{
                    System.out.println("WARN: No Objects Found.");
                }
                }catch(Exception exc){
                    System.out.println("ERR: Something happened. Error Location " + errLoc);
                }
                
            }else{
                System.out.println("WARN: No Image");
            }
            SpikeRelay1.set(Relay.Value.kForward);
            Timer.delay(1.5);
            PitchJaguar.set(PitchJaguar.get() - 0.18);
            Timer.delay(2);
            SpikeRelay1.set(Relay.Value.kOff);
            while(true){
         getWatchdog().feed();
         Timer.delay(5);
            }
        /*PitchJaguar.set(1.0);
        getWatchdog().feed();
        SpikeRelay1.set(Relay.Value.kForward);
        getWatchdog().feed();
        Timer.delay(5);
        getWatchdog().feed();
        PitchJaguar.set(0);
        getWatchdog().feed();
        SpikeRelay1.set(Relay.Value.kOff);
        SpikeRelay2.set(Relay.Value.kForward);
        Timer.delay(0.26);                                                                
        SpikeRelay2.set(Relay.Value.kOff);
        /*Timer.delay(0.75);
        ConveyorVictor.set(1.0);
        getWatchdog().feed();
        Timer.delay(2.5);
        ConveyorVictor.set(0);
        Timer.delay(0.8);
        getWatchdog().feed();
        ConveyorVictor.set(1.0);
        Timer.delay(2.5);
        ConveyorVictor.set(0);
        getWatchdog().feed();
        Timer.delay(0.05);
        PitchJaguar.set(0);
        getWatchdog().feed();
        * */
        //getWatchdog().setEnabled(false);
         
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        //getWatchdog().setExpiration(2);
  /*      try {
            LeftJaguar = new Jaguar(PWMChannelLeftMotor);
            RightJaguar = new Jaguar(PWMChannelRightMotor);
            PitchJaguar = new Jaguar(PWMChannelPitcher);
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
        // create a robot drive system that consist of four motors
        //        drive = new RobotDrive(PWMChannelFrontLeftMotor, PWMChannelRearLeftMotor,
        //                PWMChannelFrontRightMotor, PWMChannelRearRightMotor);
        drive = new RobotDrive(LeftJaguar, RightJaguar);
        //
        
        // declare variables for the stick inputs
        double leftValue;
        double rightValue;
        double pitchValue;

        // Boost factor is a scale from 0 to 1 to indicate the maximum
        // power when not in boost
        double powerFactor = 0.8;

        // enable the watchdog
        // the watchdog will automatically disable the robot if something
        // in the software hangs. As long as you feed the watchdog, it
        // will assume everything is ok. However, if you go too long
        // without feeding the watchdog, it will assume the software is
        // hung is disable the robot
        
        //getWatchdog().setExpiration(2.2);
        // loop over the following instructions as long as the robot
        // is enabled and the mode is set to teleoperated (operator control)
        while(isEnabled() && isOperatorControl()) {
            leftValue = driverStick.getRawAxis(2);
            rightValue = driverStick.getRawAxis(4);
            if(driverStick.getRawButton(7)){
                leftValue = -0.8 /** scaleFactor*/ * motionSwap;
                rightValue = -0.8 /** scaleFactor*/ * motionSwap;
            }else if(driverStick.getRawButton(8)){
                leftValue = leftValue * 0.3 * motionSwap;
                rightValue = rightValue * 0.3 * motionSwap;
            }else if(driverStick.getRawButton(5)){
                motionSwap = -1;
                //Inverted controls, Invert the wheels sent.
                rightValue = rightValue * scaleFactor * motionSwap;
                leftValue = leftValue * scaleFactor * motionSwap;
            }else{
                motionSwap = 1;
                leftValue = leftValue * scaleFactor * motionSwap;
                rightValue = rightValue * scaleFactor * motionSwap;
            }
            drive.tankDrive(leftValue, rightValue);
            getWatchdog().setEnabled(false);
            getWatchdog().setExpiration(15);
            drive.setExpiration(1.5);
            //drive.setSafetyEnabled(false);
            // always feed the watchdog first to let it know everything is ok
            //getWatchdog().feed();
            // get the move and rotate values from the joystick

            pitchValue = Math.abs(manipulatorStick.getRawAxis(2));

            if(manipulatorStick.getRawButton(1) == true){
                //Trigger Pulled.
                //System.out.println("Trigger Pulled");
                //ConveyorVictor.set(1.0);
                SpikeRelay1.set(Relay.Value.kForward);
            }else{
                //Trigger Released.
                SpikeRelay1.set(Relay.Value.kOff);
                //ConveyorVictor.set(0);
            }
            if(manipulatorStick.getRawButton(2) == true){
                //Trigger Pulled.
                SpikeRelay2.set(Relay.Value.kForward);
            }else if(manipulatorStick.getRawButton(5) == true){
                //Trigger Pulled.
                SpikeRelay2.set(Relay.Value.kReverse);
                //ConveyorVictor.set(1.0);
            }else{
                //Trigger Released.
                SpikeRelay2.set(Relay.Value.kOff);
            }
            
            
            getWatchdog().feed();
            
           /* if(manipulatorStick.getRawButton(3) == true){
                //Trigger Pulled.
                SpikeRelay1.set(Relay.Value.kForward);
            }else{
                //Trigger Released.
                SpikeRelay1.set(Relay.Value.kOff);
            }*/
            
            
           /*if(manipulatorStick.getRawButton(5) == true){
                //Trigger Pulled.
                SpikeRelay4.set(Relay.Value.kForward);
            }else{
                //Trigger Released.
                SpikeRelay4.set(Relay.Value.kOff);
            } */
            /* commented out for initial testing
             *
            // if the boost button is not pressed, scale the move and rotate
            // values by the boost factor
            if (driverStick.getRawButton(DriverButtonBoost) == false) {
                //Retrieve boost factor from the driver station analog dial
                //Analog inputs reads 0 to 3.3
                //Boost factor will be a scale from 0 to 1
                powerFactor = ds.getAnalogIn(AnalogInputBoost) / 3.3;

                //Scale the moveValue and the rotateValue
                moveValue = moveValue * powerFactor;
                rotateValue = rotateValue * powerFactor;
            }
             */

            // check to see whether we should open or close the claw


            //cuts moveValue in half, to protect linear actuator
            if(driverStick.getRawButton(6) == true){
                scaleFactor = 1;
            }else{
                scaleFactor = 0.8;
            }

            
            
            //Logic for Conveyor
            if(manipulatorStick.getRawButton(1) == false){
                //System.out.println("Limit 1: " + limitSwitch1.isOpen() + ", Limit 2: " + limitSwitch2.isOpen());
                //ds.setDigitalOut(1, limitSwitch1.isOpen());
                //ds.setDigitalOut(2, limitSwitch2.isOpen());
            if(limitSwitch1.isClosed() && limitSwitch2.isClosed()){
                ConveyorVictor.set(1.0);
                if(conveyorPressed){
                    SpikeRelay1.set(Relay.Value.kForward);
                    getWatchdog().feed();
                    Timer.delay(0.7);
                    getWatchdog().feed();
                    SpikeRelay1.set(Relay.Value.kOff);
                    conveyorPressed = false;
                }else{
                    SpikeRelay1.set(Relay.Value.kOff);
                }
            }else if(limitSwitch1.isOpen() && limitSwitch2.isClosed()){
                ConveyorVictor.set(1.0);
                SpikeRelay1.set(Relay.Value.kForward);
                conveyorPressed = true;
            }else if(limitSwitch1.isClosed() && limitSwitch2.isOpen()){
                ConveyorVictor.set(-1.0);
                SpikeRelay1.set(Relay.Value.kOff);
                /*
                if(conveyorPressed){
                    SpikeRelay1.set(Relay.Value.kForward);
                    Timer.delay(0.7);
                    SpikeRelay1.set(Relay.Value.kOff);
                    conveyorPressed = false;
                }else{
                    SpikeRelay1.set(Relay.Value.kOff);
                }*/
                
            }else if(limitSwitch1.isOpen() && limitSwitch2.isOpen()){
                ConveyorVictor.set(-1.0);
                SpikeRelay1.set(Relay.Value.kOff);
            }else{
                //What the hell? 
            }
            }
            getWatchdog().feed();
            
            /*if(manipulatorStick.getRawButton(7)){
                SpikeRelay1.set(Relay.Value.kReverse);
            }else{
                SpikeRelay1.set(Relay.Value.kOff);
            }*/
            
            
            
            

            getWatchdog().feed();
            if(manipulatorStick.getRawButton(6)){
                if(canRun){
                SpikeRelay2.set(Relay.Value.kForward);
                getWatchdog().feed();
                Timer.delay(0.26);       
                getWatchdog().feed();
                SpikeRelay2.set(Relay.Value.kOff);
                canRun = false;
                }
            }else{
                canRun = true;
            }
            getWatchdog().feed();

            //System.out.println("Scale Factor: " + scaleFactor);
            // Drive the robot in arcarde mode using the move and rotate values
            //drive.arcadeDrive (moveValue, rotateValue);
            getWatchdog().feed();
            drive.tankDrive(leftValue, rightValue);
            getWatchdog().feed();
            drive.tankDrive(leftValue, rightValue);
            PitchJaguar.set(pitchValue);
            getWatchdog().feed();
            drive.tankDrive(leftValue, rightValue);
            
            // If the claw is closed, open it when the user raises his right arm
            // If the claw is open, close it when the user lowers his right arm

            // frontLeftJaguar.set(0.5);
            // frontRightJaguar.set(-0.5);

            
            /*System.out.println("Left = " + LeftJaguar.get() +
                    ", Right = " + RightJaguar.get() +
                    ", Limit Switch(Open) = " + limitSwitch1.isOpen() + 
                    ", Relay 1 = " + driverStick.getRawButton(1));
            */
            //System.out.println("Limit 1 " + limitSwitch1.isOpen());
            // Sleep for 5 milliseconds to give the cRio a chance to
            // Process other events
            //Image Processing Code (Auto-Target)
            
            
            /*if(camera.freshImage()){
                int errLoc = 0;
                try{
                errLoc = 1;
                ColorImage currentImage = camera.getImage();
                errLoc = 2;
                MonoImage greenPlane = currentImage.getGreenPlane();
                errLoc = 3;
                EllipseDescriptor descEllipse = new EllipseDescriptor(0.0, 30.0, 0.0, 30.0);
                errLoc = 4;
                EllipseMatch[] em = greenPlane.detectEllipses(descEllipse);
                if(em[0] != null){
                errLoc = 5;
                System.out.println("X: " + em[0].m_xPos + "Y: " + em[0].m_yPos);
                errLoc = 6;
                if(em[0].m_xPos > 100 && em[0].m_xPos < 270){
                    System.out.println("Vertical > Acquired.");
                }
                if(em[0].m_yPos > 210 && em[0].m_yPos < 430){
                    System.out.println("Horizontal > Acquired.");
                }
                }else{
                    System.out.println("No Objects Found.");
                }
                }catch(Exception exc){
                    System.out.println("ERR: Something happened. Error Location " + errLoc);
                }
                
            }else{
                System.out.println("No Image");
            }*/
            
            
            Timer.delay(.005);
            getWatchdog().feed();
            getWatchdog().setEnabled(false);
            drive.tankDrive(leftValue, rightValue);
        }

    }
}