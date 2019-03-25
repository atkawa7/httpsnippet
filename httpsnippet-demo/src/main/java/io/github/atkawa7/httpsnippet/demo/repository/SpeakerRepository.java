package io.github.atkawa7.httpsnippet.demo.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import io.github.atkawa7.httpsnippet.demo.domain.Speaker;

public interface SpeakerRepository extends PagingAndSortingRepository<Speaker, Long> {}
