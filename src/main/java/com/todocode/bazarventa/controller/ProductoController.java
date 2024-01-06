package com.todocode.bazarventa.controller;


import com.todocode.bazarventa.dto.ProductoDTO;
import com.todocode.bazarventa.model.Producto;
import com.todocode.bazarventa.repository.IClienteRepository;
import com.todocode.bazarventa.repository.IProductoRepository;
import com.todocode.bazarventa.repository.IVentaRepository;
import com.todocode.bazarventa.service.IProductoService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private IProductoService productoService;
    private IProductoRepository productoRepository;
    private IClienteRepository clienteRepository;
    private IVentaRepository ventaRepository;

    @Autowired
    public ProductoController(IProductoService productoService, IProductoRepository productoRepository, IClienteRepository clienteRepository, IVentaRepository ventaRepository) {
        this.productoService = productoService;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.ventaRepository = ventaRepository;
    }

    // Metodo para crear un producto
    @PostMapping("/crear")
    public void createProducto(@RequestBody Producto producto) {
        productoService.saveProducto(producto);
    }

    // Metodo para obtener la lista completa de productos
    @GetMapping
    @ResponseBody
    public List<ProductoDTO> getAllProductosDTO() {
        List<Producto> productos = productoRepository.findAll();

        // Mapear la lista de productos a una lista de ProductoDTO
        List<ProductoDTO> productoDTOs = productos.stream()
                .map(producto -> new ProductoDTO(
                producto.getCodigoProducto(),
                producto.getNombre(),
                producto.getMarca(),
                producto.getCosto(),
                producto.getCantidadDisponible()
        ))
                .collect(Collectors.toList());

        return productoDTOs;
    }

    //Metodo para traer un producto en particular por su codigo
    @GetMapping("/{codigoProducto}")
    public ProductoDTO findByCodigoProducto(@PathVariable Long codigoProducto) {
        // Obtener el cliente de la base de datos usando el servicio o el repositorio
        Producto product = productoRepository.findByCodigoProducto(codigoProducto);

        // Verificar si se encontró el cliente
        if (product != null) {
            // Mapear los datos del cliente a ClienteDTO
            ProductoDTO productDTO = new ProductoDTO(
                    product.getCodigoProducto(),
                    product.getNombre(),
                    product.getMarca(),
                    product.getCosto(),
                    product.getCantidadDisponible()
            );

            return productDTO;
        } else {
            // Manejar el caso en el que no se encontró el cliente
            // Puedes devolver un DTO vacío o lanzar una excepción según tus necesidades.
            return new ProductoDTO();
        }

    }




    // Metodo para editar un producto por su codigo
    @PutMapping("/editar/{codigoProducto}")
    public void editProducto(@PathVariable Long codigoProducto, @RequestBody Producto producto) {
        Producto existingProducto = productoService.findCodigoProducto(codigoProducto);
        if (existingProducto != null) {

            // Actualiza los campos del producto existente
            existingProducto.setNombre(producto.getNombre());
            existingProducto.setMarca(producto.getMarca());
            existingProducto.setCosto(producto.getCosto());
            existingProducto.setCantidadDisponible(producto.getCantidadDisponible());
            // Luego, guardas el producto actualizado
            productoService.editProducto(existingProducto);

        }

    }

    @GetMapping("/falta-stock")
    public List<Producto> obtenerProductosFaltaStock(@RequestParam(value = "umbralStockMinimo", required = false) Long umbralStockMinimo) {
        if (umbralStockMinimo != null) {
            return productoService.obtenerProductosFaltaStock(umbralStockMinimo);
        } else {
            // Maneja el caso en el que el parámetro no se proporciona o es nulo.
            // Puedes devolver un mensaje de error o una lista vacía, según tus necesidades.
            return new ArrayList<>();
            //localhost:8080/productos/falta-stock?umbralStockMinimo=5
        }   // esta seria la url en postman

    }

    @PutMapping("/incrementar-stock/{codigoProducto}")
    public ResponseEntity<String> incrementarStock(@PathVariable Long codigoProducto, @RequestParam int cantidad) {
        try {
            productoService.incrementarStock(codigoProducto, cantidad);
            return ResponseEntity.ok("Stock incrementado exitosamente.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al incrementar el stock.");
        }
    }

    @DeleteMapping("/eliminar/{codigoProducto}")
    public ResponseEntity<String> deleteProducto(@PathVariable Long codigoProducto) {
        try {
            productoService.deleteProducto(codigoProducto);
            return ResponseEntity.ok("Producto y ventas asociadas eliminados correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}




    
    

