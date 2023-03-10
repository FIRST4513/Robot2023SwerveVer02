package frc.lib.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.motorControllers.TalonFXSetup;

/** Creates a FollowerFalcon for a subsystem */
public class FollowerFalconFX extends SubsystemBase {
    protected WPI_TalonFX motorFollower;
    protected WPI_TalonFX leader;

    public FollowerFalconFX(
            int followerID,
            String canBus,
            TalonFXConfiguration config,
            boolean inverted,
            WPI_TalonFX leader) {
        motorFollower = new WPI_TalonFX(followerID, canBus);
        TalonFXSetup.configFollowerSetup(motorFollower, config);
        motorFollower.setInverted(inverted);
        this.leader = leader;
    }

    public FollowerFalconFX(
            int followerID, TalonFXConfiguration config, boolean inverted, WPI_TalonFX leader) {
        this(followerID, "rio", config, inverted, leader);
    }

    @Override
    public void periodic() {
        motorFollower.follow(leader);
    }

    public WPI_TalonFX get() {
        return motorFollower;
    }
}
