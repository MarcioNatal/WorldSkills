/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpiutil.math.MathUtil;
import frc.robot.Constants.DriveTrainConstants;

//Vendor imports
import com.studica.frc.Titan;



/**
 * DriveTrain class
 * <p>
 * This class creates the instance of the Titan and enables and sets the speed of the defined motor.
 */

public class DriveTrainSubsystem extends SubsystemBase {
  /**
   * Motors
   */
  private final Titan.Motor motor;
  private final Titan.Encoder encoder1;

  private PIDController pidController = new PIDController(DriveTrainConstants.kPositionP,
                                                          DriveTrainConstants.kPositionI,
                                                          DriveTrainConstants.kPositionD);

  /**
   * Constructor
   */
  /**
   * Creates a new DriveTrain.
   *
   * @param titan controlador compartilhado com os outros subsystems do mesmo
   *              TitanQuad. Nao criar um Titan aqui: um CAN ID = um objeto Titan.
   */
  public DriveTrainSubsystem(Titan titan) 
  {
    //Motors
    motor = titan.getMotor(DriveTrainConstants.M1);
    encoder1 = titan.getEncoder(DriveTrainConstants.kEncoder1Channel,
                                DriveTrainConstants.wheelDistPerTick);

    motor.setInverted(false);

    encoder1.reset();

    //O erro e medido em mm, igual a getEncoder1Position()
    pidController.setTolerance(DriveTrainConstants.kPositionToleranceMM);
    

  }

 

  @Override
  public void periodic() 
  {
    // This method will be called once per scheduler run
  }

  /**
   * Set motor speed
   * @param speed double
   */
    public void setMotorSpeed(double speed)
    {
        motor.set(speed);
      
    }

    /**
     * Liga o motor do drive
     * @return runnable
     */
    public Command ligaMotor(){

      return new RunCommand(()->this.setMotorSpeed(0.5),this);
    }

    /**
     * Stops drive motor
     */
    public void stopMotorDrive()
    {

      motor.stopMotor();
    }

    /**
     * Gets encoder position 
     * @see DriveTrainConstants.wheelDistPerTick
     * 
     * @return distance in mm as double
     */
    public double getEncoder1Position(){
      return encoder1.getDistance();
    }

    /**
     * Zera o ponto de partida do controle de posicao.
     * Chamar no initialize() do comando, antes do primeiro setMotorPosition().
     */
    public void resetPositionControl()
    {
      encoder1.reset();
      pidController.reset();
    }

    /**
     * Malha fechada de posicao. Precisa ser chamado a cada ciclo do scheduler
     * (execute() do comando), senao o PID so calcula uma amostra e para.
     *
     * @param setPoint distancia alvo em mm, contada desde o resetPositionControl()
     */
    public void setMotorPosition(double setPoint)
    {
      double output = pidController.calculate(this.getEncoder1Position(), setPoint);

      // setVoltage divide pela tensao real da bateria, entao o mesmo erro gera o
      // mesmo esforco com bateria cheia ou caindo. So funciona porque este
      // metodo e chamado a cada ciclo.
      motor.setVoltage(MathUtil.clamp(output,
                                      -DriveTrainConstants.kPositionMaxVolts,
                                      DriveTrainConstants.kPositionMaxVolts));
    }

    /**
     * @return true quando o erro esta dentro da tolerancia
     */
    public boolean atPosition()
    {
      return pidController.atSetpoint();
    }


    public boolean atSpeed(){
      return encoder1.getSpeed()>100.0;
    }
    
    public Command stopMotor1(){

      return new RunCommand(()-> this.stopMotorDrive(),this);
    
    }

    
    


}
