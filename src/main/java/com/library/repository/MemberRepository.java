package com.library.repository;

import java.util.List;
import java.util.Optional;

import com.library.domain.entity.Member;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findById(Long id);

    Optional<Member> findByEmail(String email);

    List<Member> findAll();
}
