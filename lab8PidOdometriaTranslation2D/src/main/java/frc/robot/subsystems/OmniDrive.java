/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

//WPI imports
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.Encoder;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.controller.PIDController;

import edu.wpi.first.wpilibj.geometry.Rotation2d;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//Vendor imports
import com.studica.frc.Titan;
import com.kauailabs.navx.frc.AHRS;

//
import frc.robot.Constants;


/**
 * DriveTrain class
 * <p>
 * This class creates the instance of the Titan and enables and sets the speed of the defined motor.
 */

public class OmniDrive extends SubsystemBase 
{
    /**
     * Declare Motors
     */
    private Titan titan;
    private Titan.Motor motor1; // left motor
    private Titan.Motor motor2; // back motor
    private Titan.Motor motor3; // right motor

    private SpeedController leftMotor;
    private SpeedController backMotor;
    private SpeedController rightMotor;

 

    // Current robot position and orientation
    private double xPosition;
    private double yPosition;
    private double robotAngle;

    // PID constants
    private double kP = 0.1; // Proportional gain
    private double kI = 0.0; // Integral gain
    private double kD = 0.0; // Derivative gain

    // Motor positions (in meters)
    private static final double LEFT_MOTOR_X = 0.08;
    private static final double LEFT_MOTOR_Y = -0.1386;
    private static final double RIGHT_MOTOR_X = 0.08;
    private static final double RIGHT_MOTOR_Y = 0.1386;
    //private static final double BACK_MOTOR_X = -0.16;
    private static final double BACK_MOTOR_Y = 0.0;

    // Target speed values
    private double backTargetSpeed = 0.0;
    private double leftTargetSpeed = 0.0;
    private double rightTargetSpeed = 0.0;

   
    /**
     * Declare Encoders
     */

    //private Titan.Encoder encoderMotor1;
    
    private final Encoder encoderMotor1; 
    private final Encoder encoderMotor2;
    private final Encoder encoderMotor3;
    
    private final PIDController motor3PIDController; //= new PIDController(0.035, 0, 0);
    private final PIDController motor1PIDController;// = new PIDController(0.035, 0, 0);
    private final PIDController motor2PIDController; //= new PIDController(0.035, 0, 0);

    


    /**
     * Sensors
     */
    private AHRS gyro;

    // Gains are for example purposes only - must be determined for your own robot!
    //private final SimpleMotorFeedforward m_feedforward = new SimpleMotorFeedforward(1, 3);

    public static final double kMaxSpeed = Constants.rot2MeterPerSec; // 3 meters per second
    public static final double kMaxAngularSpeed = Constants.encoderRot2RadPerSec; // 1/2 rotation per second
    
    /**
     * Constructor
     */
    /**
     * Creates a new DriveTrain.
     */
    public OmniDrive() 
    {
        /**
         * Instantiate Objects
         * Motors
         */
        
        titan = new Titan(Constants.TITAN_ID);
        motor1 = titan.getMotor(Constants.M1);
        motor2 = titan.getMotor(Constants.M2);
        motor3 = titan.getMotor(Constants.M3);

        
        this.leftMotor = motor1;
        this.rightMotor = motor3;
        this.backMotor = motor2;

    
        /**
         * Encoders
         *@param 
        * motor , motor id, distance per tick
        */
            this.encoderMotor1 = new Encoder(Constants.kMotor1tEncoderPortA,
                                        Constants.kMotor1tEncoderPortB,
                                        Constants.kMotor1EncoderReversed);
                                        
                                        encoderMotor1.setDistancePerPulse(Constants.wheelDistPerTick);

            this.encoderMotor2 = new Encoder(Constants.kMotor2tEncoderPortA,
                                        Constants.kMotor2tEncoderPortB,
                                        Constants.kMotor2EncoderReversed);
            
                                        encoderMotor2.setDistancePerPulse(Constants.wheelDistPerTick);

            this.encoderMotor3 = new Encoder(Constants.kMotor3tEncoderPortA,
                                        Constants.kMotor3tEncoderPortB,
                                        Constants.kMotor3EncoderReversed);
                                        
                                        encoderMotor3.setDistancePerPulse(Constants.wheelDistPerTick);


        // Initialize PID controllers
        
        motor1PIDController = new PIDController(kP, kI, kD);
        motor2PIDController = new PIDController(kP, kI, kD);
        motor3PIDController = new PIDController(kP, kI, kD);

        // Configure PID controllers
        motor1PIDController.setSetpoint(0.0); // Set target speed to 0
        motor2PIDController.setSetpoint(0.0);
        motor3PIDController.setSetpoint(0.0);

        // Enable PID controllers
        
        

        /**
         * Sensors
         */
        this.gyro = new AHRS(SPI.Port.kMXP);
        gyro.reset();

        // Initialize position and orientation
        xPosition = 0.0;
        yPosition = 0.0;
        robotAngle = 0.0;
    }

  
    // Update robot position and orientation
    public void updateOdometry() 
    {
        // Get encoder values
        
        double leftEncoderDistance = getEncoderDistance(encoderMotor1);
        double rightEncoderDistance = getEncoderDistance(encoderMotor3);
        double backEncoderDistance = getEncoderDistance(encoderMotor2);

        // Calculate robot movement based on encoder distances and robot orientation
        double deltaX = (backEncoderDistance + leftEncoderDistance + rightEncoderDistance) / 3.0 * Math.cos(robotAngle);
        double deltaY = (backEncoderDistance + leftEncoderDistance + rightEncoderDistance) / 3.0 * Math.sin(robotAngle);

        // Update robot position
        xPosition += deltaX;
        yPosition += deltaY;

        // Update robot orientation from NavX
        robotAngle = gyro.getYaw();

        // Publish odometry data to SmartDashboard
        SmartDashboard.putNumber("X Position", xPosition);
        SmartDashboard.putNumber("Y Position", yPosition);
        SmartDashboard.putNumber("Robot Angle", robotAngle);
    }

