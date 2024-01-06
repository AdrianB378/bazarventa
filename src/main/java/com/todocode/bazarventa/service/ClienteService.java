
package com.todocode.bazarventa.service;

import com.todocode.bazarventa.model.Cliente;

import com.todocode.bazarventa.repository.IClienteRepository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService implements IClienteService {
    
    private final IClienteRepository clienteRepository;
    
    @Autowired
    public ClienteService(IClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public void saveCliente(Cliente cliente) {
        clienteRepository.save(cliente);
        
    }

    @Override
    public List<Cliente> getClientes() {
        return clienteRepository.findAll();
    }
     @Override
    public Cliente findClientePorCodigo(Long codigoCliente) {
        return clienteRepository.findById(codigoCliente).orElse(null); 
    }
   

    @Override
    public void deleteCliente(Long codigoCliente) {
        clienteRepository.deleteById(codigoCliente);   

   
    }

    @Override
    public void editCliente(Cliente cliente) {
        clienteRepository.save(cliente);
    }

    
    
}
