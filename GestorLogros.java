import java.util.ArrayList;
import java.util.List;

public class GestorLogros {
    // Instancia única (Singleton)
    private static GestorLogros instancia;
    private List<Logros.Logro> listaLogros;

    private GestorLogros() {
        listaLogros = new ArrayList<>();
        // Inicializamos los logros aquí una sola vez al arrancar el juego
        listaLogros.add(new Logros.Logro("Caballo Imparable", 500));
        listaLogros.add(new Logros.Logro("Cantado!", 500));
        listaLogros.add(new Logros.Logro("Francotirador del Area", 500));
        listaLogros.add(new Logros.Logro("Jackpot Supremo", 500));
        listaLogros.add(new Logros.Logro("La Mano del Destino", 500));
        listaLogros.add(new Logros.Logro("Nervios de Acero", 500));
        listaLogros.add(new Logros.Logro("Todo al Rojo", 500));
        listaLogros.add(new Logros.Logro("21 Perfecto", 500));
    }

    public static synchronized GestorLogros getInstancia() {
        if (instancia == null) {
            instancia = new GestorLogros();
        }
        return instancia;
    }

    public List<Logros.Logro> getListaLogros() {
        return listaLogros;
    }

    public void cambiarEstadoLogro(int indice, boolean completado) {
        if (indice >= 0 && indice < listaLogros.size()) {
            listaLogros.get(indice).setCompletado(completado);
        }
    }
}