/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.studica.frc.LidarMini;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.LidarConstants;

/**
 * Lidar Mini (YDLIDAR T-mini Plus) conectado a uma das portas USB 2.0 do VMX.
 * <p>
 * Nao confundir com a classe {@link com.studica.frc.Lidar}, que e o driver do
 * YDLIDAR X4: protocolo, baud rate e unidades sao diferentes.
 */
public class LidarSubsSystem extends SubsystemBase {

    // Instancia o Lidar Mini na porta USB superior do VMX.
    // O construtor apenas acorda o sensor (~250 ms), nao inicia a varredura.
    private final LidarMini lidar = new LidarMini(LidarConstants.kPort);

    // Ultima volta completa de 360 graus
    private LidarMini.ScanData scanData = new LidarMini.ScanData();

    // Flag para nao atualizar o dashboard quando nao esta varrendo
    private boolean scanning = false;

    public LidarSubsSystem() {

        // getInfo() e getHealth() SO funcionam antes de start():
        // esses comandos corrompem o fluxo de dados se enviados em modo scan.
        LidarMini.LidarInfo info = lidar.getInfo();
        LidarMini.LidarHealth health = lidar.getHealth();

        System.out.println("[Lidar] " + info);
        System.out.println("[Lidar] " + health);

        SmartDashboard.putString("Lidar/Serial", info.getSerialString());
        SmartDashboard.putNumber("Lidar/Modelo", info.model);
        SmartDashboard.putBoolean("Lidar/Saudavel", health.isHealthy());

        if (!health.isHealthy()) {
            System.out.println("[Lidar] ATENCAO: sensor reportou falha - " + health);
        }

        // O Lidar Mini nao tem filtros (cluster/kalman/media/mediana/jitter):
        // essa API existe somente no driver do X4.
        startScan();
    }

    @Override
    public void periodic() {

        telemetry();
    }

    public void telemetry() {
        if (!scanning) {
            return;
        }

        // Snapshot da ultima volta; nunca retorna null, mas pode vir vazio
        // enquanto a primeira volta nao terminar.
        scanData = lidar.getData();
        SmartDashboard.putNumber("Lidar/Pontos", scanData.size());

        if (scanData.size() == 0) {
            SmartDashboard.putBoolean("Lidar/LeituraValida", false);
            return;
        }

        // Distancia em METROS (-1.0 = fora de alcance ou interferencia).
        // O vetor tem tamanho variavel por volta, por isso se busca pelo angulo
        // em vez de indexar uma posicao fixa.
        float distance = scanData.getDistanceAt(
            LidarConstants.kTargetAngleDegrees, LidarConstants.kAngleToleranceDegrees);

        SmartDashboard.putNumber("Lidar/Angulo", LidarConstants.kTargetAngleDegrees);
        SmartDashboard.putNumber("Lidar/Distancia_m", distance);
        SmartDashboard.putBoolean("Lidar/LeituraValida", distance > 0.0f);
    }

    /**
     * Distancia medida no angulo pedido, em metros.
     *
     * @param angleDegrees angulo desejado [0, 360)
     * @return distancia em metros, ou -1.0 se nao houver leitura valida
     */
    public double getDistanceMeters(double angleDegrees) {
        return scanData.getDistanceAt(
            (float) angleDegrees, LidarConstants.kAngleToleranceDegrees);
    }

    /**
     * Inicia a varredura. Necessario mesmo apos o construtor: diferente do
     * driver do X4, o Lidar Mini nao comeca a girar sozinho.
     */
    public void startScan() {
        if (!scanning) {
            lidar.start();
            scanning = true;
        }
    }

    /**
     * Para a varredura. A porta serial continua aberta, basta chamar
     * {@link #startScan()} para retomar.
     */
    public void stopScan() {
        if (scanning) {
            lidar.stop();
            scanning = false;
        }
    }

    public boolean isScanning() {
        return scanning;
    }

    /**
     * Frequencia de varredura atual em Hz (6.0 a 12.0, padrao 6.0).
     */
    public float getScanFrequencyHz() {
        return lidar.getCurrentFrequency();
    }
}
