/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

//WPI imports
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//Vendor imports
import com.studica.frc.TitanQuad;
import com.studica.frc.TitanQuadEncoder;
import com.kauailabs.navx.frc.AHRS;

import frc.robot.Constants;

/**
 * DriveTrain class
 * <p>
 * This class creates the instance of the Titan and enables and sets the speed of the defined motor.
 */

public class DriveTrain extends SubsystemBase 
{
  /**
     * Declare Motors
     */
    private TitanQuad motor1; //right motor
    private TitanQuad motor2; // back motor
    private TitanQuad motor3; //left motor
    

    /**
     * Declare Encoders
     */
    
    private TitanQuadEncoder encoderMotor1;
    private TitanQuadEncoder encoderMotor2;
    private TitanQuadEncoder encoderMotor3;

    /**
     * Sensors
     */
    private AHRS navX;

    /**
     * Shuffleboard
     */
    
    /**
     * Constructor
     */
  /**
   * Creates a new DriveTrain.
   */
  public DriveTrain() 
  {
    /**
     * Instantiate Objects
     * Motors
     */
    
    motor1 = new TitanQuad(Constants.TITAN_ID, Constants.M1);
    motor2 = new TitanQuad(Constants.TITAN_ID, Constants.M2);
    motor3 = new TitanQuad(Constants.TITAN_ID, Constants.M3);



   

    

    /**
       * Encoders
       *@param 
       * motor , motor id, distance per tick
       */
    encoderMotor1 = new TitanQuadEncoder(motor1, Constants.M1, Constants.wheelDistPerTick);
    encoderMotor2 = new TitanQuadEncoder(motor2, Constants.M2, Constants.wheelDistPerTick);
    encoderMotor3 = new TitanQuadEncoder(motor3, Constants.M3, Constants.wheelDistPerTick);

    /**
     * Sensors
     */
    navX = new AHRS(SPI.Port.kMXP);
    


  }

  @Override
  public void periodic() 
  {
    // This method will be called once per scheduler run
    /*leftEncoderValue.setDouble(getEncoderMotor3Distance());
    rightEncoderValue.setDouble(getEncoderMotor1Distance());
    backEncoderValue.setDouble(getEncoderMotor2Distance());
    gyroValue.setDouble(getYaw());*/

    //We can monitore the robot's heading on the SmartDashboard            
    SmartDashboard.putNumber("Robot Heading", getYaw());
    SmartDashboard.putNumber("Robot Pitch", getPitch());
    SmartDashboard.putNumber("Robot Roll", getRoll());

    /**SmartDashboard.putNumber("Encoder m1", getEncoderMotor1Distance());
    SmartDashboard.putNumber("Encoder m2", getEncoderMotor2Distance());
    SmartDashboard.putNumber("Encoder m3", getEncoderMotor3Distance());*/

  }

  /**
     * Sets the speed of the motor
     * <p>
     * @param speed range -1 to 1 (0 stop)
     */
    public void setMotor3Speed(double speed)
    {
        motor3.set(speed);
    }

    /**
     * Sets the speed of the motor
     * <p>
     * @param speed range -1 to 1 (0 stop)
     */
    public void setMotor1Speed(double speed)
    {
        motor1.set(speed);
    }

    /**
     * Sets the speed of the motor
     * <p>
     * @param speed range -1 to 1 (0 stop)
     */
    public void setMotor2Speed(double speed)
    {
       motor2.set(speed);
    }

    /**
     * Sets the speed of the drive motors
     * <p>
     * @param speed range -1 to 1 (0 stop)
     */
    public void setDriveMotorSpeeds(double motor3Speed, double motor1Speed, double motor2Speed)
    {
        motor3.set(motor3Speed);
        motor1.set(motor1Speed);
        motor2.set(motor2Speed);
    }

    /**
     * Drives the robot using a holonomic algorithim 
     * <p>
     * @param x axis value (-1 to 1)
     * @param y axis value (-1 to 1)
     * @param z axis value (-1 to 1)
     */
    public void holonomicDrive(double x, double y, double z)


