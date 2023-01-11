package frc.robot.pilotGamepad;

import frc.lib.gamepads.Gamepad;
import frc.lib.gamepads.mapping.ExpCurve;
import frc.robot.elevator.commands.ElevatorCmds;
import frc.robot.slider.SliderCmds;

/** Used to add buttons to the pilot gamepad and configure the joysticks */
public class PilotGamepad extends Gamepad {
    public static ExpCurve forwardSpeedCurve =
            new ExpCurve(
                    PilotGamepadConfig.forwardSpeedExp,
                    0,
                    PilotGamepadConfig.forwardSpeedScaler,
                    PilotGamepadConfig.forwardSpeedDeadband);
    public static ExpCurve sidewaysSpeedCurve =
            new ExpCurve(
                    PilotGamepadConfig.sidewaysSpeedExp,
                    0,
                    PilotGamepadConfig.sidewaysSpeedScaler,
                    PilotGamepadConfig.sidewaysSpeedDeadband);
    public static ExpCurve rotationCurve =
            new ExpCurve(
                    PilotGamepadConfig.rotationSpeedExp,
                    0,
                    PilotGamepadConfig.rotationSpeedScaler,
                    PilotGamepadConfig.rotationSpeedDeadband);

    public PilotGamepad() {
        super("Pilot", PilotGamepadConfig.port);
    }

    public void setupTeleopButtons() {
        //gamepad.aButton.whileTrue(ElevatorCmds.goToPIDPosCmd(30.0));
        // Setup Elevator button Assignments

        //final JoystickButton xboxButton1 = new JoystickButton(gamepad, XboxController.Button.kA.value);        
        //xboxButton1.whileTrue(SliderCmds.sliderLeftCmd().withInterruptBehavior(InterruptionBehavior.kCancelSelf));

        gamepad.Dpad.Left.whileTrue(SliderCmds.sliderLeftCmd());
        gamepad.Dpad.Right.whileTrue(SliderCmds.sliderRightCmd());
        
        gamepad.Dpad.Down.whileTrue(ElevatorCmds.lowerCmd());
        gamepad.Dpad.Up.whileTrue(ElevatorCmds.raiseCmd());

        // Reset Gyro/Encoders/Pose Data
        //gamepad.selectButton.whenPressed(DrivetrainCmds.resetPoseCmd());

        // Setup Grabber (Claw) button Assignments
        //gamepad.leftBumper.whileTrue(GrabberCmds.grabberCloseCmd());
        //gamepad.rightBumper.whileTrue(GrabberCmds.grabberOpenCmd());
    }

    public void setupDisabledButtons() {
    }

    public void setupTestButtons() {}

    // forward/backward across the field
    public double getDriveFwdPositive() {
        return forwardSpeedCurve.calculateMappedVal(this.gamepad.leftStick.getY());
    }

    // 
    public double getDriveLeftPositive() {
        return sidewaysSpeedCurve.calculateMappedVal(this.gamepad.rightStick.getX());
    }

    //Positive is counter-clockwise, left Trigger is positive
    public double getDriveRotationCCWPositive() {
		double value = this.gamepad.triggers.getTwist();
		value = rotationCurve.calculateMappedVal(value);
		return value;        
    }

    // Return the angle created by the left stick in radians, 0 is up, pi/2 is left
    public Double getDriveAngle() {
        return Math.atan2(
            getDriveFwdPositive(),
            -getDriveLeftPositive());
    }

    public void rumble(double intensity) {
        this.gamepad.setRumble(intensity, intensity);
     }
}
