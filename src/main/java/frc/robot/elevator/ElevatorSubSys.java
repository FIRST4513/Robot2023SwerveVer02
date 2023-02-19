package frc.robot.elevator;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenixpro.controls.VoltageOut;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.Rmath;
import frc.robot.RobotConfig.LimitSwitches;
import frc.robot.RobotConfig.Motors;

public class ElevatorSubSys extends SubsystemBase {
    public ElevatorConfig           config;
    public static ElevFXMotorConfig configFX;
    public ElevFXMotorConfig        motorConfig;

    // Devices
    public final WPI_TalonFX m_motor;
    public final DigitalInput elevLowerLimitSw, elevUpperLimitSw;

    // PID Controller
    public final PIDController elevPIDcontroller;

    // Elevator Variables
    public double target_height;

    public double mCurrPwr = 0;
    public double mCurrElevPwr;
    public double PID_output = 0;
    public double mCurrEncoderCnt = 0;
    public double mCurrElevHt;      // Elevator Height Zero at bottom (Inches)

    // -----------  Constructor --------------------
    public ElevatorSubSys() {
        config              = new ElevatorConfig();
        configFX            = new ElevFXMotorConfig();
        m_motor             = new WPI_TalonFX(Motors.elevatorMotorID);
        elevLowerLimitSw    = new DigitalInput(LimitSwitches.elevatorLowerLimitSw);
        elevUpperLimitSw    = new DigitalInput(LimitSwitches.elevatorUpperLimitSw);
        elevPIDcontroller   = new PIDController(config.kP, config.kI, config.kD);
        elevatorMotorConfig();
    }

   @Override
   public void periodic() {
       updateCurrentElevPosition();
    }

    // --------------------------------------------
    // ---------   Elevator Methods   -------------
    // --------------------------------------------

    // ------------ Lower Elevator ----------
    public void elevLower() {
        elevSetSpeed( config.KLowerSpeedDefault ); 
    }

    // ------------ Raise Elevator ----------
    public void elevRaise() {
        elevSetSpeed( config.KRaiseSpeedDefault ); 
    }

    // ------------ Hold Elevator Position ----------
    public void elevHoldMtr(){
        elevSetSpeed( config.KHoldSpeedDefault ); 
    }

    // ------------ Stop Elevator Motor  ----------
    public void elevStop() {
        mCurrElevPwr = 0;        
        m_motor.stopMotor();
    }

    // ------------ This does all the work (Non PID) to Drive the Elevator ----------
    public void elevSetSpeed(double speed){
        // Cap speed to max
        if ( speed > config.raiseMaxSpeed )  { speed = config.raiseMaxSpeed; }
        if ( speed < config.lowerMaxSpeed )  { speed = config.lowerMaxSpeed; }

        // Were Raising the elevator
        if ( speed > config.KHoldSpeedDefault ) {
            // Test for hitting Upper Limits
            if ( isUpperLimitReached() ) {
                elevHoldMtr();
                return;
            }
            //  This is for slowing down as we approach the top
            if ( mCurrElevHt >= config.KLimitElevTopSlowHt )  {
                speed = config.KRaiseSlowSpeed;
            }
        }

        // Were Lowering the elevator
        if ( speed <= config.KHoldSpeedDefault) {
            // Test if hitting Bottom limit switch
            if ( isLowerLimitSwitchPressed() ) {
                elevStop();
                return;
            }
            //  This is for slowing down as we approach the bottom    		
            if ( mCurrElevHt <= config.KLimitElevBottomSlowHt ) {
                speed = config.KLowerSlowSpeed;
            }
        }

        mCurrElevPwr = speed;
        //m_motor.setVoltage( speed * 12.0);      // This is supposed to be more constant
        m_motor.set(mCurrElevPwr);              // Send Power to motor  
    }

    public void elevSetSpeed(DoubleSupplier speed) {
        elevSetSpeed(speed.getAsDouble());
    }


    // ------------  Set Elevator to Height by PID  ----------    
    public void setPIDheight( double ht ) {
        // All motion controlled relative to Floor
        target_height = limit_target_ht( ht);
        mCurrElevPwr = getPidCalcOut( target_height );
        System.out.println("    PID to " + ht + "   CurPos=" + mCurrElevHt + " out=" + mCurrElevPwr);
        elevSetSpeed(mCurrElevPwr); 
    }

    // ---------  PID Out Calculator  --------------
    public double getPidCalcOut(double tgt_setpoint) {
        double tgt = limit_target_ht (tgt_setpoint);
        double out = elevPIDcontroller.calculate(mCurrElevHt, tgt );
        out = out + config.kF;             // Add feedforward Term
        return out;
    }

