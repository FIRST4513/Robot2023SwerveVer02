package frc.robot.arm;

public class ArmConfig {

    // ------- Encoder Conversion Factor --------------
    // 1797.41176 cnts per degree
    public final static double kCntsPerDeg = 1797.41176;      // Convert cnt to degrees of angle
    public final static double kDegsPerCnt = 0.00055636;      // Convert cnt to degrees of angle
    public final static double KAngleDeadBand     = +1.0;            // How close to target degree is close enough

    // ------ Speed Constants ------
    public final static double kRaiseSpeed  = +0.25;
    public final static double kLowerSpeed  = -0.15;

    public final static double kArmMotorRaiseMaxPwr = +0.5;
    public final static double kArmMotorLowerMaxPwr = -0.35;

    // ------ Limit Switch ------
    public final static boolean lowerLimitSwitchTrue = false;
    public final static boolean upperLimitSwitchTrue = false;

    public final static double lowerLimitSwitchAngle = +56.0;
    public final static double upperLimitSwitchAngle = -47.0;

    // ------ Arm Angle Constants (In Degrees) ------
    public final static double minArmAngle = -20.0;
    public final static double maxArmAngle = +30.0;

    public final static double ArmAngleFullRetractPos = -25.0;
    public final static double ArmAngleStorePos       = -15.0;

    // These would be for single value for Cone or Cube
    public final static double ArmAngleLowPos         = +0.0;  // Pickup position
    public final static double ArmAngleMidPos         = +15.0;  // Pickup position
    public final static double ArmAngleHighPos        = +25.0;  // Pickup position

    // These would be for separate values for Cone or Cube
    public final static double ArmAngleConeLowPos         = +15.0;  // Angle to Eject Cone for Low Score 
    public final static double ArmAngleConeMidPos         = +20.0;
    public final static double ArmAngleConeHighPos        = +30.0;
    public final static double ArmAngleCubeLowPos         = +15.0;  // Angle to Eject Cube for Low Score
    public final static double ArmAngleCubeMidPos         = +20.0;
    public final static double ArmAngleCubeHighPos        = +25.0;

}
