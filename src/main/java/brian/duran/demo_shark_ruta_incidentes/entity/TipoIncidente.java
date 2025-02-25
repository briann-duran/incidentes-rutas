package brian.duran.demo_shark_ruta_incidentes.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document("TIPO_INCIDENTES")
public class TipoIncidente implements Serializable {
    @Id
    private String id;
    private String tipoIncidente;
    private int tipoPrioridad;

    public TipoIncidente(String tipoIncidente, int tipoPrioridad) {
        this.tipoIncidente = tipoIncidente;
        this.tipoPrioridad = tipoPrioridad;
    }
}
