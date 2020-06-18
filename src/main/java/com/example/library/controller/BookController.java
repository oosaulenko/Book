package com.example.library.controller;

import com.example.library.domain.Book;
import com.example.library.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    private static final String BOOKS_PAGE = "books";
    private static final String NEW_BOOK_PAGE = "new_book";
    private static final String REDIRECT_TO_BOOKS_PAGE = "redirect:/books";

    private final BookService service;

    public BookController(BookService bookService) {
        this.service = bookService;

        setupTestData();
    }

    private void setupTestData() {
        for (long i = 0; i < 10; i++) {
            service.save(new Book(i, "title" + i, "author" + i, (int)i, "discipline" + i));
        }
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String showBooks(Model model, @RequestParam(required = false) String title) {
        List<Book> listBook;

        if (title != null) {
            listBook = service.findByBookTitle(title);
        } else {
            listBook = service.listAll();
        }
        model.addAttribute("listBook", listBook);

        return BOOKS_PAGE;
    }

    @RequestMapping("/new")
    public String showNewBookPage(Model model) {
        Book book = new Book();
        model.addAttribute("book", book);

        return NEW_BOOK_PAGE;

    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveBook(@ModelAttribute("book") Book book) {
        service.save(book);
        return REDIRECT_TO_BOOKS_PAGE;
    }

    @RequestMapping("/edit/{id}")
    public ModelAndView showEditBookPage(@PathVariable(name = "id") Long id) {
        ModelAndView modelAndView = new ModelAndView("edit_book");

        try {
            Book book = service.get(id);
            modelAndView.addObject("book", book);
        } catch (EntityNotFoundException e) {
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            modelAndView.setViewName("404");
        }

        return modelAndView;

    }

    @RequestMapping("/delete/{id}")
    public String deleteBook(@PathVariable(name = "id") Long id) {
        service.delete(id);

        return REDIRECT_TO_BOOKS_PAGE;
    }

}
