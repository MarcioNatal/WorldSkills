package  frc.robot;
public class OdometriaOmnidirecional 
{

    // Parâmetros do robô
    private double raioRodas;  // Raio das rodas (em metros)
    private double distanciaCentroRodas;  // Distância do centro do robô até as rodas (em metros)
    
    // Posição e orientação do robô
    private double posX = 0.0;  // Posição X (em metros)
    private double posY = 0.0;  // Posição Y (em metros)
    private double orientacao = 0.0;  // Orientação theta (em radianos)
    
    // Velocidades atuais (velocidade no eixo X, Y e rotação)
    private double vx = 0.0;
    private double vy = 0.0;
    private double omega = 0.0;  // Velocidade angular (rad/s)

    public OdometriaOmnidirecional(double raioRodas, double distanciaCentroRodas) 
    {
        this.raioRodas = raioRodas;
        this.distanciaCentroRodas = distanciaCentroRodas;
    }

    // Atualiza as velocidades lineares com base nas velocidades angulares das rodas
    public void atualizarVelocidades(double velocidadeRoda1, double velocidadeRoda2, double velocidadeRoda3) 
    {
        // Matriz de transformação para robô de 3 rodas em ângulos de 120 graus
        double sqrt3 = Math.sqrt(3);
        
        vx = (2.0 / 3.0) * raioRodas * (velocidadeRoda1 - (velocidadeRoda2 / 2.0) - (velocidadeRoda3 / 2.0));
        vy = (2.0 / 3.0) * raioRodas * ((sqrt3 / 2.0) * (velocidadeRoda2 - velocidadeRoda3));
        omega = (raioRodas / (3.0 * distanciaCentroRodas)) * (velocidadeRoda1 + velocidadeRoda2 + velocidadeRoda3);
    }

    // Atualiza a posição e a orientação do robô com base nas velocidades e no tempo decorrido
    public void atualizarPosicao(double deltaTime) 
    {
        // Integração simples para atualizar posição e orientação
        posX += vx * deltaTime;
        posY += vy * deltaTime;
        orientacao += omega * deltaTime;
    }

    // Métodos para obter a posição e a orientação atuais do robô
    public double getPosX() 
    {
        return posX;
    }

    public double getPosY() 
    {
        return posY;
    }

    public double getOrientacao() 
    {
        return orientacao;
    }
    
    /*public static void main(String[] args) 
    {
        // Exemplo de uso
        double raioRodas = 0.05;  // 5 cm de raio de roda
        double distanciaCentroRodas = 0.15;  // 15 cm de distância entre o centro e as rodas
        
        OdometriaOmnidirecional odometria = new OdometriaOmnidirecional(raioRodas, distanciaCentroRodas);
        
        // Exemplo: velocidade das rodas (em rad/s)
        double velocidadeRoda1 = 10.0;
        double velocidadeRoda2 = 12.0;
        double velocidadeRoda3 = 8.0;
        
        // Atualizar as velocidades do robô
        odometria.atualizarVelocidades(velocidadeRoda1, velocidadeRoda2, velocidadeRoda3);
        
        // Simular a atualização da posição após 1 segundo
        double deltaTime = 1.0;
        odometria.atualizarPosicao(deltaTime);
        
        // Mostrar a posição e a orientação do robô
       /* System.out.println("Posição X: " + odometria.getPosX());
        System.out.println("Posição Y: " + odometria.getPosY());
        System.out.println("Orientação: " + odometria.getOrientacao());
    }*/
}