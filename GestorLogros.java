import java.util.ArrayList;
import java.util.List;

public class GestorLogros {
    // Instancia única (Singleton)
    private static GestorLogros instancia;
    private List<Logros.Logro> listaLogros;

    private GestorLogros() {
        listaLogros = new ArrayList<>();
        // Inicializamos los logros aquí una sola vez al arrancar el juego
        listaLogros.add(new Logros.Logro("si", 200));
        listaLogros.add(new Logros.Logro("no", 300));
        listaLogros.add(new Logros.Logro("haz que juancacorro haga algo (este logro es imposible)", 100));
        listaLogros.add(new Logros.Logro("tal vez", 500));
        listaLogros.add(new Logros.Logro("123456", 200));
        listaLogros.add(new Logros.Logro("your mom", 150));
        listaLogros.add(new Logros.Logro("your dad", 150));
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