/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.studica.frc.Titan;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.GarraConstants;

public class GarraSubsystem extends SubsystemBase {
  /**
   * Creates a new Garra.
   */
  private final Titan.Motor motorGarra;
  private final Titan.Encoder encoderGarra;

  /**
   * @param titan controlador compartilhado com os outros subsystems do mesmo
   *              TitanQuad. Nao criar um Titan aqui: um CAN ID = um objeto Titan.
   */
  public GarraSubsystem(Titan titan) {
    motorGarra = titan.getMotor(GarraConstants.M2);
    encoderGarra = titan.getEncoder(GarraConstants.kEncoder2Channel,
                                    GarraConstants.wheelDistPerTick);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    telemetria();
  }

  /**
   * Liga motor em 50%
   */
  public void setMotorGarra(){

    motorGarra.set(0.5);
  }

  /**
   * Para o motor da garra
   */
  public void stopMotorGarra(){
    motorGarra.stopMotor();
  }

  /**
   * Le a velocidade do motor da garra
   * @return velocida em rpm como double
   */
  public double getSpeedGarra(){
    return encoderGarra.getSpeed();
  }


  /**
   * Le a velocida do motor da garra em RPS
   * @return velocidade em rps como double
   */
  public double getSpeedGarraRPS(){

    return this.getSpeedGarra()/60.0;
  }

  


  /**
   * Verdadeiro quando a garra esta girando acima do limite, em qualquer sentido.
   * Compara o modulo porque getSpeed() e sinalizado.
   *
   * @see GarraConstants#kAtSpeedRPM limite em RPM do eixo do encoder
   * @return verdadeiro na velocidade desejada
   */
  public boolean isAtSpeed(){

    return Math.abs(this.getSpeedGarra()) > GarraConstants.kAtSpeedRPM;
  }

  /**
   * Atualiza mediçoes do motor na dashboard
   */
  public void telemetria(){

    SmartDashboard.putBoolean("GarraSubsystem/Atingiu Velocidade", this.isAtSpeed());
    SmartDashboard.putNumber("GarraSubsytem/Velocidade rps", this.getSpeedGarraRPS());
    
  

  }
}
