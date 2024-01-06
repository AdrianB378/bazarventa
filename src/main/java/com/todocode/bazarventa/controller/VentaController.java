package com.todocode.bazarventa.controller;


import com.todocode.bazarventa.dto.ProductoVentaDTO;
import com.todocode.bazarventa.dto.VentaDTO;
import com.todocode.bazarventa.dto.VentaProductoClienteDTO;
import com.todocode.bazarventa.dto.VentaResponseDTO;
import com.todocode.bazarventa.excepcion.VentaNotFoundException;
import com.todocode.bazarventa.model.Producto;
import com.todocode.bazarventa.model.Venta;
import com.todocode.bazarventa.repository.IVentaRepository;
import com.todocode.bazarventa.service.IClienteService;
import com.todocode.bazarventa.service.IProductoService;
import com.todocode.bazarventa.service.IVentaService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/ventas")
public class VentaController {

    private final IProductoService productoService;
    private final IVentaService ventaService;
    private final IClienteService clienteService;
    private final IVentaRepository ventaRepository;
   
    private static final Logger LOGGER = LoggerFactory.getLogger(VentaController.class);
   
    public VentaController(IVentaService ventaService, IProductoService productoService, IClienteService clienteService, IVentaRepository ventaRepository) {
        this.ventaService = ventaService;
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.ventaRepository = ventaRepository;
    }

  

    // Obtener todas las ventas
    @GetMapping
    public List<VentaDTO> getAllVentasDTO() {
        List<Venta> ventas = ventaRepository.findAll();

        // Mapear la lista de productos a una lista de ProductoDTO
        List<VentaDTO> ventaDTOs = ventas.stream()
                .map(venta -> new VentaDTO(
                        venta.getCodigo(),
                        venta.getFecha(),
                        venta.getTotal()
                       
                ))
                .collect(Collectors.toList());

        return ventaDTOs;
        
    }

    // Metodo para obtener una venta por su codigo
    @GetMapping("/{codigo}")
    @ResponseBody
        public VentaDTO findByCodigo(@PathVariable Long codigo) {
        
            // Obtener la venta de la base de datos usando el servicio o el repositorio
            Venta vent = ventaRepository.findByCodigo(codigo);

            // Verificar si se encontró el cliente
            if (vent != null) {
                // Mapear los datos del cliente a ClienteDTO
                VentaDTO ventDTO = new VentaDTO(
                        vent.getCodigo(),
                        vent.getFecha(),
                        vent.getTotal()
                        
                );

                return ventDTO;
            } else {
                // Manejar el caso en el que no se encontró la venta
                // Puedes devolver un DTO vacío o lanzar una excepción según tus necesidades.
                return new VentaDTO();
            }


        }

