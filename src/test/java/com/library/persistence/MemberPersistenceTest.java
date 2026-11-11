package com.library.persistence;

import com.library.domain.entity.Member;
import com.library.infrastructure.persistence.HibernateMemberRepository;
import com.library.repository.MemberRepository;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class MemberPersistenceTest {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("library-persistence-unit");

        try {
            MemberRepository memberRepository = new HibernateMemberRepository(emf);

            Member member = new Member("Manuel Del Pozo", "madepozo@gmail.com");
            Member savedMember = memberRepository.save(member);

            System.out.println("ID: " + savedMember.getId());

            memberRepository.findByEmail("madepozo@gmail.com")
                    .ifPresent(m -> System.out.println("Found: " + m.getName()));
        } finally {
            emf.close();
        }
    }
}
