## Creating Automonous Routine

Creating autonomous commands for an FRC robot, particularly with the WPILib framework, involves defining individual actions as commands and then orchestrating them into a sequence or parallel group.

## :one: Define Individual Actions as Commands:
   
:arrow_right: Each distinct action your robot performs (e.g., driving a certain distance, aiming a shooter, intaking a game piece) should be encapsulated within its own Command class. 

:robot: Example: DriveDistance Command.

````java
    public class DriveDistance extends CommandBase {
        private final DrivetrainSubsystem m_drivetrain;
        private final double m_distance;

        public DriveDistance(DrivetrainSubsystem drivetrain, double distance) {
            m_drivetrain = drivetrain;
            m_distance = distance;
            addRequirements(m_drivetrain);
        }

        @Override
        public void initialize() {
            // Reset encoders, start driving
        }

        @Override
        public void execute() {
            // Control motors to drive towards the target distance
        }

        @Override
        public boolean isFinished() {
            // Return true when the target distance is reached
            return m_drivetrain.getDistance() >= m_distance;
        }

        @Override
        public void end(boolean interrupted) {
            m_drivetrain.stop();
        }
    }

````

## :two:Combine Commands into Autonomous Routines:

- Autonomous routines are typically built using CommandGroup classes to sequence or parallelize individual commands.
  
- **Sequential Command Group:** Executes commands one after another.
<br>

  ````java
  
    public class AutonomousRoutine extends SequentialCommandGroup {
        public AutonomousRoutine(DrivetrainSubsystem drivetrain, ShooterSubsystem shooter) {
            addCommands(
                new DriveDistance(drivetrain, 5.0), // Drive 5 feet
                new AimCommand(shooter),           // Aim the shooter
                new ShootCommand(shooter)          // Shoot
            );
        }
    }

  ````
<br>

- **Parallel Command Group:** Executes commands simultaneously

  ````java

      public class ParallelActions extends ParallelCommandGroup {
        public ParallelActions(IntakeSubsystem intake, ShooterSubsystem shooter) {
            addCommands(
                new IntakeCommand(intake),
                new SpinUpShooterCommand(shooter)
            );
        }
    }

  ````
<br>

## :three: Selecting and Running Autonomous Routines:

- **SendableChooser:** Use this in your *RobotContainer* to allow selection of different autonomous routines from the SmartDashboard.
<br>

  ````java
  
    private final SendableChooser<Command> m_chooser = new SendableChooser<>();

    public RobotContainer() {
        m_chooser.setDefaultOption("Simple Auto", new SimpleAuto(m_drivetrain));
        m_chooser.addOption("Complex Auto", new ComplexAuto(m_drivetrain, m_shooter));
        SmartDashboard.putData("Auto Mode", m_chooser);
    }

    public Command getAutonomousCommand() {
        return m_chooser.getSelected();
    }


  ````

> [!IMPORTANT]
>
> :heavy_check_mark:This is crucial to odometry.
> 
> :heavy_check_mark: Ensure the correct robot orientation in relation to the field.
>
> :heavy_check_mark: If your robot is in the blue alliance and facing forward to the red wall its orientation angle is `0 rad`. 
> 
> :heavy_check_mark: But when your robot is in the red alliance facing forward to the blue alliance wal its orientation angle is `π rad`,
>
> so your robot should be flipped at the begining of the match, otherwise, the odometry will crash.
>
<br>

:arrow_down: Down below is an example within the `drive` method 

<br>


```java

boolean isFlipped = DriverStation.getAlliance().isPresent() && DriverStation.getAlliance().get() == Alliance.Blue;
swerveDrive.drive(ChassisSpeeds.fromFieldRelativeSpeeds(speeds,
                           isFlipped ? swerveDrive.getPoseEstimator().getRotation())
                           : swerveDrive.getPoseEstimator().getRotation().plus(new Rotation2d(Math.PI)));


```