    // Metodo para eliminar una venta por su codigo
    @DeleteMapping("/eliminar/{codigo}")
    public ResponseEntity<String> deleteVenta(@PathVariable Long codigo) {
        try {
            ventaService.deleteById(codigo);
            return new ResponseEntity<>("Venta eliminada correctamente.", HttpStatus.OK);
        } catch (VentaNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


    // Metodo para editar una venta
    @PutMapping("/{codigo}")
    public ResponseEntity<String> editVenta(@PathVariable Long codigo, @RequestBody Venta venta) {
        try {
            // Busca la venta existente por su código
            Venta ventaExistente = ventaService.findVentaPorCodigo(codigo);

            if (ventaExistente == null) {
                // Si no se encuentra la venta, devuelve una respuesta de error
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Venta no encontrada");
            }

            // Actualiza los campos de la venta existente con los datos proporcionados
            ventaExistente.setFecha(venta.getFecha());
            ventaExistente.setTotal(venta.getTotal());
            ventaExistente.setListaProductos(venta.getListaProductos());
            ventaExistente.setUnCliente(venta.getUnCliente());

            // Guarda la venta actualizada
            ventaService.editVenta(ventaExistente);

            // Devuelve una respuesta exitosa
            return ResponseEntity.ok("Venta actualizada correctamente");
        } catch (Exception e) {
            // Maneja cualquier excepción que pueda ocurrir durante la edición
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al editar la venta");
        }
    }
    
           
    
    // Metodo  para obtener ventas con el monto mas alto(Punto 4)
    @GetMapping("/obtenerVentaConMontoMasAlto")
    public Venta obtenerVentaConMontoMasAlto() {
        return ventaService.obtenerVentaConMontoMasAlto();
    }
    
    // Obtener productos de una venta (Punto 5)
    @GetMapping("/productos/{codigo}")
    public ResponseEntity<ProductoVentaDTO> obtenerProductosDeVenta(@PathVariable Long codigo) {
        List<Producto> productos = ventaService.obtenerProductosDeVenta(codigo);

        if (productos != null && !productos.isEmpty()) {
    // Actualiza la venta para asegurarte de que los productos tengan la venta asociada
            Venta venta = ventaService.findVentaPorCodigo(codigo);
            venta.setListaProductos(productos); // Asigna la lista de productos a la venta

            ProductoVentaDTO productoVentaDTO = new ProductoVentaDTO();
            productoVentaDTO.setCodigoVenta(codigo);
            productoVentaDTO.setListaProductos(new ArrayList<>(productos));// Asigno la lista de productos al DTO

            return new ResponseEntity<>(productoVentaDTO, HttpStatus.OK);
        } else {
    // Maneja el caso cuando no hay productos
            ProductoVentaDTO productoVentaDTO = new ProductoVentaDTO();
            productoVentaDTO.setCodigoVenta(codigo);
            productoVentaDTO.setListaProductos(new ArrayList<>()); // Lista vacía

            return new ResponseEntity<>(productoVentaDTO, HttpStatus.NOT_FOUND);



        }
    }   
    // 6- Obtener la sumatoria del monto y tambien cantidad total de ventas de un determinado
        
    // Obtener ventas y cantidad por fecha
    @GetMapping("/por-fecha/{fecha}")
    public ResponseEntity<Object> obtenerVentasYCantidadPorFecha(@PathVariable("fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
        Double totalVentas = ventaService.obtenerSumatoriaMontoPorFecha(fecha);
        Long cantidadVentas = ventaService.contarVentasPorFecha(fecha);

        

        Map<String, Object> result = new HashMap<>();
        result.put("totalVentas", totalVentas);
        result.put("cantidadVentas", cantidadVentas);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @GetMapping("/mayor_venta")
    public ResponseEntity<VentaProductoClienteDTO> obtenerInformacionVentaConMontoMasAlto() {
        // Llama al servicio para obtener la venta con el monto más alto
        Venta ventaConMontoMasAlto = ventaService.obtenerVentaConMontoMasAlto();

        if (ventaConMontoMasAlto != null) {
            // Mapea los datos al DTO
            Long codigo = ventaConMontoMasAlto.getCodigo();
            Double total = ventaConMontoMasAlto.getTotal();
            int cantidadDisponible = ventaConMontoMasAlto.getListaProductos().size();
            String nombre = ventaConMontoMasAlto.getUnCliente().getNombre();
            String apellido = ventaConMontoMasAlto.getUnCliente().getApellido();

            VentaProductoClienteDTO ventaDTO = new VentaProductoClienteDTO(codigo, total, cantidadDisponible, nombre, apellido);
            return new ResponseEntity<>(ventaDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<VentaResponseDTO> crearVentaConProductos(@RequestBody Venta venta) {
        try {
            
            // LLamo al servicio para obtener la venta con productos en Venta Service
            Venta ventaConProductos = ventaService.crearVentaConProductos(venta);
            
            if (ventaConProductos != null) {
                // mapeo los datos al DTO
               
                Long codigo = ventaConProductos.getCodigo();
                LocalDate fecha = ventaConProductos.getFecha();
                Double total = ventaConProductos.getTotal();
                List<Producto> listaProductos = ventaConProductos.getListaProductos();
                String nombre = ventaConProductos.getUnCliente().getNombre();
                String apellido = ventaConProductos.getUnCliente().getApellido();//Cliente unCliente = venta.getUnCliente(); // Aca hice un cambio volver por cualquier cosa(venta) 
                String dni = ventaConProductos.getUnCliente().getDni();
                
                VentaResponseDTO ventaRespDTO = new VentaResponseDTO( codigo, fecha, total, listaProductos, nombre, apellido, dni);
                
                // Modifica aquí para devolver solo la última venta creada
                return ResponseEntity.ok(ventaRespDTO);
                
            } else {
            // Si la venta con productos es null, devuelve un error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
             
            
            }
            
            
            
        } catch (Exception e) {
            e.printStackTrace(); // Puedes imprimir o manejar el error según tu necesidad
            
             // En caso de error, devuelve un ResponseEntity con el mensaje de error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new VentaResponseDTO());
        }
         
    }
    
    
    
}
    
    
    

    

        
        







