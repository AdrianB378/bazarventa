
package com.todocode.bazarventa.repository;

import com.todocode.bazarventa.model.Cliente;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     *
     * @param codigoCliente
     * @return
     */
    public Cliente findByCodigoCliente(Long codigoCliente);


    
    
}

