import java.awt.Color;
import java.io.*;

class ListaLigada {
    Nodo cabeza;

    public ListaLigada() {
        cabeza = null;
    }

    public void agregarTrazo(Trazo trazo) {
        Nodo nuevoNodo = new Nodo(trazo);
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            Nodo actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevoNodo;
        }
    }

    public void eliminarTrazo(Trazo trazo) {
        if (cabeza == null) return;

        if (cabeza.trazo.equals(trazo)) {
            cabeza = cabeza.siguiente;
            return;
        }

        Nodo actual = cabeza;
        while (actual.siguiente != null) {
            if (actual.siguiente.trazo.equals(trazo)) {
                actual.siguiente = actual.siguiente.siguiente;
                return;
            }
            actual = actual.siguiente;
        }
    }

    public void guardarTrazosEnArchivo(File archivo) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            Nodo actual = cabeza;
            while (actual != null) {
                writer.write(actual.trazo.toString());
                writer.newLine();
                actual = actual.siguiente;
            }
        }
    }

    public void cargarTrazosDesdeArchivo(File archivo) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 6) {
                    String tipo = partes[0];
                    int x1 = Integer.parseInt(partes[1]);
                    int y1 = Integer.parseInt(partes[2]);
                    int x2 = Integer.parseInt(partes[3]);
                    int y2 = Integer.parseInt(partes[4]);
                    Color color = new Color(Integer.parseInt(partes[5]));
                    Trazo nuevoTrazo = new Trazo(tipo, x1, y1, x2, y2, color);
                    agregarTrazo(nuevoTrazo);
                }
            }
        }
    }
}
