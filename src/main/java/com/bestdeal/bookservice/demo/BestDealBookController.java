package com.bestdeal.bookservice.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/virtualstore")
public class BestDealBookController {

    public static final ScopedValue<RestCallStatistics> SCOPED_VALUE = ScopedValue.newInstance();
    @Autowired
    private BookRetrievalService retrievalService;


    @GetMapping("/book")
    public BestDealResult getBestPriceForBook(@RequestParam String name) {

        long start = System.currentTimeMillis();
        RestCallStatistics timeObj = new RestCallStatistics();

        try {
            List<Book> books = ScopedValue.callWhere
                    (SCOPED_VALUE, timeObj, () -> retrievalService.getBookFromAllStores(name));

            Book bestPriceBook = books.stream()
                    .min(Comparator.comparing(Book::cost))
                    .orElseThrow();

            return new BestDealResult(timeObj, bestPriceBook, books);
        } catch (Exception e) {
            throw new RuntimeException("Exception while calling getBestPrice", e);
        } finally {
            long end = System.currentTimeMillis();
            //adding directly to timeobj not using scoped value
            timeObj.addTiming("Best deal Store", end - start);
        }
    }
}
