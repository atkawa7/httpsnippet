package io.github.atkawa7.httpsnippet.demo.repository;

import io.github.atkawa7.httpsnippet.demo.domain.Speaker;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SpeakerRepository extends PagingAndSortingRepository<Speaker, Long> {}
