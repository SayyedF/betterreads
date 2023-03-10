package com.jilani.userbooks;

import com.jilani.book.Book;
import com.jilani.book.BookRepository;
import com.jilani.user.BooksByUser;
import com.jilani.user.BooksByUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class UserBooksController {

    @Autowired
    private UserBooksRepository userBooksRepository;

    @Autowired
    private BooksByUserRepository booksByUserRepository;

    @Autowired
    private BookRepository bookRepository;

    @PostMapping("/addUserBook")
    public ModelAndView addBookForUser(@AuthenticationPrincipal OAuth2User principal,
                                       @RequestBody MultiValueMap<String,String> formData){
//        System.out.println(formData);

        if(principal == null || principal.getAttribute("login") == null) {
            return new ModelAndView("redirect:/");
        }

        String userId = principal.getAttribute("login");

        String bookId = formData.getFirst("bookId");
        if(!StringUtils.hasText(bookId)) {
            return new ModelAndView("redirect:/");
        }
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if(optionalBook.isEmpty()) {
            return new ModelAndView("redirect:/");
        }

        Book book = optionalBook.get();

        UserBooks userBooks = new UserBooks();
        UserBooksPrimaryKey key = new UserBooksPrimaryKey();

        key.setBookId(bookId);
        key.setUserId(userId);
        userBooks.setKey(key);

        int rating = Integer.parseInt(formData.getFirst("rating"));

        userBooks.setStartedDate(LocalDate.parse(formData.getFirst("startDate")));
        userBooks.setCompletedDate(LocalDate.parse(formData.getFirst("completedDate")));
        userBooks.setRating(rating);
        userBooks.setReadingStatus(formData.getFirst("readingStatus"));

        userBooksRepository.save(userBooks);
        System.out.println("UserBooks object created and persisted in db.");

        BooksByUser booksByUser = new BooksByUser();
        booksByUser.setId(userId);
        booksByUser.setBookId(bookId);
        booksByUser.setBookName(book.getName());
        booksByUser.setCoverIds(book.getCoverIds());
        booksByUser.setAuthorNames(book.getAuthorNames());
        booksByUser.setReadingStatus(formData.getFirst("readingStatus"));
        booksByUser.setRating(rating);
        booksByUserRepository.save(booksByUser);

        return new ModelAndView("redirect:/books/" + bookId);
    }
}