    {

        // 2. Apply deadband
        x = Math.abs(x) > Constants.kDeadband ? x : 0.0; //se abs de x for maior que deadband manda x senao manda 0
        y = Math.abs(y) > Constants.kDeadband ? y : 0.0;
        z = Math.abs(z) > Constants.kDeadband ? z : 0.0;

        //componentes de um vetor de velocidade desejada para o robô.
        /***************************************************************************************
         * o	motor1Speed: Essa equação calcula a velocidade do motor 1, considerando 
         * a combinação das velocidades desejadas nas direções x, y e z. 
         * O termo (x/3) - (y/Math.sqrt(3)) indica que a velocidade do motor 1 é influenciada 
         * positivamente pela velocidade desejada na direção x e negativamente pela velocidade 
         * desejada na direção y.
         * motor3Speed: Similar ao motor 1, mas com os sinais de y invertidos.
         * motor2Speed: Essa equação calcula a velocidade do motor 2, considerando 
         * principalmente a velocidade desejada na direção x e z.
        ******************************************************************************************/
        /*double motor1Speed = ((x / 3) - (y / Math.sqrt(3)) + z) * Math.sqrt(3);
        double motor3Speed = ((x / 3) + (y / Math.sqrt(3)) + z) * Math.sqrt(3);
        double motor2Speed = (-2 * x / 3) + z;*/

        

        double motor1Speed = 0.5 * x - y + z ;
        double motor2Speed = - x + z;
        double motor3Speed = 0.5 * x + y + z;
        
        
        double max = Math.abs(motor1Speed);
    
    

        /***********************************************************************
         * O código calcula o valor absoluto de cada velocidade e compara com 
         * o valor máximo atual. 
         * Se o valor absoluto da velocidade atual for maior que o máximo atual, 
         * o valor máximo é atualizado.
         *************************************************************************/

        if (Math.abs(motor3Speed) > max) max = Math.abs(motor3Speed);
        if (Math.abs(motor2Speed) > max) max = Math.abs(motor2Speed);

        //garante que todas as velocidades estejam entre -1 e 1
        if (max > 1)
        {
            motor1Speed /= max;
            motor3Speed /= max;
            motor2Speed /= max;
        }

        motor3.set(motor3Speed);
        motor1.set(motor1Speed);
        motor2.set(motor2Speed);

        SmartDashboard.putNumber("Eixo x", x);
        SmartDashboard.putNumber("Eixo y", y);
        SmartDashboard.putNumber("Eixo z", z);

        SmartDashboard.putNumber("Motor 1 speed", motor1Speed);
        SmartDashboard.putNumber("Motor 2 speed", motor2Speed);
        SmartDashboard.putNumber("Motor 3 speed", motor3Speed);



    }
    
    
    /**
     * Gets the encoder distance for the left drive motor
     * <p>
     * @return distance traveled in mm
     */
    public double getEncoderMotor3Distance()
    {
        return encoderMotor3.getEncoderDistance();
    }

    /**
     * Gets the encoder distance for the right drive motor
     * <p>
     * @return distance traveled in mm
     */
    public double getEncoderMotor1Distance()
    {
        return encoderMotor1.getEncoderDistance() * -1;
    }

    /**
     * Gets the encoder distance for the back drive motor
     * <p>
     * @return distance traveled in mm
     */
    public double getEncoderMotor2Distance()
    {
        return encoderMotor2.getEncoderDistance();
    }

    /**
     * Gets the average forward or reverse encoder distance
     * <p>
     * @return distance traveled in mm
     */
    public double getAverageForwardEncoderDistance()
    {
        return (getEncoderMotor3Distance() + getEncoderMotor1Distance()) / 2; 
    }

    /**
     * Call for the current Yaw angle from the internal NavX
     * <p>
     * @return yaw angle in degrees range -180° to 180°
     */
    public double getYaw()
    {
        return navX.getYaw();
    }

    public double getPitch()
    {
        return navX.getPitch();
    }

    public double getRoll()
    {
        return navX.getRoll();
    }

    /**
     * Resets all encoders
     */
    public void resetEncoders()
    {
        encoderMotor3.reset();
        encoderMotor1.reset();
        encoderMotor2.reset();
    }

    /**
     * Sets the Yaw value back to zero
     */
    public void resetYaw()
    {
        navX.zeroYaw();
    }

    

}
