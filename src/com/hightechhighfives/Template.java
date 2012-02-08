/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.nurdrobotics;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class FourMotorBot extends SimpleRobot {
    // The following variables give names to each PWM channel that indicates
    // what the PWM channel is used to control. This way, it's easy to look
    // at this section of code and make sure that the PWM wiring matches
    // where the code thinks it is connected.
    final int PWMChannelFrontLeftMotor = 2;  // PWM 1 is connected to left front motor
    final int PWMChannelRearLeftMotor = 4;   // PWM 2 is connected to left rear motor
    final int PWMChannelFrontRightMotor = 6; // PWM 3 is connected to right front motor
    final int PWMChannelRearRightMotor = 5;  // PWM 4 is connected to right rear motor

    // define constants for joystick button mappings
    final int DriverButtonBoost = 8;
    final int DriverButtonOpenClaw = 5;
    final int DriverButtonCloseClaw = 7;

    // The following variables give names to the digital input channels
    final int SwitchChannelPressure = 6;

    //The following variables give names to the Relay Channels
    final int RelayChannelCompressor = 1;

    // The following variables give names to each digital output channel
    // where the solenoids are connected for the claw
    final int OpenSolenoidChannel = 1;
    final int CloseSolenoidChannel = 2;

    // The following variables give a name to each Joystick channel. This makes
    // it easier to know in the driver station which joystick should be matched
    // to which joystick port.
    int JoystickChannelDriver = 1; // driver joystick is on channel 1

    // The following variables define objects that are global to this class
    CANJaguar frontLeftJaguar;
    CANJaguar frontRightJaguar;
    CANJaguar rearLeftJaguar;
    CANJaguar rearRightJaguar;

    //Create a new limit switch
    LimitSwitch testSwitch;

    RobotDrive drive; // drive object used to drive the robot
    Joystick driverStick; // joystick used by the driver
    KinectStick kinectRightArm; // kinect to be used like a joystick
    PneumaticClaw claw; // pneumatic claw
    Compressor compressor; // Air compressor

    //Create a driverstation object
    DriverStation ds = DriverStation.getInstance();
    
    //Assign a variable to a specific Analog Input dial
    final int AnalogInputBoost = 1;

    public FourMotorBot() {
        //drive = new RobotDrive(frontLeftJaguar, rearLeftJaguar, frontRightJaguar, rearRightJaguar);
        // create a joystick to be used by the driver
        driverStick = new Joystick(JoystickChannelDriver);

        // create the kinect joystick
        kinectRightArm = new KinectStick(2);

        // create the air compressor
        compressor = new Compressor(SwitchChannelPressure, RelayChannelCompressor);
        compressor.start();

        // create the pneumatic claw
        claw = new PneumaticClaw(OpenSolenoidChannel, CloseSolenoidChannel);

        //Limit switch on port 1
        testSwitch = new LimitSwitch(1);
    }

    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() {
        
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        try {
            frontLeftJaguar = new CANJaguar(PWMChannelFrontLeftMotor);
            frontRightJaguar = new CANJaguar(PWMChannelFrontRightMotor);
            rearLeftJaguar = new CANJaguar(PWMChannelRearLeftMotor);
            rearRightJaguar = new CANJaguar(PWMChannelRearRightMotor);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
        // create a robot drive system that consist of four motors
        //        drive = new RobotDrive(PWMChannelFrontLeftMotor, PWMChannelRearLeftMotor,
        //                PWMChannelFrontRightMotor, PWMChannelRearRightMotor);
        drive = new RobotDrive(frontLeftJaguar, rearLeftJaguar, frontRightJaguar, rearRightJaguar);

        // declare variables for the stick inputs
        double moveValue;
        double rotateValue;

        // Boost factor is a scale from 0 to 1 to indicate the maximum
        // power when not in boost
        double powerFactor = 0.8;

        // enable the watchdog
        // the watchdog will automatically disable the robot if something
        // in the software hangs. As long as you feed the watchdog, it
        // will assume everything is ok. However, if you go too long
        // without feeding the watchdog, it will assume the software is
        // hung is disable the robot
        getWatchdog().setEnabled(true);

        // loop over the following instructions as long as the robot
        // is enabled and the mode is set to teleoperated (operator control)
        while(isEnabled() && isOperatorControl()) {
            // always feed the watchdog first to let it know everything is ok
            getWatchdog().feed();
            
            // get the move and rotate values from the joystick
            moveValue = driverStick.getRawAxis(2);
            rotateValue = driverStick.getRawAxis(1);

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
            if (driverStick.getRawButton(DriverButtonOpenClaw) == true) {
                claw.Open();
            }
            else if (driverStick.getRawButton(DriverButtonCloseClaw) == true) {
                claw.Close();
            }

            //cuts moveValue in half, to protect linear actuator
            moveValue = moveValue * 0.5;

            // Drive the robot in arcarde mode using the move and rotate values
            drive.arcadeDrive(moveValue, rotateValue);

            // If the claw is closed, open it when the user raises his right arm
            // If the claw is open, close it when the user lowers his right arm
            double rightArm = kinectRightArm.getY();
            if((rightArm < -0.5) && claw.IsClosed()) {
                claw.Open();
            }
            else if((rightArm > 0.5) && claw.IsOpen()) {
                claw.Close();
            }

            // frontLeftJaguar.set(0.5);
            // frontRightJaguar.set(-0.5);

            /*
            System.out.println("move: " + moveValue +
                    " rotate = " + rotateValue +
                    ", frontLeft = " + frontLeftJaguar.get() +
                    ", rearLeft = " + rearLeftJaguar.get() +
                    ", frontRight = " + frontRightJaguar.get() +
                    ", rearRight = " + rearRightJaguar.get() +
                    ", LimitSwitch = " + testSwitch.isOpen());
             */

            // Sleep for 5 milliseconds to give the cRio a chance to
            // Process other events
            Timer.delay(.005);
        }

    }
}