package br.com.postech.software.architecture.techchallenge.service.impl;

import br.com.postech.software.architecture.techchallenge.configuration.ModelMapperConfiguration;
import br.com.postech.software.architecture.techchallenge.dto.ClienteDTO;
import br.com.postech.software.architecture.techchallenge.exception.BusinessException;
import br.com.postech.software.architecture.techchallenge.model.Cliente;
import br.com.postech.software.architecture.techchallenge.repository.jpa.ClienteJpaRepository;
import br.com.postech.software.architecture.techchallenge.service.ClientService;
import br.com.postech.software.architecture.techchallenge.util.CpfCnpjUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClientService {

    private final ClienteJpaRepository clienteJpaRepository;
    private static final ModelMapper MAPPER = ModelMapperConfiguration.getModelMapper();

    @Override
    public List<ClienteDTO> listarClientesAtivos() {
        List<Cliente> clientesAtivos = clienteJpaRepository.findByStatus(Boolean.TRUE);
        return clientesAtivos.stream()
                .map(cliente -> MAPPER.map(cliente, ClienteDTO.class))
                .toList();
    }

    @Override
    public ClienteDTO findById(Integer id) {
        Optional<Cliente> cliente = clienteJpaRepository.findByIdAndStatus(id, Boolean.TRUE);
        if (cliente.isPresent()) {
            return MAPPER.map(cliente.get(), ClienteDTO.class);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado");
    }

    @Override
    public ClienteDTO save(ClienteDTO clienteDTO) {
        var cliente = MAPPER.map(clienteDTO, Cliente.class);

        if (Objects.nonNull(cliente.getCpf())) {
            cliente.setCpf(CpfCnpjUtil.removeMaskCPFCNPJ(cliente.getCpf()));
        }

        cliente = clienteJpaRepository.save(cliente);

        return MAPPER.map(cliente, ClienteDTO.class);
    }

    @Override
    public ClienteDTO atualizarCliente(Integer id, ClienteDTO clienteDTO) {
        Cliente clienteMappado = clienteJpaRepository.findById(id)
                .map(cliente -> {
                            cliente.setNome(clienteDTO.getNome());
                            cliente.setEmail(clienteDTO.getEmail());
                            cliente.setCpf(clienteDTO.getCpf());
                            return clienteJpaRepository.save(cliente);
                        }
                ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        return MAPPER.map(clienteMappado, ClienteDTO.class);
    }

    @Override
    public ClienteDTO desativarCliente(Integer id) {
        Optional<Cliente> clienteOptional = clienteJpaRepository.findById(id);

        if (!clienteOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado");
        }

        Cliente cliente = clienteOptional.get();
        cliente.setStatus(Boolean.FALSE);
        cliente = clienteJpaRepository.save(cliente);

        return MAPPER.map(cliente, ClienteDTO.class);
    }

    @Override
    public Cliente findByCpfOrNomeOrEmail(String cpf, String nome, String email) throws BusinessException {
        return clienteJpaRepository.findByCpfOrNomeOrEmail(cpf, nome, email);
    }
}
