package brian.duran.demo_shark_ruta_incidentes.dto;

import brian.duran.demo_shark_ruta_incidentes.entity.TipoIncidente;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IncidenteDto implements Serializable {
    private String id;
    private RutaDto ruta;
    private TipoIncidente tipoIncidente;
    private double kilometro;
    private LocalDateTime timestamp;
    private String comentarios;
}
