/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import com.studica.frc.Titan;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class OmniDrive extends SubsystemBase 
{
  /**
   * Creates a new OmniDrive.
   * 
   */
  /**
     * Motors
     */
    private Titan titan;
    private Titan.Motor motor1;
    private Titan.Motor motor2;
    private Titan.Motor motor3;

    /**
     * Sensores
     */
    private AHRS gyro;

  public OmniDrive() 
  {

      titan = new Titan(Constants.TITAN_ID);
      motor1 = titan.getMotor(Constants.M1);
      motor2 = titan.getMotor(Constants.M2);
      motor3 = titan.getMotor(Constants.M3);

      gyro = new AHRS (SPI.Port.kMXP);
  }

  @Override
  public void periodic() 
  {
    // This method will be called once per scheduler run
    SmartDashboard.putNumber("Direcao Robo", getYaw());
  }

  /**
   * 
   * @return double do angulo de rotacao
   */
  public double getYaw()
  {
    return gyro.getYaw();

  }
 
  /**
   * reseta giroscopio
   */
  public void resetGyro ()
  {
      gyro.zeroYaw();
  }

  public void setMotorSpeed(double speed)
  {
    motor1.set(speed);

  }

  /**
   * 
   * @param speed1
   * @param speed2
   * @param speed3
   */
  public void set3MotorSpeed(double speed1, double speed2, double speed3)
  {
    motor1.set(speed1);
    motor2.set(speed2);
    motor3.set(speed3);

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
    


}
