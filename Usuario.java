
// aqui se guardan los datos del usuario
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    // base de datos simulada en memoria
    private static HashMap<String, Usuario> usuariosDB = new HashMap<>();
    private static Usuario usuarioActual = null;

    static {
        // Default mock users for ranking and testing
        registrarUsuarioConSaldo("Juan", "juan@unab.cl", "juan", "123", 20000);
        registrarUsuarioConSaldo("Kevin", "kevin@unab.cl", "kevin", "123", 18000);
        registrarUsuarioConSaldo("Julian", "julian@unab.cl", "julian", "123", 17000);
        registrarUsuarioConSaldo("Alejandro", "alejandro@unab.cl", "alejandro", "123", 15000);
        registrarUsuarioConSaldo("Maria", "maria@unab.cl", "maria", "123", 14000);
        registrarUsuarioConSaldo("Camila", "camila@unab.cl", "camila", "123", 13000);
        registrarUsuarioConSaldo("Santiago", "santiago@unab.cl", "santiago", "123", 12000);
        registrarUsuarioConSaldo("Laura", "laura@unab.cl", "laura", "123", 11000);
        registrarUsuarioConSaldo("Andres", "andres@unab.cl", "andres", "123", 10000);
        registrarUsuarioConSaldo("Daniel", "daniel@unab.cl", "daniel", "123", 9000);
        registrarUsuarioConSaldo("Sebastian", "sebastian@unab.cl", "sebastian", "123", 8500);
        registrarUsuarioConSaldo("Valentina", "valentina@unab.cl", "valentina", "123", 8000);
        
        // General test user
        registrarUsuarioConSaldo("Usuario de Prueba", "prueba@unab.cl", "prueba", "123", 1200);
    }

    public Usuario(String nombre, String correo, String username, String password) {
        this.nombre = nombre;
        this.correo = correo;
        this.username = username;
        this.password = password;

        // valores por default
        this.victorias = 0;
        this.derrotas = 0;
        this.saldo = 1200; // saldo inicial premium de 1200 UP
        this.banner = "Icons/default_banner.png"; // Default banner
        this.fotoPerfil = "Icons/user.png"; // Default profile picture
    }

    private static void registrarUsuarioConSaldo(String nombre, String correo, String username, String password, double saldoInicial) {
        Usuario u = new Usuario(nombre, correo, username, password);
        u.saldo = saldoInicial;
        usuariosDB.put(correo, u);
        usuariosDB.put(username, u);
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

    public static List<Usuario> getRankingGlobal() {
        Set<Usuario> uniqueUsers = new HashSet<>(usuariosDB.values());
        List<Usuario> sortedList = new ArrayList<>(uniqueUsers);
        sortedList.sort((u1, u2) -> Double.compare(u2.getSaldo(), u1.getSaldo()));
        return sortedList;
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

