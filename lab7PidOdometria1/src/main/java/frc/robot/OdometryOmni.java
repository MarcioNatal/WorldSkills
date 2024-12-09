package frc.robot;

public class OdometryOmni 
{
    
    // Variáveis de estado do robô
    private double x = 0.0;  // Posição no eixo X
    private double y = 0.0;  // Posição no eixo Y
    private double theta = 0.0;  // Orientação do robô (em radianos)

    // Raio das rodas
    //private double raioRoda = 0.05; // em metros
    // Distância entre as rodas e o centro do robô
    private double distanciaCentro = 0.15; // em metros

    // Atualiza a pose do robô baseado nas velocidades das rodas
    public void atualizarOdometria(double velRoda1, double velRoda2, double velRoda3, double deltaT) 
    {
        // Matriz inversa do modelo cinemático do robô
        double[][] matrizInversa = 
        {
            {Math.sqrt(3) / 3, -Math.sqrt(3) / 3, 0},
            {1.0 / 3, 1.0 / 3, -2.0 / 3},
            {1.0 / (3 * distanciaCentro), 1.0 / (3 * distanciaCentro), 1.0 / (3 * distanciaCentro)}
        };

        // Vetor de velocidades das rodas
        double[] velRodas = {velRoda1, velRoda2, velRoda3};

        // Vetor de velocidades do robô
        double[] velRobo = {0, 0, 0};

        // Multiplicação de matriz para obter velocidades x, y, e theta
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                velRobo[i] += matrizInversa[i][j] * velRodas[j];
            }
        }

        // Atualizar posição e orientação
        x += velRobo[0] * deltaT;
        y += velRobo[1] * deltaT;
        theta += velRobo[2] * deltaT;
    }

    public double getX() 
    {
        return x;
    }

    public double getY() 
    {
        return y;
    }

    public double getTheta() 
    {
        return theta;
    }

    /*public class OdometriaOmnidirecional 
    {
    
        // Variáveis de estado do robô
        private double x = 0.0;  // Posição no eixo X
        private double y = 0.0;  // Posição no eixo Y
        private double theta = 0.0;  // Orientação do robô (em radianos)
    
        // Raio das rodas
        private double raioRoda = 0.052; // em metros
        // Distância entre as rodas e o centro do robô
        private double distanciaCentro = 0.16; // em metros
    
        // Atualiza a pose do robô baseado nas velocidades das rodas
        public void atualizarOdometria(double velRoda1, double velRoda2, double velRoda3, double deltaT) {
            // Matriz inversa do modelo cinemático do robô
            double[][] matrizInversa = {
                {Math.sqrt(3) / 3, -Math.sqrt(3) / 3, 0},
                {1.0 / 3, 1.0 / 3, -2.0 / 3},
                {1.0 / (3 * distanciaCentro), 1.0 / (3 * distanciaCentro), 1.0 / (3 * distanciaCentro)}
            };
    
            // Vetor de velocidades das rodas
            double[] velRodas = {velRoda1, velRoda2, velRoda3};
    
            // Vetor de velocidades do robô
            double[] velRobo = {0, 0, 0};
    
            // Multiplicação de matriz para obter velocidades x, y, e theta
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    velRobo[i] += matrizInversa[i][j] * velRodas[j];
                }
            }
    
            // Atualizar posição e orientação
            x += velRobo[0] * deltaT;
            y += velRobo[1] * deltaT;
            theta += velRobo[2] * deltaT;
        }
    
        public double getX() {
            return x;
        }
    
        public double getY() {
            return y;
        }
    
        public double getTheta() {
            return theta;
        }
    
        /*public void main(String[] args) {
            OdometriaOmnidirecional odometria = new OdometriaOmnidirecional();
            
            // Exemplo de velocidades angulares das rodas (em rad/s)
            double velRoda1 = 1.0;
            double velRoda2 = -1.0;
            double velRoda3 = 0.5;
    
            // Intervalo de tempo (deltaT)
            double deltaT = 0.1; // 100 ms
    
            // Atualizar odometria
            odometria.atualizarOdometria(velRoda1, velRoda2, velRoda3, deltaT);
    
            // Imprimir nova posição e orientação
            System.out.println("x: " + odometria.getX());
            System.out.println("y: " + odometria.getY());
            System.out.println("theta: " + odometria.getTheta());
        }
    }*/

}