package frc.robot.arm;

import java.lang.reflect.Method;

//the funni code!1!1!1!!

//Yippww

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotConfig.LimitSwitches;

public class ArmSubSys extends SubsystemBase {
    private WPI_TalonFX mArmMotor = new WPI_TalonFX(ArmConfig.kMotorPort);
    private DigitalInput upperlimitSwitch = new DigitalInput(LimitSwitches.armUpperLimitSw);
    private DigitalInput lowerlimitSwitch = new DigitalInput(LimitSwitches.armLowerLimitSw);

    
    public ArmSubSys() {
        armMotorConfig();
        stopArm();
    }

    public void armMotorConfig(){
        //code blah blah blah blah aaaaaaaaa
    }

    public void periodic() {
        if (isLowerLimitSwitchPressed() == true) {
            resetEncoder();
        }
    }


    public void raiseArm() {
        if (isUpperLimitSwitchPressed() == false) {
            mArmMotor.set(ArmConfig.kRaiseSpeed);
        }else {
            holdArm();
        }
        
    }

    public void lowerArm() {
        if (isLowerLimitSwitchPressed() == false) {
            mArmMotor.set(ArmConfig.kLowerSpeed);
        }else {
            stopArm();
        }
    }

    public void holdArm() {
        mArmMotor.set(ArmConfig.kHoldSpeed);
    }

    public void stopArm() {
        mArmMotor.stopMotor();
    }

    public boolean isLowerLimitSwitchPressed() {
        if (lowerlimitSwitch.get() == ArmConfig.lowerLimitSwitchTrue) {
            return true;
        }
        return false;
    }

    public boolean isUpperLimitSwitchPressed() {
        if (upperlimitSwitch.get() == ArmConfig.upperLimitSwitchTrue) {
            return true;
        }
        return false;
    }

    public String lowerLimitSwitchStatus(){
        if (lowerlimitSwitch.get() == ArmConfig.lowerLimitSwitchTrue) {
            return "Pressed";
        }
        return "Not Pressed";
    }

    //
    // The funny code
    public String UpperLimitSwitchStatus() {
        if (upperlimitSwitch.get() == ArmConfig.upperLimitSwitchTrue) {
            return "Pressed";
        }
        return "Not Pressed";
    }

    public void resetEncoder() {
        // Sets encoder to 0
        mArmMotor.setSelectedSensorPosition(0);
    }

    public double getEncoder() {
        return mArmMotor.getSelectedSensorPosition();
    }
}

