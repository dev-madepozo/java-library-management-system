package com.library.service;

import java.util.List;

import com.library.domain.entity.Member;
import com.library.domain.exception.MemberNotFoundException;
import com.library.repository.MemberRepository;

public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member createMember(String name, String email) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Member name is required.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Member email is required.");
        }

        if (memberRepository.findByEmail(email.trim()).isPresent()) {
            throw new IllegalArgumentException("Member with email " + email + " already exists.");
        }

        Member member = new Member(name.trim(), email.trim());

        return memberRepository.save(member);
    }

    public Member getMemberById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Member ID is invalid.");
        }

        return memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException("Member not found."));
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
}
