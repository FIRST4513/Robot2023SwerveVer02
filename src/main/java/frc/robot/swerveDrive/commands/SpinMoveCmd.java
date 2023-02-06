package frc.robot.swerveDrive.commands;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Robot;
import frc.robot.swerveDrive.SwerveDriveConfig;
import frc.robot.swerveDrive.SwerveDrive;

import java.util.function.DoubleSupplier;

public class SpinMoveCmd extends CommandBase {
    private Translation2d translation;
    private boolean centerHasBeenSet = false;

    private SwerveDrive swerve;
    private DoubleSupplier leftPositiveSupplier;
    private DoubleSupplier fwdPositiveSupplier;
    private DoubleSupplier ccwPositiveSupplier;
    private Translation2d centerOfRotationMeters;

    /** Creates a new DodgeDrive. */
    public SpinMoveCmd() {
        swerve = Robot.swerve;
        centerOfRotationMeters = new Translation2d();
        fwdPositiveSupplier =  Robot.pilotGamepad::getDriveFwdPositive;
        leftPositiveSupplier = Robot.pilotGamepad::getDriveLeftPositive;
        ccwPositiveSupplier =  Robot.pilotGamepad::getDriveRotationCCWPositive;

        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(swerve);
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
        centerHasBeenSet = false;
    }

    @Override
    public void execute() {
        double fwdPositive =    fwdPositiveSupplier.getAsDouble();
        double leftPositive =   leftPositiveSupplier.getAsDouble();
        double ccwPositive =    ccwPositiveSupplier.getAsDouble();

        translation = new Translation2d(fwdPositive, leftPositive);

        Rotation2d heading = translation.getAngle().minus(Robot.swerve.getHeading());
        double angle = heading.getDegrees();

        if (Math.abs(ccwPositive) >= 0.2 && !centerHasBeenSet) {
            Translation2d offsets[] = SwerveDriveConfig.moduleOffsets(Units.inchesToMeters(3));
            if (angle < 45 && angle >= -45) {
                // negative rotation is clockwise
                // positive rotation is counter-clockwise
                if (ccwPositive > 0) {
                    centerOfRotationMeters = SwerveDriveConfig.frontRightLocation.plus(offsets[1]);
                } else {
                    centerOfRotationMeters = SwerveDriveConfig.frontLeftLocation.plus(offsets[0]);
                }
            } else if (angle >= 45 && angle < 135) {
                if (ccwPositive > 0) {
                    centerOfRotationMeters = SwerveDriveConfig.frontLeftLocation.plus(offsets[0]);
                } else {
                    centerOfRotationMeters = SwerveDriveConfig.backLeftLocation.plus(offsets[2]);
                }
            } else if (angle >= 135 || angle < -135) {
                if (ccwPositive > 0) {
                    centerOfRotationMeters = SwerveDriveConfig.backLeftLocation.plus(offsets[2]);
                } else {
                    centerOfRotationMeters = SwerveDriveConfig.backRightLocation.plus(offsets[3]);
                }
            } else if (angle >= -135 && angle < -45) {
                if (ccwPositive > 0) {
                    centerOfRotationMeters = SwerveDriveConfig.backRightLocation.plus(offsets[3]);
                } else {
                    centerOfRotationMeters = SwerveDriveConfig.frontRightLocation.plus(offsets[1]);
                }
            }
            centerHasBeenSet = true;
        }
        swerve.drive(fwdPositive, leftPositive, ccwPositive, true, false, centerOfRotationMeters);
    }

    public void end(boolean interrupted) {
        swerve.stop();
    }
}
