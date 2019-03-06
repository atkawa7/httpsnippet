package io.github.atkawa7.httpsnippet.demo.processors;

import io.github.atkawa7.httpsnippet.demo.domain.Speaker;
import io.github.atkawa7.httpsnippet.demo.dto.SpeakerDTO;
import io.github.atkawa7.httpsnippet.demo.dto.converters.DTOConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;

public interface SpeakerProcessor extends DTOConverter<Speaker, SpeakerDTO> {
HttpEntity<Page<SpeakerDTO>> listSpeakers(Pageable pageable);

HttpEntity<SpeakerDTO> createSpeaker(SpeakerDTO dto);

HttpEntity<SpeakerDTO> updateSpeaker(Long id, SpeakerDTO dto);

HttpEntity<SpeakerDTO> retrieveSpeaker(Long id);

HttpEntity<SpeakerDTO> deleteSpeaker(Long id);
}
