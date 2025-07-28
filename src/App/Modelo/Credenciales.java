package App.Modelo;

public class Credenciales {

    private int id;
    private String user;
    private String password;
    private String rol;

    public Credenciales(int id, String user, String password, String rol) {
        this.id = id;
        this.user = user;
        this.password = password;
        this.rol = rol;
    }

    public Credenciales(String user, String password, String rol) {
        this.user = user;
        this.password = password;
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "Credenciales{" +
                "id=" + id +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getRol() {
        return rol;
    }
}
