import java.awt.Color;
import java.awt.Graphics;

public class Trazo {
    String tipo;
    int x1, y1, x2, y2;
    Color color;

    public Trazo(String tipo, int x1, int y1, int x2, int y2, Color color) {
        this.tipo = tipo;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
    }

    public void dibujar(Graphics g) {
        g.setColor(color);
        switch (tipo) {
            case "Linea":
                g.drawLine(x1, y1, x2, y2);
                break;
            case "Rectángulo":
                g.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
                break;
            case "Circulo":
                int radio = (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
                g.drawOval(x1 - radio, y1 - radio, radio * 2, radio * 2);
                break;
        }
    }

    public void dibujarBorde(Graphics g) {
        g.setColor(Color.RED); // Color del borde
        switch (tipo) {
            case "Linea":
                g.drawLine(x1, y1, x2, y2); // Dibuja la línea como borde
                break;
            case "Rectángulo":
                g.drawRect(Math.min(x1, x2) - 2, Math.min(y1, y2) - 2,
                            Math.abs(x2 - x1) + 4, Math.abs(y2 - y1) + 4); // Dibuja el rectángulo como borde
                break;
            case "Circulo":
                int radio = (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
                g.drawOval(x1 - radio - 2, y1 - radio - 2, (radio + 2) * 2, (radio + 2) * 2); // Dibuja el círculo como borde
                break;
        }
    }

    public boolean contains(int x, int y) {
        switch (tipo) {
            case "Linea":
                // Comprobar si el punto (x, y) está cerca de la línea
                return Math.abs((double)(y - y1) / (y2 - y1) - (double)(x - x1) / (x2 - x1)) < 0.05; // Ajusta el valor de tolerancia según sea necesario
            case "Rectángulo":
                return (x >= Math.min(x1, x2) && x <= Math.max(x1, x2) && y >= Math.min(y1, y2) && y <= Math.max(y1, y2));
            case "Circulo":
                int radio = (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
                return Math.pow(x - x1, 2) + Math.pow(y - y1, 2) <= Math.pow(radio + 2, 2); // Añade un margen
            default:
                return false;
        }
    }

    public void actualizarCoordenadas(int x, int y) {
        this.x2 = x;
        this.y2 = y;
    }

    @Override
    public String toString() {
        return tipo + "," + x1 + "," + y1 + "," + x2 + "," + y2 + "," + color.getRGB();
    }
}
