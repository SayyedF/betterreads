package com.jilani.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.http.HttpClient;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SearchController {

    private final String SEARCH_URL_ROOT = "http://openlibrary.org/search.json";
    private final WebClient webClient;

    public SearchController(WebClient.Builder webClientBuilder) {

        webClient = webClientBuilder.exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> {
                            configurer.defaultCodecs()
                                    .maxInMemorySize(16 * 1024 * 1024);
                        }).build())
                .baseUrl(SEARCH_URL_ROOT).build();
    }

    @GetMapping(value = "/search")
    public String searchBook(@RequestParam String query, Model model){

        Mono<SearchResult> searchResultMono =
                webClient.get()
                        .uri("?q={query}", query)
                        .retrieve()
                        .bodyToMono(SearchResult.class);

        SearchResult searchResult = searchResultMono.block();
        List<SearchResultBook> booksFound =
                searchResult
                        .getDocs()
                        .stream()
                        .limit(10)
                        .map(bookResult -> {
                            bookResult.setKey(bookResult.getKey().replace("/works/", ""));
                            if(!StringUtils.hasText(bookResult.getCover_i())){
                                bookResult.setCover_i("/images/no-image.png");
                            } else {
                                bookResult.setCover_i("https://covers.openlibrary.org/b/id/"+ bookResult.getCover_i()+"-M.jpg");
                            }
                            return bookResult;
                        })
                        .collect(Collectors.toList());
        model.addAttribute("results", booksFound);
        model.addAttribute("numFound", booksFound.size());
        return "search";
    }
}
