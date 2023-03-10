package com.jilani.userbooks;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBooksRepository extends CassandraRepository<UserBooks, UserBooksPrimaryKey> {
    public Optional<UserBooks> findByKey_UserIdAndKey_BookId(String userId, String bookId);

//    Slice<UserBooks> findAllByKey_UserId(String userId, Pageable pageable);
//
//    Slice<UserBooks> findAllById(String userId, Pageable pageable);
}
