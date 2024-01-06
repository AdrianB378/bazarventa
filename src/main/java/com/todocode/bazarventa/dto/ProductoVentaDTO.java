
package com.todocode.bazarventa.dto;

import com.todocode.bazarventa.model.Producto;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductoVentaDTO {
    
    private Long codigoVenta;
    private List<Producto> listaProductos;

    public ProductoVentaDTO() {
    }

    public ProductoVentaDTO(Long codigoVenta, List<Producto> listaProductos) {
        this.codigoVenta = codigoVenta;
        this.listaProductos = listaProductos;
    }
    
    
}
