/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.hightechhighfives;

import edu.wpi.first.wpilibj.*;

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
       
    
    // The following variables give a name to each Joystick channel. This makes
    // it easier to know in the driver station which joystick should be matched
    // to which joystick port.
    int JoystickChannelDriver = 1; // driver joystick is on channel 1

    // The following variables define objects that are global to this class
    Jaguar LeftJaguar;
    Jaguar RightJaguar;

    Joystick driverStick; // joystick used by the driver
    
    //Create a driverstation object
    DriverStation ds = DriverStation.getInstance();
    
    //Assign a variable to a specific Analog Input dial
    final int AnalogInputBoost = 1;
    private RobotDrive drive;

    public RobotProgram() {
       //drive = new RobotDrive(LeftJaguar, RightJaguar);
        // create a joystick to be used by the driver
        driverStick = new Joystick(JoystickChannelDriver);
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
            LeftJaguar = new Jaguar(PWMChannelLeftMotor);
            RightJaguar = new Jaguar(PWMChannelRightMotor);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // create a robot drive system that consist of four motors
        //        drive = new RobotDrive(PWMChannelFrontLeftMotor, PWMChannelRearLeftMotor,
        //                PWMChannelFrontRightMotor, PWMChannelRearRightMotor);
        drive = new RobotDrive(LeftJaguar, RightJaguar);

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


            //cuts moveValue in half, to protect linear actuator
            moveValue = moveValue * 0.5;

            // Drive the robot in arcarde mode using the move and rotate values
            drive.arcadeDrive(moveValue, rotateValue);

            // If the claw is closed, open it when the user raises his right arm
            // If the claw is open, close it when the user lowers his right arm

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