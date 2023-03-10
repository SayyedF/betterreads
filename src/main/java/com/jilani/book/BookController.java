package com.jilani.book;

import com.jilani.userbooks.UserBooks;
import com.jilani.userbooks.UserBooksPrimaryKey;
import com.jilani.userbooks.UserBooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Controller
public class BookController {

    private final String COVER_IMAGE_ROOT = "https://covers.openlibrary.org/b/id/";

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserBooksRepository userBooksRepository;

    @GetMapping(value = "/books/{bookId}")
    public String getBook(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable String bookId, Model model) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if(optionalBook.isPresent()) {
            Book book = optionalBook.get();
            String coverImageUrl = "/images/no-image.png";
            if(book.getCoverIds() != null && book.getCoverIds().size() > 0) {
                coverImageUrl = COVER_IMAGE_ROOT + book.getCoverIds().get(0) + "-L.jpg";
            }
            if(principal != null && principal.getAttribute("login") != null) {
                Optional<UserBooks> optionalUserBooks = userBooksRepository
                        .findByKey_UserIdAndKey_BookId(principal.getAttribute("login"),bookId);
                if(optionalUserBooks.isPresent()){
                    model.addAttribute("userBooks", optionalUserBooks.get());
                } else
                    model.addAttribute("userBooks", new UserBooks());
                model.addAttribute("loginId", principal.getAttribute("login"));
            }

            model.addAttribute("coverImage", coverImageUrl);
            model.addAttribute("book", book);

            return "book";
        }
        return "book-not-found";
    }
}
