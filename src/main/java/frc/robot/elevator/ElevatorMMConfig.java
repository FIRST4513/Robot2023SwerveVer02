package frc.robot.elevator;

import frc.lib.subsystems.linearMech.LinearMechConfig;

public class ElevatorMMConfig extends LinearMechConfig {
    public static final String name = "Elevator";

    // All these are made up and need to be changed
    public final double cubeIntake = 5000;
    public final double cubeMid = 60000;
    public final double cubeTop = 100000;

    public final double coneIntake = 0;
    public final double coneStandingIntake = 0;
    public final double coneShelf = 130000;

    public final double coneMid = 130000;
    public final double coneTop = 150000;

    public final double diameterInches = 2.0; // changed from int, 4
    public final double gearRatio = 62 / 8;
    public final double maxUpFalconPos = 162116;

    public final double safePositionForFourBar = 0; // TODO: find safe position for four bar
    public final double startingHeight = 0; // TODO: find starting height
    public final double startingHorizontalExtension = 0; // TODO: find starting horizontal extension
    public final double maxExtension = 80000; // TODO: find max relative extension
    public final double angle = 60;

    public final double zeroSpeed = -0.2;

    public ElevatorMMConfig() {
        super("Elevator");
        this.kP = 0.5; // not accurate value, just testing
        this.kI = 0; // could be 0
        this.kD = 0; // could be 0
        this.kF = 0.3;
        this.motionCruiseVelocity = 50663;
        this.motionAcceleration = 60663;

        this.currentLimit = 25;
        this.tirggerThresholdLimit = 25;
        this.PeakCurrentDuration = 0.0;
        this.EnableCurrentLimit = true;
        updateTalonFXConfig();
    }
}