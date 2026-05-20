import java.util.ArrayList;
import java.util.List;

/**
 * GestorNotificaciones - Singleton global de notificaciones.
 * Cualquier ventana puede leer y marcar notificaciones desde aquí.
 */
public class GestorNotificaciones {

    // ── Modelo ────────────────────────────────────────────────────────────────
    public static class Notificacion {
        public final String emoji;
        public final String titulo;
        public final String descripcion;
        public final String tiempo;
        public boolean leida;

        public Notificacion(String emoji, String titulo, String descripcion, String tiempo) {
            this.emoji       = emoji;
            this.titulo      = titulo;
            this.descripcion = descripcion;
            this.tiempo      = tiempo;
            this.leida       = false;
        }
    }

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static GestorNotificaciones instancia;

    private final List<Notificacion> lista = new ArrayList<>();

    private GestorNotificaciones() {
        // Notificaciones de ejemplo (se pueden agregar dinámicamente en el juego)
        lista.add(new Notificacion("\uD83C\uDFAE", "Logro desbloqueado",
                "Ganaste el logro 'Primera Victoria'", "Hace 2 min"));
        lista.add(new Notificacion("\uD83D\uDCB0", "Saldo recibido",
                "+50 UP por desaf\u00EDo diario completado", "Hace 1 hora"));
        lista.add(new Notificacion("\uD83D\uDCAC", "Nuevo mensaje",
                "Carlos te envi\u00F3 un mensaje en el chat", "Hace 3 horas"));
        lista.add(new Notificacion("\uD83C\uDFC6", "Ranking actualizado",
                "Subiste al puesto #12 en el ranking global", "Hace 5 horas"));
        lista.add(new Notificacion("\uD83C\uDF89", "\u00A1Bienvenido a UniPoints!",
                "Explora todas las funciones disponibles", "Hace 1 d\u00EDa"));
    }

    public static synchronized GestorNotificaciones getInstancia() {
        if (instancia == null) {
            instancia = new GestorNotificaciones();
        }
        return instancia;
    }

    // ── API pública ───────────────────────────────────────────────────────────

    public List<Notificacion> getLista() {
        return lista;
    }

    public int contarNoLeidas() {
        int c = 0;
        for (Notificacion n : lista) {
            if (!n.leida) c++;
        }
        return c;
    }

    public void marcarTodasLeidas() {
        for (Notificacion n : lista) {
            n.leida = true;
        }
    }

    /** Agrega una notificación nueva (no leída) en tiempo de ejecución. */
    public void agregar(String emoji, String titulo, String descripcion, String tiempo) {
        lista.add(0, new Notificacion(emoji, titulo, descripcion, tiempo));
    }
}
