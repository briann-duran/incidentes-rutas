package brian.duran.demo_shark_ruta_incidentes.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document("INCIDENTES")
public class Incidente implements Serializable {
    @Id
    private String id;
    @DBRef
    @JsonBackReference
    private Ruta ruta;
    @DBRef
    private TipoIncidente tipoIncidente;
    private double kilometro;
    private LocalDateTime timestamp;
    private String comentarios;

    public Incidente(Ruta ruta, TipoIncidente tipoIncidente, double kilometro, LocalDateTime timestamp, String comentarios) {
        this.ruta = ruta;
        this.tipoIncidente = tipoIncidente;
        this.kilometro = kilometro;
        this.timestamp = timestamp;
        this.comentarios = comentarios;
    }
}
