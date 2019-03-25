package io.github.atkawa7.httpsnippet.demo.service;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import io.github.atkawa7.httpsnippet.demo.domain.Speaker;
import io.github.atkawa7.httpsnippet.demo.repository.SpeakerRepository;

public class SpeakerServiceImpl implements SpeakerService {
  private final SpeakerRepository speakerRepository;

  public SpeakerServiceImpl(SpeakerRepository speakerRepository) {
    this.speakerRepository = speakerRepository;
  }

  @Override
  public Page<Speaker> listSpeakers(Pageable pageable) {
    return speakerRepository.findAll(pageable);
  }

  @Override
  public Speaker createSpeaker(Speaker dto) {
    return speakerRepository.save(dto);
  }

  @Override
  public Speaker updateSpeaker(Long id, Speaker dto) {
    Speaker speaker = retrieveSpeaker(id);
    if (!ObjectUtils.isEmpty(dto)) {
      if (StringUtils.isNotEmpty(dto.getBiography())) {
        speaker.setBiography(dto.getBiography());
      }
      if (StringUtils.isNotEmpty(dto.getFirstName())) {
        speaker.setFirstName(dto.getFirstName());
      }
      if (StringUtils.isNotEmpty(dto.getLastName())) {
        speaker.setLastName(dto.getLastName());
      }
      if (StringUtils.isNotEmpty(dto.getCompany())) {
        speaker.setCompany(dto.getCompany());
      }
      if (StringUtils.isNotEmpty(dto.getThumbnail())) {
        speaker.setThumbnail(dto.getThumbnail());
      }
      if (StringUtils.isNotEmpty(dto.getFacebook())) {
        speaker.setFacebook(dto.getFacebook());
      }
      if (StringUtils.isNotEmpty(dto.getGithub())) {
        speaker.setGithub(dto.getGithub());
      }
      if (StringUtils.isNotEmpty(dto.getLinkedIn())) {
        speaker.setLinkedIn(dto.getLinkedIn());
      }
      if (StringUtils.isNotEmpty(dto.getTwitter())) {
        speaker.setTwitter(dto.getTwitter());
      }
    }
    return speakerRepository.save(speaker);
  }

  @Override
  public Speaker retrieveSpeaker(Long id) {
    return speakerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
  }

  @Override
  public Speaker deleteSpeaker(Long id) {
    Speaker speaker = retrieveSpeaker(id);
    speakerRepository.deleteById(id);
    return speaker;
  }
}
