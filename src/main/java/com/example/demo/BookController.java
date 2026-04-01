package com.example.demo;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    private List<Book> books = new ArrayList<>();

    @GetMapping
    public List<Book> getAllBooks(){
        return books;
    }

    @PostMapping
    public Book addBook(@RequestBody Book book){
        books.add(book);
        return book;
    }
}
