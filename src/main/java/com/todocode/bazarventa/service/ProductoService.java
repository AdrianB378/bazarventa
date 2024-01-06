package com.todocode.bazarventa.service;


import com.todocode.bazarventa.model.Producto;
import com.todocode.bazarventa.model.Venta;
import com.todocode.bazarventa.repository.IClienteRepository;
import com.todocode.bazarventa.repository.IProductoRepository;
import com.todocode.bazarventa.repository.IVentaRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductoService implements IProductoService {

    @Autowired
    private IProductoRepository productoRepository;

    @Autowired
    private IVentaRepository ventaRepository;

    @Autowired
    private IClienteRepository clienteRepository;


   

    @Override
    public void saveProducto(Producto producto) {
        productoRepository.save(producto);
    }

    
    
    @Override
    public Producto findCodigoProducto(Long codigoProducto) {
    return productoRepository.findByCodigoProducto(codigoProducto);

    }

    @Override
    public void editProducto(Producto producto) {
    productoRepository.save(producto);
    }

    @Override
    public List<Producto> obtenerProductosFaltaStock (Long umbralStockMinimo) {
    // Usa umbralStockMinimo en tu lógica para filtrar productos con stock insuficiente.
    return productoRepository.findByCantidadDisponibleLessThan(umbralStockMinimo);
    }

    @Override
    @Transactional
    public void actualizarStock  (Venta venta) {

    for (Producto producto : venta.getListaProductos()) {
            Long codigoProducto = producto.getCodigoProducto();
            productoRepository.decrementarCantidadDisponible(codigoProducto);
            // Validación para asegurarse de que la cantidad disponible no sea negativa
            if (productoRepository.findByCodigoProducto(codigoProducto).getCantidadDisponible() < 0) {
                throw new RuntimeException("Error: La cantidad disponible de un producto no puede ser negativa.");

            }
        }

    }
    @Override
    @Transactional
    public Double calcularTotalProductos(List<Long> codigosProductos) {

    // Obtener la lista de productos por los códigos proporcionados
    List<Producto> productos = productoRepository.findAllByCodigoProductoIn(codigosProductos);

        // Calcular el total sumando los costos de los productos
        Double total = productos.stream()
                .mapToDouble(Producto::getCosto)
                .sum();

        return total;
    }

    @Override
    @Transactional
    public void incrementarStock (Long codigoProducto, int cantidad) {

        Producto producto = productoRepository.findById(codigoProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con código: " + codigoProducto));

        // Obtener la cantidad disponible actual
        int cantidadDisponibleActual = producto.getCantidadDisponible();

        // Sumar la cantidad que deseas agregar
        int nuevaCantidadDisponible = cantidadDisponibleActual + cantidad;

        // Actualizar la cantidad disponible en el producto
        producto.setCantidadDisponible(nuevaCantidadDisponible);

        // Guardar el producto actualizado en la base de datos
        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void deleteProducto(Long codigoProducto) {
        Producto producto = productoRepository.findById(codigoProducto).orElse(null);
        if (producto != null) {
            eliminarVentasRelacionadas(producto);
            productoRepository.delete(producto);
        }
    }

    private void eliminarVentasRelacionadas(Producto producto) {
        // Obtener la lista de ventas asociadas al producto
        List<Venta> ventas = ventaRepository.findAllByListaProductos_CodigoProducto(producto.getCodigoProducto());
        
        // Eliminar cada venta
        for (Venta venta : ventas) {
            ventaRepository.deleteById(venta.getCodigo());
        }
    }

  
   
    
}   

   

   

        
    

    
   
   


