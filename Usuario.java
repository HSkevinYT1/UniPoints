
// aqui se guardan los datos del usuario
import java.util.HashMap;

public class Usuario {
    private String nombre;
    private String correo;
    private String username;
    private String password;

    // cositas extra
    private int victorias;
    private int derrotas;
    private String banner;
    private double saldo;
    private String fotoPerfil;

    // base de datos simulada
    private static HashMap<String, Usuario> usuariosDB = new HashMap<>();
    private static Usuario usuarioActual = null;

    public Usuario(String nombre, String correo, String username, String password) {
        this.nombre = nombre;
        this.correo = correo;
        this.username = username;
        this.password = password;

        // valores por default
        this.victorias = 0;
        this.derrotas = 0;
        this.saldo = 500;
        this.banner = "Icons/default_banner.png"; // Default banner
        this.fotoPerfil = "Icons/user.png"; // Default profile picture
    }

    // Registrar un nuevo usuario
    public static boolean registrarUsuario(Usuario nuevoUsuario) {
        // Verificar si el correo electrónico o el nombre de usuario ya existen
        if (usuariosDB.containsKey(nuevoUsuario.getCorreo()) || usuariosDB.containsKey(nuevoUsuario.getUsername())) {
            return false;
        }
        usuariosDB.put(nuevoUsuario.getCorreo(), nuevoUsuario);
        usuariosDB.put(nuevoUsuario.getUsername(), nuevoUsuario);
        return true;
    }

    // Logica de inicio de sesion
    public static boolean iniciarSesion(String identificador, String password) {
        Usuario u = usuariosDB.get(identificador);
        if (u != null && u.getPassword().equals(password)) {
            usuarioActual = u;
            return true;
        }
        return false;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getVictorias() {
        return victorias;
    }

    public void setVictorias(int victorias) {
        this.victorias = victorias;
    }

    public int getDerrotas() {
        return derrotas;
    }

    public void setDerrotas(int derrotas) {
        this.derrotas = derrotas;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }
}
