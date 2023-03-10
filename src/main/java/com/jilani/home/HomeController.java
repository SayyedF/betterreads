package com.jilani.home;

import com.jilani.user.BooksByUser;
import com.jilani.user.BooksByUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final String COVER_IMAGE_ROOT = "https://covers.openlibrary.org/b/id/";
    private final String COVER_NO_IMAGE = "/images/no-image.png";

    @Autowired
    private BooksByUserRepository booksByUserRepository;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User principal,
                       Model model){
        if(principal == null || principal.getAttribute("login") == null) {
            return "index";
        }
        String userId = principal.getAttribute("login");
        Slice<BooksByUser> booksSlice =
                booksByUserRepository.findAllById(userId, CassandraPageRequest.of(0,50));
        List<BooksByUser> booksByUser = booksSlice.getContent()
                        .stream()
                        .distinct()
                        .map(book -> {
                            String coverImageUrl = COVER_NO_IMAGE;
                            if(book.getCoverIds() != null && book.getCoverIds().size() > 0) {
                                coverImageUrl = COVER_IMAGE_ROOT + book.getCoverIds().get(0) + "-M.jpg";
                            }
                            book.setCoverUrl(coverImageUrl);
                            return book;})
                        .collect(Collectors.toList());
        model.addAttribute("books",booksByUser);
        return "home";
    }
}
