package brian.duran.demo_shark_ruta_incidentes.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RutaDto implements Serializable {
    private String id;
    private String nombre;
    private String origen;
    private String destino;
    private double distancia;
    private List<RutaDto> intersecciones;
}
