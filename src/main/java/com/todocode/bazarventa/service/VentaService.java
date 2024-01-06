package com.todocode.bazarventa.service;

import com.todocode.bazarventa.excepcion.VentaNotFoundException;
import com.todocode.bazarventa.model.Cliente;
import com.todocode.bazarventa.model.Producto;
import com.todocode.bazarventa.model.Venta;
import com.todocode.bazarventa.repository.IClienteRepository;
import com.todocode.bazarventa.repository.IProductoRepository;
import com.todocode.bazarventa.repository.IVentaRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentaService implements IVentaService {
    
    @Autowired
    private final IVentaRepository ventaRepository;
    @Autowired
    private final IProductoRepository productoRepository;
    @Autowired
    private final IClienteRepository clienteRepository;
    @Autowired
    private final ProductoService productoService;
    @Autowired
    private final ClienteService clienteService;

    private static final Logger LOGGER = LoggerFactory.getLogger(VentaService.class);

    // Inyecta el repositorio de ventas o acceso a la base de datos
    @Autowired
    public VentaService(IVentaRepository ventaRepository, IProductoRepository productoRepository, IClienteRepository clienteRepository, ProductoService productoService, ClienteService clienteService) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.productoService = productoService;
        this.clienteService = clienteService;
    }


    
    @Override
    public Venta findVentaPorCodigo(Long codigo) {
       return ventaRepository.findByCodigo(codigo); 
    }
    
    
//Implementa la logica para eliminar una venta por su codigo

    /**
     * Elimina una venta por su código.
     *
     * @param codigo El código de la venta a eliminar.
     */
    @Override
    public void deleteById(Long codigo) {
        Optional<Venta> ventaOptional = ventaRepository.findById(codigo);

        if (ventaOptional.isPresent()) {
            
            
            
            ventaRepository.deleteById(codigo);
        } else {
            throw new VentaNotFoundException("La venta con el código " + codigo + " no fue encontrada.");
        }

    }

//Implementa la logica para editar (actualizar) una venta en la base de datos    
    @Override
    public void editVenta(Venta venta) {
        ventaRepository.save(venta);// puedo utilizar el metodo save para actualizar una venta existente
    }

// Implementa la logica para obtener ventas por una fecha especifica desde  la base de datos
    @Override
    public List<Venta> obtenerVentaPorFecha(LocalDate fecha) {
        return ventaRepository.findByFecha(fecha);
    }

// Implementa la logica para obtener la venta con el monto mas alto desde la base de datos
    @Override
    public Venta obtenerVentaConMontoMasAlto() {
        return ventaRepository.findFirstByOrderByTotalDesc();
    }

    /**
     *
     * @param codigo
     * @return
     */
    @Override
    @Transactional
    public List<Producto> obtenerProductosDeVenta(Long codigo) {
        Optional<Venta> ventaOptional = ventaRepository.findById(codigo);

        if (ventaOptional.isPresent()) {
            Venta venta = ventaOptional.get();
            return venta.getListaProductos();
        } else {
            throw new VentaNotFoundException("La venta con el código " + codigo + " no fue encontrada.");

        }

    }

    @Override
    public List<Venta> obtenerVentasPorFecha(LocalDate fecha) {
        return ventaRepository.findAllByFecha(fecha);
    }

    @Override
    public Long contarVentasPorFecha(LocalDate fecha) {
        return ventaRepository.countVentasByFecha(fecha);
    }

    @Override
    public Double obtenerSumatoriaMontoPorFecha(LocalDate fecha) {
        return ventaRepository.obtenerSumatoriaMontoPorFecha(fecha);
    }

    @Override
    public Venta crearVentaConProductos(Venta venta) { //Venta

        try {
            // Lista para almacenar los productos persistentes
            List<Producto> productosPersistentes = new ArrayList<>();

            // Iterar sobre los productos de la venta
            for (Producto productoExistente : venta.getListaProductos()) {

                // Verificar si el producto tiene un código, recordar
                // que productoExistente son los que colocamos en postman
                // para buscarlos en el repository
                if (productoExistente.getCodigoProducto() != null) {
                    // Buscar el producto en el repositorio por su código
                    productosPersistentes.add(productoRepository.findById(productoExistente.getCodigoProducto())
                            .orElseThrow(() -> new RuntimeException("Producto no encontrado con código: "
                            + productoExistente.getCodigoProducto())));
                    // Sumar el costo del producto al total de la venta

                }
            }
            
             
            // Establecer la lista actualizada de productos persistentes en la venta
            venta.setListaProductos(productosPersistentes);

           // Calcular el total de la venta basado en los totales de los productos
            Double totalVenta = productoService.calcularTotalProductos(
                    productosPersistentes.stream().map(Producto::getCodigoProducto).collect(Collectors.toList())
            );

            // Establecer el total calculado en la venta
            venta.setTotal(totalVenta);

            // Establecer la lista actualizada de productos persistentes en la venta
            venta.setListaProductos(productosPersistentes);// los que estan en la base de datos
            
             // Obtener el cliente existente por su código
            Long codigoCliente = venta.getUnCliente().getCodigoCliente();
            Cliente clienteExistente = clienteService.findClientePorCodigo(codigoCliente);

            // Asignar el cliente existente a la venta
            venta.setUnCliente(clienteExistente);
            

            // Guardar la venta en el repositorio y obtener la venta persistida
            Venta ventaPersistida = ventaRepository.save(venta);
            // Se llama actualizarStock una vez realizada la venta
            productoService.actualizarStock(ventaPersistida);
            return ventaPersistida;

        } catch (Exception e) {
            // Imprimir la traza completa del error
            e.printStackTrace(); // Esto imprimirá la traza completa del error
            // Loggea la excepción
            LOGGER.error("Error al crear la venta con productos.", e);
            // Relanzar la excepción como RuntimeException
            throw new RuntimeException("Error al crear la venta con productos.", e);

        }

    }

    

   
        
        
        
    
}

