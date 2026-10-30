package com.studica.frc;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.PWMSpeedController;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;


public class ServoContinuous extends PWMSpeedController {

    public ServoContinuous(int channel) {
        super(channel);

        setBounds(2.5, 1.55, 1.50, 1.45, 0.5);
        setPeriodMultiplier(PeriodMultiplier.k4X);
        setSpeed(0.0);
        setZeroLatch();
        
        HAL.report(tResourceType.kResourceType_Servo, getChannel() + 1);
        SendableRegistry.setName(this, "ServoContinuous", getChannel());
      }    
}