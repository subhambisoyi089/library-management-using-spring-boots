package com.library.service;

import com.library.entity.Author;
import com.library.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;

    public List<Author> findAll() { return authorRepository.findAll(); }

    public Optional<Author> findById(Long id) { return authorRepository.findById(id); }

    public Author save(Author author) { return authorRepository.save(author); }

    public void deleteById(Long id) { authorRepository.deleteById(id); }

    public List<Author> search(String name) {
        return authorRepository.searchByFullName(name);
    }

    public long countTotal() { return authorRepository.count(); }
}
