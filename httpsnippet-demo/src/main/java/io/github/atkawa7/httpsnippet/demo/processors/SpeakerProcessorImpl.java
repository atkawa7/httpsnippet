package io.github.atkawa7.httpsnippet.demo.processors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;

import io.github.atkawa7.httpsnippet.demo.domain.Speaker;
import io.github.atkawa7.httpsnippet.demo.dto.SpeakerDTO;
import io.github.atkawa7.httpsnippet.demo.service.SpeakerService;

public class SpeakerProcessorImpl implements SpeakerProcessor {
  private final SpeakerService speakerService;

  public SpeakerProcessorImpl(SpeakerService speakerService) {
    this.speakerService = speakerService;
  }

  @Override
  public HttpEntity<Page<SpeakerDTO>> listSpeakers(final Pageable pageable) {
    Page<SpeakerDTO> pages = speakerService.listSpeakers(pageable).map(this::toDTO);
    return ResponseEntity.ok(pages);
  }

  @Override
  public HttpEntity<SpeakerDTO> createSpeaker(final SpeakerDTO dto) {
    return ResponseEntity.ok(toDTO(speakerService.createSpeaker(toEntity(dto))));
  }

  @Override
  public HttpEntity<SpeakerDTO> updateSpeaker(final Long id, final SpeakerDTO dto) {
    return ResponseEntity.ok(toDTO(speakerService.updateSpeaker(id, toEntity(dto))));
  }

  @Override
  public HttpEntity<SpeakerDTO> retrieveSpeaker(final Long id) {
    return ResponseEntity.ok(toDTO(speakerService.retrieveSpeaker(id)));
  }

  @Override
  public HttpEntity<SpeakerDTO> deleteSpeaker(final Long id) {
    return ResponseEntity.ok(toDTO(speakerService.deleteSpeaker(id)));
  }

  @Override
  public Speaker toEntity(SpeakerDTO dto) {
    return ObjectUtils.isEmpty(dto)
        ? null
        : Speaker.builder()
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .id(dto.getId())
            .thumbnail(dto.getThumbnail())
            .company(dto.getCompany())
            .biography(dto.getBiography())
            .twitter(dto.getTwitter())
            .facebook(dto.getFacebook())
            .github(dto.getGithub())
            .linkedIn(dto.getLinkedIn())
            .build();
  }

  @Override
  public SpeakerDTO toDTO(Speaker en) {
    return ObjectUtils.isEmpty(en)
        ? null
        : SpeakerDTO.builder()
            .firstName(en.getFirstName())
            .lastName(en.getLastName())
            .id(en.getId())
            .thumbnail(en.getThumbnail())
            .company(en.getCompany())
            .biography(en.getBiography())
            .twitter(en.getTwitter())
            .facebook(en.getFacebook())
            .github(en.getGithub())
            .linkedIn(en.getLinkedIn())
            .build();
  }
}
