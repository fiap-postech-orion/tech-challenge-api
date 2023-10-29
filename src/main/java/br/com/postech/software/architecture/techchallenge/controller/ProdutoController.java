package br.com.postech.software.architecture.techchallenge.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;

import br.com.postech.software.architecture.techchallenge.dto.ProdutoDTO;
import br.com.postech.software.architecture.techchallenge.enums.CategoriaEnum;
import br.com.postech.software.architecture.techchallenge.service.IProdutoService;

@RestController
@RequestMapping("/v1/produtos")
public class ProdutoController {

	@Autowired
	private IProdutoService produtoService;

    @PostMapping
    public ResponseEntity<ProdutoDTO> salvar(
            @RequestBody @Valid ProdutoDTO produtoDTO,
            UriComponentsBuilder uriBuilder
    ) {
        final var produto = produtoService.save(produtoDTO);
        final var uri = uriBuilder.path("/v1/produtos/{id}").buildAndExpand(produto.getId()).toUri();
        return ResponseEntity.created(uri).body(produto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(
            @PathVariable Integer id
    ) {
        this.produtoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<ProdutoDTO> autualizar(
            @RequestBody @Valid ProdutoDTO produtoDTO
    ) {
        return ResponseEntity.ok(produtoService.save(produtoDTO));
    }


    @GetMapping
	public ResponseEntity<List<ProdutoDTO>> listarProdutos(@RequestParam(required = false) CategoriaEnum categoria) {
		return new ResponseEntity<>(produtoService.findAll(categoria), HttpStatus.OK);
	}

    @GetMapping(path = "/{id}")
    public ResponseEntity<ProdutoDTO> buscarProdutoPorId(@PathVariable Integer id) {
        return new ResponseEntity<>(produtoService.findById(id), HttpStatus.OK);
    }
}
