package brian.duran.demo_shark_ruta_incidentes.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TipoIncidenteDto implements Serializable {
    private String id;
    private String tipoIncidente;
    private int tipoPrioridad;
}