    // Update motor control based on joystick input
    public void driveMotorControl(double forwardInput, double strafeInput, double rotateInput) {
        // Get joystick input
        

        // Calculate motor speeds based on joystick input and motor positions
        backTargetSpeed = forwardInput * (BACK_MOTOR_Y / (LEFT_MOTOR_Y + RIGHT_MOTOR_Y)) + rotateInput;
        leftTargetSpeed = forwardInput * (LEFT_MOTOR_Y / (LEFT_MOTOR_Y + RIGHT_MOTOR_Y)) - strafeInput * (LEFT_MOTOR_X / (LEFT_MOTOR_Y + RIGHT_MOTOR_Y)) + rotateInput;
        rightTargetSpeed = forwardInput * (RIGHT_MOTOR_Y / (LEFT_MOTOR_Y + RIGHT_MOTOR_Y)) + strafeInput * (RIGHT_MOTOR_X / (LEFT_MOTOR_Y + RIGHT_MOTOR_Y)) + rotateInput;

        // Set target speed for PID controllers
        motor2PIDController.setSetpoint(backTargetSpeed);
        motor1PIDController.setSetpoint(leftTargetSpeed);
        motor3PIDController.setSetpoint(rightTargetSpeed);

        leftMotor.setVoltage(motor1PIDController.calculate(encoderMotor1.getRate(), leftTargetSpeed));
        backMotor.setVoltage(motor2PIDController.calculate(encoderMotor2.getRate(), backTargetSpeed));
        rightMotor.setVoltage(motor3PIDController.calculate(encoderMotor3.getRate(), leftTargetSpeed));

        // Publish target speeds to SmartDashboard
        SmartDashboard.putNumber("Back Target Speed", backTargetSpeed);
        SmartDashboard.putNumber("Left Target Speed", leftTargetSpeed);
        SmartDashboard.putNumber("Right Target Speed", rightTargetSpeed);
    }

    // Set the PID constants
    public void setPIDConstants(double kP, double kI, double kD) 
    {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;

        // Update PID controller constants
        motor1PIDController.setPID(kP, kI, kD);
        motor2PIDController.setPID(kP, kI, kD);
        motor3PIDController.setPID(kP, kI, kD);
    }


    // Get encoder distance traveled in mm
    private double getEncoderDistance(Encoder encoder) 
    {
        // setDistancePerPulse(wheelDistPerTick) ja foi aplicado no construtor,
        // entao getDistance() ja devolve milimetros - nao escalar de novo.
        return encoder.getDistance();
    }

    // Get current robot X position
    public double getXPosition() 
    {
        return xPosition;
    }

    // Get current robot Y position
    public double getYPosition() {
        return yPosition;
    }

    // Get current robot orientation in radians
    public double getRobotAngle() {
        return robotAngle;
    }

   
    // This method will be called once per scheduler run
    @Override
    public void periodic() 
    {
         // Update the odometry in the periodic block
    
        // m_odometry.update(Rotation2d.fromDegrees(gyro.getAngle()), getCurrentState());
        updateOdometry();
        //We can monitore the robot's heading on the SmartDashboard            
        SmartDashboard.putNumber("Robot Heading", getYaw());

    }

    
    public Rotation2d getRotation2d ()
    {

        return Rotation2d.fromDegrees(gyro.getAngle());
    }
  
  
  
  
    public void stopMotors( )
    {
        holonomicDrive(0, 0, 0);
    }

    /**
     * Drives the robot using a holonomic algorithim 
     * <p>
     * @param x axis value (-1 to 1)
     * @param y axis value (-1 to 1) valor negativo vai para frente
     * @param rot  rotate axis value (-1 to 1)
     */
    public void holonomicDrive(double x, double y, double rot)
    {

        // 2. Apply deadband
        x = Math.abs(x) > Constants.kDeadband ? x : 0.0; //se abs de x for maior que deadband manda x senao manda 0
        y = Math.abs(y) > Constants.kDeadband ? y : 0.0;
        rot = Math.abs(rot) > Constants.kDeadband ? rot : 0.0;

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

        

        double motor1Speed = 0.5 * x - y + rot ;
        double motor2Speed = - x + rot;
        double motor3Speed = 0.5 * x + y + rot;
        
        
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
        SmartDashboard.putNumber("Eixo z", rot);

       
    }
    
    

    /**
     * Call for the current Yaw angle from the internal NavX
     * <p>
     * @return yaw angle in degrees range -180° to 180°
     */
    public double getYaw()
    {
        return gyro.getYaw();
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
        gyro.zeroYaw();
    }

    

}
