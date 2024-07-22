package com.generation.pgfarmacia.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.pgfarmacia.model.Produto;
import com.generation.pgfarmacia.repository.CategoriaRepository;
import com.generation.pgfarmacia.repository.ProdutoRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/produtos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProdutoController {

	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@GetMapping
	public ResponseEntity<List<Produto>> getAll(){
		return ResponseEntity.ok(produtoRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Produto> getById(@PathVariable long id){
		return produtoRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
	
	@GetMapping("/nome/{nome}")
	public ResponseEntity<List<Produto>> getByNome(@PathVariable String nome){
		return ResponseEntity
				.ok(produtoRepository.findAllByNomeProdutoContainingIgnoreCase(nome));
	}
	
	@PostMapping
	public ResponseEntity<Produto> create(@Valid @RequestBody Produto produto){
		if (categoriaRepository.findById(produto.getCategoria().getId()) != null) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(produtoRepository.save(produto));
			} 
			
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O tema não existe!");
	}
	
	
	@PutMapping
	public ResponseEntity<Produto> alter(@Valid @RequestBody Produto produto) {
		if (produtoRepository.existsById(produto.getId())) {
			if (categoriaRepository.existsById(produto.getCategoria().getId())) {
				return ResponseEntity.ok(produtoRepository.save(produto));
			}
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A categoria ou o produto não existem!");
		}
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
	
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void deletar(@PathVariable Long id) {
		Optional<Produto> produto = produtoRepository.findById(id);
		
		if (produto.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não existe!");
		}
		
		produtoRepository.deleteById(id);
	}
}
