/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.studica.frc.Titan;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.AtiradorConstants;


public class AtiradorSubsystem extends SubsystemBase {
  /**
   * Creates a new Atirador.
   */
  private final Titan.Motor motorAtirador;
  private final Titan.Encoder encoderAtirador;

  private  boolean flagPosition = false;
  private double target = 0.0;

  // Enquanto verdadeiro, o periodic() recalcula o PID e escreve no motor todo
  // ciclo. E isso que segura a posicao depois que o comando termina: o comando
  // so diz "o alvo e X", quem fecha a malha e o subsystem.
  private boolean holding = false;

  // So faz sentido perguntar atPosition() depois que o periodic() rodou pelo
  // menos uma vez com o alvo novo. Sem isso o atSetpoint() de um PID recem
  // resetado responde "chegou" com erro zero e o comando terminaria no 1o ciclo.
  private boolean amostraValida = false;
  
  private PIDController pidController = new PIDController(AtiradorConstants.kPositionP,
                                                          AtiradorConstants.kPositionI,
                                                          AtiradorConstants.kPositionD);
  private SimpleMotorFeedforward prev = new SimpleMotorFeedforward(AtiradorConstants.ks, 
                                                                  AtiradorConstants.kv,
                                                                  AtiradorConstants.ka);

                                                                  

  /**
   * @param titan controlador compartilhado com os outros subsystems do mesmo
   *              TitanQuad. Nao criar um Titan aqui: um CAN ID = um objeto Titan.
   */
  public AtiradorSubsystem(Titan titan) {
    motorAtirador = titan.getMotor(AtiradorConstants.M3);
    encoderAtirador = titan.getEncoder(AtiradorConstants.kEncoder3Channel,
                                       AtiradorConstants.wheelDistPerTick);


    //O erro e medido em mm, igual a getEncoder1Position()
    pidController.setTolerance(AtiradorConstants.kPositionToleranceMM);

    resetPositionControl();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run

    // A malha fechada mora aqui, nao no execute() do comando. O periodic() roda
    // em TODO ciclo do scheduler, mesmo sem nenhum comando agendado, entao a
    // correcao continua existindo depois que o AtiradorToPositionCmd termina.
    /*if (holding) {
      // setVoltage divide pela tensao real da bateria, entao o mesmo erro gera o
      // mesmo esforco com bateria cheia ou caindo.
      motorAtirador.setVoltage(
          pidController.calculate(this.getEncoderAtiradorPosition(), target));
      amostraValida = true;
    }*/

    telemetria();
  }

  /**
   * Liga motor em 50%
   */
  public void setMotorAtirador(){

    holding = false; // malha aberta: desarma o hold para o periodic nao brigar
    motorAtirador.set(0.5);
  }

  /**
   * Para o motor da Atirador
   */
  public void stopMotorAtirador(){
    holding = false; // desarma o hold, senao o periodic religa o motor
    motorAtirador.stopMotor();
  }

  /**
   * Le a velocidade do motor da Atirador
   * @return velocida em rpm como double
   */
  public double getSpeedAtirador(){
    return encoderAtirador.getSpeed();
  }


  /**
   * Le a velocida do motor da Atirador em RPS
   * @return velocidade em rps como double
   */
  public double getSpeedAtiradorRPS(){

    return this.getSpeedAtirador()/60.0;
  }

  /**
     * Gets encoder position 
     * 
     * @return distance in mm as double
     */
    public double getEncoderAtiradorPosition(){
      return encoderAtirador.getDistance();
    }

    /**
     * Zera o ponto de partida do controle de posicao.
     * Chamar no initialize() do comando, antes do primeiro setMotorPosition().
     */
    public void resetPositionControl()
    {
      encoderAtirador.reset();
      pidController.reset();
      //lagPosition = false;
      //holding = false;
      //amostraValida = false;
    }

    /**
     * Controle de posicao SEM PID (liga/desliga, bang-bang).
     * Precisa ser chamado a cada ciclo do scheduler, igual ao PID.
     *
     * A saida e sempre 0.5 enquanto nao chegou e 0 depois: nao existe
     * desaceleracao perto do alvo, por isso o robo passa do ponto.
     * E exatamente esse overshoot que o PID elimina.
     *
     * @param setpoint distancia alvo em mm, contada desde resetPositionControl()
     */
    public void runToPosition(double setpoint){

      holding = false; // bang-bang e malha aberta: o PID do periodic fica fora
      // A distancia ja vem zerada no reset, entao ela mesma e o quanto andou.
      if(this.getEncoderAtiradorPosition() < setpoint){
        motorAtirador.set(0.5);
        flagPosition = false;
      }
      else{
        motorAtirador.stopMotor();
        flagPosition = true;
      }
    }

    /**
     * Define o alvo da malha fechada de posicao e arma o hold.
     * <p>
     * Basta chamar UMA vez: quem recalcula o PID e escreve no motor a cada ciclo
     * e o {@link #periodic()}. Por isso o motor continua sendo segurado depois
     * que o comando termina, e volta sozinho se alguem empurrar o eixo.
     *
     * @param setPoint distancia alvo em mm, contada desde o resetPositionControl()
     */
    public void setMotorPosition(double setPoint)
    {/*
      if (setPoint != target) {
        amostraValida = false; // alvo novo: espera o periodic medir o erro real
      }
      target = setPoint;
      holding = true;*/
      target = setPoint;
      motorAtirador.setVoltage(
          pidController.calculate(this.getEncoderAtiradorPosition(), setPoint));
    }


    /**
     * Verdadeiro se chegou na posição
     * @return boolean
     */
    public boolean atPosition(){

     /*if (!holding) {
        return flagPosition; // modo bang-bang: quem decide e o runToPosition()
      }*/
      //return amostraValida && pidController.atSetpoint();

      return pidController.atSetpoint();

    }
     public void holdMotor()
     {
       motorAtirador.setPositionHold(true);

     }

  /**
   * Verdadeiro quando a Atirador esta girando acima do limite, em qualquer sentido.
   * Compara o modulo porque getSpeed() e sinalizado.
   *
   * @see AtiradorConstants#kAtSpeedRPM limite em RPM do eixo do encoder
   * @return verdadeiro na velocidade desejada
   */
  public boolean atSpeed(){

    return Math.abs(this.getSpeedAtirador()) > AtiradorConstants.kAtSpeedRPM;
  }

  /**
   * Atualiza mediçoes do motor na dashboard
   */
  public void telemetria(){

    SmartDashboard.putBoolean("AtiradorSubsystem/Atingiu Velocidade", this.atSpeed());
    SmartDashboard.putNumber("AtiradorSubsytem/Velocidade rps", this.getSpeedAtiradorRPS());
    // Para comparar com o PID na aula: posicao medida e condicao de parada
    SmartDashboard.putNumber("AtiradorSubsystem/Posicao mm", this.getEncoderAtiradorPosition());
    SmartDashboard.putBoolean("AtiradorSubsystem/Atingiu Posicao", this.atPosition());
    SmartDashboard.putNumber("AtiradorSubsystem/Setpoint Posicao",target);

  }
}
