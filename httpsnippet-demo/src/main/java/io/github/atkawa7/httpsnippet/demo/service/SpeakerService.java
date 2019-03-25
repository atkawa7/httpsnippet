package io.github.atkawa7.httpsnippet.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.atkawa7.httpsnippet.demo.domain.Speaker;

public interface SpeakerService {
  Page<Speaker> listSpeakers(Pageable pageable);

  Speaker createSpeaker(Speaker dto);

  Speaker updateSpeaker(Long id, Speaker dto);

  Speaker retrieveSpeaker(Long id);

  Speaker deleteSpeaker(Long id);
}
