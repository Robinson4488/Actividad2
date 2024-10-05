import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class EditorDibujo extends JFrame {
    private ListaLigada listaTrazos; // Cambiar de ArrayList a ListaLigada
    private DibujoPanel panelDibujo;
    private Color colorLinea = Color.BLACK;
    private Color colorFondo = Color.WHITE;
    private int startX, startY, endX, endY;
    private String tipoTrazo = "Linea";
    private Trazo trazoSeleccionado = null; // Trazo actualmente seleccionado
    private JButton botonDesplazar; // Botón para desplazar
    private boolean modificandoTamano = false; // Estado para modificar tamaño
    private boolean desplazando = false; // Estado de desplazamiento activo

    public EditorDibujo() {
        listaTrazos = new ListaLigada(); // Inicializa la lista ligada
        setTitle("Editor de Dibujos");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        panelDibujo = new DibujoPanel();
        panelDibujo.setBackground(colorFondo);
        add(panelDibujo, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(0, 1));
        add(panelBotones, BorderLayout.EAST);

        // Botones de selección de figura
        String[] tiposTrazos = {"Linea", "Rectángulo", "Circulo"};
        JComboBox<String> comboTiposTrazos = new JComboBox<>(tiposTrazos);
        comboTiposTrazos.addActionListener(e -> {
            tipoTrazo = (String) comboTiposTrazos.getSelectedItem(); // Actualizar tipoTrazo
        });
        panelBotones.add(comboTiposTrazos);

        // Selector de color de línea
        JButton botonColorLinea = new JButton("Color de Línea");
        botonColorLinea.setBackground(colorLinea);
        botonColorLinea.addActionListener(e -> {
            Color nuevoColor = JColorChooser.showDialog(null, "Seleccionar color de línea", colorLinea);
            if (nuevoColor != null) {
                colorLinea = nuevoColor;
                botonColorLinea.setBackground(colorLinea);
            }
        });
        panelBotones.add(botonColorLinea);

        // Selector de color de fondo
        JButton botonColorFondo = new JButton("Color de Fondo");
        botonColorFondo.setBackground(colorFondo);
        botonColorFondo.addActionListener(e -> {
            Color nuevoFondo = JColorChooser.showDialog(null, "Seleccionar color de fondo", colorFondo);
            if (nuevoFondo != null) {
                colorFondo = nuevoFondo;
                panelDibujo.setBackground(colorFondo);
                botonColorFondo.setBackground(colorFondo);
            }
        });
        panelBotones.add(botonColorFondo);

        // Botón para eliminar el trazo seleccionado
        JButton botonEliminar = new JButton("Eliminar Trazo");
        botonEliminar.addActionListener(e -> {
            // Eliminar trazos
            if (trazoSeleccionado != null) {
                listaTrazos.eliminarTrazo(trazoSeleccionado); // Eliminar el trazo seleccionado
                trazoSeleccionado = null; // Reiniciar selección
                panelDibujo.repaint();
            }
        });
        panelBotones.add(botonEliminar);

        // Botón para desplazar el trazo seleccionado
        botonDesplazar = new JButton("Desplazar Trazo");
        botonDesplazar.addActionListener(e -> {
            desplazando = !desplazando; // Activa o desactiva el modo de desplazamiento
            botonDesplazar.setBackground(desplazando ? Color.GREEN : null); // Cambia el color del botón
            modificandoTamano = false; // Desactiva la modificación de tamaño
        });
        panelBotones.add(botonDesplazar);

        // Botón para guardar trazos
        JButton botonGuardar = new JButton("Guardar");
        botonGuardar.addActionListener(e -> guardarTrazosConBusqueda());
        panelBotones.add(botonGuardar);

        // Botón para abrir trazos
        JButton botonAbrir = new JButton("Abrir");
        botonAbrir.addActionListener(e -> abrirTrazosConBusqueda());
        panelBotones.add(botonAbrir);

        // Agregar el mouse listener directamente al panelDibujo
        panelDibujo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
                endX = startX; // Inicializa
                endY = startY; // Inicializa

                // Comprobar si se ha hecho clic en un trazo existente
                for (Nodo actual = listaTrazos.cabeza; actual != null; actual = actual.siguiente) {
                    if (actual.trazo.contains(startX, startY)) {
                        trazoSeleccionado = actual.trazo; // Marca como seleccionado
                        panelDibujo.repaint(); // Redibuja para mostrar el borde
                        return; // Sale del método
                    }
                }

                // Solo añadir un nuevo trazo si no está en modo de desplazamiento
                if (!desplazando) {
                    Trazo nuevoTrazo = new Trazo(tipoTrazo, startX, startY, endX, endY, colorLinea);
                    listaTrazos.agregarTrazo(nuevoTrazo); // Usar lista ligada
                    trazoSeleccionado = nuevoTrazo; // Selecciona el trazo recién creado
                    panelDibujo.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                endX = e.getX();
                endY = e.getY();
                panelDibujo.repaint(); // Redibuja al soltar el mouse
            }
        });

        // Mouse Motion Listener para dibujar en tiempo real y desplazamiento
        panelDibujo.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                endX = e.getX();
                endY = e.getY();

                if (desplazando && trazoSeleccionado != null) {
                    // Desplaza el trazo seleccionado
                    int deltaX = endX - startX; // Diferencia en X
                    int deltaY = endY - startY; // Diferencia en Y

                    trazoSeleccionado.x1 += deltaX;
                    trazoSeleccionado.y1 += deltaY;
                    trazoSeleccionado.x2 += deltaX;
                    trazoSeleccionado.y2 += deltaY;

                    startX = endX; // Actualiza startX para el siguiente movimiento
                    startY = endY; // Actualiza startY para el siguiente movimiento

                    panelDibujo.repaint(); // Redibuja
                } else if (!desplazando && trazoSeleccionado != null) {
                    // Actualizar coordenadas del trazo nuevo mientras arrastras el mouse
                    trazoSeleccionado.actualizarCoordenadas(endX, endY);
                    panelDibujo.repaint();
                }
            }
        });
    }

    // Guardar trazos con campo de búsqueda personalizado
    private void guardarTrazosConBusqueda() {
        JFileChooser fileChooser = new JFileChooser();

        // Crear el campo de búsqueda
        JTextField campoBusqueda = new JTextField(15);
        JButton botonBuscar = new JButton("Buscar");

        // Panel que contiene el campo de búsqueda y el botón
        JPanel panelBusqueda = new JPanel(new FlowLayout());
        panelBusqueda.add(new JLabel("Buscar:"));
        panelBusqueda.add(campoBusqueda);
        panelBusqueda.add(botonBuscar);

        // Agregar el panel de búsqueda como accesorio en JFileChooser
        fileChooser.setAccessory(panelBusqueda);

        // Lógica de búsqueda al presionar el botón de buscar
        botonBuscar.addActionListener(e -> {
            String nombreArchivo = campoBusqueda.getText().toLowerCase();
            File directorioActual = fileChooser.getCurrentDirectory();

            // Filtrar archivos en el directorio actual según el texto ingresado
            File[] archivosFiltrados = directorioActual.listFiles((dir, name) -> name.toLowerCase().contains(nombreArchivo));

            if (archivosFiltrados != null && archivosFiltrados.length > 0) {
                fileChooser.setCurrentDirectory(new File(archivosFiltrados[0].getParent()));  // Cambiar el directorio actual
                fileChooser.setSelectedFile(archivosFiltrados[0]);  // Seleccionar el archivo filtrado
            } else {
                JOptionPane.showMessageDialog(fileChooser, "Archivo no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Mostrar el diálogo de guardar archivos
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try {
                listaTrazos.guardarTrazosEnArchivo(archivo); // Usar el método de la lista ligada
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al guardar el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Abrir trazos con campo de búsqueda personalizado
    private void abrirTrazosConBusqueda() {
        JFileChooser fileChooser = new JFileChooser();

        // Crear el campo de búsqueda
        JTextField campoBusqueda = new JTextField(15);
        JButton botonBuscar = new JButton("Buscar");

        // Panel que contiene el campo de búsqueda y el botón
        JPanel panelBusqueda = new JPanel(new FlowLayout());
        panelBusqueda.add(new JLabel("Buscar:"));
        panelBusqueda.add(campoBusqueda);
        panelBusqueda.add(botonBuscar);

        // Agregar el panel de búsqueda como accesorio en JFileChooser
        fileChooser.setAccessory(panelBusqueda);

        // Lógica de búsqueda al presionar el botón de buscar
        botonBuscar.addActionListener(e -> {
            String nombreArchivo = campoBusqueda.getText().toLowerCase();
            File directorioActual = fileChooser.getCurrentDirectory();

            // Filtrar archivos en el directorio actual según el texto ingresado
            File[] archivosFiltrados = directorioActual.listFiles((dir, name) -> name.toLowerCase().contains(nombreArchivo));

            if (archivosFiltrados != null && archivosFiltrados.length > 0) {
                fileChooser.setCurrentDirectory(new File(archivosFiltrados[0].getParent()));  // Cambiar el directorio actual
                fileChooser.setSelectedFile(archivosFiltrados[0]);  // Seleccionar el archivo filtrado
            } else {
                JOptionPane.showMessageDialog(fileChooser, "Archivo no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Mostrar el diálogo de apertura de archivos
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            listaTrazos = new ListaLigada(); // Reinicia la lista ligada antes de cargar nuevos
            try {
                listaTrazos.cargarTrazosDesdeArchivo(archivo); // Usar el método de la lista ligada
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al abrir el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
            panelDibujo.repaint(); // Repaint después de cargar los trazos
        }
    }

    // Panel personalizado para dibujar
    class DibujoPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (Nodo actual = listaTrazos.cabeza; actual != null; actual = actual.siguiente) {
                actual.trazo.dibujar(g);
            }

            // Si hay un trazo seleccionado, dibujar el borde
            if (trazoSeleccionado != null) {
                Graphics2D g2d = (Graphics2D) g; // Convertir a Graphics2D
                g2d.setColor(Color.RED); // Color del borde
                g2d.setStroke(new BasicStroke(2)); // Grosor del borde
                trazoSeleccionado.dibujarBorde(g2d); // Dibuja el borde del trazo seleccionado
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EditorDibujo editor = new EditorDibujo();
            editor.setVisible(true);
        });
    }
}
