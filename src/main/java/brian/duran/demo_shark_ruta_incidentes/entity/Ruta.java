package brian.duran.demo_shark_ruta_incidentes.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Document("RUTAS")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class Ruta implements Serializable {
    @Id
    private String id;
    private String nombre;
    private String origen;
    private String destino;
    private double distancia;

    @DBRef(lazy = true)
    @JsonManagedReference
    private List<Ruta> intersecciones;

    public Ruta(String nombre, String origen, String destino, double distancia) {
        this.nombre = nombre;
        this.origen = origen;
        this.destino = destino;
        this.distancia = distancia;
    }
}
