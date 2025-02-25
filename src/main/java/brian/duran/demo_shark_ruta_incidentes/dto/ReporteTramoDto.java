package brian.duran.demo_shark_ruta_incidentes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReporteTramoDto implements Serializable {
    private int tramo;
    private String nombreRuta;
    private double gravedadPromedio;
}