    // Test PID Calc routine
    public double getPidCalcTestOut(double test_setpoint) {
        return getPidCalcOut(test_setpoint);
    }

    // ------------  Set Elev to Height by Motion Magic  ----------
    public void setMMPosition(double height) {
        double position = convertHeightToFalconCnt(height);
        //m_motor.set(ControlMode.MotionMagic, position);
        m_motor.set( ControlMode.MotionMagic, position,
                     DemandType.ArbitraryFeedForward,
                     ElevFXMotorConfig.arbitraryFeedForward);
    }

// -----------------  Encoder Sensor Methods --------------------
    public double getElevEncoderCnt()               { return mCurrEncoderCnt;}
    public double getElevHeightInches()             { return mCurrElevHt; }

    public void updateCurrentElevPosition() {
        // Called in subsystem periodic so only one CAN call needed per command loop 20ms
        mCurrEncoderCnt = m_motor.getSelectedSensorPosition();
        mCurrElevHt = Rmath.mRound(convertFalconCntToHeight(mCurrEncoderCnt) , 2);
    }

    public double convertHeightToFalconCnt( double height) {
        return height / config.kElevatorEncoderDistPerPulse;
    }

    public double convertFalconCntToHeight( double cnt) {
        return config.kElevatorEncoderDistPerPulse * cnt;
    }

    public void resetEncoder(){
        m_motor.setSelectedSensorPosition(0);
        mCurrEncoderCnt = 0.0 ;
        mCurrElevHt = 0.0;
    }

    public void resetEncoder( double position ){
        m_motor.setSelectedSensorPosition(position);
        mCurrEncoderCnt = position;
        mCurrElevHt = Rmath.mRound(convertFalconCntToHeight(position) , 2);
    }

    // ------------- Other Misc Methods  ---------------
    public double getTargetHeight()         { return target_height; }
    public double getElevMotorPwr()         { return mCurrElevPwr; }

    public double limit_target_ht( double ht ){
        if (ht > config.KElevMaxTopHt)     { ht = config.KElevMaxTopHt; }
        if (ht < 0)                        { ht = 0; }
        return ht;
    }

    // -----------------  Lower/Upper Limits ----------------
    public boolean isLowerLimitSwitchPressed() {
        if (elevLowerLimitSw.get() == config.lowerLimitTrue) {
            return true;
        }
        return false;
    }

    public boolean isUpperLimitSwitchPressed() {
        if (elevUpperLimitSw.get() == config.lowerLimitTrue) {
            return true;
        }
        return false;
    }

    public boolean isUpperLimitReached() {
        if ( mCurrElevHt >= config.KElevMaxTopHt ) {
            return true;
        }
        return false;
    }

    public boolean isLowerLmtSwNotPressed() { return !isLowerLimitSwitchPressed(); }
    public boolean isUpperLmtNotReached()   { return !isUpperLimitSwitchPressed(); }

    public String getUpperLimitSwStatus(){
        if ( isUpperLimitSwitchPressed() ) {
            return "Pressed"; 
        } else {
            return "Not Pressed";
        }
    }

    public String getLowerLimitSwStatus(){
        if ( isLowerLimitSwitchPressed() ) {
            return "Pressed"; 
        } else {
            return "Not Pressed";
        }
    }

    
    // ------------------------------------------------------
    // ---------- Commands from Spectrum 2023 Code ----------
    // ------------------------------------------------------

    public void zeroElevator() {
        m_motor.setSelectedSensorPosition(0);
    }

    public void resetSensorPosition(double pos) {
        m_motor.setSelectedSensorPosition(pos); // 10 for now, will change later
    }

    public void softLimitsTrue() {
        m_motor.configReverseSoftLimitEnable(true);
    }

    public void softLimitsFalse() {
        m_motor.configReverseSoftLimitEnable(false);
    }

    // public double getKf() {
    //     TalonFXConfiguration FXconfig = new TalonFXConfiguration();
    //     m_motor.getAllConfigs(FXconfig);
    //     return FXconfig.slot0.kF;
    // }


    /**
     * Converts inches to meters.
     *
     * @param inches
     * @return meters
     */
    public static double inchesToMeters(double inches) {
        double meters = inches * 0.0254;
        return meters;
    }

    // --------------------------------------------------------
    // ---------------- Configure Elev Motor ------------------
    // --------------------------------------------------------
    public void elevatorMotorConfig(){
        // This config is for the Talon FX Controller
        m_motor.configFactoryDefault();
        m_motor.configAllSettings(ElevFXMotorConfig.config);
        m_motor.setInverted(ElevFXMotorConfig.elevMotorInvert);
        m_motor.setNeutralMode(ElevFXMotorConfig.elevNeutralMode);
        m_motor.setSelectedSensorPosition(0);                     // Reset Encoder to zero
    }
}
