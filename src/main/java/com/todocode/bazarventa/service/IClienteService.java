
package com.todocode.bazarventa.service;

import com.todocode.bazarventa.model.Cliente;
import java.util.List;

public interface IClienteService {
    
    public void saveCliente(Cliente cliente);
    
    public List<Cliente> getClientes();
    
    public Cliente findClientePorCodigo(Long codigoCliente);
    
    public void deleteCliente(Long codigoCliente);
    
    public void editCliente(Cliente cliente);
    
    
}
